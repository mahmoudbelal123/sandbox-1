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
import com.appster.turtle.adapter.SearchRoomsAdapter;
import com.appster.turtle.databinding.FragmentSearchRoomsBinding;
import com.appster.turtle.model.SearchRoomsModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.SearchRoomsResponse;
import com.appster.turtle.network.response.TopRoomsResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 01/11/17.
 * Search  Fragment from room
 */

public class SearchRoomsFragment extends BaseFragment implements View.OnTouchListener {

    private static final Integer NUMBER_OF_POPULAR_ROOMS = 3;
    FragmentSearchRoomsBinding mBinding;
    private static final String ARG_SECTION_NUMBER = "section_number";

    private RetrofitRestRepository mRepository;

    private List<SearchRoomsModel> mNewRoomsList;
    private List<SearchRoomsModel> mPopularRoomsList;

    private SearchRoomsAdapter mAdapter;

    private List<SearchRoomsModel> mRoomsList;

    private BaseCallback<TopRoomsResponse> mTopRoomsBaseCallback;
    private BaseCallback<SearchRoomsResponse> mSearchRoomsBaseCallback;

    private int mTotalPagesAvailable;
    private boolean isLoading;
    private int mCurrentPage = 1;

    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public String getFragmentName() {
        return getClass().getName();
    }

    public SearchRoomsFragment() {

    }

    public static SearchRoomsFragment newInstance(int sectionNumber) {
        SearchRoomsFragment fragment = new SearchRoomsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_search_rooms, container, false);
        initUI();
        return mBinding.getRoot();
    }

    private void initUI() {
        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        mNewRoomsList = new ArrayList<>();
        mPopularRoomsList = new ArrayList<>();

        setupRecyclerView();

        // TODO: 01/11/17 Implement swipe to refresh
    }

    private void setupRecyclerView() {
        mRoomsList = new ArrayList<>();

        mAdapter = new SearchRoomsAdapter(getActivity(), mRoomsList, false);

        mBinding.rvRooms.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 19)));
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.rvRooms.setLayoutManager(mLinearLayoutManager);

        mBinding.rvRooms.setAdapter(mAdapter);

        mBinding.tvResultsFound.setOnTouchListener(this);
        mBinding.rvRooms.setOnTouchListener(this);
        mBinding.clSearchRooms.setOnTouchListener(this);

        getTopRooms();
    }

    RecyclerView.OnScrollListener searchRoomsScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int mVisibleItemCount = mLinearLayoutManager.getChildCount();
            int mPastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
            int mTotalItemCount = mLinearLayoutManager.getItemCount();

            if (mCurrentPage < mTotalPagesAvailable && (mVisibleItemCount + mPastVisibleItems <= mTotalItemCount) && !isLoading) {
                if (mRoomsList != null) {
                    mCurrentPage++;
                    isLoading = true;
                    searchRooms(((SearchRoomsAndPeopleActivity) getActivity()).getCurrentSearchText());
                }
            }
        }

    };

    public void searchRooms(String query) {

        if (((SearchRoomsAndPeopleActivity) getActivity()).getRoomsSearchQuery().equals(query)) {

            //so that search is carried out during pagination
            if (!isLoading)
                return;
        }

        ((SearchRoomsAndPeopleActivity) getActivity()).setRoomsSearchQuery(query);

        //if query is blank and last search in room wasn't same search as before
        if (query.isEmpty()) {
            getTopRooms();
        } else {

            mBinding.rvRooms.addOnScrollListener(searchRoomsScrollListener);

            if (mTopRoomsBaseCallback != null) {
                mTopRoomsBaseCallback.cancel();
            }

            if (mSearchRoomsBaseCallback != null) {
                mSearchRoomsBaseCallback.cancel();
            }

            mSearchRoomsBaseCallback = new BaseCallback<SearchRoomsResponse>((BaseActivity) getActivity(),
                    mRepository.getApiService().listRooms(0, mCurrentPage,
                            Constants.LIST_LENGTH, query), false) {

                @Override
                public void onSuccess(SearchRoomsResponse response) {

                    mBinding.tvResultsFound.setVisibility(View.VISIBLE);

                    mTotalPagesAvailable = response.getPagination().getTotalPages();

                    if (mCurrentPage == 1) {
                        mRoomsList.clear();
                        mRoomsList.addAll(response.getData());

                        if ((response.getPagination().getNumberOfElements() == 1)) {

                            mBinding.tvResultsFound.setText(String.format(getString(R.string.found_result), String.valueOf(response.getPagination().getNumberOfElements())));
                        } else {
                            mBinding.tvResultsFound.setText(String.format(getString(R.string.found_results), String.valueOf(response.getPagination().getNumberOfElements())));
                        }

                    } else {
                        mRoomsList.addAll((response.getData()));
                    }

                    mAdapter.notifyDataSetChanged();
                    isLoading = false;
                }

                @Override
                public void onFail() {
//                    LOGV("failed", "failed");
                    isLoading = false;
                }
            };
        }
    }

    private void getTopRooms() {

        mBinding.rvRooms.removeOnScrollListener(searchRoomsScrollListener);

        mBinding.tvResultsFound.setVisibility(View.GONE);

        if (mTopRoomsBaseCallback != null) {
            mTopRoomsBaseCallback.cancel();
        }

        if (mSearchRoomsBaseCallback != null) {
            mSearchRoomsBaseCallback.cancel();
        }

        mTopRoomsBaseCallback = new BaseCallback<TopRoomsResponse>(((BaseActivity) getActivity()),
                mRepository.getApiService().topRooms(NUMBER_OF_POPULAR_ROOMS), true) {

            @Override
            public void onSuccess(TopRoomsResponse response) {

                mNewRoomsList.clear();
                mPopularRoomsList.clear();

                if (!StringUtils.isNullOrEmpty(response.getData().getNewRooms())) {
                    mNewRoomsList.addAll(response.getData().getNewRooms());

                    for (SearchRoomsModel room : mNewRoomsList) {
                        room.setExtraElementType(Constants.SearchExtraElementType.NEW_ROOM);
                    }

                    //add "New" header to list
                    SearchRoomsModel newRoomsCategoryHeader = new SearchRoomsModel();
                    newRoomsCategoryHeader.setCategoryHead("New");
                    newRoomsCategoryHeader.setExtraElementType(Constants.SearchExtraElementType.CATEGORY_HEADER);
                    mNewRoomsList.add(0, newRoomsCategoryHeader);

                    //add invite friends button
//                    SearchRoomsModel inviteFriendsButton = new SearchRoomsModel();
//                    inviteFriendsButton.setButtonText("Invite Friends");
//                    inviteFriendsButton.setExtraElementType(Constants.SearchExtraElementType.BUTTON);
//                    mNewRoomsList.add(response.getData().getPopularRooms().size() + 1, inviteFriendsButton);
                }

                if (!StringUtils.isNullOrEmpty(response.getData().getPopularRooms())) {
                    mPopularRoomsList.addAll(response.getData().getPopularRooms());

                    for (SearchRoomsModel room : mPopularRoomsList) {
                        room.setExtraElementType(Constants.SearchExtraElementType.POPULAR_ROOM);
                    }

                    //add "Most Popular Searches" header to list
                    SearchRoomsModel mostPopularSearchesCategoryHeader = new SearchRoomsModel();
                    mostPopularSearchesCategoryHeader.setCategoryHead("Most Popular Searches");
                    mostPopularSearchesCategoryHeader.setExtraElementType(Constants.SearchExtraElementType.CATEGORY_HEADER);
                    mPopularRoomsList.add(0, mostPopularSearchesCategoryHeader);

                    //add view more button
                    SearchRoomsModel viewMoreButton = new SearchRoomsModel();
                    viewMoreButton.setButtonText("View More");
                    viewMoreButton.setExtraElementType(Constants.SearchExtraElementType.BUTTON);
                    mPopularRoomsList.add(response.getData().getPopularRooms().size() + 1, viewMoreButton);
                }

                if (mPopularRoomsList.size() == 0 && mNewRoomsList.size() == 0) {
                    //// TODO: 01/11/17 Handle visibility cases when popular rooms or new rooms are not present

                    return;
                }

                mRoomsList.clear();
                mRoomsList.addAll(mPopularRoomsList);
                mRoomsList.addAll(mPopularRoomsList.size(), mNewRoomsList);

                mAdapter.notifyDataSetChanged();

                isLoading = false;
            }

            @Override
            public void onFail() {
//                LOGV(TAG1, "failed");
                isLoading = false;
            }
        };
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideKeyboard();
        return false;
    }
}
