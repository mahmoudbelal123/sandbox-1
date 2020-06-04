/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.adapter.AttachmentsAdapter;
import com.appster.turtle.adapter.GalleryAdapter;
import com.appster.turtle.adapter.viewholder.AttachmentsViewHolder;
import com.appster.turtle.databinding.ActivityUploadAttachmentsBinding;
import com.appster.turtle.model.Attachment;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.RemoveAttachmentRequest;
import com.appster.turtle.network.response.AttachmentResponse;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.network.response.VerifyEmailResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.media.RectangleCropperActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.ImagePickerUtils;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.appster.turtle.util.LogUtils.LOGE;
import static com.appster.turtle.util.LogUtils.LOGI;
import static com.appster.turtle.util.LogUtils.LOGW;
/*
* Activity for uplaod  note attachments
 */
public class UploadAttachmentsActivity extends BaseActivity implements View.OnClickListener,
        AttachmentsViewHolder.IAttachmentDeleted,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ImagePickerUtils.OnImagePickerListener, BaseActivity.PermissionI {

    private GoogleApiClient mGoogleApiClient;
    private ActivityUploadAttachmentsBinding mBinding;
    private NotesModel mNotes;

    private List<Attachment> mAttachmentsList;

    private AttachmentsAdapter mAttachmentsAdapter;
    private int attachmentCount = 0, pos;
    private boolean isEdit, isNoteSelected = false, isSnipetSelected = false, isNewAdded;


    /**
     * Request code for auto Google Play Services error resolution.
     */
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_FILE_SELECT = 2;
    private RetrofitRestRepository mRepository;
    private boolean isPermission;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(UploadAttachmentsActivity.this, R.layout.activity_upload_attachments);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }


        initUI();
    }

    @SuppressLint("StringFormatMatches")
    private void initUI() {
        setUpHeader(true, mBinding.header.clHeader, getString(R.string.create_notes), getString(R.string.next));
        mBinding.header.tvHeaderEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_bg_grey));
        mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.app_white));
        mBinding.header.clHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.white_alpha_upr_rounded));
        //set notes data

        isNewAdded = getIntent().getBooleanExtra(Constants.IS_BACK, false);

        isEdit = getIntent().getBooleanExtra(Constants.IS_EDIT, false);
        pos = getIntent().getIntExtra(Constants.POS, 0);
        if (!isNewAdded) {
            mNotes = getIntent().getExtras().getParcelable(Constants.NOTES);
        }

        if (mNotes != null && mNotes.getAttachments() != null) {
            mAttachmentsList = mNotes.getAttachments();
            attachmentCount = mNotes.getAttachments().size();
        } else {
            mAttachmentsList = new ArrayList<>();
            attachmentCount = 0;
        }
        //set click listeners
        mBinding.header.tvHeaderEnd.setOnClickListener(this);
        mBinding.tvNotes.setOnClickListener(this);
        mBinding.tvSnippet.setOnClickListener(this);
        mBinding.ibSnippet.setOnClickListener(this);
        mBinding.ivSnippet.setRadius(44.5f);
        mBinding.tvNotesPrice.setText(new StringBuffer("$").append(mNotes.getFormattedPrice()));

        mBinding.tvDescHeaderText.setOnClickListener(this);

        mBinding.setNoteModel(mNotes);

        mBinding.tvDescHeaderText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (mBinding.cvSnippetImages.getVisibility() == View.VISIBLE)
                    mBinding.cvSnippetImages.setVisibility(View.GONE);
            }
        });

        if (isEdit || isNewAdded) {
            mBinding.tvDescHeaderText.setText(mNotes.getDetails());
            mBinding.ivSnippet.setVisibility(View.VISIBLE);
            mBinding.tvSnippet.setVisibility(View.GONE);
            mBinding.ibSnippet.setVisibility(View.VISIBLE);
            try {
                GlideApp.with(UploadAttachmentsActivity.this)
                        .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + mNotes.getSnippetUrl()))
                        .centerCrop()
                        .placeholder(R.drawable.white_alpha_selector_bg_radius_21)
                        .into(mBinding.ivSnippet);
            } catch (MalformedURLException e) {
                LogUtils.printStackTrace(e);
            }


        }
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setAutoMeasureEnabled(true);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvAttachments.setLayoutManager(manager);
        mBinding.rvAttachments.setNestedScrollingEnabled(false);
        mBinding.rvAttachments.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(this, 20)));
        mAttachmentsAdapter = new AttachmentsAdapter(this, mAttachmentsList, this);
        mBinding.rvAttachments.setAdapter(mAttachmentsAdapter);

        mBinding.tvWordRemaining.setText(String.format(getString(R.string.characters_remaining1), (200 - mBinding.tvDescHeaderText.getText().length())));

        mBinding.tvDescHeaderText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                validateNotes();

            }

            @SuppressLint("StringFormatMatches")
            @Override
            public void afterTextChanged(Editable editable) {
                mBinding.tvWordRemaining.setText(String.format(getString(R.string.characters_remaining1), (200 - editable.length())));

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_header_end:
                nextClicked();
                break;

            case R.id.tv_notes:
                isNoteSelected = true;
                isSnipetSelected = false;
                if (attachmentCount <= 9) {
                    uploadNotes();
                } else {
                    showSnackBar(getString(R.string.error_attactments));

                }
                break;
            case R.id.tv_snippet:
                isNoteSelected = false;
                isSnipetSelected = true;
                showRecentImages();
                break;

            case R.id.ib_snippet:
                removeSnippet();
                break;

            default:
                break;

        }
    }

    private void removeSnippet() {
        Alert.createYesNoAlert(this, getString(R.string.remove_snippet), getString(R.string.msg_snippet), new Alert.OnAlertClickListener() {
            @Override
            public void onPositive(DialogInterface dialog) {
                mBinding.ivSnippet.setVisibility(View.GONE);
                mBinding.ibSnippet.setVisibility(View.INVISIBLE);
                mBinding.tvSnippet.setVisibility(View.VISIBLE);
                isSnipetSelected = false;
                validateNotes();
                dialog.dismiss();
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();
            }
        }).show();

    }

    @Override
    public void onBackPressed() {
        if (mBinding.cvSnippetImages.getVisibility() == View.VISIBLE)
            mBinding.cvSnippetImages.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    private void showRecentImages() {
        if (isPermission) {
            mBinding.cvSnippetImages.setVisibility(View.VISIBLE);

            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mBinding.rvSnippetImages.setHasFixedSize(true);
            mBinding.rvSnippetImages.setLayoutManager(mLinearLayoutManager);


            GalleryAdapter galleryAdapter = new GalleryAdapter(UploadAttachmentsActivity.this, false);

            if (galleryAdapter.getItemCount() == 0) {
                showSnackBar(getString(R.string.no_image));
                mBinding.cvSnippetImages.setVisibility(View.GONE);
            }

            mBinding.rvSnippetImages.setAdapter(galleryAdapter);
        } else {
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);
        }
    }

    public void selectGalleryItem(String imageUri) {
        Bundle b = new Bundle();
        b.putString(Constants.BundleKey.IMAGE_URL, imageUri);
        ExplicitIntent.getsInstance().navigateForResult(UploadAttachmentsActivity.this, RectangleCropperActivity.class, Constants.REQUEST_CODE.REQUEST_CROPPER, b);
    }

    private void nextClicked() {

        mNotes.setDetails(mBinding.tvDescHeaderText.getText().toString());
        new BaseCallback<NoteResponse>(this, mRepository.getApiService().addNotes(mNotes), true) {

            @Override
            public void onSuccess(NoteResponse response) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.PurchaseNotes.FROM_POST_NOTES, true);
                bundle.putParcelable(Constants.NOTES, response.getData());
                bundle.putBoolean(Constants.IS_EDIT, isEdit);
                if (isEdit)
                    bundle.putInt(Constants.POS, pos);
                ExplicitIntent.getsInstance().navigateTo(UploadAttachmentsActivity.this, NotesFinalActivity.class, bundle);
            }

            @Override
            public void onFail() {
                showToast("Failed");
            }
        };

    }

    private void uploadNotes() {
        View filePickerView = LayoutInflater.from(UploadAttachmentsActivity.this).inflate(R.layout.layout_upload_notes_bs, mBinding.bsUploadNotes, false);
        mBinding.bsUploadNotes.setShouldDimContentView(true);
        mBinding.bsUploadNotes.showWithSheetView(filePickerView);

        filePickerView.findViewById(R.id.iv_drive).setOnClickListener(view -> {
            showFilesListFromDrive();
            mBinding.bsUploadNotes.dismissSheet();
        });

        filePickerView.findViewById(R.id.iv_gallery).setOnClickListener(view -> {
            fetchImageFromCameraOrGallery();
            mBinding.bsUploadNotes.dismissSheet();
        });
    }


    private void showFilesListFromDrive() {
        mGoogleApiClient.connect();
        // create new contents resource
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);

    }

    /**
     * This is Result result handler of Drive contents.
     * Call selectFileFromGoogleDrive() method, send intent onActivityResult() method to handle result.
     */
    private final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            result -> {

                if (result.getStatus().isSuccess()) {
                    selectFileFromGoogleDrive();
                }
            };

    private void selectFileFromGoogleDrive() {

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"image/png", "image/jpg", "image/jpeg", "application/pdf"})
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_FILE_SELECT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            LOGW(getActivityName(), "Unable to send intent", e);
        }
    }

    private void fetchImageFromCameraOrGallery() {
        if (isPermission)
            ImagePickerUtils.add(false, getSupportFragmentManager(), UploadAttachmentsActivity.this);
        else
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);
    }

    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_CODE_RESOLUTION:
                    mGoogleApiClient.connect();
                    break;


                case REQUEST_CODE_FILE_SELECT:
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    String resourceId = driveId.getResourceId();
                    DriveFile file = driveId.asDriveFile();
                    file.getMetadata(getGoogleApiClient()).setResultCallback(metadataCallback);
                    break;

                case Constants.REQUEST_CODE.REQUEST_CROPPER:
                    Bundle request = data.getExtras();
                    String imagePath = request.getString(Constants.BundleKey.IMAGE_URL);
                    uploadSnippet(imagePath);
                    break;
                default:
                    break;
            }
        }

    }

    private void uploadSnippet(String imagePath) {

        if (imagePath.isEmpty()) {
            showToast("Error in fetching snippet");
            return;
        }

        showProgressBar();

        HashMap<String, RequestBody> task = new HashMap<>();
        RequestBody body = null;
        String filePath;
        showProgressBar();

        filePath = imagePath.replace("file:/", "");
        File file = new File(filePath);
        body = RequestBody.create(MediaType.parse("image/*"), file);

        task.put("id", createPartFromString(mNotes.getId() + ""));

        rx.Observable<NoteResponse> observable = mRepository.getApiService().addSnippet(body, task);
        new BaseCallback<NoteResponse>(UploadAttachmentsActivity.this, observable, true) {
            @Override
            public void onSuccess(NoteResponse response) {
                if (isEdit) {
                    mNotes.setSnippetUrl(response.getData().getSnippetUrl());
                } else {
                    mNotes = response.getData();
                }
                mBinding.ivSnippet.setVisibility(View.VISIBLE);
                mBinding.tvSnippet.setVisibility(View.GONE);
                mBinding.ibSnippet.setVisibility(View.VISIBLE);
                try {
                    GlideApp.with(UploadAttachmentsActivity.this)
                            .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + response.getData().getSnippetUrl()))
                            .centerCrop()
                            .placeholder(R.drawable.white_alpha_selector_bg_radius_21)
                            .into(mBinding.ivSnippet);
                } catch (MalformedURLException e) {
                    LogUtils.printStackTrace(e);
                }
                mBinding.cvSnippetImages.setVisibility(View.GONE);
                validateNotes();
            }

            @Override
            public void onFail() {
                mBinding.ivSnippet.setVisibility(View.GONE);
                mBinding.cvSnippetImages.setVisibility(View.GONE);
                mBinding.tvSnippet.setVisibility(View.VISIBLE);
                mBinding.ibSnippet.setVisibility(View.GONE);
                hideProgressBar();
            }
        };
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    final private ResultCallback<DriveResource.MetadataResult> metadataCallback = result -> {
        if (!result.getStatus().isSuccess()) {
            showToast(getString(R.string.problem_to_fetch_metadata));
            return;
        }

        Metadata metadata = result.getMetadata();

        if (!metadata.isShared()) {
            showToast(getString(R.string.enable_share_file));
            return;
        }

        if (metadata.getMimeType().contains("pdf")) {
            uploadDrivePdfAttachment(metadata);
        } else if (metadata.getMimeType().contains("image")) {
            uploadDriveImageAttachment(metadata);
        }
    };


    /**
     * Called when activity gets invisible. Connection to Drive service needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LOGI(getActivityName(), "GoogleApiClient connected");
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        LOGI(getActivityName(), "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        LOGI(getActivityName(), "GoogleApiClient connection failed: " + result.toString());

        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            LOGE(getActivityName(), "Exception while starting resolution activity", e);
        }
    }

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    private GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void success(String name, String path) {
        //Image picked from gallery succesfully
        uploadGalleryImageAttachment(path);
    }

    private void uploadGalleryImageAttachment(String imagePath) {
        //update mAttachmentsAdapter on successful upload and validateNotes()

        if (imagePath != null && !imagePath.isEmpty()) {
            try {

                showProgressBar();

                HashMap<String, RequestBody> task = new HashMap<>();
                RequestBody body = null;
                String filePath;

                filePath = imagePath.replace("file:/", "");
                File file = new File(filePath);
                body = RequestBody.create(MediaType.parse("image/*"), file);

                task.put("id", createPartFromString(String.valueOf(mNotes.getId())));
                task.put("attachementUrl", createPartFromString(null));
                task.put("orignalFileName", createPartFromString(file.getName()));
                task.put("uploadType", createPartFromString(String.valueOf(Constants.ATTACHMENT.FILE)));

                rx.Observable<AttachmentResponse> observable = mRepository.getApiService().addAttachment(body, task);
                new BaseCallback<AttachmentResponse>(UploadAttachmentsActivity.this, observable, true) {
                    @Override
                    public void onSuccess(AttachmentResponse response) {
                        //update mAttachmentsAdapter on successful upload
                        attachmentCount++;
                        mAttachmentsList.add(response.getData());
                        mNotes.setAttachments(mAttachmentsList);
                        mAttachmentsAdapter.notifyDataSetChanged();
                        validateNotes();
                    }

                    @Override
                    public void onFail() {
                        showToast("Fail");
                    }
                };
            } catch (Exception e) {
                LogUtils.printStackTrace(e);
            }
        }
    }


    private void uploadDriveImageAttachment(Metadata metadata) {

        showProgressBar();

        HashMap<String, RequestBody> task = new HashMap<>();
        RequestBody body = null;
        task.put("id", createPartFromString(String.valueOf(mNotes.getId())));
        task.put("attachementUrl", createPartFromString(metadata.getWebContentLink()));
        if (metadata.getMimeType().contains("pdf") && !metadata.getTitle().endsWith(".pdf"))
            task.put("orignalFileName", createPartFromString(metadata.getTitle() + ".pdf"));
        else
            task.put("orignalFileName", createPartFromString(metadata.getTitle()));
        task.put("uploadType", createPartFromString(String.valueOf(Constants.ATTACHMENT.URL)));

        rx.Observable<AttachmentResponse> observable = mRepository.getApiService().addAttachment(body, task);
        new BaseCallback<AttachmentResponse>(UploadAttachmentsActivity.this, observable, true) {
            @Override
            public void onSuccess(AttachmentResponse response) {
                //update mAttachmentsAdapter on successful upload
                attachmentCount++;
                mAttachmentsList.add(response.getData());
                mNotes.setAttachments(mAttachmentsList);
                mAttachmentsAdapter.notifyDataSetChanged();
                validateNotes();
            }

            @Override
            public void onFail() {
                showToast("Fail");
            }
        };
    }

    private void uploadDrivePdfAttachment(Metadata metadata) {
        //image and pdf from drive upload have same parameters as of now so call same function
        uploadDriveImageAttachment(metadata);
    }

    @Override
    protected void onResume() {
        super.onResume();
        validateNotes();
        setupUI(mBinding.bsUploadNotes);
    }

    private void validateNotes() {
        if (attachmentCount == 0 || mBinding.tvDescHeaderText.getText().toString().isEmpty() || mBinding.ivSnippet.getVisibility() == View.GONE) {
            mBinding.header.tvHeaderEnd.setEnabled(false);

        } else {
            mBinding.header.tvHeaderEnd.setEnabled(true);
        }
    }

    @Override
    public void fail(String message) {
        showToast("Failed to fetch Image");
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        if (descriptionString == null)
            return RequestBody.create(MultipartBody.FORM, "");
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);

    }

    @Override
    public void removeAttachment(final int position) {

        RemoveAttachmentRequest request = new RemoveAttachmentRequest();
        request.setId(mAttachmentsList.get(position).getId());
        request.setNotesId(mNotes.getId());

        showProgressBar();

        new BaseCallback<VerifyEmailResponse>(this, mRepository.getApiService().remAttachment(request), true) {

            @Override
            public void onSuccess(VerifyEmailResponse response) {
                attachmentCount--;
                mAttachmentsList.remove(position);
                mNotes.setAttachments(mAttachmentsList);
                mAttachmentsAdapter.notifyDataSetChanged();
                validateNotes();

            }

            @Override
            public void onFail() {
                showToast("Failed");
            }
        };

        validateNotes();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getExtras() != null) {
            setExistingNotesData(intent.getExtras().getParcelable(Constants.NOTES));
        }
    }

    private void setExistingNotesData(NotesModel notes) {
        mNotes = notes;
        initUI();
    }

    @Override
    public void onPermissionGiven() {
        isPermission = true;
        if (isNoteSelected && !isSnipetSelected)
            fetchImageFromCameraOrGallery();
        else
            showRecentImages();
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard();
                v.clearFocus();
                return false;
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
