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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityAddDescriptionBinding;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.AddEditNote;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
/*
* Activity for add desc.
 */
public class AddNotesDescriptionActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private ActivityAddDescriptionBinding mBinding;
    private int mCharRemaining;
    private RetrofitRestRepository mRepository;
    private NotesModel mNotes;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(AddNotesDescriptionActivity.this, R.layout.activity_add_description);

        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, "SELL NOTES", "Next");

        mNotes = getIntent().getExtras().getParcelable(Constants.NOTES);

        //set click listeners
        mBinding.header.tvHeaderEnd.setOnClickListener(this);

        mBinding.etDescription.addTextChangedListener(this);
        mBinding.tvRemainingCharacters.setText(String.format(getString(R.string.characters_max), Constants.MAX_CHARACTERS_ADD_DESC));

        if (mNotes.getDetails() != null) {
            setExistingDescription(mNotes);
        }

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_header_end:
                nextClicked();
                break;
            default:
                break;
        }
    }

    private void nextClicked() {

        showProgressBar();

        AddEditNote addEditNoteRequest = new AddEditNote();
        addEditNoteRequest.setId(mNotes.getId());
        addEditNoteRequest.setClassName(mNotes.getClassName());
        addEditNoteRequest.setTitle(mNotes.getTitle());
        addEditNoteRequest.setPrice(mNotes.getPrice());
        addEditNoteRequest.setDetails(mBinding.etDescription.getText().toString().trim());

        new BaseCallback<NoteResponse>(this, mRepository.getApiService().addEditNotes(addEditNoteRequest)) {

            @Override
            public void onSuccess(NoteResponse response) {
                Bundle bundle = new Bundle();
                mNotes.setDetails(response.getData().getDetails());
                mNotes.setId(response.getData().getId());
                bundle.putParcelable(Constants.NOTES, mNotes);
                ExplicitIntent.getsInstance().navigateTo(AddNotesDescriptionActivity.this, UploadAttachmentsActivity.class, bundle);
            }

            @Override
            public void onFail() {
                showToast("Failed");
            }
        };

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if (mBinding.etDescription.getText().toString().trim().length() > 0) {
            mBinding.header.tvHeaderEnd.setEnabled(true);
        } else {
            mBinding.header.tvHeaderEnd.setEnabled(false);
        }

        //calculate remaining characters
        mCharRemaining = Constants.MAX_CHARACTERS_ADD_DESC - charSequence.length();
        updateCharactersRemaining();
    }

    @Override
    public void afterTextChanged(Editable editable) {
//
    }

    private void updateCharactersRemaining() {

        //if no character is entered, reset remaining characters string
        if (mCharRemaining == Constants.MAX_CHARACTERS_ADD_DESC) {
            mBinding.tvRemainingCharacters.setText(String.format(getString(R.string.characters_max), Constants.MAX_CHARACTERS_ADD_DESC));
        } else if (mCharRemaining == 1) {
            // 'characters' replaced by 'character'
            mBinding.tvRemainingCharacters.setText(String.format(getString(R.string.character_remaining), mCharRemaining));
        } else {
            mBinding.tvRemainingCharacters.setText(String.format(getString(R.string.characters_remaining), mCharRemaining));
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getExtras() != null) {
            setExistingDescription(intent.getExtras().getParcelable(Constants.NOTES));
        }
    }

    private void setExistingDescription(NotesModel notesModel) {
        mBinding.etDescription.setText(notesModel.getDetails());
        mBinding.etDescription.setSelection(mBinding.etDescription.getText().toString().trim().length());

        mCharRemaining = Constants.MAX_CHARACTERS_ADD_DESC - mBinding.etDescription.getText().toString().trim().length();
        updateCharactersRemaining();
    }
}
