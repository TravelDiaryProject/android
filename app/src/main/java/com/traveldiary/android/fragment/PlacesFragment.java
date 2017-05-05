package com.traveldiary.android.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.traveldiary.android.R;
import com.traveldiary.android.Validator;
import com.traveldiary.android.activity.DetailActivity;
import com.traveldiary.android.activity.MapsActivity;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Place;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.dataService;
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


public class PlacesFragment extends Fragment implements RecyclerAdapter.ItemClickListener, RecyclerAdapter.ItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Place> mPlacesList;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;
    private FloatingActionButton mAddPlaceButton;
    private int mTripId;
    private int mCityId;
    private int mCountryId;
    private String mPlacesFor;

    private TextView mNoPlacesTextView;
    private Button mNoPlacesButton;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            mPlacesFor = getArguments().getString(PLACES_FOR);
            mTripId = getArguments().getInt(ID_STRING);
            mCityId = getArguments().getInt(PLACES_BY_CITY);
            mCountryId = getArguments().getInt(PLACES_BY_COUNTRY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places,
                container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.places_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        mNoPlacesTextView = (TextView) rootView.findViewById(R.id.no_placess_textView);
        mNoPlacesButton = (Button) rootView.findViewById(R.id.no_places_button);
        mNoPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });

        mAddPlaceButton = (FloatingActionButton) rootView.findViewById(R.id.add_place_button);
        mAddPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment uploadDialog = new UploadDialog();
                Bundle args = new Bundle();
                args.putInt(ID_STRING, mTripId);
                args.putString(UPLOAD_FROM, GALLERY);
                uploadDialog.setArguments(args);
                uploadDialog.show(getFragmentManager(), "dialo");
            }
        });
        mAddPlaceButton.hide();

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.places_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.places_recycler_view);

        mPlacesList = new ArrayList<>();

        mRecyclerAdapter = new RecyclerAdapter(getActivity(), null, this, this, mPlacesList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        listPLacesByForType(false);

        return rootView;
    }

    private void listPLacesByForType(final boolean isThisRefresh){
        switch (mPlacesFor){

            case PLACES_FOR_TOP:
                dataService.getTopPlaces(0, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o, isThisRefresh);
                        //mRecyclerAdapter.setLoadMore(true);
                        //mRecyclerAdapter.addLoadingFooter();
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        if (mPlacesList.size()==0){
                            mNoPlacesTextView.setText("При загрузке данных произошла ошибка. Проверте подключение к сети.");
                            mNoPlacesTextView.setVisibility(View.VISIBLE);
                            mNoPlacesButton.setVisibility(View.VISIBLE);
                        }
                    }
                });

                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        loadNextTopPage(dy);
                    }
                });

                break;

            case PLACES_FOR_CITY:

                mRecyclerView.clearOnScrollListeners();

                dataService.getPlacesByCity(mCityId, new CallBack() {
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
                mRecyclerView.clearOnScrollListeners();

                dataService.getPlacesByCountry(mCountryId, new CallBack() {
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

                mRecyclerView.clearOnScrollListeners();

                dataService.getTripById(mTripId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Trip trip = (Trip) o;
                        checkIsMineTrip(trip);
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });

                dataService.getPLacesByTrip(mTripId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Log.d("REMOVE", "response get places by trpId = " + mTripId);
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

    public void checkIsMineTrip(Trip trip){
        if (trip.getIsMine()==1&&trip.getIsFuture()==0){
            mAddPlaceButton.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
            mAddPlaceButton.show();
        }
    }

    public void manipulationWithResponse(Object o, boolean isThisRefresh){
        List<Place> placesList = (List<Place>) o;
        mNoPlacesTextView.setVisibility(View.GONE);
        mNoPlacesButton.setVisibility(View.GONE);

        if (placesList==null || placesList.size()==0){
            mNoPlacesTextView.setText("No places yet");
            mNoPlacesTextView.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "No Places", Toast.LENGTH_LONG).show();
        }

        if (!isThisRefresh) {
            mPlacesList.clear();
            mPlacesList.addAll(placesList);
            mRecyclerAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }else {
            mPlacesList.clear();
            mPlacesList.addAll(placesList);
            mRecyclerAdapter.updateAdapter(placesList);
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
                                //mRecyclerAdapter.setLoadMore(true);
                                //mRecyclerAdapter.notifyDataSetChanged();

                                mRecyclerAdapter.removeLoadingFooter();
                                mPlacesList.addAll(placesList);
                                mRecyclerAdapter.notifyDataSetChanged();
                                mRecyclerAdapter.addLoadingFooter();
                            }else {
                                mRecyclerAdapter.removeLoadingFooter();
                                //mRecyclerAdapter.setLoadMore(false);
                                mRecyclerAdapter.notifyDataSetChanged();
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
    public void onItemClick(final View view, final int position) {

        if (view.getId()!=R.id.placeShowInMapButton && view.getId()!=R.id.placeImageView){

            if (Validator.isNetworkAvailable(getActivity())){
                if (TOKEN_CONST != null)
                    clickLogic(view, position);
                else
                    Toast.makeText(getActivity(), "Need Authorization", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        }else {
            clickLogic(view, position);
        }
    }

    public void clickLogic(final View view, final int possition){

        final Place place = mPlacesList.get(possition);

        switch (view.getId()){

            case R.id.placeImageView:
                if (mPlacesFor.equals(PLACES_FOR_TRIP)) {
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
                        mRecyclerAdapter.notifyItemChanged(possition);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage() , Toast.LENGTH_LONG).show();
                        mRecyclerAdapter.notifyItemChanged(possition);
                    }
                });
                break;

            case R.id.placeLikeButton:

                dataService.addLike(place, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Toast.makeText(getActivity(), "places Liked ", Toast.LENGTH_LONG).show();
                        mRecyclerAdapter.notifyItemChanged(possition);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage() , Toast.LENGTH_LONG).show();
                        mRecyclerAdapter.notifyItemChanged(possition);
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

    @Override
    public void onItemLongClick(View view, final int position) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11 " + position);
        final Place place = mPlacesList.get(position);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Точно удалить?")
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        dataService.removePlace(place, new CallBack() {
                            @Override
                            public void responseNetwork(Object o) {
                                Toast.makeText(getActivity(), "удалено", Toast.LENGTH_SHORT).show();
//                                mRecyclerAdapter.notifyItemChanged(position);

                                mRecyclerAdapter.notifyItemRemoved(position);
                                mPlacesList.remove(position);
                                //mRecyclerAdapter.notifyDataSetChanged();
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
}
