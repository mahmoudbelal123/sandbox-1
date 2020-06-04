/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.rooms;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityRoomInfoBinding;
import com.appster.turtle.databinding.LayoutImageSelectorBinding;
import com.appster.turtle.databinding.PrivacyDialogBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.AddRoomRequest;
import com.appster.turtle.network.response.GetRoomResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ImagePickerUtils;
import com.appster.turtle.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.appster.turtle.util.LogUtils.LOGE;
/*
* Activity for room info
 */
public class RoomInfoActivity extends BaseActivity implements View.OnClickListener, ImagePickerUtils.OnImagePickerListener, BaseActivity.PermissionI {

    private ActivityRoomInfoBinding mBinding;
    private RetrofitRestRepository repository;
    private int roomType = -1;//Constants.ROOM_TYPE.PRIVATE;
    private ArrayList<Tag> tagList = new ArrayList<>();
    private String roomName;
    private ArrayList<Integer> memberIDs;
    private boolean imageFetched;
    private String imagePath;
    private LayoutImageSelectorBinding bottomSheetBinding;
    private boolean isPermissionGiven;

    private Dialog privacyDialog;
    private String roomDesc;

    @Override
    public String getActivityName() {
        return RoomInfoActivity.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_room_info);
        repository = ((ApplicationController) getApplicationContext()).provideRepository();

        if (getIntent().hasExtra(Constants.USER_IDS_KEY)) {
            memberIDs = getIntent().getIntegerArrayListExtra(Constants.USER_IDS_KEY);
        }

        if (getIntent().hasExtra(Constants.ROOM_NAME_KEY)) {
            roomName = getIntent().getStringExtra(Constants.ROOM_NAME_KEY);
        }

        if (getIntent().hasExtra(Constants.ROOM_DESC_KEY)) {
            roomDesc = getIntent().getStringExtra(Constants.ROOM_DESC_KEY);
        }

        initUI();
    }

    private void initUI() {

        setUpHeader(false, mBinding.header.clHeader, getString(R.string.create_space), "", R.drawable.back_arrow);
        mBinding.clTags.ivRoomType.setImageResource(R.drawable.plus);
        setClickListeners();
    }

    private void setClickListeners() {
        mBinding.tvCreateRoom.setOnClickListener(this);

        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(RoomInfoActivity.this), R.layout.layout_image_selector, mBinding.bottomSheetMembers, false);

        bottomSheetBinding.tvTakePhoto.setOnClickListener(this);
        bottomSheetBinding.tvCameraRoll.setOnClickListener(this);
        bottomSheetBinding.tvCancel.setOnClickListener(this);

        mBinding.clIncludedAvatar.clAvatar.setOnClickListener(this);
        mBinding.ivAvatarImage.setOnClickListener(this);

        mBinding.clRoomType.setOnClickListener(view -> showPrivacyDialog());


        mBinding.clTags.ivRoomType.setOnClickListener(view -> addTags());

    }

    private void showPrivacyDialog() {
        privacyDialog = new Dialog(RoomInfoActivity.this);
        privacyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        privacyDialog.setCancelable(true);
        PrivacyDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(RoomInfoActivity.this), R.layout.privacy_dialog, null, false);
        privacyDialog.setContentView(binding.getRoot());

        Window window = privacyDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        binding.ivPublic.setOnClickListener(RoomInfoActivity.this);
        binding.ivPrivate.setOnClickListener(RoomInfoActivity.this);
        binding.ivHidden.setOnClickListener(RoomInfoActivity.this);

        switch (roomType) {
            case Constants.ROOM_TYPE.PRIVATE:
                binding.ivPrivate.setImageResource(R.drawable.circle_tick_stroke);
                break;
            case Constants.ROOM_TYPE.PUBLIC:
                binding.ivPublic.setImageResource(R.drawable.circle_tick_stroke);
                break;
            case Constants.ROOM_TYPE.HIDDEN:
                binding.ivHidden.setImageResource(R.drawable.circle_tick_stroke);
                break;
        }

        privacyDialog.show();
    }


    private void showRoomImage() {
        mBinding.ivAvatarImage.setVisibility(View.VISIBLE);
    }

    private void hideRoomImageAvatar() {
        mBinding.clIncludedAvatar.clAvatar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_create_room:
                createRoom();
                break;

            case R.id.cl_included_avatar:
                fetchImageFromCameraOrGallery();
                break;

            case R.id.cl_avatar:
                fetchImageFromCameraOrGallery();
                break;

            case R.id.iv_avatar_image:
                fetchImageFromCameraOrGallery();
                break;


            case R.id.tv_camera_roll:
                mBinding.bottomSheetMembers.dismissSheet();
                ImagePickerUtils.add(false, getSupportFragmentManager(), RoomInfoActivity.this);
                break;
            case R.id.tv_take_photo:
                mBinding.bottomSheetMembers.dismissSheet();
                ImagePickerUtils.add(true, getSupportFragmentManager(), RoomInfoActivity.this);

                break;
            case R.id.tv_cancel:
                mBinding.bottomSheetMembers.dismissSheet();
                break;

            case R.id.iv_private:
                ((ImageView) view).setImageResource(R.drawable.circle_tick_stroke);
                privacyDialog.dismiss();
                setRoomTypeSelected(Constants.ROOM_TYPE.PRIVATE);
                mBinding.tvRoomTypeDesc.setText(R.string.private_title);
                break;
            case R.id.iv_public:
                ((ImageView) view).setImageResource(R.drawable.circle_tick_stroke);
                privacyDialog.dismiss();
                setRoomTypeSelected(Constants.ROOM_TYPE.PUBLIC);
                mBinding.tvRoomTypeDesc.setText(R.string.public_title);
                break;
            case R.id.iv_hidden:
                ((ImageView) view).setImageResource(R.drawable.circle_tick_stroke);
                privacyDialog.dismiss();
                setRoomTypeSelected(Constants.ROOM_TYPE.HIDDEN);
                mBinding.tvRoomTypeDesc.setText(R.string.hidden_title);
                break;

            default:
                break;
        }
    }

    private void fetchImageFromCameraOrGallery() {

        if (isPermissionGiven)
            mBinding.bottomSheetMembers.showWithSheetView(bottomSheetBinding.getRoot());
        else
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);


    }

    private void addTags() {
        Intent i = new Intent(RoomInfoActivity.this, AddTagsActivity.class);
        if (tagList.size() > 0)
            i.putParcelableArrayListExtra(Constants.TAGS, tagList);

        i.putExtra(Constants.IS_FROM_ROOM, true);
        startActivityForResult(i, Constants.REQUEST_CODE.REQUEST_ADD_TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Constants.REQUEST_CODE.REQUEST_CROPPER:
                if (resultCode == RESULT_OK) {
                    Bundle request = data.getExtras();
                    imagePath = request.getString(Constants.BundleKey.IMAGE_URL);
                    LOGE("Cropper", imagePath + "");

                    setImageUrl();

                }
                break;

            case Constants.REQUEST_CODE.REQUEST_ADD_TAG:
                if (resultCode == RESULT_OK) {
                    tagList.clear();
                    tagList = data.getParcelableArrayListExtra(Constants.TAGS);
                    displayTags();
                }

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }

    private void displayTags() {

        if (tagList == null || tagList.isEmpty())
            return;

        StringBuilder tagStr = new StringBuilder();
        for (Tag tag : tagList) {
            if (tagStr.length() == 0)
                tagStr = new StringBuilder("#" + tag.getValue());
            else
                tagStr.append(" #").append(tag.getValue());

        }

        mBinding.clTags.tvRoomTypeDesc.setText(tagStr.toString());
    }


    private void setRoomTypeSelected(int roomType) {
        this.roomType = roomType;

    }


    private void createRoom() {

        if (!isValidRoomInfo()) {
            return;
        }

        showProgressBar();

        AddRoomRequest roomRequest = new AddRoomRequest();
        roomRequest.setRoomDesc(roomDesc);
        roomRequest.setMemberCanPost(!mBinding.switchDontAllow.isChecked());
        roomRequest.setPublicFlag(true);
        roomRequest.setRoomName(roomName);
        roomRequest.setRoomType(roomType);
        roomRequest.setTags(tagList);
        roomRequest.setSearchable(true);
        roomRequest.setMemberIds(memberIDs);
        roomRequest.setRoomId(0);
        roomRequest.setGlobalRoom(mBinding.switchGlobalCampus.isChecked());

        new BaseCallback<GetRoomResponse>(RoomInfoActivity.this, repository.getApiService().addEditRoom(roomRequest), false) {
            @Override
            public void onSuccess(final GetRoomResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {


                    if (imagePath != null && !imagePath.isEmpty()) {
                        uploadRoomImage(response.getData().getRoomId());
                    } else
                        navigateToRoom(response.getData());


                }
            }

            @Override
            public void onFail() {

            }
        };
    }

    private void navigateToRoom(final Room room) {
        Alert.createAlert(RoomInfoActivity.this, "", getString(R.string.room_creation_msg)).setOnDismissListener(dialog -> {

            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BundleKey.ROOM, room);
            bundle.putInt(Constants.BundleKey.ROOM_ID, room.getRoomId());
            bundle.putBoolean(Constants.BundleKey.NEW_ROOM, true);
            Intent intent = new Intent(RoomInfoActivity.this, HomeMainActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);


        }).show();
    }

    private boolean isValidRoomInfo() {

        if (roomType == -1) {
            showSnackBar(getString(R.string.please_select_room_type));
            return false;
        }


        return true;
    }


    @Override
    public void success(String name, String path) {


        imagePath = path;
        LOGE("Cropper", imagePath + "");

        if (imagePath.endsWith(".gif")) {
            FileOutputStream out = null;
            try {
                File image = new File(imagePath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);


                out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/test.png");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
                imagePath = Environment.getExternalStorageDirectory() + "/test.png";
            } catch (Exception e) {
                LogUtils.LOGD(TAG1, e.getMessage());
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    LogUtils.LOGD(TAG1, e.getMessage());
                }
            }
        }

        setImageUrl();
    }

    @Override
    public void fail(String message) {
//
    }


    private void setImageUrl() {

        mBinding.ivAvatarImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));

        hideRoomImageAvatar();
        showRoomImage();
    }

    private void uploadRoomImage(int roomId) {

        if (imagePath.isEmpty()) {
            return;
        }

        try {
            HashMap<String, RequestBody> task = new HashMap<>();
            RequestBody body = null;
            String filePath;

            filePath = imagePath.replace("file:/", "");
            File file = new File(filePath);
            body = RequestBody.create(MediaType.parse("image/*"), file);

            task.put("id", createPartFromString(roomId + ""));

            rx.Observable<GetRoomResponse> observable = repository.getApiService().addRoomImage(body, task);
            new BaseCallback<GetRoomResponse>(RoomInfoActivity.this, observable, true) {
                @Override
                public void onSuccess(GetRoomResponse response) {

                    navigateToRoom(response.getData());
                }

                @Override
                public void onFail() {
//
                }
            };
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        if (descriptionString == null)
            return RequestBody.create(MultipartBody.FORM, "");
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);

    }

    @Override
    public void onPermissionGiven() {
        isPermissionGiven = true;
        mBinding.bottomSheetMembers.showWithSheetView(bottomSheetBinding.getRoot());

    }
}

