package com.traveldiary.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.traveldiary.android.R;
import com.traveldiary.android.ToolbarActionMode;
import com.traveldiary.android.Validator;
import com.traveldiary.android.activity.CreateFindActivity;
import com.traveldiary.android.activity.DetailActivity;
import com.traveldiary.android.activity.MapsActivity;
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
import static com.traveldiary.android.Constans.PLACES_BY_CITY_NAME;
import static com.traveldiary.android.Constans.PLACES_BY_COUNTRY;
import static com.traveldiary.android.Constans.PLACES_BY_COUNTRY_NAME;
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
    private int mTripId;
    private int mCityId;
    private String mCityName;
    private int mCountryId;
    private String mCountryName;
    private String mPlacesFor;

    private TextView mNoPlacesTextView;
    private Button mNoPlacesButton;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private ActionMode mActionMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPlacesFor = getArguments().getString(PLACES_FOR);
            mTripId = getArguments().getInt(ID_STRING);
            mCityId = getArguments().getInt(PLACES_BY_CITY);
            mCountryId = getArguments().getInt(PLACES_BY_COUNTRY);
            mCityName = getArguments().getString(PLACES_BY_CITY_NAME);
            mCountryName = getArguments().getString(PLACES_BY_COUNTRY_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places,
                container, false);

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

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

    private void listPLacesByForType(final boolean isThisRefresh) {
        switch (mPlacesFor) {

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
                        noNetworkOrEmptyListInfo(getResources().getString(R.string.check_network_connection), getResources().getString(R.string.try_again));
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

                ((CreateFindActivity) getActivity())
                        .setActionBarTitle(mCityName);

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
                        noNetworkOrEmptyListInfo(getResources().getString(R.string.check_network_connection), getResources().getString(R.string.try_again));
                    }
                });
                break;

            case PLACES_FOR_COUNTRY:

                ((CreateFindActivity) getActivity())
                        .setActionBarTitle(mCountryName);

                mRecyclerView.clearOnScrollListeners();

                Log.d("PlacesFragment", "PLACES_FOR_COUNTRY");

                dataService.getPlacesByCountry(mCountryId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        List<Place> list = (List<Place>) o;
                        Log.d("PlacesFragment", "PLACES_FOR_COUNTRY" + " responseNetwork size = " + list.size());
                        manipulationWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        Log.d("PlacesFragment", "PLACES_FOR_COUNTRY" + " failNetwork");
                        mProgressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        noNetworkOrEmptyListInfo(getResources().getString(R.string.check_network_connection), getResources().getString(R.string.try_again));
                    }
                });
                break;

            case PLACES_FOR_TRIP:

                mRecyclerView.clearOnScrollListeners();

                dataService.getPLacesByTrip(mTripId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o, isThisRefresh);
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        noNetworkOrEmptyListInfo(getResources().getString(R.string.check_network_connection), getResources().getString(R.string.try_again));
                    }
                });
                break;
        }
    }

    public void manipulationWithResponse(Object o, boolean isThisRefresh) {
        List<Place> placesList = (List<Place>) o;
        mNoPlacesTextView.setVisibility(View.GONE);
        mNoPlacesButton.setVisibility(View.GONE);

        Log.d("PlacesFragment", "PLACES_FOR_COUNTRY manipulationWithResponse placesList.size = " + placesList.size());

        if (placesList == null || placesList.size() == 0) {

            noNetworkOrEmptyListInfo("No places yet", null);

//            mNoPlacesTextView.setText("No places yet");
//            mNoPlacesTextView.setVisibility(View.VISIBLE);
//            Toast.makeText(getActivity(), "No Places", Toast.LENGTH_LONG).show();
        }

        if (!isThisRefresh) {
            mPlacesList.clear();
            mPlacesList.addAll(placesList);
            mRecyclerAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        } else {
            mPlacesList.clear();
            mPlacesList.addAll(placesList);
            mRecyclerAdapter.updateAdapter(placesList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void noNetworkOrEmptyListInfo(String textView, String buttonText) {

        if (mPlacesList.size() == 0) {
            mNoPlacesTextView.setText(textView);
            mNoPlacesTextView.setVisibility(View.VISIBLE);
            if (buttonText != null) {
                mNoPlacesButton.setText(buttonText);
                mNoPlacesButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void loadNextTopPage(int dy) {
        if (dy > 0) {
            visibleItemCount = mLayoutManager.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loading = false;
                    mRecyclerAdapter.addLoadingFooter();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
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
                                        //mRecyclerAdapter.addLoadingFooter();
                                    } else {
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
                    }, 1000);

                }
            }
        }
    }

    @Override
    public void onItemClick(final View view, final int position) {

        if (mActionMode != null) {
            onListItemSelect(position);
        } else {
            if (view.getId() != R.id.placeShowInMapButton && view.getId() != R.id.placeImageView) {

                if (Validator.isNetworkAvailable(getActivity())) {
                    if (TOKEN_CONST != null)
                        clickLogic(view, position);
                    else
                        Toast.makeText(getActivity(), "Need Authorization", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                }
            } else {
                clickLogic(view, position);
            }
        }
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        onListItemSelect(position);
    }

    private void onListItemSelect(int position) {
        mRecyclerAdapter.toggleSelection(position);

        boolean hasCheckedItems = mRecyclerAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ToolbarActionMode(mRecyclerAdapter, null, PlacesFragment.this));
        else if (!hasCheckedItems && mActionMode != null)
            mActionMode.finish();
        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(mRecyclerAdapter.getSelectedCount()));
    }

    public void setNullToActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
        }
    }

    public void deleteRows() {
        final SparseBooleanArray selected = mRecyclerAdapter.getSelectedIds();//Get selected ids

        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                final int finalI = i;
                final Place place = mPlacesList.get(selected.keyAt(finalI));

                dataService.removePlace(mPlacesList.get(selected.keyAt(i)), new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        mPlacesList.remove(place);

                        if (selected.size() > 2)
                            mRecyclerAdapter.notifyDataSetChanged();
                        else
                            mRecyclerAdapter.notifyItemRemoved(selected.keyAt(finalI));
                    }

                    @Override
                    public void failNetwork(Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        Toast.makeText(getActivity(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();
    }

    public void clickLogic(final View view, final int possition) {

        final Place place = mPlacesList.get(possition);

        switch (view.getId()) {

            case R.id.placeImageView:
                if (mPlacesFor.equals(PLACES_FOR_TRIP)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    SlideShowDialogFragment newFragment = new SlideShowDialogFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("placeList", (Serializable) mPlacesList);
                    args.putInt("selectedPosition", possition);
                    newFragment.setArguments(args);
                    newFragment.show(ft, "slideshow");
                } else {
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
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        mRecyclerAdapter.notifyItemChanged(possition);
                    }
                });
                break;

            case R.id.placeLikeButton:

                if (place.getIsLiked() == 0) {
                    dataService.addLike(place, new CallBack() {
                        @Override
                        public void responseNetwork(Object o) {
                            Toast.makeText(getActivity(), "place Liked ", Toast.LENGTH_LONG).show();
                            mRecyclerAdapter.notifyItemChanged(possition);
                        }

                        @Override
                        public void failNetwork(Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                            mRecyclerAdapter.notifyItemChanged(possition);
                        }
                    });
                } else if (place.getIsLiked() == 1) {
                    dataService.removeLike(place, new CallBack() {
                        @Override
                        public void responseNetwork(Object o) {
                            Toast.makeText(getActivity(), "place Unliked ", Toast.LENGTH_LONG).show();
                            mRecyclerAdapter.notifyItemChanged(possition);
                        }

                        @Override
                        public void failNetwork(Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                            mRecyclerAdapter.notifyItemChanged(possition);
                        }
                    });
                }
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
