/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.tutorial;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityMainNoiseBinding;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.auth.SignInActivity;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.signUp.SignUpActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;

/**
 * Created by atul on 13/09/17.
 * To inject activity reference.
 */

public class TutorialActivity extends BaseActivity {

    private ActivityMainNoiseBinding tutorialBinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_noise);


        navigateIfSignedIn();
        tutorialBinding.tvSignUp.setOnClickListener(view -> ExplicitIntent.getsInstance().navigateTo(TutorialActivity.this, SignUpActivity.class));
        tutorialBinding.tvSignIn.setOnClickListener(view -> ExplicitIntent.getsInstance().navigateTo(TutorialActivity.this, SignInActivity.class));
    }


    private void navigateIfSignedIn() {
        UserBaseModel userBaseModel = PreferenceUtil.getUser();
        if (userBaseModel != null && userBaseModel.isEmailVerified() && userBaseModel.isProfileComplete() && !StringUtils.isNullOrEmpty(PreferenceUtil.getToken())) {
            finish();
            ExplicitIntent.getsInstance().navigateTo(TutorialActivity.this, HomeMainActivity.class);
        }
    }


    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }


}
