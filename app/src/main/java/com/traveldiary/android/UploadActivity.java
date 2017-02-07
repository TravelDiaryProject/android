package com.traveldiary.android;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {

    private Button uploadFromGalleryButton;
    private int RESULT_LOAD_IMAGE = 1;
    private int tripId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        tripId = getIntent().getIntExtra("tripId", 0);

        uploadFromGalleryButton = (Button) findViewById(R.id.uploadFromGalleryButton);

        uploadFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            TravelDiaryService travelDiaryService;

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://188.166.77.89").build();
            travelDiaryService = retrofit.create(TravelDiaryService.class);

            ///get path to image
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ///


            File file = new File(picturePath); // picture path like in phone

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);

            RequestBody tripIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), Integer.toString(tripId));

            MultipartBody.Part body = MultipartBody.Part.createFormData("place[file]", file.getName(), reqFile);

            travelDiaryService.postImage(body, tripIdRequest).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    System.out.println("onResponse = " + response);
                    inform();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("onFail");
                }
            });
        }
    }

    public void inform(){
        Toast toast = Toast.makeText(this,
                "Place has been created!!!", Toast.LENGTH_SHORT);
        toast.show();

        Intent openPlacesFragmentIntent = new Intent(this, AllTripsActivity.class);
        openPlacesFragmentIntent.putExtra("OPEN_PLACES_FRAGMENT_WITH_ID", tripId);
        startActivity(openPlacesFragmentIntent);

    }
}
