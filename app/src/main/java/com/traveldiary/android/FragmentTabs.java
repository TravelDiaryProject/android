package com.traveldiary.android;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.traveldiary.android.essence.Place;

import static com.traveldiary.android.Constans.ALL;
import static com.traveldiary.android.Constans.LOAD_TO;
import static com.traveldiary.android.Constans.PLACES_FOR;
import static com.traveldiary.android.Constans.TRIPS_FOR;


public class FragmentTabs extends Fragment {

    private String loadTo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            loadTo = getArguments().getString(LOAD_TO);
        }
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_tabs, container, false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        //tabLayout.addTab(tabLayout.newTab().setText("1"));
        //tabLayout.addTab(tabLayout.newTab().setText("2"));
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), loadTo));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Trips");
        tabLayout.getTabAt(1).setText("Places");
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs = 2;
        String loadTo = null;
        Bundle args;

        public PagerAdapter(FragmentManager fm, String loadTo) {
            super(fm);
            this.loadTo = loadTo;
        }


        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    TripsFragment tripsFragment = new TripsFragment();
                    args = new Bundle();
                    args.putString(TRIPS_FOR, loadTo);
                    tripsFragment.setArguments(args);
                    return tripsFragment;
                case 1:
                    PlacesFragment placesFragment = new PlacesFragment();
                    args = new Bundle();
                    args.putString(PLACES_FOR, loadTo);
                    placesFragment.setArguments(args);
                    return placesFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}