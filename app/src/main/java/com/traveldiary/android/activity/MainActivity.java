package com.traveldiary.android.activity;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.traveldiary.android.Validator;
import com.traveldiary.android.adapter.ViewPagerAdapter;
import com.traveldiary.android.fragment.FindPlaceFragment;
import com.traveldiary.android.fragment.PlacesFragment;
import com.traveldiary.android.R;
import com.traveldiary.android.fragment.TripsFragment;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.APP_PREFERENCES;
import static com.traveldiary.android.Constans.APP_PREFERENCES_EMAIL;
import static com.traveldiary.android.Constans.APP_PREFERENCES_TOKEN;
import static com.traveldiary.android.Constans.CREATE_TRIP;
import static com.traveldiary.android.Constans.FUTURE;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.TOKEN_CONST;
import static com.traveldiary.android.Constans.PLACES_FOR_TOP;
import static com.traveldiary.android.Constans.TRIPS_FOR;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TripsFragment.OnPlaneButtonListener, View.OnClickListener {

    private String mUserName;
    private TextView mUserNameTextView;
    private DrawerLayout mDrawerLayout;
    private SharedPreferences mSharedPreferences;
    //private NavigationView mNavigationView;
    //private TabLayout mTabLayout;
    //private ViewPager mViewPager;
    private FloatingActionButton mFab;
    private long mBack_pressed;

    private BottomNavigationView mBottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnBottomNavigationItemSelectedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.altenative_activity_main);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkAuthorizasion();


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();*/

//        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
//        if (mUserName !=null) {
//            View headerView = mNavigationView.getHeaderView(0);
//            mUserNameTextView = (TextView) headerView.findViewById(R.id.navHeaderUserEmail);
//            mUserNameTextView.setText(mUserName);
//        }
//        mNavigationView.setNavigationItemSelectedListener(this);
//        mNavigationView.setCheckedItem(R.id.menu_top_places);
//        setTitle("Top Places");

//        mFab = (FloatingActionButton) findViewById(R.id.add_trip_button);
//        mFab.setOnClickListener(this);
//        mFab.hide();

        mOnBottomNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_top:
                        PlacesFragment topPlacesFragment = new PlacesFragment();
                        Bundle args = new Bundle();
                        args.putString(PLACES_FOR, PLACES_FOR_TOP);
                        topPlacesFragment.setArguments(args);

                        trans(topPlacesFragment);
                        //mTextMessage.setText(R.string.title_home);
                        return true;
                    case R.id.navigation_my_trips:
                        TripsFragment myTripsFagment = new TripsFragment();
                        args = new Bundle();
                        args.putString(TRIPS_FOR, MY);
                        myTripsFagment.setArguments(args);

                        trans(myTripsFagment);
                        //mTextMessage.setText(R.string.title_dashboard);
                        return true;
                    case R.id.navigation_future_trips:
                        TripsFragment futureTripsFragment = new TripsFragment();
                        args = new Bundle();
                        args.putString(TRIPS_FOR, FUTURE);
                        futureTripsFragment.setArguments(args);

                        trans(futureTripsFragment);
                        //mTextMessage.setText(R.string.title_notifications);
                        return true;
                }
                return false;
            }

        };

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnBottomNavigationItemSelectedListener);

        /*mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        setTitle("Top Places");
                        mNavigationView.setCheckedItem(R.id.menu_top_places);
                        mFab.hide();
                        break;
                    case 1:
                        setTitle("My Trips");
                        mNavigationView.setCheckedItem(R.id.menu_good_memories);
                        mFab.show();
                        break;
                    case 2:
                        setTitle("Future Trips");
                        mNavigationView.setCheckedItem(R.id.menu_future_trips);
                        mFab.hide();
                        break;
                    case 3:
                        setTitle("Find Place");
                        mNavigationView.setCheckedItem(R.id.menu_find_place);
                        mFab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();*/

        //mTabLayout.getTabAt(0).select();

    }

    /*private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        PlacesFragment topPlacesFragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString(PLACES_FOR, PLACES_FOR_TOP);
        topPlacesFragment.setArguments(args);

        TripsFragment myTripsFagment = new TripsFragment();
        args = new Bundle();
        args.putString(TRIPS_FOR, MY);
        myTripsFagment.setArguments(args);

        TripsFragment futureTripsFragment = new TripsFragment();
        args = new Bundle();
        args.putString(TRIPS_FOR, FUTURE);
        futureTripsFragment.setArguments(args);

        adapter.addFragment(topPlacesFragment);
        adapter.addFragment(myTripsFagment);
        adapter.addFragment(futureTripsFragment);
        adapter.addFragment(new FindPlaceFragment());
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_like);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_menu_good_memories);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_menu_future_trips);
        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_menu_find_place);
    }*/

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
                if (mBack_pressed + 2000 > System.currentTimeMillis())
                    super.onBackPressed();
                else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.press_once_again),
                            Toast.LENGTH_SHORT).show();
                }
                mBack_pressed = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        if (TOKEN_CONST != null && !TOKEN_CONST.equals("")) {
            MenuItem singItem = menu.findItem(R.id.action_login);
            singItem.setTitle(getResources().getString(R.string.log_out));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_login) {
            if (TOKEN_CONST != null && !TOKEN_CONST.equals("")) {
                TOKEN_CONST = null;
                mSharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.remove(APP_PREFERENCES_TOKEN);
                editor.apply();

                dataService.removeAll();
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
        switch (item.getItemId()) {
            case R.id.menu_good_memories:
                //mTabLayout.getTabAt(1).select();
                break;

            case R.id.menu_future_trips:
                //mTabLayout.getTabAt(2).select();
                break;

            case R.id.menu_find_place:
                //mTabLayout.getTabAt(3).select();
                break;

            case R.id.menu_top_places:
                //mTabLayout.getTabAt(0).select();
                break;
        }

        item.setChecked(true);

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onPlaneButtonClick() {
        //mTabLayout.getTabAt(3).select();
}

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.add_trip_button){
            if (TOKEN_CONST!=null && !TOKEN_CONST.equals("")) {
                if (Validator.isNetworkAvailable(this)) {
                    Intent intent = new Intent(this, CreateFindActivity.class);
                    intent.putExtra(CREATE_TRIP, CREATE_TRIP);
                    startActivity(intent);
                } else {
                    Snackbar.make(v, "Need a connection to create new trip" , Snackbar.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.need_authorization_function), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void trans(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        //ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
