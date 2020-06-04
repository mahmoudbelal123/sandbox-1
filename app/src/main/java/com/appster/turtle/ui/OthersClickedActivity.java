/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.appster.turtle.R;
import com.appster.turtle.adapter.UsersAdapter;
import com.appster.turtle.databinding.ActivityOthersClickedBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.util.Utils;

import java.util.List;
/*
* Activity for other clicked
 */
public class OthersClickedActivity extends BaseActivity {

    ActivityOthersClickedBinding mBinding;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
    }

    private void initUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_others_clicked);

        Bundle b = getIntent().getExtras();
        List<User> mAdminsList = b.getParcelableArrayList(Constants.ADMINS_LIST);

        setUpHeader(false, mBinding.header.clHeader, b.getString(Constants.HEADER_TITLE));
        UsersAdapter adapter = new UsersAdapter(this, mAdminsList);
        mBinding.rvUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvUsers.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(this, 10)));

        mBinding.rvUsers.setAdapter(adapter);
    }
}
