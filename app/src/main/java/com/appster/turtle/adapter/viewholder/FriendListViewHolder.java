package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemFriendsBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.util.CustomTypefaceSpan;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 16/12/17.
 *
 * view holder of friends
 */

public class FriendListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ItemFriendsBinding mBinding;
    private IFriendListener listener;
    private User user;
    private int position;

    public FriendListViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        mBinding = DataBindingUtil.bind(viewDataBinding.getRoot());
    }

    public void bindData(Context context, User user, int section, int position, IFriendListener listener) {
        this.listener = listener;
        this.position = position;
        this.user = user;
        mBinding.setUser(user);

        if (section == 0) {
            mBinding.tvUniversity.setText(user.getUniversityBaseModel().getDisplayName());
            mBinding.tvUsername.setVisibility(View.GONE);
            mBinding.ibAccept.setVisibility(View.GONE);
            mBinding.ibReject.setVisibility(View.GONE);

            setName();
        } else {
            mBinding.tvUniversity.setText(user.getMutualFriendCount() + " " + context.getString(R.string.mutual_friends));
            mBinding.tvUsername.setVisibility(View.GONE);
            mBinding.tvName.setText(user.getFullName());
            mBinding.ibAccept.setVisibility(View.VISIBLE);
            mBinding.ibReject.setVisibility(View.VISIBLE);
        }

        mBinding.tvUniversity.setOnClickListener(this);
        mBinding.tvUsername.setOnClickListener(this);
        mBinding.tvName.setOnClickListener(this);
        mBinding.ibAccept.setOnClickListener(this);
        mBinding.ibReject.setOnClickListener(this);


    }

    private void setName() {

        SpannableString string = new SpannableString(user.getFullName() + "@" + user.getUserName());

        Typeface semiBold = Typeface.createFromAsset(mBinding.tvName.getContext().getAssets(), mBinding.tvName.getContext().getString(R.string.eina_01_semibold));
        Typeface regular = Typeface.createFromAsset(mBinding.tvName.getContext().getAssets(), mBinding.tvName.getContext().getString(R.string.eina_01_regular));

        string.setSpan(new CustomTypefaceSpan(" ", semiBold)
                , 0, user.getFullName().length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mBinding.tvName.getContext(), R.color.done_unselected_color)),
                0, user.getFullName().length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        string.setSpan(new CustomTypefaceSpan(" ", regular)
                , user.getFullName().length(), (user.getFullName() + "@" + user.getUserName()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mBinding.tvName.getContext(), R.color.text_font_color_grey)),
                user.getFullName().length(), (user.getFullName() + "@" + user.getUserName()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mBinding.tvName.setText(string);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_name:

                listener.onProfileClicked(user.getUserId());
                break;
            case R.id.tv_username:
                listener.onProfileClicked(user.getUserId());

                break;
            case R.id.tv_university:
                listener.onMutualFriendsClicked(user.getUserId());

                break;
            case R.id.ib_accept:
                listener.onAcceptClicked(user.getUserId(), position);
                break;
            case R.id.ib_reject:
                listener.onRejectClicked(user.getUserId(), position);

                break;
            default:
                break;

        }

    }


    public interface IFriendListener {
        void onAcceptClicked(int userId, int pos);

        void onRejectClicked(int userId, int pos);

        void onProfileClicked(int userId);

        void onMutualFriendsClicked(int userId);
    }
}
