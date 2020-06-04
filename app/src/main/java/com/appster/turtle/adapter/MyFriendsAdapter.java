/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.FriendListViewHolder;
import com.appster.turtle.databinding.ItemFriendsBinding;
import com.appster.turtle.model.User;

import java.util.List;

/**
 * Created by rohantaneja on 26/09/17.
 * adapter for my friend
 */

public class MyFriendsAdapter extends RecyclerView.Adapter implements FriendListViewHolder.IFriendListener {

    private final int section;
    Context mContext;
    List<User> mFriendsList;
    private FriendListViewHolder.IFriendListener mListener;

    public MyFriendsAdapter(Context context, int section, List<User> mFriendsList, FriendListViewHolder.IFriendListener listener) {
        mContext = context;
        this.mFriendsList = mFriendsList;
        mListener = listener;
        this.section = section;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFriendsBinding itemFriendsBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_friends, parent, false);
        return new FriendListViewHolder(itemFriendsBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FriendListViewHolder) {
            ((FriendListViewHolder) holder).bindData(mContext, mFriendsList.get(position), section, position, MyFriendsAdapter.this);
        }

    }

    @Override
    public int getItemCount() {
        return mFriendsList != null ? mFriendsList.size() : 0;
    }


    @Override
    public void onAcceptClicked(int userId, int pos) {
        mListener.onAcceptClicked(userId, pos);
    }

    @Override
    public void onRejectClicked(int userId, int pos) {
        mListener.onRejectClicked(userId, pos);

    }

    @Override
    public void onProfileClicked(int userId) {
        mListener.onProfileClicked(userId);

    }

    @Override
    public void onMutualFriendsClicked(int userId) {
        mListener.onMutualFriendsClicked(userId);


    }
}
