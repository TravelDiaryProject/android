package com.traveldiary.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateTripActivity extends AppCompatActivity {

    private Button createTripButton;
    private EditText editTripTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        createTripButton = (Button) findViewById(R.id.createTripButton);
        editTripTitle = (EditText) findViewById(R.id.editTripTitle);

        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTripTitle != null){
                    String tripTitle = editTripTitle.getText().toString();

                    TravelDiaryService travelDiaryService;

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://188.166.77.89").build();
                    travelDiaryService = retrofit.create(TravelDiaryService.class);

                    RequestBody tripTitleRequest = RequestBody.create(MediaType.parse("multipart/form-data"), tripTitle);
                    travelDiaryService.createTrip(tripTitle).enqueue(new Callback<ResponseBody>() {
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

    }

    public void inform(){
        Toast toast = Toast.makeText(this,
                "Trip created!!!", Toast.LENGTH_SHORT);
        toast.show();

        Intent intent = new Intent(this, AllTripsActivity.class);
        startActivity(intent);
    }
}
