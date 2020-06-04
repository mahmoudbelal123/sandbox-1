/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes.editNotes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.AttachmentsAdapter;
import com.appster.turtle.adapter.GalleryAdapter;
import com.appster.turtle.adapter.viewholder.AttachmentsViewHolder;
import com.appster.turtle.databinding.ActivityEditNotesBinding;
import com.appster.turtle.model.Attachment;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.media.RectangleCropperActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.Utils;
import com.bumptech.glide.signature.ObjectKey;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.appster.turtle.util.LogUtils.LOGE;
/*
* Activity for edit note
 */
public class EditNotesActivity extends BaseActivity implements View.OnClickListener,
        AttachmentsViewHolder.IAttachmentDeleted, BaseActivity.PermissionI {

    ActivityEditNotesBinding mBinding;
    private NotesModel mNotes;
    private RetrofitRestRepository mRepository;
    private AttachmentsAdapter mAttachmentsAdapter;
    private boolean isPermission;
    private List<Attachment> mAttachmentsList;
    private ArrayList<Integer> mRemovedAttachmentsList;
    private RequestBody mSnippetBody;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_notes);

        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, "MY NOTES");

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        mNotes = (NotesModel) getIntent().getExtras().get(Constants.BundleKey.NOTES);
        mRemovedAttachmentsList = new ArrayList<>();

        mBinding.header.ivIconStart.setOnClickListener(this);
        mBinding.ivEditDesc.setOnClickListener(this);
        mBinding.tvNotes.setOnClickListener(this);
        mBinding.rlSnippet.setOnClickListener(this);
        mBinding.tvSnippet.setOnClickListener(this);
        mBinding.tvSaveEditedNotes.setOnClickListener(this);

        setNotesInViews();
    }

    private void setNotesInViews() {

        //title, class, description, snippet
        mBinding.setNotes(mNotes);

        //set attachments
        mBinding.rvAttachments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvAttachments.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(this, 20)));
        mAttachmentsList = mNotes.getAttachments();
        mAttachmentsAdapter = new AttachmentsAdapter(this, mAttachmentsList, this, false);
        mBinding.rvAttachments.setAdapter(mAttachmentsAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_start:
                showAreYouSureAlert();
                break;

            case R.id.rl_snippet:
                showRecentImages();
                break;

            case R.id.tv_snippet:
                showRecentImages();
                break;

            case R.id.iv_edit_desc:
                Bundle b = new Bundle();
                b.putParcelable(Constants.BundleKey.NOTES, mNotes);
                ExplicitIntent.getsInstance().navigateForResult(this, EditNotesDetailsActivity.class, Constants.REQUEST_CODE.REQUEST_NOTES_DETAILS, b);
                break;

            case R.id.tv_notes:
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.NOTES, mNotes);
                ExplicitIntent.getsInstance().navigateForResult(this, EditNotesAttachmentsActivity.class, Constants.REQUEST_CODE.REQUEST_NOTES_ATTACHMENTS, bundle);
                break;

            case R.id.tv_save_edited_notes:
                saveNotes();
                break;

                default:
                    break;
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        if (descriptionString == null)
            return RequestBody.create(MultipartBody.FORM, "");
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);

    }

    private void saveNotes() {

        StringBuilder removedAttachmentsIdsString = new StringBuilder();
        for (int i = 0; i < mRemovedAttachmentsList.size(); i++) {

            removedAttachmentsIdsString.append(mRemovedAttachmentsList.get(i));

            //if last element, don't append comma
            if (i == mRemovedAttachmentsList.size() - 1) {
                break;
            } else {
                removedAttachmentsIdsString.append(",");
            }
        }

        HashMap<String, RequestBody> map = new HashMap<>();

        map.put("id", createPartFromString(mNotes.getId() + ""));
        map.put("title", createPartFromString(mNotes.getTitle()));
        map.put("details", createPartFromString(mNotes.getDetails()));
        map.put("className", createPartFromString(mNotes.getClassName()));

        ArrayList<Attachment> mNewAttachments = new ArrayList<>();
        mNewAttachments.addAll(mAttachmentsList);

        for (Attachment attachment : mAttachmentsList) {
            if (attachment.getId() != 0) {
                mNewAttachments.remove(attachment);
            }
        }

        for (int i = 0; i < mNewAttachments.size(); i++) {
            Attachment attachment = mNewAttachments.get(i);

            RequestBody attachmentRequestBody;
            RequestBody attachmentUrlRequestBody;
            RequestBody uploadTypeRequestBody;

            //attachment id
            map.put("newAttachments[" + i + "].id", createPartFromString(String.valueOf(attachment.getId())));

            //original file name
            map.put("newAttachments[" + i + "].orignalFileName", createPartFromString(attachment.getFileName()));

            //attachment file
            attachmentRequestBody = (attachment.getFile() != null ? RequestBody.create(MediaType.parse("image/*"), attachment.getFile()) : createPartFromString(""));
            map.put("newAttachments[" + i + "].attachment\"; filename=\"newAttachments[" + i + "].attachment.jpg", attachmentRequestBody);

            //attachment url
            attachmentUrlRequestBody = (attachment.getFileUrl() != null ? createPartFromString(attachment.getFileUrl()) : createPartFromString(""));
            map.put("newAttachments[" + i + "].attachementUrl", attachmentUrlRequestBody);

            //uploadType
            uploadTypeRequestBody = (attachment.getUploadType() == null ? createPartFromString("0") : createPartFromString(attachment.getUploadType() + ""));
            map.put("newAttachments[" + i + "].uploadType", uploadTypeRequestBody);
        }

        new BaseCallback<NoteResponse>(this, mRepository.getApiService().editNotes(removedAttachmentsIdsString.toString(), map, mSnippetBody), true) {

            @Override
            public void onSuccess(NoteResponse response) {
                Intent i = new Intent();
                i.setAction("notes.review");
                sendBroadcast(i);
                finish();
            }

            @Override
            public void onFail() {
//                LOGV(getActivityName(), "Failed");
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE.REQUEST_CROPPER:
                    Bundle request = data.getExtras();
                    String imagePath = request.getString(Constants.BundleKey.IMAGE_URL);
                    LOGE("Cropper", imagePath + "");

                    GlideApp.with(EditNotesActivity.this)
                            .load(Uri.parse(imagePath))
                            .signature(new ObjectKey(UUID.randomUUID().toString()))
                            .into(mBinding.ivSnippet);

                    mBinding.cvSnippetImages.setVisibility(View.GONE);

                    File file = new File(imagePath.replace("file:/", ""));
                    mSnippetBody = RequestBody.create(MediaType.parse("image/*"), file);

                    break;

                case Constants.REQUEST_CODE.REQUEST_NOTES_DETAILS:
                    if (data.getExtras() != null) {
                        mNotes = data.getExtras().getParcelable(Constants.NOTES);
                        mBinding.setNotes(mNotes);
                    }
                    break;

                case Constants.REQUEST_CODE.REQUEST_NOTES_ATTACHMENTS:
                    if (data.getExtras() != null) {
                        mNotes = data.getExtras().getParcelable(Constants.NOTES);
                        ArrayList<Integer> removedIdsList = data.getExtras().getIntegerArrayList(Constants.BundleKey.EDIT_NOTES_REMOVED_ATTACHMENTS);
                        if (removedIdsList != null)
                            mRemovedAttachmentsList.addAll(removedIdsList);
                    }

                    mAttachmentsList.clear();
                    mAttachmentsList.addAll(mNotes.getAttachments());
                    mAttachmentsAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void showRecentImages() {

        if (isPermission) {
            mBinding.cvSnippetImages.setVisibility(View.VISIBLE);

            mBinding.rvSnippetImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            GalleryAdapter galleryAdapter = new GalleryAdapter(this, true);
            if (galleryAdapter.getItemCount() == 0) {
                showSnackBar("No recent images available");
            }

            mBinding.rvSnippetImages.setAdapter(galleryAdapter);
        } else {
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);
        }
    }

    public void selectGalleryItem(String imageUri) {
        Bundle b = new Bundle();
        b.putString(Constants.BundleKey.IMAGE_URL, imageUri);
        ExplicitIntent.getsInstance().navigateForResult(this, RectangleCropperActivity.class, Constants.REQUEST_CODE.REQUEST_CROPPER, b);
    }

    @Override
    public void removeAttachment(int position) {
//
    }

    @Override
    public void onPermissionGiven() {
        isPermission = true;
        showRecentImages();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.cvSnippetImages.getVisibility() == View.VISIBLE)
            mBinding.cvSnippetImages.setVisibility(View.GONE);
        else
            showAreYouSureAlert();
    }

    private void showAreYouSureAlert() {
        Alert.createYesNoAlert(EditNotesActivity.this, null, "Are you sure you want to cancel updating notes?", new Alert.OnAlertClickListener() {
            @Override
            public void onPositive(DialogInterface dialog) {
                finish();
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();
            }
        }).show();

    }

}
