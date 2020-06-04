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
import android.support.annotation.Nullable;
import android.text.Html;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityCompleteProfileBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.tutorial.TutorialActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;

/**
 * Created by atul on 18/09/17.
 * To inject activity reference.
 * Activity for complete profile
 */

public class CompleteProfileActivity extends BaseActivity {

    private ActivityCompleteProfileBinding profileBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setUpView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        profileBinding.tvCmpProf.setClickable(true);

    }

    private void initDataBinding() {
        profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_complete_profile);
        profileBinding.setUserName(PreferenceUtil.getUser().getName());
    }

    private void setUpView() {

        String next = getString(R.string.txt_cpf_mjr_1) + " <font color='#ffad82'>" + PreferenceUtil.getUser().getName() + "</font><br />" + getString(R.string.txt_cpf_mjr_2) + "<br />" + getString(R.string.txt_cpf_mjr_3);
        profileBinding.tvMjr.setText(Html.fromHtml(next));

        profileBinding.tvCmpProf.setOnClickListener(view -> {
            profileBinding.tvCmpProf.setClickable(false);
            ExplicitIntent.getsInstance().navigateTo(CompleteProfileActivity.this, ChooseAvatarActivity.class);
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(CompleteProfileActivity.this, TutorialActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }

}
