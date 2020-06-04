/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes.editNotes;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityEditNotesDetailsBinding;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.StringUtils;
/*
* Activity for edit detail note
 */
public class EditNotesDetailsActivity extends BaseActivity implements View.OnClickListener {

    ActivityEditNotesDetailsBinding mBinding;
    private NotesModel mNotes;
    private Bundle mBundle;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_notes_details);

        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, "MY NOTES");

        mBundle = getIntent().getExtras();
        mNotes = mBundle.getParcelable(Constants.BundleKey.NOTES);
        mBinding.setNotes(mNotes);

        mBinding.tvDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_done:

                String title = mBinding.etNotesTitle.getText().toString().trim();
                String className = mBinding.etNotesClass.getText().toString().trim();
                String description = mBinding.etNotesDescription.getText().toString().trim();

                if (StringUtils.isNullOrEmpty(title)) {
                    showSnackBar(getString(R.string.title_cannot_be_blank));
                    return;
                } else if (StringUtils.isNullOrEmpty(className)) {
                    showSnackBar(getString(R.string.class_cannot_be_blank));
                    return;
                } else if (StringUtils.isNullOrEmpty(description)) {
                    showSnackBar(getString(R.string.desc_cannot_be_blank));
                    return;
                } else if (description.length() > Constants.MAX_CHARACTERS_ADD_DESC) {
                    showSnackBar("Description cannot have more than " + Constants.MAX_CHARACTERS_ADD_DESC + " characters");
                    return;
                }

                mNotes.setTitle(title);
                mNotes.setClassName(className);
                mNotes.setDetails(description);

                Intent i = getIntent();
                mBundle.putParcelable(Constants.BundleKey.NOTES, mNotes);
                i.putExtras(mBundle);
                setResult(RESULT_OK, i);
                finish();
                break;


            default:
                break;
        }

    }
}
