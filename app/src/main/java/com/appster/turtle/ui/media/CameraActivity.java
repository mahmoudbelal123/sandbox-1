/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.media;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityCameraBinding;
import com.appster.turtle.model.Sticker;
import com.appster.turtle.model.Tag;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ImagePickerUtils;
import com.appster.turtle.util.Utils;
import com.appster.turtle.viewmodel.CameraPreviewModel;
import com.appster.turtle.viewmodel.CameraViewModel;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;

import java.util.ArrayList;
/**
 * A activity class for Camera
 */
public class CameraActivity extends BaseActivity implements ImagePickerUtils.OnImagePickerListener, BaseActivity.PermissionI {


    private ActivityCameraBinding cameraBinding;
    private CameraView camera;
    private CameraPreviewModel cameraPreviewModel;
    private CameraViewModel cameraViewModel;
    private boolean isPermissionGranted;
    private Bundle mBundle;
    private int position;
    private int roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
        camera = cameraBinding.rlCameraControl.camera;


        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            roomId = mBundle.getInt(Constants.BundleKey.ROOM_ID);
            position = getIntent().getExtras().getInt(Constants.BundleKey.ROOM_ID, -1);
        }

        cameraPreviewModel = new CameraPreviewModel(CameraActivity.this);
        cameraBinding.rlPreviewControl.setViewModel(cameraPreviewModel);

        cameraBinding.rlPreviewControl.etCaption.setMentionEnabled(true);
        cameraBinding.rlPreviewControl.etCaption.setHashtagEnabled(true);


        setViewModel();

        cameraBinding.rlPreviewControl.tvUse.setOnClickListener(view -> cameraPreviewModel.onUserClick());
        cameraBinding.rlPreviewControl.stickerView.setLocked(false);
        cameraBinding.rlPreviewControl.stickerView.setConstrained(true);

        cameraBinding.rlPreviewControl.stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
//
            }

            @Override
            public void onStickerClicked(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {

                sticker.setAlpha(0);
                cameraPreviewModel.onStickerClicked((TextSticker) sticker);
            }

            @Override
            public void onStickerDeleted(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
//
            }

            @Override
            public void onStickerDragFinished(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
//
            }

            @Override
            public void onStickerZoomFinished(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
//
            }

            @Override
            public void onStickerFlipped(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
//
            }

            @Override
            public void onStickerDoubleTapped(@NonNull com.xiaopo.flying.sticker.Sticker sticker) {
//
            }
        });

        cameraBinding.rlPreviewControl.vvPreview.setOnInfoListener((mp, i, i1) -> {
            float videoWidth = mp.getVideoWidth();
            float videoHeight = mp.getVideoHeight();

            setStickerViewSize((int) videoWidth, (int) videoHeight);
            return false;
        });

    }

    private void setViewModel() {

        if (!isPermissionGranted)
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, this);
        else {
            cameraViewModel = new CameraViewModel(CameraActivity.this, camera, cameraPreviewModel);
            cameraBinding.rlCameraControl.setViewModel(cameraViewModel);

            cameraBinding.rlPreviewControl.colorPickerView.setColorListener(color -> {


            });

            if (mBundle != null && mBundle.getString(Constants.BundleKey.IMAGE_URL) != null) {
                String uri = mBundle.getString(Constants.BundleKey.IMAGE_URL);
                cameraPreviewModel.setUri(Uri.parse(uri));
            }

            cameraBinding.rlPreviewControl.colorPickerView.selectCenter();
        }


    }

    public int getRoom() {
        return roomId;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String getActivityName() {
        return CameraActivity.class.getName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cameraBinding.rlCameraControl.camera.getLayoutParams();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.height = displayMetrics.heightPixels;
        layoutParams.width = displayMetrics.widthPixels;

        cameraBinding.rlCameraControl.camera.setLayoutParams(layoutParams);


        if (isPermissionGranted)
            camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPermissionGranted)
            camera.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPermissionGranted)
            camera.destroy();
    }


    @Override
    public void onBackPressed() {
        hideKeyboard();
        if (cameraPreviewModel.getPreview()) {
            cameraPreviewModel.setBitmap(null);
            cameraPreviewModel.setUri(null);
            cameraPreviewModel.setCaption("");
            cameraPreviewModel.setTagList(new ArrayList<Tag>());
            cameraPreviewModel.setUserTagList(new ArrayList<>());
            cameraPreviewModel.setPreview(false);
            cameraBinding.rlPreviewControl.stickerView.removeAllStickers();
            cameraPreviewModel.setTextEditing(false);
//            cameraBinding.rlPreviewControl.etImageText.setText("");
            cameraViewModel.setSessionType(SessionType.PICTURE);
            cameraViewModel.setCameraType(CameraViewModel.CAMERA_TYPE_PICTURE);
        } else
            finish();
    }

    public void selectGalleryItem(String uri) {
        cameraPreviewModel.setUri(Uri.parse(uri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Constants.REQUEST_CODE.REQUEST_TEXT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Bundle request = data.getExtras();
                    Sticker sticker = request.getParcelable(Constants.BundleKey.IMAGE_TEXT);
                    int position = request.getInt(Constants.BundleKey.IMAGE_TEXT_POS, -1);

                    if (!sticker.getText().isEmpty()) {
                        cameraPreviewModel.setColor(sticker.getColor());
                        cameraPreviewModel.setImageText(sticker.getText());
                        cameraPreviewModel.setTextEditing(true);
                        cameraPreviewModel.addSticker(sticker, position);
                        cameraBinding.rlPreviewControl.ivCameraText.setBackground(Utils.getBackgroundDrawable(sticker.getColor()));


                    } else {
                        cameraPreviewModel.setTextEditing(false);

                    }


                }
                break;


            case Constants.REQUEST_CODE.REQUEST_ADD_TAG:


                if (resultCode == RESULT_OK) {

                    cameraPreviewModel.setTagList(data.getParcelableArrayListExtra(Constants.TAGS));
                    cameraPreviewModel.setUserTagList(data.getParcelableArrayListExtra(Constants.USER_TAGS));
                    cameraPreviewModel.setCaption(data.getStringExtra(Constants.CAPTION));

                }

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    public void setStickerViewSize(int imageWidth, int imageHeight) {

        int width = Utils.getScreenWidth(CameraActivity.this);
        int height = (imageHeight * width) / imageWidth;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraBinding.rlPreviewControl.stickerView.getLayoutParams();
        params.width = width;
        params.height = height;

        cameraBinding.rlPreviewControl.stickerView.setLayoutParams(params);

    }

    public ActivityCameraBinding getCameraBinding() {
        return cameraBinding;
    }


    @Override
    public void success(String name, String path) {

        if (path != null)
            selectGalleryItem(path);

    }

    @Override
    public void fail(String message) {
//
    }

    @Override
    public void onPermissionGiven() {
        isPermissionGranted = true;
        setViewModel();
    }
}
