package com.appster.turtle.ui.friends;
/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.MembersAdapter;
import com.appster.turtle.adapter.viewholder.MembersViewHolder;
import com.appster.turtle.databinding.ActivityMutualBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.LikedByResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
/**
 * A activity class for mutual friends
 */
public class MutualFriendsActivity extends BaseActivity {

    private ActivityMutualBinding mBinding;
    private RetrofitRestRepository repository;
    private MembersAdapter adapter;
    private int mCurrentPage = 1;
    private int mTotalPagesAvailable;
    private List<User> users = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private boolean isLoading;
    private int userId;
    private boolean isAllFriend;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mutual);

        Bundle b = getIntent().getExtras();
        userId = b.getInt(Constants.USER_ID);
        isAllFriend = b.getBoolean(Constants.IS_ALL_CONNECTION);

        this.repository = ((ApplicationController) getApplicationContext()).provideRepository();

        initUI();
    }

    private void initUI() {

        setUpHeader(mBinding.clLikedByHeader.clHeader, "MUTUAL FRIENDS", R.drawable.ic_back_black, 0);

        if (isAllFriend)
            setUpHeader(mBinding.clLikedByHeader.clHeader, "FRIENDS", R.drawable.ic_back_black, 0);


        adapter = new MembersAdapter(MutualFriendsActivity.this, users, new MembersViewHolder.IMembers() {
            @Override
            public void onMemberSelected(int userId, int pos) {
//
            }

            @Override
            public void onMemberAccept(int userId) {
//
            }

            @Override
            public void onMemberDecline(int userId) {
//
            }
        });
        linearLayoutManager = new LinearLayoutManager(MutualFriendsActivity.this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvLikedBy.setLayoutManager(linearLayoutManager);
        mBinding.rvLikedBy.setAdapter(adapter);
        mBinding.rvLikedBy.addItemDecoration(new ItemDecorationView(MutualFriendsActivity.this, Utils.dpToPx(MutualFriendsActivity.this, 12), Utils.dpToPx(MutualFriendsActivity.this, 12)));

        mBinding.rvLikedBy.addOnScrollListener(getScrollListener());
        mBinding.swipeRefresh.setOnRefreshListener(() -> {
            mBinding.swipeRefresh.setRefreshing(true);
            mCurrentPage = 1;
            getData();
        });

        getData();
    }

    public RecyclerView.OnScrollListener getScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = linearLayoutManager.getChildCount();
                mTotalItemCount = linearLayoutManager.getItemCount();
                mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                    if (users != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getData();


                    }
                }


            }
        };
    }

    private void getData() {

        Observable observable = repository.getApiService()
                .getMutualFriends(mCurrentPage, Constants.LIST_LENGTH, userId);
        if (isAllFriend) {

            observable = repository.getApiService()
                    .getConnections(mCurrentPage, Constants.LIST_LENGTH, userId);
        }


        new BaseCallback<LikedByResponse>(MutualFriendsActivity.this, observable) {
            @Override
            public void onSuccess(LikedByResponse response) {

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                if (mBinding.swipeRefresh.isRefreshing())
                    users.clear();

                users.addAll(response.getData());
                isLoading = false;
                mBinding.swipeRefresh.setRefreshing(false);

                adapter.notifyDataSetChanged();

                mBinding.tvNo.setVisibility(View.GONE);

                if (users.isEmpty()) {
                    mBinding.tvNo.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFail() {
                mBinding.swipeRefresh.setRefreshing(false);

                mBinding.tvNo.setVisibility(View.GONE);

                if (users.isEmpty()) {
                    mBinding.tvNo.setVisibility(View.VISIBLE);
                }

            }
        };

    }


}
