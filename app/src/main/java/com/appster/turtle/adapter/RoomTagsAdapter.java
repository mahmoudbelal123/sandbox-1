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
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemRoomTagBinding;
import com.appster.turtle.model.Tag;
import com.appster.turtle.ui.media.AddCaptionActivity;
import com.appster.turtle.ui.post.CreatePostActivity;
import com.appster.turtle.ui.rooms.AddTagsActivity;
import com.appster.turtle.ui.rooms.RoomActivity;
import com.appster.turtle.ui.textpost.CreateTextPostActivity;

import java.util.List;

/**
 * Created by rohantaneja on 28-Aug-2017
 */

public class RoomTagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Tag> mTagsList;

    public RoomTagsAdapter(Context mContext, List<Tag> mTagsList) {
        this.mContext = mContext;
        this.mTagsList = mTagsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_room_tag, parent, false);
        return new RoomTagsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof RoomTagsViewHolder) {
            RoomTagsViewHolder viewHolder = (RoomTagsViewHolder) holder;
            viewHolder.getBinding().tvTag.setTag(mTagsList.get(position));
            viewHolder.getBinding().tvTag.setText(String.format("%s%s", mContext.getString(R.string.hash_prefix), mTagsList.get(position).getValue()));
        }

        holder.itemView.setTag(mTagsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTagsList != null ? mTagsList.size() : 0;
    }


    public class RoomTagsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemRoomTagBinding mBinding;

        public RoomTagsViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

            mBinding.tvTag.setOnClickListener(this);
        }

        public ItemRoomTagBinding getBinding() {
            return mBinding;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_tag:

                    if (mContext instanceof AddTagsActivity) {
                        ((AddTagsActivity) mContext).appendTag((Tag) view.getTag());
                    } else if (mContext instanceof CreateTextPostActivity) {
                        ((CreateTextPostActivity) mContext).appendTag((Tag) view.getTag());
                    } else if (mContext instanceof AddCaptionActivity) {
                        ((AddCaptionActivity) mContext).appendTag((Tag) view.getTag());
                    } else if (mContext instanceof RoomActivity) {
                        ((RoomActivity) mContext).appendTag((Tag) view.getTag());
                    } else if (mContext instanceof CreatePostActivity) {
                        ((CreatePostActivity) mContext).getTextPostFragment().appendTag((Tag) view.getTag());
                    }

                    break;

                default:
                    break;
            }
        }
    }

}
