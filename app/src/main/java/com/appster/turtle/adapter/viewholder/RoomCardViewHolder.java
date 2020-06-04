package com.appster.turtle.adapter.viewholder;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.HomeRoomItemBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.User;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.MemberRequestActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.appster.turtle.util.bindingadapters.CameraBindingAdapters;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 19/02/18.
 * view holder of room card view
 */

public class RoomCardViewHolder extends RecyclerView.ViewHolder {
    HomeRoomItemBinding binding;

    public RoomCardViewHolder(View v) {
        super(v);
        binding = DataBindingUtil.bind(v);

    }

    public HomeRoomItemBinding getBinding() {
        return binding;
    }

    public void bindData(final Room room) {

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

        binding.rlIvUser1.setOnClickListener(view -> {

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.ROOM_ID, room.getRoomId());
            bundle.putParcelable(Constants.BundleKey.ROOM, room);
            bundle.putBoolean(Constants.BundleKey.IS_ONLY_MEMBERS, true);

            ExplicitIntent.getsInstance().navigateTo((BaseActivity) binding.rlIvUser1.getContext(), MemberRequestActivity.class, bundle);

        });

        binding.rlIvUser2.setOnClickListener(view -> {

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.ROOM_ID, room.getRoomId());
            bundle.putParcelable(Constants.BundleKey.ROOM, room);
            bundle.putBoolean(Constants.BundleKey.IS_ONLY_MEMBERS, true);

            ExplicitIntent.getsInstance().navigateTo((BaseActivity) binding.rlIvUser1.getContext(), MemberRequestActivity.class, bundle);


        });

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
