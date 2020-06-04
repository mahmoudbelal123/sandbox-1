/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivitySignInBinding;
import com.appster.turtle.model.SignUp;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.auth.SignInRequest;
import com.appster.turtle.network.response.SignInResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.settings.ChangePasswordActivity;
import com.appster.turtle.ui.signUp.CompleteProfileActivity;
import com.appster.turtle.ui.signUp.UserNameActivity;
import com.appster.turtle.ui.signUp.VerifyCodeActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
/**
 * A activity class for Signin
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener {

    ActivitySignInBinding mBinding;
    private RetrofitRestRepository mRepository;

    private double mWidthPixels;
    private double mHeightPixels;


    @Override
    public String getActivityName() {
        return SignInActivity.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();


        if (getIntent().hasExtra(Constants.BundleKey.AUTH_ERR)) {
            mBinding.txtError.setVisibility(View.VISIBLE);
            mBinding.txtError.setText(getIntent().getStringExtra(Constants.BundleKey.AUTH_ERR));
        } else {
            mBinding.txtError.setVisibility(View.INVISIBLE);
        }

        navigateIfSignedIn();
        CharSequence charSequence = mBinding.etPassword.getText().toString();
        Utils.getTransformation(charSequence, mBinding.etPassword);

        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, getString(R.string.sign_in), R.drawable.ic_back_black, 0);
        mBinding.header.tvHeaderCenter.setTextColor(ContextCompat.getColor(SignInActivity.this, R.color.black));
        setRealDeviceSizeInPixels();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(mWidthPixels / dm.xdpi, 2);
        double y = Math.pow(mHeightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        LogUtils.LOGD("debug", "Screen inches : " + screenInches);

        if (screenInches > 5) {
            //Show keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(mBinding.clSignIn.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

            //Adjust Resize
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        mBinding.header.ivIconStart.setOnClickListener(this);

        //set last signed-in email
        mBinding.etEmail.addTextChangedListener(emailTextWatcher);
        mBinding.etPassword.addTextChangedListener(passwordTextWatcher);

        if (PreferenceUtil.getUser() != null) {
            mBinding.etEmail.setText(PreferenceUtil.getUser().getEmail());
            mBinding.etEmail.setSelection(mBinding.etEmail.getText().toString().trim().length());
        }

        mBinding.etPassword.setOnEditorActionListener(editorActionListener);
    }

    private void navigateIfSignedIn() {
        UserBaseModel userBaseModel = PreferenceUtil.getUser();
        if (userBaseModel != null && userBaseModel.isProfileComplete() && !StringUtils.isNullOrEmpty(PreferenceUtil.getToken())) {
            finish();
            ExplicitIntent.getsInstance().navigateTo(SignInActivity.this, HomeMainActivity.class);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.FORGOT_PASS_REQUEST) {
            if (resultCode == RESULT_OK) {
                StringUtils.displaySnackBar(findViewById(android.R.id.content), getString(R.string.password_reset_msg));

            }
        }
    }

    TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            mBinding.txtError.setVisibility(View.INVISIBLE);

            //set editText footer color
            if (StringUtils.isNullOrEmpty(s.toString())) {
                mBinding.tvEmailUserName.setTextColor(ContextCompat.getColor(SignInActivity.this, R.color.unselected_grey));
                mBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mBinding.etEmail.setBackground(ContextCompat.getDrawable(SignInActivity.this, R.drawable.edit_text_signin_bg));

            } else {
                mBinding.etEmail.setBackground(ContextCompat.getDrawable(SignInActivity.this, R.drawable.edit_text_underline_black));
                mBinding.tvEmailUserName.setTextColor(ContextCompat.getColor(SignInActivity.this, R.color.black));
                mBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right, 0);
            }
            if (!validateInput()) {
                mBinding.tvSignIn.setEnabled(false);
            } else {
                mBinding.tvSignIn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {

            mBinding.txtError.setVisibility(View.INVISIBLE);

            //set editText footer color

            if (StringUtils.isNullOrEmpty(s.toString())) {
                mBinding.tvPassword.setTextColor(ContextCompat.getColor(SignInActivity.this, R.color.unselected_grey));
                mBinding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mBinding.etPassword.setBackground(ContextCompat.getDrawable(SignInActivity.this, R.drawable.edit_text_signin_bg));
            } else {
                mBinding.etPassword.setBackground(ContextCompat.getDrawable(SignInActivity.this, R.drawable.edit_text_underline_black));
                mBinding.tvPassword.setTextColor(ContextCompat.getColor(SignInActivity.this, R.color.black));
                mBinding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right, 0);
            }
            if (!validateInput()) {
                mBinding.tvSignIn.setEnabled(false);
            } else {
                mBinding.tvSignIn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_start:
                finish();
                break;
        }
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.tv_forgot_password:
                Intent i = new Intent(this, ForgotPasswordActivity.class);
                startActivityForResult(i, Constants.FORGOT_PASS_REQUEST);
                break;

            case R.id.tv_sign_in:
                login();
                break;
        }
    }

    private void login() {

        showProgressBar();

        SignInRequest request = new SignInRequest();
        request.setDeviceToken(FirebaseInstanceId.getInstance().getToken());
        request.setDeviceType(0);
        request.setLoginId(mBinding.etEmail.getText().toString().trim());
        request.setPassword(mBinding.etPassword.getText().toString().trim());

        new BaseCallback<SignInResponse>(this, mRepository.getApiService().signIn(request)) {
            @Override
            public void onSuccess(SignInResponse response) {

                if (response.getMeta().getCode() != Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    mBinding.txtError.setVisibility(View.VISIBLE);
                    if (response.getMeta().getCode() == Constants.RESPONSE_CODE.LOGIN_ID_ERROR) {

                        String error = response.getMeta().getMessage().replace(":", " ");
                        if (error.startsWith("loginId")) {
                            error = error.replace("loginId", "");
                        } else if (error.contains(".,password")) {
                            int pos = error.indexOf(".,passowrd") + 2;
                            error = error.substring(pos + 1).toUpperCase();
                        } else if (error.contains(".,loginId")) {
                            error = error.replace(".,loginId", " ,").toUpperCase();

                        }
                        mBinding.txtError.setText(String.format("%s%s", error.substring(0, 1).toUpperCase(), error.substring(1, error.length())));
                    } else {

                        mBinding.txtError.setText(response.getMeta().getMessage());
                    }
                    if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_AUTH_EMAIL_ERROR) {
                        mBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);
                    } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_AUTH_PASSWORD_ERROR) {
                        mBinding.etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);
                    }
                    return;

                }

                UserBaseModel userBaseModel = response.getData().getUserBaseModel();
                PreferenceUtil.setToken(response.getData().getToken());


                userBaseModel.setfName(userBaseModel.getfName());
                userBaseModel.setlName(userBaseModel.getlName());

                PreferenceUtil.setUser(response.getData().getUserBaseModel());
                PreferenceUtil.setUniv(response.getData().getUniversityBaseModel());

                if (userBaseModel.isPasswordResetReq()) {
                    Bundle bundle = new Bundle();
                    ExplicitIntent.getsInstance().navigateTo(SignInActivity.this, ChangePasswordActivity.class, bundle);

                } else if (userBaseModel.getUserName().isEmpty()) {

                    SignUp signUp = new SignUp();
                    signUp.setEmail(PreferenceUtil.getUser().getEmail());
                    signUp.setFirstName(PreferenceUtil.getUser().getfName());
                    signUp.setLastName(PreferenceUtil.getUser().getlName());
                    signUp.setPassword(mBinding.etPassword.getText().toString().trim());
                    signUp.setConfirmPassword(mBinding.etPassword.getText().toString().trim());

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.BundleKey.DATA, signUp);
                    ExplicitIntent.getsInstance().navigateTo(SignInActivity.this, UserNameActivity.class, bundle);


                } else if (userBaseModel.isProfileComplete()) {
                    finish();
                    Bundle bundle = new Bundle();
                    ExplicitIntent.getsInstance().navigateTo(SignInActivity.this, HomeMainActivity.class, bundle);

                } else if (!userBaseModel.isEmailVerified()) {
                    ExplicitIntent.getsInstance().navigateTo(SignInActivity.this, VerifyCodeActivity.class);
                } else if (userBaseModel.isEmailVerified() && !userBaseModel.isProfileComplete()) {
                    ExplicitIntent.getsInstance().navigateTo(SignInActivity.this, CompleteProfileActivity.class);
                }

            }

            @Override
            public void onFail() {
                LogUtils.LOGD("here", "her");
            }
        };

    }

    private boolean validateInput() {

        String email = mBinding.etEmail.getText().toString().trim();
        String password = mBinding.etPassword.getText().toString().trim();

        return !StringUtils.isNullOrEmpty(email) && !StringUtils.isNullOrEmpty(password);


    }

    private void enableSignIn(boolean enable) {
        mBinding.tvSignIn.setEnabled(enable);
    }


    TextView.OnEditorActionListener editorActionListener = (v, actionId, event) -> {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            hideKeyboard();
            login();
            return true;
        }

        return false;
    };

    private void setRealDeviceSizeInPixels() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);


        // since SDK_INT = 1;
        mWidthPixels = displayMetrics.widthPixels;
        mHeightPixels = displayMetrics.heightPixels;


        try {
            Point realSize = new Point();
            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
            mWidthPixels = realSize.x;
            mHeightPixels = realSize.y;
        } catch (Exception ignored) {
        }
//        }

    }

}
