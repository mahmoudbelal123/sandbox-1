/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.rooms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityHomeBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.activity.ActivitiesNotificationActivity;
import com.appster.turtle.ui.friends.FriendListActivity;
import com.appster.turtle.ui.notes.MyNotesActivity;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.ui.search.SearchRoomsAndPeopleActivity;
import com.appster.turtle.ui.settings.SettingsActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
/*
* Activity for home
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityHomeBinding homeBinding;
    private BroadcastReceiver mNotificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        pushFragment(Constants.FRAGMENTS.HOME_FRAGMENT, null, homeBinding.rlContainer.getId(), false);
        homeBinding.ivTrending.setOnClickListener(this);

        initDataBinding();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationReceiver, new IntentFilter(Constants.Notification.EVENT_NOTIFICATION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationReceiver);
    }

    private void initDataBinding() {
        homeBinding.layHomeMenu.setUser(PreferenceUtil.getUser());
        homeBinding.layHomeMenu.setUniv(PreferenceUtil.getUniv());

        if (getIntent().hasExtra(Constants.BundleKey.SHOW_TOUR) && getIntent().getBooleanExtra(Constants.BundleKey.SHOW_TOUR, false)) {
            homeBinding.rlTour.postDelayed(() -> startTour(), 1000);
        }

        initNotificationsReceiver();
    }

    private void initNotificationsReceiver() {
        mNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ((HomeFragment) getFragment(HomeFragment.class.getName())).updateNotificationCount();
            }
        };
    }

    private void startTour() {
        homeBinding.rlTour.setVisibility(View.VISIBLE);
        homeBinding.cardviewTab.setVisibility(View.INVISIBLE);
        onTourTap(homeBinding.rlTour);

        HomeFragment homeFragmemt = (HomeFragment) getFragment(HomeFragment.class.getName());

        homeFragmemt.hideToolbar();
    }

    @Override
    public String getActivityName() {
        return HomeActivity.class.getName();
    }

    public void creatRoomClicked(View view) {
        startActivity(new Intent(HomeActivity.this, CreateRoomActivity.class));
    }

    @Override
    protected void onNewIntent(Intent intent) {


        if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.BundleKey.ROOM)) {
            Room room = intent.getExtras().getParcelable(Constants.BundleKey.ROOM);
            if (getFragment(HomeFragment.class.getName()) != null) {
                ((HomeFragment) getFragment(HomeFragment.class.getName())).refresh(room);
            }

            ExplicitIntent.getsInstance().navigateTo(HomeActivity.this, RoomActivityCoordinator.class, intent.getExtras());

            if (room != null && room.isUserBlocked()) {
                Alert.createAlert(HomeActivity.this, "", getString(R.string.user_blocked_message));
            }

        } else if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.BundleKey.ROOM_REFRESH)) {
            Room room = intent.getExtras().getParcelable(Constants.BundleKey.ROOM_REFRESH);
            int pos = intent.getExtras().getInt(Constants.BundleKey.ROOM_ID);
            if (getFragment(HomeFragment.class.getName()) != null) {
                ((HomeFragment) getFragment(HomeFragment.class.getName())).refreshRoom(room, pos);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (homeBinding.drawerLayout.isDrawerOpen(Gravity.END))
            toggleDrawerLayout();
        else
            finishAffinity();
    }

    @Override
    public void onClick(View view) {
    }


    private void toggleDrawerLayout() {
        if (homeBinding.drawerLayout.isDrawerOpen(Gravity.END)) {
            homeBinding.drawerLayout.closeDrawer(Gravity.END);
        } else {
            homeBinding.drawerLayout.openDrawer(Gravity.END);
        }
    }


    public void toggleMenu(View view) {
        toggleDrawerLayout();
    }

    public void searchIconClicked(View view) {
        searchRoomsAndPeople();
    }

    public void searchEditTextClicked(View view) {
        searchRoomsAndPeople();
    }

    private void searchRoomsAndPeople() {
        ExplicitIntent.getsInstance().navigateTo(this, SearchRoomsAndPeopleActivity.class);
    }

    @Override
    public void menuClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu_close:
                toggleDrawerLayout();
                break;
            case R.id.tv_menu_profile:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(HomeActivity.this, UserProfileActivity.class);


                break;
            case R.id.tv_menu_friends:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(HomeActivity.this, FriendListActivity.class);


                break;
            case R.id.tv_menu_my_activity:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(HomeActivity.this, ActivitiesNotificationActivity.class);
                break;

            case R.id.ll_my_activity:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(HomeActivity.this, ActivitiesNotificationActivity.class);
                break;

            case R.id.tv_menu_my_notes:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(HomeActivity.this, MyNotesActivity.class);

                break;

            case R.id.tv_menu_settings:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(HomeActivity.this, SettingsActivity.class);
                break;

            case R.id.btn_sign_out:
                homeBinding.layHomeMenu.llMenuHomeOptions.setVisibility(View.GONE);
                homeBinding.layHomeMenu.btnSignOut.setVisibility(View.GONE);
                homeBinding.layHomeMenu.divider.setVisibility(View.INVISIBLE);
                homeBinding.layHomeMenu.clSignOutConfirm.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_cancel:
                homeBinding.layHomeMenu.clSignOutConfirm.setVisibility(View.GONE);
                homeBinding.layHomeMenu.divider.setVisibility(View.VISIBLE);

                homeBinding.layHomeMenu.llMenuHomeOptions.setVisibility(View.VISIBLE);
                homeBinding.layHomeMenu.btnSignOut.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_sign_out_confirm:
                logout();
                toggleDrawerLayout();
                break;

            default:
                break;
        }
    }


    public void onTourTap(View view) {

        switch (view.getId()) {
            case R.id.tv_finish:
                dismissTour();
                break;
            case R.id.tv_top_finish:
                dismissTour();
                break;

            case R.id.tv_bottom_finish:
                dismissTour();
                break;


            case R.id.iv_fab:
                changeTour(2);
                break;


            case R.id.iv_search_tour:
                changeTour(3);
                break;

            case R.id.iv_menu_tour:
                changeTour(4);
                break;


            case R.id.iv_close_bottom:
                changeTour(6);
                break;

            case R.id.iv_close_top:
                changeTour(6);
                break;


            default:
                break;

        }


    }

    private void changeTour(int i) {
        homeBinding.llStartTour.setVisibility(View.INVISIBLE);
        switch (i) {
            case 0:

                homeBinding.tvBottomHeader.setText(R.string.dashboard);
                homeBinding.tvBottomMsg.setText(R.string.tour_desc_home);

                homeBinding.rlBottom.setVisibility(View.VISIBLE);
                homeBinding.rlTop.setVisibility(View.INVISIBLE);


                break;

            case 2:
                homeBinding.rlBottom.setVisibility(View.VISIBLE);
                homeBinding.rlTop.setVisibility(View.INVISIBLE);

                homeBinding.tvBottomHeader.setText(R.string.create);
                homeBinding.tvBottomMsg.setText(R.string.tour_desc_create_room);


                break;


            case 3:
                homeBinding.rlBottom.setVisibility(View.INVISIBLE);
                homeBinding.rlTop.setVisibility(View.VISIBLE);

                homeBinding.rlTop.setBackgroundResource(R.drawable.ic_tutorial_bg_top_left);

                homeBinding.tvTopHeader.setText(R.string.tour_title_search);
                homeBinding.tvTopDesc.setText(R.string.tour_desc_search);


                break;

            case 4:

                homeBinding.rlBottom.setVisibility(View.INVISIBLE);
                homeBinding.rlTop.setVisibility(View.VISIBLE);

                homeBinding.rlTop.setBackgroundResource(R.drawable.ic_tutorial_bg_top_right);


                homeBinding.tvTopHeader.setText(R.string.menu_tut);
                homeBinding.tvTopDesc.setText(R.string.menu_tut_sub);


                break;

            case 5:
                dismissTour();
                break;

            case 6:
                homeBinding.rlBottom.setVisibility(View.INVISIBLE);
                homeBinding.rlTop.setVisibility(View.INVISIBLE);
                homeBinding.llStartTour.setVisibility(View.VISIBLE);

                break;

            default:
                break;
        }
    }

    private void dismissTour() {
        homeBinding.llStartTour.setVisibility(View.INVISIBLE);
        homeBinding.rlTour.setVisibility(View.GONE);
        homeBinding.cardviewTab.setVisibility(View.VISIBLE);
        HomeFragment homeFragmemt = (HomeFragment) getFragment(HomeFragment.class.getName());
        homeFragmemt.showToolbar();
    }

    public void updateNotificationCountDisplayed(int newCount) {
        if (newCount > 0) {
            homeBinding.layHomeMenu.tvNotificationCount.setVisibility(View.VISIBLE);
            homeBinding.layHomeMenu.tvNotificationCount.setText(String.valueOf(newCount));
        } else {
            homeBinding.layHomeMenu.tvNotificationCount.setVisibility(View.GONE);
        }
    }
}
