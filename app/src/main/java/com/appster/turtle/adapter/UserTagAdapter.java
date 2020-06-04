/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemUserTagBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.ui.media.AddCaptionActivity;
import com.appster.turtle.ui.post.CreateTextPostFragment;
import com.appster.turtle.ui.post.PostDetailActivity;

import java.util.List;

/**
 * Created by rohantaneja on 28-Aug-2017
 * adapter for user tag
 */

public class UserTagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Fragment fragment;
    private Context mContext;
    private List<User> mTagsList;

    public UserTagAdapter(Context mContext, List<User> mTagsList) {
        this.mContext = mContext;
        this.mTagsList = mTagsList;
    }

    public UserTagAdapter(Fragment fragment, List<User> mTagsList) {
        this.fragment = fragment;
        this.mContext = fragment.getActivity();
        this.mTagsList = mTagsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_user_tag, parent, false);
        return new RoomTagsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof RoomTagsViewHolder) {
            RoomTagsViewHolder viewHolder = (RoomTagsViewHolder) holder;

            viewHolder.getBinding().setUser(mTagsList.get(position));
            viewHolder.getBinding().tvTag.setTag(mTagsList.get(position));
            viewHolder.getBinding().tvTag.setText(String.format("%s%s", mContext.getString(R.string.at_prefix), mTagsList.get(position).getUserName()));
        }

        holder.itemView.setTag(mTagsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTagsList != null ? mTagsList.size() : 0;
    }


    public class RoomTagsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemUserTagBinding mBinding;

        public RoomTagsViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            mBinding.tvTag.setOnClickListener(this);
        }

        public ItemUserTagBinding getBinding() {
            return mBinding;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_tag:

                    if (mContext instanceof PostDetailActivity) {
                        ((PostDetailActivity) mContext).appendTag((User) view.getTag());
                    } else if (mContext instanceof AddCaptionActivity) {
                        ((AddCaptionActivity) mContext).appendTag((User) view.getTag());
                    } else if (fragment instanceof CreateTextPostFragment) {
                        ((CreateTextPostFragment) fragment).appendTag((User) view.getTag());
                    }

                    break;

                default:
                    break;
            }
        }
    }

}
