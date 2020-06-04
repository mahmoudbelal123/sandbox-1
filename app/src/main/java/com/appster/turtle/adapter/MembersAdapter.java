/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.MembersViewHolder;
import com.appster.turtle.model.User;
import com.appster.turtle.ui.BaseFragment;

import java.util.List;

/**
 * Created by rohantaneja on 29-Aug-2017
 *
 * adapter of member
 */

public class MembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private BaseFragment fragment;
    private Context mContext;
    private List<User> mMembersList;
    private MembersViewHolder.IMembers listener;

    public MembersAdapter(Context mContext, List<User> mMembersList, MembersViewHolder.IMembers listener) {
        this.mContext = mContext;
        this.mMembersList = mMembersList;
        this.listener = listener;
    }

    public MembersAdapter(Context mContext, List<User> mMembersList, BaseFragment listener) {
        this.mContext = mContext;
        this.mMembersList = mMembersList;
        this.listener = (MembersViewHolder.IMembers) listener;
        this.fragment = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_member_added, parent, false);
        return new MembersViewHolder(v, fragment);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MembersViewHolder) {
            ((MembersViewHolder) holder).bindData(mContext, mMembersList.get(position), listener);
        }
    }

    public void updated(User user, int pos) {
        mMembersList.set(pos, user);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMembersList != null ? mMembersList.size() : 0;
    }

}
