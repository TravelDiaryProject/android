package com.traveldiary.android;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.traveldiary.android.network.CallBack;

import java.io.File;
import java.io.IOException;

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


public class UploadDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "UploadDialog";

    private static int RESULT_LOAD_IMAGE = 1;
    private String photoFromCameraPath;
    private File directory;

    private int mTripId;

    private String uploadFrom = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTripId = getArguments().getInt(ID_STRING);
        //createDirectory();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Title");

        View view = inflater.inflate(R.layout.fragment_upload_dialog, null);
        view.findViewById(R.id.buttonCamera).setOnClickListener(this);
        view.findViewById(R.id.buttonGallery).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonCamera:
                uploadFrom = CAMERA;
                fromCamera();
                break;
            case R.id.buttonGallery:
                uploadFrom = GALLERY;
                fromGallery();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public void fromCamera(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri());
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    public void fromGallery(){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private Uri generateFileUri(){
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(directory.getPath() + "/" + "photo_" + System.currentTimeMillis() + ".jpg");

        photoFromCameraPath = file.getPath();

        return Uri.fromFile(file);
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

                if (checkLocationInImage(picturePath)) { // picture must have location

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
            }
        } else if (uploadFrom.equals(CAMERA)){

            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

                if (checkLocationInImage(photoFromCameraPath)) { // picture must have location

                    File file = new File(photoFromCameraPath); // picture path like in phone

                    RequestBody reqFile = RequestBody.create(MediaType.parse("image"), file);

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
        dismiss();
    }

    private boolean checkLocationInImage(String photoPath){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoPath);

        } catch (IOException e) {
            Log.d(TAG, "problem with exifInterface", e);
        }
        if (exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)==null || exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)==null) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Image must have location! Turn on location in your camera! Or choice another image!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();


            return false;
        }
        else if (exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)!=null || exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)!=null)
            return true;
        return false;
    }
}
