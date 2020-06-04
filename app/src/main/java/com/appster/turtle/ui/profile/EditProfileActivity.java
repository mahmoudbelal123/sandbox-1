package com.appster.turtle.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityEditProfileBinding;
import com.appster.turtle.databinding.LayoutGallerySelectorBinding;
import com.appster.turtle.model.Dorm;
import com.appster.turtle.model.Event;
import com.appster.turtle.model.GraduationYear;
import com.appster.turtle.model.GreekLife;
import com.appster.turtle.model.Interest;
import com.appster.turtle.model.MajorModel;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.model.UserProfileData;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.IdResponse;
import com.appster.turtle.network.response.UserProfileResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.media.CropperActivity;
import com.appster.turtle.ui.media.MediaBottomFragment;
import com.appster.turtle.ui.signUp.AddInterestActivity;
import com.appster.turtle.ui.signUp.AddSkillsActivity;
import com.appster.turtle.ui.signUp.ChooseAvatarActivity;
import com.appster.turtle.ui.signUp.DormFragment;
import com.appster.turtle.ui.signUp.GreekLifeSelectorFragment;
import com.appster.turtle.ui.signUp.HometownSearchFragment;
import com.appster.turtle.ui.signUp.MajorSearchFragment;
import com.appster.turtle.ui.signUp.ProfileVisibilitySelectorFragment;
import com.appster.turtle.ui.signUp.YearSearchFragment;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.ImagePickerUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.bindingadapters.CameraBindingAdapters;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import rx.Observable;
/*
* Activity for edit profile
 */
public class EditProfileActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, ImagePickerUtils.OnImagePickerListener, BaseActivity.PermissionI {

    private ActivityEditProfileBinding binding;
    private UserProfileData userProfileData;
    private GoogleApiClient mGoogleApiClient;
    private RetrofitRestRepository retrofitRestRepository;

    private ArrayList<GraduationYear> graduationYears = new ArrayList<>();
    private ArrayList<Dorm> dorms = new ArrayList<>();
    private ArrayList sororities = new ArrayList<>();
    private ArrayList fraternities = new ArrayList<>();
    private ArrayList minors = new ArrayList<>(), majors = new ArrayList<>();
    private boolean onPermissionGranted;
    private LayoutGallerySelectorBinding bottomSheetBinding;
    private String imagePath;
    private ArrayList<Interest> selectedSkills = new ArrayList<>();
    private ArrayList<Interest> selectedInterests = new ArrayList<>();
    private BaseCallback<IdResponse> baseCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(EditProfileActivity.this, R.layout.activity_edit_profile);
        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(EditProfileActivity.this), R.layout.layout_gallery_selector, binding.bottomSheet, false);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setUpHeader(false, binding.clHeader.clHeader, getString(R.string.edit_profile), getString(R.string.save), R.drawable.back_arrow);
        userProfileData = getIntent().getParcelableExtra(Constants.USER);

        binding.clHeader.tvHeaderEnd.setEnabled(true);

        binding.setUser(userProfileData);


        setData();

        setImage();


        binding.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().startsWith("@")) {
                    binding.etUsername.setText("@");
                    Selection.setSelection(binding.etUsername.getText(), binding.etUsername.getText().length());
                }

                isIdExist();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpApi();
    }

    private void setImage() {
        if (userProfileData.getUserModel().getProfileUrlType() == Constants.IMAGE_INITIALS_ID) {
            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(EditProfileActivity.this, R.color.bright_teal));
            binding.ivRoomImage.setImageResource(R.drawable.bg_orange);


            binding.ivRoomImageInitials.setVisibility(View.VISIBLE);
            CameraBindingAdapters.bindImageUrl(binding.ivRoomImageInitials, userProfileData.getUserModel().getProfileUrl());
        } else {
            CameraBindingAdapters.bindImageUrl(binding.ivRoomImage, userProfileData.getUserModel().getProfileUrl());
            binding.ivRoomImageInitials.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onDestroy() {

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    private void setUpApi() {

        mGoogleApiClient = new GoogleApiClient
                .Builder(EditProfileActivity.this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(EditProfileActivity.this, this)
                .build();
        retrofitRestRepository = ((ApplicationController) getApplicationContext()).provideRepository();
    }

    @Override
    public String getActivityName() {
        return EditProfileActivity.class.getName();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void showImageSelector(View view) {


        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.CHOOSE_AVATAR_USER, userProfileData);
        ExplicitIntent.getsInstance().navigateForResult(EditProfileActivity.this, ChooseAvatarActivity.class, Constants.REQUEST_CODE.REQUEST_CHOOSE_AVATAR, bundle);


    }


    private void toggleMediaBottom(boolean isVisible) {

        if (onPermissionGranted) {
            if (getFragment(MediaBottomFragment.class.getName()) == null)
                pushFragment(Constants.FRAGMENTS.MEDIA_BOTTOM_FRAGMENT, null, bottomSheetBinding.flMediaBottomSheet.getId(), false, ANIMATION_TYPE.NONE);


            binding.bottomSheet.addOnSheetStateChangeListener(state -> {

                if (state.equals(BottomSheetLayout.State.PREPARING)) {
                    if (getFragment(MediaBottomFragment.class.getName()) != null)
                        ((MediaBottomFragment) getFragment(MediaBottomFragment.class.getName())).start();
                }


            });

            if (!isVisible) {
                binding.bottomSheet.dismissSheet();

            } else {
                binding.bottomSheet.showWithSheetView(bottomSheetBinding.getRoot());
            }


        } else if (isVisible)
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);


    }


    public void onEditTextClick(View view) {

        switch (view.getId()) {
            case R.id.et_hometown:
                displayHometown();
                break;

            case R.id.et_academic_year:
                displayYear();
                break;

            case R.id.et_profile_visibility:
                displayProfileVisibilty();
                break;

            case R.id.et_dorm:
                displayDorms();
                break;

            case R.id.et_greek_life:
                displayGreekLife();
                break;

            case R.id.et_major:
                displayMajorMinor(true);
                break;

            case R.id.et_minor:
                displayMajorMinor(false);
                break;

            case R.id.et_skill:
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(Constants.SELECTED_IDS, selectedSkills);
                ExplicitIntent.getsInstance().navigateForResult(EditProfileActivity.this, AddSkillsActivity.class, Constants.REQUEST_CODE.REQUEST_ADD_SKILLS, bundle);

                break;

            case R.id.et_interest:

                bundle = new Bundle();
                bundle.putParcelableArrayList(Constants.SELECTED_IDS, selectedInterests);
                ExplicitIntent.getsInstance().navigateForResult(EditProfileActivity.this, AddInterestActivity.class, Constants.REQUEST_CODE.REQUEST_ADD_INTEREST, bundle);


                break;

            default:
                break;


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case Constants.REQUEST_CODE.REQUEST_CHOOSE_AVATAR:
                if (resultCode == RESULT_OK) {

                    userProfileData = data.getParcelableExtra(Constants.CHOOSE_AVATAR_USER);
                    setImage();

                }
                break;
            case Constants.REQUEST_CODE.REQUEST_ADD_INTEREST:
                if (resultCode == RESULT_OK) {


                    selectedInterests = data.getParcelableArrayListExtra(Constants.SELECTED_IDS);
                    userProfileData.getUserModel().setInterestIds(getInterestIds(selectedInterests));


                    StringBuilder stringBuilder = new StringBuilder();
                    for (Interest str : selectedInterests) {
                        stringBuilder.append(",").append(str.getValue());

                    }

                    binding.etInterest.setText(stringBuilder.toString().replaceFirst(",", ""));


                    if (selectedInterests.isEmpty())
                        binding.etInterest.setText("");
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_ADD_SKILLS:
                if (resultCode == RESULT_OK) {


                    selectedSkills = data.getParcelableArrayListExtra(Constants.SELECTED_IDS);
                    userProfileData.getUserModel().setSkillIds(getInterestIds(selectedSkills));


                    StringBuilder stringBuilder = new StringBuilder();
                    for (Interest str : selectedSkills) {
                        stringBuilder.append(",").append(str.getValue());

                    }

                    binding.etSkill.setText(stringBuilder.toString().replaceFirst(",", ""));


                    if (selectedSkills.isEmpty())
                        binding.etSkill.setText("");


                }
                break;

            case Constants.REQUEST_CODE.REQUEST_CROPPER:
                if (resultCode == RESULT_OK) {
                    Bundle request = data.getExtras();
                    imagePath = request.getString(Constants.BundleKey.IMAGE_URL);

                    if (getFragment(MediaBottomFragment.class.getName()) != null)
                        ((MediaBottomFragment) getFragment(MediaBottomFragment.class.getName())).setCameraStop(false);

                    if (imagePath != null && !imagePath.isEmpty()) {

                        GlideApp.with(EditProfileActivity.this)
                                .load(Uri.parse(imagePath))
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .dontAnimate()
                                .placeholder(R.drawable.ic_pp_upload)
                                .into(binding.ivRoomImage);
                    }
                    toggleMediaBottom(false);

                } else {
                    toggleMediaBottom(false);

                }
                break;


            default:
                super.onActivityResult(requestCode, resultCode, data);
                toggleMediaBottom(false);
                break;
        }
    }

    public void nextClicked(View view) {

        updateProfile();
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
        mGoogleApiClient.stopAutoManage(EditProfileActivity.this);
        mGoogleApiClient.disconnect();
    }

    public void setHomeTown(String autocompletePrediction) {

        if ((autocompletePrediction == null) && (StringUtils.isNullOrEmpty(userProfileData.getUserModel().getHomeTown()))) {
            binding.etHometown.setText("");
            userProfileData.getUserModel().setHomeTown("");
        } else if (autocompletePrediction != null) {

            binding.etHometown.setText(autocompletePrediction);
            userProfileData.getUserModel().setHomeTown(autocompletePrediction);
        }

    }

    private void setData() {

        userProfileData.getUserModel().setAcademicYearId(
                userProfileData.getUserModel().getAcademicYear().getId());

        userProfileData.getUserModel().setDormatoryId(
                userProfileData.getUserModel().getDormitory().getDormId());


        sororities.clear();
        sororities.addAll(userProfileData.getUserModel().getGreekLife(false));

        fraternities.clear();
        fraternities.addAll(userProfileData.getUserModel().getGreekLife(true));

        majors.clear();
        majors.addAll(userProfileData.getUserModel().getMajorMinor(true));

        minors.clear();
        minors.addAll(userProfileData.getUserModel().getMajorMinor(false));

        userProfileData.getUserModel().setInterestIds(getInterestIds(userProfileData.getUserModel().getInterests()));
        userProfileData.getUserModel().setSkillIds(getInterestIds(userProfileData.getUserModel().getSkills()));

        selectedSkills.addAll(userProfileData.getUserModel().getSkills());
        selectedInterests.addAll(userProfileData.getUserModel().getInterests());

        binding.etFName.setText(userProfileData.getUserModel().getfName());
        binding.etLName.setText(userProfileData.getUserModel().getlName());


        binding.etLName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.etFName.getText().toString().isEmpty() || binding.etLName.getText().toString().isEmpty()) {
                    binding.clHeader.tvHeaderEnd.setEnabled(false);
                    binding.clHeader.tvHeaderEnd.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.black));
                } else {
                    binding.clHeader.tvHeaderEnd.setEnabled(true);
                    binding.clHeader.tvHeaderEnd.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.app_white));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }
        });

        binding.etFName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.etLName.getText().toString().isEmpty() || binding.etFName.getText().toString().isEmpty()) {
                    binding.clHeader.tvHeaderEnd.setEnabled(false);
                    binding.clHeader.tvHeaderEnd.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.black));
                } else {
                    binding.clHeader.tvHeaderEnd.setEnabled(true);
                    binding.clHeader.tvHeaderEnd.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.app_white));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }
        });

    }


    private void displayYear() {
        graduationYears.clear();
        graduationYears.add(userProfileData.getUserModel().getAcademicYear());


        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment profileFragment = YearSearchFragment.newInstance(graduationYears);
        fragmentTransaction
                .add(R.id.flContainer, profileFragment);
        fragmentTransaction.addToBackStack("YearSearchFragment");
        fragmentTransaction.commit();

    }

    public void setYear(ArrayList<GraduationYear> graduationYear) {

        this.graduationYears.clear();
        this.graduationYears.addAll(graduationYear);

        if (!graduationYears.isEmpty()) {

            if (graduationYears != null && !graduationYears.isEmpty()) {
                userProfileData.getUserModel().setAcademicYear(graduationYears.get(0));
                userProfileData.getUserModel().setAcademicYearId(graduationYears.get(0).getId());
                binding.etAcademicYear.setText(graduationYears.get(0).getValue());

            }
        } else
            binding.etAcademicYear.setText("");


    }

    private void displayProfileVisibilty() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();

        ArrayList<String> privacy = new ArrayList<>();
        privacy.clear();

        if (userProfileData.getUserModel().isPublicProfile()) {
            privacy.add(getString(R.string.global_small));
        } else {
            privacy.add(getString(R.string.campus_small));

        }


        Fragment hometownSearchFragment = ProfileVisibilitySelectorFragment.newInstance(privacy);
        fragmentTransaction
                .replace(R.id.flContainer, hometownSearchFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setProfileVisibility(String profileVisibility) {
        userProfileData.getUserModel().setPublicProfile(profileVisibility.equalsIgnoreCase("Global"));
        binding.etProfileVisibility.setText(profileVisibility);
    }

    private void displayDorms() {
        dorms.clear();
        dorms.add(userProfileData.getUserModel().getDormitory());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        Fragment hometownSearchFragment = DormFragment.newInstance(true, dorms);
        fragmentTransaction
                .replace(R.id.flContainer, hometownSearchFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setDorm(ArrayList<Dorm> dormlist) {

        dorms.clear();
        dorms.addAll(dormlist);

        if (!dormlist.isEmpty()) {

            userProfileData.getUserModel().setDormitory(dormlist.get(0));
            userProfileData.getUserModel().setDormatoryId(dormlist.get(0).getDormId());
            binding.etDorm.setText(dormlist.get(0).getTitle());
        } else
            binding.etDorm.setText("");


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

    }

    public void updateProfile() {


        userProfileData.getUserModel().setMajor(getDisciplineIds(majors));
        userProfileData.getUserModel().setMinor(getDisciplineIds(minors));

        userProfileData.getUserModel().setFraternities(getIds(fraternities));
        userProfileData.getUserModel().setSororities(getIds(sororities));
        userProfileData.getUserModel().setfName(binding.etFName.getText().toString());
        userProfileData.getUserModel().setlName(binding.etLName.getText().toString());
        userProfileData.getUserModel().setBio(binding.etBio.getText().toString());
        userProfileData.getUserModel().setUserName(binding.etUsername.getText().toString().replaceFirst("@", ""));


        Observable<UserProfileResponse> userProfileResponseObservable = retrofitRestRepository.getApiService().updateUser(userProfileData.getUserModel());
        new BaseCallback<UserProfileResponse>(this, userProfileResponseObservable) {

            @Override
            public void onSuccess(UserProfileResponse response) {
                hideProgressBar();

                if (response != null) {
                    if (response.getData() != null) {
                        UserBaseModel user = response.getData();
                        PreferenceUtil.setUser(user);


                        finish();
                    } else if (response.getMeta() != null && response.getMeta().getCode() != 200) {

                        showSnackBar(response.getMeta().getMessage());
                    }
                }
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

    private ArrayList<Integer> getDisciplineIds(ArrayList<MajorModel> arrayList) {
        ArrayList<Integer> integers = new ArrayList<>();
        for (MajorModel major : arrayList) {
            integers.add(major.getId());
        }

        return integers;
    }

    private ArrayList<Integer> getInterestIds(ArrayList<Interest> arrayList) {
        ArrayList<Integer> integers = new ArrayList<>();
        for (Interest major : arrayList) {
            integers.add(major.getId());
        }

        return integers;
    }


    @Override
    public void onPermissionGiven() {
        onPermissionGranted = true;
        toggleMediaBottom(true);
    }

    @Override
    public void success(String name, String path) {

        if (getFragment(MediaBottomFragment.class.getName()) != null)
            ((MediaBottomFragment) getFragment(MediaBottomFragment.class.getName())).setCameraStop(true);

        toggleMediaBottom(false);
        Bundle b = new Bundle();
        b.putString(Constants.BundleKey.IMAGE_URL, path);
        ExplicitIntent.getsInstance().navigateForResult(EditProfileActivity.this, CropperActivity.class, Constants.REQUEST_CODE.REQUEST_CROPPER, b);
    }

    @Override
    public void fail(String message) {
        toggleMediaBottom(true);

    }

    private void isIdExist() {

        if (baseCallback != null)
            baseCallback.cancel();

        if (binding.etUsername.getText().toString().trim().replaceFirst("@", "").isEmpty()) {
            binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);
            binding.txtError.setVisibility(View.VISIBLE);
            binding.txtError.setText(R.string.err_valid_username_msg);
            binding.clHeader.tvHeaderEnd.setEnabled(false);
            binding.clHeader.tvHeaderEnd.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.black));
            return;

        }

        baseCallback = new BaseCallback<IdResponse>(EditProfileActivity.this, retrofitRestRepository.getApiService()
                .userId(binding.etUsername.getText().toString().trim().replaceFirst("@", ""))) {
            @Override
            public void onSuccess(IdResponse emailResponse) {
                if (emailResponse.getData().isSuccess() || (binding.etUsername.getText().toString().trim().replaceFirst("@", "")).equalsIgnoreCase(PreferenceUtil.getUser().getUserName())) {

                    binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_tick_stroke, 0);
                    binding.txtError.setVisibility(View.INVISIBLE);
                    binding.clHeader.tvHeaderEnd.setEnabled(true);
                    binding.clHeader.tvHeaderEnd.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.app_white));

                } else {

                    binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);
                    binding.txtError.setVisibility(View.VISIBLE);
                    binding.txtError.setText(R.string.err_username_msg);
                    binding.clHeader.tvHeaderEnd.setEnabled(false);
                    binding.clHeader.tvHeaderEnd.setTextColor(ContextCompat.getColor(EditProfileActivity.this, R.color.black));

                }
            }

            @Override
            public void onFail() {
                binding.etUsername.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        };
    }

    @Subscribe
    public void onEvent(Event event) {
    }


}
