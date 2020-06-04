package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.adapter.RoomCardAdapter;
import com.appster.turtle.databinding.ItemHeaderPostBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetRoomsResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;

/**
 * Created  on 03/07/18 .
 */

public class HeaderItemViewholder extends RecyclerView.ViewHolder {


    ItemHeaderPostBinding headerPostBinding;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mPastVisiblesItems;
    private int currentRoomPage = 1;
    private int mTotalPagesAvailable;
    private boolean isRoomLoading;
    private RetrofitRestRepository mRepository;
    private ArrayList<Room> mRooms = new ArrayList<>();


    public HeaderItemViewholder(ItemHeaderPostBinding headerPostBinding, Context context) {
        super(headerPostBinding.getRoot());
        this.headerPostBinding = headerPostBinding;
        mRepository = ((ApplicationController) context.getApplicationContext()).provideRepository();

    }

    public void setData(ArrayList<Room> rooms, final Context context, final boolean home, final int screen) {
        final RoomCardAdapter roomCardAdapter = new RoomCardAdapter(context, mRooms);
        final LinearLayoutManager roomLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        headerPostBinding.rvRooms.setLayoutManager(roomLayoutManager);

        ItemDecorationView itemDecorationView = new ItemDecorationView(context, 0, 0, Utils.dpToPx(context, 9.8f), 0);
        if (headerPostBinding.rvRooms.getItemDecorationCount() == 0) {
            headerPostBinding.rvRooms.addItemDecoration(itemDecorationView);
        }


        headerPostBinding.rvRooms.setAdapter(roomCardAdapter);
        getRooms(context, home, screen, roomCardAdapter);

        headerPostBinding.rvRooms.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mVisibleItemCount = roomLayoutManager.getChildCount();
                mTotalItemCount = roomLayoutManager.getItemCount();
                mPastVisiblesItems = roomLayoutManager.findFirstVisibleItemPosition();
                if (currentRoomPage < mTotalPagesAvailable && ((mPastVisiblesItems + mVisibleItemCount) >= mTotalItemCount && !isRoomLoading)) {
                    currentRoomPage++;

                    isRoomLoading = true;
                    getRooms(context, home, screen, roomCardAdapter);


                }
            }
        });


    }

    public void getRooms(Context context, boolean home, int screen, final RoomCardAdapter roomCardAdapter) {
        //headerPostBinding.loading.setVisibility(View.VISIBLE);
        if (currentRoomPage == 1) {

        }

        new BaseCallback<GetRoomsResponse>((BaseActivity) context, mRepository.getApiService()
                .getHomeRooms(currentRoomPage, Constants.LIST_LENGTH, screen, home)) {
            @Override
            public void onSuccess(GetRoomsResponse response) {
               // headerPostBinding.loading.setVisibility(View.GONE);

                /*if (!isPostLoading) {
                    binding.loading.setVisibility(View.GONE);
                    isDataRefreshed = false;
                }*/


                isRoomLoading = false;

                mTotalPagesAvailable = response.getPagination().getTotalPages();
                if (currentRoomPage == 1) {
                    mRooms.clear();
                }

                mRooms.addAll(response.getData());

                if (currentRoomPage == 1)
                    roomCardAdapter.notifyDataSetChanged();
                else
                    roomCardAdapter.notifyItemChanged(mRooms.size() + 1, mRooms);


                /*if (currentRoomPage == 1)
                    getPosts();*/

//                isSwipeToRefresh = false;

            }

            @Override
            public void onFail() {
                isRoomLoading = false;
               // headerPostBinding.loading.setVisibility(View.GONE);

                //isSwipeToRefresh = false;

                /*if (!isPostLoading) {
                    binding.loading.setVisibility(View.GONE);
                    isDataRefreshed = false;
                }
                binding.swipeRefresh.setRefreshing(false);*/

            }
        };
    }

    public class ItemDecorationView extends RecyclerView.ItemDecoration {
        private int itemBottomOffset;
        private int itemTopOffset;
        private int itemStartOffset;
        private int itemEndOffset;

        public ItemDecorationView(Context context, int itemTopOffset, int itemBottomOffset) {

            this.itemTopOffset = itemTopOffset;
            this.itemBottomOffset = itemBottomOffset;
        }

        public ItemDecorationView(Context context, int itemTopOffset, int itemBottomOffset, int itemStartOffset, int itemEndOffset) {

            this.itemTopOffset = itemTopOffset;
            this.itemBottomOffset = itemBottomOffset;
            this.itemStartOffset = itemStartOffset;
            this.itemEndOffset = itemEndOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(itemStartOffset, itemTopOffset, itemEndOffset, itemBottomOffset);
        }

    }

}
