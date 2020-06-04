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
import com.appster.turtle.adapter.viewholder.NotificationViewHolder;
import com.appster.turtle.databinding.ItemNotificationBinding;
import com.appster.turtle.model.MessageMeta;
import com.appster.turtle.model.Notification;
import com.appster.turtle.model.Room;
import com.appster.turtle.model.UserProfileData;
import com.appster.turtle.util.ActivityMessage;
import com.appster.turtle.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 10/10/17.
 * adapter for notifiation
 */

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    private List<Notification> mNotificationsList;
    private OnItemClickListener mOnItemClickLister;

    public NotificationAdapter(Context mContext, List<Notification> mNotificationsList) {
        this.mContext = mContext;
        this.mNotificationsList = mNotificationsList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickLister = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemNotificationBinding mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_notification, parent, false);
        return new NotificationViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationViewHolder) {
            ((NotificationViewHolder) holder).setNotification(mContext, mNotificationsList.get(position), mOnItemClickLister);
            holder.itemView.setTag(position);
        }
    }

    public void update(int pos, Notification notification) {
        mNotificationsList.set(pos, notification);
        notifyDataSetChanged();
    }

    public void updateRoom(int pos, Room room) {
        try{
        Notification model = mNotificationsList.get(pos);
        List<Integer> array = new ArrayList<>();
        if(room!=null) {
            array.add(room.getUserReqStatus());
        }
        for (MessageMeta messageMeta : model.getMessageMeta()) {
            if (messageMeta.getType().equals(ActivityMessage.MessageType.STATUS)) {
                messageMeta.setData(array);

            }
        }
        notifyDataSetChanged();}catch (ArrayIndexOutOfBoundsException e){
            LogUtils.LOGD("ERROR",e.getMessage());
        }
    }

    public void updateUser(int pos, UserProfileData userProfileData) {
        Notification model = mNotificationsList.get(pos);
        List<Integer> array = new ArrayList<>();
        array.add(userProfileData.getFriendStatus());
        for (MessageMeta messageMeta : model.getMessageMeta()) {
            if (messageMeta.getType().equals(ActivityMessage.MessageType.STATUS)) {
                messageMeta.setData(array);

            }
        }
        notifyDataSetChanged();
    }


    public void updateTrue(int pos) {

        Notification set = mNotificationsList.get(pos);
        set.setIsRead("T");
        notifyItemChanged(pos);

    }

    public void removeItem(int pos) {
        mNotificationsList.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public int getItemCount() {
        return mNotificationsList != null ? mNotificationsList.size() : 0;
    }

    public interface OnItemClickListener {
        void onItemClicked(Notification notification, int pos);

        void onAcceptReject(Notification notification, int pos, int status);
    }
}
