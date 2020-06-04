package com.appster.turtle.ui.rooms;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.UsersAdapter;
import com.appster.turtle.databinding.ActivityCreateRoomNewBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a room to view its posts
 */
@SuppressWarnings("ALL")
public class CreateRoomActivity extends BaseActivity implements TextView.OnEditorActionListener {

    private static final int CHAR_DESC_COUNT = 140;
    private ActivityCreateRoomNewBinding mBinding;
    private RetrofitRestRepository mRepository;
    private int mCurrentPage = 1;

    private List<User> mUsersList;
    private List<User> mSelectedUsersList;
    private UsersAdapter mAdapter;

    private int mTotalPagesAvailable;
    private boolean isLoading;

    private LinearLayoutManager linearLayoutManager;
    private BaseCallback<SearchUserResponse> baseCallback;
    private int mVisibleItemCount;
    private int mPastVisibleItems;
    private int mTotalItemCount;

    @Override
    public String getActivityName() {
        return CreateRoomActivity.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_room_new);

        setSupportActionBar(mBinding.toolbar);

        initUI();

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        linearLayoutManager.setAutoMeasureEnabled(true);
        mBinding.rvUsers.setNestedScrollingEnabled(false);
        mBinding.rvUsers.setLayoutManager(linearLayoutManager);
        mBinding.rvUsers.addItemDecoration(new ItemDecoration(this, 0, Utils.dpToPx(this, 17)));


        mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        getUsers("");


        mBinding.rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mVisibleItemCount = linearLayoutManager.getChildCount();
                mPastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                mTotalItemCount = linearLayoutManager.getItemCount();

                if (mCurrentPage < mTotalPagesAvailable && (mVisibleItemCount + mPastVisibleItems <= mTotalItemCount) && !isLoading) {
                    if (mUsersList != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getUsers(mBinding.etSearch.getText().toString().trim());
                    }
                }
            }
        });


        mBinding.toolbar.setBackgroundResource(R.color.app_white);


    }

    private void initUI() {
        setUpHeader(false, mBinding.header.clHeader, getString(R.string.create_space), getString(R.string.next), R.drawable.back_arrow);
        mBinding.etRoomName.addTextChangedListener(roomNameTextWatcher);
        mBinding.etDesc.addTextChangedListener(roomNameTextWatcher);
        mSelectedUsersList = new ArrayList<>();
        mBinding.etSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mBinding.etSearch.addTextChangedListener(searchTextWatcher);
        mBinding.etDesc.addTextChangedListener(descTextWatcher);

        mBinding.etDesc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(CHAR_DESC_COUNT)});
    }

    private void getUsers(final String query) {

        rx.Observable<SearchUserResponse> observable;

        if (query.isEmpty()) {
            showProgressBar();
            observable = mRepository.getApiService().getFriends(mCurrentPage, Constants.MyFriend.ACCEPTED_FRIEND, Constants.LIST_LENGTH, query);
            PreferenceUtil.setLastSearchWasForFriends(true);
        } else {
            observable = mRepository.getApiService().searchUser(mCurrentPage, Constants.LIST_LENGTH, query, true);
            PreferenceUtil.setLastSearchWasForFriends(false);
        }


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
                        if ((response.getPagination().getNumberOfElements() == 1)) {
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

                    mAdapter = new UsersAdapter(CreateRoomActivity.this, mUsersList);
                    setAdapter();
                } else {

                    List<User> usersList = response.getData();

                    for (int i = 0; i < usersList.size(); i++) {

                        if (mSelectedUsersList.contains(usersList.get(i))) {
                            usersList.get(i).setSelected(true);
                        }
                    }


                    mAdapter.setmUsersList(usersList);

                }

                hideProgressBar();

            }

            @Override
            public void onFail() {
                hideProgressBar();
            }
        };

    }

    private void setAdapter() {
        mBinding.rvUsers.setAdapter(mAdapter);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            if (textView.getText().toString().trim().length() < 2) {
                showToast(getString(R.string.min_2_char));
            } else {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            }

            return true;
        }

        return false;
    }

    public void nextClicked(View view) {

        ArrayList<Integer> userIdsList = new ArrayList<>();
        for (User user : mSelectedUsersList) {
            userIdsList.add(user.getUserId());
        }

        Intent i = new Intent(CreateRoomActivity.this, RoomInfoActivity.class);
        i.putIntegerArrayListExtra(Constants.USER_IDS_KEY, userIdsList);
        //convert first letter to caps
        String etRoomName = mBinding.etRoomName.getText().toString().trim();
        i.putExtra(Constants.ROOM_NAME_KEY, etRoomName.substring(0, 1).toUpperCase() + etRoomName.substring(1));
        i.putExtra(Constants.ROOM_DESC_KEY, mBinding.etDesc.getText().toString());
        startActivity(i);
    }

    private TextWatcher roomNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            validateRoom();
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            mCurrentPage = 1;

            if (mBinding.etSearch.getText().toString().trim().length() == 0) {
                if (!PreferenceUtil.wasLastSearchForFriends()) {
                    getUsers("");
                    return;
                }
            }

            getUsers(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
//
        }
    };
    private TextWatcher descTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//


        }

        @Override
        public void afterTextChanged(Editable editable) {
            String currentText = editable.toString();
            int remaining = CHAR_DESC_COUNT - currentText.length();
            mBinding.tvDescLength.setText(getResources().getQuantityString(R.plurals.char_remaining, remaining, remaining));


        }
    };


    private void validateRoom() {

        //set editText footer color
        int drawableFooterColor;
        if (!mBinding.etRoomName.hasFocus()) {
            drawableFooterColor = ContextCompat.getColor(this, R.color.text_font_color);
        } else {
            drawableFooterColor = ContextCompat.getColor(this, R.color.bright_teal);
        }


        //set checkmark for valid roomName
        int drawableRoomName;
        if (isValidRoomName()) {
            drawableRoomName = R.drawable.circle_tick_stroke;
        } else {
            drawableRoomName = 0;
        }
        mBinding.etRoomName.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRoomName, 0);

        //enable/disable next button if valid room name and atleast 1 user is selected
        mBinding.header.tvHeaderEnd.setEnabled(isValidRoomName() && isAtleastOneUserSelected() && !mBinding.etDesc.getText().toString().isEmpty());
    }

    private boolean isValidRoomName() {
        return mBinding.etRoomName.getText().toString().trim().length() >= 1
                && mBinding.etRoomName.getText().toString().trim().length() <= 20;
    }

    private boolean isAtleastOneUserSelected() {
        return mSelectedUsersList != null && mSelectedUsersList.size() >= 1;
    }

    public class ItemDecoration extends RecyclerView.ItemDecoration {
        private int itemBottomOffset;
        private int itemTopOffset;

        public ItemDecoration(Context context, int itemTopOffset, int itemBottomOffset) {

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
}
