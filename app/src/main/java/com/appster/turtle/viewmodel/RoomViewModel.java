/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BR;
import com.appster.turtle.R;
import com.appster.turtle.adapter.RoomAdapter;
import com.appster.turtle.databinding.LayoutRoomOptionsBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.UserActionRequest;
import com.appster.turtle.network.response.Meetup;
import com.appster.turtle.network.response.PostResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.PostsResponse;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.meetup.CreateMeetupActivity;
import com.appster.turtle.ui.poll.CreatePollActivity;
import com.appster.turtle.ui.rooms.LikedByActivity;
import com.appster.turtle.ui.rooms.MeetupsActivity;
import com.appster.turtle.ui.rooms.RoomActivity;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.ui.textpost.CreateTextPostActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.widget.RecyclerScrollListener;
import com.flipboard.bottomsheet.BottomSheetLayout;

import rx.Observable;

/**
 * Created by atul on 31/08/17.
 * To inject activity reference.
 */

public class RoomViewModel extends BaseObservable implements RoomAdapter.OnClickListener, RoomAdapter.IUpdateMeetupStatus {

    private LayoutRoomOptionsBinding mEditBottomSheetBinding;
    private BottomSheetLayout mBottomSheetLayout;
    private Room room;
    public int offset = 1; //NOSONAR
    private boolean refreshing;
    private boolean noMoreData;
    private BaseActivity mActivity;
    private RoomAdapter roomAdapter;
    private RetrofitRestRepository repository;
    private LinearLayoutManager linearLayoutManager;

    public RoomViewModel(Context context, Room room) {
        if (context instanceof RoomActivity)
            mActivity = (RoomActivity) context;
        else if (context instanceof MeetupsActivity)
            mActivity = (MeetupsActivity) context;
        else if (context instanceof RoomActivityCoordinator)
            mActivity = (RoomActivityCoordinator) context;

        this.room = room;
        this.repository = ((ApplicationController) mActivity.getApplicationContext()).provideRepository();
        linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        roomAdapter = new RoomAdapter(context);
        roomAdapter.setOnClickListener(this);
        roomAdapter.setOnMeetupStatusChangeListener(this);
        if (mActivity instanceof RoomActivity) {
            mEditBottomSheetBinding = ((RoomActivity) mActivity).getEditBottomSheetBinding();
            mBottomSheetLayout = ((RoomActivity) mActivity).getmBottomSheetMembers();
        } else if (mActivity instanceof MeetupsActivity) {
            mEditBottomSheetBinding = ((MeetupsActivity) mActivity).getEditBottomSheetBinding();
            mBottomSheetLayout = ((MeetupsActivity) mActivity).getmBottomSheetMembers();
        } else if (mActivity instanceof RoomActivityCoordinator) {
            mEditBottomSheetBinding = ((RoomActivityCoordinator) mActivity).getEditBottomSheetBinding();
            mBottomSheetLayout = ((RoomActivityCoordinator) mActivity).getmBottomSheetMembers();
        }
        mEditBottomSheetBinding.tvEdit.setText(R.string.edit);


    }


    public View.OnClickListener onPollClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.ROOM_ID, room.getRoomId());
            ExplicitIntent.getsInstance().navigateTo(mActivity, CreatePollActivity.class, bundle);
        }
    };

    public View.OnClickListener onMediaClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View view) {


            ((RoomActivity) mActivity).toggleMediaBottom();

        }
    };

    public View.OnClickListener onMeetUpClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BundleKey.ROOM, room);
            ExplicitIntent.getsInstance().navigateTo(mActivity, CreateMeetupActivity.class, bundle);
        }
    };

    public void getPosts(final boolean isShowProgress) {
        getPosts(isShowProgress, false);
    }

    public void getPosts(final boolean isShowProgress, boolean getOnlyMeetups) {
        if (isShowProgress) {
            mActivity.showProgressBar();
        }

        Observable<PostsResponse> observable;

        if (getOnlyMeetups)
            observable = repository.getApiService().getPosts(room.getRoomId(), offset, Constants.RECORD_PER_PAGE, Constants.VIEW_TYPE.MEET_UP);
        else {
            observable = repository.getApiService().getPosts(room.getRoomId(), offset, Constants.RECORD_PER_PAGE, Constants.POST_QUERY);
        }
        new BaseCallback<PostsResponse>(mActivity, observable) {
            @Override
            public void onSuccess(PostsResponse postsResponse) {
                if (isShowProgress)
                    mActivity.hideProgressBar();


            }

            @Override
            public void onFail() {
                setRefreshing(false);
            }
        };
    }


    @Bindable
    public RoomAdapter getAdapter() {
        return roomAdapter;
    }

    @Bindable
    public RecyclerView.OnScrollListener getScrollListener() {
        return new RecyclerScrollListener(getLayoutManager()) {
            @Override
            public void onLoadMore() {
                if (!noMoreData && !roomAdapter.isLoadMore()) {
                    roomAdapter.showLoadMore();
                    ++offset;
                    getPosts(false);
                }
            }
        };
    }

    @Bindable
    public boolean getRefreshing() {
        return refreshing;
    }

    private void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        notifyPropertyChanged(BR.refreshing);
    }

    @Bindable
    public LinearLayoutManager getLayoutManager() {
        return linearLayoutManager;
    }

    @Bindable
    public SwipeRefreshLayout.OnRefreshListener getRefreshListener() {
        return () -> {
            setRefreshing(true);
            offset = 1;
            getPosts(false);
        };
    }

    @Bindable
    public SwipeRefreshLayout.OnRefreshListener getMeetupsRefreshListener() {
        return () -> {
            setRefreshing(true);
            offset = 1;
            getPosts(false, true);
        };
    }

    @Bindable
    public RecyclerView.OnScrollListener getMeetupsScrollListener() {
        return new RecyclerScrollListener(getLayoutManager()) {
            @Override
            public void onLoadMore() {
                if (!noMoreData && !roomAdapter.isLoadMore()) {
                    roomAdapter.showLoadMore();
                    ++offset;
                    getPosts(false, true);
                }
            }
        };
    }

    @Override
    public void onClick(final int position, int postId, int answerId) {
        mActivity.showProgressBar();
        new BaseCallback<PostResponse>(mActivity, repository.getApiService()
                .respondPoll(postId, answerId)) {
            @Override
            public void onSuccess(PostResponse postsResponse) {
                roomAdapter.updateItem(position, postsResponse.getData());
                mActivity.hideProgressBar();
            }

            @Override
            public void onFail() {
                setRefreshing(false);
            }
        };
    }

    @Override
    public void onLiked(final int position, int postId, int likeStatus) {
        new BaseCallback<PostResponse>(mActivity, repository.getApiService()
                .likeDislikePost(postId, likeStatus == Constants.LikeStatus.NOT_LIKED ? Constants.LikeStatus.LIKED : Constants.LikeStatus.NOT_LIKED)) {
            @Override
            public void onSuccess(PostResponse likeDislike) {
                roomAdapter.updateItem(position, likeDislike.getData());
            }

            @Override
            public void onFail() {
                setRefreshing(false);
            }
        };
    }

    @Override
    public void onDownArrowClick(final int position, int postId) {

        final Posts post = roomAdapter.getItem(position);
        if (post.getUsersListModel().getUserId() == PreferenceUtil.getUser().getUserId()) {

            if (post.getPostType() == Constants.VIEW_TYPE.TEXT || post.getPostType() == Constants.VIEW_TYPE.MEET_UP) {
                mEditBottomSheetBinding.tvEdit.setVisibility(View.VISIBLE);
            } else {
                mEditBottomSheetBinding.tvEdit.setVisibility(View.GONE);

            }
            mEditBottomSheetBinding.tvEdit.setText(R.string.edit);
            mEditBottomSheetBinding.tvDelete.setVisibility(View.VISIBLE);
            mEditBottomSheetBinding.tvEdit.setOnClickListener(view -> {
                mBottomSheetLayout.dismissSheet();

                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.MEETUP, post);
                bundle.putBoolean(Constants.BundleKey.FOR_EDIT, true);

                if (post.getPostType() == Constants.VIEW_TYPE.MEET_UP) {
                    ExplicitIntent.getsInstance().navigateTo(mActivity, CreateMeetupActivity.class, bundle);
                } else if (post.getPostType() == Constants.VIEW_TYPE.TEXT) {
                    ExplicitIntent.getsInstance().navigateTo(mActivity, CreateTextPostActivity.class, bundle);

                }

            });

            mEditBottomSheetBinding.tvDelete.setOnClickListener(view -> {
                mBottomSheetLayout.dismissSheet();

                UserActionRequest request = new UserActionRequest();
                request.setRoomId(post.getPostId());
                Observable<UserActionResponse> userActionResponseObservable = repository.getApiService().deletePost(request);
                mActivity.showProgressBar();
                new BaseCallback<UserActionResponse>(mActivity, userActionResponseObservable) {
                    @Override
                    public void onSuccess(UserActionResponse response) {
                        mActivity.hideProgressBar();
                        mActivity.showSnackBar("Post deleted successfully");
                        roomAdapter.removeItem(position);
                    }

                    @Override
                    public void onFail() {
                        mActivity.hideProgressBar();

                    }
                };
            });
        } else {
            mEditBottomSheetBinding.tvEdit.setText(R.string.flag);
            mEditBottomSheetBinding.tvDelete.setVisibility(View.GONE);

            mEditBottomSheetBinding.tvEdit.setOnClickListener(view -> {


                mBottomSheetLayout.dismissSheet();

                Posts item = post;

                if (item.isFlaggedByMe()) {
                    mActivity.showSnackBar("Post already flagged");
                    return;
                }

                UserActionRequest request = new UserActionRequest();
                request.setRoomId(item.getPostId());
                Observable<UserActionResponse> userActionResponseObservable = repository.getApiService().flagPost(request);
                mActivity.showProgressBar();

                new BaseCallback<UserActionResponse>(mActivity, userActionResponseObservable) {

                    @Override
                    public void onSuccess(UserActionResponse response) {

                        mActivity.hideProgressBar();

                        mActivity.showSnackBar("Post flagged successfully");
                        post.setFlaggedByMe(true);
                    }

                    @Override
                    public void onFail() {
                        mActivity.hideProgressBar();
                    }
                };

            });

        }


        mBottomSheetLayout.showWithSheetView(mEditBottomSheetBinding.getRoot());
        mEditBottomSheetBinding.tvCancel.setOnClickListener(view -> mBottomSheetLayout.dismissSheet());

    }

    @Override
    public void onLikedBy(int position, int postId) {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKey.POST, postId);
        ExplicitIntent.getsInstance().navigateTo(mActivity, LikedByActivity.class, bundle);
    }

    @Override
    public void updateMeetupStatus(final int position, int postId, int status, boolean reset) {
        mActivity.showProgressBar();

        new BaseCallback<Meetup>(mActivity, repository.getApiService().meetupStatusChanged(postId, status, reset)) {

            @Override
            public void onSuccess(Meetup meetup) {
                roomAdapter.updateMeetupItem(position, meetup.getData());
                mActivity.hideProgressBar();
            }

            @Override
            public void onFail() {
                setRefreshing(false);
            }
        };
    }

    @Bindable
    public String getQuery() {
        return Constants.POST_QUERY;
    }


}


