package com.appster.turtle.ui.search;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.SearchHomePeopleMoreAdapter;
import com.appster.turtle.databinding.ActivitySearchSpacesMoreBinding;
import com.appster.turtle.databinding.ItemSearchPeopleMoreBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.FriendRequestResponse;
import com.appster.turtle.network.response.SearchPopularUserResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhaykant on 16/02/18.
 * Search people more
 */

public class SearchPeopleMoreActivity extends BaseActivity implements View.OnTouchListener, SearchPeopleClickListner, View.OnClickListener {
    private static final String TAG = SearchPeopleMoreActivity.class.getName();

    private ActivitySearchSpacesMoreBinding mBinder;
    private BaseCallback<SearchPopularUserResponse> mSearchRoomsBaseCallback;
    private SearchHomePeopleMoreAdapter mAdapter;
    private RetrofitRestRepository mRepository;
    private int mCurrentPage = 1;
    private int mTotalPagesAvailable;
    private List<User> mUsersList;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean isLoading;
    final Handler mHandler = new Handler();
    private String mQuery = "";


    @Override
    public String getActivityName() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_search_spaces_more);
        Bundle bundle = getIntent().getExtras();
        mQuery = bundle.getString(Constants.POST_QUERY);

        init();
    }

    private void init() {
        hideKeyboard();
        mBinder.btnDone.setOnClickListener(this);
        mBinder.rvMoreItems.setOnTouchListener(this);

        mBinder.etSearch.setText(mQuery);

        mRepository = ((ApplicationController) this.getApplicationContext()).provideRepository();

        setupRecyclerView();

        mBinder.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                mHandler.postDelayed(() -> {
                    mQuery = charSequence.toString();
                    mCurrentPage = 1;
                  //  mUsersList.clear();
                    searchRooms(mQuery);

                }, 200);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void setupRecyclerView() {
        mUsersList = new ArrayList<>();


        mBinder.rvMoreItems.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(SearchPeopleMoreActivity.this, 19)));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinder.rvMoreItems.setLayoutManager(mLinearLayoutManager);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentPage = 1;
        searchRooms(mQuery);

    }

    RecyclerView.OnScrollListener searchRoomsScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int mVisibleItemCount = mLinearLayoutManager.getChildCount();
            int mPastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
            int mTotalItemCount = mLinearLayoutManager.getItemCount();

            if (mCurrentPage < mTotalPagesAvailable && (mVisibleItemCount + mPastVisibleItems <= mTotalItemCount) && !isLoading) {
                if (mUsersList != null) {
                    mCurrentPage++;
                    isLoading = true;
                    searchRooms(mQuery);
                }
            }
        }

    };


    public void searchRooms(String query) {

        //if query is blank and last search in room wasn't same search as before
        if (mSearchRoomsBaseCallback != null) {
            mSearchRoomsBaseCallback.cancel();
        }
        mBinder.rvMoreItems.addOnScrollListener(searchRoomsScrollListener);
        mSearchRoomsBaseCallback = new BaseCallback<SearchPopularUserResponse>(this,
                mRepository.getApiService().searchPopularUser(mCurrentPage,
                        Constants.LIST_LENGTH, query), false) {
            @Override
            public void onSuccess(SearchPopularUserResponse response) {

                int pos = 0;
                mTotalPagesAvailable = response.getPagination().getTotalPages();
                if (mCurrentPage == 1) {
                    mUsersList.clear();
                    mUsersList.addAll((response.getData()));
                    mAdapter = new SearchHomePeopleMoreAdapter(SearchPeopleMoreActivity.this, mUsersList, SearchPeopleMoreActivity.this);
                    mBinder.rvMoreItems.setAdapter(mAdapter);

                    if ((response.getPagination().getNumberOfElements() == 0)) {

                        mBinder.tvNoResultFound.setVisibility(View.VISIBLE);
                    } else {
                        mBinder.tvNoResultFound.setVisibility(View.GONE);
                    }
                    mAdapter.notifyDataSetChanged();

                } else {
                    pos = mUsersList.size() + 1;
                    mUsersList.addAll((response.getData()));
                    mAdapter.notifyItemChanged(pos, mUsersList);

                }

                isLoading = false;
            }

            @Override
            public void onFail() {
                isLoading = false;
            }
        };
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideKeyboard();
        return false;
    }

    @Override
    public void onUserClick(ItemSearchPeopleMoreBinding item, User user, int pos) {

        LogUtils.LOGD(TAG, "pos>" + pos + " user:" + user.getFullName());
        LogUtils.LOGD(TAG, "pos>" + pos + " friendReqStatus:" + user.getUserReqStatus());
        if (user.getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED ||
                user.getUserReqStatus() == Constants.MyFriend.STATUS_REJECTED) {
            actionOnRequest(user.getUserReqStatus(), user.getUserId(), user);
        }
    }

    @Override
    public void onInvite(ItemSearchPeopleMoreBinding item, User user, int pos, boolean accept) {

        BaseCallback<FriendRequestResponse> baseCallback = new BaseCallback<FriendRequestResponse>(SearchPeopleMoreActivity.this, mRepository.getApiService().actionFriendRequest(user.getUserId(), accept ? 2 : 3)) {
            @Override
            public void onSuccess(FriendRequestResponse response) {

                if (response.getData() != null) {

                    mUsersList.get(pos).setUserReqStatus(response.getData().getUserReqStatus());

                    mUsersList.get(pos).setRequestedByMe(true);

                    mAdapter.notifyItemChanged(pos);


                }


            }

            @Override
            public void onFail() {

            }
        };
    }


    private void actionOnRequest(int friendStatus, int friendId, final User user) {
        showProgressBar();
        int action = 0;

        /*
          q== 1) {//Send new Friend request
         q == 2) { // accept friend request
         q == 3) { //decline request

         */


        if (friendStatus == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED || friendStatus == Constants.MyFriend.STATUS_REJECTED) {
            action = 1;
        }


        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();


        new BaseCallback<FriendRequestResponse>(SearchPeopleMoreActivity.this, mRepository.getApiService().actionFriendRequest(friendId, action)) {
            @Override
            public void onSuccess(FriendRequestResponse response) {
                hideProgressBar();
                if (response != null) {
                    user.setUserReqStatus(response.getData().getUserReqStatus());
                    user.setRequestedByMe(true);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail() {
                hideProgressBar();

            }
        };
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_done:
                hideKeyboard();
                finish();
                break;
        }
    }

}


