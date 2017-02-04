package com.traveldiary.android;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripsFragment extends Fragment {

    private ChangeFragmentInterface mChangeFragmentInterface;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Trip trip;
    private List<Trip> mTripList;
    private LinearLayoutManager mLayoutManager;

    private Retrofit retrofit;
    private static TravelDiaryService travelDiaryService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trips,
                container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.trips_recycler_view);

        mTripList = new ArrayList<>();

        recyclerAdapter = new RecyclerAdapter(getActivity(), mTripList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Trip trip = (Trip) view.getTag(); //получаем нажатый трип
                Fragment fragment = new PlacesFragment();

                Bundle args = new Bundle();
                args.putInt("id", trip.getId());
                fragment.setArguments(args);//передаем в новый фрагмент ид трипа чтобы подтянуть имг этого трипа

                mChangeFragmentInterface.trans(fragment);
            }
        }, null);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        downloadImage();

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

    private void downloadImage() {

        retrofit = new Retrofit.Builder()
                .baseUrl("http://188.166.77.89")
                .addConverterFactory(GsonConverterFactory.create()).build();
        travelDiaryService = retrofit.create(TravelDiaryService.class);

        travelDiaryService.listAllTrips().enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                mTripList.addAll(response.body());

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {

            }
        });
    }
}