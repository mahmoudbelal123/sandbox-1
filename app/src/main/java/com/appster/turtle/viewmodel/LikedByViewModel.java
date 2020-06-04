/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.adapter.UsersAdapter;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.LikedByResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.LikedByActivity;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class LikedByViewModel extends BaseObservable {

    private RetrofitRestRepository repository;

    private UsersAdapter adapter;
    private int mCurrentPage = 1;

    private BaseActivity activity;
    private int mTotalPagesAvailable;
    private List<User> users;
    private LinearLayoutManager linearLayoutManager;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private boolean isLoading;
    private int postId = 0;
    private int commentId = 0;


    public LikedByViewModel(BaseActivity activity, int postId, int commentId) {
        this.activity = activity;
        this.postId = postId;
        this.commentId = commentId;
        this.repository = ((ApplicationController) activity.getApplicationContext()).provideRepository();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        users = new ArrayList<>();
        adapter = new UsersAdapter(activity, users);
    }


    public void getLikedBy(final int mCurrentPage) {


        new BaseCallback<LikedByResponse>(activity, repository.getApiService()
                .getLikedBy(postId, commentId, mCurrentPage, Constants.LIST_LENGTH)) {
            @Override
            public void onSuccess(LikedByResponse response) {

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                if (((LikedByActivity) activity).getmBinding().swipeRefresh.isRefreshing())
                    users.clear();

                users.addAll(response.getData());
                isLoading = false;
                ((LikedByActivity) activity).getmBinding().swipeRefresh.setRefreshing(false);

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFail() {
                ((LikedByActivity) activity).getmBinding().swipeRefresh.setRefreshing(false);

            }
        };

    }


    @Bindable
    public UsersAdapter getAdapter() {
        return adapter;
    }

    @Bindable
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
                        getLikedBy(mCurrentPage);


                    }
                }


            }
        };
    }

    public ItemDecoration getItemDecoration() {
        return new ItemDecoration(Utils.dpToPx(activity, 10f), Utils.dpToPx(activity, 10f));
    }

    public class ItemDecoration extends RecyclerView.ItemDecoration {
        private int itemBottomOffset;
        private int itemTopOffset;

        public ItemDecoration(int itemTopOffset, int itemBottomOffset) {

            this.itemTopOffset = itemTopOffset;
            this.itemBottomOffset = itemBottomOffset;
        }


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, itemTopOffset, 0, itemBottomOffset);
        }

    }

    @Bindable
    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

}
