/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.android.databinding.library.baseAdapters.BR;
import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.model.SignIn;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.SignInResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.auth.ForgotPasswordActivity;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.signUp.CompleteProfileActivity;
import com.appster.turtle.ui.signUp.VerifyCodeActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;

public class SignInViewModel extends BaseObservable {


    RetrofitRestRepository repository;
    SignIn signIn;
    boolean isSignInEnable;
    BaseActivity context;


    public SignInViewModel(BaseActivity context) {
        this.context = context;
        this.repository = ((ApplicationController) context.getApplicationContext()).provideRepository();
        this.signIn = new SignIn();
    }

    public void login() {
        context.showProgressBar();
        new BaseCallback<SignInResponse>((context), repository.getApiService().signIn(signIn)) {
            @Override
            public void onSuccess(SignInResponse response) {

                // TODO: 03/10/17 Set error message in textView on the basis of response


                UserBaseModel userBaseModel = response.getData().getUserBaseModel();
                PreferenceUtil.setToken(response.getData().getToken());
                PreferenceUtil.setUser(response.getData().getUserBaseModel());
                PreferenceUtil.setUniv(response.getData().getUniversityBaseModel());
                if (userBaseModel.isProfileComplete()) {
                    context.finish();
                    Bundle bundle = new Bundle();
                    ExplicitIntent.getsInstance().navigateTo(context, HomeMainActivity.class, bundle);
                } else if (!userBaseModel.isEmailVerified()) {
                    ExplicitIntent.getsInstance().navigateTo(context, VerifyCodeActivity.class);
                } else if (userBaseModel.isEmailVerified() && !userBaseModel.isProfileComplete()) {
                    ExplicitIntent.getsInstance().navigateTo(context, CompleteProfileActivity.class);
                }
            }

            @Override
            public void onFail() {

            }
        };


    }

    //NOSONAR
    public View.OnClickListener loginClickListener = v -> login();

    public View.OnClickListener forgotPasswordClickListener = new View.OnClickListener() { //NOSONAR

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, ForgotPasswordActivity.class);
            context.startActivityForResult(i, Constants.FORGOT_PASS_REQUEST);
        }
    };

    public View.OnClickListener backClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View v) {
            context.finish();
        }
    };

    @Bindable
    public boolean isSignInEnable() {
        return isSignInEnable;
    }

    public void setSignInEnable(boolean signInEnable) {
        isSignInEnable = signInEnable;
        notifyPropertyChanged(BR.signInEnable);
    }

    public SignIn getSignIn() {
        return signIn;
    }

    @Bindable
    public TextWatcher getPasswordTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                signIn.setPassword(s.toString());
                setSignInEnable(validateInput(false));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        };
    }

    @Bindable
    public TextWatcher getEmailTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                signIn.setEmail(s.toString());
                setSignInEnable(validateInput(false));

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        };
    }

    @Bindable
    public TextView.OnEditorActionListener getEditorActionListener() {
        return (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                context.hideKeyboard();

                if (validateInput(true))
                    login();
                return true;
            }
            return false;
        };
    }


    public boolean validateInput(boolean showError) {

        if (StringUtils.isNullOrEmpty(signIn.getEmail())) {

            if (showError)
                StringUtils.displaySnackBar(context.findViewById(android.R.id.content), context.getString(R.string.email_empty_msg));


            return false;
        }

        if (!StringUtils.isValidEmail(signIn.getEmail())) {

            if (showError)
                StringUtils.displaySnackBar(context.findViewById(android.R.id.content), context.getString(R.string.email_valid_msg));


            return false;
        }

        if (StringUtils.isNullOrEmpty(signIn.getPassword())) {

            if (showError)
                StringUtils.displaySnackBar(context.findViewById(android.R.id.content), context.getString(R.string.password_empty_msg));


            return false;
        }

        if (!(signIn.getPassword().length() >= 6)) {

            if (showError)
                StringUtils.displaySnackBar(context.findViewById(android.R.id.content), context.getString(R.string.password_valid_msg));

            return false;
        }

        return true;


    }

}
