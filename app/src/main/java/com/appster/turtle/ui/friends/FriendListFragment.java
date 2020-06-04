package com.appster.turtle.ui.friends;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.MyFriendsAdapter;
import com.appster.turtle.adapter.viewholder.FriendListViewHolder;
import com.appster.turtle.databinding.FragmentFriendsListBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.FriendRequestResponse;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 16/12/17.
 * fragment
 */

public class FriendListFragment extends BaseFragment implements FriendListViewHolder.IFriendListener {

    FragmentFriendsListBinding mBinding;
    private boolean isMyFriends;
    private RetrofitRestRepository mRepository;
    private LinearLayoutManager linearLayoutManager;
    private List<User> mFriendsList;
    private int mCurrentPage = 1;

    private int mTotalPagesAvailable;
    private BaseCallback<SearchUserResponse> baseCallback;
    private MyFriendsAdapter adapter;
    private boolean isLoading;

    @Override
    public String getFragmentName() {
        return FriendListFragment.class.getName();
    }

    private static final String ARG_SECTION_NUMBER = "section_number";

    public FriendListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FriendListFragment newInstance(int sectionNumber) {
        FriendListFragment fragment = new FriendListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends_list, container, false);

        isMyFriends = getArguments().getInt(ARG_SECTION_NUMBER) == 0;

        initUI();


        return mBinding.getRoot();
    }

    private void initUI() {

        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mFriendsList = new ArrayList<>();

        adapter = new MyFriendsAdapter(getActivity(), getArguments().getInt(ARG_SECTION_NUMBER), mFriendsList, FriendListFragment.this);
        mBinding.rvFriends.addOnScrollListener(purchaseScrollListener);
        mBinding.rvFriends.setLayoutManager(linearLayoutManager);
        mBinding.rvFriends.setAdapter(adapter);
        mBinding.rvFriends.addItemDecoration(new ItemDecorationViewFrg(getActivity(), Utils.dpToPx(getActivity(), 22), 0));

        mBinding.swiperefresh.setOnRefreshListener(() -> {
            mCurrentPage = 1;
            getFriendList();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentPage = 1;
        getFriendList();

    }


    private RecyclerView.OnScrollListener purchaseScrollListener = new RecyclerView.OnScrollListener() {


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int mVisibleItemCount = linearLayoutManager.getChildCount();
            int mTotalItemCount = linearLayoutManager.getItemCount();
            int mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
            if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                if (mFriendsList != null) {
                    mCurrentPage++;
                    isLoading = true;
                    getFriendList();
                }
            }
        }
    };


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isMyFriends && (getActivity() instanceof FriendListActivity)) {
            if (((FriendListActivity) getActivity()).isUserRefresh()) {
                mCurrentPage = 1;
                getFriendList();
            }
        }
    }

    private void getFriendList() {


        if (baseCallback != null)
            baseCallback.cancel();

        int friendReq = Constants.MyFriend.ACCEPTED_FRIEND;
        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1)
            friendReq = Constants.MyFriend.ALL_REQUESTS_AND_FRIENDS;


        baseCallback = new BaseCallback<SearchUserResponse>(getBaseActivity(), mRepository.getApiService().getFriends(mCurrentPage, friendReq, Constants.LIST_LENGTH, "")) {
            @Override
            public void onSuccess(SearchUserResponse response) {

                if (mBinding.swiperefresh.isRefreshing())
                    mBinding.swiperefresh.setRefreshing(false);


                mTotalPagesAvailable = response.getPagination().getTotalPages();
                isLoading = false;

                if (mCurrentPage == 1) {
                    mFriendsList.clear();

                    adapter.notifyDataSetChanged();
                    if (response.getData().size() == 0) {
                        mBinding.tvNoUsers.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.tvNoUsers.setVisibility(View.GONE);
                    }

                } else {
                    mBinding.tvNoUsers.setVisibility(View.GONE);
                }

                mFriendsList.addAll((response.getData()));

                adapter.notifyItemChanged(mFriendsList.size() + 1, mFriendsList);

            }

            @Override
            public void onFail() {

                if (mBinding.swiperefresh.isRefreshing())
                    mBinding.swiperefresh.setRefreshing(false);
                mBinding.tvNoUsers.setVisibility(View.GONE);


                isLoading = false;

            }
        };

    }


    private void actionOnRequest(int userId, int action, final int pos) {

        new BaseCallback<FriendRequestResponse>(getBaseActivity(), mRepository.getApiService().actionFriendRequest(userId, action)) {
            @Override
            public void onSuccess(FriendRequestResponse response) {

                if (response.getData() != null &&
                        (response.getData().getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_CONNECTED ||
                                response.getData().getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED ||
                                response.getData().getUserReqStatus() == Constants.MyFriend.STATUS_REJECTED)) {
                    mFriendsList.remove(pos);
                    adapter.notifyDataSetChanged();

                    if (getActivity() instanceof FriendListActivity) {
                        ((FriendListActivity) getActivity()).refreshFriends();
                    }
                }
            }

            @Override
            public void onFail() {
                isLoading = false;

            }
        };
    }


    @Override
    public void onAcceptClicked(int userId, int pos) {

        actionOnRequest(userId, Constants.MyFriend.ACCEPT_REQUEST, pos);
    }

    @Override
    public void onRejectClicked(int userId, int pos) {
        actionOnRequest(userId, Constants.MyFriend.REJECT_REQUEST, pos);


    }

    @Override
    public void onProfileClicked(int userId) {
//
    }

    @Override
    public void onMutualFriendsClicked(int userId) {
//
    }


}
