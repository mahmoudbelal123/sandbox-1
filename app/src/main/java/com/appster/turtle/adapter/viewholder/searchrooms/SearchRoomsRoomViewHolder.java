/*
 *
 *   Copyright © 2017 Noise . All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder.searchrooms;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemSearchRoomsRoomBinding;
import com.appster.turtle.model.SearchRoomsModel;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.ExplicitIntent;

/**
 * Created by rohan on 01/11/17.
 * View holder for search room
 */

public class SearchRoomsRoomViewHolder extends RecyclerView.ViewHolder {

    private ItemSearchRoomsRoomBinding mBinding;

    public SearchRoomsRoomViewHolder(View root) {
        super(root);

        mBinding = DataBindingUtil.bind(root);
    }

    public void bindData(final SearchRoomsModel room , Context context) {

        mBinding.tvRoomName.setText(room.getRoomName());

        //for searched rooms
        if (room.getExtraElementType() == null) {
            mBinding.tvRoomSubtext.setVisibility(View.VISIBLE);

            if (room.getMembersCount() == 0) {
                mBinding.tvRoomSubtext.setText(R.string.no_member_found
                );
            } else
                mBinding.tvRoomSubtext.setText(context.getResources().getQuantityString(R.plurals.member_found, room.getMembersCount(), room.getMembersCount()));

        }

        //for popular and new rooms
        else {

            //hide subtext when showing popular rooms
            if (room.getExtraElementType() != null) {

                if (room.getExtraElementType() == Constants.SearchExtraElementType.NEW_ROOM)
                    mBinding.tvRoomSubtext.setText(room.getCreatedAt());
                else
                    mBinding.tvRoomSubtext.setVisibility(View.GONE);
            }
        }


        mBinding.tvRoomName.setOnClickListener(view -> {


            Bundle bundle = new Bundle();

            bundle.putInt(Constants.BundleKey.ROOM_ID, Integer.parseInt(room.getRoomId()));
            bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);

            ExplicitIntent.getsInstance().navigateTo((BaseActivity) view.getContext(), RoomActivityCoordinator.class, bundle);

        });

        switch (room.getUserReqStatus()) {
            case Constants.UserAddedToRoomStatus.ADDED:
                mBinding.ivRoomEntryStatus.setImageResource(R.drawable.ic_join_white);
                break;

            case Constants.UserAddedToRoomStatus.PENDING:
                mBinding.ivRoomEntryStatus.setImageResource(R.drawable.pending);
                break;

            default:
                mBinding.ivRoomEntryStatus.setImageResource(R.drawable.add_white);
                break;
        }

    }
}
