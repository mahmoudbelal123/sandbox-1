package com.appster.turtle.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityOtherByBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;

/**
 * A activity class for other user like
 */

public class OtherByActivity extends BaseActivity {

    private ActivityOtherByBinding mBinding;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_liked_by);

        Bundle b = getIntent().getExtras();


        int postId = b.getInt(Constants.BundleKey.POST);
        int commentId;
        if (getIntent().hasExtra(Constants.BundleKey.COMMENT))
            commentId = b.getInt(Constants.BundleKey.COMMENT);

        initUI();
    }

    private void initUI() {

        setUpHeader(mBinding.clLikedByHeader.clHeader, getString(R.string.liked_by), R.drawable.back_arrow, 0);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_other_by);

    }

    public ActivityOtherByBinding getmBinding() {
        return mBinding;
    }
}
