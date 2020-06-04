/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.android.databinding.library.baseAdapters.BR;
import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.model.ForgotPassword;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.StringUtils;

public class ForgotPasswordViewModel extends BaseObservable {

    RetrofitRestRepository repository;
    ForgotPassword forgotPassword;
    boolean isButtonEnable;
    Context context;

    public ForgotPasswordViewModel(Context context) {

        this.context = context;
        this.repository = ((ApplicationController) context.getApplicationContext()).provideRepository();
        this.forgotPassword = new ForgotPassword();

    }

    public void forgotPassword() {

        ((BaseActivity) context).showProgressBar();

        new BaseCallback<BaseResponse>(((BaseActivity) context), repository.getApiService().forgotPassword(forgotPassword)) {
            @Override
            public void onSuccess(BaseResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
//                    StringUtils.displaySnackBar(((BaseActivity) context).findViewById(android.R.id.content), context.getString(R.string.password_reset_msg));

                    ((BaseActivity) context).setResult(Activity.RESULT_OK);
                    ((BaseActivity) context).finish();
                }
            }

            @Override
            public void onFail() {

            }
        };


    }


    @Bindable
    public TextView.OnEditorActionListener getEditorActionListener() {
        return (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                ((BaseActivity) context).hideKeyboard();

                if (validateInput(true))
                    forgotPassword();
                return true;
            }
            return false;
        };
    }


    //NOSONAR
    public View.OnClickListener forgotPasswordClickListener = v -> forgotPassword();

    public View.OnClickListener backClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View v) {
            ((BaseActivity) context).finish();
        }
    };

    @Bindable
    public boolean isButtonEnable() {
        return isButtonEnable;
    }

    public void setButtonEnable(boolean buttonEnable) {
        isButtonEnable = buttonEnable;
        notifyPropertyChanged(BR.buttonEnable);

    }

    public ForgotPassword getForgotPassword() {
        return forgotPassword;
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
                forgotPassword.setEmail(s.toString());
                setButtonEnable(validateInput(false));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        };
    }

    public boolean validateInput(boolean showError) {

        if (StringUtils.isNullOrEmpty(forgotPassword.getEmail())) {

            if (showError)
                StringUtils.displaySnackBar(((BaseActivity) context).findViewById(android.R.id.content), context.getString(R.string.email_empty_msg));


            return false;
        }

        if (!StringUtils.isValidEmail(forgotPassword.getEmail())) {

            if (showError)
                StringUtils.displaySnackBar(((BaseActivity) context).findViewById(android.R.id.content), context.getString(R.string.email_valid_msg));


            return false;
        }

        return true;


    }


}
