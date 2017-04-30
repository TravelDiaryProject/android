package com.traveldiary.android;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.traveldiary.android.data.DataService;
import com.traveldiary.android.network.CallBack;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.TOKEN_CONST;
import static com.traveldiary.android.Constans.TRIPS_FOR;


public class CreatTripFragment extends Fragment {

    private ImageView createTripButton;
    private EditText editTripTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creat_trip,
                container, false);

        createTripButton = (ImageView) rootView.findViewById(R.id.createTripButton);
        editTripTitle = (EditText) rootView.findViewById(R.id.editTripTitle);

        final DataService dataService = new DataService();

        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTripTitle != null){
                    String tripTitle = editTripTitle.getText().toString();

                    dataService.createNewTrip(tripTitle, new CallBack() {
                        @Override
                        public void responseNetwork(Object o) {
                            Toast.makeText(getActivity(),"Trip created!!!", Toast.LENGTH_SHORT).show();

                            Fragment fragment = new TripsFragment();
                            Bundle args = new Bundle();
                            args.putString(TRIPS_FOR, MY);
                            fragment.setArguments(args);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.content_main, fragment);
                            //ft.addToBackStack(null);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            ft.commit();
                        }

                        @Override
                        public void failNetwork(Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return rootView;
    }
}