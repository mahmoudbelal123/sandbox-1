/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.databinding.BaseObservable;

import com.appster.turtle.databinding.ActivityEnterBioBinding;
import com.appster.turtle.ui.BaseActivity;

public class UserBioViewModel extends BaseObservable {
    private BaseActivity mActivity;

    public UserBioViewModel(BaseActivity mActivity, ActivityEnterBioBinding dataBinding) {
        this.mActivity = mActivity;
    }


}
