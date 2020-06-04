/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.signUp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityUserNameBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.viewmodel.UserNameViewModel;

/**
 * Created by atul on 15/09/17.
 * To inject activity reference.
 * Activity for user name
 */

public class UserNameActivity extends BaseActivity {

    private UserNameViewModel nameViewModel;
    private ActivityUserNameBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setUpViews();
    }

    private void initDataBinding() {
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_name);
        nameViewModel = new UserNameViewModel(this, getIntent().getExtras().getParcelable(Constants.BundleKey.DATA), dataBinding);
        dataBinding.header.tvHeaderEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_bg_grey));
        dataBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.black));
        dataBinding.setViewModel(nameViewModel);


        dataBinding.rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = dataBinding.rootView.getRootView().getHeight() - dataBinding.rootView.getHeight();

            if (heightDiff > dataBinding.rootView.getHeight() / 4) {
                dataBinding.tvFooter.setVisibility(View.GONE);
            } else {
                dataBinding.tvFooter.setVisibility(View.VISIBLE);
            }
        });
        dataBinding.txtPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // checkbox status is changed from uncheck to checked.
            if (isChecked) {
                // show password
                dataBinding.txtPassword.setText(R.string.hide);
                dataBinding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // hide password
                dataBinding.txtPassword.setText(R.string.show);
                dataBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            dataBinding.etPassword.setSelection(dataBinding.etPassword.length());
        });
    }

    private void setUpViews() {
        setUpHeader(false, dataBinding.header.clHeader, getString(R.string.txt_unm_ttl), getString(R.string.txt_signUp_nxt));
        dataBinding.header.tvHeaderEnd.setOnClickListener(view -> nameViewModel.signUp());
    }

    @Override
    public String getActivityName() {
        return null;
    }

}
