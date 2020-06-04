/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes.editNotes;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.adapter.AttachmentsAdapter;
import com.appster.turtle.adapter.viewholder.AttachmentsViewHolder;
import com.appster.turtle.adapter.viewholder.AttachmentsViewHolder.IAttachmentIdDeleted;
import com.appster.turtle.databinding.ActivityEditNotesAttachmentsBinding;
import com.appster.turtle.model.Attachment;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ImagePickerUtils;
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
import java.util.ArrayList;
import java.util.List;

import static com.appster.turtle.util.LogUtils.LOGE;
import static com.appster.turtle.util.LogUtils.LOGI;
import static com.appster.turtle.util.LogUtils.LOGW;
/*
* Activity for edit attactment
 */
public class EditNotesAttachmentsActivity extends BaseActivity implements IAttachmentIdDeleted,
        AttachmentsViewHolder.IAttachmentDeleted, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ImagePickerUtils.OnImagePickerListener, BaseActivity.PermissionI, View.OnClickListener {

    ActivityEditNotesAttachmentsBinding mBinding;
    private NotesModel mNotes;
    private AttachmentsAdapter mAttachmentsAdapter;
    private List<Attachment> mAttachmentsList;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Integer> mRemovedAttachmentsList;

    private boolean isPermission;

    /**
     * Request code for auto Google Play Services error resolution.
     */
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_FILE_SELECT = 2;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_notes_attachments);

        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, "MY NOTES", "Upload");
        mBinding.header.tvHeaderEnd.setEnabled(true);

        mRemovedAttachmentsList = new ArrayList<>();
        mNotes = getIntent().getExtras().getParcelable(Constants.BundleKey.NOTES);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mBinding.header.tvHeaderEnd.setOnClickListener(this);
        mBinding.tvDone.setOnClickListener(this);

        mBinding.rvAttachments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvAttachments.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(this, 20)));
        mAttachmentsList = mNotes.getAttachments();
        mAttachmentsAdapter = new AttachmentsAdapter(this, mAttachmentsList, this, this);
        mBinding.rvAttachments.setAdapter(mAttachmentsAdapter);

    }

    private void uploadNotes() {
        View filePickerView = LayoutInflater.from(EditNotesAttachmentsActivity.this).inflate(R.layout.layout_upload_notes_bs, mBinding.bsUploadNotes, false);
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
            ImagePickerUtils.add(false, getSupportFragmentManager(), EditNotesAttachmentsActivity.this);
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
            }
        }

    }

    final private ResultCallback<DriveResource.MetadataResult> metadataCallback = result -> {
        if (!result.getStatus().isSuccess()) {
            showToast("Problem while trying to fetch metadata");
            return;
        }

        Metadata metadata = result.getMetadata();

        if (!metadata.isShared()) {
            showToast("Please enable sharing for this file or select another file");
            return;
        }

        if (metadata.getMimeType().contains("pdf")) {
            uploadDrivePdfAttachment(metadata);
        } else if (metadata.getMimeType().contains("image")) {
            uploadDriveImageAttachment(metadata);
        } else {
            //can add other file types here later ons
        }
    };

    /**
     * Getter for the {@code GoogleApiClient}.
     */
    private GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void success(String name, String path) {
        //Image picked from gallery successfully
        uploadGalleryImageAttachment(path);
    }

    private void uploadGalleryImageAttachment(String imagePath) {
        //update mAttachmentsAdapter on successful upload and validateNotes()

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                String filePath;

                filePath = imagePath.replace("file:/", "");
                File file = new File(filePath);

                Attachment attachment = new Attachment();

                attachment.setId(0);
                attachment.setFile(file);
                attachment.setFileUrl(null);
                attachment.setFileName(file.getName());
                attachment.setUploadType(Constants.ATTACHMENT.FILE);

                mAttachmentsList.add(attachment);
                mAttachmentsAdapter.notifyDataSetChanged();

            } catch (Exception e) {
//                LOGV(getActivityName(), "Unable to get image");
                showSnackBar("Unable to get image");
            }
        }
    }

    private void uploadDriveImageAttachment(Metadata metadata) {

        Attachment attachment = new Attachment();

        String title;
        if (metadata.getMimeType().contains("pdf") && !metadata.getTitle().endsWith(".pdf"))
            title = metadata.getTitle() + ".pdf";
        else
            title = metadata.getTitle();

        attachment.setId(0);
        attachment.setFile(null);
        attachment.setFileUrl(metadata.getWebContentLink());
        attachment.setFileName(title);
        attachment.setUploadType(Constants.ATTACHMENT.URL);

        mAttachmentsList.add(attachment);
        mAttachmentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void fail(String message) {
        showToast("Failed to fetch Image");
    }

    private void uploadDrivePdfAttachment(Metadata metadata) {
        //image and pdf from drive upload have same parameters as of now so call same function
        uploadDriveImageAttachment(metadata);
    }


    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
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

    @Override
    public void onPermissionGiven() {
        isPermission = true;
        fetchImageFromCameraOrGallery();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_header_end:
                uploadNotes();
                break;

            case R.id.tv_done:

                if (mAttachmentsList.size() == 0) {
                    showSnackBar("Atleast 1 attachment required.");
                    return;
                }

                Intent i = getIntent();
                Bundle b = new Bundle();
                b.putIntegerArrayList(Constants.BundleKey.EDIT_NOTES_REMOVED_ATTACHMENTS, mRemovedAttachmentsList);
                mNotes.setAttachments(mAttachmentsList);
                b.putParcelable(Constants.BundleKey.NOTES, mNotes);
                i.putExtras(b);
                setResult(RESULT_OK, i);
                finish();
                break;
        }
    }

    @Override
    public void removeAttachmentWithId(int id) {
        if (id != 0) {
            mRemovedAttachmentsList.add(id);

            for (Attachment attachment : mAttachmentsList) {
                if (attachment.getId() == id) {
                    mAttachmentsList.remove(attachment);
                    mAttachmentsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void removeAttachment(int position) {
        mAttachmentsList.remove(position);
        mAttachmentsAdapter.notifyDataSetChanged();
    }
}
