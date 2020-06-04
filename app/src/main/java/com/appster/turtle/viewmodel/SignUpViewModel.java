/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivitySignUpBinding;
import com.appster.turtle.model.SignUp;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.IdResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by atul on 14/09/17.
 * To inject activity reference.
 */

public class SignUpViewModel extends BaseObservable {

    private final long DELAY = 1000;

    SignUp signUp;
    BaseActivity mActivity;
    private Timer timer = new Timer();
    RetrofitRestRepository repository;
    ActivitySignUpBinding signUpBinding;
    private boolean isValidEmail;

    public SignUpViewModel(Activity activity, ActivitySignUpBinding signUpBinding) {
        this.signUpBinding = signUpBinding;
        this.repository = ((ApplicationController) activity.getApplicationContext()).provideRepository();
        this.mActivity = (BaseActivity) activity;
        this.signUp = new SignUp();
        this.signUpBinding.etFstName.attachLabel(this.signUpBinding.tvFirstNameHeader);
        this.signUpBinding.etLstName.attachLabel(this.signUpBinding.tvLastNameHeader);
        this.signUpBinding.etEmail.attachLabel(this.signUpBinding.tvEmailHeader);
    }

    private void isEmailExist() {
        new BaseCallback<IdResponse>(mActivity, repository.getApiService()
                .checkEmail(signUp)) {
            @Override
            public void onSuccess(IdResponse emailResponse) {
                if (emailResponse.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
//                    signUpBinding.ivEmail.setVisibility(View.VISIBLE);
                    signUpBinding.txtError.setVisibility(View.INVISIBLE);
//                    signUpBinding.ivEmail.setImageDrawable(ContextCompat.getDrawable(mActivity , R.drawable.ic_right));
                    signUpBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(mActivity, R.color.app_white));
                    signUpBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right, 0);
                    signUpBinding.tvUnviversityName.setVisibility(View.VISIBLE);
                    signUpBinding.tvUnviversityName.setText(emailResponse.getData().getUniversityBaseModel().getDisplayName());
                    isValidEmail = true;
                    validate();
                } else {
                    isValidEmail = false;
//                    signUpBinding.ivEmail.setVisibility(View.VISIBLE);
//                    signUpBinding.ivEmail.setImageDrawable(ContextCompat.getDrawable(mActivity , R.drawable.ic_cross));
                    signUpBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);
                    signUpBinding.header.tvHeaderEnd.setEnabled(false);
                    signUpBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                    signUpBinding.txtError.setVisibility(View.VISIBLE);
                    signUpBinding.txtError.setText(emailResponse.getMeta().getMessage());
                    signUpBinding.tvUnviversityName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail() {

//                signUpBinding.ivEmail.setVisibility(View.INVISIBLE);
                signUpBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);
                signUpBinding.header.tvHeaderEnd.setEnabled(false);
                signUpBinding.tvUnviversityName.setVisibility(View.GONE);


            }
        };
    }


    @Bindable
    public TextWatcher getFirstNameWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validate();
                if (StringUtils.isNullOrEmpty(s.toString())) {
                    //    signUpBinding.vFstName.setBackgroundResource(R.color.unselected_grey);
                    signUpBinding.tvFirstNameHeader.setTextColor(ContextCompat.getColor(mActivity, R.color.unselected_grey));
                    //    signUpBinding.vFstName.setBackgroundResource(R.color.text_font_color_grey);
                    //signUpBinding.tvFirstNameHeader.setTextColor(ContextCompat.getColor(mActivity,R.color.text_font_color_grey));
//                    signUpBinding.ivFstName.setVisibility(View.INVISIBLE);
                    signUpBinding.etFstName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


                    return;
                }

                signUpBinding.tvFirstNameHeader.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                //  signUpBinding.vFstName.setBackgroundResource(R.color.app_white);
//                signUpBinding.ivFstName.setVisibility(View.VISIBLE);
                signUpBinding.etFstName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right, 0);

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        };
    }


    public View.OnFocusChangeListener focusChange() {
        return (v, hasFocus) -> {
            if (!hasFocus) {

                if (!StringUtils.isNullOrEmpty(signUpBinding.etEmail.getText().toString()) && !StringUtils.isValidEmail(signUpBinding.etEmail.getText().toString())) {
                    signUpBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);
                    signUpBinding.header.tvHeaderEnd.setEnabled(false);
                    signUpBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                    signUpBinding.txtError.setVisibility(View.VISIBLE);
                    signUpBinding.txtError.setText(R.string.email_valid_msg);
                } else {
                    signUpBinding.txtError.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    @Bindable
    public TextWatcher getLastNameWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validate();
                if (StringUtils.isNullOrEmpty(s.toString())) {
                    signUpBinding.tvLastNameHeader.setTextColor(ContextCompat.getColor(mActivity, R.color.unselected_grey));
//                    signUpBinding.ivLstName.setVisibility(View.INVISIBLE);

                    signUpBinding.etLstName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    return;
                }
                signUpBinding.tvLastNameHeader.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                signUpBinding.etLstName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right, 0);

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        };
    }

    @Bindable
    public TextWatcher getEmailWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null)
                    timer.cancel();
                signUpBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                signUpBinding.tvUnviversityName.setVisibility(View.INVISIBLE);

                signUpBinding.header.tvHeaderEnd.setEnabled(false);
                if (StringUtils.isNullOrEmpty(s.toString())) {
//                    signUpBinding.ivEmail.setVisibility(View.INVISIBLE);
                    signUpBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    signUpBinding.txtError.setVisibility(View.INVISIBLE);
                    signUpBinding.tvUnviversityName.setVisibility(View.INVISIBLE);
                    signUpBinding.tvEmailHeader.setTextColor(ContextCompat.getColor(mActivity, R.color.unselected_grey));

                    return;
                }
                if (signUpBinding.txtError.getVisibility() == View.VISIBLE) {
                    signUpBinding.txtError.setVisibility(View.INVISIBLE);
                }
                signUpBinding.tvEmailHeader.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isValidEmail(s.toString())) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isEmailExist();
                        }
                    }, DELAY);

                } else {
                    signUpBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    signUpBinding.tvUnviversityName.setVisibility(View.INVISIBLE);


//                    signUpBinding.ivEmail.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    public SignUp getSignUp() {
        return signUp;
    }

    private void validate() {
        if (StringUtils.isNullOrEmpty(signUp.getFirstName()) || StringUtils.isNullOrEmpty(signUp.getLastName()) || StringUtils.isNullOrEmpty(signUp.getEmail()) || !isValidEmail)
            signUpBinding.header.tvHeaderEnd.setEnabled(false);
        else
            signUpBinding.header.tvHeaderEnd.setEnabled(true);
    }

}
