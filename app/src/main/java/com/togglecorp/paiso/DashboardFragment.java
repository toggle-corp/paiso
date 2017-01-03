package com.togglecorp.paiso;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class DashboardFragment extends Fragment {
    private static final String TAG = "Dashboard Fragment";
    private TimelineView timelineView;

    public DashboardFragment() {
        // Required empty public constructor
//        timelineView = (TimelineView) getActivity().findViewById(R.id.canvas);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        timelineView = (TimelineView) view.findViewById(R.id.canvas);

        Log.d(TAG, "showing");
        return view;
    }

}
