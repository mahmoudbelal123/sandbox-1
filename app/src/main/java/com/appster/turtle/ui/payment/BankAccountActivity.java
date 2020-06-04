/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.payment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityBankAccountBinding;
import com.appster.turtle.model.BankModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetBankDetailResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.StringUtils;
/*
* Activity for bank detail acc
 */
public class BankAccountActivity extends BaseActivity {
    private ActivityBankAccountBinding mBinding;
    private BankModel bankModel;

    private boolean isSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bank_account);

        isSettings = getIntent().getExtras().getBoolean(Constants.BundleKey.FROM_SETTINGS);

        getBankDetail();


        initUi();
    }

    @Override
    public String getActivityName() {
        return BankAccountActivity.class.getName();
    }

    private void initUi() {
        setUpHeader(mBinding.header.clHeader, getString(R.string.bank_acc_details), R.drawable.ic_back_black, 0);

        mBinding.header.tvHeaderCenter.setAllCaps(true);
        mBinding.tvNextBtn.setOnClickListener(view -> {

            if (isValid()) {
                Bundle b = new Bundle();
                b.putParcelable(Constants.BundleKey.BANK_MODEL, bankModel);
                b.putBoolean(Constants.BundleKey.FROM_SETTINGS, isSettings);
                ExplicitIntent.getsInstance().navigateTo(BankAccountActivity.this, BankAccountDetailsActivity.class, b);
            }
        });


        mBinding.tvFirstName.addTextChangedListener(commonTextWatcher);
        mBinding.tvLastName.addTextChangedListener(commonTextWatcher);
        mBinding.tvRoutingNum.addTextChangedListener(commonTextWatcher);
        mBinding.tvAccountNum.addTextChangedListener(commonTextWatcher);
        mBinding.tvConfirmAccountNum.addTextChangedListener(commonTextWatcher);


    }

    private boolean isValid() {
        if (mBinding.tvFirstName.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvFirstName, getString(R.string.bank_first_name_err));
            return false;
        }
        if (mBinding.tvLastName.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvFirstName, "Please enter the last name");

            return false;
        }
        if (mBinding.tvRoutingNum.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvFirstName, "Please enter the routing number");

            return false;
        }
        if (mBinding.tvAccountNum.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvFirstName, "Please enter the account number");

            return false;
        }
        if (mBinding.tvConfirmAccountNum.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvFirstName, "Please confirm account number");

            return false;
        }

        if (!mBinding.tvConfirmAccountNum.getText().toString().equals(mBinding.tvAccountNum.getText().toString())) {
            StringUtils.displaySnackBar(mBinding.tvFirstName, "Account numbers dont match");

            return false;
        }
        return true;

    }

    private TextWatcher commonTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (mBinding.tvFirstName.getText().toString().isEmpty() ||
                    mBinding.tvLastName.getText().toString().isEmpty() ||
                    mBinding.tvRoutingNum.getText().toString().isEmpty() ||
                    mBinding.tvAccountNum.getText().toString().isEmpty() ||
                    mBinding.tvConfirmAccountNum.getText().toString().isEmpty()) {
                mBinding.tvNextBtn.setEnabled(false);
                mBinding.tvNextBtn.setBackgroundResource(R.drawable.grey_soild_roun_rect);
                mBinding.tvNextBtn.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.black));
            } else {
                if (mBinding.tvAccountNum.getText().toString().equals(mBinding.tvConfirmAccountNum.getText().toString())) {
                    mBinding.tvNextBtn.setEnabled(true);
                    mBinding.tvNextBtn.setBackgroundResource(R.drawable.circle_yellow_button);
                    mBinding.tvNextBtn.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.app_white));
                } else {
                    mBinding.tvNextBtn.setEnabled(false);
                    mBinding.tvNextBtn.setBackgroundResource(R.drawable.grey_soild_roun_rect);
                    mBinding.tvNextBtn.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.black));

                }
            }

            if (!mBinding.tvFirstName.getText().toString().isEmpty()) {
                bankModel.setFirstName(mBinding.tvFirstName.getText().toString());
                mBinding.tvFirstNameHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.black));

            } else {
                mBinding.tvFirstNameHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.unselected_grey));
            }
            if (!mBinding.tvLastName.getText().toString().isEmpty()) {
                bankModel.setLastName(mBinding.tvLastName.getText().toString());
                mBinding.tvLastNameHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.black));

            } else {
                mBinding.tvLastNameHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.unselected_grey));
            }
            if (!mBinding.tvRoutingNum.getText().toString().isEmpty()) {
                bankModel.setRoutingNumber(mBinding.tvRoutingNum.getText().toString());
                mBinding.tvRountingHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.black));
            } else {
                mBinding.tvRountingHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.unselected_grey));
            }
            if (!mBinding.tvAccountNum.getText().toString().isEmpty()) {
                bankModel.setAccountNumber(mBinding.tvAccountNum.getText().toString());
                mBinding.tvAccountNumHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.black));

            } else {
                mBinding.tvAccountNumHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.unselected_grey));
            }


            if (mBinding.tvConfirmAccountNum.getText().toString().isEmpty()) {
                mBinding.tvAccountNumConfrmHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.unselected_grey));
            } else {
                mBinding.tvAccountNumConfrmHeader.setTextColor(ContextCompat.getColor(BankAccountActivity.this, R.color.black));
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };


    private void getBankDetail() {

        showProgressBar();
        RetrofitRestRepository mRepository = ((ApplicationController) getApplication()).provideRepository();
        new BaseCallback<GetBankDetailResponse>(this, mRepository.getApiService().getBankDetail(), true) {

            @Override
            public void onSuccess(GetBankDetailResponse response) {
                hideProgressBar();
                if (response.getData() != null) {
                    bankModel = response.getData();
                    bankModel.setFromServer(true);
                    bankModel.setVerificationDocumentId(bankModel.getDocumentUrl());
                } else
                    bankModel = new BankModel();

                mBinding.setBank(bankModel);


            }

            @Override
            public void onFail() {

                hideProgressBar();
                bankModel = new BankModel();
                mBinding.setBank(bankModel);
            }
        };
    }


}
