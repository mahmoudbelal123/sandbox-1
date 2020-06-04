/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.CameraBottomViewHolder;
import com.appster.turtle.adapter.viewholder.MediaBottomViewHolder;
import com.appster.turtle.databinding.ItemCameraBottomBinding;
import com.appster.turtle.databinding.ItemMediaBottomBinding;
import com.appster.turtle.ui.media.MediaBottomFragment;
import com.otaliastudios.cameraview.SessionType;

import java.util.ArrayList;
import java.util.List;
/*
 * adapter for media botton
 */
public class MediaBottomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final MediaBottomFragment fragment;
    Activity mContext;
    List<String> imageList;
    public static final int ITEM_VIEW_TYPE = 1;
    public static final int CAMERA_VIEW_TYPE = 2;


    public MediaBottomAdapter(Activity mContext, MediaBottomFragment fragment, ArrayList<String> imageList) {
        this.mContext = mContext;

        this.imageList = imageList;
        this.imageList.add(0, "");

        this.fragment = fragment;

    }

    public void update() {
        imageList.add(0, "");

        notifyDataSetChanged();
    }

    public void addAll() {
        if (imageList.size() > 1 && imageList.get(1).isEmpty())
            imageList.remove(1);

        notifyDataSetChanged();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE) {
            ItemMediaBottomBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_media_bottom, parent, false);
            return new MediaBottomViewHolder(mContext, binding);
        } else {
            ItemCameraBottomBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_camera_bottom, parent, false);
            viewHolder = new CameraBottomViewHolder(mContext, binding);
            return viewHolder;
        }


    }

    CameraBottomViewHolder viewHolder;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MediaBottomViewHolder) {

            if (fragment.getFirstItemPosition() > 3) {

                if (viewHolder != null && viewHolder.getBottomBinding().camera.isStarted()) {

                    viewHolder.getBottomBinding().camera.stop();
                }
            }
            if (imageList.get(position).isEmpty())
                return;

            ((MediaBottomViewHolder) holder).setImage(imageList.get(position));

        } else {

            if (!viewHolder.getBottomBinding().camera.isStarted()) {
                viewHolder.getBottomBinding().camera.start();
            } else {
                viewHolder.getBottomBinding().camera.setSessionType(SessionType.PICTURE);
            }
        }

        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {

        return imageList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return CAMERA_VIEW_TYPE;
        }
        return ITEM_VIEW_TYPE;
    }


}

