package com.traveldiary.android.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.traveldiary.android.fragment.FindPlaceFragment;
import com.traveldiary.android.fragment.PlacesFragment;
import com.traveldiary.android.R;
import com.traveldiary.android.fragment.TripsFragment;

import static com.traveldiary.android.Constans.APP_PREFERENCES;
import static com.traveldiary.android.Constans.APP_PREFERENCES_EMAIL;
import static com.traveldiary.android.Constans.APP_PREFERENCES_TOKEN;
import static com.traveldiary.android.Constans.FUTURE;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.TOKEN_CONST;
import static com.traveldiary.android.Constans.PLACES_FOR_TOP;
import static com.traveldiary.android.Constans.TRIPS_FOR;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String mUserName;
    private TextView mUserNameTextView;

    private DrawerLayout drawerLayout;

    private MenuItem itemEnabled = null;

    private SharedPreferences mSharedPreferences;

    private long back_pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkAuthorizasion();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mUserName !=null) {
            View headerView = navigationView.getHeaderView(0);
            mUserNameTextView = (TextView) headerView.findViewById(R.id.navHeaderUserEmail);
            mUserNameTextView.setText(mUserName);
        }
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.menu_top_places);
        setTitle(navigationView.getMenu().getItem(3).getTitle());

        PlacesFragment placesFragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString(PLACES_FOR, PLACES_FOR_TOP);
        placesFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, placesFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }

    @Override
    public void onBackPressed(){

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            android.app.FragmentManager fm = getFragmentManager();
            int count = fm.getBackStackEntryCount();
            if (count > 0) {
                fm.popBackStack();
                fm.executePendingTransactions();
            } else {
                if (back_pressed + 2000 > System.currentTimeMillis())
                    super.onBackPressed();
                else {
                    Toast.makeText(getBaseContext(), "Press once again to exit!",
                            Toast.LENGTH_SHORT).show();
                }
                back_pressed = System.currentTimeMillis();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        if (TOKEN_CONST != null && !TOKEN_CONST.equals("")){
            MenuItem singItem = menu.findItem(R.id.action_login);
            singItem.setTitle("Log Out");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_login) {
            if (TOKEN_CONST!=null && !TOKEN_CONST.equals("")){
                TOKEN_CONST=null;
                mSharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.remove(APP_PREFERENCES_TOKEN);
                editor.apply();
            }
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        Bundle args;

        switch (item.getItemId()){
            case R.id.menu_good_memories:
                if (TOKEN_CONST != null && !TOKEN_CONST.equals("")) {
                    fragment = new TripsFragment();
                    args = new Bundle();
                    args.putString(TRIPS_FOR, MY);
                    fragment.setArguments(args);
                    trans(fragment);
                }else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;

            case R.id.menu_future_trips:
                if (TOKEN_CONST != null && !TOKEN_CONST.equals("")) {
                    fragment = new TripsFragment();
                    args = new Bundle();
                    args.putString(TRIPS_FOR, FUTURE);
                    fragment.setArguments(args);
                    trans(fragment);
                }else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;

            case R.id.menu_find_place:
                fragment = new FindPlaceFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, fragment);
                //ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();


                /// /trans(fragment);
                break;

            case R.id.menu_top_places:
                fragment = new PlacesFragment();
                args = new Bundle();
                args.putString(PLACES_FOR, PLACES_FOR_TOP);
                fragment.setArguments(args);
                //trans(fragment);

                FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                ft1.replace(R.id.content_main, fragment);
                //ft.addToBackStack(null);
                ft1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft1.commit();
                break;
        }

        /*if (itemEnabled == null){
            item.setEnabled(false);
            itemEnabled = item;
        }else {
            itemEnabled.setEnabled(true);
            item.setEnabled(false);
            itemEnabled = item;
        }*/
        item.setChecked(true);
        setTitle(item.getTitle());

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void trans(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        //ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    public void checkAuthorizasion(){
        if (TOKEN_CONST==null || !TOKEN_CONST.equals("")) {
            mSharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
            if (mSharedPreferences.contains(APP_PREFERENCES_TOKEN)) {
                TOKEN_CONST = mSharedPreferences.getString(APP_PREFERENCES_TOKEN, "");
                mUserName = mSharedPreferences.getString(APP_PREFERENCES_EMAIL, "");
            }
        }
    }
}
