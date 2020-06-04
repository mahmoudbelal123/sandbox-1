/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.search;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.UsersAdapter;
import com.appster.turtle.databinding.FragmentSearchPeopleBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.settings.InviteFriendsActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

/**
 * Created by rohan on 01/11/17.
 * Search people fragment
 */

public class SearchPeopleFragment extends BaseFragment implements View.OnClickListener,
        View.OnTouchListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    FragmentSearchPeopleBinding mBinding;
    private ArrayList<User> mPeopleList;

    private RetrofitRestRepository mRepository;
    private UsersAdapter mAdapter;

    private BaseCallback<SearchUserResponse> baseCallback;
    private int mTotalPagesAvailable;
    private boolean isLoading;
    private int mCurrentPage = 1;

    private String mQuery;

    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public String getFragmentName() {
        return getClass().getName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_people, container, false);
        initUI();
        return mBinding.getRoot();
    }

    private void initUI() {
        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        setupRecyclerView();

        mBinding.tvInviteFriends.setOnTouchListener(this);
        mBinding.tvPlaceholderSearchPeople.setOnTouchListener(this);
        mBinding.tvUsersFound.setOnTouchListener(this);
        mBinding.rvPeople.setOnTouchListener(this);
        mBinding.clSearchPeople.setOnTouchListener(this);

        // TODO: 01/11/17 Implement swipe to refresh
    }

    private void setupRecyclerView() {
        mPeopleList = new ArrayList<>();
        mAdapter = new UsersAdapter(getActivity(), mPeopleList);
        mBinding.rvPeople.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 14)));
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.rvPeople.setLayoutManager(mLinearLayoutManager);
        mBinding.rvPeople.setAdapter(mAdapter);

        mBinding.rvPeople.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int mVisibleItemCount = mLinearLayoutManager.getChildCount();
                int mPastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                int mTotalItemCount = mLinearLayoutManager.getItemCount();

                if (mCurrentPage < mTotalPagesAvailable && (mVisibleItemCount + mPastVisibleItems <= mTotalItemCount) && !isLoading) {
                    if (mPeopleList != null) {
                        mCurrentPage++;
                        isLoading = true;
                        searchPeople(mQuery);
                    }
                }
            }

        });
    }

    public static SearchPeopleFragment newInstance(int sectionNumber) {
        SearchPeopleFragment searchPeopleFragment = new SearchPeopleFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_SECTION_NUMBER, sectionNumber);
        searchPeopleFragment.setArguments(b);
        return searchPeopleFragment;
    }

    public void searchPeople(String query) {

        if (((SearchRoomsAndPeopleActivity) getActivity()).getPeopleSearchQuery().equals(query)) {

            //so that search is carried out during pagination
            if (!isLoading)
                return;
        }

        mQuery = query;

        ((SearchRoomsAndPeopleActivity) getActivity()).setPeopleSearchQuery(query);

        //if query is blank and last search in people wasn't same search as before
        if (query.length() == 0) {

            mBinding.tvPlaceholderSearchPeople.setVisibility(View.VISIBLE);
            mBinding.tvUsersFound.setVisibility(View.INVISIBLE);

            //to prevent search when user quickly clears text
            if (baseCallback != null)
                baseCallback.cancel();

            mPeopleList.clear();
            mAdapter.notifyDataSetChanged();

        } else {

            if (baseCallback != null)
                baseCallback.cancel();

            baseCallback = new BaseCallback<SearchUserResponse>((BaseActivity) getActivity(),
                    mRepository.getApiService().searchUser(mCurrentPage, Constants.LIST_LENGTH,
                            String.valueOf(query)), false) {

                @Override
                public void onSuccess(SearchUserResponse response) {

                    mTotalPagesAvailable = response.getPagination().getTotalPages();
                    isLoading = false;

                    if (mCurrentPage == 1) {
                        mPeopleList = (ArrayList<User>) (response.getData());

                        mBinding.tvPlaceholderSearchPeople.setVisibility(View.INVISIBLE);
                        mBinding.tvUsersFound.setVisibility(View.VISIBLE);

                        if ((response.getPagination().getNumberOfElements() == 1)) {
                            mBinding.tvUsersFound.setText(String.format(getString(R.string.found_result), String.valueOf(response.getPagination().getNumberOfElements())));
                        } else {
                            mBinding.tvUsersFound.setText(String.format(getString(R.string.found_results), String.valueOf(response.getPagination().getNumberOfElements())));
                        }

                        mAdapter = new UsersAdapter(getActivity(), mPeopleList);
                        mBinding.rvPeople.setAdapter(mAdapter);

                    } else {
                        mPeopleList.addAll((response.getData()));
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFail() {

                }
            };

        }
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_invite_friends:
                hideKeyboard();
                ExplicitIntent.getsInstance().navigateTo(getActivity(), InviteFriendsActivity.class);
                break;

            case R.id.tv_users_found:
                hideKeyboard();
                break;

            case R.id.rv_rooms:
                hideKeyboard();
                break;

            case R.id.tv_placeholder_search_people:
                hideKeyboard();
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideKeyboard();
        return false;
    }
}
