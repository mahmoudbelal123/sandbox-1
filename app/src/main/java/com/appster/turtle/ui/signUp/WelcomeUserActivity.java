/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.signUp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityWelcomeUserBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
/*
* Activity for Welcome user
 */
public class WelcomeUserActivity extends BaseActivity {

    private static final String ACTIVITY_WELCOME = "welcome-activity";
    private ActivityWelcomeUserBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome_user);

        initDataBinding();
    }

    private void initDataBinding() {
        mBinding.setUser(PreferenceUtil.getUser());

    }

    @Override
    public String getActivityName() {
        return ACTIVITY_WELCOME;
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_tour:
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.BundleKey.SHOW_TOUR, true);
                ExplicitIntent.getsInstance().navigateTo(this, HomeMainActivity.class, bundle, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();

                break;

            case R.id.tv_start:
                ExplicitIntent.getsInstance().navigateTo(this, HomeMainActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                break;

            default:
                break;


        }
    }
}
