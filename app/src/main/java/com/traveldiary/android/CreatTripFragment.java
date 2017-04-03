package com.traveldiary.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.traveldiary.android.network.CallBackInterface;
import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.network.Network;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.traveldiary.android.App.network;


public class CreatTripFragment extends Fragment implements CallBackInterface {

    private Button createTripButton;
    private EditText editTripTitle;

    private ChangeFragmentInterface mChangeFragmentInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creat_trip,
                container, false);

        createTripButton = (Button) rootView.findViewById(R.id.createTripButton);
        editTripTitle = (EditText) rootView.findViewById(R.id.editTripTitle);

        //final Network network = new Network(this);
        network.setCallBackInterface(this);
        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTripTitle != null){
                    String tripTitle = editTripTitle.getText().toString();

                    network.createTrip(LoginActivity.TOKEN_TO_SEND.toString(), tripTitle);
                }
            }
        });

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

    @Override
    public void createTrip(String info) {
        Toast toast = Toast.makeText(getActivity(),
                "Trip created!!! + response = " + info, Toast.LENGTH_SHORT);
        toast.show();

        Fragment fragment = new TripsFragment();
        mChangeFragmentInterface.trans(fragment);
    }

    @Override
    public void signIn(Response<RegistrationResponse> response) {

    }

    @Override
    public void registration(Response<RegistrationResponse> response) {

    }

    @Override
    public void uploadPlace(Response<ResponseBody> response) {

    }

    @Override
    public void getAllCities(List<City> allCities) {

    }

    @Override
    public void getAllPlaces(List<Place> allPlaces) {

    }

    @Override
    public void getMyPlaces(List<Place> myPlaces) {

    }

    @Override
    public void getPlacesByTrip(List<Place> placesByTrip) {

    }

    @Override
    public void getPlacesByCity(List<Place> placesByCity) {

    }

    @Override
    public void getAllTrips(List<Trip> allTrips) {

    }

    @Override
    public void getMyTrips(List<Trip> myTrips) {

    }

    @Override
    public void getTripsByCity(List<Trip> tripsByCity) {

    }

    @Override
    public void getTripById(Trip trip) {

    }
}
