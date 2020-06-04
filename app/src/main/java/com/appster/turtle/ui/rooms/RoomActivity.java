/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.rooms;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.RoomTagsAdapter;
import com.appster.turtle.databinding.ActivityRoomRelativeBinding;
import com.appster.turtle.databinding.LayoutLeaveRoomBinding;
import com.appster.turtle.databinding.LayoutRoomOptionsBinding;
import com.appster.turtle.model.Event;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.CreateTextPostRequest;
import com.appster.turtle.network.response.CreateTextPostResponse;
import com.appster.turtle.network.response.GetTagsResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.OthersClickedActivity;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.media.CameraActivity;
import com.appster.turtle.ui.media.MediaBottomFragment;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.CustomTypefaceSpan;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.SpaceItemDecoration;
import com.appster.turtle.viewmodel.RoomViewModel;
import com.flipboard.bottomsheet.BottomSheetLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by atul on 31/08/17.
 * To inject activity reference.
 *
 * activity for room
 */

public class RoomActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener, BaseActivity.PermissionI {

    private Room room;
    private Bundle mBundle;
    private RoomViewModel mRoomViewModel;
    private ActivityRoomRelativeBinding mRoomBinding;

    public BottomSheetLayout getmBottomSheetMembers() {
        return mBottomSheetMembers;
    }

    public LayoutRoomOptionsBinding getEditBottomSheetBinding() {
        return editBottomSheetBinding;
    }

    private BottomSheetLayout mBottomSheetMembers;
    private LayoutLeaveRoomBinding bottomSheetBinding;
    private LayoutRoomOptionsBinding editBottomSheetBinding;

    //for text post
    private int mCharRemaining;
    private String mQuery;
    private RetrofitRestRepository mRepository;
    private List<Tag> mSelectedTags;
    private List<Tag> mTags;
    private RoomTagsAdapter mRoomTagsAdapter;
    private int mTotalPagesAvailable;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int mCurrentPage;
    private boolean isLoading;
    private LinearLayoutManager mLinearLayoutManager;
    private int position = -1;
    private boolean isUpdated = false;
    private boolean isPermissionGiven;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            room = mBundle.getParcelable(Constants.BundleKey.ROOM);
            position = mBundle.getInt(Constants.BundleKey.ROOM_ID, -1);

            if (room.isUserBlocked()) {
                Alert.createAlert(RoomActivity.this, "", getString(R.string.user_blocked_message))
                        .show();
                room.setBlockedAlertShownBefore(true);
            }
        }

        initDataBinding();
        initUI();
    }

    private void initDataBinding() {

        mRoomBinding = DataBindingUtil.setContentView(this, R.layout.activity_room_relative);
        mBottomSheetMembers = mRoomBinding.bottomSheetMembers;
        bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(RoomActivity.this), R.layout.layout_leave_room, mRoomBinding.bottomSheetMembers, false);
        editBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(RoomActivity.this), R.layout.layout_room_options, mRoomBinding.bottomSheetMembers, false);

        bottomSheetBinding.tvLeave.setOnClickListener(this);
        bottomSheetBinding.tvCancel.setOnClickListener(this);

        bottomSheetBinding.tvLeave.setVisibility(View.VISIBLE);
        mRoomViewModel = new RoomViewModel(this, room);
        mRoomBinding.setViewModel(mRoomViewModel);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mRoomBinding.layRoomMenu.setUser(PreferenceUtil.getUser());
        mRoomBinding.layRoomMenu.setRoom(room);
        mRoomBinding.layRoomMenu.setViewModel(mRoomViewModel);


        if (room.isUserAdmin()) {
            mRoomBinding.layRoomMenu.tvAdminName.setText(R.string.you_are_admin);
        } else {
            mRoomBinding.layRoomMenu.tvAdminName.setText(getAdminNames());
        }


        mRoomBinding.layRoomMenu.clRoomSearch.etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                toggleDrawerLayout();
                searchNotes();
                return true;
            }

            return false;
        });

        //set underline
        String adminsText = mRoomBinding.layRoomMenu.tvAdminName.getText().toString().trim();
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
                    Bundle b = new Bundle();
                    b.putParcelableArrayList(Constants.ADMINS_LIST, room.getAllAdmins());
                    b.putString(Constants.HEADER_TITLE, "Admins");
                    ExplicitIntent.getsInstance().navigateTo(RoomActivity.this, OthersClickedActivity.class, b);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(ContextCompat.getColor(RoomActivity.this, android.R.color.black));
                }


            }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mRoomBinding.layRoomMenu.tvAdminName.setText(underlineSpan);
            mRoomBinding.layRoomMenu.tvAdminName.setMovementMethod(LinkMovementMethod.getInstance());
            mRoomBinding.layRoomMenu.tvAdminName.setHighlightColor(Color.TRANSPARENT);
        }
    }

    private void searchNotes() {
        mRoomViewModel.offset = 1;
        mRoomViewModel.getPosts(true);
    }

    private String getAdminNames() {

        StringBuilder sb = new StringBuilder(getString(R.string.room_admin_prefix) + " ");

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

    private void initUI() {
        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        mSelectedTags = new ArrayList<>();

        try {
            if (room != null) {
                setUpHeader(mRoomBinding.header.clHeader, room.getRoomName(), R.drawable.menu, true);
                String owner = room.getCreateByWithAtSign();

                String fullText = getString(R.string.cre_by) + owner + getString(R.string.on) + room.getCreatedAt();
                setRoomInfo(mRoomBinding.tvRoomInfo, fullText, owner, ContextCompat.getColor(this, R.color.colorTxtNormal));
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }

        mRoomBinding.ivTextPostBack.setOnClickListener(this);
        mRoomBinding.ivMedia.setOnClickListener(this);
        mRoomBinding.ivMeetUp.setOnClickListener(this);
        mRoomBinding.ivPoll.setOnClickListener(this);
        mRoomBinding.etTextPost.setOnTouchListener(this);

        mRoomBinding.etTextPost.addTextChangedListener(createPostTextWatcher);
        mRoomBinding.ivPost.setOnClickListener(this);
        mRoomBinding.ivPost.setEnabled(false);


        initHashtagsRecyclerView();

    }

    private void initHashtagsRecyclerView() {
        //for tags
        mRoomBinding.rvHashtagSuggestions.addItemDecoration(new SpaceItemDecoration(12));
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRoomBinding.rvHashtagSuggestions.setLayoutManager(mLinearLayoutManager);

        //load certain number of suggestions at a time, not all at once
        mRoomBinding.rvHashtagSuggestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = mLinearLayoutManager.getChildCount();
                mTotalItemCount = mLinearLayoutManager.getItemCount();
                mPastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                    if (mTags != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getTagSuggestions();
                    }
                }
            }
        });
    }

    public void toggleMediaBottom() {


        if (isPermissionGiven) {

            if (getFragment(MediaBottomFragment.class.getName()) == null)
                pushFragment(Constants.FRAGMENTS.MEDIA_BOTTOM_FRAGMENT, null, mRoomBinding.flMediaBottom.getId(), false, ANIMATION_TYPE.NONE);

            mRoomBinding.flMediaBottom.setVisibility(mRoomBinding.flMediaBottom.getVisibility() == View.VISIBLE ? GONE : View.VISIBLE);
        } else
            checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);


    }

    private void setRoomInfo(TextView view, String fulltext, String subtext, int color) {
        try {
            view.setText(fulltext, TextView.BufferType.SPANNABLE);
            Spannable str = (Spannable) view.getText();
            int i = fulltext.indexOf(subtext);
            str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRoomBinding.layRoomMenu.clRoomSearch.etSearch.setText("");
        mRoomViewModel.offset = 1;
        mRoomViewModel.getPosts(true);
    }

    @Subscribe
    public void onEvent(Event event) {

        switch (event.getEventId()) {
            case Constants.eventI.ON_CAMERA_CLICK:
                toggleMediaBottom();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.ROOM, room);
                ExplicitIntent.getsInstance().navigateTo(RoomActivity.this, CameraActivity.class, bundle);

                break;

            case Constants.eventI.ON_IMAGE_CLICK:
                toggleMediaBottom();

                bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.ROOM, room);
                if (event.getEventMsg() != null)
                    bundle.putString(Constants.BundleKey.IMAGE_URL, event.getEventMsg());
                ExplicitIntent.getsInstance().navigateTo(RoomActivity.this, CameraActivity.class, bundle);

                break;
            default:
                break;
        }
    }

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
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
                ExplicitIntent.getsInstance().navigateTo(RoomActivity.this, MemberRequestActivity.class, mBundle);

                break;

            case R.id.tv_menu_edit_room:
                toggleDrawerLayout();

                ExplicitIntent.getsInstance().navigateForResult(RoomActivity.this, EditRoomActivity.class, Constants.REQUEST_CODE.REQUEST_EDIT_ROOM, mBundle);

                break;

            case R.id.tv_menu_leave:

                toggleDrawerLayout();
                mBottomSheetMembers.showWithSheetView(bottomSheetBinding.getRoot());


                break;

            case R.id.tv_menu_meetup:

                ExplicitIntent.getsInstance().navigateTo(RoomActivity.this, MeetupsActivity.class, mBundle);
                break;
            default:
                break;
        }

    }

    private void toggleDrawerLayout() {
        hideKeyboard();
        if (mRoomBinding.drawerLayout.isDrawerOpen(Gravity.END)) {
            mRoomBinding.drawerLayout.closeDrawer(Gravity.END);
        } else {
            mRoomBinding.drawerLayout.openDrawer(Gravity.END);
        }

    }

    private void leaveRoom() {
        RetrofitRestRepository repository = ((ApplicationController) getApplicationContext()).provideRepository();
        new BaseCallback<BaseResponse>(RoomActivity.this, repository.getApiService()
                .leaveRoom(room.getRoomId())) {
            @Override
            public void onSuccess(BaseResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    Alert.createAlert(RoomActivity.this, "", "Room left").setOnDismissListener(dialog -> {

                        dialog.dismiss();
                        finish();

                    }).show();

                }

            }

            @Override
            public void onFail() {
//
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (mRoomBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mRoomBinding.drawerLayout.closeDrawers();
        } else if (mRoomBinding.flMediaBottom.getVisibility() == View.VISIBLE)
            toggleMediaBottom();
        else if (isUpdated && position != -1) {
            Bundle b = new Bundle();
            b.putParcelable(Constants.BundleKey.ROOM_REFRESH, room);
            b.putInt(Constants.BundleKey.ROOM_ID, position);
            Intent i = new Intent(RoomActivity.this, HomeMainActivity.class);
            i.putExtras(b);
            startActivity(i);
            finish();

        } else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_leave:
                mRoomBinding.bottomSheetMembers.dismissSheet();
                leaveRoom();
                break;

            case R.id.tv_cancel:
                mRoomBinding.bottomSheetMembers.dismissSheet();

                break;

            case R.id.iv_text_post_back:
                resetLayout();
                break;

            case R.id.iv_post:
                postClicked();
                break;

            case R.id.ivMedia:
                mRoomBinding.etTextPost.clearFocus();
                resetLayout();
                break;

            case R.id.ivMeetUp:
                mRoomBinding.etTextPost.clearFocus();
                resetLayout();
                break;

            case R.id.ivPoll:
                mRoomBinding.etTextPost.clearFocus();
                resetLayout();
                break;
            default:
                break;
        }
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
        }
    }

    private void resetLayout() {

        if (!mRoomBinding.etTextPost.hasFocus()) {
            mRoomBinding.tvRemainingCharacters.setVisibility(GONE);
            mRoomBinding.etTextPost.setCursorVisible(false);
        }

        mRoomBinding.etTextPost.setEllipsize(TextUtils.TruncateAt.END);
        mRoomBinding.ivTextPostBack.setVisibility(GONE);
        mRoomBinding.ivPoll.setVisibility(View.VISIBLE);
        mRoomBinding.ivMedia.setVisibility(View.VISIBLE);
        mRoomBinding.ivMeetUp.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        view.performClick();

        switch (view.getId()) {
            case R.id.et_text_post:


                mRoomBinding.tvRemainingCharacters.setVisibility(View.VISIBLE);

                if (mRoomBinding.ivTextPostBack.isShown()) {
                    break;
                } else {
                    mRoomBinding.flMediaBottom.setVisibility(GONE);
                    mRoomBinding.ivPoll.setVisibility(GONE);
                    mRoomBinding.ivMedia.setVisibility(GONE);
                    mRoomBinding.ivMeetUp.setVisibility(GONE);
                    mRoomBinding.ivTextPostBack.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
        return false;
    }

    private TextWatcher createPostTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!mRoomBinding.etTextPost.isCursorVisible()) {
                mRoomBinding.etTextPost.setCursorVisible(true);
            }

            //set gravity of media, meetup, poll icons for more than 1 line because when there's one line, gravity bottom doesn't look aligned.
            //Hence, set gravity center_vertical for one line and gravity bottom for more than one line.
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRoomBinding.ivPoll.getLayoutParams();

            if (mRoomBinding.etTextPost.getLineCount() > 1) {
                params.gravity = Gravity.BOTTOM;
            } else {
                params.gravity = Gravity.CENTER_VERTICAL;
            }

            mRoomBinding.ivPoll.setLayoutParams(params);
            mRoomBinding.ivMedia.setLayoutParams(params);
            mRoomBinding.ivMeetUp.setLayoutParams(params);
            mRoomBinding.ivTextPostBack.setLayoutParams(params);

            //calculate remaining characters
            mCharRemaining = Constants.MAX_CHARACTERS_TEXT_POST - charSequence.length();
            updateCharactersRemaining();

            checkForHashTags(charSequence);

            validateData();
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    private void updateCharactersRemaining() {

        //if no character is entered, reset remaining characters string
        if (mCharRemaining == Constants.MAX_CHARACTERS_TEXT_POST) {
            mRoomBinding.tvRemainingCharacters.setText(String.format(getString(R.string.characters_max), Constants.MAX_CHARACTERS_TEXT_POST));
        } else if (mCharRemaining == 1) {
            // 'characters' replaced by 'character'
            mRoomBinding.tvRemainingCharacters.setText(String.format(getString(R.string.character_remaining_half_string), mCharRemaining));
        } else {
            mRoomBinding.tvRemainingCharacters.setText(String.format(getString(R.string.characters_remaining_half_string), mCharRemaining));
        }

    }

    private void checkForHashTags(CharSequence s) {
        //for hashtags
        int selection = mRoomBinding.etTextPost.getText().length();

        String str = s.toString().substring(0, selection);
        if (str.lastIndexOf("#") > str.lastIndexOf(" ")) {
            mQuery = str.substring(str.lastIndexOf("#") + 1, str.length());

            mCurrentPage = 1;
            if (mQuery.length() > 0)
                getTagSuggestions();
            else {
                clearTagList();
            }
        } else {
            clearTagList();

        }

    }

    private void validateData() {
        if (mRoomBinding.etTextPost.getText().toString().trim().length() >= 1
                && mRoomBinding.etTextPost.getText().toString().trim().length() <= Constants.MAX_CHARACTERS_TEXT_POST) {
            //enable post button
            mRoomBinding.ivPost.setEnabled(true);
        } else {
            //disable post button
            mRoomBinding.ivPost.setEnabled(false);
        }

    }

    private void getTagSuggestions() {


        //show hashtags recyclerView
        mRoomBinding.rvHashtagSuggestions.setVisibility(View.VISIBLE);

        new BaseCallback<GetTagsResponse>(RoomActivity.this, mRepository.getApiService()
                .getTags(mCurrentPage, Constants.LIST_LENGTH, mQuery)) {
            @Override
            public void onSuccess(GetTagsResponse response) {

                if (mCurrentPage == 1) {
                    mTags = response.getData();
                    mTotalPagesAvailable = response.getPagination().getTotalPages();
                    isLoading = false;
                    mRoomTagsAdapter = new RoomTagsAdapter(RoomActivity.this, response.getData());
                    mRoomBinding.rvHashtagSuggestions.setAdapter(mRoomTagsAdapter);

                } else {
                    mTotalPagesAvailable = response.getPagination().getTotalPages();
                    mTags.addAll(response.getData());
                    isLoading = false;
                    mRoomTagsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFail() {
//
            }
        };

    }

    private void clearTagList() {

        //hide hashtags recyclerView
        mRoomBinding.rvHashtagSuggestions.setVisibility(GONE);

        if (mTags != null) {
            mTags.clear();
            if (mRoomTagsAdapter != null)
                mRoomTagsAdapter.notifyDataSetChanged();
        }
    }

    public void appendTag(Tag tag) {
        String originalString = mRoomBinding.etTextPost.getText().toString().trim();

        originalString = originalString.substring(0, originalString.lastIndexOf("#") + 1);

        mRoomBinding.etTextPost.setText(originalString + tag.getValue() + " ");
        mRoomBinding.etTextPost.setSelection(mRoomBinding.etTextPost.getText().length());

        if (!mSelectedTags.contains(tag))
            mSelectedTags.add(tag);

        clearTagList();
    }

    private void postClicked() {

        showProgressBar();

        List<Integer> roomIds = new ArrayList<>();
        roomIds.add(room.getRoomId());

        CreateTextPostRequest textPostRequest = new CreateTextPostRequest();
        textPostRequest.setText(mRoomBinding.etTextPost.getText().toString().trim());
        textPostRequest.setTags(getHashTags());
        textPostRequest.setPostId(0); //0 for new post, 1 for edit post
        textPostRequest.setRoomIds(roomIds);
        textPostRequest.setVisibility(0);

        new BaseCallback<CreateTextPostResponse>(RoomActivity.this, mRepository.getApiService().createTextPost(textPostRequest)) {

            @Override
            public void onSuccess(CreateTextPostResponse response) {
                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {

                    Alert.createAlert(RoomActivity.this, "", getString(R.string.post_created_message)).setOnDismissListener(dialog -> {

                        hideProgressBar();
                        //to display post
                        if (!mRoomViewModel.getQuery().isEmpty()) {
                            mRoomBinding.layRoomMenu.clRoomSearch.etSearch.setText("");
                            mRoomViewModel.offset = 1;
                        }
                        mRoomViewModel.getPosts(false);
                        mRoomBinding.tvRemainingCharacters.setVisibility(GONE);
                        mRoomBinding.etTextPost.setText("");
                    }).show();

                    resetLayout();
                    hideKeyboard();
                }
            }

            @Override
            public void onFail() {

            }
        };

    }

    private List<Tag> getHashTags() {
        String[] tags = mRoomBinding.etTextPost.getText().toString().split("(?=#)");

        if (mRoomBinding.etTextPost.getText().toString().isEmpty())
            return null;

        if (tags.length == 0)
            return null;

        ArrayList<String> tagStrs = new ArrayList<>();
        for (int i = 0; i < tags.length; i++) {
            if (tags[i].trim().startsWith("#")) {
                String trimmedTag = tags[i].trim().replaceFirst("#", "");
                if (trimmedTag.contains(" ")) {
                    trimmedTag = trimmedTag.split("\\s+")[0];
                }
                tagStrs.add(trimmedTag);
            }

        }


        ArrayList<Tag> finalTags = new ArrayList<>();

        for (String tagStr : tagStrs) {

            boolean added = false;

            for (Tag tag : mSelectedTags) {

                if (tag.getValue().equals(tagStr)) {
                    added = true;
                    finalTags.add(tag);
                    break;
                }
            }

            if (!added) {

                Tag tag = new Tag();
                tag.setId(0);
                tag.setValue(tagStr);

                finalTags.add(tag);
            }
        }

        return finalTags;

    }

    @Override
    public void onPermissionGiven() {
        isPermissionGiven = true;
        toggleMediaBottom();
    }
}
