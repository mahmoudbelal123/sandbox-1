package com.appster.turtle.ui.rooms;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.MembersAdapter;
import com.appster.turtle.adapter.viewholder.MembersViewHolder;
import com.appster.turtle.databinding.FragmentMemberBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.request.SendInviteRequest;
import com.appster.turtle.network.request.UserActionRequest;
import com.appster.turtle.network.response.SearchUserResponse;
import com.appster.turtle.network.response.UserActionResponse;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;
import com.appster.turtle.widget.CustomTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/*
* Fragment  for request
 */
public class RequestsFragment extends BaseFragment implements MembersViewHolder.IMembers, View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";
    private static final String ARG_ROOM_NUMBER = "ARG_ROOM_NUMBER";
    private FragmentMemberBinding mBinding;
    private boolean isMember;
    private RetrofitRestRepository mRepository;
    private LinearLayoutManager linearLayoutManager;
    private List<User> mMemberList;
    private int mCurrentPage = 1;

    private int mTotalPagesAvailable;
    private BaseCallback baseCallback;
    private boolean isPurchased;
    private MembersAdapter mAdapter;
    private String query = "";
    private int mSelectedUserId;
    private Room mRoom;
    private User user;
    private MemberRequestActivity activity;

    public RequestsFragment() {
        // Required empty public constructor
    }

    public static RequestsFragment newInstance(int sectionNumber, Room roomId) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putParcelable(ARG_ROOM_NUMBER, roomId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getFragmentName() {
        return RequestsFragment.class.getName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_member, container, false);

        isMember = getArguments().getInt(ARG_SECTION_NUMBER) == 0;
        mRoom = getArguments().getParcelable(ARG_ROOM_NUMBER);

        initUI();

        activity = (MemberRequestActivity) getActivity();


        return mBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();

        getMembers();

    }

    private void initUI() {

        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mMemberList = new ArrayList<>();


        mBinding.rvMembersAdded.addOnScrollListener(purchaseScrollListener);
        mBinding.rvMembersAdded.setLayoutManager(linearLayoutManager);
        mBinding.rvMembersAdded.addItemDecoration(new ItemDecorationViewFrg(getActivity(), Utils.dpToPx(getActivity(), 22), 0));


        if (!isMember) {
            mBinding.etSearch.setVisibility(View.GONE);
            mBinding.tvSearch.setVisibility(View.GONE);
        }


        mBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (isMember) {
                    query = charSequence.toString();
                    getMembers();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }
        });
    }

    private boolean isLoading;
    private RecyclerView.OnScrollListener purchaseScrollListener = new RecyclerView.OnScrollListener() {


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int mVisibleItemCount = linearLayoutManager.getChildCount();
            int mTotalItemCount = linearLayoutManager.getItemCount();
            int mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
            if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                if (mMemberList != null) {
                    mCurrentPage++;
                    isLoading = true;
                    getMembers();
                }
            }
        }
    };


    private void getMembers() {


        if (query.isEmpty()) {
            showProgressBar();
            PreferenceUtil.setLastSearchWasAllMembers(true);
        } else {
            PreferenceUtil.setLastSearchWasAllMembers(false);
        }


        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<SearchUserResponse>(getBaseActivity(), mRepository.getApiService().getRoomRequests(mRoom.getRoomId(), mCurrentPage, Constants.LIST_LENGTH, query), query.isEmpty()) {

            @Override
            public void onSuccess(SearchUserResponse response) {

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                isLoading = false;

                if (mCurrentPage == 1) {

                    mMemberList = response.getData();


                    mAdapter = new MembersAdapter(getActivity(), mMemberList, (BaseFragment) RequestsFragment.this);
                    mBinding.rvMembersAdded.setAdapter(mAdapter);
                } else {
                    mMemberList.addAll((response.getData()));
                    mAdapter.notifyDataSetChanged();
                }

                mBinding.tvSearch.setVisibility(View.VISIBLE);
                if (getActivity() != null) {
                    mBinding.tvSearch.setTextColor(ContextCompat.getColor(getActivity(), R.color.bright_teal));
                    mBinding.tvSearch.setText(getResources().getQuantityString(R.plurals.new_requests, mMemberList.size(), mMemberList.size()));

                }
            hideHud();

            }

            @Override
            public void onFail() {
                getBaseActivity().hideProgressBar();
            }
        };

    }


    @Override
    public void onMemberSelected(int userId, int pos) {
        initBottomSheet(userId);
    }

    @Override
    public void onMemberAccept(int userId) {

        confirmUserRequest(userId, Constants.RoomRequest.ROOM_REQUEST_ACCEPT);
    }

    @Override
    public void onMemberDecline(int userId) {
        confirmUserRequest(userId, Constants.RoomRequest.ROOM_REQUEST_DECLINE);

    }

    private User getUserFromId() {
        for (User user : mMemberList) {
            if (user.getUserId() == mSelectedUserId) {
                return user;
            }
        }

        return null;
    }

    private User getUserFromId(int id) {
        for (User user : mMemberList) {
            if (user.getUserId() == id) {
                return user;
            }
        }

        return null;
    }

    public void initBottomSheet(int userId) {

        hideKeyboard();

        mSelectedUserId = userId;

        //if admin clicks on their name, don't show bottom sheet
        if (PreferenceUtil.getUser().getUserId() == mSelectedUserId) {
            return;
        }

        //if clicked on admin
        user = getUserFromId();
        if (user != null) {
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

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_member_actions, activity.getBottomSheet(), false);
        activity.getBottomSheet().setShouldDimContentView(true);
        activity.getBottomSheet().showWithSheetView(v);

        if (makeAdmin)
            v.findViewById(R.id.tv_make_user_admin).setOnClickListener(this);
        else
            v.findViewById(R.id.tv_make_user_admin).setVisibility(View.GONE);

        if (block) {
            v.findViewById(R.id.tv_block_user).setOnClickListener(this);

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
    public void onClick(View view) {
        switch (view.getId()) {


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

            default:
                break;

        }
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

        baseCallback = new BaseCallback<UserActionResponse>(getBaseActivity(), mRepository.getApiService().removeUser(userActionRequest)) {

            @Override
            public void onSuccess(UserActionResponse response) {
                if (response.getData().getSuccess()) {
                    removeUserFromList();
                }
            }

            @Override
            public void onFail() {
//
            }
        };

        activity.getBottomSheet().dismissSheet();

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

        baseCallback = new BaseCallback<UserActionResponse>(getBaseActivity(), mRepository.getApiService().makeAdmin(userActionRequest)) {

            @Override
            public void onSuccess(UserActionResponse response) {
                if (response.getData().getSuccess()) {
                    updateAdmins();
                }
            }

            @Override
            public void onFail() {
//
            }
        };


        activity.getBottomSheet().dismissSheet();

    }

    private void removeUserFromList() {
        if (user != null) {
            mMemberList.remove(user);
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
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_new_admin_created, activity.getBottomSheet(), false);
        CustomTextView tv = v.findViewById(R.id.cl_admin_created).findViewById(R.id.tv_admin_created);
        tv.setText("@" + user.getUserName() + getString(R.string.is_now_admin));
        activity.getBottomSheet().setShouldDimContentView(false);
        activity.getBottomSheet().showWithSheetView(v);
        v.setOnClickListener(view -> Toast.makeText(getActivity(), user.getUserName(), Toast.LENGTH_SHORT).show());
        hideBottomSheetAfterDelay(2000);
    }

    private void hideBottomSheetAfterDelay(int millis) {
        new Handler().postDelayed(() -> activity.getBottomSheet().dismissSheet(), millis);
    }

    private void blockThisUser() {

        showProgressBar();

        UserActionRequest request = new UserActionRequest();
        request.setRoomId(mRoom.getRoomId());
        request.setUserIds(Collections.singletonList(mSelectedUserId));

        new BaseCallback<UserActionResponse>(getBaseActivity(), mRepository.getApiService().blockUser(request), true) {

            @Override
            public void onSuccess(UserActionResponse response) {
                if (response.getData().getSuccess()) {

                    activity.getBottomSheet().dismissSheet();
                }
            }

            @Override
            public void onFail() {
                activity.getBottomSheet().dismissSheet();
            }
        };

    }

    private void reportOrFlagThisUser() {
        showProgressBar();

        UserActionRequest request = new UserActionRequest();
        request.setRoomId(mRoom.getRoomId());
        request.setUserIds(Collections.singletonList(mSelectedUserId));

        new BaseCallback<UserActionResponse>(getBaseActivity(), mRepository.getApiService().reportFlagRoomMember(request), true) {

            @Override
            public void onSuccess(UserActionResponse response) {
                showSnackBar(user.getName() + " has been reported/flagged.");
                activity.getBottomSheet().dismissSheet();

            }

            @Override
            public void onFail() {
                activity.getBottomSheet().dismissSheet();
            }
        };
    }

    private void confirmUserRequest(final int userid, int isAccept) {

        showProgressBar();

        ArrayList<Integer> userIds = new ArrayList<>();
        userIds.add(userid);
        SendInviteRequest sendInviteRequest = new SendInviteRequest();
        sendInviteRequest.setUserIds(userIds);
        sendInviteRequest.setId(mRoom.getRoomId());


        if (baseCallback != null)
            baseCallback.cancel();

        baseCallback = new BaseCallback<BaseResponse>(getBaseActivity(), mRepository.getApiService().actionRoomRequests(isAccept, sendInviteRequest)) {

            @Override
            public void onSuccess(BaseResponse response) {

                int index = -1;
                for (int i = 0; i < mMemberList.size(); i++) {

                    if (mMemberList.get(i).getUserId() == userid) {
                        index = i;
                    }
                }

                if (index != -1) {
                    mMemberList.remove(index);
                    mAdapter.notifyDataSetChanged();
                }

                if (getActivity() instanceof MemberRequestActivity) {
                    ((MemberRequestActivity) getActivity()).refresh();
                }

                mBinding.tvSearch.setText(getResources().getQuantityString(R.plurals.new_requests, mMemberList.size(), mMemberList.size()));

            }

            @Override
            public void onFail() {
//
            }
        };


        activity.getBottomSheet().dismissSheet();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE.FLAG_POST:

                    break;

                case Constants.REQUEST_CODE.REQUEST_REFRESH:
                    mCurrentPage = 1;
                    getMembers();
                    break;
                default:
                    break;
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void cancel() {
        activity.getBottomSheet().dismissSheet();
    }
}
