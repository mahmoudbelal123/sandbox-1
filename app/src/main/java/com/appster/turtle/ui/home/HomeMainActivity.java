package com.appster.turtle.ui.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityHomeMainBinding;
import com.appster.turtle.databinding.PostMenuBinding;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.UserActionRequest;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.UnreadNotifCountResponse;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.HashTagPostDetailActivity;
import com.appster.turtle.ui.ReactionByActivity;
import com.appster.turtle.ui.ReportActivity;
import com.appster.turtle.ui.activity.ActivitiesNotificationActivity;
import com.appster.turtle.ui.friends.FriendListActivity;
import com.appster.turtle.ui.media.AddCaptionActivity;
import com.appster.turtle.ui.notes.NotesListingActivity;
import com.appster.turtle.ui.post.CreatePostActivity;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.ui.rooms.CreateRoomActivity;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.ui.search.SearchRoomsAndPeopleActivity;
import com.appster.turtle.ui.settings.SettingsActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;

import java.net.MalformedURLException;
import java.net.URL;

import rx.Observable;

/**
 * A activity class for home main
 */
public class HomeMainActivity extends BaseActivity implements HomeMainFragment.OnFragmentInteractionListener, HomeFeedFragment.OnFragmentInteractionListener, View.OnClickListener, PostListener, BaseActivity.OnNotificationGet {

    private boolean isHome = true;
    private HomeMainFragment homeFragment, exploreFragment;
    private ActivityHomeMainBinding homeBinding;
    private PostMenuBinding bottomSheetBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_main);
        initUI();
        initDataBinding();
    }

    private void initUI() {

        if (getIntent().hasExtra(Constants.BundleKey.SHOW_TOUR) && getIntent().getBooleanExtra(Constants.BundleKey.SHOW_TOUR, false))
            isHome = false;

        switchScreen();


        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(HomeMainActivity.this), R.layout.post_menu, homeBinding.bottomSheet, false);
        bottomSheetBinding.tvCancel.setOnClickListener(view -> homeBinding.bottomSheet.dismissSheet());

        setOnNotificationGet(this);
    }

    public void refreshHome() {
        if (homeFragment != null)
            homeFragment.refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initImages();
        homeBinding.layHomeMenu.setUser(PreferenceUtil.getUser());
        homeBinding.layHomeMenu.setUniv(PreferenceUtil.getUniv());
        updateNotificationCount();
        updateNotificationCountDisplayed(PreferenceUtil.getNotification());
    }

    @Override
    public String getActivityName() {
        return HomeMainActivity.class.getName();
    }

    public void mainClick(View view) {
        switch (view.getId()) {
            case R.id.tv_home:
                isHome = true;
                switchScreen();
                break;

            case R.id.tv_explore:
                isHome = false;

                switchScreen();

                break;

            default:
                break;
        }

    }

    private void switchScreen() {

        homeBinding.tvHome.setTextColor(ContextCompat.getColor(HomeMainActivity.this, isHome ? R.color.bright_teal : R.color.black));
        homeBinding.tvExplore.setTextColor(ContextCompat.getColor(HomeMainActivity.this, !isHome ? R.color.bright_teal : R.color.black));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();

        if (exploreFragment != null)
            fragmentTransaction.hide(exploreFragment);
        if (homeFragment != null)
            fragmentTransaction.hide(homeFragment);

        if (isHome) {

            if (homeFragment == null) {
                homeFragment = HomeMainFragment.newInstance(true);
                fragmentTransaction
                        .add(R.id.fl_container, homeFragment);

            }
            fragmentTransaction.show(homeFragment);
        } else {

            if (exploreFragment == null) {
                exploreFragment = HomeMainFragment.newInstance(false);
                fragmentTransaction
                        .add(R.id.fl_container, exploreFragment);

            }
            fragmentTransaction.show(exploreFragment);

        }
        fragmentTransaction.commitAllowingStateLoss();

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void initDataBinding() {
        homeBinding.layHomeMenu.setUser(PreferenceUtil.getUser());
        homeBinding.layHomeMenu.setUniv(PreferenceUtil.getUniv());


        if (getIntent().hasExtra(Constants.BundleKey.SHOW_TOUR) && getIntent().getBooleanExtra(Constants.BundleKey.SHOW_TOUR, false)) {

            homeBinding.tour.getRoot().setVisibility(View.VISIBLE);


        } else {
            homeBinding.tour.getRoot().setVisibility(View.INVISIBLE);


        }

    }

    private void initImages() {
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) (homeBinding.layHomeMenu.ivMenuUser).getLayoutParams();
        if (PreferenceUtil.getUser().getProfileUrlType() == Constants.AVATAR.NAME || PreferenceUtil.getUser().getProfileUrlType() == Constants.AVATAR.EMOJI) {


            homeBinding.layHomeMenu.ivMenuUser.setScaleType(ImageView.ScaleType.FIT_CENTER);
            homeBinding.layHomeMenu.ivCircle.setVisibility(View.VISIBLE);


            try {
                GlideApp.with(HomeMainActivity.this)
                        .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + PreferenceUtil.getUser().getProfileUrl()))
                        .placeholder(R.color.black)


                        .override(Utils.dpToPx(HomeMainActivity.this, 63), Utils.dpToPx(HomeMainActivity.this, 63))
                        .into(homeBinding.layHomeMenu.ivMenuUser);
            } catch (MalformedURLException e) {
                LogUtils.LOGD(TAG1, e.getMessage());
            }

            if (PreferenceUtil.getUser().getProfileUrlType() == Constants.AVATAR.NAME) {
                param.height = Utils.dpToPx(HomeMainActivity.this, 63);
                param.width = Utils.dpToPx(HomeMainActivity.this, 63);
            } else {
                param.height = Utils.dpToPx(HomeMainActivity.this, 93);
                param.width = Utils.dpToPx(HomeMainActivity.this, 93);
            }

            param.addRule(RelativeLayout.CENTER_IN_PARENT);
            homeBinding.layHomeMenu.ivMenuUser.setLayoutParams(param);


        } else {

            homeBinding.layHomeMenu.ivCircle.setVisibility(View.INVISIBLE);

            homeBinding.layHomeMenu.ivMenuUser.setScaleType(ImageView.ScaleType.CENTER_CROP);

            homeBinding.layHomeMenu.ivMenuUser.setBackground(null);

            try {
                GlideApp.with(HomeMainActivity.this)
                        .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + PreferenceUtil.getUser().getProfileUrl()))
                        .placeholder(R.color.black)
                        .centerCrop()
                        .into(homeBinding.layHomeMenu.ivMenuUser);
            } catch (MalformedURLException e) {
                LogUtils.LOGD(TAG1, e.getMessage());
            }

            param.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            param.width = RelativeLayout.LayoutParams.MATCH_PARENT;

            param.addRule(RelativeLayout.CENTER_IN_PARENT);
            homeBinding.layHomeMenu.ivMenuUser.setLayoutParams(param);


        }


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


            refreshHome();
            if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.BundleKey.NEW_ROOM))
                ExplicitIntent.getsInstance().navigateTo(this, RoomActivityCoordinator.class, intent.getExtras());

        } else if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.BundleKey.ROOM_REFRESH)) {
            if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.IS_FROM_ROOM)) {
                if (exploreFragment != null)
                    exploreFragment.refreshData();

                refreshHome();
                //homeFragment.refreshRoomData(intent.getExtras().getInt(Constants.IS_DELETED));
            } else {
                if (exploreFragment != null)
                    exploreFragment.refreshData();
                refreshHome();
            }
        } else if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.IS_EDIT)) {
            if (exploreFragment != null)
                exploreFragment.refreshData();

            refreshHome();
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
        switch (view.getId()) {
            case R.id.iv_trending:

                break;
        }
    }


    public void toggleDrawerLayout() {
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

                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(HomeMainActivity.this, UserProfileActivity.class);


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


            case R.id.tv_menu_my_notes:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateTo(this, NotesListingActivity.class);

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
                parms.removeRule(RelativeLayout.BELOW);
                break;

            case 5:

                dismissTour();
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
                homeBinding.tour.searchTour.setVisibility(View.VISIBLE);
                homeBinding.tour.ivFab.setVisibility(View.VISIBLE);
                homeBinding.tour.rlHomeExplore.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }

        homeBinding.tour.tvFinish.setLayoutParams(parms);
    }

    private void dismissTour() {
        homeBinding.tour.llStartTour.setVisibility(View.INVISIBLE);
        homeBinding.tour.rlTour.setVisibility(View.GONE);

    }

    public void updateNotificationCountDisplayed(int newCount) {
        if (newCount > 0) {
            homeBinding.layHomeMenu.tvNotificationCount.setVisibility(View.VISIBLE);
            homeBinding.layHomeMenu.tvNotificationCount.setText(String.valueOf(newCount));
        } else {
            homeBinding.layHomeMenu.tvNotificationCount.setVisibility(View.GONE);
        }
    }


    @Override
    public void onMenuClicked(final Posts post, final int pos) {

        if (post == null)
            return;

        if (post.isPostedByMe()) {
            bottomSheetBinding.tvFlag.setVisibility(View.GONE);
            bottomSheetBinding.tvDelete.setVisibility(View.VISIBLE);
            bottomSheetBinding.tvEdit.setVisibility(View.VISIBLE);
            if (post.getPostType() == Constants.VIEW_TYPE.POLL) {
                bottomSheetBinding.tvEdit.setVisibility(View.GONE);
            } else {
                bottomSheetBinding.tvEdit.setVisibility(View.VISIBLE);
            }
        } else {
            bottomSheetBinding.tvFlag.setVisibility(View.VISIBLE);
            bottomSheetBinding.tvDelete.setVisibility(View.GONE);
            bottomSheetBinding.tvEdit.setVisibility(View.GONE);
        }

        bottomSheetBinding.tvDelete.setOnClickListener(v -> {
            if (isHome) {
                homeFragment.deletePost(post, pos);
            } else {
                exploreFragment.deletePost(post, pos);
            }
            homeBinding.bottomSheet.dismissSheet();
        });


        bottomSheetBinding.tvEdit.setOnClickListener(v -> {

            if (post.getPostType() == Constants.VIEW_TYPE.MEDIA) {

                homeBinding.bottomSheet.dismissSheet();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.POST_QUERY, post);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                ExplicitIntent.getsInstance().navigateForResult(HomeMainActivity.this, AddCaptionActivity.class, Constants.REQUEST_CODE.REQUEST_ADD_TAG, bundle);
//
            } else if (post.getPostType() == Constants.VIEW_TYPE.MEET_UP) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, false);
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                ExplicitIntent.getsInstance().navigateTo(HomeMainActivity.this, CreatePostActivity.class, bundle);
                homeBinding.bottomSheet.dismissSheet();
            } else {
                Bundle bundle = new Bundle();

                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, false);
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                ExplicitIntent.getsInstance().navigateTo(HomeMainActivity.this, CreatePostActivity.class, bundle);
                homeBinding.bottomSheet.dismissSheet();
            }
        });
        if (post.isFlaggedByMe()) {
            bottomSheetBinding.tvFlag.setEnabled(false);
            bottomSheetBinding.tvFlag.setText(getString(R.string.flaged));
        } else {
            bottomSheetBinding.tvFlag.setEnabled(true);
            bottomSheetBinding.tvFlag.setText(getString(R.string.flag_post));
        }
        bottomSheetBinding.tvFlag.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BundleKey.POST, post);
            bundle.putBoolean(Constants.IS_FROM_POST, true);
            bundle.putInt(Constants.POS, pos);
            ExplicitIntent.getsInstance().navigateForResult(HomeMainActivity.this, ReportActivity.class, Constants.REQUEST_CODE.FLAG_POST, bundle);
            homeBinding.bottomSheet.dismissSheet();
        });

        homeBinding.bottomSheet.showWithSheetView(bottomSheetBinding.getRoot());
    }

    @Override
    public void onPollClick(int position, int postId, int answerId) {
        if (isHome) {
            homeFragment.onPollClick(position, postId, answerId);
        } else {
            exploreFragment.onPollClick(position, postId, answerId);
        }
    }

    @Override
    public void onLikedBy(int position, int postId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKey.POST, postId);
        ExplicitIntent.getsInstance().navigateTo(this, ReactionByActivity.class, bundle);
    }

    @Override
    public void onLiked(int position, Posts postId, int likeStatus, int alreadyStatus) {
        if (isHome) {
            homeFragment.onLiked(position, postId, likeStatus, alreadyStatus);
        } else {
            exploreFragment.onLiked(position, postId, likeStatus, alreadyStatus);
        }
    }

    @Override
    public void onClick(Posts post) {
//
    }

    @Override
    public void onMeetupStatusChange(int position, int postId, int status, boolean isMeetupResponded) {
        if (isHome) {
            homeFragment.onMeetupStatusChange(position, postId, status, isMeetupResponded);
        } else {
            exploreFragment.onMeetupStatusChange(position, postId, status, isMeetupResponded);
        }
    }


    @Override
    public void onHasHTagClick(Posts post, int pos, String string) {
        LogUtils.LOGD("Log", "Cliked " + string);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.POST, post);
        bundle.putBoolean(Constants.IS_FROM_ROOM, false);
        bundle.putString(Constants.KEY, string);
        if (isHome) {
            bundle.putInt(Constants.POS, homeFragment.getSelectedScreen());
        } else {
            bundle.putInt(Constants.POS, exploreFragment.getSelectedScreen());
        }

        ExplicitIntent.getsInstance().navigateForResult(HomeMainActivity.this, HashTagPostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, bundle);
    }

    @Override
    public void onCommentClicked(Posts post) {
//
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case Constants.REQUEST_CODE.REQUEST_POST_DETAIL:

                    updatePost(data.getExtras().getParcelable(Constants.POST_QUERY));

                    break;

                case Constants.REQUEST_CODE.FLAG_POST:


                    updatePost(data.getParcelableExtra(Constants.BundleKey.POST));
                    break;

                case Constants.REQUEST_CODE.REQUEST_ADD_TAG:

                    updatePost(data.getParcelableExtra(Constants.POST_QUERY));
                    break;

                case Constants.REQUEST_CODE.REQUEST_REFRESH:
                    if (isHome) {
                        homeFragment.refreshData();
                    } else {
                        exploreFragment.refreshData();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void updatePost(Posts posts) {
        if (homeFragment != null) {
            homeFragment.updatePost(posts);
        }

        if (exploreFragment != null) {
            exploreFragment.updatePost(posts);

        }
    }

    private void setFlag(final Posts item) {


        UserActionRequest request = new UserActionRequest();
        request.setRoomId(item.getPostId());
        Observable<UserActionResponse> userActionResponseObservable = ((ApplicationController) getApplicationContext()).provideRepository().getApiService().flagPost(request);
        showProgressBar();

        new BaseCallback<UserActionResponse>(this, userActionResponseObservable) {

            @Override
            public void onSuccess(UserActionResponse response) {

                hideProgressBar();

                showSnackBar(getString(R.string.post_flagged));
                item.setFlaggedByMe(true);
                homeBinding.bottomSheet.dismissSheet();
            }

            @Override
            public void onFail() {
                hideProgressBar();
                homeBinding.bottomSheet.dismissSheet();
            }
        };
    }

    public void updateNotificationCount() {

        RetrofitRestRepository repository = ((ApplicationController) getApplicationContext()).provideRepository();
        new BaseCallback<UnreadNotifCountResponse>(this, repository.getApiService().markUnreadCount(), false) {

            @Override
            public void onSuccess(UnreadNotifCountResponse response) {
                PreferenceUtil.setNotificationCount(response.getData().getCount());
                updateNotificationCountDisplayed(response.getData().getCount());
            }

            @Override
            public void onFail() {
//
            }
        };
    }

    @Override
    public void onNotificationGet() {
        updateNotificationCountDisplayed(PreferenceUtil.getNotification());
    }

    @Override
    public void onUnreadNotification() {
//
    }
}
