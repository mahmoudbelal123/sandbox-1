package com.appster.turtle.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.appster.turtle.R;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.tutorial.TutorialActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;

/**
 * Created  on 09/07/18 .
 */

public class SplashActivity extends BaseActivity {

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      DataBindingUtil.setContentView(this, R.layout.activity_splash);
        new Handler().postDelayed(() -> navigateIfSignedIn(), 3500);

    }


    private void navigateIfSignedIn() {
        UserBaseModel userBaseModel = PreferenceUtil.getUser();
        if (userBaseModel != null && userBaseModel.isEmailVerified() && userBaseModel.isProfileComplete() && !StringUtils.isNullOrEmpty(PreferenceUtil.getToken())) {
            ExplicitIntent.getsInstance().navigateTo(SplashActivity.this, HomeMainActivity.class);
            finish();
        }else{
            ExplicitIntent.getsInstance().navigateTo(SplashActivity.this, TutorialActivity.class);
            finish();
        }
    }

}
