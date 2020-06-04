/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.AttachmentsAdapter;
import com.appster.turtle.databinding.ActivityNotesFinalBinding;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.PostNoteRequest;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.Utils;
/*
* Activity for final note
 */
public class NotesFinalActivity extends BaseActivity implements View.OnClickListener {

    private ActivityNotesFinalBinding mBinding;
    private NotesModel mNotes;

    private AttachmentsAdapter mAttachmentsAdapter;
    private RetrofitRestRepository mRepository;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notes_final);
        mNotes = (NotesModel) getIntent().getExtras().get(Constants.NOTES);
        if (getIntent().hasExtra(Constants.IS_EDIT))
            isEdit = getIntent().getExtras().getBoolean(Constants.IS_EDIT);
        else
            isEdit = false;
        initUI();
    }

    private void initUI() {
        setUpHeader(true, mBinding.header.clHeader, getString(R.string.create_notes));
        mBinding.header.tvHeaderEnd.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_bg_grey));
        mBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(this, R.color.app_white));
        mBinding.header.clHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.white_alpha_upr_rounded));
        mBinding.setNotes(mNotes);

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setAutoMeasureEnabled(true);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvAttachments.setLayoutManager(manager);
        mBinding.rvAttachments.setNestedScrollingEnabled(false);
        mBinding.rvAttachments.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(this, 20)));
        mAttachmentsAdapter = new AttachmentsAdapter(this, mNotes.getAttachments());
        mBinding.rvAttachments.setAdapter(mAttachmentsAdapter);
        mBinding.ivSnippet.setRadius(44.5f);
        mBinding.tvPostedBy.setText(String.format(getString(R.string.posted_by_placeholder), mNotes.getPostedBy()));
        mBinding.tvPostNotes.setOnClickListener(this);
        if (isEdit) {
            mBinding.tvPostNotes.setText(getString(R.string.update_note));
        } else
            mBinding.tvPostNotes.setText(getString(R.string.post_notes));
    }

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_post_notes:
                uploadNotes();
                break;

            case R.id.iv_back:
                backPressed();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }

    private void backPressed() {
        Bundle b = new Bundle();
        b.putParcelable(Constants.NOTES, mNotes);
        b.putBoolean(Constants.IS_EDIT, isEdit);
        if (!isEdit) {
            b.putBoolean(Constants.IS_BACK, true);
        }
        ExplicitIntent.getsInstance().navigateTo(this, UploadAttachmentsActivity.class, b, Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    private void uploadNotes() {

        showProgressBar();
        PostNoteRequest request = new PostNoteRequest();
        request.setId(mNotes.getId());

        new BaseCallback<NoteResponse>(this, mRepository.getApiService().postNotes(request), true) {

            @Override
            public void onSuccess(NoteResponse response) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.PurchaseNotes.FROM_POST_NOTES, true);
                bundle.putParcelable(Constants.NOTES, response.getData());
                bundle.putBoolean(Constants.IS_EDIT, isEdit);
                if (isEdit) {
                    bundle.putInt(Constants.POS, getIntent().getIntExtra(Constants.POS, 0));
                } else {
                    bundle.putBoolean(Constants.IS_NEWADD, true);
                }
                ExplicitIntent.getsInstance().navigateTo(NotesFinalActivity.this, NotesListingActivity.class, bundle);
                finish();
            }

            @Override
            public void onFail() {
                showToast("Failed");
            }
        };
    }
}
