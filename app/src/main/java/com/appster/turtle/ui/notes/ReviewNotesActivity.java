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
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityWriteReviewBinding;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.AddNotesReviewResponse;
import com.appster.turtle.network.response.NotesReview;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
/*
* Activity for review note
 */
public class ReviewNotesActivity extends BaseActivity {


    RetrofitRestRepository repository;
    private ActivityWriteReviewBinding activityWriteReviewBinding;
    private NotesModel mNotes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
        this.repository = ((ApplicationController) getApplicationContext()).provideRepository();
        mNotes = getIntent().getParcelableExtra(Constants.BundleKey.NOTES);

    }

    private void setUpViews() {
        activityWriteReviewBinding = DataBindingUtil.setContentView(this, R.layout.activity_write_review);
        setUpHeader(activityWriteReviewBinding.header.clHeader, getString(R.string.write_review), getString(R.string.post), R.drawable.ic_closs_cross);
        activityWriteReviewBinding.tvCharCount.setText(getString(R.string.no_of_characters_remaining, "" + (480 - activityWriteReviewBinding.etReview.getText().toString().length())));

        activityWriteReviewBinding.ratingBar.setOnRatingChangeListener((ratingBar, rating) -> {
            if (!activityWriteReviewBinding.tvTitle.getText().toString().isEmpty() && activityWriteReviewBinding.ratingBar.getRating() > 0) {
                activityWriteReviewBinding.header.tvHeaderEnd.setEnabled(true);
            } else {
                activityWriteReviewBinding.header.tvHeaderEnd.setEnabled(false);
            }
        });

        activityWriteReviewBinding.etReview.addTextChangedListener(new TextWatcher() {
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
                activityWriteReviewBinding.tvCharCount.setText(getString(R.string.no_of_characters_remaining, "" + (480 - activityWriteReviewBinding.etReview.getText().toString().length())));
                if (!activityWriteReviewBinding.tvTitle.getText().toString().isEmpty() && activityWriteReviewBinding.ratingBar.getRating() > 0) {
                    activityWriteReviewBinding.header.tvHeaderEnd.setEnabled(true);
                } else {
                    activityWriteReviewBinding.header.tvHeaderEnd.setEnabled(false);
                }
            }
        });

        activityWriteReviewBinding.header.tvHeaderEnd.setOnClickListener(view -> {
            float rating = activityWriteReviewBinding.ratingBar.getRating();
            String review = activityWriteReviewBinding.etReview.getText().toString();

            NotesReview notesReview = new NotesReview();
            notesReview.setRating(rating);
            notesReview.setDetails(review);
            notesReview.setNotesId(mNotes.getId());

            addReview(notesReview);
        });
    }


    public void addReview(NotesReview review) {
        showProgressBar();
        new BaseCallback<AddNotesReviewResponse>((this), repository.getApiService().addNotesReview(review)) {
            @Override
            public void onSuccess(AddNotesReviewResponse response) {
                hideProgressBar();
                if (response != null) {
                    updateNote(response.getData());
                    Bundle bundle = new Bundle();
                    mNotes.setAvgRating(response.getData().getRating());
                    bundle.putInt(Constants.BundleKey.NOTE_ID, mNotes.getId());
                    if (getIntent().hasExtra(Constants.POS)) {
                        bundle.putInt(Constants.POS, getIntent().getIntExtra(Constants.POS, 0));
                    }
                    ExplicitIntent.getsInstance().navigateTo(ReviewNotesActivity.this, ReviewListActivity.class, bundle);
                    ReviewNotesActivity.this.finish();
                }
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };


    }


    private void updateNote(NotesReview notesReview) {

        Intent i = new Intent();
        i.setAction("notes.review");
        sendBroadcast(i);
    }


    @Override
    public String getActivityName() {
        return ReviewNotesActivity.class.getName();
    }
}
