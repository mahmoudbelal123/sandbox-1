/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.signUp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityVerifyCodeBinding;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.request.auth.VerifyCode;
import com.appster.turtle.network.response.VerifyEmailResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.tutorial.TutorialActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;

/**
 * Created by atul on 18/09/17.
 * To inject activity reference.
 * Activity for verify code
 */

public class VerifyCodeActivity extends BaseActivity {

    private ActivityVerifyCodeBinding codeBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setUpViews();
    }

    private void initDataBinding() {
        codeBinding = DataBindingUtil.setContentView(this, R.layout.activity_verify_code);
    }

    private void setUpViews() {
        setUpHeader(codeBinding.header.clHeader, getString(R.string.txt_vrf_ttl));
        codeBinding.txtPinEntry.setOnPinEnteredListener(str -> {
            if (str.length() == 4) {
                hideKeyboard();
                validateCode(Integer.parseInt(str.toString()));
            }
        });

        codeBinding.header.clHeader.setBackgroundResource(android.R.color.transparent);
        codeBinding.header.ivIconStart.setOnClickListener(v -> {

        });
        SpannableString ss = new SpannableString(getString(R.string.txt_vrf_nr));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                resendCode();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);


            }
        };

        ss.setSpan(clickableSpan, 22, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        codeBinding.tvResend.setText(ss);
        codeBinding.tvResend.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void validateCode(int code) {
        showProgressBar();
        new BaseCallback<VerifyEmailResponse>(this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService()
                .verifyEmail(new VerifyCode(code))) {
            @Override
            public void onSuccess(VerifyEmailResponse baseResponse) {
                hideProgressBar();
                if (baseResponse.getData() != null) {
                    if (baseResponse.getData().isSuccess()) {
                        UserBaseModel userBaseModel = PreferenceUtil.getUser();
                        userBaseModel.setEmailVerified(true);
                        PreferenceUtil.setUser(userBaseModel);
                        ExplicitIntent.getsInstance().navigateTo(VerifyCodeActivity.this, CompleteProfileActivity.class);
                    } else {
                        StringUtils.displaySnackBar(codeBinding.header.tvHeaderCenter, baseResponse.getData().getMessage());
                    }
                }
            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };
    }

    private void resendCode() {
        showProgressBar();
        new BaseCallback<VerifyEmailResponse>(this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService()
                .resendResponse()) {
            @Override
            public void onSuccess(VerifyEmailResponse baseResponse) {
                hideProgressBar();
                if (baseResponse.getData().isSuccess()) {
                    showSnackBar(getString(R.string.otp_send_to_email));
                }

            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(VerifyCodeActivity.this, TutorialActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }

}
