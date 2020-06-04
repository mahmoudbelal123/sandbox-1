/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemUserBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.friends.AddFriendsActivity;
import com.appster.turtle.ui.rooms.AddMemberActivity;
import com.appster.turtle.ui.rooms.CreateRoomActivity;
import com.appster.turtle.ui.search.SearchRoomsAndPeopleActivity;
import com.appster.turtle.util.CustomTypefaceSpan;

/**
 * Created by rohantaneja on 12-Sep-2017
 * view holder of user view
 */

public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ItemUserBinding mBinding;
    private Context mContext;
    private User mUser;

    public UsersViewHolder(View itemView) {
        super(itemView);

        mBinding = DataBindingUtil.bind(itemView);

        mBinding.ivSelection.setOnClickListener(this);
    }

    public void bindData(Context context, User user) {
        mContext = context;
        mUser = user;

        String userNameAndUsernameString = mUser.getFullName() + " " + mUser.getName();
        SpannableString userNameAndUsernameSpannableString = new SpannableString(userNameAndUsernameString);

        Typeface einaSemiBoldTypeface = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.eina_01_semibold));

        userNameAndUsernameSpannableString.setSpan(new CustomTypefaceSpan("", einaSemiBoldTypeface), 0, mUser.getFullName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userNameAndUsernameSpannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.textcolor_room_name)), 0, mUser.getFullName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mBinding.tvStudentName.setText(userNameAndUsernameSpannableString);

        mBinding.setUser(user);

//        if (user.getProfileUrlType() == Constants.IMAGE_INITIALS_ID) {
//            mBinding.ivCircleImage.setImageResource(R.drawable.ic_pp_cred);
//            CameraBindingAdapters.bindImageUrl(mBinding.ivUserInitials, user.getProfileUrl());
//            mBinding.ivUserInitials.setVisibility(View.VISIBLE);
//        } else {
//            mBinding.ivUserInitials.setVisibility(View.INVISIBLE);
//            CameraBindingAdapters.bindImageUrl(mBinding.ivCircleImage, user.getProfileUrl());
//
//        }

        if (context instanceof CreateRoomActivity) {

//            mBinding.tvPending.setVisibility(View.INVISIBLE);


//            if (user.getUserReqStatus() == -1) {

            if (user.isSelected())
                mBinding.ivSelection.setImageResource(R.drawable.circle_tick_stroke);
            else
                mBinding.ivSelection.setImageResource(R.drawable.plus);

//            } else if (user.getUserReqStatus() == 0) {
//
//                mBinding.ivSelection.setImageResource(R.drawable.ic_status_pending);
////                mBinding.tvPending.setVisibility(View.VISIBLE);
//            }

        } else if (context instanceof AddMemberActivity) {

//            mBinding.tvPending.setVisibility(View.INVISIBLE);

            mBinding.ivSelection.setVisibility(View.VISIBLE);
            mBinding.ivAccept.setVisibility(View.GONE);
            mBinding.ivDecline.setVisibility(View.GONE);
            mBinding.tvRespond.setVisibility(View.GONE);


            mBinding.ivAccept.setOnClickListener(this);
            mBinding.ivDecline.setOnClickListener(this);


            if (user.getUserReqStatus() == Constants.RoomMember.STATUS_NOT_CONNECTED || user.getUserReqStatus() == Constants.RoomMember.STATUS_REJECTED) {

                if (user.isSelected())
                    mBinding.ivSelection.setImageResource(R.drawable.circle_tick_stroke);
                else
                    mBinding.ivSelection.setImageResource(R.drawable.plus);

            } else if (user.getUserReqStatus() == Constants.RoomMember.STATUS_PENDING) {

                if (!user.isRequestedByMe()) {
                    mBinding.ivSelection.setVisibility(View.INVISIBLE);
                    mBinding.ivAccept.setVisibility(View.VISIBLE);
                    mBinding.ivDecline.setVisibility(View.VISIBLE);
                    mBinding.tvRespond.setVisibility(View.VISIBLE);
                } else {
                    mBinding.ivSelection.setVisibility(View.VISIBLE);
                    mBinding.ivAccept.setVisibility(View.GONE);
                    mBinding.ivDecline.setVisibility(View.GONE);
                    mBinding.tvRespond.setVisibility(View.GONE);
                    mBinding.ivSelection.setImageResource(R.drawable.ic_status_pending);
                }
//                mBinding.tvPending.setVisibility(View.VISIBLE);
            }

        } else if (context instanceof AddFriendsActivity) {
            mBinding.tvPending.setVisibility(View.INVISIBLE);
            if (user.isFriend()) {

                mBinding.ivSelection.setImageResource(R.drawable.circle_tick_stroke);
            } else {
                switch (user.getUserReqStatus()) {
                    case Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED:
                    case Constants.MyFriend.STATUS_REJECTED:
                        mBinding.ivSelection.setImageResource(R.drawable.plus);
                        break;
                    case Constants.MyFriend.FRIEND_STATUS_PENDING:
                        mBinding.ivSelection.setImageResource(R.drawable.ic_status_pending);
                        break;
                    case Constants.MyFriend.FRIEND_STATUS_CONNECTED:
                        mBinding.ivSelection.setImageResource(R.drawable.circle_tick_stroke);
                }
            }
        }
        if (context instanceof SearchRoomsAndPeopleActivity) {

            itemView.setOnTouchListener((view, motionEvent) -> {
                ((SearchRoomsAndPeopleActivity) mContext).hideKeyboard();
                return false;
            });


        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_accept:

                if (mContext instanceof AddMemberActivity) {

                    ((AddMemberActivity) mContext).confirmUserRequest(mUser.getUserId(), Constants.RoomRequest.ROOM_REQUEST_ACCEPT);
                }


                break;

            case R.id.iv_decline:
                if (mContext instanceof AddMemberActivity) {

                    ((AddMemberActivity) mContext).confirmUserRequest(mUser.getUserId(), Constants.RoomRequest.ROOM_REQUEST_DECLINE);
                }
                break;

            case R.id.iv_selection:


                if (mContext instanceof BaseActivity)
                    ((BaseActivity) mContext).hideKeyboard();

                if (mContext instanceof CreateRoomActivity) {
                    if (mUser.isSelected()) {
                        mUser.setSelected(false);
                        ((CreateRoomActivity) mContext).removeUserFromList(mUser.getUserId());
                        mBinding.ivSelection.setImageResource(R.drawable.plus);


                    } else {
                        mUser.setSelected(true);
                        ((CreateRoomActivity) mContext).addUserToList(mUser.getUserId());
                        mBinding.ivSelection.setImageResource(R.drawable.circle_tick_stroke);

                    }
                } else if (mContext instanceof AddMemberActivity) {

                    if (mUser.getUserReqStatus() == Constants.RoomMember.STATUS_NOT_CONNECTED ||
                            mUser.getUserReqStatus() == Constants.RoomMember.STATUS_REJECTED
                            ) {
                        if (mUser.isSelected()) {
                            mUser.setSelected(false);
                            ((AddMemberActivity) mContext).removeUserFromList(mUser.getUserId());
                            mBinding.ivSelection.setImageResource(R.drawable.plus);

                        } else {
                            mUser.setSelected(true);
                            ((AddMemberActivity) mContext).addUserToList(mUser.getUserId());
                            mBinding.ivSelection.setImageResource(R.drawable.circle_tick_stroke);

                        }
                    } else {
                        return;
                    }
                } else if (mContext instanceof AddFriendsActivity) {

                    if (mUser.isFriend()) {
                        return;
                    }

                    if (mUser.getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED ||
                            mUser.getUserReqStatus() == Constants.MyFriend.STATUS_REJECTED) {
                        mUser.setUserReqStatus(Constants.MyFriend.FRIEND_STATUS_PENDING);
                        switch (mUser.getUserReqStatus()) {
                            case Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED:
                            case Constants.MyFriend.STATUS_REJECTED:
                                mBinding.ivSelection.setImageResource(R.drawable.plus);
                                break;
                            case Constants.MyFriend.FRIEND_STATUS_PENDING:
                                mBinding.ivSelection.setImageResource(R.drawable.ic_status_pending);
                                break;
                            case Constants.MyFriend.FRIEND_STATUS_CONNECTED:
                                mBinding.ivSelection.setImageResource(R.drawable.circle_tick_stroke);
                        }
                        ((AddFriendsActivity) mContext).sendFriendRequest(mUser.getUserId(), Constants.MyFriend.SEND_REQUEST);
                        return;
                    }
                }
                mBinding.setUser(mUser);
                break;
        }
    }
}
