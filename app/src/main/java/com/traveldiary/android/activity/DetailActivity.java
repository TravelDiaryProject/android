package com.traveldiary.android.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.traveldiary.android.callback.CallbackPlaces;
import com.traveldiary.android.fragment.PlacesFragment;
import com.traveldiary.android.R;
import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.fragment.UploadDialog;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.model.Trip;
import com.traveldiary.android.callback.SimpleCallBack;

import java.util.List;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.GALLERY;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.PLACES_FOR_TRIP;
import static com.traveldiary.android.Constans.UPLOAD_FROM;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{

    private final int PERMISSION_REQUEST = 1;
    private boolean permissionsAreGranted;

    private FloatingActionButton mFab;
    private TextView descriptionTrip;

    private int mTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        descriptionTrip = (TextView) findViewById(R.id.descriptionTrip);

        mTripId = getIntent().getIntExtra(ID_STRING, -1);
        

        mFab = (FloatingActionButton) findViewById(R.id.add_place_button);
        mFab.setOnClickListener(this);
        mFab.hide();

        Log.d("MYLOG", " tripID from Intent = " + mTripId);

        dataService.getTripById(mTripId, new SimpleCallBack() {
            @Override
            public void response(Object o) {
                Trip trip = (Trip) o;
                if (trip.getIsMine()==1 && trip.getIsFuture()==0){
                    mFab.show();
                } else if (trip.getIsMine()==1 && trip.getIsFuture()==1){

                }
                setTitle(trip.getTitle());
                descriptionTrip.setText(trip.getDescription());
            }

            @Override
            public void fail(Throwable t) {
                System.out.println(t.getMessage());
            }
        });

        PlacesFragment placesFragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString(PLACES_FOR, PLACES_FOR_TRIP);
        args.putInt(ID_STRING, mTripId);
        placesFragment.setArguments(args);
        trans(placesFragment);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionsAreGranted){
            onClick(mFab);
            permissionsAreGranted = false;
        }
    }

    public void trans(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_detail, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.start_navigation_button){
            dataService.getPlacesByTrip(mTripId, new CallbackPlaces() {
                @Override
                public void response(List<Place> placeList) {

                    StringBuilder sBuilder = new StringBuilder("https://maps.google.ch/maps?daddr=");
                    sBuilder.append(placeList.get((placeList.size()-1)).getLatitude());
                    sBuilder.append(",");
                    sBuilder.append(placeList.get((placeList.size()-1)).getLongitude());

                    for (int i = (placeList.size()-2); i >= 0 ; i--){
                        sBuilder.append(" to:");
                        sBuilder.append(placeList.get(i).getLatitude());
                        sBuilder.append(",");
                        sBuilder.append(placeList.get(i).getLongitude());
                    }
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(sBuilder.toString()));
                    startActivity(intent);

                }

                @Override
                public void fail(Throwable t) {

                }
            });
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.add_place_button){
            permissionsSetting();
        }
    }

    public void permissionsSetting(){

        if (checkStoragePermission() && checkLocationPermission()) {
            startUploadDialog();
        } else if (!checkStoragePermission() && !checkLocationPermission()) {
            requestMultiplePermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            });
        } else if (!checkStoragePermission()) {
            requestMultiplePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        } else if (!checkLocationPermission()) {
            requestMultiplePermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    public boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkLocationPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestMultiplePermissions(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                permissionsAreGranted = true;
            } else {
                isNeverAskSelected();
            }
        } else if (requestCode == PERMISSION_REQUEST && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionsAreGranted = true;
            } else {
                isNeverAskSelected();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void isNeverAskSelected(){
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Snackbar.make(mFab, "Storage permission isn't granted" , Snackbar.LENGTH_LONG)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();

                            Toast.makeText(getApplicationContext(),
                                    "Open Permissions and grant the Storage permission",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .show();
        }
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST) {
            permissionsAreGranted = true;
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startUploadDialog(){
        DialogFragment uploadDialog = new UploadDialog();
        Bundle args = new Bundle();
        args.putInt(ID_STRING, mTripId);
        args.putString(UPLOAD_FROM, GALLERY);
        uploadDialog.setArguments(args);
        uploadDialog.show(getSupportFragmentManager(), "dialo");
    }
}