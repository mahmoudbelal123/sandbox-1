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
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.HomeRoomsAdapter;
import com.appster.turtle.databinding.FragmentHomeBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetRoomsResponse;
import com.appster.turtle.network.response.UnreadNotifCountResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.notes.NotesListingActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;
/*
* fragment for home
 */
public class HomeFragment extends BaseFragment implements HomeRoomsAdapter.OnClickListener {

    private FragmentHomeBinding mBinding;
    private RetrofitRestRepository mRepository;
    private Context mContext;

    HomeRoomsAdapter mRoomsAdapter;
    private int mCurrentPage = 1;

    private int mTotalPagesAvailable;
    private List<Room> mRoomsList;
    private LinearLayoutManager mLinearLayoutManager;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private boolean isLoading;

    private BroadcastReceiver mResetNotifCountReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        initUI();
        return mBinding.getRoot();
    }

    private void initUI() {

        mResetNotifCountReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateNotificationCount();
            }
        };

        updateNotificationCount();
        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRoomsList = new ArrayList<>();
        mRoomsAdapter = new HomeRoomsAdapter(getActivity(), mRoomsList);
        mRoomsAdapter.setOnClickListener(this);

        getRoomsFromApi(mCurrentPage);

        mBinding.rvRooms.setLayoutManager(mLinearLayoutManager);
        mBinding.rvRooms.addItemDecoration(new ItemDecorationViewFrg(getActivity(), 0, Utils.dpToPx(getActivity(), 40)));
        mBinding.rvRooms.setAdapter(mRoomsAdapter);

        mBinding.rvRooms.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = mLinearLayoutManager.getChildCount();
                mTotalItemCount = mLinearLayoutManager.getItemCount();
                mPastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                    if (mRoomsList != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getRoomsFromApi(mCurrentPage);


                    }
                }
            }
        });

        mBinding.swipeRoom.setOnRefreshListener(() -> {
            mBinding.swipeRoom.setRefreshing(true);
            mCurrentPage = 1;
            getRoomsFromApi(mCurrentPage);
        });

        mRepository = ((ApplicationController) getActivity().getApplicationContext()).provideRepository();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mResetNotifCountReceiver, new IntentFilter(Constants.Notification.RESET_NOTIF_COUNT));
    }

    public void stopRefresh() {
        mBinding.swipeRoom.setRefreshing(false);
    }

    public void refresh(Room room) {
        addRoomToTop(room);
    }

    public void refreshRoom(Room room, int pos) {
        addRoomToPos(room, pos);
    }

    @Override
    public String getFragmentName() {
        return HomeFragment.class.getName();
    }

    public void hideToolbar() {
        mBinding.etSearch.setVisibility(View.INVISIBLE);
        mBinding.ivMenu.setVisibility(View.INVISIBLE);
        mBinding.ivSearch.setVisibility(View.INVISIBLE);
    }

    public void showToolbar() {
        mBinding.etSearch.setVisibility(View.VISIBLE);
        mBinding.ivMenu.setVisibility(View.VISIBLE);
        mBinding.ivSearch.setVisibility(View.VISIBLE);
    }

    public void addRoomToTop(Room room) {
        mRoomsList.add(1, room);
        mRoomsAdapter.notifyItemInserted(1);
    }

    public void addRoomToPos(Room room, int pos) {
        mRoomsList.set(pos, room);
        mRoomsAdapter.notifyItemChanged(pos);
    }

    public void getRoomsFromApi(final int page) {

        mCurrentPage = page;
        new BaseCallback<GetRoomsResponse>((BaseActivity) getActivity(), mRepository.getApiService()
                .getRooms(page, Constants.LIST_LENGTH)) {
            @Override
            public void onSuccess(GetRoomsResponse response) {

                if (getActivity() == null)
                    return;

                stopRefresh();

                if (page == 1) {
                    mRoomsList.clear();
                    Room notesRoom = new Room(getString(R.string.notes), true, false, true);
                    mRoomsList.add(0, notesRoom);
                }

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                mRoomsList.addAll(response.getData());
                isLoading = false;

                mRoomsAdapter.notifyDataSetChanged();

                updateNotificationCount();
            }

            @Override
            public void onFail() {
                stopRefresh();

            }
        };
    }

    @Override
    public void onClick(Room selRoom, int pos) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.ROOM, selRoom);
        bundle.putInt(Constants.BundleKey.ROOM_ID, pos);

        if (pos == 0) {
            ExplicitIntent.getsInstance().navigateTo(getActivity(), NotesListingActivity.class, bundle);
        } else
            ExplicitIntent.getsInstance().navigateTo(getActivity(), RoomActivityCoordinator.class, bundle);
    }

    public void updateNotificationCount() {


        new BaseCallback<UnreadNotifCountResponse>((BaseActivity) getActivity(), mRepository.getApiService().markUnreadCount(), false) {

            @Override
            public void onSuccess(UnreadNotifCountResponse response) {
                if (mContext != null)
                    ((HomeActivity) mContext).updateNotificationCountDisplayed(response.getData().getCount());
                PreferenceUtil.setNotificationCount(response.getData().getCount());
            }

            @Override
            public void onFail() {

            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
}
