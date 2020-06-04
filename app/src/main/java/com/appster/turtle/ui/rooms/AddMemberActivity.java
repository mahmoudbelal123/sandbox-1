package com.appster.turtle.ui.rooms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.adapter.UsersAdapter;
import com.appster.turtle.databinding.ActivityAddMemberBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.SendInviteRequest;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.group.Group;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.util.ArrayList;
import java.util.List;
/*
* Activity for add member
 */
@SuppressWarnings("ALL")
public class AddMemberActivity extends BaseActivity implements TextView.OnEditorActionListener, View.OnClickListener, BaseActivity.PermissionI {

    private RetrofitRestRepository mRepository;
    private static final int RESULT_PICK_CONTACT = 10223;
    private boolean isPermissionGiven;

    private int mCurrentPage = 1;

    private List<User> mUsersList;
    private List<User> mSelectedUsersList;
    private UsersAdapter mAdapter;

    private int mTotalPagesAvailable;
    private boolean isLoading;

    private LinearLayoutManager linearLayoutManager;
    private ActivityAddMemberBinding mBinding;
    private Room room;
    private List<Contact> contacts;
    private List<Group> groups;
    private BaseCallback baseCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_member);

        initUI();
        Bundle bundle = getIntent().getExtras();
        if (bundle.getParcelable(Constants.BundleKey.ROOM) != null) {
            room = bundle.getParcelable(Constants.BundleKey.ROOM);
        }

        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        getUsers("");

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvUsers.setLayoutManager(linearLayoutManager);
        mBinding.rvUsers.addItemDecoration(new ItemDecoration(0, Utils.dpToPx(this, 17)));

        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = mBinding.getRoot().getRootView().getHeight() - mBinding.getRoot().getHeight();

            if (heightDiff > mBinding.getRoot().getHeight() / 4) {
                mBinding.tvSendInvite.setVisibility(View.GONE);
            } else {
                mBinding.tvSendInvite.setVisibility(View.VISIBLE);
            }

        });


        mBinding.rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int mVisibleItemCount = linearLayoutManager.getChildCount();
                int mPastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                int mTotalItemCount = linearLayoutManager.getItemCount();

                if (mCurrentPage < mTotalPagesAvailable && (mVisibleItemCount + mPastVisibleItems <= mTotalItemCount) && !isLoading) {
                    if (mUsersList != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getUsers(mBinding.etSearch.getText().toString().trim());
                    }
                }
            }
        });
    }

    private void initUI() {

        setUpHeader(false, mBinding.clMembersHeader.clHeader, getString(R.string.add_members_small), getString(R.string.contacts), R.drawable.back_arrow);

        mSelectedUsersList = new ArrayList<>();
        mBinding.etSearch.setHint(getString(R.string.search_name_or_college));
        mBinding.etSearch.setOnEditorActionListener(this);
        mBinding.etSearch.addTextChangedListener(searchTextWatcher);
        mBinding.clMembersHeader.tvHeaderEnd.setOnClickListener(this);
        mBinding.tvSendInvite.setOnClickListener(this);
        mBinding.clMembersHeader.tvHeaderEnd.setEnabled(true);
        int paddingTop = mBinding.clMembersHeader.tvHeaderEnd.getPaddingBottom();
        mBinding.clMembersHeader.tvHeaderEnd.setPadding(Utils.dpToPx(AddMemberActivity.this, 7.7f), paddingTop, Utils.dpToPx(AddMemberActivity.this, 7.7f), paddingTop);
        mBinding.clMembersHeader.tvHeaderEnd.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(AddMemberActivity.this, R.drawable.ic_contacts), null, null, null);
        mBinding.clMembersHeader.tvHeaderEnd.setCompoundDrawablePadding(Utils.dpToPx(AddMemberActivity.this, 5.6f));

    }

    private void getUsers(final String query) {

        if (query.isEmpty())
            showProgressBar();

        rx.Observable<SearchUserResponse> observable;

        observable = mRepository.getApiService().searchNewMembers(room.getRoomId(), mCurrentPage, Constants.LIST_LENGTH, query);
        PreferenceUtil.setLastSearchWasForFriends(false);

        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<SearchUserResponse>(this, observable) {

            @Override
            public void onSuccess(SearchUserResponse response) {

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                isLoading = false;

                if (mCurrentPage == 1) {
                    mUsersList = response.getData();

                    //check if 'friends' queried
                    if (query.isEmpty()) {
                        mBinding.tvUsersFound.setVisibility(View.GONE);
                    } else {
                        mBinding.tvUsersFound.setVisibility(View.VISIBLE);
                        if ((response.getPagination().getNumberOfElements() == 0)) {
                            mBinding.tvUsersFound.setText(R.string.no_user);
                        } else if ((response.getPagination().getNumberOfElements() == 1)) {
                            mBinding.tvUsersFound.setText(String.valueOf(response.getPagination().getNumberOfElements()).concat(" user found"));
                        } else {
                            mBinding.tvUsersFound.setText(String.valueOf(response.getPagination().getNumberOfElements()).concat(" users found"));
                        }
                    }

                    for (int i = 0; i < mUsersList.size(); i++) {

                        if (mSelectedUsersList.contains(mUsersList.get(i))) {
                            mUsersList.get(i).setSelected(true);
                        }
                    }

                    mAdapter = new UsersAdapter(AddMemberActivity.this, mUsersList);
                    setAdapter();
                } else {

                    List<User> usersList = response.getData();

                    for (int i = 0; i < usersList.size(); i++) {

                        if (mSelectedUsersList.contains(usersList.get(i))) {
                            usersList.get(i).setSelected(true);
                        }
                    }

                    mUsersList.addAll(usersList);
                    mAdapter.notifyDataSetChanged();
                }


                hideProgressBar();

            }

            @Override
            public void onFail() {
                hideProgressBar();
                isLoading = false;
            }
        };


    }

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (mBinding.etSearch.getText().toString().trim().length() == 0) {
                if (!PreferenceUtil.wasLastSearchForFriends()) {
                    mCurrentPage = 1;
                    getUsers("");
                }
            }
            if (mBinding.etSearch.getText().toString().trim().length() > 1) {
                //suggestive search
                mCurrentPage = 1;
                getUsers(charSequence.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };


    private void setAdapter() {
        mBinding.rvUsers.setAdapter(mAdapter);
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
    public String getActivityName() {
        return AddMemberActivity.class.getName();
    }

    @Override
    public void onPermissionGiven() {
        isPermissionGiven = true;
        fetchContacts();
    }

    public class ItemDecoration extends RecyclerView.ItemDecoration {
        private int itemBottomOffset;
        private int itemTopOffset;

        public ItemDecoration(int itemTopOffset, int itemBottomOffset) {

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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_header_end:
                fetchContacts();

                break;

            case R.id.tv_send_invite:
                sendInvite();
                break;

            default:
                break;

        }

    }

    private void fetchContacts() {

        if (isPermissionGiven) {

            Intent intent = new Intent(this, ContactPickerActivity.class)
                    .putExtra(ContactPickerActivity.EXTRA_THEME, R.style.Theme_Light_Base)
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                    .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, true)

                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
            startActivityForResult(intent, RESULT_PICK_CONTACT);
        } else
            checkPermissions(new String[]{Manifest.permission.READ_CONTACTS}, this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_PICK_CONTACT && resultCode == Activity.RESULT_OK &&
                data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {

            contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
            groups = (List<Group>) data.getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA);

            sendSMS();

        }
    }

    private void sendSMS() {

        StringBuilder sBuffer = new StringBuilder("smsto:");


        try {
            if (groups != null && !groups.isEmpty()) {
                for (Group group : groups) {
                    for (Contact contact : group.getContacts()) {

                        sBuffer.append(contact.getPhone(0) + ";");
                    }
                }
            }
            if (contacts != null && !contacts.isEmpty()) {
                for (Contact contact : contacts) {
                    sBuffer.append(contact.getPhone(0) + ";");

                }
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }

        if (sBuffer.toString().contains(";"))
            sBuffer.deleteCharAt(sBuffer.length() - 1);

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(sBuffer.toString()));
        smsIntent.putExtra("sms_body", getString(R.string.sms_body) + BuildConfig.BASE_URL.replace("/api/v1", "") + Utils.deeplinkRoom(room.getRoomId()));
        startActivity(smsIntent);
    }

    private void sendInvite() {
        ArrayList<Integer> userIdsList = new ArrayList<>();
        for (User user : mSelectedUsersList) {
            userIdsList.add(user.getUserId());
        }

        showProgressBar();

        SendInviteRequest sendInviteRequest = new SendInviteRequest();
        sendInviteRequest.setUserIds(userIdsList);
        sendInviteRequest.setId(room.getRoomId());

        new BaseCallback<BaseResponse>(AddMemberActivity.this, mRepository.getApiService().sendInvite(sendInviteRequest)) {
            @Override
            public void onSuccess(BaseResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {

                    Alert.createAlert(AddMemberActivity.this, "", getString(R.string.invitation_sent)).setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            dialog.dismiss();
                            finish();

                        }
                    }).show();

                }
            }

            @Override
            public void onFail() {

            }
        };
    }

    public void removeUserFromList(int userId) {
        for (User user : mUsersList) {
            if (user.getUserId() == userId && mSelectedUsersList.contains(user)) {
                mSelectedUsersList.remove(user);
                validateRoom();
                return;
            }
        }
    }

    public void addUserToList(int userId) {
        for (User user : mUsersList) {
            if (user.getUserId() == userId) {
                mSelectedUsersList.add(user);
                validateRoom();
                return;
            }
        }
    }

    private void validateRoom() {
        if (mSelectedUsersList != null && mSelectedUsersList.size() >= 1) {
            mBinding.tvSendInvite.setEnabled(true);
        } else {
            mBinding.tvSendInvite.setEnabled(false);

        }
    }


    public void confirmUserRequest(final int userid, int isAccept) {

        showProgressBar();

        ArrayList<Integer> userIds = new ArrayList<>();
        userIds.add(userid);
        SendInviteRequest sendInviteRequest = new SendInviteRequest();
        sendInviteRequest.setUserIds(userIds);
        sendInviteRequest.setId(room.getRoomId());


        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<BaseResponse>(AddMemberActivity.this, mRepository.getApiService().actionRoomRequests(isAccept, sendInviteRequest)) {

            @Override
            public void onSuccess(BaseResponse response) {

                int index = -1;
                for (int i = 0; i < mUsersList.size(); i++) {

                    if (mUsersList.get(i).getUserId() == userid) {
                        index = i;
                    }
                }

                if (index != -1) {
                    mUsersList.remove(index);
                    mAdapter.notifyDataSetChanged();
                }

                if (mBinding.etSearch.getText().toString().isEmpty()) {
                    mBinding.tvUsersFound.setVisibility(View.GONE);
                } else {
                    mBinding.tvUsersFound.setVisibility(View.VISIBLE);
                    if ((mUsersList.size() == 0)) {
                        mBinding.tvUsersFound.setText(R.string.no_user);
                    } else if ((mUsersList.size() == 1)) {
                        mBinding.tvUsersFound.setText(String.valueOf(mUsersList.size()).concat(" user found"));
                    } else {
                        mBinding.tvUsersFound.setText(String.valueOf(mUsersList.size()).concat(" users found"));
                    }
                }


            }

            @Override
            public void onFail() {

            }
        };


    }

    @Override
    public void onBackPressed() {

        if (getIntent().getExtras().getBoolean(Constants.BundleKey.ROOM_REFRESH, false)) {
            setResult(RESULT_OK);
            finish();

        } else
            super.onBackPressed();

    }
}
