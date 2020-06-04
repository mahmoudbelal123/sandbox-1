/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.signUp;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.adapter.AvatarAdapter;
import com.appster.turtle.databinding.ActivityChooseAvatarBinding;
import com.appster.turtle.databinding.LayoutGallerySelectorBinding;
import com.appster.turtle.model.AvatarModel;
import com.appster.turtle.model.Event;
import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.model.UserProfileData;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.response.ChooseAvatarResponse;
import com.appster.turtle.network.response.GetImageResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.media.CropperActivity;
import com.appster.turtle.ui.media.MediaBottomFragment;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.ImagePickerUtils;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.flipboard.bottomsheet.BottomSheetLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by atul on 18/09/17.
 * To inject activity reference.
 * Activity for choose avatar
 */

@SuppressWarnings("unchecked")
public class ChooseAvatarActivity extends BaseActivity implements ImagePickerUtils.OnImagePickerListener, BaseActivity.PermissionI {

    private int mProfileTypePic = 0;
    private int mImageId = 0;
    private RequestBody mRequestBody;
    private RequestBody mCropRequestBody;
    private ActivityChooseAvatarBinding avatarBinding;
    private String imagePath, imageCompletePath;
    private AvatarAdapter avatarAdapter;
    private boolean isUpdated;
    private boolean onPermissionGranted;
    private ArrayList<AvatarModel> mAvatars;
    private LayoutGallerySelectorBinding bottomSheetBinding;
    private UserProfileData user;
    private ViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();


        if (getIntent().hasExtra(Constants.CHOOSE_AVATAR_USER)) {
            user = getIntent().getParcelableExtra(Constants.CHOOSE_AVATAR_USER);

        }

//        toggleMediaBottom(false);
        setUpHeader(avatarBinding.header.clHeader, getString(R.string.txt_ca_ttl), user != null ? getString(R.string.save) : getString(R.string.txt_signUp_nxt));

        avatarBinding.header.tvHeaderEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_bg_grey));
        avatarBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.black));
        avatarBinding.header.ivIconStart.setImageResource(R.drawable.ic_back_black);

        getAvatars();


    }

    private void initDataBinding() {
        avatarBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_avatar);
        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(ChooseAvatarActivity.this), R.layout.layout_gallery_selector, avatarBinding.bottomSheetMembers, false);
        bottomSheetBinding.rlHeader.setVisibility(View.GONE);


        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void setPreSelectedImage() {
        for (int i = 0; i < avatarAdapter.getAvatarModels().size(); i++) {
            if (avatarAdapter.getAvatarModels().get(i).getImageType() == user.getUserModel().getProfileUrlType()) {
                viewPager.setCurrentItem(i);
            }

            mProfileTypePic = user.getUserModel().getProfileUrlType();

            if (user.getUserModel().getProfileUrlType() == 1) {
                imagePath = BuildConfig.ORIGINAL_IMAGE_BASE_URL + user.getUserModel().getProfileUrl();
                avatarAdapter.refreshImage(imagePath);
                avatarBinding.tvEdit.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setUpView() {

        avatarAdapter = new AvatarAdapter(this, mAvatars);
        viewPager = avatarBinding.pagerContainer.getViewPager();
        viewPager.setAdapter(avatarAdapter);
        viewPager.setPageMargin(180);
        avatarBinding.header.tvHeaderEnd.setEnabled(true);
        avatarBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.app_white));

        if (user != null) {
            setPreSelectedImage();

        }

        viewPager.setClipChildren(false);
        viewPager.setPageTransformer(true, new CustomPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mProfileTypePic = avatarAdapter.getItem(position).getImageType();
                mImageId = avatarAdapter.getItem(position).getId();

                if (position == 1) {

                    avatarBinding.header.tvHeaderEnd.setEnabled(false);
                    if (imagePath != null && !imagePath.isEmpty()) {
                        avatarBinding.header.tvHeaderEnd.setEnabled(true);
                        avatarBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(ChooseAvatarActivity.this, R.color.app_white));

                        avatarBinding.tvEdit.setVisibility(View.VISIBLE);
                    } else {
                        toggleMediaBottom(true);
                        avatarBinding.tvEdit.setVisibility(View.GONE);
                    }


                } else {
                    toggleMediaBottom(false);
                    avatarBinding.header.tvHeaderEnd.setEnabled(true);
                    avatarBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(ChooseAvatarActivity.this, R.color.app_white));

                    avatarBinding.tvEdit.setVisibility(View.GONE);

                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        avatarBinding.tvEdit.setOnClickListener(view -> {
            toggleMediaBottom(true);
            avatarBinding.tvEdit.setVisibility(View.GONE);
        });

        avatarBinding.header.tvHeaderEnd.setOnClickListener(view -> {

            avatarBinding.header.tvHeaderEnd.setClickable(false);
            if (viewPager.getCurrentItem() == 1) {
                if (user != null) {
                    if (imagePath != null && imagePath.startsWith("http")) {
                        onBackPressed();
                        return;
                    }
                }

                if (imagePath != null && !imagePath.isEmpty() && imageCompletePath != null && !imageCompletePath.isEmpty()) {
                    imagePath = imagePath.replace("file:/", "");
                    File file = new File(imagePath);
                    File completeImageFile = new File(imageCompletePath);
                    mRequestBody = RequestBody.create(MediaType.parse("image/*"), completeImageFile);
                    mCropRequestBody = RequestBody.create(MediaType.parse("croppedImage/*"), file);
                }


            } else {
                mRequestBody = null;
                mCropRequestBody = null;

            }


            uploadProfilePic();
        });

    }


    public void openGallery() {

        if (viewPager.getCurrentItem() == 1) {
            toggleMediaBottom(true);
            avatarBinding.tvEdit.setVisibility(View.GONE);
        }
    }


    @Subscribe
    public void onEvent(Event event) {

        switch (event.getEventId()) {
            case Constants.eventI.ON_CAMERA_CLICK:
                toggleMediaBottom(false);
                ImagePickerUtils.add(true, getSupportFragmentManager(), ChooseAvatarActivity.this);


                break;

            case Constants.eventI.ON_IMAGE_CLICK:
                toggleMediaBottom(false);

                event.getEventMsg();
                imageCompletePath = event.getEventMsg();
                if (imageCompletePath.endsWith(".gif")) {
                    FileOutputStream out = null;
                    try {
                        File image = new File(imageCompletePath);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);


                        out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/test.png");
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                        imageCompletePath = Environment.getExternalStorageDirectory() + "/test.png";
                    } catch (Exception e) {
                        LogUtils.LOGD("ERROR", e.getMessage());
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            LogUtils.LOGD("ERROR", e.getMessage());
                        }
                    }
                }
                Bundle b = new Bundle();
                b.putString(Constants.BundleKey.IMAGE_URL, event.getEventMsg());
                ExplicitIntent.getsInstance().navigateForResult(ChooseAvatarActivity.this, CropperActivity.class, Constants.REQUEST_CODE.REQUEST_CROPPER, b);


                break;

        }
    }

    @Override
    protected void onDestroy() {

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    private void uploadProfilePic() {
        showProgressBar();
        HashMap<String, RequestBody> task = new HashMap();
        task.put("profileUrlType", createPartFromString(String.valueOf(mProfileTypePic)));
        task.put("id", createPartFromString(String.valueOf(PreferenceUtil.getUser().getUserId())));
        task.put("selectedImgId", createPartFromString(String.valueOf(mImageId)));

        new BaseCallback<ChooseAvatarResponse>(this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService()
                .imageUpdate(mRequestBody, mCropRequestBody, task)) {
            @Override
            public void onSuccess(ChooseAvatarResponse baseResponse) {
                hideProgressBar();

                UserBaseModel userBaseModel = baseResponse.getData();

                if (user != null) {
                    user.getUserModel().setProfileUrl(userBaseModel.getProfileUrl());
                    user.getUserModel().setProfileUrlType(userBaseModel.getProfileUrlType());

                    PreferenceUtil.setUser(user.getUserModel());
                    Intent i = new Intent();
                    i.putExtra(Constants.CHOOSE_AVATAR_USER, user);
                    setResult(RESULT_OK, i);
                    finish();
                } else {


                    userBaseModel.setProfileComplete(true);
                    PreferenceUtil.setUser(userBaseModel);

                    ExplicitIntent.getsInstance().navigateTo(ChooseAvatarActivity.this, ProfileSetup1Activity.class);
                }
                avatarBinding.header.tvHeaderEnd.setClickable(true);


            }

            @Override
            public void onFail() {
                avatarBinding.header.tvHeaderEnd.setClickable(true);


                hideProgressBar();
            }
        };
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        if (descriptionString == null)
            return RequestBody.create(MultipartBody.FORM, "");
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);

    }


    private void toggleMediaBottom(boolean isVisible) {

        if (onPermissionGranted) {
            if (getFragment(MediaBottomFragment.class.getName()) == null)
                pushFragment(Constants.FRAGMENTS.MEDIA_BOTTOM_FRAGMENT, null, bottomSheetBinding.flMediaBottomSheet.getId(), false, ANIMATION_TYPE.NONE);

//

            avatarBinding.bottomSheetMembers.addOnSheetStateChangeListener(state -> {

                if (state.equals(BottomSheetLayout.State.PREPARING)) {
                    if (getFragment(MediaBottomFragment.class.getName()) != null)
                        ((MediaBottomFragment) getFragment(MediaBottomFragment.class.getName())).start();
                }

                avatarBinding.tvEdit.setVisibility(imagePath == null || imagePath.isEmpty() ? View.GONE : View.VISIBLE);

            });

            if (!isVisible) {
                avatarBinding.bottomSheetMembers.dismissSheet();

            } else {
                avatarBinding.bottomSheetMembers.showWithSheetView(bottomSheetBinding.getRoot());
            }

            avatarBinding.tvEdit.setVisibility(imagePath == null || imagePath.isEmpty() ? View.GONE : View.VISIBLE);

        } else if (isVisible)
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);


    }


    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }


    @Override
    public void success(String name, String path) {

        if (getFragment(MediaBottomFragment.class.getName()) != null)
            ((MediaBottomFragment) getFragment(MediaBottomFragment.class.getName())).setCameraStop(true);

        toggleMediaBottom(false);
        imageCompletePath = path;

        if (imageCompletePath.endsWith(".gif")) {
            FileOutputStream out = null;
            try {
                File image = new File(imageCompletePath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);


                out = new FileOutputStream(Environment.getExternalStorageDirectory() + "test.png");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
                imageCompletePath = Environment.getExternalStorageDirectory() + "test.png";
            } catch (Exception e) {
                LogUtils.LOGD("ERROR", e.getMessage());
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    LogUtils.LOGD("ERROR", e.getMessage());
                }
            }
        }
        Bundle b = new Bundle();
        b.putString(Constants.BundleKey.IMAGE_URL, path);
        ExplicitIntent.getsInstance().navigateForResult(ChooseAvatarActivity.this, CropperActivity.class, Constants.REQUEST_CODE.REQUEST_CROPPER, b);
    }

    @Override
    public void fail(String message) {
        toggleMediaBottom(true);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE.REQUEST_CROPPER:
                if (resultCode == RESULT_OK) {
                    Bundle request = data.getExtras();
                    imagePath = request.getString(Constants.BundleKey.IMAGE_URL);

                    if (getFragment(MediaBottomFragment.class.getName()) != null)
                        ((MediaBottomFragment) getFragment(MediaBottomFragment.class.getName())).setCameraStop(false);

                    isUpdated = true;
                    if (imagePath != null && !imagePath.isEmpty()) {

                        avatarBinding.tvEdit.setVisibility(View.VISIBLE);

                        avatarBinding.header.tvHeaderEnd.setEnabled(true);
                        avatarBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.app_white));

                        avatarAdapter.refreshImage(imagePath);
                    }
                    toggleMediaBottom(false);
                    isUpdated = false;

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

    @Override
    public void onPermissionGiven() {
        onPermissionGranted = true;
        toggleMediaBottom(true);
    }

    private class CustomPageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {


        }
    }


    private void getAvatars() {
        showProgressBar();
        new BaseCallback<GetImageResponse>(this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService()
                .getProfileImages()) {
            @Override
            public void onSuccess(GetImageResponse baseResponse) {
                hideProgressBar();
                if (baseResponse.getData() != null && !baseResponse.getData().isEmpty()) {
                    mAvatars = baseResponse.getData();

                    mProfileTypePic = mAvatars.get(0).getImageType();
                    mImageId = mAvatars.get(0).getId();

                    setUpView();


                }
            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };
    }

}
