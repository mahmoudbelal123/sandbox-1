package com.appster.turtle.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.ActivityAdapter;
import com.appster.turtle.adapter.NotificationAdapter;
import com.appster.turtle.databinding.ActivityNotificationTabbedBinding;
import com.appster.turtle.model.Activities;
import com.appster.turtle.model.Notification;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.UnreadNotifCountResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
/**
 * A activity class for ActivitiesNotification
 */
public class ActivitiesNotificationActivity extends BaseActivity implements View.OnClickListener, NotificationAdapter.OnItemClickListener, ActivityAdapter.OnItemClickListener, BaseActivity.OnNotificationGet {

    private ActivityNotificationTabbedBinding mBinding;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private boolean isBackgroundUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification_tabbed);
        initUI();
        setOnNotificationGet(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotification();
    }

    @Override
    public String getActivityName() {
        return ActivitiesNotificationActivity.class.getName();
    }

    private void initUI() {

        setUpHeader(mBinding.clHeader.clHeader, getString(R.string.notifications), "", R.drawable.back_arrow);

        mBinding.tvActivityHeader.setOnClickListener(this);
        mBinding.tvNotificationsHeader.setOnClickListener(this);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mBinding.container.setAdapter(sectionsPagerAdapter);
        mBinding.container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        notificationTabSelected();
                        break;

                    case 1:
                        activitiesTabSelected();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
//
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//
            }
        });

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_activity_header:
                mBinding.container.setCurrentItem(1);
                activitiesTabSelected();
                break;

            case R.id.tv_notifications_header:
                mBinding.container.setCurrentItem(0);
                notificationTabSelected();
                break;

            default:
                break;
        }

    }


    private void notificationTabSelected() {

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Notification.RESET_NOTIF_COUNT));
        mBinding.tvActivityHeader.setTextColor(ContextCompat.getColor(ActivitiesNotificationActivity.this, R.color.gray));
        mBinding.tvNotificationsHeader.setTextColor(ContextCompat.getColor(ActivitiesNotificationActivity.this, R.color.black));
        mBinding.vActivityIndicator.setVisibility(View.INVISIBLE);
        mBinding.vNotificationsIndicator.setVisibility(View.VISIBLE);
    }

    private void activitiesTabSelected() {
        mBinding.tvNotificationsHeader.setTextColor(ContextCompat.getColor(ActivitiesNotificationActivity.this, R.color.gray));
        mBinding.tvActivityHeader.setTextColor(ContextCompat.getColor(ActivitiesNotificationActivity.this, R.color.black));
        mBinding.vNotificationsIndicator.setVisibility(View.INVISIBLE);
        mBinding.vActivityIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(Notification notification, int pos) {
        if (sectionsPagerAdapter.getCurrentFragment() instanceof NotificationsFragment) {

            sectionsPagerAdapter.notificationsFragment.readNotification(notification, pos);
        }
    }

    @Override
    public void onAcceptReject(Notification notification, int pos, int status) {
//
    }

    @Override
    public void onItemClicked(Activities activities, int pos) {
        if (sectionsPagerAdapter.getCurrentFragment() instanceof ActivitiesFragment) {
            sectionsPagerAdapter.activitiesFragment.onItemClicked(activities, pos);
        }
    }

    @Override
    public void onNotificationGet() {
        isBackgroundUpdate = false;
        updateNotification();

    }

    @Override
    public void onUnreadNotification() {
//
    }

    public class ItemDecorationView extends RecyclerView.ItemDecoration {
        private int itemBottomOffset;
        private int itemTopOffset;

        public ItemDecorationView(Context context, int itemTopOffset, int itemBottomOffset) {

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

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        NotificationsFragment notificationsFragment;
        ActivitiesFragment activitiesFragment;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getCurrentFragment() {
            int position = mBinding.container.getCurrentItem();

            if (position == 0) {
                return notificationsFragment;
            } else {
                return activitiesFragment;
            }
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                if (notificationsFragment == null) {
                    return NotificationsFragment.newInstance(position);
                }

                return notificationsFragment;
            } else {
                if (activitiesFragment == null) {
                    return ActivitiesFragment.newInstance(position);
                }

                return activitiesFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    public void updateNotification() {

        if (isBackgroundUpdate)
            showProgressBar();

        RetrofitRestRepository repository = ((ApplicationController) getApplicationContext()).provideRepository();
        new BaseCallback<UnreadNotifCountResponse>(this, repository.getApiService().markRead(), false) {

            @Override
            public void onSuccess(UnreadNotifCountResponse response) {
                isBackgroundUpdate = !isBackgroundUpdate;
                hideProgressBar();
                if (response.getData().getCount() > 0) {
                    mBinding.vNotification.setVisibility(View.VISIBLE);
                } else {
                    mBinding.vNotification.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFail() {
                hideKeyboard();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
