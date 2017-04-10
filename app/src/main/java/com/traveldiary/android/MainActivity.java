package com.traveldiary.android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
//import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import static com.traveldiary.android.Constans.ALL;
import static com.traveldiary.android.Constans.FUTURE;
import static com.traveldiary.android.Constans.LOAD_TO;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.TOP;
import static com.traveldiary.android.Constans.TRIPS_FOR;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ChangeFragmentInterface {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        MainFragment mainFragment = new MainFragment();
        trans(mainFragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_login) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        Bundle args;

        switch (item.getItemId()){
            case R.id.menu_my_trips:
                if (LoginActivity.TOKEN_TO_SEND != null) {
                    fragment = new TripsFragment();
                    args = new Bundle();
                    args.putString(TRIPS_FOR, MY);
                    fragment.setArguments(args);
                    trans(fragment);
                }else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.menu_my_places:
                if (LoginActivity.TOKEN_TO_SEND != null) {
                    fragment = new PlacesFragment();
                    args = new Bundle();
                    args.putString(PLACES_FOR, MY);
                    fragment.setArguments(args);
                    trans(fragment);
                }else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.menu_future_trips:
                if (LoginActivity.TOKEN_TO_SEND != null) {
                    fragment = new TripsFragment();
                    args = new Bundle();
                    args.putString(TRIPS_FOR, FUTURE);
                    fragment.setArguments(args);
                    trans(fragment);
                }else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.menu_my_trips_and_places:
                if (LoginActivity.TOKEN_TO_SEND != null) {
                    fragment = new FragmentTabs();
                    args = new Bundle();
                    args.putString(LOAD_TO, MY);
                    fragment.setArguments(args);
                    trans(fragment);
                }else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.menu_all_trips:
                fragment = new TripsFragment();
                args = new Bundle();
                args.putString(TRIPS_FOR, ALL);
                fragment.setArguments(args);
                trans(fragment);
                break;
            case R.id.menu_top_places:
                fragment = new PlacesFragment();
                args = new Bundle();
                args.putString(PLACES_FOR, TOP);
                fragment.setArguments(args);
                trans(fragment);
                break;
            case R.id.menu_search:

                break;
            case R.id.menu_tripsandplaces:
                fragment = new FragmentTabs();
                args = new Bundle();
                args.putString(LOAD_TO, ALL);
                fragment.setArguments(args);
                trans(fragment);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

  /*  public void trans(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }*/

    @Override
    public void trans(android.support.v4.app.Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
