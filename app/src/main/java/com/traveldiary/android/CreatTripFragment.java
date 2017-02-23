package com.traveldiary.android;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.traveldiary.android.Interfaces.ChangeFragmentInterface;
import com.traveldiary.android.Interfaces.TravelDiaryService;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.traveldiary.android.Constans.ROOT_URL;


public class CreatTripFragment extends Fragment {

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

        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTripTitle != null){
                    String tripTitle = editTripTitle.getText().toString();

                    TravelDiaryService travelDiaryService;

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ROOT_URL).build();
                    travelDiaryService = retrofit.create(TravelDiaryService.class);

                    //RequestBody tripTitleRequest = RequestBody.create(MediaType.parse("multipart/form-data"), tripTitle);
                    travelDiaryService.createTrip(LoginActivity.TOKEN_TO_SEND.toString(), tripTitle).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            inform();

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            }
        });

        return rootView;
    }

    public void inform(){
        Toast toast = Toast.makeText(getActivity(),
                "Trip created!!!", Toast.LENGTH_SHORT);
        toast.show();

        Fragment fragment = new TripsFragment();
        mChangeFragmentInterface.trans(fragment);
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
}
