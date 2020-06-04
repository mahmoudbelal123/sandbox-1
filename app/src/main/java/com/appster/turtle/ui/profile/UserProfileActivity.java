package com.appster.turtle.ui.profile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityUserProfileCooBinding;
import com.appster.turtle.databinding.LayoutProfileAcceptRejectBinding;
import com.appster.turtle.databinding.LayoutProfileMenuBinding;
import com.appster.turtle.model.UserProfileData;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.FriendRequestResponse;
import com.appster.turtle.network.response.GetUserProfile;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.ReportActivity;
import com.appster.turtle.ui.friends.MutualFriendsActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;
import com.appster.turtle.widget.UserLockBottomSheetBehaviour;

import java.net.MalformedURLException;
import java.net.URL;

import rx.Observable;
/*
* Activity for user profile
 */
public class UserProfileActivity extends BaseActivity implements View.OnClickListener {

    private ActivityUserProfileCooBinding binding;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private int userId, pos;
    private UserProfileData userProfileData;
    private RetrofitRestRepository mRepository;
    private LayoutProfileMenuBinding bottomSheetMenuBinding;
    private boolean isLoggedUser;
    private LayoutProfileAcceptRejectBinding bottomSheetAcceptBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile_coo);
        userId = getIntent().getIntExtra(Constants.USER_ID, PreferenceUtil.getUser().getUserId());
        pos = getIntent().getIntExtra(Constants.POS, 0);

        isLoggedUser = userId == PreferenceUtil.getUser().getUserId();

        if (isLoggedUser) {
            setUpHeader(binding.header.clHeader, "", "Edit");
            binding.header.tvHeaderEnd.setBackgroundResource(R.drawable.button_hollow_color);
            binding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(UserProfileActivity.this, R.color.bright_teal));
            binding.header.tvHeaderEnd.setEnabled(true);
            binding.bottomSheet.tvMutualFriend.setVisibility(View.GONE);
            binding.bottomSheet.llFirstSeg.setVisibility(View.VISIBLE);
            binding.bottomSheet.llFirstSeg.setBackground(null);
            binding.bottomSheet.llFirstSegOther.setVisibility(View.GONE);
            binding.bottomSheet.llProfileDetails.setVisibility(View.VISIBLE);
        } else {
            setUpHeader(binding.header.clHeader, "");
            binding.bottomSheet.llFirstSeg.setVisibility(View.GONE);
            binding.bottomSheet.llFirstSegOther.setVisibility(View.VISIBLE);
            binding.bottomSheet.llProfileDetails.setVisibility(View.GONE);

        }
        bottomSheetMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(UserProfileActivity.this), R.layout.layout_profile_menu, binding.bsBottomMenu, false);
        bottomSheetAcceptBinding = DataBindingUtil.inflate(LayoutInflater.from(UserProfileActivity.this), R.layout.layout_profile_accept_reject, binding.bsBottomMenu, false);
        setUpBottomSheet();


        binding.bottomSheet.tvViewProfile.setOnClickListener(this);
        binding.bottomSheet.tvConnect.setOnClickListener(this);
        binding.bottomSheet.tvInfo.setOnClickListener(this);
        bottomSheetMenuBinding.tvDisconnect.setOnClickListener(this);
        bottomSheetMenuBinding.tvCancel.setOnClickListener(this);
        bottomSheetMenuBinding.tvReportFlagUser.setOnClickListener(this);
        bottomSheetAcceptBinding.tvAccept.setOnClickListener(this);
        bottomSheetAcceptBinding.tvReject.setOnClickListener(this);
        bottomSheetAcceptBinding.tvCancel.setOnClickListener(this);
        bottomSheetMenuBinding.tvReportFlagUser.setOnClickListener(this);
        binding.bottomSheet.tvMutualFriend.setOnClickListener(this);
        binding.bottomSheet.rlConnections.setOnClickListener(this);
        binding.bottomSheet.rlPosts.setOnClickListener(this);
        binding.bottomSheet.rlSpaces.setOnClickListener(this);
        binding.header.ivIconStart.setOnClickListener(v -> onBackPressed());

    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserProfile();
    }

    public void nextClicked(View view) {
        if (userProfileData != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.USER, userProfileData);
            ExplicitIntent.getsInstance().navigateTo(UserProfileActivity.this, EditProfileActivity.class, bundle);

        }
    }


    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra(Constants.POS)) {
            Intent it = new Intent();
            it.putExtra(Constants.USER, userProfileData);
            it.putExtra(Constants.POS, pos);
            setResult(RESULT_OK, it);
            finish();
        } else
            finish();
    }


    @Override
    public String getActivityName() {
        return UserProfileActivity.class.getName();
    }

    private void setUpBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetDetails);
        bottomSheetBehavior.setPeekHeight(Utils.dpToPx(UserProfileActivity.this, isLoggedUser ? 250 : 270));


        // change the state of the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // set hideable or not
        bottomSheetBehavior.setHideable(false);

        // set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.bottomSheet.tvViewProfile.setVisibility(View.VISIBLE);

                    binding.bottomSheet.nestedScroll.smoothScrollTo(0, 0);

                    binding.bottomSheet.nestedScroll.post(() -> binding.bottomSheet.nestedScroll.smoothScrollTo(0, 0));
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    binding.bottomSheet.tvViewProfile.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                binding.bottomSheet.tvViewProfile.setAlpha(1 - slideOffset);
            }
        });
    }


    private void getUserProfile() {
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        Observable<GetUserProfile> observable = mRepository.getApiService().getUserProfile(userId);
        new BaseCallback<GetUserProfile>(UserProfileActivity.this, observable, true) {

            @Override
            public void onSuccess(GetUserProfile response) {


                if (response != null && (response.getMeta().getCode() == Constants.RESPONSE_CODE.UNIVERSITY_INACTIVE_USER || response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_AUTH_RESET_EMAIL_ERROR)) {

                    Alert.logoutAlert(UserProfileActivity.this, "", response.getMeta().getMessage(), "OK", false, () -> UserProfileActivity.this.finish());
                }

                if (response != null && response.getData() != null) {


                    userProfileData = response.getData();
                    binding.setUser(userProfileData);
                    binding.bottomSheet.setUser(userProfileData);

                    if (userProfileData.getUserModel().getName().length() > 20)
                        bottomSheetMenuBinding.tvName.setText(userProfileData.getUserModel().getName().substring(0, 20) + "...");
                    else
                        bottomSheetMenuBinding.tvName.setText(userProfileData.getUserModel().getName());


                    bottomSheetMenuBinding.tvUsername.setText(userProfileData.getUserModel().getUsernameWithAtSign());


                    if (userProfileData.getUniversityBaseModel() != null)
                        bottomSheetMenuBinding.tvUniversity.setText(userProfileData.getUniversityBaseModel().getDisplayName());

                    setProfileUI();

                }


            }

            @Override
            public void onFail() {

            }
        };

    }


    private void actionOnRequest() {
        int action = 0;

        if (userProfileData.getFriendStatus() == Constants.MyFriend.FRIEND_STATUS_PENDING)
            action = 2;
        else if (userProfileData.getFriendStatus() == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED ||
                userProfileData.getFriendStatus() == Constants.MyFriend.STATUS_REJECTED)
            action = 1;
        else if (userProfileData.getFriendStatus() == Constants.MyFriend.FRIEND_STATUS_CONNECTED)
            action = 4;

        actionOnRequest(action);
    }


    private void actionOnRequest(int action) {


        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();


        BaseCallback<FriendRequestResponse> baseCallback = new BaseCallback<FriendRequestResponse>(UserProfileActivity.this, mRepository.getApiService().actionFriendRequest(userId, action)) {
            @Override
            public void onSuccess(FriendRequestResponse response) {

                if (response.getData() != null) {
                    userProfileData.setFriendStatus(response.getData().getUserReqStatus());
                    userProfileData.setRequestedByMe(true);
                    setProfileUI();
                    binding.bsBottomMenu.dismissSheet();
                }


            }

            @Override
            public void onFail() {

            }
        };
    }

    private void setProfileUI() {
//

        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) (binding.ivBg).getLayoutParams();

        if (userProfileData.getUserModel().getProfileUrlType() == Constants.AVATAR.NAME || userProfileData.getUserModel().getProfileUrlType() == Constants.AVATAR.EMOJI) {

            binding.ivBg.setScaleType(ImageView.ScaleType.FIT_CENTER);

            binding.ivCircle.setVisibility(View.VISIBLE);

            try {
                GlideApp.with(UserProfileActivity.this)
                        .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + userProfileData.getUserModel().getProfileUrl()))
                        .placeholder(R.color.black)
                        .into(binding.ivBg);
            } catch (MalformedURLException e) {
                LogUtils.printStackTrace(e);
            }

            if (PreferenceUtil.getUser().getProfileUrlType() == Constants.AVATAR.NAME) {
                param.height = Utils.dpToPx(UserProfileActivity.this, 90);
                param.width = Utils.dpToPx(UserProfileActivity.this, 90);
            } else {
                param.height = Utils.dpToPx(UserProfileActivity.this, 120);
                param.width = Utils.dpToPx(UserProfileActivity.this, 120);
            }

            param.addRule(RelativeLayout.ALIGN_TOP, binding.ivCircle.getId());
            param.addRule(RelativeLayout.ALIGN_BOTTOM, binding.ivCircle.getId());
            param.addRule(RelativeLayout.CENTER_HORIZONTAL);


        } else {

            binding.ivBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            binding.ivCircle.setVisibility(View.INVISIBLE);

            binding.ivBg.setBackground(null);

            try {
                GlideApp.with(UserProfileActivity.this)
                        .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + userProfileData.getUserModel().getProfileUrl()))
                        .placeholder(R.color.black)
                        .centerCrop()
                        .into(binding.ivBg);
            } catch (MalformedURLException e) {
                LogUtils.printStackTrace(e);
            }

            param.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            param.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            param.removeRule(RelativeLayout.ALIGN_TOP);
            param.removeRule(RelativeLayout.ALIGN_BOTTOM);
            param.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            param.topMargin = 0;


        }

        binding.ivBg.setLayoutParams(param);


        if (userId != PreferenceUtil.getUser().getUserId()) {

            binding.bottomSheet.tvMutualFriend.setText(String.format(getString(R.string.mutual_friend), userProfileData.getExtraInfo().getMutualFriendCount()));

//

            if (userProfileData.getUserModel().isPublicProfile() || (userProfileData != null && !userProfileData.getUserModel().isPublicProfile() && userProfileData.getUniversityBaseModel() != null && userProfileData.getUniversityBaseModel().getId().equals(PreferenceUtil.getUniv().getId()))) {
                binding.bottomSheet.tvConnect.setActivated(true);
                binding.bottomSheet.tvConnect.setText(R.string.connect);
                binding.bottomSheet.tvViewProfile.setVisibility(View.VISIBLE);
                binding.bottomSheet.llFirstSegOther.setVisibility(View.VISIBLE);
                binding.bottomSheet.llProfileDetails.setVisibility(View.VISIBLE);
                binding.bottomSheet.llFirstSeg.setVisibility(View.VISIBLE);
                bottomSheetMenuBinding.tvDisconnect.setVisibility(View.GONE);


            } else {
                binding.bottomSheet.tvConnect.setActivated(true);
                binding.bottomSheet.tvConnect.setText(R.string.connect);
                binding.bottomSheet.tvViewProfile.setVisibility(View.GONE);
                lockBottomSheet(true);
                bottomSheetMenuBinding.tvDisconnect.setVisibility(View.GONE);
                binding.bottomSheet.llProfileDetails.setVisibility(View.GONE);
                binding.bottomSheet.llFirstSeg.setVisibility(View.GONE);

            }


            if (userProfileData.getFriendStatus() == Constants.MyFriend.FRIEND_STATUS_PENDING) {

                if (userProfileData.isRequestedByMe()) {
                    binding.bottomSheet.tvConnect.setActivated(false);
                    binding.bottomSheet.tvConnect.setText(R.string.pending_str);
                } else {
                    binding.bottomSheet.tvConnect.setActivated(true);
                    binding.bottomSheet.tvConnect.setText(R.string.respond_to_request);
                }
            } else if (userProfileData.getFriendStatus() == Constants.MyFriend.FRIEND_STATUS_CONNECTED) {
                binding.bottomSheet.tvConnect.setActivated(true);
                binding.bottomSheet.tvConnect.setText(R.string.connected);
                lockBottomSheet(false);

                binding.bottomSheet.tvViewProfile.setVisibility(View.VISIBLE);
                binding.bottomSheet.llFirstSegOther.setVisibility(View.VISIBLE);
                binding.bottomSheet.llProfileDetails.setVisibility(View.VISIBLE);
                binding.bottomSheet.llFirstSeg.setVisibility(View.VISIBLE);

                bottomSheetMenuBinding.tvDisconnect.setVisibility(View.VISIBLE);


            }

            if (userProfileData.getExtraInfo().getMutualFriendCount() == 0)
                binding.bottomSheet.tvMutualFriend.setText(R.string.no_mutual_friend);

            if (userProfileData.isFlaggedByMe()) {
                bottomSheetMenuBinding.tvReportFlagUser.setEnabled(false);
                bottomSheetMenuBinding.tvReportFlagUser.setText(getString(R.string.report_flag_this_user_ed));
            } else {
                bottomSheetMenuBinding.tvReportFlagUser.setText(getString(R.string.report_flag_this_user));

                bottomSheetMenuBinding.tvReportFlagUser.setEnabled(true);
            }
        }
    }

    private void lockBottomSheet(boolean lock) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) binding.bottomSheet.bottomSheetDetails.getLayoutParams();

        if (lock)
            params.setBehavior(new UserLockBottomSheetBehaviour());
        else
            params.setBehavior(new BottomSheetBehavior());


        binding.bottomSheet.bottomSheetDetails.requestLayout();

        setUpBottomSheet();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_info:
                binding.bsBottomMenu.showWithSheetView(bottomSheetMenuBinding.getRoot());

                break;

            case R.id.tv_view_profile:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;

            case R.id.tv_connect:


                if (userProfileData.getFriendStatus() == Constants.MyFriend.FRIEND_STATUS_PENDING && !userProfileData.isRequestedByMe()) {
                    binding.bsBottomMenu.showWithSheetView(bottomSheetAcceptBinding.getRoot());
                    return;
                }

                if (userProfileData.getFriendStatus() != Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED)
                    return;

                actionOnRequest();
                break;

            case R.id.tv_cancel:
                binding.bsBottomMenu.dismissSheet();
                break;

            case R.id.tv_disconnect:
                actionOnRequest();
                break;

            case R.id.tv_report_flag_user:
                Bundle b = new Bundle();
                b.putParcelable(Constants.USER, userProfileData);
                b.putInt(Constants.USER_ID, userId);
                ExplicitIntent.getsInstance().navigateForResult(this, ReportActivity.class, Constants.REQUEST_CODE.FLAG_POST, b);
                binding.bsBottomMenu.dismissSheet();
                break;

            case R.id.tv_accept:
                actionOnRequest(2);

                break;

            case R.id.tv_reject:
                actionOnRequest(3);

                break;

            case R.id.tv_mutual_friend:

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.USER_ID, userId);
                bundle.putBoolean(Constants.IS_ALL_CONNECTION, false);
                ExplicitIntent.getsInstance().navigateTo(UserProfileActivity.this, MutualFriendsActivity.class, bundle);
                break;

            case R.id.rl_spaces:


                bundle = new Bundle();
                bundle.putInt(Constants.USER_ID, userId);
                ExplicitIntent.getsInstance().navigateTo(UserProfileActivity.this, UserSpacesActivity.class, bundle);


                break;

            case R.id.rl_connections:


                bundle = new Bundle();
                bundle.putInt(Constants.USER_ID, userId);
                bundle.putBoolean(Constants.IS_ALL_CONNECTION, true);
                ExplicitIntent.getsInstance().navigateTo(UserProfileActivity.this, MutualFriendsActivity.class, bundle);


                break;

            case R.id.rl_posts:

                bundle = new Bundle();
                bundle.putInt(Constants.USER_ID, userId);
                ExplicitIntent.getsInstance().navigateTo(UserProfileActivity.this, UserPostActivity.class, bundle);

                break;

            default:
                break;

        }
    }
}
