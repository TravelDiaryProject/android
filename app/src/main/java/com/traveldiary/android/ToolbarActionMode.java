package com.traveldiary.android;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.traveldiary.android.adapter.RecyclerAdapter;
import com.traveldiary.android.fragment.PlacesFragment;
import com.traveldiary.android.fragment.TripsFragment;
import com.traveldiary.android.model.Trip;

import java.util.List;


public class ToolbarActionMode implements ActionMode.Callback {


    private RecyclerAdapter recyclerAdapter;
    private TripsFragment tripsFragment;
    private PlacesFragment placesFragment;


    public ToolbarActionMode(RecyclerAdapter recyclerAdapter, TripsFragment tripsFragment, PlacesFragment placesFragment) {
        this.recyclerAdapter = recyclerAdapter;
        this.tripsFragment = tripsFragment;
        this.placesFragment = placesFragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        if (tripsFragment!=null)
            tripsFragment.deleteRows();
        else
            placesFragment.deleteRows();

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        recyclerAdapter.removeSelection();
        if (tripsFragment!=null)
            tripsFragment.setNullToActionMode();
        else
            placesFragment.setNullToActionMode();
    }
}
