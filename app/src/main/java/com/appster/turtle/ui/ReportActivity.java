package com.appster.turtle.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityReportBinding;
import com.appster.turtle.model.Comment;
import com.appster.turtle.model.User;
import com.appster.turtle.model.UserProfileData;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.request.ReportRequest;
import com.appster.turtle.network.request.UserActionRequest;
import com.appster.turtle.network.response.GetCommentResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.UserActionResponse;

import java.util.Collections;

import rx.Observable;

/**
 *
 * Created  on 27/03/18 .
 * actibvity for report
 */

public class ReportActivity extends BaseActivity {

    private ActivityReportBinding mBinding;
    private boolean isFromPost;
    private boolean isFromComment;
    private boolean isNew;
    private Posts posts;
    private Comment comment;
    private UserProfileData userProfileData;
    private User user;
    private int roomId;
    private int userId;

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
        if (getIntent() != null) {


            if (getIntent().hasExtra(Constants.IS_FROM_POST)) {
                posts = getIntent().getParcelableExtra(Constants.BundleKey.POST);
                isFromPost = getIntent().getBooleanExtra(Constants.IS_FROM_POST, false);
            } else if (getIntent().hasExtra(Constants.IS_FROM_COMMENT)) {
                isFromPost = false;
                isFromComment = getIntent().getBooleanExtra(Constants.IS_FROM_COMMENT, false);
                comment = getIntent().getParcelableExtra(Constants.BundleKey.COMMENT);
            } else if (getIntent().hasExtra(Constants.IS_NEWADD)) {
                isFromPost = false;
                isNew = true;
                isFromComment = false;
                roomId = getIntent().getIntExtra(Constants.BundleKey.ROOM_ID, 0);
                user = getIntent().getParcelableExtra(Constants.USER);
                userId = getIntent().getIntExtra(Constants.USER_ID, 0);

            } else {
                isNew = false;
                isFromPost = false;
                userProfileData = getIntent().getParcelableExtra(Constants.USER);
                userId = getIntent().getIntExtra(Constants.USER_ID, 0);
            }
        }

    }

    private void setUpViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        setUpHeader(mBinding.header.clHeader, getString(R.string.report), getString(R.string.send), R.drawable.ic_closs_cross);
        mBinding.tvCharCount.setText(getString(R.string.no_of_characters_remaining, "" + (200 - mBinding.etReview.getText().toString().length())));

        mBinding.etReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mBinding.etReview.getText().toString().isEmpty()) {
                    mBinding.header.tvHeaderEnd.setEnabled(false);
                } else {
                    mBinding.header.tvHeaderEnd.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mBinding.tvCharCount.setText(getString(R.string.no_of_characters_remaining, "" + (200 - mBinding.etReview.getText().toString().length())));

            }
        });

        mBinding.header.tvHeaderEnd.setOnClickListener(view -> {
            if (isFromPost) {
                setFlag(posts);
            } else if (isFromComment) {
                flagComment(comment);
            } else if (isNew) {
                reportOrFlagThisUserMember();
            } else {
                reportOrFlagThisUser();
            }
        });

    }

    private void setFlag(final Posts item) {


        UserActionRequest request = new UserActionRequest();
        request.setRoomId(item.getPostId());
        request.setReason(mBinding.etReview.getText().toString());
        Observable<UserActionResponse> userActionResponseObservable = ((ApplicationController) getApplicationContext()).provideRepository().getApiService().flagPost(request);
        showProgressBar();

        new BaseCallback<UserActionResponse>(this, userActionResponseObservable) {

            @Override
            public void onSuccess(UserActionResponse response) {
                hideProgressBar();
                item.setFlaggedByMe(true);
                Intent intent = new Intent();
                intent.putExtra(Constants.BundleKey.POST, item);
                setResult(RESULT_OK, intent);
                showSnackBar("Post flagged successfully");

                new Handler().postDelayed(() -> finish(), 1000);


            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };
    }

    private void reportOrFlagThisUser() {
        showProgressBar();
        ReportRequest request = new ReportRequest();
        request.setId(userId);
        request.setReason(mBinding.etReview.getText().toString());
        new BaseCallback<UserActionResponse>(ReportActivity.this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().reportFlagUser(request), true) {

            @Override
            public void onSuccess(UserActionResponse response) {
                hideProgressBar();
                showSnackBar(userProfileData.getUserModel().getName() + " has been reported/flagged.");
                userProfileData.setFlaggedByMe(true);

                Intent intent = new Intent();
                intent.putExtra(Constants.BundleKey.POST, userProfileData);
                setResult(RESULT_OK, intent);
                new Handler().postDelayed(() -> finish(), 1000);
            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };
    }


    private void flagComment(final Comment item) {

        UserActionRequest baseId = new UserActionRequest();
        baseId.setRoomId(item.getCommentId());
        baseId.setReason(mBinding.etReview.getText().toString());
        new BaseCallback<GetCommentResponse>(ReportActivity.this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().flagComment(baseId)) {

            @Override
            public void onSuccess(GetCommentResponse response) {

                if (response != null && response.getData() != null) {
                    hideProgressBar();
                    item.setFlaggedByMe(true);
                    Intent intent = new Intent();
                    intent.putExtra(Constants.BundleKey.COMMENT, item);
                    setResult(RESULT_OK, intent);
                    showSnackBar("Comment flagged successfully");

                    new Handler().postDelayed(() -> finish(), 1000);

                }
            }

            @Override
            public void onFail() {
                //
            }
        };
    }


    private void reportOrFlagThisUserMember() {
        showProgressBar();

        UserActionRequest request = new UserActionRequest();
        request.setRoomId(roomId);
        request.setUserIds(Collections.singletonList(userId));
        request.setReason(mBinding.etReview.getText().toString());

        new BaseCallback<UserActionResponse>(this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().reportFlagRoomMember(request), true) {

            @Override
            public void onSuccess(UserActionResponse response) {
                hideProgressBar();
                showSnackBar(user.getName() + " has been reported/flagged.");
                user.setFlaggedByMe(true);

                Intent intent = new Intent();
                intent.putExtra(Constants.BundleKey.POST, user);
                setResult(RESULT_OK, intent);
                new Handler().postDelayed(() -> finish(), 1000);

            }

            @Override
            public void onFail() {
//
            }
        };
    }


}
