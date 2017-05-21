package com.traveldiary.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.traveldiary.android.callback.SimpleCallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.callback.CallbackPlaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.traveldiary.android.App.dataService;
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


public class PlacesFragment extends Fragment implements RecyclerAdapter.RecyclerItemListener, SwipeRefreshLayout.OnRefreshListener {

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
    private int totalItemCount;

    private ActionMode mActionMode;

    private boolean mIsRefresh = false;

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

        mRecyclerAdapter = new RecyclerAdapter(getActivity(), null, this, mPlacesList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        listPLacesByForType();

        return rootView;
    }

    private void listPLacesByForType() {
        switch (mPlacesFor) {

            case PLACES_FOR_TOP:
                dataService.getTopPlacesOffset(0, getCallbackPlaces());

                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        loadNextTopPage(dy);
                    }
                });
                break;

            case PLACES_FOR_CITY:
                ((CreateFindActivity) getActivity()).setActionBarTitle(mCityName);

                dataService.getPlacesByCity(mCityId, getCallbackPlaces());
                break;

            case PLACES_FOR_COUNTRY:
                ((CreateFindActivity) getActivity()).setActionBarTitle(mCountryName);

                dataService.getPlacesByCountry(mCountryId, getCallbackPlaces());
                break;

            case PLACES_FOR_TRIP:
                dataService.getPlacesByTrip(mTripId, getCallbackPlaces());
                break;
        }
    }

    @NonNull
    private CallbackPlaces getCallbackPlaces() {
        return new CallbackPlaces() {
            @Override
            public void response(List<Place> placeList) {
                manipulationWithResponse(placeList);
            }

            @Override
            public void fail(Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                noNetworkOrEmptyListInfo(getString(R.string.check_network_connection), getString(R.string.try_again));
            }
        };
    }

    public void manipulationWithResponse(List<Place> placesList) {

        mNoPlacesTextView.setVisibility(View.GONE);
        mNoPlacesButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        if (placesList.size() == 0) {
            mPlacesList.clear();
            noNetworkOrEmptyListInfo("No places yet", null);
        }

        if (!mIsRefresh) {
            mPlacesList.clear();
            mPlacesList.addAll(placesList);
            mRecyclerAdapter.updateAdapterPlace(placesList);

        } else {
            mIsRefresh = false;
            mPlacesList.clear();
            mPlacesList.addAll(placesList);
            mRecyclerAdapter.updateAdapterPlace(placesList);
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
            int visibleItemCount = mLayoutManager.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loading = false;
                    mRecyclerAdapter.addLoadingFooter();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dataService.getTopPlacesOffset(totalItemCount, new CallbackPlaces() {
                                @Override
                                public void response(List<Place> placesList) {
                                    loading = true;

                                    if (placesList.size() > 0) {
                                        mRecyclerAdapter.removeLoadingFooter();
                                        mPlacesList.addAll(placesList);
                                        mRecyclerAdapter.notifyDataSetChanged();
                                    } else {
                                        mRecyclerAdapter.removeLoadingFooter();
                                        mRecyclerAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void fail(Throwable t) {
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
                        Toast.makeText(getActivity(), getString(R.string.need_authorization_function), Toast.LENGTH_SHORT).show();
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
        final SparseBooleanArray selected = mRecyclerAdapter.getSelectedIds();

        swipeRefreshLayout.setRefreshing(true);

        for (int i = 0; i < selected.size(); i++) {
            if (selected.valueAt(i)) {
                final Place place = mPlacesList.get(selected.keyAt(i));

                dataService.removePlace(place.getId(), new SimpleCallBack() {
                    @Override
                    public void response(Object o) {
                        onRefresh();
                    }

                    @Override
                    public void fail(Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        Toast.makeText(getActivity(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();
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

                dataService.addToFutureTrips(place, new SimpleCallBack() {
                    @Override
                    public void response(Object o) {
                        mRecyclerAdapter.notifyItemChanged(possition);
                    }

                    @Override
                    public void fail(Throwable t) {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        mRecyclerAdapter.notifyItemChanged(possition);
                    }
                });
                break;

            case R.id.placeLikeButton:
                if (place.getIsLiked() == 0) {
                    dataService.likePlace(place, new SimpleCallBack() {
                        @Override
                        public void response(Object o) {
                            mRecyclerAdapter.notifyItemChanged(possition);
                        }

                        @Override
                        public void fail(Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                            mRecyclerAdapter.notifyItemChanged(possition);
                        }
                    });
                } else if (place.getIsLiked() == 1) {
                    dataService.unlikePlace(place, new SimpleCallBack() {
                        @Override
                        public void response(Object o) {
                            mRecyclerAdapter.notifyItemChanged(possition);
                        }

                        @Override
                        public void fail(Throwable t) {
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
        mIsRefresh = true;
        listPLacesByForType();
    }
}
