package com.traveldiary.android;


import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.network.InternetStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.CAMERA;
import static com.traveldiary.android.Constans.GALLERY;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.KEY_FOR_MAIN;
import static com.traveldiary.android.Constans.MAP;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.PLACES_BY_CITY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACE_ID;
import static com.traveldiary.android.Constans.TOKEN_CONST;
import static com.traveldiary.android.Constans.TOP;
import static com.traveldiary.android.Constans.TRIP_ID;
import static com.traveldiary.android.Constans.UPLOAD_FROM;


public class PlacesFragment extends Fragment implements RecyclerAdapter.ItemClickListener {
    private ChangeFragmentInterface mChangeFragmentInterface;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private List<Place> mPlacesList;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;

    private FloatingActionButton addPlaceButton;

    private int tripId;
    private int cityId;

    private Dialog mDialog;
    private Button uploadFromGalleryBut;
    private Button uploadFromCameraBut;

    private String placesFor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            placesFor = getArguments().getString(PLACES_FOR);
            tripId = getArguments().getInt(ID_STRING);
            cityId = getArguments().getInt(PLACES_BY_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places,
                container, false);

        mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.dialog_view);
        uploadFromGalleryBut = (Button) mDialog.findViewById(R.id.uploadFromGalleryBut);
        uploadFromCameraBut = (Button) mDialog.findViewById(R.id.uploadFromCameraBut);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.places_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        addPlaceButton = (FloatingActionButton) rootView.findViewById(R.id.add_place_button);
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
                //mDialogBackButton();
                uploadFromGalleryBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();

                        Fragment fragment = new UploadFragment();
                        Bundle args = new Bundle();
                        args.putInt(ID_STRING, tripId);
                        args.putString(UPLOAD_FROM, GALLERY);
                        fragment.setArguments(args);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_detail, fragment);
                        //ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();

                        //startActivity(new Intent(TestActivity.this, MainActivity.class));
                    }
                });

                uploadFromCameraBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();

                        Fragment fragment = new UploadFragment();
                        Bundle args = new Bundle();
                        args.putInt(ID_STRING, tripId);
                        args.putString(UPLOAD_FROM, CAMERA);
                        fragment.setArguments(args);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_detail, fragment);
                        //ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                });

                /*Intent uploadActivity = new Intent(getActivity(), UploadActivity.class);
                uploadActivity.putExtra(TRIP_ID, tripId);
                startActivity(uploadActivity);*/
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.places_recycler_view);

        mPlacesList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(getActivity(), null, this, mPlacesList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        // временный ужас!!!!!!
        if (placesFor == null){
            System.out.println("null places for");

            if (cityId != 0){
                network.getPlacesByCity(cityId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o);
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
            }else{
                network.getPlacesByTrip(tripId, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        manipulationWithResponse(o);
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
            }

        }else if (placesFor.equals(TOP)){
            network.getTopPlaces(new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    manipulationWithResponse(o);
                }

                @Override
                public void failNetwork(Throwable t) {

                }
            });
        }else if (placesFor.equals(MY)){
            network.getMyPlaces(TOKEN_CONST.toString(), new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    manipulationWithResponse(o);
                }

                @Override
                public void failNetwork(Throwable t) {

                }
            });
        }

        return rootView;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mChangeFragmentInterface = (ChangeFragmentInterface) activity;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    public void manipulationWithResponse(Object o){
        List<Place> placesList = (List<Place>) o;

        mPlacesList.addAll(placesList);
        recyclerAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(final View view, final int possition) {

        if (view.getId()!=R.id.placeShowInMapButton){
            // проверка подключения к инету
            InternetStatus inetStatus = new InternetStatus();
            inetStatus.check(getActivity(), new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    // проверка авторизации
                    if (TOKEN_CONST != null){
                        clickLogic(view, possition);
                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Need Authorization", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
            case R.id.placeImageView:
                if (placesFor!=null && placesFor.equals(TOP)) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(ID_STRING, place.getTripId());
                    startActivity(intent);
                }else {

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    SlideShowDialogFragment newFragment = new SlideShowDialogFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("placeList", (Serializable) mPlacesList);
                    args.putInt("selectedPosition", possition);
                    newFragment.setArguments(args);
                    newFragment.show(ft, "slideshow");
                    /*Intent intent = new Intent(getActivity(), ShowPLaceWithMap.class);
                    intent.putExtra(TRIP_ID, place.getTripId());
                    intent.putExtra(PLACE_ID, place.getId());
                    startActivity(intent);*/
                }
                break;

            case R.id.placeAddToFutureButton:
                network.addToFutureTrips(TOKEN_CONST, place.getId(), new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Response<ResponseBody> response = (Response<ResponseBody>) o;
                        if (response.code()==201) {
                            Toast.makeText(getActivity(), "places added to your future trips", Toast.LENGTH_LONG).show();

                            place.setIsInFutureTrips(1);
                            recyclerAdapter.notifyItemChanged(possition);
                        }else {
                            Toast.makeText(getActivity(), "Проблема с сервером ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
                break;

            case R.id.placeLikeButton:
                network.likePlace(TOKEN_CONST, place.getId(), new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Response<ResponseBody> response = (Response<ResponseBody>) o;
                        if (response.code()==201) {
                            Toast.makeText(getActivity(), "places Liked ", Toast.LENGTH_LONG).show();

                            place.setIsLiked(1);
                            recyclerAdapter.notifyItemChanged(possition);
                        }else {
                            Toast.makeText(getActivity(), "Проблема с сервером ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
                break;
            case R.id.placeShowInMapButton:

                if (placesFor!=null && placesFor.equals(TOP)) {
                    MapsFragment mapsFragment = new MapsFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();

                    Bundle args = new Bundle();
                    args.putInt(ID_STRING, place.getTripId());
                    args.putInt(PLACE_ID, place.getId());
                    mapsFragment.setArguments(args);

                    ft.replace(R.id.content_main, mapsFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }else {

                    MapsFragment mapsFragment = new MapsFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();

                    Bundle args = new Bundle();
                    args.putInt(ID_STRING, place.getTripId());
                    args.putInt(PLACE_ID, place.getId());
                    mapsFragment.setArguments(args);

                    ft.replace(R.id.content_detail, mapsFragment);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();

                   /* Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra(KEY_FOR_MAIN, MAP);
                    intent.putExtra(ID_STRING, place.getTripId());
                    intent.putExtra(PLACE_ID, place.getId());
                    startActivity(intent);*/
                }
                break;
        }
    }
}
