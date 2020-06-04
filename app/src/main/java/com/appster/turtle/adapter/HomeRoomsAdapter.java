package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemRoomListBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.HomeActivity;
import com.appster.turtle.ui.rooms.HomeFragment;
import com.appster.turtle.util.Alert;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;
/*
 * adapter for room
 */
public class HomeRoomsAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<Room> mRoomsList;
    private OnClickListener listener;

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public HomeRoomsAdapter(Context mContext, List<Room> mRoomsList) {
        this.mContext = mContext;
        this.mRoomsList = mRoomsList;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_room_list, parent, false);
        return new HomeRoomsAdapterViewHolder(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof HomeRoomsAdapterViewHolder) {
            final HomeRoomsAdapterViewHolder holder = (HomeRoomsAdapterViewHolder) viewHolder;
            ItemRoomListBinding roomListBinding = holder.getBinding();
            roomListBinding.setRoom(mRoomsList.get(position));

            roomListBinding.includedFavouriteLayout.tvFavourite.setText(mRoomsList.get(position).isFavourite() ? R.string.unfavourite : R.string.favourite);
            roomListBinding.includedFavouriteLayout.ivFavourite.setImageResource(mRoomsList.get(position).isFavourite() ? R.drawable.ic_interested_selected : R.drawable.ic_interested);

            roomListBinding.clMainItemLayout.setOnClickListener(view -> {

                closeAllSwipe();

                listener.onClick(mRoomsList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            });
        }

        mItemManger.bindView(viewHolder.itemView, position);

    }

    private void closeAllSwipe() {

        mItemManger.closeAllItems();
    }

    @Override
    public int getItemCount() {
        return (mRoomsList != null) ? mRoomsList.size() : 0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.sl_parent;
    }

    private class HomeRoomsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SwipeLayout.SwipeListener {

        private ItemRoomListBinding mBinding;

        public HomeRoomsAdapterViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

            mBinding.includedFavouriteLayout.clBottomWrapperFavouriteChild.setOnClickListener(this);
            mBinding.includedLeaveLayout.clBottomWrapperLeaveChild.setOnClickListener(this);
            mBinding.slParent.setShowMode(SwipeLayout.ShowMode.PullOut);

            mBinding.slParent.addDrag(SwipeLayout.DragEdge.Left, mBinding.includedFavouriteLayout.clBottomWrapperFavourite);
            mBinding.slParent.addDrag(SwipeLayout.DragEdge.Right, mBinding.includedLeaveLayout.clBottomWrapperLeave);

            mBinding.slParent.addSwipeListener(this);

            mBinding.slParent.close();
            mBinding.slParent.setClickToClose(true);


        }


        public ItemRoomListBinding getBinding() {
            return mBinding;
        }

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.cl_bottom_wrapper_favourite_child) {
                closeAllSwipe();
                setFavRoom(mBinding.getRoom());

            } else if (i == R.id.cl_bottom_wrapper_leave_child) {
                closeAllSwipe();
                leaveRoom(mBinding.getRoom());

            }

        }

        @Override
        public void onStartOpen(SwipeLayout layout) {
//start
        }

        @Override
        public void onOpen(SwipeLayout layout) {
//open

        }

        @Override
        public void onStartClose(SwipeLayout layout) {
//begainto close
        }

        @Override
        public void onClose(SwipeLayout layout) {
//close
        }

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
//update
        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
//
        }


    }


    public interface OnClickListener {
        void onClick(Room room, int pos);
    }


    private void leaveRoom(final Room room) {
        RetrofitRestRepository repository = ((ApplicationController) mContext.getApplicationContext()).provideRepository();
        new BaseCallback<BaseResponse>((BaseActivity) mContext, repository.getApiService()
                .leaveRoom(room.getRoomId())) {
            @Override
            public void onSuccess(BaseResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    if (mRoomsList != null) {
                        mRoomsList.remove(room);
                        notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onFail() {
//fail
            }
        };
    }


    private void setFavRoom(final Room room) {
        RetrofitRestRepository repository = ((ApplicationController) mContext.getApplicationContext()).provideRepository();
        new BaseCallback<BaseResponse>((BaseActivity) mContext, repository.getApiService()
                .favRoom(room.getRoomId(), !room.isFavourite())) {
            @Override
            public void onSuccess(BaseResponse response) {

                if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                    room.setFavourite(!room.isFavourite());

                    Alert.createAlert(mContext, "", "Room is now " + (room.isFavourite() ? mContext.getString(R.string.favourite) : mContext.getString(R.string.unfavourite))).setOnDismissListener(dialog -> {

                        dialog.dismiss();

                        if (mContext instanceof HomeActivity) {
                            HomeFragment homeFragment = (HomeFragment) ((HomeActivity) mContext).getFragment(HomeFragment.class.getName());
                            homeFragment.getRoomsFromApi(1);
                        }

                    }).show();
                }

            }

            @Override
            public void onFail() {
//fail
            }
        };
    }

}
