package com.traveldiary.android.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.traveldiary.android.DataParser;
import com.traveldiary.android.R;
import com.traveldiary.android.callback.SimpleCallBack;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.callback.CallbackPlaces;
import com.traveldiary.android.model.Trip;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.PLACE_ID;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MAPS";
    private GoogleMap mMap;
    private List<LatLng> mCoordinates;
    private int mTripId;
    private int mFocusPlaceId;
    private List<Place> mPlacesList = new ArrayList<>();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMap);
        setSupportActionBar(toolbar);
        setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mContext = this;

        if (getIntent().getExtras() != null) {
            mTripId = getIntent().getExtras().getInt(ID_STRING);
            mFocusPlaceId = getIntent().getExtras().getInt(PLACE_ID);
        }

        dataService.getTripById(mTripId, new SimpleCallBack() {
            @Override
            public void response(Object o) {
                Trip trip = (Trip) o;
                setTitle(trip.getTitle());
            }

            @Override
            public void fail(Throwable t) {
                System.out.println(t.getMessage());
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;

        dataService.getPlacesByTrip(mTripId, new CallbackPlaces() {
            @Override
            public void response(List<Place> placeList) {
                mPlacesList.addAll(placeList);
                smth(map);
            }

            @Override
            public void fail(Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MarkerOptions markerInit(Place place){

        LatLng latLng = new LatLng(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()));

        mCoordinates.add(latLng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(place.getTitle());

        return markerOptions;
    }

    private void smth(GoogleMap googleMap){

        mCoordinates = new ArrayList<>();

        for (int i = 0; i < mPlacesList.size(); i++){
            if (mPlacesList.get(i).getLatitude()!=null && mPlacesList.get(i).getLongitude()!=null
                    && !mPlacesList.get(i).getLatitude().equals("")) {
                googleMap.addMarker(markerInit(mPlacesList.get(i)));

                if (mPlacesList.get(i).getId() == mFocusPlaceId){

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(Double.parseDouble(mPlacesList.get(i).getLatitude()),
                                    Double.parseDouble(mPlacesList.get(i).getLongitude())), 10));
                }
            }
        }

        String url = getDirectionsUrl(mCoordinates);

        Log.d(TAG, url);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

    }

    private String getDirectionsUrl( List<LatLng> coordinates ) {

        // EXAMPLE = https://maps.googleapis.com/maps/api/directions/json?
        // origin=49.442881,2032.056765
        // &destination=49.446446,2032.061647
        // &waypoints=49.446320,2032.055853
        // |49.448713,2032.053890
        // &key=AIzaSyDUw13lKf_QKtQAce0JkGIz_e62xzYE-dY
        int lastCoordinate = coordinates.size()-1;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://maps.googleapis.com/maps/api/directions/json?origin=");
        stringBuilder.append(coordinates.get(0).latitude);
        stringBuilder.append(",");
        stringBuilder.append(coordinates.get(0).longitude);
        stringBuilder.append("&destination=");
        stringBuilder.append(coordinates.get(lastCoordinate).latitude);
        stringBuilder.append(",");
        stringBuilder.append(coordinates.get(lastCoordinate).longitude);
        stringBuilder.append("&waypoints=");

        for (int i = 1; i < lastCoordinate; i++){
            stringBuilder.append(coordinates.get(i).latitude);
            stringBuilder.append(",");
            stringBuilder.append(coordinates.get(i).longitude);
            if (i != lastCoordinate-1){
                stringBuilder.append("|");
            }
        }

        return stringBuilder.toString();
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        URL url = new URL(strUrl);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();

        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }



}