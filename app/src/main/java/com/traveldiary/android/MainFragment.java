package com.traveldiary.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.traveldiary.android.Interfaces.ChangeFragmentInterface;
import com.traveldiary.android.Interfaces.TravelDiaryService;
import com.traveldiary.android.adapter.AdapterWithHeader;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.essence.Header;
import com.traveldiary.android.essence.Trip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.ROOT_URL;


public class MainFragment extends Fragment implements View.OnClickListener {

    private Header header;

    private ChangeFragmentInterface mChangeFragmentInterface;

    private LinearLayoutManager mLayoutManager;
    RecyclerView recyclerView;
    List<Trip> mTripList;
    AdapterWithHeader adapterWithHeader;
    private Retrofit retrofit;
    private static TravelDiaryService travelDiaryService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main,
                container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_recycler_view);

        mTripList = new ArrayList<>();

        adapterWithHeader = new AdapterWithHeader(getHeader(), mTripList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterWithHeader);


        downloadImage();

        return rootView;
    }


    private void downloadImage() {

        retrofit = new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        travelDiaryService = retrofit.create(TravelDiaryService.class);

        System.out.println("AAAAAAAAAAAaa = " + LoginActivity.TOKEN_TO_SEND.toString());

        travelDiaryService.listAllTrips().enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, retrofit2.Response<List<Trip>> response) {

                mTripList.addAll(response.body());

                //mProgressBar.setVisibility(View.GONE);

                adapterWithHeader.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {

            }
        });
    }


    public Header getHeader(){
        header = new Header();
        header.setName("Вот и хефдер");
        return header;
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

    @Override
    public void onClick(View view) {

        //Button listener
        if (view.getParent()instanceof LinearLayout){
            System.out.println("instanceof Liner");
            System.out.println("id" + header.getId());

            Trip trip = mTripList.get(Integer.parseInt(header.getId()));

            Fragment fragment = new PlacesFragment();

            Bundle args = new Bundle();
            args.putInt(ID_STRING, trip.getId());
            fragment.setArguments(args);//передаем в новый фрагмент ид трипа чтобы подтянуть имг этого трипа

            mChangeFragmentInterface.trans(fragment);

        //list item listener
        }else if (view.getParent()instanceof RecyclerView){
            System.out.println("Recycler");
        }
    }
}