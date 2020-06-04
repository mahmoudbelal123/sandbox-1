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
import com.appster.turtle.adapter.SearchSpacesMoreAdapter;
import com.appster.turtle.databinding.ActivitySearchSpacesMoreBinding;
import com.appster.turtle.databinding.ItemSearchBinding;
import com.appster.turtle.model.SearchRoomsNewModel;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.AddRoomNewResponse;
import com.appster.turtle.network.response.JoinRoomResponse;
import com.appster.turtle.network.response.SearchRoomsNewResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhaykant on 16/02/18.
 * Activity for Search space
 */

public class SearchSpacesMoreActivity extends BaseActivity implements View.OnTouchListener, SearchRoomClickListner, View.OnClickListener {
    private static final String TAG = SearchSpacesMoreActivity.class.getName();
    private ActivitySearchSpacesMoreBinding mBinder;
    private BaseCallback<SearchRoomsNewResponse> mSearchRoomsBaseCallback;
    private SearchSpacesMoreAdapter mAdapter;
    private RetrofitRestRepository mRepository;
    private int mCurrentPage = 1;
    private int mTotalPagesAvailable;
    private List<SearchRoomsNewModel> mRoomsList;
    LinearLayoutManager mLinearLayoutManager;
    private boolean isLoading;
    final Handler mHandler = new Handler();
    private String mQuery = "";
    private static final int isFilterHome = 1;

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

        mBinder.etSearch.setText(mQuery);

        mBinder.rvMoreItems.setOnTouchListener(this);

        mRepository = ((ApplicationController) this.getApplicationContext()).provideRepository();

        setupRecyclerView();

        mBinder.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                mHandler.postDelayed(() -> {
                    mQuery = charSequence.toString();
                    mCurrentPage = 1;
                 //   mRoomsList.clear();
                    searchRooms(mQuery, false);

                }, 200);

            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentPage = 1;
        searchRooms(mQuery, true);
    }

    private void setupRecyclerView() {
        mRoomsList = new ArrayList<>();
        mBinder.rvMoreItems.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(SearchSpacesMoreActivity.this, 19)));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinder.rvMoreItems.setLayoutManager(mLinearLayoutManager);


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
                    searchRooms(mQuery, false);
                }
            }
        }

    };


    public void searchRooms(String query, boolean isShowProgress) {

        //if query is blank and last search in room wasn't same search as before

        if (mSearchRoomsBaseCallback != null) {
            mSearchRoomsBaseCallback.cancel();
        }
        mBinder.rvMoreItems.addOnScrollListener(searchRoomsScrollListener);


        mSearchRoomsBaseCallback = new BaseCallback<SearchRoomsNewResponse>(this,
                mRepository.getApiService().listRoomsForSearch(isFilterHome, mCurrentPage,
                        Constants.LIST_LENGTH, query), isShowProgress) {

            @Override
            public void onSuccess(SearchRoomsNewResponse response) {
                hideProgressBar();
                mTotalPagesAvailable = response.getPagination().getTotalPages();
                int pos = 0;
                if (mCurrentPage == 1) {
                    mRoomsList.clear();
                    mRoomsList.addAll(response.getData());
                    mAdapter = new SearchSpacesMoreAdapter(SearchSpacesMoreActivity.this, SearchSpacesMoreActivity.this);
                    mBinder.rvMoreItems.setAdapter(mAdapter);
                    if ((response.getPagination().getNumberOfElements() == 0)) {

                        mBinder.tvNoResultFound.setVisibility(View.VISIBLE);
                    } else {
                        mBinder.tvNoResultFound.setVisibility(View.GONE);
                    }
                } else {
                    mRoomsList.addAll((response.getData()));
                }

                mAdapter.setmRoomsList(mRoomsList);
                mAdapter.notifyDataSetChanged();

                isLoading = false;
            }

            @Override
            public void onFail() {
                isLoading = false;
                hideProgressBar();

            }
        };
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideKeyboard();
        return false;
    }


    private void addRoom(int roomId, final SearchRoomsNewModel roomsModel) {
        showProgressBar();

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        new BaseCallback<AddRoomNewResponse>(SearchSpacesMoreActivity.this, mRepository.getApiService().addRoom(roomId)) {
            @Override
            public void onSuccess(AddRoomNewResponse response) {
                hideProgressBar();
                if (response.getMeta().getCode() == 200) {
                    if (roomsModel.getRoomType() == Constants.ROOM_TYPE.PRIVATE) {
                        roomsModel.setUserReqStatus(Constants.UserAddedRoomStatus.PENDING);
                        roomsModel.setRequestedByMe(true);
                    } else {
                        roomsModel.setUserReqStatus(Constants.UserAddedRoomStatus.ADDED);
                    }

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
    public void onRoomClick(ItemSearchBinding item, SearchRoomsNewModel roomsModel, int pos) {
//        LogLogUtils.LOGD(TAG, "userReqStatus:" + roomsModel.getUserReqStatus()
//                + " userName :" + roomsModel.getRoomName() + " roomId:" + roomsModel.getRoomId());

        if (roomsModel.getUserReqStatus() == Constants.UserAddedRoomStatus.NOT_INVITED || roomsModel.getUserReqStatus() == Constants.UserAddedRoomStatus.REJECTED) {
            addRoom(roomsModel.getRoomId(), roomsModel);
        }
    }

    @Override
    public void onInviteClick(ItemSearchBinding item, SearchRoomsNewModel roomsModel, int pos, boolean accept) {

        new BaseCallback<JoinRoomResponse>(SearchSpacesMoreActivity.this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().roomInvite(roomsModel.getRoomId(), accept), true) {

            @Override
            public void onSuccess(JoinRoomResponse response) {

                mRoomsList.get(pos).setUserReqStatus(response.getData().getUserReqStatus());
                mRoomsList.get(pos).setRequestedByMe(true);
                mAdapter.notifyItemChanged(pos);


            }

            @Override
            public void onFail() {
//
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

            default:
                break;
        }
    }
}


