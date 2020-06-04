/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.auth;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityForgotPasswordBinding;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.auth.ForgotPasswordRequest;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.StringUtils;
/**
 * A activity class for froget password
 */
public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    private ActivityForgotPasswordBinding mBinding;
    private RetrofitRestRepository mRepository;

    @Override
    public String getActivityName() {
        return ForgotPasswordActivity.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, getString(R.string.password_reset_caps), R.drawable.ic_back_black, 0);
        mBinding.header.tvHeaderCenter.setTextColor(ContextCompat.getColor(ForgotPasswordActivity.this, R.color.black));

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        mBinding.header.ivIconStart.setOnClickListener(this);
        mBinding.etEmail.addTextChangedListener(emailTextWatcher);
        mBinding.etEmail.setOnEditorActionListener(editorActionListener);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_start:
                finish();
                break;
            default:
                break;
        }
    }

    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            mBinding.tvErrorText.setVisibility(View.GONE);
            if (StringUtils.isNullOrEmpty(charSequence.toString())) {
                mBinding.tvEmailUserName.setTextColor(ContextCompat.getColor(ForgotPasswordActivity.this, R.color.text_font_color_grey));
                mBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mBinding.tvSend.setEnabled(false);
                return;
            }
            mBinding.tvEmailUserName.setTextColor(ContextCompat.getColor(ForgotPasswordActivity.this, R.color.black));
            mBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right, 0);
            mBinding.tvSend.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    public void forgotPassword(View v) {

        showProgressBar();

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(mBinding.etEmail.getText().toString().trim());

        new BaseCallback<BaseResponse>(this, mRepository.getApiService().forgotPassword(request)) {
            @Override
            public void onSuccess(BaseResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    setResult(Activity.RESULT_OK);
                    finish();
                } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_AUTH_RESET_EMAIL_ERROR) {
                    mBinding.tvErrorText.setVisibility(View.VISIBLE);
                    mBinding.tvErrorText.setText(response.getMeta().getMessage());
                    mBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);
                } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.LOGIN_ID_ERROR) {
                    mBinding.tvErrorText.setVisibility(View.VISIBLE);
                    String[] error = response.getMeta().getMessage().split(":");
                    mBinding.etEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);
                    mBinding.tvErrorText.setText(error[1]);
                }
            }

            @Override
            public void onFail() {
//
            }
        };

    }

    TextView.OnEditorActionListener editorActionListener = (v, actionId, event) -> {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            hideKeyboard();

            if (validateInput(false))
                forgotPassword(null);
            return true;
        }
        return false;
    };

    private void enableForgotPassword(boolean enable) {
        mBinding.tvSend.setEnabled(enable);
    }

    public boolean validateInput(boolean showError) {

        String email = mBinding.etEmail.getText().toString().trim();

        if (StringUtils.isNullOrEmpty(email)) {
            if (showError)
                StringUtils.displaySnackBar(findViewById(android.R.id.content), getString(R.string.email_empty_msg));

            return false;
        }

        if (!StringUtils.isValidEmail(email)) {
            if (showError)
                StringUtils.displaySnackBar(findViewById(android.R.id.content), getString(R.string.email_valid_msg));

            return false;
        }

        return true;
    }


}
