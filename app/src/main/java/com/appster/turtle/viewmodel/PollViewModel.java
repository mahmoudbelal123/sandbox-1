/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.Poll;
import com.appster.turtle.network.response.CreateMeetupResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.util.Alert;

/**
 * Created by atul on 01/09/17.
 * To inject activity reference.
 */

public class PollViewModel extends BaseObservable {

    public static final String TAG = PollViewModel.class.getSimpleName();
    public final Poll poll;
    private BaseActivity mActivity;
    private RetrofitRestRepository repository;

    public PollViewModel(Context context, Poll poll) {
        this.poll = poll;
        this.mActivity = (BaseActivity) context;
        this.repository = ((ApplicationController) mActivity.getApplicationContext()).provideRepository();
    }

    public void postPoll() {
        mActivity.showProgressBar();

        new BaseCallback<CreateMeetupResponse>(mActivity, repository.getApiService()
                .postPoll(poll)) {
            @Override
            public void onSuccess(CreateMeetupResponse pollResponse) {
                Alert.createAlert(mActivity, "", mActivity.getString(R.string.poll_created_message)).setOnDismissListener(dialog -> {

                    mActivity.hideProgressBar();
                    mActivity.finish();

                }).show();
            }

            @Override
            public void onFail() {
                mActivity.hideProgressBar();
            }
        };
    }

    public String getPollQuestion() {
        return poll.getQuestion();
    }

}
