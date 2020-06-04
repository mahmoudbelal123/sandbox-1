package com.appster.turtle.ui.signUp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityProfileSetup1Binding;
import com.appster.turtle.model.GraduationYear;
import com.appster.turtle.model.MajorModel;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.UserProfileResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

import rx.Observable;
/*
* Activity for profile setup
 */
public class ProfileSetup1Activity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {
    private ActivityProfileSetup1Binding binding;
    private UserBaseModel userBaseModel;
    private ArrayList minors = new ArrayList<>(), majors = new ArrayList<>();
    private ArrayList graduationYear = new ArrayList();
    private GoogleApiClient mGoogleApiClient;
    private RetrofitRestRepository retrofitRestRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.
                setContentView(ProfileSetup1Activity.this, R.layout.activity_profile_setup1);


        retrofitRestRepository = ((ApplicationController) getApplicationContext()).provideRepository();


        userBaseModel = PreferenceUtil.getUser();
        binding.setUserModel(userBaseModel);

        setUpHeader(false, binding.header.clHeader, getString(R.string.your_prfl_text), getString(R.string.txt_signUp_nxt));
        binding.header.tvHeaderEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_bg_grey));
        binding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.black));
        binding.header.tvHeaderEnd.setOnClickListener(view -> {

            binding.header.tvHeaderEnd.setClickable(true);

            showProgressBar();
            updateProfile();


        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        mGoogleApiClient = new GoogleApiClient
                .Builder(ProfileSetup1Activity.this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(ProfileSetup1Activity.this, this)
                .build();
    }

    public void onTextClick(View view) {
        switch (view.getId()) {
            case R.id.etHomeTown:

                displayHometown();

                break;
            case R.id.etGraduation:
                displayYear();
                break;
            case R.id.etMajor:

                displayMajorMinor(true);
                break;
            case R.id.etMinor:
                displayMajorMinor(false);
                break;
            case R.id.tvSkip:
                ExplicitIntent.getsInstance().navigateTo(ProfileSetup1Activity.this, ProfileSetup2Activity.class);

                break;
            default:
                break;

        }
    }

    private void displayYear() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment profileFragment = YearSearchFragment.newInstance(graduationYear);
        fragmentTransaction
                .add(R.id.flContainer, profileFragment);
        fragmentTransaction.addToBackStack("YearSearchFragment");
        fragmentTransaction.commit();

    }


    private void displayMajorMinor(boolean isMajor) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment profileFragment = MajorSearchFragment.newInstance(isMajor, isMajor ? majors : minors);
        fragmentTransaction
                .add(R.id.flContainer, profileFragment);
        fragmentTransaction.addToBackStack("MajorSearchFragment");
        fragmentTransaction.commit();
    }

    private void displayHometown() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment hometownSearchFragment = new HometownSearchFragment();
        fragmentTransaction
                .add(R.id.flContainer, hometownSearchFragment);
        fragmentTransaction.addToBackStack("HometownSearchFragment");
        fragmentTransaction.commit();
    }

    public GoogleApiClient getmGoogleApiClient() {

        return mGoogleApiClient;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(ProfileSetup1Activity.this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public String getActivityName() {
        return ProfileSetup1Activity.class.getName();
    }

    public void setHomeTown(String autocompletePrediction) {

        if ((autocompletePrediction == null) && (StringUtils.isNullOrEmpty(userBaseModel.getHomeTown()))) {
            binding.etHomeTown.setText("");
            userBaseModel.setHomeTown("");
        } else if (autocompletePrediction != null) {

            binding.etHomeTown.setText(autocompletePrediction);
            userBaseModel.setHomeTown(autocompletePrediction);

        }

        validateData();

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else super.onBackPressed();
    }

    public void setMajor(ArrayList<MajorModel> majors) {
        StringBuilder majorStr = new StringBuilder();
        this.majors.clear();

        if (!majors.isEmpty()) {

            for (MajorModel major : majors) {

                majorStr.append(",").append(major.getValue());
                this.majors.add(major);


            }

            binding.etMajor.setText(majorStr.toString().replaceFirst(",", ""));
        } else {
            binding.etMajor.setText("");

        }

        validateData();


    }

    public void setMinor(ArrayList<MajorModel> minors) {

        StringBuilder minorStr = new StringBuilder();
        this.minors.clear();

        if (!minors.isEmpty()) {
            for (MajorModel minor : minors) {

                minorStr.append(",").append(minor.getValue());
                this.minors.add(minor);

            }
            binding.etMinor.setText(minorStr.toString().replaceFirst(",", ""));

        } else {
            binding.etMinor.setText("");


        }

        validateData();


    }

    public void setYear(ArrayList<GraduationYear> graduationYears) {


        graduationYear.clear();
        graduationYear.addAll(graduationYears);

        if (!graduationYears.isEmpty()) {

            if (graduationYears != null && !graduationYears.isEmpty()) {
                userBaseModel.setAcademicYearId(graduationYears.get(0).getId());
                binding.etGraduation.setText(graduationYears.get(0).getValue());

            }
        } else
            binding.etGraduation.setText("");


        validateData();

    }

    public void findPlaceById(String id) {

        if (TextUtils.isEmpty(id) || mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, id).setResultCallback(places -> {
            if (places.getStatus().isSuccess()) {
                Place place = places.get(0);

            }
            //Release the PlaceBuffer to prevent a memory leak
            places.release();
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void validateData() {
        if (!binding.etHomeTown.getText().toString().isEmpty()) {
            binding.tvHomeTown.setTextColor(ContextCompat.getColor(ProfileSetup1Activity.this, R.color.black));
        } else {
            binding.tvHomeTown.setTextColor(ContextCompat.getColor(ProfileSetup1Activity.this, R.color.text_font_color_grey));
        }

        if (!binding.etGraduation.getText().toString().isEmpty()) {
            binding.tvGraduation.setTextColor(ContextCompat.getColor(ProfileSetup1Activity.this, R.color.black));

        } else {
            binding.tvGraduation.setTextColor(ContextCompat.getColor(ProfileSetup1Activity.this, R.color.text_font_color_grey));

        }

        if (!binding.etMajor.getText().toString().isEmpty()) {
            binding.tvMajor.setTextColor(ContextCompat.getColor(ProfileSetup1Activity.this, R.color.black));

        } else {
            binding.tvMajor.setTextColor(ContextCompat.getColor(ProfileSetup1Activity.this, R.color.text_font_color_grey));

        }

        if (!binding.etMinor.getText().toString().isEmpty()) {
            binding.tvMinor.setTextColor(ContextCompat.getColor(ProfileSetup1Activity.this, R.color.black));

        } else {
            binding.tvMinor.setTextColor(ContextCompat.getColor(ProfileSetup1Activity.this, R.color.text_font_color_grey));

        }

        if (!binding.etHomeTown.getText().toString().isEmpty() &&
                (!binding.etMajor.getText().toString().isEmpty()) &&
                (!binding.etMinor.getText().toString().isEmpty())) {

            binding.header.tvHeaderEnd.setEnabled(true);
            binding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.app_white));
        } else {
            binding.header.tvHeaderEnd.setEnabled(false);
            binding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.black));
        }


    }

    private ArrayList<Integer> getDisciplineIds(ArrayList<MajorModel> arrayList) {
        ArrayList<Integer> integers = new ArrayList<>();
        for (MajorModel major : arrayList) {
            integers.add(major.getId());
        }

        return integers;
    }

    public void updateProfile() {

        userBaseModel.setMajor(getDisciplineIds(majors));
        userBaseModel.setMinor(getDisciplineIds(minors));


        Observable<UserProfileResponse> userProfileResponseObservable = retrofitRestRepository.getApiService().updateUser(userBaseModel);
        new BaseCallback<UserProfileResponse>(this, userProfileResponseObservable) {

            @Override
            public void onSuccess(UserProfileResponse response) {


                UserBaseModel user = response.getData();

                user.setMajor(getDisciplineIds(majors));
                user.setMinor(getDisciplineIds(minors));
                user.setAcademicYearId(userBaseModel.getAcademicYearId());
                user.setHomeTown(userBaseModel.getHomeTown());

                PreferenceUtil.setUser(user);
                hideProgressBar();

                ExplicitIntent.getsInstance().navigateTo(ProfileSetup1Activity.this, ProfileSetup2Activity.class);
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };


    }
}
