/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.rooms;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.MembersAdapter;
import com.appster.turtle.adapter.viewholder.MembersViewHolder;
import com.appster.turtle.databinding.ActivityMembersBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.UserActionRequest;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;
import com.appster.turtle.widget.CustomTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
* Activity for member
 */
@SuppressWarnings("ALL")
public class MembersActivity extends BaseActivity implements TextView.OnEditorActionListener, View.OnClickListener, MembersViewHolder.IMembers {

    private ActivityMembersBinding mBinding;
    private RetrofitRestRepository mRepository;

    private int mCurrentPage = 1;

    private List<User> mMembersList;
    private MembersAdapter mAdapter;

    private int mTotalPagesAvailable;
    private boolean isLoading;

    private LinearLayoutManager linearLayoutManager;

    private Room mRoom;
    private Bundle bundle;
    private int mSelectedUserId;
    boolean isFromEditRoom;

    private BaseCallback baseCallback;
    private User user;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_members);

        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMembers(mBinding.etSearch.getText().toString().trim());
    }

    private void initUI() {
        setUpHeader(false, mBinding.clMembersHeader.clHeader, "Members", "Add", R.drawable.back_arrow);

        mBinding.etSearch.setHint(getString(R.string.search_name_username));
        mBinding.etSearch.setTextAppearance(this, R.style.SearchEditText_Small);

        mBinding.etSearch.setOnEditorActionListener(this);
        mBinding.etSearch.addTextChangedListener(searchTextWatcher);

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();

        bundle = getIntent().getExtras();
        mRoom = bundle.getParcelable(Constants.BundleKey.ROOM);
        if (bundle.containsKey(Constants.BundleKey.FROM_EDIT_ROOM))
            isFromEditRoom = bundle.getBoolean(Constants.BundleKey.FROM_EDIT_ROOM);


        mBinding.clMembersHeader.tvHeaderEnd.setVisibility(View.GONE);
        if (mRoom.isUserAdmin()) {
            mBinding.clMembersHeader.tvHeaderEnd.setVisibility(View.VISIBLE);
        }

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvMembersAdded.setLayoutManager(linearLayoutManager);
        mBinding.rvMembersAdded.addItemDecoration(new ItemDecorationView(this, 0, Utils.dpToPx(this, 17)));

        mBinding.clMembersHeader.ivIconStart.setOnClickListener(this);
        mBinding.clMembersHeader.tvHeaderEnd.setEnabled(true);
        mBinding.clMembersHeader.tvHeaderEnd.setOnClickListener(this);

        mBinding.rvMembersAdded.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int mVisibleItemCount = linearLayoutManager.getChildCount();
                int mPastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                int mTotalItemCount = linearLayoutManager.getItemCount();

                if (mCurrentPage < mTotalPagesAvailable && (mVisibleItemCount + mPastVisibleItems <= mTotalItemCount) && !isLoading) {
                    if (mMembersList != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getMembers(mBinding.etSearch.getText().toString().trim());
                    }
                }
            }
        });
    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mBinding.etSearch.getText().toString().trim().length() > 1) {

                mCurrentPage = 1;
                getMembers(charSequence.toString());
            }

            if (mBinding.etSearch.getText().toString().trim().length() == 0) {
                if (!PreferenceUtil.wasLastSearchAllMembers()) {
                    mCurrentPage = 1;
                    getMembers("");
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void getMembers(final String query) {

        if (query.isEmpty()) {
            showProgressBar();
            PreferenceUtil.setLastSearchWasAllMembers(true);
        } else {
            PreferenceUtil.setLastSearchWasAllMembers(false);
        }


        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<SearchUserResponse>(this, mRepository.getApiService().getMembers(mRoom.getRoomId(), mCurrentPage, Constants.LIST_LENGTH, query)) {

            @Override
            public void onSuccess(SearchUserResponse response) {

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                isLoading = false;

                if (mCurrentPage == 1) {

                    mMembersList = response.getData();

                    mAdapter = new MembersAdapter(MembersActivity.this, mMembersList, MembersActivity.this);
                    setAdapter();
                } else {
                    mMembersList.addAll((response.getData()));
                    mAdapter.notifyDataSetChanged();
                }

                hideProgressBar();

            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            if (textView.getText().toString().trim().length() < 2) {
                showToast("Minimum 2 characters required for search");
            } else {

                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            }

            return true;
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_header_end:
                ExplicitIntent.getsInstance().navigateTo(MembersActivity.this, AddMemberActivity.class, bundle);
                break;

            case R.id.iv_icon_start:
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
                break;

            case R.id.tv_make_user_admin:
                makeThisUserAdmin();
                break;

            case R.id.tv_block_user:
                blockThisUser();
                break;

            case R.id.tv_report_flag_user:
                reportOrFlagThisUser();
                break;

            case R.id.tv_remove_user:
                removeThisUser();
                break;

            case R.id.tv_cancel:
                cancel();
                break;


        }
    }

    private void initBottomSheet(int userId) {

        hideKeyboard();

        mSelectedUserId = userId;

        //if admin clicks on their name, don't show bottom sheet
        if (PreferenceUtil.getUser().getUserId() == mSelectedUserId) {
            return;
        }

        user = getUserFromId();
        if (user != null) {
            //if clicked on admin

            if (user.isAdmin()) {
                if (mRoom.isUserAdmin()) {
                    adminSeesAdmin();
                } else {
                    userSeesAdmin();
                }
            } else {
                //if clicked on user
                if (mRoom.isUserAdmin()) {
                    adminSeesUser();
                } else {
                    userSeesUser();
                }

            }
        }
    }

    /*
    Logged in as admin and clicks on admin: Block user, Remove user;
     */
    private void adminSeesAdmin() {
        setMemberActions(false, true, false, true);
    }

    /*
    Logged in as admin and clicks on user: Make admin, Block user, Remove user;
     */
    private void adminSeesUser() {
        setMemberActions(true, true, false, true);
    }

    /*
    Logged in as user and clicks on admin: Report/Flag;
     */
    private void userSeesAdmin() {
        setMemberActions(false, false, true, false);
    }

    /*
    Logged in as user and clicks on user: Report/Flag
     */
    private void userSeesUser() {
        setMemberActions(false, false, true, false);
    }

    private void setMemberActions(boolean makeAdmin, boolean block, boolean reportOrFlag, boolean removeUser) {

        View v = LayoutInflater.from(this).inflate(R.layout.layout_member_actions, mBinding.bottomSheetMembers, false);
        mBinding.bottomSheetMembers.setShouldDimContentView(true);
        mBinding.bottomSheetMembers.showWithSheetView(v);

        if (makeAdmin)
            v.findViewById(R.id.tv_make_user_admin).setOnClickListener(this);
        else
            v.findViewById(R.id.tv_make_user_admin).setVisibility(View.GONE);

        if (block) {
            v.findViewById(R.id.tv_block_user).setOnClickListener(this);
            if (user != null)// && getUserFromId().isBlocked())
                ((CustomTextView) v.findViewById(R.id.tv_block_user)).setText(getString(R.string.unblock_this_user));
            else
                ((CustomTextView) v.findViewById(R.id.tv_block_user)).setText(getString(R.string.block_this_user));
        } else
            v.findViewById(R.id.tv_block_user).setVisibility(View.GONE);

        if (reportOrFlag)
            v.findViewById(R.id.tv_report_flag_user).setOnClickListener(this);
        else
            v.findViewById(R.id.tv_report_flag_user).setVisibility(View.GONE);

        if (removeUser)
            v.findViewById(R.id.tv_remove_user).setOnClickListener(this);
        else
            v.findViewById(R.id.tv_remove_user).setVisibility(View.GONE);

        v.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onMemberSelected(int userId, int pos) {
        initBottomSheet(userId);
    }

    @Override
    public void onMemberAccept(int userId) {

    }

    @Override
    public void onMemberDecline(int userId) {

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

    private void setAdapter() {
        mBinding.rvMembersAdded.setAdapter(mAdapter);
    }

    private void removeThisUser() {

        showProgressBar();

        UserActionRequest userActionRequest = new UserActionRequest();
        userActionRequest.setRoomId(mRoom.getRoomId());
        List<Integer> userIds = new ArrayList<>();
        userIds.add(mSelectedUserId);
        userActionRequest.setUserIds(userIds);

        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<UserActionResponse>(this, mRepository.getApiService().removeUser(userActionRequest)) {

            @Override
            public void onSuccess(UserActionResponse response) {
                if (response.getData().getSuccess()) {
                    removeUserFromList();
                }
            }

            @Override
            public void onFail() {

            }
        };

        mBinding.bottomSheetMembers.dismissSheet();

    }

    private void makeThisUserAdmin() {

        showProgressBar();

        UserActionRequest userActionRequest = new UserActionRequest();
        userActionRequest.setRoomId(mRoom.getRoomId());
        List<Integer> userIds = new ArrayList<>();
        userIds.add(mSelectedUserId);
        userActionRequest.setUserIds(userIds);

        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<UserActionResponse>(this, mRepository.getApiService().makeAdmin(userActionRequest)) {

            @Override
            public void onSuccess(UserActionResponse response) {
                if (response.getData().getSuccess()) {
                    updateAdmins();
                }
            }

            @Override
            public void onFail() {

            }
        };

        mBinding.bottomSheetMembers.dismissSheet();

    }

    private void removeUserFromList() {
        if (user != null) {
            mMembersList.remove(user);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateAdmins() {
        if (user != null) {
            user.setAdmin(true);
            mAdapter.notifyDataSetChanged();
        }

        showNewAdminBottomSheet();
    }

    private void showNewAdminBottomSheet() {
        View v = LayoutInflater.from(MembersActivity.this).inflate(R.layout.layout_new_admin_created, mBinding.bottomSheetMembers, false);
        CustomTextView tv = v.findViewById(R.id.cl_admin_created).findViewById(R.id.tv_admin_created);
        tv.setText("@" + user.getUserName() + getString(R.string.is_now_admin));
        mBinding.bottomSheetMembers.setShouldDimContentView(false);
        mBinding.bottomSheetMembers.showWithSheetView(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MembersActivity.this, user.getUserName(), Toast.LENGTH_SHORT).show();
            }
        });
        hideBottomSheetAfterDelay(2000);
    }

    private void hideBottomSheetAfterDelay(int millis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.bottomSheetMembers.dismissSheet();
            }
        }, millis);
    }

    private void blockThisUser() {

        showProgressBar();

        UserActionRequest request = new UserActionRequest();
        request.setRoomId(mRoom.getRoomId());
        request.setUserIds(Collections.singletonList(mSelectedUserId));

        new BaseCallback<UserActionResponse>(this, mRepository.getApiService().blockUser(request), true) {

            @Override
            public void onSuccess(UserActionResponse response) {
                if (response.getData().getSuccess()) {

                    mBinding.bottomSheetMembers.dismissSheet();
                }
            }

            @Override
            public void onFail() {
                mBinding.bottomSheetMembers.dismissSheet();
            }
        };

    }

    private void reportOrFlagThisUser() {
        showProgressBar();

        UserActionRequest request = new UserActionRequest();
        request.setRoomId(mRoom.getRoomId());
        request.setUserIds(Collections.singletonList(mSelectedUserId));

        new BaseCallback<UserActionResponse>(this, mRepository.getApiService().reportFlagRoomMember(request), true) {

            @Override
            public void onSuccess(UserActionResponse response) {
                showSnackBar(user.getName() + getString(R.string.reported_flagged));
                mBinding.bottomSheetMembers.dismissSheet();

            }

            @Override
            public void onFail() {
                mBinding.bottomSheetMembers.dismissSheet();
            }
        };
    }

    private void cancel() {
        mBinding.bottomSheetMembers.dismissSheet();
    }

    private User getUserFromId() {
        for (User user : mMembersList) {
            if (user.getUserId() == mSelectedUserId) {
                return user;
            }
        }

        return null;
    }

    private User getUserFromId(int id) {
        for (User user : mMembersList) {
            if (user.getUserId() == id) {
                return user;
            }
        }

        return null;
    }
}
