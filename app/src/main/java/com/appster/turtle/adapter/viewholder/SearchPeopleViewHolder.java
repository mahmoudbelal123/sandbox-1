/*
 *
 *   Copyright © 2017 NOISE. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemSearchPeopleBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.AddMemberActivity;
import com.appster.turtle.ui.rooms.CreateRoomActivity;
import com.appster.turtle.ui.search.SearchRoomsAndPeopleActivity;

/**
 * Created by rohantaneja on 12-Sep-2017
 * view holder of search people view
 */

public class SearchPeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ItemSearchPeopleBinding mBinding;
    private Context mContext;
    private User mUser;

    public SearchPeopleViewHolder(View itemView) {
        super(itemView);

        mBinding = DataBindingUtil.bind(itemView);

        mBinding.ivRequestStatus.setOnClickListener(this);
    }

    public void bindData(Context context, User user) {

        mContext = context;
        mUser = user;

        mBinding.setUser(user);

        if (context instanceof SearchRoomsAndPeopleActivity) {

            itemView.setOnClickListener(view -> ((SearchRoomsAndPeopleActivity) mContext).hideKeyboard());

            switch (String.valueOf(mUser.getUserReqStatus())) {
                case Constants.UserAddedToRoomStatus.ADDED:
                    mBinding.ivRequestStatus.setImageResource(R.drawable.circle_tick_stroke);
                    break;

                case Constants.UserAddedToRoomStatus.PENDING:
                    mBinding.ivRequestStatus.setImageResource(R.drawable.ic_pending);
                    // TODO: 01/11/17 Change this icon to pending icon
                    break;

                default:
                    mBinding.ivRequestStatus.setImageResource(R.drawable.ic_circle_unticked);
                    break;
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_selection:

                if (mContext instanceof CreateRoomActivity) {
                    if (mUser.isSelected()) {
                        mUser.setSelected(false);
                        ((CreateRoomActivity) mContext).removeUserFromList(mUser.getUserId());
                    } else {
                        mUser.setSelected(true);
                        ((CreateRoomActivity) mContext).addUserToList(mUser.getUserId());
                    }
                } else if (mContext instanceof AddMemberActivity) {

                    if (mUser.getUserReqStatus() == Constants.RoomMember.STATUS_NOT_CONNECTED || mUser.getUserReqStatus() == Constants.RoomMember.STATUS_REJECTED) {
                        if (mUser.isSelected()) {
                            mUser.setSelected(false);
                            ((AddMemberActivity) mContext).removeUserFromList(mUser.getUserId());
                        } else {
                            mUser.setSelected(true);
                            ((AddMemberActivity) mContext).addUserToList(mUser.getUserId());
                        }
                    } else {
                        return;
                    }
                }

                mBinding.setUser(mUser);
                break;

            default:
                break;
        }
    }
}
