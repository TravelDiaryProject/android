package com.traveldiary.android;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WatchingActivity extends AppCompatActivity {

    static final String ROOT_URL = "http://188.166.77.89/";

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Place place;
    private List<Place> mPlaceList;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar mProgressBar;

    private static final String URL_TRIPS = "http://188.166.77.89/api/v1/trips";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        //mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mPlaceList = new ArrayList<>();

        DownloaddImageTask downloaddImageTask = new DownloaddImageTask();
        downloaddImageTask.execute();

    }

    public class DownloaddImageTask extends AsyncTask<Void, Void, Void> {

        RequestQueue queue = Volley.newRequestQueue(WatchingActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            System.out.println("onPreExecute");

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Request a jsonArray response from the provided URL.

            System.out.println("doInBackground_1 : List size = " +  mPlaceList.size());

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL_TRIPS,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            System.out.println("RESPONSE = " + response.toString());

                            for (int i = 0; i < response.length(); i++){
                                try {
                                    JSONObject trip = response.getJSONObject(i);

                                    place = new Place(trip.getString("title"), trip.getString("photo"));
                                    mPlaceList.add(place);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            System.out.println("doInBackground_2 : List size = " +  mPlaceList.size());

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("ERROR = " + error.toString());
                }
            });

            // Add the request to the RequestQueue.
            queue.add(jsonArrayRequest);

            System.out.println("doInBackground_3 : List size = " +  mPlaceList.size());
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
            // [... Обновите индикатор хода выполнения, уведомления или другой
            // элемент пользовательского интерфейса ...]
            //mProgressBar.setProgress(0);

            System.out.println("onProgressUpdate");

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            System.out.println("onPostExecute: List size =  " + mPlaceList.size());

            RecyclerAdapter recyclerAdapter;
            recyclerAdapter = new RecyclerAdapter(WatchingActivity.this, mPlaceList);
            mLayoutManager = new LinearLayoutManager(WatchingActivity.this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(recyclerAdapter);


        }
    }

}

