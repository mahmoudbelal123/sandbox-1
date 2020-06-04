package com.appster.turtle.ui.signUp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityProfileSetup2Binding;
import com.appster.turtle.model.Dorm;
import com.appster.turtle.model.GreekLife;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.UserProfileResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;

import java.util.ArrayList;

import rx.Observable;
/*
* Activity for profile setup
 */
public class ProfileSetup2Activity extends BaseActivity {
    private ActivityProfileSetup2Binding binding;
    private UserBaseModel userBaseModel;
    private ArrayList sororities = new ArrayList<>();
    private ArrayList fraternities = new ArrayList<>();
    private ArrayList dormList = new ArrayList<>();
    private String profileVisibility = "GLOBAL";

    private int dorm;
    private RetrofitRestRepository retrofitRestRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_setup2);

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

    public void onTextClick(View view) {
        switch (view.getId()) {
            case R.id.etGreekLife:
                displayGreekLife();
                break;
            case R.id.etDorm:
                displayOverlay(true);
                break;

            case R.id.skip:
                ExplicitIntent.getsInstance().navigateTo(ProfileSetup2Activity.this, AddInterestActivity.class);


                break;
            case R.id.cl_global_campus:
                displayProfileVisibilty();
                break;

            default:
                break;


        }
    }

    private ArrayList getGreekLifes() {
        ArrayList greekLife = new ArrayList();
        greekLife.addAll(sororities);
        greekLife.addAll(fraternities);
        return greekLife;
    }

    private void displayOverlay(boolean isDorm) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment hometownSearchFragment = DormFragment.newInstance(isDorm, isDorm ? dormList : getGreekLifes());
        fragmentTransaction
                .replace(R.id.flContainer, hometownSearchFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void displayGreekLife() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment hometownSearchFragment = GreekLifeSelectorFragment.newInstance(fraternities, sororities);
        fragmentTransaction
                .replace(R.id.flContainer, hometownSearchFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void displayProfileVisibilty() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();

        ArrayList<String> strings = new ArrayList<>();
        strings.clear();
        strings.add(getLowerCase(profileVisibility));
        Fragment hometownSearchFragment = ProfileVisibilitySelectorFragment.newInstance(strings);
        fragmentTransaction
                .replace(R.id.flContainer, hometownSearchFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private String getLowerCase(String str) {
        if (str.isEmpty())
            return "";

        else {

            str = str.toLowerCase();
            return Character.toString(str.charAt(0)).toUpperCase() + str.substring(1);
        }
    }


    @Override
    public String getActivityName() {
        return ProfileSetup2Activity.class.getName();
    }


    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
        binding.tvGlobalCampusDesc.setText(profileVisibility);
    }

    public void setGreekLife(ArrayList<GreekLife> fraternities, ArrayList<GreekLife> sororities) {

        StringBuilder greekStr = new StringBuilder();
        this.sororities.clear();
        this.fraternities.clear();


        if (!fraternities.isEmpty()) {

            for (GreekLife greekLife : fraternities) {

                greekStr.append(",").append(greekLife.getValue());

                if (greekLife.getType().equalsIgnoreCase(Constants.FRATERNITIES))
                    this.fraternities.add(greekLife);
                else
                    this.sororities.add(greekLife);

            }

        }

        if (!sororities.isEmpty()) {

            for (GreekLife greekLife : sororities) {

                greekStr.append(",").append(greekLife.getValue());

                if (greekLife.getType().equalsIgnoreCase(Constants.FRATERNITIES))
                    this.fraternities.add(greekLife);
                else
                    this.sororities.add(greekLife);

            }

        }

        binding.etGreekLife.setText(greekStr.toString().replaceFirst(",", ""));

        if (sororities.isEmpty() && fraternities.isEmpty())
            binding.etGreekLife.setText("");


        validateData();


    }


    public void setDorm(ArrayList<Dorm> dorms) {
        StringBuilder dormStr = new StringBuilder();

        dormList.clear();
        dormList.addAll(dorms);

        if (!dorms.isEmpty()) {
            for (Dorm dorm : dorms) {

                dormStr.append(",").append(dorm.getTitle());
                this.dorm = dorm.getDormId();


            }
            binding.etDorm.setText(dormStr.toString().replaceFirst(",", ""));
        } else
            binding.etDorm.setText("");


        validateData();

    }

    private void validateData() {
        if (!binding.etGreekLife.getText().toString().isEmpty()) {
            binding.tvGreekLife.setTextColor(ContextCompat.getColor(ProfileSetup2Activity.this, R.color.black));
        } else {
            binding.tvGreekLife.setTextColor(ContextCompat.getColor(ProfileSetup2Activity.this, R.color.unselected_grey));
        }

        if (!binding.etDorm.getText().toString().isEmpty()) {
            binding.tvDorm.setTextColor(ContextCompat.getColor(ProfileSetup2Activity.this, R.color.black));
        } else {
            binding.tvDorm.setTextColor(ContextCompat.getColor(ProfileSetup2Activity.this, R.color.unselected_grey));
        }

        if (!binding.tvGlobalCampusDesc.getText().toString().isEmpty()) {
            binding.tvGlobalCampusTitle.setTextColor(ContextCompat.getColor(ProfileSetup2Activity.this, R.color.black));
        } else {
            binding.tvGlobalCampusTitle.setTextColor(ContextCompat.getColor(ProfileSetup2Activity.this, R.color.unselected_grey));
        }


        if (!binding.etDorm.getText().toString().isEmpty() &&
                (!binding.etGreekLife.getText().toString().isEmpty())
                ) {
            binding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.app_white));
            binding.header.tvHeaderEnd.setEnabled(true);
        } else {
            binding.header.tvHeaderEnd.setEnabled(false);
            binding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.black));
        }

    }


    public void updateProfile() {

        userBaseModel.setDormatoryId(dorm);
        userBaseModel.setPublicProfile(profileVisibility.equalsIgnoreCase("GLOBAL"));
        userBaseModel.setFraternities(getIds(fraternities));
        userBaseModel.setSororities(getIds(sororities));


        Observable<UserProfileResponse> userProfileResponseObservable = retrofitRestRepository.getApiService().updateUser(userBaseModel);
        new BaseCallback<UserProfileResponse>(this, userProfileResponseObservable) {

            @Override
            public void onSuccess(UserProfileResponse response) {
                hideProgressBar();

                UserBaseModel user = response.getData();
                user.setDormatoryId(dorm);
                user.setPublicProfile(profileVisibility.equalsIgnoreCase("GLOBAL"));
                user.setFraternities(getIds(fraternities));
                user.setSororities(getIds(sororities));
                user.setMajor(userBaseModel.getMajor());
                user.setMinor(userBaseModel.getMinor());
                user.setAcademicYearId(userBaseModel.getAcademicYearId());
                user.setHomeTown(userBaseModel.getHomeTown());

                PreferenceUtil.setUser(user);



                ExplicitIntent.getsInstance().navigateTo(ProfileSetup2Activity.this, AddInterestActivity.class);
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };


    }

    private ArrayList<Integer> getIds(ArrayList<GreekLife> arrayList) {
        ArrayList<Integer> integers = new ArrayList<>();
        for (GreekLife major : arrayList) {
            integers.add(major.getId());
        }

        return integers;
    }
}
