/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.signUp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityEnterBioBinding;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.UserProfileResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.viewmodel.UserBioViewModel;

import rx.Observable;

/**
 * Created by atul on 15/09/17.
 * To inject activity reference.
 * Activity for bio
 */

public class BioActivity extends BaseActivity {

    private ActivityEnterBioBinding dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setUpViews();
    }

    private void initDataBinding() {
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_enter_bio);
        UserBioViewModel bioViewModel = new UserBioViewModel(this, dataBinding);
        dataBinding.setViewModel(bioViewModel);
        dataBinding.title.setText(getString(R.string.finally_tell_us_a_little_about_yourself) + " " + PreferenceUtil.getUser().getfName() + ".");
        dataBinding.tvRemainingCharacters.setText(getString(R.string.no_of_characters_remaining, "" + (260 - dataBinding.etBio.getText().toString().length())));

    }

    private void setUpViews() {
        setUpHeader(dataBinding.header.clHeader, getString(R.string.your_prfl_text), getString(R.string.txt_signUp_nxt));
        dataBinding.header.tvHeaderEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_bg_grey));
        dataBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.black));
        dataBinding.header.ivIconStart.setImageResource(R.drawable.ic_back_black);

        dataBinding.header.tvHeaderEnd.setOnClickListener(view -> saveToUserProfile());

        dataBinding.llSkip.setOnClickListener(view -> {
            ExplicitIntent.getsInstance().navigateTo(BioActivity.this, WelcomeUserActivity.class);

            finish();
        });

        dataBinding.etBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dataBinding.tvRemainingCharacters.setText(getString(R.string.no_of_characters_remaining, "" + (260 - dataBinding.etBio.getText().toString().length())));
                if (dataBinding.etBio.getText().toString().isEmpty()) {
                    dataBinding.header.tvHeaderEnd.setEnabled(false);
                    dataBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(BioActivity.this, R.color.black));

                    dataBinding.vTextView.setTextColor(ContextCompat.getColor(BioActivity.this, R.color.unselected_grey));
                    dataBinding.etBioView.setBackgroundResource(R.color.unselected_grey);

                } else {
                    dataBinding.header.tvHeaderEnd.setEnabled(true);
                    dataBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(BioActivity.this, R.color.app_white));

                    dataBinding.vTextView.setTextColor(ContextCompat.getColor(BioActivity.this, R.color.black));
                    dataBinding.etBioView.setBackgroundResource(R.color.black);


                }
            }
        });


    }


    private void saveToUserProfile() {
        hideKeyboard();
        showProgressBar();
        final UserBaseModel userBaseModel = PreferenceUtil.getUser();
        String bioStr = dataBinding.etBio.getText().toString();
        userBaseModel.setBio(bioStr);
        RetrofitRestRepository retrofitRestRepository = ((ApplicationController) getApplication()).provideRepository();
        Observable<UserProfileResponse> userProfileResponseObservable = retrofitRestRepository.getApiService().updateUser(userBaseModel);
        new BaseCallback<UserProfileResponse>(this, userProfileResponseObservable) {

            @Override
            public void onSuccess(UserProfileResponse response) {
                hideProgressBar();


                UserBaseModel user = response.getData();
                user.setMajor(userBaseModel.getMajor());
                user.setMinor(userBaseModel.getMinor());
                user.setAcademicYearId(userBaseModel.getAcademicYearId());
                user.setHomeTown(userBaseModel.getHomeTown());
                user.setDormatoryId(userBaseModel.getDormatoryId());
                user.setPublicProfile(userBaseModel.isPublicProfile());
                user.setFraternities(userBaseModel.getFraternities());
                user.setSororities(userBaseModel.getSororities());
                user.setSkillIds(userBaseModel.getSkillIds());
                user.setInterestIds(userBaseModel.getInterestIds());

                PreferenceUtil.setUser(user);

                ExplicitIntent.getsInstance().navigateTo(BioActivity.this, WelcomeUserActivity.class);

                finish();
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };

    }

    @Override
    public String getActivityName() {
        return null;
    }

}
