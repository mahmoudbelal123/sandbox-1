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
import com.appster.turtle.adapter.viewholder.SearchPeopleViewHolder;
import com.appster.turtle.adapter.viewholder.UsersViewHolder;
import com.appster.turtle.model.User;
import com.appster.turtle.ui.search.SearchRoomsAndPeopleActivity;

import java.util.List;

/**
 * Created by rohantaneja on 29-Aug-2017
 * adapter for user
 */

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<User> mUsersList;

    public UsersAdapter(Context mContext, List<User> mUsersList) {
        this.mContext = mContext;
        this.mUsersList = mUsersList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (mContext instanceof SearchRoomsAndPeopleActivity) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_search_people, parent, false);
            return new SearchPeopleViewHolder(v);
        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
            return new UsersViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UsersViewHolder) {
            ((UsersViewHolder) holder).bindData(mContext, mUsersList.get(position));


        } else if (holder instanceof SearchPeopleViewHolder) {
            ((SearchPeopleViewHolder) holder).bindData(mContext, mUsersList.get(position));
        }

    }

    public void setmUsersList(List<User> mUsersList) {
        int pos = this.mUsersList.size();
        this.mUsersList.addAll(mUsersList);
        notifyItemRangeInserted(pos, this.mUsersList.size());
    }

    @Override
    public int getItemCount() {
        return mUsersList != null ? mUsersList.size() : 0;
    }
}
