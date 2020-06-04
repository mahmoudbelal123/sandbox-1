/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.signUp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.InterestAdapter;
import com.appster.turtle.adapter.TagAdapter;
import com.appster.turtle.databinding.ActivityAddInterestsBinding;
import com.appster.turtle.model.Interest;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.AddInterestResponse;
import com.appster.turtle.network.response.InterestResponse;
import com.appster.turtle.network.response.UserProfileResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

import rx.Observable;
/*
* Activity for add interest
 */
public class AddInterestActivity extends BaseActivity {


    private ActivityAddInterestsBinding mBinding;
    private RetrofitRestRepository retrofitRestRepository;
    private Observable<InterestResponse> getInterests;
    private BaseCallback<InterestResponse> interestResponseBaseCallback;
    private InterestAdapter interestAdapter;
    private ArrayList<Interest> selected = new ArrayList<>();
    private ArrayList<Interest> selectedTop = new ArrayList<>();
    private ArrayList<Interest> popularInterests = new ArrayList<>();
    private TagAdapter popularAdapter, selectedAdapter;

    @Override
    public String getActivityName() {
        return AddInterestActivity.class.getName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrofitRestRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        if (getIntent().hasExtra(Constants.SELECTED_IDS))
            selectedTop = getIntent().getParcelableArrayListExtra(Constants.SELECTED_IDS);
        initDataBinding();


    }

    private void initDataBinding() {

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_interests);
        setUpHeader(false, mBinding.header.clHeader, getString(R.string.Interests), getIntent().hasExtra(Constants.SELECTED_IDS) ? getString(R.string.done) : getString(R.string.txt_signUp_nxt));


        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(AddInterestActivity.this);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mBinding.interests.setLayoutManager(layoutManager);
        mBinding.interests.setNestedScrollingEnabled(false);
        popularAdapter = new TagAdapter(popularInterests, interest -> {

            popularInterests.get(interest).setSelected(!popularInterests.get(interest).isSelected());
            popularAdapter.notifyItemChanged(interest);


            if (!getIntent().hasExtra(Constants.SELECTED_IDS)) {
                if (!getIds().isEmpty()) {
                    mBinding.header.tvHeaderEnd.setEnabled(true);
                    mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(AddInterestActivity.this, R.color.app_white));
                } else {
                    mBinding.header.tvHeaderEnd.setEnabled(false);
                    mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(AddInterestActivity.this, R.color.black));
                }
            }

        });
        mBinding.interests.setAdapter(popularAdapter);


        FlexboxLayoutManager layoutManagerSelected = new FlexboxLayoutManager(AddInterestActivity.this);
        layoutManagerSelected.setFlexDirection(FlexDirection.ROW);
        layoutManagerSelected.setFlexWrap(FlexWrap.WRAP);
        layoutManagerSelected.setJustifyContent(JustifyContent.FLEX_START);
        mBinding.selectedInterests.setLayoutManager(layoutManagerSelected);
        mBinding.selectedInterests.setNestedScrollingEnabled(false);
        selectedAdapter = new TagAdapter(selected, interest -> {

            if (selected.size() > interest) {
                selected.remove(interest);
//                    selectedAdapter.notifyItemRemoved(interest);
                selectedAdapter.notifyDataSetChanged();
            }
        }, true);
        mBinding.selectedInterests.setAdapter(selectedAdapter);


        mBinding.header.tvHeaderEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_bg_grey));
        mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.black));

        if (getIntent().hasExtra(Constants.SELECTED_IDS)) {
            mBinding.header.tvHeaderEnd.setEnabled(true);
            mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.app_white));
            mBinding.llSkip.setVisibility(View.GONE);

        }

        interestAdapter = new InterestAdapter(this, R.layout.item_interest_dropdown);
        mBinding.et.setAdapter(interestAdapter);
        mBinding.et.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mBinding.view.setBackgroundColor(ContextCompat.getColor(AddInterestActivity.this, R.color.bright_teal));
            } else {
                mBinding.view.setBackgroundColor(ContextCompat.getColor(AddInterestActivity.this, R.color.text_font_color_grey));

            }
        });

        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = mBinding.getRoot().getRootView().getHeight() - mBinding.getRoot().getHeight();

            if (heightDiff > mBinding.getRoot().getHeight() / 4) {
                mBinding.tvBottom.setVisibility(View.GONE);
            } else {
                mBinding.tvBottom.setVisibility(View.VISIBLE);
            }
        });


        mBinding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = mBinding.et.getText().toString();
                if (query.isEmpty()) {
                    mBinding.tv.setTextColor(ContextCompat.getColor(AddInterestActivity.this, R.color.text_font_color_grey));
                    mBinding.et.setTextColor(ContextCompat.getColor(AddInterestActivity.this, R.color.text_font_color_grey));
                    mBinding.et.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_orange_search, 0, 0, 0);
                    mBinding.btnAdd.setImageBitmap(null);
                    mBinding.btnAdd.setEnabled(false);
                } else {

                    mBinding.tv.setTextColor(ContextCompat.getColor(AddInterestActivity.this, R.color.black));
                    mBinding.et.setTextColor(ContextCompat.getColor(AddInterestActivity.this, R.color.black));
                    mBinding.et.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_orange_search, 0, 0, 0);
                    mBinding.btnAdd.setImageResource(R.drawable.ic_add);
                    mBinding.btnAdd.setEnabled(true);
                }


                getInterests = retrofitRestRepository.getApiService().searchInterests(query, 1, 10);

                if (interestResponseBaseCallback != null) {
                    interestResponseBaseCallback.cancel();
                }

                interestResponseBaseCallback = new BaseCallback<InterestResponse>(AddInterestActivity.this, getInterests) {
                    @Override
                    public void onSuccess(InterestResponse response) {
                        interestAdapter.addAll(response.getData());
                        interestAdapter.getFilter().filter("");
                    }

                    @Override
                    public void onFail() {

                    }
                };
            }
        });


        mBinding.et.setOnItemClickListener((adapterView, view, i, l) -> {

            try {
                if (interestAdapter.getItem(i) != null) {

                    if (popularInterests.contains(interestAdapter.getItem(i))) {
                        mBinding.et.setText("");
                        int index = popularInterests.indexOf(interestAdapter.getItem(i));
                        popularInterests.get(index).setSelected(true);
                        popularAdapter.notifyItemChanged(index);


                    } else {
                        mBinding.et.setText("");

                        if (selected.contains(interestAdapter.getItem(i))) {
                            return;

                        }
                        selected.add(interestAdapter.getItem(i));
                        selected.get(selected.size() - 1).setSelected(true);
                        selectedAdapter.notifyItemInserted(selected.size() - 1);
                        mBinding.header.tvHeaderEnd.setEnabled(true);
                        mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(AddInterestActivity.this, R.color.app_white));

                    }
                }
            } catch (Exception e) {
                LogUtils.printStackTrace(e);
            }
            hideKeyboard();
        });

        mBinding.btnAdd.setOnClickListener(view -> addInterest());

        mBinding.llSkip.setOnClickListener(view -> ExplicitIntent.getsInstance().navigateTo(AddInterestActivity.this, AddSkillsActivity.class));


        new BaseCallback<InterestResponse>(this, retrofitRestRepository.getApiService().getPopularInterest()) {

            @Override
            public void onSuccess(InterestResponse response) {

                popularInterests.addAll(response.getData());

                if (selectedTop != null && !selectedTop.isEmpty()) {
                    selected.addAll(selectedTop);

                    for (Interest interest : selectedTop) {
                        if (popularInterests.contains(interest)) {
                            popularInterests.get(popularInterests.indexOf(interest)).setSelected(true);
                            selected.remove(interest);
                        }
                    }
                    for (int i = 0; i < selected.size(); i++) {
                        selected.get(i).setSelected(true);
                    }
                    mBinding.header.tvHeaderEnd.setEnabled(true);
                    mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(AddInterestActivity.this, R.color.app_white));
                }

                popularAdapter.notifyDataSetChanged();
                selectedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail() {


            }
        };


        mBinding.header.tvHeaderEnd.setOnClickListener(view -> {
            if (getIntent().hasExtra(Constants.SELECTED_IDS))
                returnBack();
            else
                saveToUserProfile();
        });

    }

    private void returnBack() {


        Intent i = new Intent();
        getSelectedInterest();
        i.putParcelableArrayListExtra(Constants.SELECTED_IDS, selectedTop);
        setResult(RESULT_OK, i);
        finish();
    }


    private void saveToUserProfile() {
        hideKeyboard();
        showProgressBar();
        final UserBaseModel userBaseModel = PreferenceUtil.getUser();
        userBaseModel.setInterestIds(getIds());

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

                //request has "interestIds" but response gives "interests"
                //so on receiving userModel "interestIds" are null but "interests" has data
                //hence when we add skills on next screen, we were sending null "interestIds".
                //Therefore, fetch all interestIds from interests and set that in shared prefs user model.
                ArrayList<Integer> interestIds = new ArrayList<>();
                for (Interest interest : user.getInterests()) {
                    interestIds.add(interest.getId());
                }
                user.setInterestIds(interestIds);
                PreferenceUtil.setUser(user);
                ExplicitIntent.getsInstance().navigateTo(AddInterestActivity.this, AddSkillsActivity.class);
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };

    }

    private void addInterest() {
        hideKeyboard();

        final String query = mBinding.et.getText().toString();

        showProgressBar();
        Interest interest = new Interest();
        interest.setValue(query);
        Observable<AddInterestResponse> addInterest = retrofitRestRepository.getApiService().addInterest(interest);
        new BaseCallback<AddInterestResponse>(AddInterestActivity.this, addInterest) {
            @Override
            public void onSuccess(AddInterestResponse response) {
                hideProgressBar();
                try {
                    mBinding.et.setText("");

                    if (response.getData() != null) {
                        if (!selected.contains(response.getData())) {
                            response.getData().setSelected(true);
                            selected.add(response.getData());
                        }


                        mBinding.header.tvHeaderEnd.setEnabled(true);
                        mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(AddInterestActivity.this, R.color.app_white));

                        selectedAdapter.notifyItemInserted(selected.size() - 1);
                    }else if (response.getMeta().getCode() == 215){
                        showSnackBar(response.getMeta().getMessage());
                    }


                } catch (Exception e) {
                    LogUtils.printStackTrace(e);
                }
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };


    }


    private ArrayList<Integer> getIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (Interest interest : selected) {
            ids.add(interest.getId());

        }
        for (Interest interest : popularInterests) {
            if (interest.isSelected())
                ids.add(interest.getId());
        }


        return ids;
    }

    private void getSelectedInterest() {
        selectedTop.clear();
        selectedTop.addAll(selected);
        for (Interest interest : popularInterests) {
            if (interest.isSelected())
                selectedTop.add(interest);
        }


    }
}
