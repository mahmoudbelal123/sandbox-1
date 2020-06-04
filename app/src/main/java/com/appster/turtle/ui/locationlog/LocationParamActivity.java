/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.locationlog;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.appster.turtle.R;
import com.appster.turtle.ui.BaseActivity;
/**
 * A activity class for location param
 */
public class LocationParamActivity extends BaseActivity {


    @Override
    public String getActivityName() {
        return LocationParamActivity.class.getName();
    }

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location_params);

        LocationFragment fragment = LocationFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frame, fragment);
        fragmentTransaction.commit();
    }


}
