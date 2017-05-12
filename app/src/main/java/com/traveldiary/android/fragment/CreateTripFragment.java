package com.traveldiary.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.traveldiary.android.R;
import com.traveldiary.android.callback.SimpleCallBack;

import static com.traveldiary.android.App.dataService;


public class CreateTripFragment extends Fragment {

    private ImageView mCreateTripButton;
    private EditText mEditTripTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_trip,
                container, false);

        mCreateTripButton = (ImageView) rootView.findViewById(R.id.createTripButton);
        mEditTripTitle = (EditText) rootView.findViewById(R.id.editTripTitle);

        mCreateTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditTripTitle != null){
                    String tripTitle = mEditTripTitle.getText().toString().trim();


                    dataService.createTrip(tripTitle, new SimpleCallBack() {
                        @Override
                        public void response(Object o) {
                            Toast.makeText(getActivity(),"Trip created!!!", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }

                        @Override
                        public void fail(Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return rootView;
    }
}