/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.HomeRoomsAdapter;
import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetRoomsResponse;
import com.appster.turtle.network.response.UnreadNotifCountResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.notes.NotesListingActivity;
import com.appster.turtle.ui.rooms.HomeActivity;
import com.appster.turtle.ui.rooms.HomeFragment;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
public class HomeViewModel extends BaseObservable implements HomeRoomsAdapter.OnClickListener {

    private final Fragment fragment;
    private RetrofitRestRepository repository;

    HomeRoomsAdapter adapter;
    private int mCurrentPage = 1;

    private int mTotalPagesAvailable;
    private List<Room> rooms;
    private LinearLayoutManager linearLayoutManager;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private boolean isLoading;

    public HomeViewModel(Fragment fragment) {
        this.fragment = fragment;
        this.repository = ((ApplicationController) fragment.getActivity().getApplicationContext()).provideRepository();
        linearLayoutManager = new LinearLayoutManager(fragment.getActivity(), LinearLayoutManager.VERTICAL, false);
        rooms = new ArrayList<>();
        adapter = new HomeRoomsAdapter(fragment.getActivity(), rooms);
        adapter.setOnClickListener(this);
    }

    public void addRoomToTop(Room room) {
        rooms.add(1, room);
        adapter.notifyItemInserted(1);
    }

    public void addRoomToPos(Room room, int pos) {
        rooms.set(pos, room);
        adapter.notifyItemInserted(pos);
    }

    public void getRoomsFromApi(final int mCurrentPage) {


        new BaseCallback<GetRoomsResponse>((BaseActivity) fragment.getActivity(), repository.getApiService()
                .getRooms(mCurrentPage, Constants.LIST_LENGTH)) {
            @Override
            public void onSuccess(GetRoomsResponse response) {

                if (fragment.getActivity() == null)
                    return;

                ((HomeFragment) fragment).stopRefresh();

                if (mCurrentPage == 1) {
                    HomeViewModel.this.mCurrentPage = 1;
                    rooms.clear();
                    Room notesRoom = new Room(fragment.getString(R.string.notes), true, false, true);
                    rooms.add(0, notesRoom);
                }

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                rooms.addAll(response.getData());
                isLoading = false;

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFail() {
                ((HomeFragment) fragment).stopRefresh();

            }
        };

    }


    @Bindable
    public HomeRoomsAdapter getAdapter() {
        return adapter;
    }

    @Bindable
    public RecyclerView.OnScrollListener getScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = linearLayoutManager.getChildCount();
                mTotalItemCount = linearLayoutManager.getItemCount();
                mPastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (mCurrentPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isLoading)) {
                    if (rooms != null) {
                        mCurrentPage++;
                        isLoading = true;
                        getRoomsFromApi(mCurrentPage);


                    }
                }


            }
        };
    }

    @Bindable
    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

    @Bindable
    public ItemDecoration getItemDecoration() {
        return new ItemDecoration(fragment.getActivity(), 0, Utils.dpToPx(fragment.getActivity(), 40));
    }

    public void onClick(Room selRoom, int pos) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.ROOM, selRoom);
        bundle.putInt(Constants.BundleKey.ROOM_ID, pos);

        //for handling Notes click
        if (pos == 0) {
            ExplicitIntent.getsInstance().navigateTo(fragment.getActivity(), NotesListingActivity.class, bundle);
        } else
//            ExplicitIntent.getsInstance().navigateTo(fragment.getActivity(), RoomActivity.class, bundle);
            ExplicitIntent.getsInstance().navigateTo(fragment.getActivity(), RoomActivityCoordinator.class, bundle);
    }

    public void updateNotificationCount() {

        ((BaseActivity) fragment.getActivity()).showProgressBar();

        new BaseCallback<UnreadNotifCountResponse>((BaseActivity) fragment.getActivity(), repository.getApiService().markRead(), true) {

            @Override
            public void onSuccess(UnreadNotifCountResponse response) {
                ((HomeActivity) fragment.getActivity()).updateNotificationCountDisplayed(response.getData().getCount());
            }

            @Override
            public void onFail() {

            }
        };
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

}