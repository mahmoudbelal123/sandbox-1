/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.payment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityBankAccountDetailsBinding;
import com.appster.turtle.databinding.LayoutImageSelectorBinding;
import com.appster.turtle.model.BankModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.VerificationImage;
import com.appster.turtle.network.response.BankPostResponse;
import com.appster.turtle.network.response.BankVerificationImageResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.notes.NotesListingActivity;
import com.appster.turtle.ui.settings.SettingsActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.ImagePickerUtils;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;

import java.util.Calendar;

import rx.Observable;

/*
* Activity for bank acc
 */
public class BankAccountDetailsActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, BaseActivity.PermissionI, ImagePickerUtils.OnImagePickerListener {
    private ActivityBankAccountDetailsBinding mBinding;

    private BankModel updateBankModel;
    private BankModel bankModel;
    private RetrofitRestRepository mRepository;
    private String imagePath = "";
    private String verificationId = "";
    private LayoutImageSelectorBinding bottomSheetBinding;
    private boolean isPermission;
    private boolean isDocumentChangable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bank_account_details);

        bankModel = getIntent().getExtras().getParcelable(Constants.BundleKey.BANK_MODEL);

        mBinding.setBank(bankModel);
        initUi();
    }

    @Override
    public String getActivityName() {
        return BankAccountDetailsActivity.class.getName();
    }

    private void initUi() {
        setUpHeader(mBinding.header.clHeader, getString(R.string.bank_acc_details), R.drawable.ic_back_half_arrow, 0);
        mBinding.header.tvHeaderCenter.setAllCaps(true);
        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(BankAccountDetailsActivity.this), R.layout.layout_image_selector, mBinding.bottomSheetMembers, false);

        bottomSheetBinding.tvTakePhoto.setOnClickListener(this);
        bottomSheetBinding.tvCameraRoll.setOnClickListener(this);
        bottomSheetBinding.tvCancel.setOnClickListener(this);
        mBinding.tvAttachment.setOnClickListener(this);
        mBinding.ivRemoveImage.setOnClickListener(this);
        mBinding.tvWhat.setOnClickListener(this);


        mBinding.tvNextBtn.setOnClickListener(view -> {

            if (isValid())
                postBankDetail();
        });


        mBinding.tvDateBirth.addTextChangedListener(commonTextWatcher);
        mBinding.tvAddress.addTextChangedListener(commonTextWatcher);
        mBinding.tvState.addTextChangedListener(commonTextWatcher);
        mBinding.tvCity.addTextChangedListener(commonTextWatcher);
        mBinding.tvSsn.addTextChangedListener(commonTextWatcher);
        mBinding.tvPostalCode.addTextChangedListener(commonTextWatcher);


        mBinding.tvDateBirth.setOnClickListener(view -> showDatePickerDialog());


        if (bankModel != null && bankModel.isFromServer()) {
            mBinding.tvDateBirth.setClickable(false);
            mBinding.tvDateBirth.setEnabled(false);
            mBinding.tvSsn.setEnabled(false);


            if (!StringUtils.isNullOrEmpty(bankModel.getDocumentUrl())) {

                if (bankModel.getVerificationStatus().equalsIgnoreCase("Verified")) {
                    mBinding.tvAttachment.setText(R.string.document_verified);
                    mBinding.tvAttachment.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    mBinding.tvWhat.setVisibility(View.GONE);
                    isDocumentChangable = false;

                } else {
                    mBinding.tvAttachment.setText(R.string.change_attachment);
                    mBinding.tvAttachment.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(BankAccountDetailsActivity.this, R.drawable.ic_bank_attachment), null);
                    mBinding.tvWhat.setVisibility(View.VISIBLE);
                    isDocumentChangable = true;

                }

            } else {
                SpannableString text = new SpannableString(getString(R.string.add_verification_documents));
                text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.unselected_grey)), 26, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mBinding.tvAttachment.setText(text);
                // mBinding.tvAttachment.setText(R.string.add_verification_documents);
                mBinding.tvAttachment.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(BankAccountDetailsActivity.this, R.drawable.ic_bank_attachment), null);
                mBinding.tvWhat.setVisibility(View.VISIBLE);

                isDocumentChangable = true;
            }


        }


    }


    private boolean isValid() {


        if (mBinding.tvDateBirth.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvDateBirth, "Please select the date of birth");
            return false;
        }
        if (mBinding.tvAddress.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvDateBirth, "Please enter the address");
            return false;
        }
        if (mBinding.tvCity.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvDateBirth, "Please enter the city");
            return false;
        }
        if (mBinding.tvState.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvDateBirth, "Please enter the state");
            return false;
        }
        if (mBinding.tvPostalCode.getText().toString().isEmpty()) {
            StringUtils.displaySnackBar(mBinding.tvDateBirth, "Please enter the postal code");
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

            if (mBinding.tvDateBirth.getText().toString().isEmpty()) {
                mBinding.tvDobHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.unselected_grey));
            } else {
                mBinding.tvDobHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.black));
            }

            if (mBinding.tvAddress.getText().toString().isEmpty()) {
                mBinding.tvAddressHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.unselected_grey));
            } else {
                bankModel.setAddress(mBinding.tvAddress.getText().toString());
                mBinding.tvAddressHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.black));
            }

            if (mBinding.tvCity.getText().toString().isEmpty()) {

                mBinding.tvCityHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.unselected_grey));
            } else {
                bankModel.setSuburb(mBinding.tvCity.getText().toString());
                mBinding.tvCityHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.black));
            }

            if (mBinding.tvState.getText().toString().isEmpty()) {

                mBinding.tvStateHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.unselected_grey));
            } else {
                bankModel.setState(mBinding.tvState.getText().toString());
                mBinding.tvStateHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.black));
            }


            if (mBinding.tvPostalCode.getText().toString().isEmpty()) {

                mBinding.tvPostalCodeHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.unselected_grey));
            } else {
                bankModel.setPostcode(mBinding.tvPostalCode.getText().toString());
                mBinding.tvPostalCodeHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.black));
            }


            if (mBinding.tvSsn.getText().toString().isEmpty()) {

                mBinding.tvSsnHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.unselected_grey));
            } else {
                mBinding.tvSsnHeader.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.black));
            }

            checkDoneBtn();
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    private void checkDoneBtn() {
        if (mBinding.tvAddress.getText().toString().isEmpty() ||
                mBinding.tvCity.getText().toString().isEmpty() ||
                mBinding.tvDateBirth.getText().toString().isEmpty() ||
                mBinding.tvPostalCode.getText().toString().isEmpty() ||
                mBinding.tvState.getText().toString().isEmpty()) {

            mBinding.tvNextBtn.setEnabled(false);
            mBinding.tvNextBtn.setBackgroundResource(R.drawable.grey_soild_roun_rect);
            mBinding.tvNextBtn.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.black));
        } else {

            mBinding.tvNextBtn.setEnabled(true);
            mBinding.tvNextBtn.setBackgroundResource(R.drawable.circle_yellow_button);
            mBinding.tvNextBtn.setTextColor(ContextCompat.getColor(BankAccountDetailsActivity.this, R.color.app_white));

        }
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -13);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(BankAccountDetailsActivity.this, this, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        month = month + 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        mBinding.tvDateBirth.setText(year + "/" + month + "/" + day);
        bankModel.setDob(Utils.getDOB(calendar.getTime()));

    }

    private void postBankDetail() {

        bankModel.setVerificationDocumentId(verificationId);

        mRepository = ((ApplicationController) getApplication()).provideRepository();


        Observable<BankPostResponse> observable = mRepository.getApiService().postBankDetail(bankModel);

        if (bankModel.isFromServer()) {


            updateBankModel = new BankModel();
            updateBankModel.setAccountNumber(bankModel.getAccountNumber());
            updateBankModel.setAddress(bankModel.getAddress());
            updateBankModel.setPostcode(bankModel.getPostcode());
            updateBankModel.setRoutingNumber(bankModel.getRoutingNumber());
            updateBankModel.setState(bankModel.getState());
            updateBankModel.setSuburb(bankModel.getSuburb());
            updateBankModel.setVerificationDocumentId(bankModel.getVerificationDocumentId());

            observable = mRepository.getApiService().updateBankDetail(updateBankModel);
        }


        new BaseCallback<BankPostResponse>(this, observable, true) {

            @Override
            public void onSuccess(BankPostResponse response) {
                hideProgressBar();


                if (response.getData().isStatus()) {

                    Alert.createAlert(BankAccountDetailsActivity.this, "", "Bank details added").setOnDismissListener(dialog -> {

                        if (getIntent().getExtras().getBoolean(Constants.BundleKey.FROM_SETTINGS)) {
                            ExplicitIntent.getsInstance().navigateTo(BankAccountDetailsActivity.this, SettingsActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        } else
                            ExplicitIntent.getsInstance().navigateTo(BankAccountDetailsActivity.this, NotesListingActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);


                        dialog.dismiss();


                    }).show();

                }


            }

            @Override
            public void onFail() {

                hideProgressBar();
            }
        };
    }


    private void uploadBankImage() {
        showProgressBar();

        if (imagePath != null && !imagePath.isEmpty()) {

            String base64 = Utils.getBase64(imagePath);
            VerificationImage verificationImage = new VerificationImage();
            verificationImage.setImage(base64);

            new BaseCallback<BankVerificationImageResponse>(this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService()
                    .addBankImage(verificationImage)) {
                @Override
                public void onSuccess(BankVerificationImageResponse baseResponse) {
                    hideProgressBar();
                    if (baseResponse.getData().getDocId() != null && !baseResponse.getData().getDocId().isEmpty()) {
                        verificationId = baseResponse.getData().getDocId();
                        setImageUrl();
                        checkDoneBtn();
                    }
                }

                @Override
                public void onFail() {
                    hideProgressBar();
                }
            };
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_attachment:

                if (isDocumentChangable) {
                    if (isPermission)
                        mBinding.bottomSheetMembers.showWithSheetView(bottomSheetBinding.getRoot());
                    else
                        checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);
                }

                break;
            case R.id.iv_remove_image:

                removeImage();
                break;

            case R.id.tv_camera_roll:
                mBinding.bottomSheetMembers.dismissSheet();
                ImagePickerUtils.add(false, getSupportFragmentManager(), BankAccountDetailsActivity.this);
                break;
            case R.id.tv_take_photo:
                mBinding.bottomSheetMembers.dismissSheet();
                ImagePickerUtils.add(true, getSupportFragmentManager(), BankAccountDetailsActivity.this);

                break;
            case R.id.tv_cancel:
                mBinding.bottomSheetMembers.dismissSheet();
                break;

            case R.id.tv_what:

                Alert.createAlert(BankAccountDetailsActivity.this, "", getString(R.string.bank_what_msg)).setOnDismissListener(dialog -> dialog.dismiss()).show();


                break;
        }
    }

    @Override
    public void success(String name, String path) {
        imagePath = path;
        uploadBankImage();

    }

    @Override
    public void fail(String message) {

    }

    private void setImageUrl() {

        ((View) mBinding.ivAttachment.getParent()).setVisibility(View.VISIBLE);
        mBinding.tvWhat.setVisibility(View.VISIBLE);
        try {
            mBinding.ivAttachment.setImageURI(Uri.parse(imagePath));


        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
    }

    private void removeImage() {
        imagePath = "";
        mBinding.ivAttachment.setImageBitmap(null);
        ((View) mBinding.ivAttachment.getParent()).setVisibility(View.GONE);
        mBinding.tvWhat.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPermissionGiven() {
        isPermission = true;
        mBinding.bottomSheetMembers.showWithSheetView(bottomSheetBinding.getRoot());

    }
}
