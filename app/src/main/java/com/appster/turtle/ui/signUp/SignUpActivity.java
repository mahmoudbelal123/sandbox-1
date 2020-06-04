/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.signUp;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivitySignUpBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.settings.PrivacyPolicyActivity;
import com.appster.turtle.ui.settings.TermsAndConditionsActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.viewmodel.SignUpViewModel;

/**
 * Created by atul on 14/09/17.
 * To inject activity reference.
 * Activity for signup
 */

public class SignUpActivity extends BaseActivity {

    private SignUpViewModel signUpViewModel;
    private ActivitySignUpBinding signUpBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setUpViews();
    }

    private void initDataBinding() {
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        signUpViewModel = new SignUpViewModel(this, signUpBinding);
        signUpBinding.header.tvHeaderEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_bg_grey));
        signUpBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.black));
        signUpBinding.setViewModel(signUpViewModel);
        signUpBinding.etEmail.setOnFocusChangeListener(signUpViewModel.focusChange());
        signUpBinding.rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = signUpBinding.rootView.getRootView().getHeight() - signUpBinding.rootView.getHeight();

            if (heightDiff > signUpBinding.rootView.getHeight() / 4) {
                signUpBinding.tvTerms.setVisibility(View.GONE);
            } else {
                signUpBinding.tvTerms.setVisibility(View.VISIBLE);
            }
        });

        SpannableString ss = new SpannableString(getString(R.string.txt_signUp_tnc));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                ExplicitIntent.getsInstance().navigateTo(SignUpActivity.this, TermsAndConditionsActivity.class);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);


            }
        };


        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                ExplicitIntent.getsInstance().navigateTo(SignUpActivity.this, PrivacyPolicyActivity.class);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);


            }
        };

        ss.setSpan(clickableSpan, 31, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan1, 40, 53, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUpBinding.tvTerms.setText(ss);
        signUpBinding.tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void setUpViews() {
        setUpHeader(false, signUpBinding.header.clHeader, getString(R.string.txt_signUp_ttl), getString(R.string.txt_signUp_nxt));
        signUpBinding.header.tvHeaderEnd.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BundleKey.DATA, signUpViewModel.getSignUp());
            ExplicitIntent.getsInstance().navigateTo(SignUpActivity.this, UserNameActivity.class, bundle);
        });
    }

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }

}
