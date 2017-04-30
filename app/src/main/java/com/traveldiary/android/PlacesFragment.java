package com.traveldiary.android;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.traveldiary.android.data.DataService;
import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.network.InternetStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.Constans.GALLERY;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.PLACES_BY_CITY;
import static com.traveldiary.android.Constans.PLACES_BY_COUNTRY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACES_FOR_CITY;
import static com.traveldiary.android.Constans.PLACES_FOR_COUNTRY;
import static com.traveldiary.android.Constans.PLACES_FOR_TRIP;
import static com.traveldiary.android.Constans.PLACE_ID;
import static com.traveldiary.android.Constans.TOKEN_CONST;
import static com.traveldiary.android.Constans.PLACES_FOR_TOP;
import static com.traveldiary.android.Constans.UPLOAD_FROM;


public class PlacesFragment extends Fragment implements RecyclerAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Place> mPlacesList;

    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;

    private FloatingActionButton addPlaceButton;

    private DataService dataService = new DataService();

    private int tripId;
    private int cityId;
    private int countryId;

    private String placesFor;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            placesFor = getArguments().getString(PLACES_FOR);
            tripId = getArguments().getInt(ID_STRING);
            cityId = getArguments().getInt(PLACES_BY_CITY);
            countryId = getArguments().getInt(PLACES_BY_COUNTRY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places,
                container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.places_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        addPlaceButton = (FloatingActionButton) rootView.findViewById(R.id.add_place_button);
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment uploadDialog = new UploadDialog();
                Bundle args = new Bundle();
                args.putInt(ID_STRING, tripId);
                args.putString(UPLOAD_FROM, GALLERY);
                uploadDialog.setArguments(args);
                uploadDialog.show(getFragmentManager(), "dialo");
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.places_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.places_recycler_view);

        mPlacesList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(getActivity(), null, this, mPlacesList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        listPLacesByForType(false);

        return rootView;
    }

    private void listPLacesByForType(final boolean isThisRefresh){
        switch (placesFor){

            case PLACES_FOR_TOP:
                dataService.getTopPlaces(0, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        loadNextTopPage(dy);
                    }
                });

                break;

            case PLACES_FOR_CITY:

                recyclerView.clearOnScrollListeners();

                dataService.getPlacesByCity(cityId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case PLACES_FOR_COUNTRY:
                recyclerView.clearOnScrollListeners();

                dataService.getPlacesByCountry(countryId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        mProgressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case PLACES_FOR_TRIP:

                recyclerView.clearOnScrollListeners();

                dataService.getPLacesByTrip(tripId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    public void manipulationWithResponse(Object o, boolean isThisRefresh){
        List<Place> placesList = (List<Place>) o;

        if (placesList.size()==0){
            Toast.makeText(getActivity(), "No Places", Toast.LENGTH_LONG).show();
        }
        //if its refresh not update adapter
        if (!isThisRefresh) {
            mPlacesList.clear();
            mPlacesList.addAll(placesList);
            recyclerAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }else {
            mPlacesList.clear();
            mPlacesList.addAll(placesList);
            recyclerAdapter.updateAdapter(placesList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void loadNextTopPage(int dy){
        if (dy > 0) {
            visibleItemCount = mLayoutManager.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loading = false;
                    dataService.getTopPlaces(totalItemCount, new CallBack() {
                        @Override
                        public void responseNetwork(Object o) {
                            List<Place> placesList = (List<Place>) o;

                            loading = true;

                            if (placesList.size() > 0) {
                                recyclerAdapter.setLoadMore(true);
                                recyclerAdapter.notifyDataSetChanged();

                                mPlacesList.addAll(placesList);
                                recyclerAdapter.notifyItemInserted(totalItemCount);
                            }else {
                                recyclerAdapter.setLoadMore(false);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void failNetwork(Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onItemClick(final View view, final int possition) {

        if (view.getId()!=R.id.placeShowInMapButton && view.getId()!=R.id.placeImageView){
            // проверка подключения к инету
            InternetStatus inetStatus = new InternetStatus();
            inetStatus.check(getActivity(), new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (TOKEN_CONST != null)
                                clickLogic(view, possition);
                            else
                                Toast.makeText(getActivity(), "Need Authorization", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void failNetwork(Throwable t) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            clickLogic(view, possition);
        }
    }

    public void clickLogic(final View view, final int possition){

        final Place place = mPlacesList.get(possition);

        switch (view.getId()){

            case R.id.placeRemoveButton:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Точно удалить?")
                                .setCancelable(true)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                                        dataService.removePlace(place, new CallBack() {
                                            @Override
                                            public void responseNetwork(Object o) {
                                                Toast.makeText(getActivity(), "удалено", Toast.LENGTH_SHORT).show();
                                                //recyclerAdapter.removePlace(place);
                                                dialog.cancel();
                                            }

                                            @Override
                                            public void failNetwork(Throwable t) {
                                                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                dialog.cancel();
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                        dialog.cancel();

                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
                break;

            case R.id.placeImageView:
                if (placesFor.equals(PLACES_FOR_TRIP)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    SlideShowDialogFragment newFragment = new SlideShowDialogFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("placeList", (Serializable) mPlacesList);
                    args.putInt("selectedPosition", possition);
                    newFragment.setArguments(args);
                    newFragment.show(ft, "slideshow");
                }else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(ID_STRING, place.getTripId());
                    startActivity(intent);
                }
                break;

            case R.id.placeAddToFutureButton:

                dataService.addToFuture(place, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Toast.makeText(getActivity(), "places added to your future trips", Toast.LENGTH_LONG).show();
                        recyclerAdapter.notifyItemChanged(possition);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage() , Toast.LENGTH_LONG).show();
                        recyclerAdapter.notifyItemChanged(possition);
                    }
                });
                break;

            case R.id.placeLikeButton:

                dataService.addLike(place, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Toast.makeText(getActivity(), "places Liked ", Toast.LENGTH_LONG).show();
                        recyclerAdapter.notifyItemChanged(possition);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage() , Toast.LENGTH_LONG).show();
                        recyclerAdapter.notifyItemChanged(possition);
                    }
                });
                break;

            case R.id.placeShowInMapButton:

                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra(ID_STRING, place.getTripId());
                intent.putExtra(PLACE_ID, place.getId());
                startActivity(intent);

                break;
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        listPLacesByForType(true);
    }
}
