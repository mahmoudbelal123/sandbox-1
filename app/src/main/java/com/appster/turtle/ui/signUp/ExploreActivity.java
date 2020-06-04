package com.appster.turtle.ui.signUp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityHomeNoiseBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.activity.ActivitiesNotificationActivity;
import com.appster.turtle.ui.friends.FriendListActivity;
import com.appster.turtle.ui.notes.MyNotesActivity;
import com.appster.turtle.ui.rooms.CreateRoomActivity;
import com.appster.turtle.ui.rooms.HomeFragment;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.ui.search.SearchRoomsAndPeopleActivity;
import com.appster.turtle.ui.settings.SettingsActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;


/**
 * Created  on 22/01/18 .
 * Activity for explore
 */

public class ExploreActivity extends BaseActivity implements View.OnClickListener {

    private ActivityHomeNoiseBinding homeBinding;
    private BroadcastReceiver mNotificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_noise);
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
//        if (getIntent().hasExtra(Constants.BundleKey.SHOW_TOUR) && getIntent().getBooleanExtra(Constants.BundleKey.SHOW_TOUR, false)) {
////            homeBinding.tour.rlTour.postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    startTour();
////                }
////            }, 1000);
////            startTour();
//            homeBinding.tour.getRoot().setVisibility(View.VISIBLE);
//
//
//        } else {
//            homeBinding.tour.getRoot().setVisibility(View.VISIBLE);
//
//
//        }

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
        homeBinding.tour.rlTour.setVisibility(View.VISIBLE);
        onTourTap(homeBinding.tour.rlTour);

        // HomeFragment homeFragmemt = (HomeFragment) getFragment(HomeFragment.class.getValue());

//        homeFragmemt.hideToolbar();
    }

    @Override
    public String getActivityName() {
        return ExploreActivity.class.getName();
    }

    public void creatRoomClicked(View view) {
        startActivity(new Intent(this, CreateRoomActivity.class));
    }

    public void onDrawerClicked(View view) {
        toggleDrawerLayout();
    }

    @Override
    protected void onNewIntent(Intent intent) {


        if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.BundleKey.ROOM)) {
            Room room = intent.getExtras().getParcelable(Constants.BundleKey.ROOM);
            if (getFragment(HomeFragment.class.getName()) != null) {
                ((HomeFragment) getFragment(HomeFragment.class.getName())).refresh(room);
            }

//            ExplicitIntent.getsInstance().navigateTo(this, RoomActivity.class, intent.getExtras());
            ExplicitIntent.getsInstance().navigateTo(this, RoomActivityCoordinator.class, intent.getExtras());

            if (room != null && room.isUserBlocked()) {
                Alert.createAlert(this, "", getString(R.string.user_blocked_message));
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
//            super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_trending:

                break;
        }
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

    public void menuClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_menu_close:
                toggleDrawerLayout();
                break;
            case R.id.tv_menu_profile:

                break;
            case R.id.tv_menu_friends:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(this, FriendListActivity.class);


                break;
            case R.id.tv_menu_my_activity:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(this, ActivitiesNotificationActivity.class);
                break;

            case R.id.ll_my_activity:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(this, ActivitiesNotificationActivity.class);
                break;

//            case R.id.tv_menu_invite:
//                toggleDrawerLayout();
//                ExplicitIntent.getsInstance().navigateTo(this, InviteFriendsActivity.class);
//
//                break;
            case R.id.tv_menu_my_notes:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(this, MyNotesActivity.class);

                break;

            case R.id.tv_menu_settings:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(this, SettingsActivity.class);


                break;

            case R.id.btn_sign_out:
                homeBinding.layHomeMenu.llMenuHomeOptions.setVisibility(View.GONE);
                homeBinding.layHomeMenu.btnSignOut.setVisibility(View.GONE);
                homeBinding.layHomeMenu.divider.setVisibility(View.INVISIBLE);

                homeBinding.layHomeMenu.clSignOutConfirm.setVisibility(View.VISIBLE);
//                logout(view);
//                toggleDrawerLayout();

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
        }
    }

//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RESULT_PICK_CONTACT && resultCode == Activity.RESULT_OK &&
//                data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {
//
//            // we got a result from the contact picker
//
//            // process contacts
//            List<Contact> contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
//            for (Contact contact : contacts) {
//                // process the contacts...
//            }
//
//            // process groups
////            List<Group> groups = (List<Group>) data.getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA);
////            for (Group group : groups) {
////                // process the groups...
////            }
//        }
//        else
//            super.onActivityResult(requestCode,resultCode,data);
//    }


    public void onTourTap(View view) {
        switch (view.getId()) {
            case R.id.tv_finish:
                dismissTour();
                break;
            case R.id.rl_home_explore:
                changeTour(1);
                break;
            case R.id.search_tour:
                changeTour(0);
                break;

            case R.id.iv_fab:
                changeTour(2);
                break;
            case R.id.iv_close_bottom:
                changeTour(6);
                break;
            case R.id.iv_close_top:
                changeTour(6);
                break;
            case R.id.iv_close_centre:
                changeTour(6);
                break;

//
//            case R.id.iv_fab:
//                changeTour(0);
//                break;
//
//
//            case R.id.iv_profile_tour:
//                changeTour(3);
//                break;
//
//            case R.id.iv_menu_tour:
//                changeTour(4);
//                break;
//
//            case R.id.iv_search_tour:
//                changeTour(2);
//                break;
//
//            case R.id.iv_close_bottom:
//                changeTour(6);
//                break;
//
//            case R.id.iv_close_top:
//                changeTour(6);
//                break;

            default:
                break;

        }


    }

    private void changeTour(int i) {
        homeBinding.tour.llStartTour.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams parms = ((RelativeLayout.LayoutParams) homeBinding.tour.tvFinish.getLayoutParams());

        switch (i) {
            case 0:
                homeBinding.tour.rlCentre.setVisibility(View.VISIBLE);
                homeBinding.tour.rlBottom.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlTop.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlHomeExplore.setVisibility(View.INVISIBLE);
                homeBinding.tour.searchTour.setVisibility(View.VISIBLE);
                homeBinding.tour.ivFab.setVisibility(View.INVISIBLE);
                homeBinding.searchTour.setAlpha(1f);
                parms.addRule(RelativeLayout.BELOW, R.id.rl_bottom);
                parms.removeRule(RelativeLayout.ABOVE);
                break;

            case 1:
                homeBinding.tour.rlCentre.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlBottom.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlTop.setVisibility(View.VISIBLE);
                homeBinding.tour.rlHomeExplore.setVisibility(View.VISIBLE);
                homeBinding.tour.searchTour.setVisibility(View.INVISIBLE);
                homeBinding.tour.ivFab.setVisibility(View.INVISIBLE);
                homeBinding.searchTour.setAlpha(0.5f);
                parms.addRule(RelativeLayout.BELOW, R.id.rl_bottom);
                parms.removeRule(RelativeLayout.ABOVE);

                break;
            case 2:
                homeBinding.tour.rlCentre.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlBottom.setVisibility(View.VISIBLE);
                homeBinding.tour.rlTop.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlHomeExplore.setVisibility(View.INVISIBLE);
                homeBinding.tour.searchTour.setVisibility(View.INVISIBLE);
                homeBinding.tour.ivFab.setVisibility(View.VISIBLE);
                homeBinding.searchTour.setAlpha(1f);
                parms.addRule(RelativeLayout.BELOW, R.id.rl_bottom);
                parms.removeRule(RelativeLayout.ABOVE);
                break;
            case 3:
                homeBinding.tour.rlCentre.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlBottom.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlTop.setVisibility(View.VISIBLE);
                homeBinding.tour.rlTop.setBackgroundResource(R.drawable.ic_tutorial_bg_top_left);
                homeBinding.tour.tvTopHeader.setText(R.string.tour_title_find_room);
                homeBinding.tour.tvTopDesc.setText(R.string.tour_find_room);
                homeBinding.searchTour.setAlpha(1f);
                parms.addRule(RelativeLayout.ABOVE, R.id.iv_fab);
                parms.removeRule(RelativeLayout.BELOW);
                break;

            case 4:

                homeBinding.tour.rlCentre.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlTop.setVisibility(View.VISIBLE);
                homeBinding.tour.rlBottom.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlTop.setBackgroundResource(R.drawable.ic_tutorial_bg_top_right);
                homeBinding.tour.tvTopHeader.setText(R.string.menu_tut);
                homeBinding.tour.tvTopDesc.setText(R.string.menu_tut_sub);
                parms.addRule(RelativeLayout.ABOVE, R.id.iv_fab);
                homeBinding.searchTour.setAlpha(1f);
                parms.removeRule(RelativeLayout.BELOW);
                break;

            case 5:

                dismissTour();
                homeBinding.searchTour.setAlpha(1f);
                parms.addRule(RelativeLayout.ABOVE, R.id.iv_fab);
                parms.removeRule(RelativeLayout.BELOW);
                break;

            case 6:
                homeBinding.tour.rlCentre.setVisibility(View.INVISIBLE);
                homeBinding.tour.rlTop.setVisibility(View.INVISIBLE);
                homeBinding.tour.llStartTour.setVisibility(View.VISIBLE);
                homeBinding.tour.rlBottom.setVisibility(View.INVISIBLE);
                parms.addRule(RelativeLayout.ABOVE, R.id.iv_fab);
                parms.removeRule(RelativeLayout.BELOW);
                homeBinding.searchTour.setAlpha(1f);
                homeBinding.tour.searchTour.setVisibility(View.VISIBLE);
                homeBinding.tour.ivFab.setVisibility(View.VISIBLE);
                homeBinding.tour.rlHomeExplore.setVisibility(View.VISIBLE);
                break;
        }

        homeBinding.tour.tvFinish.setLayoutParams(parms);
    }

    private void dismissTour() {
        homeBinding.tour.llStartTour.setVisibility(View.INVISIBLE);
        homeBinding.tour.rlTour.setVisibility(View.GONE);
//        HomeFragment homeFragmemt = (HomeFragment) getFragment(HomeFragment.class.getValue());
//        homeFragmemt.showToolbar();
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

