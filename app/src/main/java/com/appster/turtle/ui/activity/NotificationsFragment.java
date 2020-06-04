/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.NotificationAdapter;
import com.appster.turtle.databinding.FragmentNotificationsBinding;
import com.appster.turtle.model.MessageMeta;
import com.appster.turtle.model.Notification;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.DeleteNotificationRequest;
import com.appster.turtle.network.request.NotificationReadRequest;
import com.appster.turtle.network.request.SendInviteRequest;
import com.appster.turtle.network.response.FriendRequestResponse;
import com.appster.turtle.network.response.NotificationReadUnreadResponse;
import com.appster.turtle.network.response.NotificationsResponse;
import com.appster.turtle.network.response.RoomResponse;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.notes.NotesPurchaseActivity;
import com.appster.turtle.ui.notes.ReviewListActivity;
import com.appster.turtle.ui.post.PostDetailActivity;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.util.ActivityMessage;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

/**
 * Created  on 09/10/17 .
 */

public class NotificationsFragment extends BaseFragment implements NotificationAdapter.OnItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    FragmentNotificationsBinding mBinding;

    private NotificationAdapter mNotificationAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    ArrayList<Notification> mNotificationsList = new ArrayList<>();
    private int mNotificationCurrentPage = 1;
    private int mVisiblePostItemCount;
    private int mTotalPostItemCount;
    private int mPastVisiblesPostItems;
    private int mTotalPagesAvailable;
    private boolean isLoading, isAccpected;
    private RetrofitRestRepository repository;

    @Override
    public String getFragmentName() {
        return getClass().getName();
    }

    public NotificationsFragment() {
//
    }

    public static NotificationsFragment newInstance(int sectionNumber) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        initUI();
        return mBinding.getRoot();
    }

    private void initUI() {
        repository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        mNotificationAdapter = new NotificationAdapter(getActivity(), mNotificationsList);
        mNotificationAdapter.setOnItemClickListener(this);
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.rvNotifications.setLayoutManager(mLinearLayoutManager);
        mBinding.rvNotifications.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 4)));
        mBinding.rvNotifications.setAdapter(mNotificationAdapter);

        mBinding.rvNotifications.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mVisiblePostItemCount = mLinearLayoutManager.getChildCount();
                mTotalPostItemCount = mLinearLayoutManager.getItemCount();
                mPastVisiblesPostItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (mNotificationCurrentPage < mTotalPagesAvailable && ((mPastVisiblesPostItems + mVisiblePostItemCount) >= mTotalPostItemCount && !isLoading)) {
                    if (mNotificationsList != null) {
                        mNotificationCurrentPage++;
                        isLoading = true;
                        getNotifications();

                    }
                }
            }
        });

        mBinding.swipeRoom.setOnRefreshListener(() -> refresh());
    }


    public void refresh() {
        mNotificationCurrentPage = 1;
        mBinding.swipeRoom.setRefreshing(true);
        getNotifications();
    }


    private void getNotifications() {

        if (!mBinding.swipeRoom.isRefreshing())
            showProgressBar();

        new BaseCallback<NotificationsResponse>((BaseActivity) getActivity(), repository.getApiService().getNotification(mNotificationCurrentPage, Constants.LIST_LENGTH)) {

            @Override
            public void onSuccess(NotificationsResponse response) {
                mTotalPagesAvailable = response.getPagination().getTotalPages();
                mBinding.swipeRoom.setRefreshing(false);
                isLoading = false;
                if (response != null && response.getData() != null) {
                    if (mNotificationCurrentPage == 1) {
                        mNotificationsList.clear();
                    }
                    mNotificationsList.addAll(response.getData());
                    mNotificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail() {
                mBinding.swipeRoom.setRefreshing(false);
                isLoading = false;
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        getNotifications();
    }

    public void readNotification(final Notification notification, final int pos) {
        if (notification.getIsRead().equals("F")) {
            showProgressBar();
            NotificationReadRequest readRequest = new NotificationReadRequest(notification.getId());
            new BaseCallback<NotificationReadUnreadResponse>((BaseActivity) getActivity(), ((ApplicationController) getActivity().getApplicationContext()).provideRepository().getApiService().markAsRead(readRequest)) {

                @Override
                public void onSuccess(NotificationReadUnreadResponse response) {
                    hideHud();
                    if (!isAccpected)
                        notificationNavigation(notification, pos);
                    mNotificationAdapter.updateTrue(pos);
                }

                @Override
                public void onFail() {
                    hideHud();
                }
            };
        } else {
            notificationNavigation(notification, pos);
        }
    }

    private void notificationNavigation(Notification notification, int pos) {

        if (notification.getNotificationType() == ActivityMessage.NotificationType.FRIEND_REQUEST) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.USER_ID, notification.getData().getSenderIds());
            bundle.putInt(Constants.POS, pos);
            ExplicitIntent.getsInstance().navigateForResult(getActivity(), UserProfileActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, bundle);
        } else if (notification.getNotificationType() == ActivityMessage.NotificationType.NOTE_REVIEW_EVENT) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.NOTE_ID, notification.getData().getNoteId());
            bundle.putBoolean(Constants.IS_NEWADD, true);
            ExplicitIntent.getsInstance().navigateTo(getActivity(), ReviewListActivity.class, bundle);
        } else if (notification.getNotificationType() == ActivityMessage.NotificationType.NOTE_PURCHASED_EVENT) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.NOTE_ID, notification.getData().getNoteId());
            bundle.putBoolean(Constants.IS_NEWADD, true);
            ExplicitIntent.getsInstance().navigateTo(getActivity(), NotesPurchaseActivity.class, bundle);

        } else if (notification.getNotificationType() == ActivityMessage.NotificationType.POST_TEXT_COMMENT ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_PICTURE_COMMENT ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_VIDEO_COMMENT ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_GIF_COMMENT ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_POLL_COMMENT || notification.getNotificationType() == ActivityMessage.NotificationType.POST_MEETUP_COMMENT ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_TEXT_LIKE ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_PICTURE_LIKE ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_VIDEO_LIKE ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_GIF_LIKE ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_POLL_LIKE ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_MEETUP_RESPONSE_ATTENDING ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_MEETUP_RESPONSE_INTERESTED ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_POLL_RESPONSE ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_MEETUP_EDIT ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_USER_TAG ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_USER_TAG_MEDIA ||
                notification.getNotificationType() == ActivityMessage.NotificationType.POST_MEETUP_LIKE) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.POST_ID, Integer.parseInt(notification.getData().getPostId()));
            ExplicitIntent.getsInstance().navigateForResult(getActivity(), PostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_POST_DETAIL, bundle);
        } else if (notification.getNotificationType() == ActivityMessage.NotificationType.ROOM_REQUEST || notification.getNotificationType() == ActivityMessage.NotificationType.BECOME_ROOM_ADMIN || notification.getNotificationType() == ActivityMessage.NotificationType.ROOM_JOIN_REQUEST) {
            assert getActivity() != null;
            ((BaseActivity) getActivity()).openRoom(Integer.parseInt(notification.getData().getRoomId()), pos);
        }
    }

    @Override
    public void onItemClicked(Notification notification, int pos) {
        readNotification(notification, pos);

    }

    @Override
    public void onAcceptReject(Notification notification, int pos, int status) {
        if (notification.getNotificationType() == ActivityMessage.NotificationType.FRIEND_REQUEST) {
            actionOnRequest(notification, status == 1 ? Constants.MyFriend.ACCEPT_REQUEST : Constants.MyFriend.REJECT_REQUEST, pos);
        } else if (notification.getNotificationType() == ActivityMessage.NotificationType.ROOM_JOIN_REQUEST) {
            confirmUserRequest(status == 1 ? Constants.RoomRequest.ROOM_REQUEST_ACCEPT : Constants.RoomRequest.ROOM_REQUEST_DECLINE, notification, pos);
        } else {
            onRoomAccpectReject(status, pos, notification);
        }
    }


    private void actionOnRequest(final Notification notification, int action, final int pos) {
        LogUtils.LOGD("TAG1", notification.getData().getSenderIds() + "     ");
        showProgressBar();
        new BaseCallback<FriendRequestResponse>(getBaseActivity(), repository.getApiService().actionFriendRequest(notification.getData().getSenderIds(), action)) {
            @Override
            public void onSuccess(FriendRequestResponse response) {
                hideHud();
                if (response.getData().getUserReqStatus() == Constants.MyFriend.ALL_REQUESTS_AND_FRIENDS) {

                    for (MessageMeta messageMeta : notification.getMessageMeta()) {
                        switch (messageMeta.getType()) {
                            case ActivityMessage.MessageType.STATUS:
                                messageMeta.getData().set(messageMeta.getData().size() - 1, response.getData().getUserReqStatus());
                                break;
                        }
                    }
                    mNotificationAdapter.update(pos, notification);
                } else {
                    mNotificationAdapter.removeItem(pos);
                    DownloadWebPageTask task = new DownloadWebPageTask();
                    task.execute(notification.getId());

                }

            }

            @Override
            public void onFail() {
                hideHud();
            }
        };
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... id) {

            ArrayList<String> list = new ArrayList<>();
            list.add(id[0]);
            DeleteNotificationRequest request = new DeleteNotificationRequest(list);
            new BaseCallback<UserActionResponse>(getBaseActivity(), repository.getApiService().deleteNotification(request)) {

                @Override
                public void onSuccess(UserActionResponse response) {

                }

                @Override
                public void onFail() {

                }
            };
            return "Download failed";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private void onRoomAccpectReject(int action, final int pos, final Notification notification) {
        boolean accept = action == 1;
        showProgressBar();
        new BaseCallback<NotificationReadUnreadResponse>(getBaseActivity(), repository.getApiService().accpectRejectRoom(Integer.parseInt(notification.getData().getRoomId()), accept)) {
            @Override
            public void onSuccess(NotificationReadUnreadResponse response) {
                hideHud();
                for (MessageMeta messageMeta : notification.getMessageMeta()) {
                    switch (messageMeta.getType()) {
                        case ActivityMessage.MessageType.STATUS:
                            if (response.getData().getUserReqStatus() == 1) {
                                try {
                                    messageMeta.getData().set(messageMeta.getData().size() - 1, response.getData().getUserReqStatus());
                                    mNotificationAdapter.update(pos, notification);
                                } catch (Exception e) {
                                    LogUtils.LOGD("TAG", e.getMessage());
                                }
                            } else {
                                mNotificationAdapter.removeItem(pos);
                                DownloadWebPageTask task = new DownloadWebPageTask();
                                task.execute(notification.getId());

                            }
                            break;
                    }
                }

            }

            @Override
            public void onFail() {

                hideHud();
            }
        };
    }

    public void confirmUserRequest(int isAccept, final Notification notification, final int pos) {

        showProgressBar();

        ArrayList<Integer> userIds = new ArrayList<>();
        userIds.add(notification.getData().getSenderIds());
        SendInviteRequest sendInviteRequest = new SendInviteRequest();
        sendInviteRequest.setUserIds(userIds);
        sendInviteRequest.setId(Integer.parseInt(notification.getData().getRoomId()));

        new BaseCallback<RoomResponse>((BaseActivity) getActivity(), repository.getApiService().actionRoomRequestNotification(isAccept, sendInviteRequest)) {

            @Override
            public void onSuccess(RoomResponse response) {
                hideHud();
                for (MessageMeta messageMeta : notification.getMessageMeta()) {
                    switch (messageMeta.getType()) {
                        case ActivityMessage.MessageType.STATUS:
                            if (response.getData().getUserReqStatus() == 1) {


                                messageMeta.getData().set(messageMeta.getData().size() - 1, response.getData().getUserReqStatus());
                                mNotificationAdapter.update(pos, notification);
                            } else {
                                mNotificationAdapter.removeItem(pos);
                                DownloadWebPageTask task = new DownloadWebPageTask();
                                task.execute(notification.getId());

                            }
                            break;
                    }
                }


            }

            @Override
            public void onFail() {
                hideHud();
            }
        };


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {


            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == Constants.REQUEST_CODE.ROOM_UPDATED) {
                    mNotificationAdapter.updateRoom(data.getIntExtra(Constants.POS, 0), data.getParcelableExtra(Constants.BundleKey.ROOM_REFRESH));
                } else if (requestCode == Constants.REQUEST_CODE.REQUEST_REFRESH) {
                    mNotificationAdapter.updateUser(data.getIntExtra(Constants.POS, 0), data.getParcelableExtra(Constants.USER));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
