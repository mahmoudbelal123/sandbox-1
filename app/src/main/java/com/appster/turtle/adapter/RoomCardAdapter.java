package com.appster.turtle.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.RoomCardViewHolder;
import com.appster.turtle.model.Room;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.JoinRoomResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.ExplicitIntent;

import java.util.ArrayList;
/*
 * adapter for room card
 */
public class RoomCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private ArrayList<Room> rooms;
    private RetrofitRestRepository mRepository;

    public RoomCardAdapter(Context context, ArrayList<Room> rooms) {

        this.mContext = context;
        this.rooms = rooms;
        mRepository = ((ApplicationController) mContext.getApplicationContext()).provideRepository();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.home_room_item, parent, false);
        return new RoomCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ((RoomCardViewHolder) holder).bindData(rooms.get(position));
        ((RoomCardViewHolder) holder).getBinding().ivJoinRoom.setOnClickListener(view -> {

            if (rooms.get(holder.getAdapterPosition()).getUserReqStatus() == Constants.ROOM_REQUEST.NO_REQUESTED || rooms.get(holder.getAdapterPosition()).getUserReqStatus() == Constants.ROOM_REQUEST.DECLINED)
                confirmUserRequest(holder.getAdapterPosition());

        });

        ((RoomCardViewHolder) holder).getBinding().ivAccept.setOnClickListener(view -> {

            if (rooms.get(holder.getAdapterPosition()).getUserReqStatus() == Constants.ROOM_REQUEST.PENDING)
                invite(holder.getAdapterPosition(), true);

        });

        ((RoomCardViewHolder) holder).getBinding().ivReject.setOnClickListener(view -> {

            if (rooms.get(holder.getAdapterPosition()).getUserReqStatus() == Constants.ROOM_REQUEST.PENDING)
                invite(holder.getAdapterPosition(), false);

        });

        ((RoomCardViewHolder) holder).getBinding().getRoot().setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BundleKey.ROOM, rooms.get(holder.getAdapterPosition()));
            bundle.putInt(Constants.BundleKey.ROOM_ID, rooms.get(holder.getAdapterPosition()).getRoomId());
            bundle.putInt(Constants.POS, holder.getAdapterPosition());
            bundle.putBoolean(Constants.IS_FROM_ROOM,true);

            ExplicitIntent.getsInstance().navigateTo((BaseActivity) mContext, RoomActivityCoordinator.class, bundle);
        });


    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    private void confirmUserRequest(final int position) {


        new BaseCallback<JoinRoomResponse>((BaseActivity) mContext, mRepository.getApiService().joinRoomRequest(rooms.get(position).getRoomId())) {

            @Override
            public void onSuccess(JoinRoomResponse response) {

                if (response.getData().getUserReqStatus() == Constants.ROOM_REQUEST.ACCEPTED) {
                    rooms.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, rooms.size());

                } else {
                    rooms.get(position).setUserReqStatus(response.getData().getUserReqStatus());
                    rooms.get(position).setRequestedByMe(true);
                    notifyItemChanged(position);
                }


                if (mContext instanceof HomeMainActivity)
                    ((HomeMainActivity) mContext).refreshHome();
            }

            @Override
            public void onFail() {

            }
        };


    }

    private void invite(final int position, boolean accept) {


        new BaseCallback<JoinRoomResponse>((BaseActivity) mContext, ((ApplicationController) mContext.getApplicationContext()).provideRepository().getApiService().roomInvite(rooms.get(position).getRoomId(), accept), true) {

            @Override
            public void onSuccess(JoinRoomResponse response) {

                if (response.getData().getUserReqStatus() == Constants.RoomMember.STATUS_CONNECTED) {
                    rooms.remove(position);
                    notifyItemRemoved(position);
                } else {
                    rooms.get(position).setUserReqStatus(response.getData().getUserReqStatus());
                    rooms.get(position).setRequestedByMe(true);
                    notifyItemChanged(position);
                }

                if (mContext instanceof HomeMainActivity)
                    ((HomeMainActivity) mContext).refreshHome();

            }

            @Override
            public void onFail() {

            }
        };


    }

    public void remove(int pos){
        rooms.remove(pos);
   notifyItemRemoved(pos);
    }
}
