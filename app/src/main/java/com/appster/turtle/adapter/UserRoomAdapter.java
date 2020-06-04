package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemUserRoomBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.JoinRoomResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.profile.UserSpacesActivity;
import com.appster.turtle.ui.rooms.MemberRequestActivity;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.appster.turtle.util.bindingadapters.CameraBindingAdapters;

import java.util.ArrayList;
/*
 * adapter for user room
 */

public class UserRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Room> rooms;
    private RetrofitRestRepository mRepository;

    public UserRoomAdapter(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserRoomViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_user_room, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((UserRoomViewHolder) holder).setData();

    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    private class UserRoomViewHolder extends RecyclerView.ViewHolder {

        ItemUserRoomBinding binding;

        public UserRoomViewHolder(ItemUserRoomBinding inflate) {
            super(inflate.getRoot());

            binding = inflate;
        }


        public void setData() {

            final Room room = rooms.get(getAdapterPosition());
            binding.setRoom(room);

            if (room.isGlobalRoom()) {
                binding.tvRoomType.setText(R.string.GLOBAL);
                binding.tvRoomType.setTextColor(ContextCompat.getColor(binding.tvRoomType.getContext(), R.color.bright_teal));

            } else {
                binding.tvRoomType.setText(R.string.CAMPUS);
                binding.tvRoomType.setTextColor(ContextCompat.getColor(binding.tvRoomType.getContext(), R.color.app_white));

            }

            binding.ivJoinRoom.setVisibility(View.VISIBLE);
            binding.ivAccept.setVisibility(View.INVISIBLE);
            binding.ivReject.setVisibility(View.INVISIBLE);


            if (room.getUserReqStatus() == Constants.ROOM_REQUEST.ACCEPTED) {
                binding.ivJoinRoom.setImageResource(R.drawable.ic_tick_room);
            } else if (room.getUserReqStatus() == Constants.ROOM_REQUEST.PENDING) {
                if (room.isRequestedByMe())
                    binding.ivJoinRoom.setImageResource(R.drawable.ic_pending);
                else {
                    binding.ivJoinRoom.setVisibility(View.INVISIBLE);
                    binding.ivAccept.setVisibility(View.VISIBLE);
                    binding.ivReject.setVisibility(View.VISIBLE);
                }

            } else {
                binding.ivJoinRoom.setImageResource(R.drawable.ic_plus_room);

            }


            try {

                if (room.getMembersCount() > 2) {
                    binding.tvMemCount.setText("+" + Utils.getIntK(room.getMembersCount() - 2));
                } else {
                    binding.tvMemCount.setText("");
                }
            } catch (Exception e) {
                LogUtils.printStackTrace(e);
                binding.tvMemCount.setText("");
            }

            int userCount = 0;
            for (User user : room.getAllAdmins()) {
                if (userCount == 0) {
                    if (user.getProfileUrlType() == Constants.IMAGE_INITIALS_ID) {

                        binding.cvUser1.setImageResource(R.drawable.ic_pp_cred);

                        CameraBindingAdapters.bindImageUrl(binding.ivUserInit1, user.getProfileUrl());
                        binding.ivUserInit1.setVisibility(View.VISIBLE);
                    } else {
                        binding.ivUserInit1.setVisibility(View.INVISIBLE);
                        CameraBindingAdapters.bindProImageUrl(binding.cvUser1, user.getProfileUrl());

                    }
                } else if (userCount == 1) {

                    if (user.getProfileUrlType() == Constants.IMAGE_INITIALS_ID) {

                        binding.cvUser2.setImageResource(R.drawable.ic_pp_cred);

                        CameraBindingAdapters.bindImageUrl(binding.ivUserInit2, user.getProfileUrl());
                        binding.ivUserInit2.setVisibility(View.VISIBLE);
                    } else {
                        binding.ivUserInit2.setVisibility(View.INVISIBLE);
                        CameraBindingAdapters.bindProImageUrl(binding.cvUser2, user.getProfileUrl());

                    }

                } else
                    break;

                userCount++;


            }

            binding.getRoot().setOnClickListener(view -> {

                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.ROOM, rooms.get(getAdapterPosition()));
                bundle.putInt(Constants.BundleKey.ROOM_ID, rooms.get(getAdapterPosition()).getRoomId());
                bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);

                if (binding.getRoot().getContext() instanceof UserSpacesActivity) {
                    ((UserSpacesActivity) binding.getRoot().getContext()).setScrollPos(getAdapterPosition());
                }


                ExplicitIntent.getsInstance().navigateTo((BaseActivity) binding.getRoot().getContext(), RoomActivityCoordinator.class, bundle);
            });

            binding.ivJoinRoom.setOnClickListener(view -> {

                if (rooms.get(getAdapterPosition()).getUserReqStatus() == Constants.ROOM_REQUEST.NO_REQUESTED || rooms.get(getAdapterPosition()).getUserReqStatus() == Constants.ROOM_REQUEST.DECLINED)
                    confirmUserRequest(getAdapterPosition(), view.getContext());

            });


            binding.rlIvUser1.setOnClickListener(view -> {

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.ROOM_ID, room.getRoomId());
                bundle.putParcelable(Constants.BundleKey.ROOM, room);
                bundle.putBoolean(Constants.BundleKey.IS_ONLY_MEMBERS, true);

                ExplicitIntent.getsInstance().navigateTo((BaseActivity) binding.rlIvUser1.getContext(), MemberRequestActivity.class, bundle);

            });

            binding.ivAccept.setOnClickListener(view -> {

                if (room.getUserReqStatus() == Constants.ROOM_REQUEST.PENDING)
                    invite(view.getContext(), getAdapterPosition(), true);

            });

            binding.ivReject.setOnClickListener(view -> {

                if (room.getUserReqStatus() == Constants.ROOM_REQUEST.PENDING)
                    invite(view.getContext(), getAdapterPosition(), false);

            });


            binding.rlIvUser2.setOnClickListener(view -> {

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.ROOM_ID, room.getRoomId());
                bundle.putParcelable(Constants.BundleKey.ROOM, room);
                bundle.putBoolean(Constants.BundleKey.IS_ONLY_MEMBERS, true);

                ExplicitIntent.getsInstance().navigateTo((BaseActivity) binding.rlIvUser1.getContext(), MemberRequestActivity.class, bundle);


            });


            if (room.getAllAdmins().size() > 0) {
                if (room.getAllAdmins().size() == 1) {
                    binding.cvUser2.setImageBitmap(null);
                    binding.ivUserInit2.setVisibility(View.INVISIBLE);
                }
            } else {
                binding.cvUser1.setImageBitmap(null);
                binding.ivUserInit1.setVisibility(View.INVISIBLE);
                binding.cvUser2.setImageBitmap(null);
                binding.ivUserInit2.setVisibility(View.INVISIBLE);

            }

        }
    }

    private void invite(final Context mContext, final int position, boolean accept) {


        new BaseCallback<JoinRoomResponse>((BaseActivity) mContext, ((ApplicationController) mContext.getApplicationContext()).provideRepository().getApiService().roomInvite(rooms.get(position).getRoomId(), accept), true) {

            @Override
            public void onSuccess(JoinRoomResponse response) {

                rooms.get(position).setUserReqStatus(response.getData().getUserReqStatus());
                rooms.get(position).setRequestedByMe(true);
                notifyItemChanged(position);


            }

            @Override
            public void onFail() {
//
            }
        };


    }


    private void confirmUserRequest(final int position, final Context context) {

        mRepository = ((ApplicationController) context.getApplicationContext()).provideRepository();

        new BaseCallback<JoinRoomResponse>((BaseActivity) context, mRepository.getApiService().joinRoomRequest(rooms.get(position).getRoomId())) {

            @Override
            public void onSuccess(JoinRoomResponse response) {

                if (response.getData().getUserReqStatus() == Constants.ROOM_REQUEST.ACCEPTED) {

                    rooms.get(position).setUserReqStatus(response.getData().getUserReqStatus());
                    notifyItemChanged(position, rooms.size());

                } else {
                    rooms.get(position).setUserReqStatus(response.getData().getUserReqStatus());
                    rooms.get(position).setRequestedByMe(true);
                    notifyItemChanged(position);
                }

            }

            @Override
            public void onFail() {
                //
            }
        };


    }
}
