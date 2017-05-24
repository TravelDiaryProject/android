package com.traveldiary.android.fragment;

import android.app.AlertDialog;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.traveldiary.android.R;
import com.traveldiary.android.SingleShotLocationProvider;
import com.traveldiary.android.callback.SimpleCallBack;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACES_FOR_TRIP;


public class UploadDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "UploadDialog";

    private static final int LOAD_GALLERY_REQUEST = 1;
    private static final int LOAD_CAMERA_REQIEST = 2;
    private static final int PLACE_PICKER_REQUEST = 3;

    private String photoFromCameraPath;
    private String photoFromGalleryPath;

    private int mTripId;

    private ProgressBar mUploadProgressBar;
    private Button mButtonCamera;
    private Button mButtonGallery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTripId = getArguments().getInt(ID_STRING);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.photo);

        View view = inflater.inflate(R.layout.fragment_upload_dialog, container, false);
        mButtonCamera = (Button) view.findViewById(R.id.buttonCamera);
        mButtonGallery = (Button) view.findViewById(R.id.buttonGallery);
        mButtonCamera.setOnClickListener(this);
        mButtonGallery.setOnClickListener(this);
        mUploadProgressBar = (ProgressBar) view.findViewById(R.id.upload_progress);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonCamera:
                fromCamera();
                break;
            case R.id.buttonGallery:
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
            buildAlertNoGps();
        }

        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            i.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri());
            startActivityForResult(i, LOAD_CAMERA_REQIEST);
        }
    }

    public void fromGallery(){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, LOAD_GALLERY_REQUEST);
    }

    private void buildAlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.gps_disabled)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertChoiceLocation(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.picture_no_coordinates))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.map), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        placePicker();
                    }
                })
                .setNeutralButton(getString(R.string.gps), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );
                        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                            buildAlertNoGps();
                        }

                        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
                            SingleShotLocationProvider.requestSingleUpdate(getActivity(),
                                    new SingleShotLocationProvider.LocationCallback() {
                                        @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                            setExif(photoFromGalleryPath, location.latitude, location.longitude);
                                            if (checkLocationInImage(photoFromGalleryPath)) {
                                                upload(photoFromGalleryPath, mTripId);
                                            } else {
                                                buildAlertNoLocation();
                                            }
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void buildAlertNoLocation(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.picture_must_have_gps)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private Uri generateFileUri(){
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(directory.getPath() + "/" + "photo_" + System.currentTimeMillis() + ".jpg");
        photoFromCameraPath = file.getPath();
        return Uri.fromFile(file);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case LOAD_GALLERY_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    int columnIndex;
                    if (cursor != null) {
                        cursor.moveToFirst();
                        columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        photoFromGalleryPath = cursor.getString(columnIndex);
                        cursor.close();
                        if (checkLocationInImage(photoFromGalleryPath)) {
                            upload(photoFromGalleryPath, mTripId);
                        } else {
                            buildAlertChoiceLocation();
                        }
                    }
                }
                break;

            case LOAD_CAMERA_REQIEST:
                if (resultCode == RESULT_OK ) {
                    if (checkLocationInImage(photoFromCameraPath)) {
                        upload(photoFromCameraPath, mTripId);
                    }else {
                        SingleShotLocationProvider.requestSingleUpdate(getActivity(),
                                new SingleShotLocationProvider.LocationCallback() {
                                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                        setExif(photoFromCameraPath, location.latitude, location.longitude);
                                        if (checkLocationInImage(photoFromCameraPath)) {
                                            upload(photoFromCameraPath, mTripId);
                                        } else {
                                            buildAlertNoLocation();
                                        }
                                    }
                                });
                    }
                }
                break;

            case PLACE_PICKER_REQUEST:
                if (requestCode == PLACE_PICKER_REQUEST) {
                    if (resultCode == RESULT_OK) {
                        Place place = PlacePicker.getPlace(getActivity(), data); // Place from com.google.android.gms.location.places.Place;
                        setExif(photoFromGalleryPath, place.getLatLng().latitude, place.getLatLng().longitude);
                        if (checkLocationInImage(photoFromGalleryPath)) {
                            upload(photoFromGalleryPath, mTripId);
                        } else {
                            buildAlertNoLocation();
                        }
                    }
                }
                break;
        }
    }

    public void placePicker(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void setExif(String path, double latitude, double longitude){

        ExifInterface exif;
        try {
            exif = new ExifInterface(path);
            int num1Lat = (int)Math.floor(latitude);
            int num2Lat = (int)Math.floor((latitude - num1Lat) * 60);
            double num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;

            int num1Lon = (int)Math.floor(longitude);
            int num2Lon = (int)Math.floor((longitude - num1Lon) * 60);
            double num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitude>0?"N":"S");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitude>0?"E":"W");

            exif.saveAttributes();

        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    public void upload(String picturePath, int tripId){

        File file = new File(picturePath);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("place[file]", file.getName(), reqFile);

        mUploadProgressBar.setVisibility(View.VISIBLE);
        mButtonGallery.setEnabled(false);
        mButtonCamera.setEnabled(false);


        dataService.uploadPlace(body, tripId, new SimpleCallBack() {
            @Override
            public void response(Object o) {
                mUploadProgressBar.setVisibility(View.GONE);
                inform();
            }

            @Override
            public void fail(Throwable t) {
                mUploadProgressBar.setVisibility(View.GONE);
                mButtonGallery.setClickable(true);
                mButtonCamera.setClickable(true);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void inform(){
        Toast.makeText(getActivity(),R.string.place_created, Toast.LENGTH_SHORT).show();
        PlacesFragment placesFragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString(PLACES_FOR, PLACES_FOR_TRIP);
        args.putInt(ID_STRING, mTripId);
        placesFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_detail, placesFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        dismiss();
    }

    private boolean checkLocationInImage(String photoPath){
        ExifInterface exif;
        try {
            exif = new ExifInterface(photoPath);
            return exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null && exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) != null;

        } catch (IOException e) {
            Log.d(TAG, "problem with exifInterface", e);
        }
        return false;
    }
}
