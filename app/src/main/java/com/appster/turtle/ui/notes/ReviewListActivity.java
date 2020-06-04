/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.notes;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.NotesReviewAdapter;
import com.appster.turtle.databinding.ActivityNotesReviewBinding;
import com.appster.turtle.model.NotesModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.NoteResponse;
import com.appster.turtle.network.response.NotesReviewListResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;

import rx.Observable;
/*
* Activity for review note list
 */
public class ReviewListActivity extends BaseActivity {

    private ActivityNotesReviewBinding mBinding;
    private RetrofitRestRepository mRepository;
    private LinearLayoutManager linearLayoutManager;
    private NotesReviewAdapter mNotesReviewAdapter;
    private NotesModel mNotes;

    private boolean isLoading, isPullToRefresh;
    private int mCurrentPage = 1;
    private int mTotalPagesAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notes_review);
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        getnote(getIntent().getExtras().getInt(Constants.BundleKey.NOTE_ID));
        mBinding.header.ivIconEnd.setVisibility(View.GONE);

    }

    @Override
    public String getActivityName() {
        return BaseActivity.class.getName();
    }


    private void initUI() {

        setUpHeader(true
                , mBinding.header.clHeader, getString(R.string.read_reviews));
        mBinding.header
                .clHeader.setBackgroundColor(ContextCompat.getColor(this, R.color.app_white));

        mBinding.header.ivIconStart.setOnClickListener(v -> onBackPressed());

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        mBinding.rvNotesReviews.addOnScrollListener(purchaseScrollListener);
        mBinding.rvNotesReviews.setLayoutManager(linearLayoutManager);

        mNotesReviewAdapter = new NotesReviewAdapter(this);
        mBinding.rvNotesReviews.setAdapter(mNotesReviewAdapter);
        getReviews();

        mBinding.swipeRefresh.setOnRefreshListener(() -> {
            mBinding.swipeRefresh.setRefreshing(true);
            mCurrentPage = 1;
            isPullToRefresh = true;
            getReviews();
        });
    }

    private RecyclerView.OnScrollListener purchaseScrollListener = new RecyclerView.OnScrollListener() {


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int mVisibleItemCount = linearLayoutManager.getChildCount();
            int mTotalItemCount = linearLayoutManager.getItemCount();
            int mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
            if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                if (mNotesReviewAdapter.getItemCount() > 0) {
                    mCurrentPage++;
                    isLoading = true;
                    getReviews();
                }
            }
        }
    };


    private void getReviews() {

        if (!isPullToRefresh) {
            showProgressBar();
        }
        Observable<NotesReviewListResponse> notesReview = mRepository.getApiService().getNotesReview(mNotes.getId(), mCurrentPage, 10);

        new BaseCallback<NotesReviewListResponse>(this, notesReview) {
            @Override
            public void onSuccess(NotesReviewListResponse response) {
                hideProgressBar();
                mTotalPagesAvailable = response.getPagination().getTotalPages();
                mBinding.swipeRefresh.setRefreshing(false);
                if (response.getData() != null) {
                    if (mCurrentPage == 1)
                        mNotesReviewAdapter.clear();

                    mNotesReviewAdapter.addAll(response.getData());
                }
            }

            @Override
            public void onFail() {
                mBinding.swipeRefresh.setRefreshing(false);

                hideProgressBar();

            }
        };

    }


    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra(Constants.POS)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.PurchaseNotes.FROM_POST_NOTES, true);
            bundle.putParcelable(Constants.NOTES, mNotes);
            bundle.putBoolean(Constants.IS_EDIT, true);
            bundle.putInt(Constants.POS, getIntent().getIntExtra(Constants.POS, 0));
            ExplicitIntent.getsInstance().navigateTo(ReviewListActivity.this, NotesListingActivity.class, bundle);
            finish();
        } else
            super.onBackPressed();

    }

    private void getnote(int noteId) {

        new BaseCallback<NoteResponse>(this, mRepository.getApiService().noteDetails(noteId), true) {

            @Override
            public void onSuccess(NoteResponse response) {
                isPullToRefresh = false;
                mNotes = response.getData();
                initUI();


            }

            @Override
            public void onFail() {
//
            }
        };
    }
}


