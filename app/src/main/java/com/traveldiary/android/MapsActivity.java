package com.traveldiary.android;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.traveldiary.android.model.Place;
import com.traveldiary.android.network.CallBack;

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

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.ID_STRING;
import static com.traveldiary.android.Constans.PLACE_ID;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MAPS";

    private GoogleMap mMap;

    List<LatLng> coordinates;

    private int tripId;
    private int focusPlaceId;
    private List<Place> mPlacesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        if (getIntent().getExtras()!=null){
            tripId = getIntent().getExtras().getInt(ID_STRING);
            focusPlaceId = getIntent().getExtras().getInt(PLACE_ID);

            Log.d("MYLOG", "tripId = " + tripId + " focusPLaceId = " + focusPlaceId);

        }
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
        //return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;

        network.getPlacesByTrip(tripId, new CallBack() {
            @Override
            public void responseNetwork(Object o) {
                List<Place> placesList = (List<Place>) o;
                mPlacesList.addAll(placesList);
                smth(map);
            }

            @Override
            public void failNetwork(Throwable t) {

            }
        });






    }

    private MarkerOptions markerInit(Place place){

        LatLng latLng = new LatLng(Double.parseDouble(place.getLatitude()), Double.parseDouble(place.getLongitude()));

        coordinates.add(latLng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Title");

        return markerOptions;
    }

    private void smth(GoogleMap googleMap){

        coordinates = new ArrayList<>();

        Log.d("MYLOG", "WTF list size = " + mPlacesList.size());

        for (int i = 0; i < mPlacesList.size(); i++){
            if (mPlacesList.get(i).getLatitude()!=null && mPlacesList.get(i).getLongitude()!=null
                    && !mPlacesList.get(i).getLatitude().equals("")) {
                googleMap.addMarker(markerInit(mPlacesList.get(i)));

                if (mPlacesList.get(i).getId() == focusPlaceId){

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(Double.parseDouble(mPlacesList.get(i).getLatitude()),
                                    Double.parseDouble(mPlacesList.get(i).getLongitude())), 10));
                }

                System.out.println("place lat = " + mPlacesList.get(i).getLatitude() + " long = " + mPlacesList.get(i).getLongitude());
            }

        }

        String url = getDirectionsUrl(coordinates);

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
        stringBuilder.append("waypoints=");


        String origin = "origin=" + coordinates.get(0).latitude + "," + coordinates.get(0).longitude;
        String destination = "destination=" + coordinates.get(lastCoordinate).latitude + "," + coordinates.get(lastCoordinate).longitude;


        for (int i = 1; i < lastCoordinate; i++){
            stringBuilder.append(coordinates.get(i).latitude);
            stringBuilder.append(",");
            stringBuilder.append(coordinates.get(i).longitude);
            if (i != lastCoordinate-1){
                stringBuilder.append("|");
            }
        }



        String url = "https://maps.googleapis.com/maps/api/directions/json?" + origin + "&" + destination + "&" + stringBuilder.toString();

        return url;
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
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
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