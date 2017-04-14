package com.traveldiary.android;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.traveldiary.android.network.CallBack;

import static com.traveldiary.android.App.network;
import static com.traveldiary.android.Constans.TOKEN_CONST;


public class CreatTripFragment extends Fragment {

    private Button createTripButton;
    private EditText editTripTitle;

    private ChangeFragmentInterface mChangeFragmentInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creat_trip,
                container, false);

        createTripButton = (Button) rootView.findViewById(R.id.createTripButton);
        editTripTitle = (EditText) rootView.findViewById(R.id.editTripTitle);

        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTripTitle != null){
                    String tripTitle = editTripTitle.getText().toString();

                    network.createTrip(TOKEN_CONST, tripTitle, new CallBack() {
                        @Override
                        public void responseNetwork(Object o) {
                            Toast toast = Toast.makeText(getActivity(),
                                    "Trip created!!!", Toast.LENGTH_SHORT);
                            toast.show();

                            Fragment fragment = new TripsFragment();
                            mChangeFragmentInterface.trans(fragment);
                        }

                        @Override
                        public void failNetwork(Throwable t) {
                            Toast toast = Toast.makeText(getActivity(),
                                    "FAIL!!!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mChangeFragmentInterface = (ChangeFragmentInterface) activity;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
