package com.appster.turtle.ui.rooms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityRoomCoordinatorNewBinding;
import com.appster.turtle.databinding.LayoutLeaveRoomBinding;
import com.appster.turtle.databinding.LayoutRoomOptionsBinding;
import com.appster.turtle.databinding.PostMenuBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetRoomResponse;
import com.appster.turtle.network.response.JoinRoomResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.HashTagPostDetailActivity;
import com.appster.turtle.ui.OthersClickedActivity;
import com.appster.turtle.ui.ReactionByActivity;
import com.appster.turtle.ui.ReportActivity;
import com.appster.turtle.ui.activity.MeetUpDetailActivity;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.media.AddCaptionActivity;
import com.appster.turtle.ui.post.CreatePostActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.CustomTypefaceSpan;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.bindingadapters.CameraBindingAdapters;
import com.appster.turtle.viewmodel.RoomViewModel;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.net.MalformedURLException;
import java.net.URL;

/*
 * Activity for room listing
 */
public class RoomActivityCoordinator extends BaseActivity implements View.OnClickListener, RoomFragment.OnFragmentInteractionListener, PostListener {

    ActivityRoomCoordinatorNewBinding mBinding;
    private Bundle mBundle;
    private Room room;
    private int position = -1;

    private BottomSheetLayout mBottomSheetMembers;
    private LayoutLeaveRoomBinding bottomSheetBinding;
    private PostMenuBinding postMenuBottomBinding;
    private boolean isUpdated;
    private LayoutRoomOptionsBinding editBottomSheetBinding;

    private RoomViewModel mRoomViewModel;
    private RoomFragment hotFragment;
    private RoomFragment topFragment;
    private RoomFragment newFragment;
    private int selectedScreen = Constants.HOME_CURRENT_SCREEN.HOT;

    private int userStatus, pos;
    private boolean isFromUser, fromNotification, isLeave, isFromRoom;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_room_coordinator_new);


        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            position = mBundle.getInt(Constants.BundleKey.ROOM_ID, -1);
            isFromUser = mBundle.getBoolean(Constants.BundleKey.ROOM_FROM_USER, false);
            fromNotification = mBundle.getBoolean(Constants.BundleKey.FROM_NOTIFICATION, false);
            isFromRoom = mBundle.getBoolean(Constants.IS_FROM_ROOM, false);
            if (fromNotification) {
                pos = mBundle.getInt(Constants.POS, 0);
            } else if (isFromRoom) {
                pos = mBundle.getInt(Constants.POS, 0);
            }

        }

        mBinding.toolbarLayout.setTitleEnabled(false);
        mBinding.toolbar.setNavigationIcon(R.drawable.back_arrow);


        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);

        getRooms();


        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                refreshData();

            }
        };


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);

    }

    private void initUI() {

        mBinding.setRoom(room);
        setMemberImages();

        postMenuBottomBinding = DataBindingUtil.inflate(LayoutInflater.from(RoomActivityCoordinator.this), R.layout.post_menu, mBinding.bottomSheetMembers, false);
        postMenuBottomBinding.tvCancel.setOnClickListener(view -> mBinding.bottomSheetMembers.dismissSheet());

        mBinding.rlMembers.setOnClickListener(this);
        mBinding.rlJoin.tvJoinRoom.setOnClickListener(this);
        mBinding.rlJoin.tvAccept.setOnClickListener(this);
        mBinding.rlJoin.tvReject.setOnClickListener(this);


        if (room.getCreatedBy().equals(PreferenceUtil.getUser().getUserName()) || room.isUserAdmin()) {
            mBinding.rlJoin.getRoot().setVisibility(View.GONE);
            mBinding.rlJoin.tvAccept.setVisibility(View.GONE);
            mBinding.rlJoin.tvReject.setVisibility(View.GONE);


        } else if (room.getUserReqStatus() == Constants.ROOM_REQUEST.PENDING) {

            mBinding.rlJoin.getRoot().setVisibility(View.VISIBLE);
            if (room.isRequestedByMe()) {
                mBinding.rlJoin.tvJoinRoom.setVisibility(View.VISIBLE);
                mBinding.rlJoin.tvJoinRoom.setText("Pending");
                mBinding.rlJoin.tvAccept.setVisibility(View.GONE);
                mBinding.rlJoin.tvReject.setVisibility(View.GONE);
            } else {
                mBinding.rlJoin.tvJoinRoom.setVisibility(View.GONE);
                mBinding.rlJoin.tvAccept.setVisibility(View.VISIBLE);
                mBinding.rlJoin.tvReject.setVisibility(View.VISIBLE);

            }


        } else if (room.getUserReqStatus() == Constants.ROOM_REQUEST.NO_REQUESTED || room.getUserReqStatus() == Constants.ROOM_REQUEST.DECLINED) {
            mBinding.rlJoin.getRoot().setVisibility(View.VISIBLE);

            mBinding.rlJoin.tvJoinRoom.setVisibility(View.VISIBLE);
            mBinding.rlJoin.tvJoinRoom.setText("Join Space");

            mBinding.rlJoin.tvAccept.setVisibility(View.GONE);
            mBinding.rlJoin.tvReject.setVisibility(View.GONE);

        } else if (room.getUserReqStatus() == Constants.ROOM_REQUEST.ACCEPTED) {
            mBinding.rlJoin.getRoot().setVisibility(View.GONE);


            mBinding.rlJoin.tvJoinRoom.setVisibility(View.GONE);
            mBinding.rlJoin.tvAccept.setVisibility(View.GONE);
            mBinding.rlJoin.tvReject.setVisibility(View.GONE);

        }


        if (room.getUserReqStatus() == Constants.ROOM_REQUEST.ACCEPTED || room.isUserAdmin()) {

            mBinding.ivPost.setEnabled(true);
            mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mBinding.rlMenu.ivMenu.setAlpha(1f);
            mBinding.ivPost.setAlpha(1f);


        } else {
            mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mBinding.rlMenu.ivMenu.setAlpha(0.4f);
            mBinding.ivPost.setEnabled(false);
            mBinding.ivPost.setAlpha(0f);

        }

        if (room.isMemberCanPost() || room.isUserAdmin())
            mBinding.ivPost.setVisibility(View.VISIBLE);
        else
            mBinding.ivPost.setVisibility(View.INVISIBLE);


        mBinding.layRoomMenu.switchNotification.setChecked(room.isGetNotification());

        mBinding.layRoomMenu.switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> setNotification(isChecked));

        switchScreen();

        try {
            GlideApp.with(mBinding.ivRoomImage.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + room.getRoomUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.color.app_white)
                    .error(R.color.app_white)
                    .into(mBinding.ivRoomImage);
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }


        mBinding.tvRoomDescription.setText(room.getRoomDesc());
        ViewTreeObserver vto = mBinding.tvRoomDescription.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    ViewTreeObserver obs = mBinding.tvRoomDescription.getViewTreeObserver();
                    obs.removeOnGlobalLayoutListener(this);

                    int lineCount = mBinding.tvRoomDescription.getLayout().getLineCount();

                    if (lineCount < 2)
                        return;


                    String expandText = " " + getString(R.string.read_more);
                    int lineEndIndex = mBinding.tvRoomDescription.getLayout().getLineEnd(1);
                    String text = room.getRoomDesc().substring(0, lineEndIndex - expandText.length()) + expandText;

                    mBinding.tvRoomDescription.setMovementMethod(LinkMovementMethod.getInstance());

                    SpannableString string = new SpannableString((Html.fromHtml(text)));
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {

                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.ROOM_NAME_KEY, room.getRoomName());
                            bundle.putString(Constants.ROOM_DESC_KEY, room.getRoomDesc());

                            ExplicitIntent.getsInstance().navigateTo(RoomActivityCoordinator.this, RoomDescActivity.class, bundle);

                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(ContextCompat.getColor(RoomActivityCoordinator.this, R.color.bright_teal));
                            ds.setUnderlineText(false);
                        }
                    }, lineEndIndex - expandText.length(), lineEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    mBinding.tvRoomDescription.setText(string);

                } catch (Exception e) {

                    LogUtils.LOGD(TAG1, e.getMessage());
                }

            }
        });


        mBinding.toolbar.setTitleTextColor(ContextCompat.getColor(RoomActivityCoordinator.this, R.color.black));

        mBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            LogUtils.LOGD("OFFSET", verticalOffset + "");
            LogUtils.LOGD("OFFSET TOTAL", Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() + "");
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                mBinding.toolbar.setBackgroundResource(R.color.app_white);
                mBinding.mainToolbarTitle.setText(room.getRoomName().toUpperCase());
            } else {
                //Expanded


                if (Math.abs(verticalOffset) > 160) {
                    mBinding.toolbar.setBackgroundResource(R.color.app_white);
                    mBinding.mainToolbarTitle.setText(room.getRoomName().toUpperCase());
                } else {
                    mBinding.toolbar.setBackgroundResource(R.color.transparent);
                    mBinding.mainToolbarTitle.setText("");
                }
            }

        });


        mBinding.swipeRefresh.setOnRefreshListener(() -> {
            mBinding.swipeRefresh.setRefreshing(true);

            refreshData();
        });
    }


    public void refreshData() {

        if (hotFragment != null)
            hotFragment.dataRefresh();

        if (newFragment != null)
            newFragment.dataRefresh();

        if (topFragment != null)
            topFragment.dataRefresh();
    }

    public boolean isSwipeRefreshing() {
        return mBinding.swipeRefresh.isRefreshing();

    }

    public void hideSwipeRefresh() {
        mBinding.swipeRefresh.setRefreshing(false);
    }

    private void setMemberImages() {
        //show max 5 images
        int numberOfAdmins = Math.min(room.getRandomUsers().size(), 5);

        Resources resources = getResources();
        String packageName = getPackageName();

        //array of image view ids

        int i;
        //show images for number of admins (<=5)
        for (i = 5; i > 0; i--) {

            CircularImageView cvImage = findViewById(resources.getIdentifier("cv_user_" + (6 - i), "id", packageName));
            ImageView ivInit = findViewById(resources.getIdentifier("iv_user_init_" + (6 - i), "id", packageName));

            if (i <= numberOfAdmins) {

                User user = room.getRandomUsers().get(i - 1);


                if (user.getProfileUrlType() == Constants.IMAGE_INITIALS_ID) {
                    cvImage.setImageResource(R.drawable.ic_pp_cred);

                    CameraBindingAdapters.bindImageUrl(ivInit, user.getProfileUrl());
                    ivInit.setVisibility(View.VISIBLE);
                } else {
                    ivInit.setVisibility(View.INVISIBLE);
                    CameraBindingAdapters.bindProImageUrl(cvImage, user.getProfileUrl());

                }
            }


        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDataBinding() {
        mBottomSheetMembers = mBinding.bottomSheetMembers;
        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(RoomActivityCoordinator.this), R.layout.layout_leave_room, mBinding.bottomSheetMembers, false);
        editBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(RoomActivityCoordinator.this), R.layout.layout_room_options, mBinding.bottomSheetMembers, false);

        bottomSheetBinding.tvLeave.setOnClickListener(this);
        bottomSheetBinding.tvCancel.setOnClickListener(this);
        mBinding.rlMenu.tvTop.setOnClickListener(this);
        mBinding.rlMenu.tvNew.setOnClickListener(this);
        mBinding.rlMenu.tvHot.setOnClickListener(this);
        mBinding.rlMenu.ivMenu.setOnClickListener(this);
        mBinding.rlMenu.ivSearch.setOnClickListener(this);


        bottomSheetBinding.tvLeave.setVisibility(View.VISIBLE);
        mRoomViewModel = new RoomViewModel(this, room);
        mBinding.setViewModel(mRoomViewModel);

        mBinding.ivPost.setOnClickListener(v -> initPost());
        mBinding.layRoomMenu.setUser(PreferenceUtil.getUser());
        mBinding.layRoomMenu.setRoom(room);
        mBinding.layRoomMenu.setViewModel(mRoomViewModel);

        if (room.isUserAdmin()) {
            mBinding.layRoomMenu.tvAdminName.setText(R.string.you_are_admin);
        } else {
            mBinding.layRoomMenu.tvAdminName.setText(getAdminNames());
        }

        mBinding.layRoomMenu.clRoomSearch.etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                toggleDrawerLayout();
                return true;
            }

            return false;
        });

        //set underline
        String adminsText = mBinding.layRoomMenu.tvAdminName.getText().toString().trim();
        String underlineText = room.getAllAdmins().size() - 2 + " more..";
        if (adminsText.contains(underlineText)) {
            SpannableString underlineSpan = new SpannableString(adminsText);
            Typeface italicsTypeface = Typeface.createFromAsset(getAssets(), getString(R.string.bruta_normal_glb_semi_bold_italic));
            int startIndex = adminsText.indexOf(underlineText);
            int endIndex = startIndex + underlineText.length();
            underlineSpan.setSpan(new UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            underlineSpan.setSpan(new CustomTypefaceSpan("", italicsTypeface), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            underlineSpan.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    //launch likedBy activity to show all admins
                    toggleDrawerLayout();
                    Bundle b = new Bundle();
                    b.putParcelableArrayList(Constants.ADMINS_LIST, room.getAllAdmins());
                    b.putString(Constants.HEADER_TITLE, "Admins");
                    ExplicitIntent.getsInstance().navigateTo(RoomActivityCoordinator.this, OthersClickedActivity.class, b);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(ContextCompat.getColor(RoomActivityCoordinator.this, android.R.color.white));
                }


            }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mBinding.layRoomMenu.tvAdminName.setText(underlineSpan);
            mBinding.layRoomMenu.tvAdminName.setMovementMethod(LinkMovementMethod.getInstance());
            mBinding.layRoomMenu.tvAdminName.setHighlightColor(Color.TRANSPARENT);
        }
    }

    public void initPost() {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKey.ROOM_ID, position);
        com.appster.turtle.network.response.Room room1 = new com.appster.turtle.network.response.Room();
        room1.setId(room.getRoomId());
        room1.setValue(room.getRoomName());
        bundle.putParcelable(Constants.BundleKey.ROOM, room1);
        bundle.putBoolean(Constants.IS_FROM_ROOM, true);
        isUpdated = true;
        ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, CreatePostActivity.class, Constants.REQUEST_CODE.REQUEST_CREATE_POST, bundle);
    }

    public BottomSheetLayout getmBottomSheetMembers() {
        return mBottomSheetMembers;
    }

    public LayoutRoomOptionsBinding getEditBottomSheetBinding() {
        return editBottomSheetBinding;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mBinding.layRoomMenu.clRoomSearch.etSearch.setText("");
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.POST_BROADCAST));

    }


    private String getAdminNames() {
        StringBuffer sb = new StringBuffer(getString(R.string.room_admin_prefix) + " ");

        if (room != null && room.getAllAdmins() != null) {

            switch (room.getAllAdmins().size()) {
                case 1:
                    sb.append(room.getAllAdmins().get(0).getName());
                    break;

                case 2:
                    sb.append(room.getAllAdmins().get(0).getName()).append(" and ").append(room.getAllAdmins().get(1).getName());
                    break;

                //more than 2
                default:
                    sb.append(room.getAllAdmins().get(0).getName())
                            .append(", ")
                            .append(room.getAllAdmins().get(1).getName())
                            .append(" and ")
                            .append(room.getAllAdmins().size() - 2)
                            .append(" more..");

                    break;
            }
            return sb.toString();
        }

        return "";
    }


    @Override
    public void menuClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_icon_end:
                toggleDrawerLayout();
                break;

            case R.id.iv_menu_close:
                toggleDrawerLayout();
                break;

            case R.id.tv_menu_members:
                toggleDrawerLayout();
                mBundle.putParcelable(Constants.BundleKey.ROOM, room);
                mBundle.putBoolean(Constants.BundleKey.IS_ONLY_MEMBERS, false);
                ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, MemberRequestActivity.class, Constants.REQUEST_CODE.REQUEST_ROOM_MEMBER, mBundle);
                break;

            case R.id.tv_menu_edit_room:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, EditRoomActivity.class, Constants.REQUEST_CODE.REQUEST_EDIT_ROOM, mBundle);
                break;

            case R.id.tv_menu_leave:
                toggleDrawerLayout();
                mBottomSheetMembers.showWithSheetView(bottomSheetBinding.getRoot());
                break;

            case R.id.tv_menu_meetup:
                toggleDrawerLayout();
                ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, MeetUpDetailActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, mBundle);
                break;

        }

    }

    private void toggleDrawerLayout() {
        hideKeyboard();
        if (mBinding.drawerLayout.isDrawerOpen(Gravity.END)) {
            mBinding.drawerLayout.closeDrawer(Gravity.END);
        } else {
            mBinding.drawerLayout.openDrawer(Gravity.END);
        }
    }

    private void leaveRoom() {
        RetrofitRestRepository repository = ((ApplicationController) getApplicationContext()).provideRepository();
        new BaseCallback<BaseResponse>(RoomActivityCoordinator.this, repository.getApiService()
                .leaveRoom(room.getRoomId())) {
            @Override
            public void onSuccess(BaseResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    Alert.createAlert(RoomActivityCoordinator.this, "", getString(R.string.space_left)).setOnDismissListener(dialog -> {

                        dialog.dismiss();
                        isLeave = true;
                        onBackPressed();


                    }).show();
                }
            }

            @Override
            public void onFail() {

            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE.REQUEST_EDIT_ROOM:
                if (resultCode == RESULT_OK) {
                    room = data.getParcelableExtra(Constants.BundleKey.ROOM);
                    isUpdated = true;
                    initDataBinding();
                    initUI();
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_CREATE_POST:
                if (resultCode == RESULT_OK) {
                    Posts posts = data.getParcelableExtra(Constants.BundleKey.POST);
                    updatePost(posts);
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_ROOM_MEMBER:
                if (resultCode == RESULT_OK) {

                }
                break;

            case Constants.REQUEST_CODE.FLAG_POST:
                if (resultCode == RESULT_OK) {

                    Posts posts = data.getParcelableExtra(Constants.BundleKey.POST);
                    updatePost(posts);
                    break;
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_POST_DETAIL:


                if (resultCode == RESULT_OK) {
                    updatePost(data.getExtras().getParcelable(Constants.POST_QUERY));

                    break;
                }
                break;
            case Constants.REQUEST_CODE.REQUEST_ADD_TAG:

                if (resultCode == RESULT_OK) {
                    updatePost(data.getExtras().getParcelable(Constants.POST_QUERY));

                    break;
                }
                break;

            case Constants.REQUEST_CODE.REQUEST_REFRESH:
                refreshData();
                break;

        }


    }


    private void joinRoom() {


        new BaseCallback<JoinRoomResponse>(RoomActivityCoordinator.this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().joinRoomRequest(room.getRoomId()), true) {

            @Override
            public void onSuccess(JoinRoomResponse response) {

                room.setUserReqStatus(response.getData().getUserReqStatus());
                room.setRequestedByMe(true);
                isUpdated = true;

                initUI();


            }

            @Override
            public void onFail() {
//
            }
        };


    }

    private void invite(boolean accept) {


        new BaseCallback<JoinRoomResponse>(RoomActivityCoordinator.this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().roomInvite(room.getRoomId(), accept), true) {

            @Override
            public void onSuccess(JoinRoomResponse response) {

                room.setUserReqStatus(response.getData().getUserReqStatus());
                room.setRequestedByMe(true);
                isUpdated = true;
                initUI();


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

            case R.id.tv_join_room:

                if (mBinding.rlJoin.tvJoinRoom.getText().toString().equalsIgnoreCase("pending"))
                    return;

                joinRoom();

                break;
            case R.id.tv_accept:
                invite(true);
                break;
            case R.id.tv_reject:
                invite(false);
                break;

            case R.id.rl_members:

                mBundle.putParcelable(Constants.BundleKey.ROOM, room);
                mBundle.putBoolean(Constants.BundleKey.IS_ONLY_MEMBERS, true);
                ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, MemberRequestActivity.class, Constants.REQUEST_CODE.REQUEST_ROOM_MEMBER, mBundle);

                break;


            case R.id.tv_leave:
                mBinding.bottomSheetMembers.dismissSheet();
                leaveRoom();
                break;

            case R.id.tv_cancel:
                mBinding.bottomSheetMembers.dismissSheet();
                break;
            case R.id.tv_hot:
                selectedScreen = Constants.HOME_CURRENT_SCREEN.HOT;
                switchScreen();
                break;
            case R.id.tv_new:
                selectedScreen = Constants.HOME_CURRENT_SCREEN.NEW;
                switchScreen();

                break;
            case R.id.tv_top:
                selectedScreen = Constants.HOME_CURRENT_SCREEN.TOP;
                switchScreen();

                break;
            case R.id.iv_menu:
                if (room.getUserReqStatus() == Constants.ROOM_REQUEST.ACCEPTED || room.isUserAdmin()) {
                    toggleDrawerLayout();
                }

                break;

            case R.id.iv_search:
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.ROOM_ID, room.getRoomId());
                bundle.putInt(Constants.FILTER, selectedScreen);
                ExplicitIntent.getsInstance().navigateTo(RoomActivityCoordinator.this, SearchRoomActivity.class, bundle);


                break;
            default:
                break;
        }
    }

    private void switchScreen() {

        mBinding.rlMenu.tvHot.setTextColor(ContextCompat.getColor(this, selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT ? R.color.bright_teal : R.color.black));
        mBinding.rlMenu.tvTop.setTextColor(ContextCompat.getColor(this, selectedScreen == Constants.HOME_CURRENT_SCREEN.TOP ? R.color.bright_teal : R.color.black));
        mBinding.rlMenu.tvNew.setTextColor(ContextCompat.getColor(this, selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW ? R.color.bright_teal : R.color.black));


        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();

        if (hotFragment != null)
            fragmentTransaction.hide(hotFragment);

        if (topFragment != null)
            fragmentTransaction.hide(topFragment);

        if (newFragment != null)
            fragmentTransaction.hide(newFragment);


        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {

            if (hotFragment == null) {
                hotFragment = RoomFragment.newInstance(Constants.HOME_CURRENT_SCREEN.HOT, room);
                fragmentTransaction.add(R.id.fl_container_new, hotFragment);

            }
            fragmentTransaction.show(hotFragment);

        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {

            if (newFragment == null) {
                newFragment = RoomFragment.newInstance(Constants.HOME_CURRENT_SCREEN.NEW, room);
                fragmentTransaction.add(R.id.fl_container_new, newFragment);

            }

            fragmentTransaction.show(newFragment);


        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.TOP) {

            if (topFragment == null) {
                topFragment = RoomFragment.newInstance(Constants.HOME_CURRENT_SCREEN.TOP, room);
                fragmentTransaction.add(R.id.fl_container_new, topFragment);

            }
            fragmentTransaction.show(topFragment);


        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {


        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawers();
        } else if (isFromUser) {
            if (fromNotification) {
                Intent b = new Intent();
                b.putExtra(Constants.BundleKey.ROOM_REFRESH, room);
                b.putExtra(Constants.POS, pos);
                setResult(RESULT_OK, b);
                finish();
            }
            super.onBackPressed();

        } else if (isUpdated && position != -1) {
            Bundle b = new Bundle();
            b.putParcelable(Constants.BundleKey.ROOM_REFRESH, room);
            b.putInt(Constants.BundleKey.ROOM_ID, position);
            Intent i = new Intent(RoomActivityCoordinator.this, HomeMainActivity.class);
            i.putExtras(b);
            startActivity(i);
            finish();
        } else if (isLeave) {
            Bundle b = new Bundle();
            b.putParcelable(Constants.BundleKey.ROOM_REFRESH, room);
            b.putBoolean(Constants.IS_FROM_ROOM, isFromRoom);
            b.putInt(Constants.IS_DELETED, pos);
            Intent i = new Intent(RoomActivityCoordinator.this, HomeMainActivity.class);
            i.putExtras(b);
            startActivity(i);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMenuClicked(final Posts post, final int position) {
        if (post == null)
            return;

        if (post.isPostedByMe()) {
            postMenuBottomBinding.tvFlag.setVisibility(View.GONE);
            postMenuBottomBinding.tvDelete.setVisibility(View.VISIBLE);
            postMenuBottomBinding.tvEdit.setVisibility(View.VISIBLE);
            if (post.getPostType() == Constants.VIEW_TYPE.POLL) {
                postMenuBottomBinding.tvEdit.setVisibility(View.GONE);
            } else {
                postMenuBottomBinding.tvEdit.setVisibility(View.VISIBLE);
            }
        } else {
            postMenuBottomBinding.tvFlag.setVisibility(View.VISIBLE);
            postMenuBottomBinding.tvDelete.setVisibility(View.GONE);
            postMenuBottomBinding.tvEdit.setVisibility(View.GONE);
        }

        if (post.isFlaggedByMe()) {

            postMenuBottomBinding.tvFlag.setEnabled(false);
            postMenuBottomBinding.tvFlag.setText(getString(R.string.flaged));
        } else {
            postMenuBottomBinding.tvFlag.setEnabled(true);
            postMenuBottomBinding.tvFlag.setText(getString(R.string.flag_post));
        }

        postMenuBottomBinding.tvFlag.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BundleKey.POST, post);
            bundle.putBoolean(Constants.IS_FROM_POST, true);
            bundle.putInt(Constants.POS, position);
            ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, ReportActivity.class, Constants.REQUEST_CODE.FLAG_POST, bundle);
            mBinding.bottomSheetMembers.dismissSheet();
        });

        postMenuBottomBinding.tvDelete.setOnClickListener(v -> {
            if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {
                hotFragment.deletePost(post, position);
            } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {
                newFragment.deletePost(post, position);
            } else {
                topFragment.deletePost(post, position);
            }
            mBinding.bottomSheetMembers.dismissSheet();
        });

        postMenuBottomBinding.tvEdit.setOnClickListener(v -> {
            if (post.getPostType() == Constants.VIEW_TYPE.MEDIA) {
                mBinding.bottomSheetMembers.dismissSheet();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.POST_QUERY, post);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, AddCaptionActivity.class, Constants.REQUEST_CODE.REQUEST_ADD_TAG, bundle);

            } else if (post.getPostType() == Constants.VIEW_TYPE.MEET_UP) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, CreatePostActivity.class, Constants.REQUEST_CODE.REQUEST_CREATE_POST, bundle);
                mBinding.bottomSheetMembers.dismissSheet();
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.POST, post);
                bundle.putBoolean(Constants.IS_EDIT, true);
                bundle.putBoolean(Constants.IS_FROM_ROOM, true);
                bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
                bundle.putParcelable(Constants.BundleKey.ROOM, post.getRooms());
                ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, CreatePostActivity.class, Constants.REQUEST_CODE.REQUEST_CREATE_POST, bundle);
                mBinding.bottomSheetMembers.dismissSheet();
            }

        });

        mBinding.bottomSheetMembers.showWithSheetView(postMenuBottomBinding.getRoot());
    }


    private void getRooms() {


        new BaseCallback<GetRoomResponse>((BaseActivity) this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService()
                .getRoom(position), true) {
            @Override
            public void onSuccess(GetRoomResponse response) {
                if (response.getData() != null) {
                    room = response.getData();
                    //TODO:Change
                    mBundle.putParcelable(Constants.BundleKey.ROOM, room);
                    initDataBinding();
                    initUI();
                }
            }

            @Override
            public void onFail() {
//
            }
        };


    }


    @Override
    public void onPollClick(int position, int postId, int answerId) {
        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {
            hotFragment.onPollClick(position, postId, answerId);
        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {
            newFragment.onPollClick(position, postId, answerId);
        } else {
            topFragment.onPollClick(position, postId, answerId);
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
        LogUtils.LOGD("status", likeStatus + "  ");
        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {
            hotFragment.onLiked(position, postId, likeStatus, alreadyStatus);
        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {
            newFragment.onLiked(position, postId, likeStatus, alreadyStatus);
        } else {
            topFragment.onLiked(position, postId, likeStatus, alreadyStatus);
        }
    }


    @Override
    public void onClick(Posts post) {
//
    }

    @Override
    public void onMeetupStatusChange(int position, int postId, int status, boolean isMeetupResponded) {
        if (selectedScreen == Constants.HOME_CURRENT_SCREEN.HOT) {
            hotFragment.onMeetupStatusChange(position, postId, status, isMeetupResponded);
        } else if (selectedScreen == Constants.HOME_CURRENT_SCREEN.NEW) {
            newFragment.onMeetupStatusChange(position, postId, status, isMeetupResponded);
        } else {
            topFragment.onMeetupStatusChange(position, postId, status, isMeetupResponded);
        }
    }

    @Override
    public void onHasHTagClick(Posts post, int pos, String string) {
        LogUtils.LOGD("Log", "Cliked " + string);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.POST, post);
        bundle.putBoolean(Constants.IS_FROM_ROOM, true);
        bundle.putString(Constants.KEY, string);
        bundle.putInt(Constants.POS, selectedScreen);
        ExplicitIntent.getsInstance().navigateForResult(RoomActivityCoordinator.this, HashTagPostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, bundle);
    }

    @Override
    public void onCommentClicked(Posts post) {
//
    }


    public void updatePost(Posts posts) {
        if (hotFragment != null) {
            hotFragment.updatePost(posts, true, false);
        }

        if (newFragment != null) {
            newFragment.updatePost(posts, true, false);

        }

        if (topFragment != null) {
            topFragment.updatePost(posts, true, false);

        }
    }

    private void setNotification(boolean isChecked) {
        showProgressBar();
        new BaseCallback<UserActionResponse>(this, ((ApplicationController) getApplicationContext()).provideRepository().getApiService().setRoomNotification(room.getRoomId(), isChecked)) {

            @Override
            public void onSuccess(UserActionResponse response) {
                mBinding.layRoomMenu.switchNotification.setChecked(response.getData().getStatus());
                hideProgressBar();

            }

            @Override
            public void onFail() {
                hideProgressBar();
            }

        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(Constants.BundleKey.ROOM_ID)) {
            position = mBundle.getInt(Constants.BundleKey.ROOM_ID, -1);
            isFromUser = mBundle.getBoolean(Constants.BundleKey.ROOM_FROM_USER, false);
            getRooms();
        }
    }
}

