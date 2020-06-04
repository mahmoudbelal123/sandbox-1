/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.locationlog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.appster.turtle.R;
import com.appster.turtle.util.LocationUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.google.android.gms.location.LocationRequest;
/**
 * A fragment class for location
 */
public class LocationFragment extends LocationUtils {
    private static LocationFragment fragment;


    private View view;
    private EditText etDistance;
    private EditText etTime;
    private Button btnSave;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;

    public LocationFragment() {
        //
    }

    public static LocationFragment newInstance() {
        fragment = new LocationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setBackground(true); ///Location service will run in background even if killed
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R
                .layout.fragment_gps, container, false);

        initView();
        return view;

    }


    private void initView() {
        spinner = view.findViewById(R.id.spinner_priority);
        etDistance = view.findViewById(R.id.et_distance);
        etTime = view.findViewById(R.id.et_time);
        btnSave = view.findViewById(R.id.btn_save);

        etDistance.setText(String.valueOf(MINIMUM_DISPLACEMENT));
        etTime.setText(String.valueOf(UPDATE_INTERVAL_IN_MILLISECONDS));

        adapter = ArrayAdapter
                .createFromResource(getActivity(),
                        R.array.priority, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        btnSave.setOnClickListener(view -> {

            int priority = LocationRequest.PRIORITY_LOW_POWER;
            switch (spinner.getSelectedItemPosition()) {
                case 0:
                    priority = LocationRequest.PRIORITY_LOW_POWER;
                    break;
                case 1:
                    priority = LocationRequest.PRIORITY_NO_POWER;
                    break;
                case 2:
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                    break;
                case 3:
                    priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                    break;
                default:
                    break;

            }
            if (!TextUtils.isEmpty(etTime.getText().toString()) && !TextUtils.isEmpty(etDistance.getText().toString())) {

                PreferenceUtil.setPriority(priority);
                setCustomLocationRequest(Integer.parseInt(etTime.getText().toString()), Integer.parseInt(etDistance.getText().toString()), priority);
            }
        });

    }


}
