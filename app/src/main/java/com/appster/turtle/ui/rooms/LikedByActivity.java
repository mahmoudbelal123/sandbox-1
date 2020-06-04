/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.rooms;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityLikedByBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.viewmodel.LikedByViewModel;
/*
* Activity for other user liked
 */
public class LikedByActivity extends BaseActivity {

    private ActivityLikedByBinding mBinding;
    private int postId;
    private int commentId;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_liked_by);

        Bundle b = getIntent().getExtras();


        postId = b.getInt(Constants.BundleKey.POST);
        if (getIntent().hasExtra(Constants.BundleKey.COMMENT))
            commentId = b.getInt(Constants.BundleKey.COMMENT);

        initUI();
    }

    private void initUI() {

        setUpHeader(mBinding.clLikedByHeader.clHeader, getString(R.string.liked_by), R.drawable.back_arrow, 0);
        final LikedByViewModel likedByViewModel = new LikedByViewModel(LikedByActivity.this, postId, commentId);
        likedByViewModel.getLikedBy(1);
        mBinding.setViewModel(likedByViewModel);

        mBinding.swipeRefresh.setOnRefreshListener(() -> {
            mBinding.swipeRefresh.setRefreshing(true);
            likedByViewModel.getLikedBy(1);
        });


    }

    public ActivityLikedByBinding getmBinding() {
        return mBinding;
    }
}
