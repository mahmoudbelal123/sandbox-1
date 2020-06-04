package com.appster.turtle.ui.friends;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.UsersAdapter;
import com.appster.turtle.databinding.ActivityAddFriendsBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.response.FriendRequestResponse;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;
/**
 * A activity class for add friends
 */
public class AddFriendsActivity extends BaseActivity implements TextView.OnEditorActionListener, TextWatcher {
    private ActivityAddFriendsBinding mBinding;
    private int mCurrentPage = 1, mTotalPagesAvailable;
    private UsersAdapter mAdapter;
    private List<User> mPeopleList = new ArrayList<>();
    private boolean isLoading;
    private BaseCallback baseCallback;
    private String query;

    @Override
    public String getActivityName() {
        return AddFriendsActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(AddFriendsActivity.this, R.layout.activity_add_friends);
        initUI();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (query != null && !query.isEmpty()) {
            mCurrentPage = 1;
            showProgressBar();
            searchPeople(query);
        }
    }

    private void initUI() {
        mBinding.header.tvHeaderCenter.setText(getString(R.string.add_friends));
        mBinding.header.ivIconEnd.setVisibility(View.GONE);
        mBinding.header.tvHeaderEnd.setVisibility(View.GONE);
        mBinding.etSearch.setOnEditorActionListener(this);
        mBinding.etSearch.addTextChangedListener(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddFriendsActivity.this);
        mBinding.rvPeople.setLayoutManager(linearLayoutManager);
        mBinding.rvPeople.addItemDecoration(new ItemDecorationView(0, Utils.dpToPx(this, 17)));
        mBinding.rvPeople.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int mVisibleItemCount = mBinding.rvPeople.getLayoutManager().getChildCount();
                int mPastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                int mTotalItemCount = linearLayoutManager.getItemCount();

                if (mCurrentPage < mTotalPagesAvailable && (mVisibleItemCount + mPastVisibleItems <= mTotalItemCount) && !isLoading) {
                    if (mPeopleList != null) {
                        mCurrentPage++;
                        isLoading = true;
                        searchPeople(query);
                    }
                }
            }
        });
    }

    public void searchPeople(String query) {
        if (baseCallback != null) {
            baseCallback.cancel();
        }
        baseCallback = new BaseCallback<SearchUserResponse>(AddFriendsActivity.this,
                ((ApplicationController) getApplication()).provideRepository()
                        .getApiService().searchUser(mCurrentPage, Constants.LIST_LENGTH,
                        String.valueOf(query)), false) {

            @Override
            public void onSuccess(SearchUserResponse response) {
                isLoading = false;
                hideProgressBar();
                mTotalPagesAvailable = response.getPagination().getTotalPages();

                if (mCurrentPage == 1) {
                    mPeopleList = response.getData();


                    mAdapter = new UsersAdapter(AddFriendsActivity.this, mPeopleList);
                    mBinding.rvPeople.setAdapter(mAdapter);

                } else {
                    mPeopleList.addAll((response.getData()));
                    mAdapter.notifyDataSetChanged();
                    mCurrentPage++;
                }

                if (mBinding.etSearch.getText().toString().isEmpty()) {
                    if (mPeopleList != null)
                        mPeopleList.clear();


                    mAdapter.notifyDataSetChanged();
                }

                if (mPeopleList != null && mPeopleList.isEmpty())
                    mBinding.tvNoResult.setVisibility(View.VISIBLE);
                else
                    mBinding.tvNoResult.setVisibility(View.GONE);


            }

            @Override
            public void onFail() {
                hideProgressBar();
                isLoading = false;
            }
        };

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            if (textView.getText().toString().trim().length() < 2) {
                showToast("Minimum 2 characters required for search");
            } else {

                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            }

            return true;
        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString().length() >= 2) {
            mCurrentPage = 1;

            if (mPeopleList != null) {
                mPeopleList.clear();
            }

            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
            query = editable.toString();
            searchPeople(query);
        } else if (editable.toString().isEmpty()) {
            query = "";
            if (mPeopleList != null)
                mPeopleList.clear();

            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }

    }

    public class ItemDecorationView extends RecyclerView.ItemDecoration {
        private int itemBottomOffset;
        private int itemTopOffset;

        public ItemDecorationView(int itemTopOffset, int itemBottomOffset) {

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

    public void sendFriendRequest(int userId, int action) {
        new BaseCallback<FriendRequestResponse>(AddFriendsActivity.this,
                ((ApplicationController) getApplication())
                        .provideRepository().getApiService().actionFriendRequest(userId, action)) {
            @Override
            public void onSuccess(FriendRequestResponse response) {

                if (response.getData().isStatus()) {
                    mAdapter.notifyDataSetChanged();
                    showSnackBar(getString(R.string.request_sent));
                }
            }

            @Override
            public void onFail() {
                isLoading = false;

            }
        };
    }

}
