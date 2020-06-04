package com.appster.turtle.ui.rooms;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityEditRoomBinding;
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
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.ImagePickerUtils;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.appster.turtle.util.bindingadapters.CameraBindingAdapters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditRoomActivity extends BaseActivity implements View.OnClickListener, ImagePickerUtils.OnImagePickerListener, BaseActivity.PermissionI {

    private ActivityEditRoomBinding mBinding;
    private Bundle mBundle;
    private Room room;
    private LayoutImageSelectorBinding bottomSheetBinding;
    private String imagePath = "";
    private ArrayList<Tag> tagList = new ArrayList<>();
    private ArrayList<Integer> memberIDs;
    private RetrofitRestRepository repository;
    private boolean isUpdated = false;
    private boolean isMemberUpdated = false;
    private boolean isPermissionGiven;
    private boolean mMembersCanPost;
    private Dialog privacyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(EditRoomActivity.this, R.layout.activity_edit_room);

        setUpHeader(false, mBinding.clHeader.clHeader, getString(R.string.edit_space), getString(R.string.save), R.drawable.back_arrow);

        initUI();


    }

    private void initUI() {

        mBundle = getIntent().getExtras();

        if (mBundle != null) {
            room = mBundle.getParcelable(Constants.BundleKey.ROOM);
            mBinding.setRoom(room);
        }

        getRoom();

        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(EditRoomActivity.this), R.layout.layout_image_selector, mBinding.bottomSheetMembers, false);

        bottomSheetBinding.tvTakePhoto.setOnClickListener(this);
        bottomSheetBinding.tvCameraRoll.setOnClickListener(this);
        bottomSheetBinding.tvCancel.setOnClickListener(this);

        mBinding.clHeader.tvHeaderEnd.setEnabled(true);
        mBinding.clHeader.tvHeaderEnd.setOnClickListener(this);

        mBinding.clEditRoomTags.ivRoomType.setImageDrawable(null);
        mBinding.clMembers.ivRoomType.setImageDrawable(null);
        mBinding.clPrivacy.ivRoomType.setImageDrawable(null);

        mBinding.etRoomDesc.addTextChangedListener(descTextWatcher);


        setClickListeners();
    }

    private TextWatcher descTextWatcher = new TextWatcher() {
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
            String currentText = editable.toString();
            int CHAR_DESC_COUNT = 140;
            int remaining = CHAR_DESC_COUNT - currentText.length();
            mBinding.tvDescLength.setText(getResources().getQuantityString(R.plurals.char_remaining, remaining, remaining));


        }
    };


    private void setClickListeners() {

        mBinding.clEditRoomTags.clRoomType.setOnClickListener(view -> addTags());

        mBinding.clMembers.clRoomType.setOnClickListener(view -> {
            mBundle.putBoolean(Constants.BundleKey.FROM_EDIT_ROOM, true);
            mBundle.putParcelable(Constants.BundleKey.ROOM, room);
            mBundle.putBoolean(Constants.BundleKey.IS_ONLY_MEMBERS, false);
            ExplicitIntent.getsInstance().navigateForResult(EditRoomActivity.this, MemberRequestActivity.class, Constants.REQUEST_CODE.REQUEST_ROOM_MEMBER, mBundle);


        });

        mBinding.clPrivacy.clRoomType.setOnClickListener(view -> {

            showPrivacyDialog();
        });


        mBinding.clGlobalCampus.setOnClickListener(view -> mBinding.switchGlobalCampus.toggle());

        mBinding.clHeader.tvHeaderEnd.setOnClickListener(view -> {
            if (mBinding.etRoomName.getText().toString().trim().isEmpty()) {
                showSnackBar(getString(R.string.enter_valid_room_name));
                return;
            }

            if (mBinding.etRoomDesc.getText().toString().trim().isEmpty()) {
                showSnackBar(getString(R.string.enter_desc));
                return;
            }


            Alert.createYesNoAlert(EditRoomActivity.this, getString(R.string.edit_room), getString(R.string.edit_room_msg), new Alert.OnAlertClickListener() {
                @Override
                public void onPositive(DialogInterface dialog) {
                    showProgressBar();
                    if (imagePath.isEmpty())
                        updateRoom();
                    else
                        uploadRoomImage();
                }

                @Override
                public void onNegative(DialogInterface dialog) {
                    dialog.dismiss();
                }
            }).show();
        });

    }

    public void showImageSelector(View view) {
        if (isPermissionGiven)
            mBinding.bottomSheetMembers.showWithSheetView(bottomSheetBinding.getRoot());
        else
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);
    }


    @Override
    public String getActivityName() {
        return EditRoomActivity.class.getName();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_camera_roll:
                mBinding.bottomSheetMembers.dismissSheet();
                ImagePickerUtils.add(false, getSupportFragmentManager(), EditRoomActivity.this);
                break;

            case R.id.tv_take_photo:
                mBinding.bottomSheetMembers.dismissSheet();
                ImagePickerUtils.add(true, getSupportFragmentManager(), EditRoomActivity.this);
                break;

            case R.id.tv_cancel:
                mBinding.bottomSheetMembers.dismissSheet();
                break;


            case R.id.iv_private:
                ((ImageView) view).setImageResource(R.drawable.circle_tick_stroke);
                privacyDialog.dismiss();
                room.setRoomType(Constants.ROOM_TYPE.PRIVATE);
                mBinding.clPrivacy.tvRoomTypeDesc.setText(R.string.private_title);
                break;
            case R.id.iv_public:
                ((ImageView) view).setImageResource(R.drawable.circle_tick_stroke);
                privacyDialog.dismiss();
                room.setRoomType(Constants.ROOM_TYPE.PUBLIC);
                mBinding.clPrivacy.tvRoomTypeDesc.setText(R.string.public_title);
                break;
            case R.id.iv_hidden:
                ((ImageView) view).setImageResource(R.drawable.circle_tick_stroke);
                privacyDialog.dismiss();
                room.setRoomType(Constants.ROOM_TYPE.HIDDEN);
                mBinding.clPrivacy.tvRoomTypeDesc.setText(R.string.hidden_title);
                break;

            default:
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUpdatedMembersCount();
    }

    private void getUpdatedMembersCount() {
        repository = ((ApplicationController) getApplicationContext()).provideRepository();
        new BaseCallback<GetRoomResponse>(EditRoomActivity.this, repository.getApiService()
                .getRoom(room.getRoomId())) {
            @Override
            public void onSuccess(GetRoomResponse response) {
                if (response.getData() != null) {
                    mBinding.clMembers.tvRoomTypeDesc.setText(String.valueOf(response.getData().getMembersCount()) + " Members");
                }
            }

            @Override
            public void onFail() {
                isMemberUpdated = false;

            }
        };
    }

    private void setRoomType() {
//


        switch (room.getRoomType()) {
            case Constants.ROOM_TYPE.PRIVATE:
                mBinding.clPrivacy.tvRoomTypeDesc.setText(R.string.private_title);
                break;
            case Constants.ROOM_TYPE.PUBLIC:
                mBinding.clPrivacy.tvRoomTypeDesc.setText(R.string.public_title);
                break;
            case Constants.ROOM_TYPE.HIDDEN:
                mBinding.clPrivacy.tvRoomTypeDesc.setText(R.string.hidden_title);
                break;
        }

    }


    @Override
    public void success(String name, String path) {

        imagePath = path;


        setImageUrl();
    }

    @Override
    public void fail(String message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE.REQUEST_CROPPER:
                if (resultCode == RESULT_OK) {
                    Bundle request = data.getExtras();
                    imagePath = request.getString(Constants.BundleKey.IMAGE_URL);
                    setImageUrl();
                    isUpdated = true;
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_ADD_TAG:
                if (resultCode == RESULT_OK) {
                    tagList.clear();
                    tagList = data.getParcelableArrayListExtra(Constants.TAGS);
                    displayTags();
                    isUpdated = true;
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_ROOM_NAME:
                if (resultCode == RESULT_OK) {
                    String roomName = data.getExtras().getString(Constants.BundleKey.ROOM_NAME);
                    room.setRoomName(roomName);
                    isUpdated = true;
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_ROOM_PRIVACY:
                if (resultCode == RESULT_OK) {
                    room.setRoomType(data.getExtras().getInt(Constants.BundleKey.ROOM_TYPE));
                    isUpdated = true;
                }
                break;


            case Constants.REQUEST_CODE.REQUEST_ROOM_MEMBER:

                if (resultCode == RESULT_OK) {
                    isUpdated = true;
                    isMemberUpdated = true;
                    getRoom();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }

    private void setImageUrl() {
        Uri uri = Utils.getUri(EditRoomActivity.this, new File(imagePath));
        CameraBindingAdapters.bindCircleImageUri(mBinding.ivRoomImage, uri);
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

        mBinding.clEditRoomTags.tvRoomTypeDesc.setText(tagStr.toString());
    }

    private void addTags() {
        Intent i = new Intent(EditRoomActivity.this, AddTagsActivity.class);
        if (tagList.size() > 0)
            i.putParcelableArrayListExtra(Constants.TAGS, tagList);

        i.putExtra(Constants.IS_FROM_ROOM, true);
        startActivityForResult(i, Constants.REQUEST_CODE.REQUEST_ADD_TAG);
    }

    private void getRoom() {
        repository = ((ApplicationController) getApplicationContext()).provideRepository();
        new BaseCallback<GetRoomResponse>(EditRoomActivity.this, repository.getApiService()
                .getRoom(room.getRoomId())) {
            @Override
            public void onSuccess(GetRoomResponse response) {

                if (response.getData() != null) {

                    mBinding.clMembers.tvRoomTypeDesc.setText(String.valueOf(response.getData().getMembersCount()) + " Members");

                    if (isMemberUpdated) {
                        room.setMembersCount(response.getData().getMembersCount());
                        isMemberUpdated = false;
                        return;
                    }

                    room = response.getData();
                    mBinding.setRoom(room);

                    //set all data

                    tagList = room.getTags() != null ? room.getTags() : new ArrayList<>();
                    displayTags();

                    mMembersCanPost = room.isMemberCanPost();
                    mBinding.switchAllow.setChecked(!mMembersCanPost);
                    mBinding.switchGlobalCampus.setChecked(room.isGlobalRoom());


                    setRoomType();


                }

            }

            @Override
            public void onFail() {
                isMemberUpdated = false;

            }
        };
    }


    private void updateRoom() {


        showProgressBar();

        AddRoomRequest roomRequest = new AddRoomRequest();
        roomRequest.setMemberCanPost(!mBinding.switchAllow.isChecked());
        roomRequest.setPublicFlag(true);
        String roomName = mBinding.etRoomName.getText().toString().trim();
        if (roomName.isEmpty()) {
            showToast("Enter valid room name");
            return;
        }
        roomRequest.setRoomName(roomName.substring(0, 1).toUpperCase() + roomName.substring(1));
        roomRequest.setRoomType(room.getRoomType());
        roomRequest.setRoomDesc(mBinding.etRoomDesc.getText().toString());
        roomRequest.setTags(tagList);
        roomRequest.setSearchable(true);
        roomRequest.setMemberIds(memberIDs);
        roomRequest.setRoomId(room.getRoomId());
        roomRequest.setGlobalRoom(mBinding.switchGlobalCampus.isChecked());

        new BaseCallback<GetRoomResponse>(EditRoomActivity.this, repository.getApiService().addEditRoom(roomRequest)) {
            @Override
            public void onSuccess(final GetRoomResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {

                    Alert.createAlert(EditRoomActivity.this, "", getString(R.string.room_updated)).setOnDismissListener(dialog -> {

                        room = response.getData();
                        Bundle b = new Bundle();
                        b.putParcelable(Constants.BundleKey.ROOM, room);
                        Intent i = new Intent();
                        i.putExtras(b);
                        setResult(RESULT_OK, i);
                        finish();
                        EditRoomActivity.this.finish();

                    }).show();

                }
            }

            @Override
            public void onFail() {

                hideProgressBar();

            }
        };
    }

    @Override
    public void onBackPressed() {

        if (isUpdated) {
            Alert.createYesNoAlert(EditRoomActivity.this, getString(R.string.edit_room), getString(R.string.update_room), new Alert.OnAlertClickListener() {
                @Override
                public void onPositive(DialogInterface dialog) {
                    setResult(RESULT_CANCELED);
                    EditRoomActivity.this.finish();
                }

                @Override
                public void onNegative(DialogInterface dialog) {

                    dialog.dismiss();
                }
            }).show();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void showPrivacyDialog() {
        privacyDialog = new Dialog(EditRoomActivity.this);
        privacyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        privacyDialog.setCancelable(true);
        PrivacyDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(EditRoomActivity.this), R.layout.privacy_dialog, null, false);
        privacyDialog.setContentView(binding.getRoot());

        Window window = privacyDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        binding.ivPublic.setOnClickListener(EditRoomActivity.this);
        binding.ivPrivate.setOnClickListener(EditRoomActivity.this);
        binding.ivHidden.setOnClickListener(EditRoomActivity.this);

        switch (room.getRoomType()) {
            case Constants.ROOM_TYPE.PRIVATE:
                binding.ivPrivate.setImageResource(R.drawable.circle_tick_stroke);
                break;
            case Constants.ROOM_TYPE.PUBLIC:
                binding.ivPublic.setImageResource(R.drawable.circle_tick_stroke);
                break;
            case Constants.ROOM_TYPE.HIDDEN:
                binding.ivHidden.setImageResource(R.drawable.circle_tick_stroke);
                break;

            default:
                break;
        }

        privacyDialog.show();
    }


    private void uploadRoomImage() {

        if (imagePath.isEmpty()) {
            updateRoom();
            return;
        }

        try {
            @SuppressWarnings("unchecked") HashMap<String, RequestBody> task = new HashMap();
            RequestBody body = null;
            String filePath;
            showProgressBar();


            filePath = imagePath.replace("file:/", "");
            File file = new File(filePath);
            body = RequestBody.create(MediaType.parse("image/*"), file);

            task.put("id", createPartFromString(room.getRoomId() + ""));

            rx.Observable<GetRoomResponse> observable = repository.getApiService().addRoomImage(body, task);
            new BaseCallback<GetRoomResponse>(EditRoomActivity.this, observable, false) {
                @Override
                public void onSuccess(GetRoomResponse response) {

                    updateRoom();
                }

                @Override
                public void onFail() {

                    hideProgressBar();
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
