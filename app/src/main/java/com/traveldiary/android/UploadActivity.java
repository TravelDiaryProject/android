package com.traveldiary.android;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

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

            Uri uri = data.getData();
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

            System.out.println("AAAAAAAAAAAAAAAAAAAAAAa uri path = " + uri.getPath() + " picturePath = " + picturePath);

            File file = new File(picturePath); // picture path like in phone

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);

            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);

            final retrofit2.Call<okhttp3.ResponseBody> req = travelDiaryService.postImage(reqFile);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    System.out.println("ONRESPONSE!!!! = " + response.toString());
                    // Do Something
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    System.out.println("FAIL!!");
                    t.printStackTrace();
                }
            });
        }
    }

}
