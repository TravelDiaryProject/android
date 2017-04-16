package com.traveldiary.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.traveldiary.android.network.CallBack;
import com.traveldiary.android.model.City;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.RegistrationResponse;
import com.traveldiary.android.model.Trip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.CAMERA;
import static com.traveldiary.android.Constans.GALLERY;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.TOKEN_CONST;
import static com.traveldiary.android.Constans.UPLOAD_FROM;

public class UploadFragment extends Fragment {

    private String photoFromCameraPath;

    private String uploadFrom = null;

    private File directory;

    private int mTripId;
    private static int RESULT_LOAD_IMAGE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTripId = getArguments().getInt(ID_STRING);
        uploadFrom = getArguments().getString(UPLOAD_FROM);
        //createDirectory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upload,
                container, false);

        if (uploadFrom.equals(GALLERY)) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        } else if (uploadFrom.equals(CAMERA)){
            Log.d("LOOOOOOOOOOOOOOLLLLL", "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO onCreateView");
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri());
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (uploadFrom.equals(GALLERY)) {

            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

                ///get path to image
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                ///

                File file = new File(picturePath); // picture path like in phone

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);

                RequestBody tripIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), Integer.toString(mTripId));

                MultipartBody.Part body = MultipartBody.Part.createFormData("place[file]", file.getName(), reqFile);

                network.uploadPlace(TOKEN_CONST, body, tripIdRequest, new CallBack() {
                    @Override
                    public void responseNetwork(Object o) {
                        Response<ResponseBody> response = (Response<ResponseBody>) o;
                        try {
                            System.out.println("onResponse = " + response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        inform();
                    }

                    @Override
                    public void failNetwork(Throwable t) {

                    }
                });
            }
        } else if (uploadFrom.equals(CAMERA)){

            Log.d("LOOOOOOOOOOOOOOLLLLL", "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO onActivityResult");
            Log.d("LOOOOOOOOOOOOOOLLLLL", requestCode + " resc = " + resultCode + " data = " + data );

            File file = new File(photoFromCameraPath); // picture path like in phone

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);

            RequestBody tripIdRequest = RequestBody.create(MediaType.parse("multipart/form-data"), Integer.toString(mTripId));

            MultipartBody.Part body = MultipartBody.Part.createFormData("place[file]", file.getName(), reqFile);

            network.uploadPlace(TOKEN_CONST, body, tripIdRequest, new CallBack() {
                @Override
                public void responseNetwork(Object o) {
                    Response<ResponseBody> response = (Response<ResponseBody>) o;
                    try {
                        System.out.println("onResponse = " + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    inform();
                }

                @Override
                public void failNetwork(Throwable t) {

                }
            });

        }
    }

    public void inform(){
        Toast toast = Toast.makeText(getActivity(),
                "Place has been created!!!", Toast.LENGTH_SHORT);
        toast.show();

        PlacesFragment placesFragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putInt(ID_STRING, mTripId);
        placesFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_detail, placesFragment);
        //ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

       /* Fragment fragment = new PlacesFragment();

        Bundle args = new Bundle();
        args.putInt(ID_STRING, mTripId);
        fragment.setArguments(args);//передаем в новый фрагмент ид трипа чтобы подтянуть имг этого трипа

        //mChangeFragmentInterface.trans(fragment);*/

    }

    private Uri generateFileUri(){
        Log.d("LOOOOOOOOOOOOOOLLLLL", "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO generateFileUri");
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(directory.getPath() + "/" + "photo_" + System.currentTimeMillis() + ".jpg");

        photoFromCameraPath = file.getPath();


        return Uri.fromFile(file);
    }

   /* private void createDirectory(){
        Log.d("LOOOOOOOOOOOOOOLLLLL", "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO createDirectory");
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
       *//* if (!directory.exists())
            directory.mkdirs();*//*
    }*/


   /* @Override
    public void onResume() {
        super.onResume();
        PlacesFragment placesFragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putInt(ID_STRING, mTripId);
        placesFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_detail, placesFragment);
        //ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }*/
}
