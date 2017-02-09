package com.traveldiary.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button allTripsActivityButton;
    private Button uploadActivityButton;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadActivityButton = (Button) findViewById(R.id.uploadActivityButton);
        allTripsActivityButton = (Button) findViewById(R.id.allTripsActivityButton);
        allTripsActivityButton.setOnClickListener(this);
        uploadActivityButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.uploadActivityButton:
                intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
                break;
            case R.id.allTripsActivityButton:
                intent = new Intent(MainActivity.this, AllTripsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
