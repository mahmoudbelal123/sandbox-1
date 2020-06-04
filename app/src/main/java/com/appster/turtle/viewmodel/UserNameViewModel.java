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
import android.widget.EditText;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityUserNameBinding;
import com.appster.turtle.model.SignUp;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.IdResponse;
import com.appster.turtle.network.response.SignUpResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.signUp.VerifyCodeActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by atul on 15/09/17.
 * To inject activity reference.
 */

public class UserNameViewModel extends BaseObservable {

    private final long DELAY = 1000;

    private SignUp signUp;
    private BaseActivity mActivity;
    private Timer timer = new Timer();
    RetrofitRestRepository repository;
    private ActivityUserNameBinding nameBinding;
    private boolean isValidUsername;

    public UserNameViewModel(Activity activity, SignUp signUp, ActivityUserNameBinding nameBinding) {
        this.signUp = signUp;
        this.mActivity = (BaseActivity) activity;
        this.nameBinding = nameBinding;
        this.repository = ((ApplicationController) activity.getApplicationContext()).provideRepository();

        setup();
    }

    public void setup() {

        if ((signUp.getPassword() != null && !signUp.getPassword().isEmpty()) && (signUp.getConfirmPassword() != null && !signUp.getConfirmPassword().isEmpty())) {
            nameBinding.etPassword.setText(signUp.getPassword());
            nameBinding.etRePassword.setText(signUp.getConfirmPassword());
            nameBinding.clPassword.setVisibility(View.INVISIBLE);

//
//            disableEditText(nameBinding.etPassword);
//            disableEditText(nameBinding.etRePassword);

        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
    }

    public SignUp getSignUp() {
        return signUp;
    }

    private void isIdExist() {

        new BaseCallback<IdResponse>(mActivity, repository.getApiService()
                .userId(signUp.getUserName())) {
            @Override
            public void onSuccess(IdResponse emailResponse) {
                if (emailResponse.getData().isSuccess()) {
                    nameBinding.ivUsrName.setVisibility(View.VISIBLE);
                    nameBinding.ivUsrName.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_right));
                    isValidUsername = true;
                    nameBinding.txtError.setVisibility(View.INVISIBLE);

                    nameBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(mActivity, R.color.app_white));
                    enableNextButton(validate());
                } else {
                    nameBinding.ivUsrName.setVisibility(View.VISIBLE);
                    nameBinding.ivUsrName.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_cross));
                    nameBinding.header.tvHeaderEnd.setEnabled(false);
                    isValidUsername = false;
                    nameBinding.txtError.setVisibility(View.VISIBLE);
                    nameBinding.txtError.setText(R.string.err_username_msg);

                    nameBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                    //   StringUtils.displaySnackBar(nameBinding.etUsrName, emailResponse.getData().getMessage());=
                }
            }

            @Override
            public void onFail() {
                nameBinding.ivUsrName.setVisibility(View.INVISIBLE);
            }
        };
    }

    public void signUp() {

        if (!nameBinding.header.tvHeaderEnd.isEnabled())
            return;

        signUp.setDeviceToken();

        mActivity.showProgressBar();
        new BaseCallback<SignUpResponse>(mActivity, repository.getApiService()
                .signUp(signUp)) {
            @Override
            public void onSuccess(SignUpResponse signUpResponse) {
                if (signUpResponse != null && signUpResponse.getData() != null) {
                    mActivity.hideProgressBar();
                    PreferenceUtil.setToken(signUpResponse.getData().getToken());

                    UserBaseModel userBaseModel = signUpResponse.getData().getUserBaseModel();
                    userBaseModel.setfName(signUp.getFirstName().trim());
                    userBaseModel.setlName(signUp.getLastName().trim());
                    PreferenceUtil.setUser(userBaseModel);
                    PreferenceUtil.setUniv(signUpResponse.getData().getUniversityBaseModel());
                    ExplicitIntent.getsInstance().navigateTo(mActivity, VerifyCodeActivity.class);
                }
            }

            @Override
            public void onFail() {
                mActivity.hideProgressBar();
            }
        };
    }

    @Bindable
    public TextWatcher getUserNameWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null)
                    timer.cancel();

                nameBinding.header.tvHeaderEnd.setEnabled(false);
                if (StringUtils.isNullOrEmpty(s.toString())) {

                    nameBinding.ivUsrName.setVisibility(View.INVISIBLE);

                    nameBinding.tvUsrName.setTextColor(ContextCompat.getColor(mActivity, R.color.text_font_color_grey));

                    nameBinding.txtError.setVisibility(View.INVISIBLE);
                    return;
                }

                nameBinding.tvUsrName.setTextColor(ContextCompat.getColor(mActivity, R.color.black));


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isIdExist();
                        }
                    }, DELAY);
                } else {
                    nameBinding.ivUsrName.setVisibility(View.INVISIBLE);

                }
            }
        };
    }

    @Bindable
    public TextWatcher getPasswordWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameBinding.etRePassword.setText("");
                enableNextButton(validate());
                if (StringUtils.isNullOrEmpty(s.toString())) {
                    nameBinding.tvPassword.setTextColor(ContextCompat.getColor(mActivity, R.color.text_font_color_grey));


                } else {

                    nameBinding.tvPassword.setTextColor(ContextCompat.getColor(mActivity, R.color.black));

                    if (s.toString().equals(nameBinding.etRePassword.getText().toString().trim())) {
                        if (StringUtils.isValidPassword(s.toString())) {
                            nameBinding.ivRePassword.setVisibility(View.VISIBLE);
                            nameBinding.ivPassword.setVisibility(View.VISIBLE);
                            nameBinding.txtPassword.setVisibility(View.GONE);

                        }
                    } else {
                        nameBinding.ivRePassword.setVisibility(View.INVISIBLE);
                        nameBinding.ivPassword.setVisibility(View.GONE);
                        nameBinding.txtPassword.setVisibility(View.VISIBLE);

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        };
    }

    @Bindable
    public TextWatcher getConfirmPasswordWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableNextButton(validate());
                if (StringUtils.isNullOrEmpty(s.toString())) {
                    nameBinding.tvRepassword.setTextColor(ContextCompat.getColor(mActivity, R.color.text_font_color_grey));


                } else {
                    nameBinding.tvRepassword.setTextColor(ContextCompat.getColor(mActivity, R.color.black));

//                    if (s.length() >= 6 && (StringUtils.isNullOrEmpty(signUp.getConfirmPassword()) || s.toString().equals(signUp.getPassword()))) {
                    if (s.toString().equals(nameBinding.etPassword.getText().toString().trim())) {
                        if (StringUtils.isValidPassword(s.toString())) {
                            nameBinding.ivRePassword.setVisibility(View.VISIBLE);
                            nameBinding.ivPassword.setVisibility(View.VISIBLE);
                            nameBinding.txtPassword.setVisibility(View.GONE);

                        }
                    } else {
                        nameBinding.ivRePassword.setVisibility(View.INVISIBLE);
                        nameBinding.ivPassword.setVisibility(View.GONE);
                        nameBinding.txtPassword.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        };
    }

    private void enableNextButton(boolean enable) {
        nameBinding.header.tvHeaderEnd.setEnabled(enable);
    }

    private boolean validate() {
        if (StringUtils.isNullOrEmpty(signUp.getUserName()) || StringUtils.isNullOrEmpty(signUp.getPassword()) || StringUtils.isNullOrEmpty(signUp.getConfirmPassword()) || !isValidUsername) {
//            nameBinding.header.tvHeaderEnd.setEnabled(false);
            return false;
        }


        if (!signUp.getPassword().equals(signUp.getConfirmPassword())) {
//            nameBinding.header.tvHeaderEnd.setEnabled(true);
            return false;
        }

        if (signUp.getPassword().length() < Constants.PASSWORD_MIN_LENGTH || signUp.getPassword().length() > Constants.PASSWORD_MAX_LENGTH) {
            return false;
        }

        if (!StringUtils.isValidPassword(signUp.getPassword())) {
            LogUtils.LOGD("password", false + "");
            return false;
        } else {
            LogUtils.LOGD("password", true + "");
        }

        return true;
    }

}
