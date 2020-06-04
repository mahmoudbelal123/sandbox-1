/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityChangePasswordBinding;
import com.appster.turtle.model.SignUp;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.auth.ChangePasswordRequest;
import com.appster.turtle.network.response.ChangePasswordResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.signUp.UserNameActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;
/*
* Activity for change password
 */
public class ChangePasswordActivity extends BaseActivity {

    private ActivityChangePasswordBinding mBinding;
    private RetrofitRestRepository mRepository;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, getString(R.string.change_password_caps), "", R.drawable.ic_back_black);

        mBinding.tvConfirm.setText(getString(R.string.confirm));
        mBinding.etCurrentPassword.addTextChangedListener(textWatcher);
        mBinding.etNewPassword.addTextChangedListener(textWatcher);
        mBinding.etRetypeNewPassword.addTextChangedListener(textWatcher);

        mBinding.etCurrentPassword.attachLabel(mBinding.tvCurrentPassword);
        mBinding.etNewPassword.attachLabel(mBinding.tvNewPassword);
        mBinding.etRetypeNewPassword.attachLabel(mBinding.tvRetypePassword);


        mBinding.tvConfirm.setOnClickListener(view -> changePassword());
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            if (StringUtils.isNullOrEmpty(mBinding.etCurrentPassword.getText().toString().trim()) &&
                    StringUtils.isNullOrEmpty(mBinding.etRetypeNewPassword.getText().toString().trim()) &&
                    StringUtils.isNullOrEmpty(mBinding.etNewPassword.getText().toString().trim())) {
                mBinding.tvConfirm.setTextColor(ContextCompat.getColor(ChangePasswordActivity.this, R.color.unselected_grey));
                mBinding.tvConfirm.setEnabled(false);


            } else {

                mBinding.tvConfirm.setEnabled(true);
                mBinding.tvConfirm.setTextColor(ContextCompat.getColor(ChangePasswordActivity.this, R.color.app_white));
            }
            if (StringUtils.isValidPassword(mBinding.etNewPassword.getText().toString()))
                mBinding.ivNewPassword.setVisibility(View.VISIBLE);
            else
                mBinding.ivNewPassword.setVisibility(View.GONE);

            if (StringUtils.isValidPassword(mBinding.etRetypeNewPassword.getText().toString()) && mBinding.etRetypeNewPassword.getText().toString().equals(mBinding.etNewPassword.getText().toString()))
                mBinding.ivRetypePassword.setVisibility(View.VISIBLE);
            else
                mBinding.ivRetypePassword.setVisibility(View.GONE);


        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    private boolean isValidPasswords() {

        String currentPassword = mBinding.etCurrentPassword.getText().toString().trim();
        String newPassword = mBinding.etNewPassword.getText().toString().trim();
        String newRetypedPassword = mBinding.etRetypeNewPassword.getText().toString().trim();

//        1. None of the fields empty
        if (currentPassword.isEmpty()) {
            showSnackBar(getString(R.string.cureent_password));
            return false;
        }
        if (newPassword.isEmpty()) {
            showSnackBar(getString(R.string.entre_new_password));
            return false;
        }
        if (newRetypedPassword.isEmpty()) {
            showSnackBar(getString(R.string.retype_password));
            return false;
        }

//        2. Check if current password is same as new password
        if (currentPassword.equals(newPassword)) {
            showSnackBar(getString(R.string.password_equal_err));
            return false;
        }

//        3. Check if new and retype new is same
        if (!newPassword.equals(newRetypedPassword)) {
            showSnackBar(getString(R.string.error_new_password));
            return false;
        }

        if (!StringUtils.isValidPassword(newPassword)) {
            showSnackBar(getString(R.string.vaild_password));
            return false;
        }

        return true;

    }

    private void changePassword() {


        if (!isValidPasswords())
            return;

        showProgressBar();

        final ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPass(mBinding.etCurrentPassword.getText().toString().trim());
        request.setNewPass(mBinding.etNewPassword.getText().toString().trim());
        request.setConfirmPass(mBinding.etRetypeNewPassword.getText().toString().trim());

        new BaseCallback<ChangePasswordResponse>(this, mRepository.getApiService().changePassword(request), true) {

            @Override
            public void onSuccess(ChangePasswordResponse response) {
                if (response.getData() != null) {
                    PreferenceUtil.setToken(response.getData().getToken());

                    showToast(getString(R.string.change_password_done));
                    finish();

                    if (PreferenceUtil.getUser().isPasswordResetReq()) {

                        SignUp signUp = new SignUp();
                        signUp.setEmail(PreferenceUtil.getUser().getEmail());
                        signUp.setFirstName(PreferenceUtil.getUser().getfName());
                        signUp.setLastName(PreferenceUtil.getUser().getlName());
                        signUp.setPassword(mBinding.etNewPassword.getText().toString().trim());
                        signUp.setConfirmPassword(mBinding.etNewPassword.getText().toString().trim());

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.BundleKey.DATA, signUp);
                        ExplicitIntent.getsInstance().navigateTo(ChangePasswordActivity.this, UserNameActivity.class, bundle);
                    }
                } else if (response.getMeta() != null) {
                    showSnackBar(response.getMeta().getMessage());
                }
            }

            @Override
            public void onFail() {

            }
        };
    }
}
