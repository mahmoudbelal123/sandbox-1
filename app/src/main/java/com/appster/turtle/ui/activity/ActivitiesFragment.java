/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.activity;

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
import com.appster.turtle.adapter.ActivityAdapter;
import com.appster.turtle.databinding.FragmentActivitiesBinding;
import com.appster.turtle.model.Activities;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetActivitiesResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.post.PostDetailActivity;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.util.ActivityMessage;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

/**
 * Created by rohan on 09/10/17.
 * A fragment class for Activities
 */

public class ActivitiesFragment extends BaseFragment implements ActivityAdapter.OnItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    FragmentActivitiesBinding mBinding;

    private ActivityAdapter mActivityAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    ArrayList<Activities> mActivitiesList = new ArrayList<>();
    private int mActivityCurrentPage = 1;
    private int mTotalPagesAvailable;
    private boolean isLoading;
    private String date = "";

    @Override
    public String getFragmentName() {
        return getClass().getName();
    }

    public ActivitiesFragment() {
//
    }

    public static ActivitiesFragment newInstance(int sectionNumber) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activities, container, false);
        initUI();
        return mBinding.getRoot();
    }

    private void initUI() {
        mActivityAdapter = new ActivityAdapter(getActivity(), mActivitiesList);
        mActivityAdapter.setOnItemClickListener(this);
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mBinding.rvActivity.setLayoutManager(mLinearLayoutManager);
        mBinding.rvActivity.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 14)));
        mBinding.rvActivity.setAdapter(mActivityAdapter);
        mBinding.rlDate.getRoot().setVisibility(View.GONE);

        mBinding.rvActivity.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int mVisibleItemCount = mLinearLayoutManager.getChildCount();
                int mPastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                int mTotalItemCount = mLinearLayoutManager.getItemCount();

                if (mActivityCurrentPage < mTotalPagesAvailable && (mVisibleItemCount + mPastVisibleItems <= mTotalItemCount) && !isLoading) {
                    if (mActivitiesList != null) {
                        mActivityCurrentPage++;
                        isLoading = true;
                        getActivities();
                    }
                }

                if (mActivitiesList != null && mActivitiesList.size() != 0 && !date.equals(Utils.getDOBFromMillis(mActivitiesList.get(mLinearLayoutManager.findFirstVisibleItemPosition()).getCreatedAt()))) {
                    mBinding.rlDate.getRoot().setVisibility(View.VISIBLE);

                    if (mActivitiesList != null && !mActivitiesList.isEmpty())
                        mBinding.rlDate.tvDate.setText(Utils.getDOBFromMillis(mActivitiesList.get(mLinearLayoutManager.findFirstVisibleItemPosition()).getCreatedAt()));
                }

            }
        });
    }

    private void getActivities() {

        RetrofitRestRepository repository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        new BaseCallback<GetActivitiesResponse>((BaseActivity) getActivity(), repository.getApiService()
                .getActivities(mActivityCurrentPage, Constants.LIST_LENGTH), mActivityCurrentPage == 1) {
            @Override
            public void onSuccess(GetActivitiesResponse response) {
                try {
                    mTotalPagesAvailable = response.getPagination().getTotalPages();
                    isLoading = false;
                    getBaseActivity().hideProgressBar();
                    if (response != null && response.getData() != null) {
                        if (mActivityCurrentPage == 1) {
                            mActivitiesList.clear();
                            mActivitiesList.addAll(response.getData());
                            mBinding.rlDate.getRoot().setVisibility(View.VISIBLE);
                            mActivityAdapter.notifyDataSetChanged();
                            if (mActivitiesList.size() > 0)
                                mBinding.rlDate.tvDate.setText(Utils.getDOBFromMillis(mActivitiesList.get(0).getCreatedAt()));
                            else
                                mBinding.rlDate.getRoot().setVisibility(View.GONE);
                        } else {
                            mActivitiesList.addAll(response.getData());
                            mActivityAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    LogUtils.LOGE(ActivitiesFragment.class.getSimpleName(), e.getMessage());
                }
            }

            @Override
            public void onFail() {
                isLoading = false;
                getBaseActivity().hideProgressBar();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivityCurrentPage = 1;
        getActivities();
    }

    @Override
    public void onItemClicked(Activities activities, int pos) {
        navigation(activities, pos);
    }

    private void navigation(Activities notification, int pos) {

        if (notification.getActivityType() == ActivityMessage.ActivityType.ACCEPT_FRIEND_REQUEST) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.USER_ID, notification.getData().getSenderIds());
            ExplicitIntent.getsInstance().navigateTo(getActivity(), UserProfileActivity.class, bundle);
        } else if (notification.getActivityType() == ActivityMessage.ActivityType.POST_TEXT_COMMENT ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_PICTURE_COMMENT ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_VIDEO_COMMENT ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_GIF_COMMENT ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_POLL_COMMENT ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_MEETUP_COMMENT ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_TEXT_LIKE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_PICTURE_LIKE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_VIDEO_LIKE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_GIF_LIKE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_POLL_LIKE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_TEXT_COMMENT_LIKE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_POLL ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_MEETUP ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_VIDEO ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_PICTURE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_TEXT ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_GIF ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_MEETUP_RESPONSE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_MEETUP_COMMENT_LIKE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_POLL_COMMENT_LIKE ||
                notification.getActivityType() == ActivityMessage.ActivityType.POST_MEETUP_LIKE) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.POST_ID, Integer.parseInt(notification.getData().getPostId()));
            ExplicitIntent.getsInstance().navigateForResult(getActivity(), PostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_POST_DETAIL, bundle);
        } else if (notification.getActivityType() == ActivityMessage.ActivityType.JOIN_ROOM || notification.getActivityType() == ActivityMessage.ActivityType.CREATE_ROOM || notification.getActivityType() == ActivityMessage.ActivityType.MAKE_ROOM_ADMIN) {
            if (getActivity() != null) {
                ((BaseActivity) getActivity()).openRoom(Integer.parseInt(notification.getData().getRoomId()), pos);
            }
        }
    }
}
