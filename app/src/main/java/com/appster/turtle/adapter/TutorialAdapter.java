/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appster.turtle.R;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.tutorial.TutorialFragment;

/**
 * Created by atul on 13/09/17.
 * To inject activity reference.
 */

public class TutorialAdapter extends FragmentStatePagerAdapter {

    public TutorialAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle;
        Fragment fragment;
        switch (position) {
            case 0:
                bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.TXT_RES_ID, R.string.txt_tut_1);
                bundle.putBoolean(Constants.BundleKey.TXT_SIZE_CHANGE, true);
                bundle.putInt(Constants.BundleKey.TXT_ID, 0);

                fragment = TutorialFragment.newInstance(bundle);
                break;
            case 1:
                bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.TXT_RES_ID, R.string.txt_tut_2);
                bundle.putBoolean(Constants.BundleKey.TXT_SIZE_CHANGE, false);
                bundle.putInt(Constants.BundleKey.TXT_ID, 1);

                fragment = TutorialFragment.newInstance(bundle);
                break;
            case 2:
                bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.TXT_RES_ID, R.string.txt_tut_3);
                bundle.putBoolean(Constants.BundleKey.TXT_SIZE_CHANGE, false);
                bundle.putInt(Constants.BundleKey.TXT_ID, 2);

                fragment = TutorialFragment.newInstance(bundle);
                break;
            case 3:
                bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.TXT_RES_ID, R.string.txt_tut_4);
                bundle.putBoolean(Constants.BundleKey.TXT_SIZE_CHANGE, false);
                bundle.putInt(Constants.BundleKey.TXT_ID, 3);

                fragment = TutorialFragment.newInstance(bundle);
                break;

            default:
                bundle = new Bundle();

                bundle.putBoolean(Constants.BundleKey.TXT_SIZE_CHANGE, false);
                bundle.putInt(Constants.BundleKey.TXT_ID, 4);

                bundle.putInt(Constants.BundleKey.TXT_RES_ID, R.string.txt_tut_5);
                fragment = TutorialFragment.newInstance(bundle);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

}
