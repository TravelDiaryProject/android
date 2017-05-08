package com.traveldiary.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.traveldiary.android.R;
import com.traveldiary.android.activity.MainActivity;
import com.traveldiary.android.network.CallBack;

import static com.traveldiary.android.App.dataService;
import static com.traveldiary.android.Constans.MY;
import static com.traveldiary.android.Constans.TRIPS_FOR;


public class CreatTripFragment extends Fragment {

    private ImageView mCreateTripButton;
    private EditText mEditTripTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creat_trip,
                container, false);

        mCreateTripButton = (ImageView) rootView.findViewById(R.id.createTripButton);
        mEditTripTitle = (EditText) rootView.findViewById(R.id.editTripTitle);

        mCreateTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditTripTitle != null){
                    String tripTitle = mEditTripTitle.getText().toString().trim();


                    dataService.createNewTrip(tripTitle, new CallBack() {
                        @Override
                        public void responseNetwork(Object o) {
                            Toast.makeText(getActivity(),"Trip created!!!", Toast.LENGTH_SHORT).show();

                            //Intent intent = new Intent(getActivity(), MainActivity.class);
                            //startActivity(intent);

                              getActivity().finish();
//                            Fragment fragment = new TripsFragment();
//                            Bundle args = new Bundle();
//                            args.putString(TRIPS_FOR, MY);
//                            fragment.setArguments(args);
//                            FragmentTransaction ft = getFragmentManager().beginTransaction();
//                            ft.replace(R.id.content_main, fragment);
//                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                            ft.commit();
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