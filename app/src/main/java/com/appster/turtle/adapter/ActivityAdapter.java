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
import com.appster.turtle.adapter.viewholder.ActivityViewHolder;
import com.appster.turtle.databinding.ItemActivityBinding;
import com.appster.turtle.databinding.ItemActivityDateBinding;
import com.appster.turtle.model.Activities;
import com.appster.turtle.util.Utils;

import java.util.List;
/*
 * adapter for activity
 */
public class ActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Activities> mActivities;
    private final int DATE_VIEW_TYPE = 1;
    private final int ACTIVITY_VIEW_TYPE = 2;
    private OnItemClickListener mOnItemClickLister;

    public ActivityAdapter(Context mContext, List<Activities> mActivities) {
        this.mContext = mContext;
        this.mActivities = mActivities;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickLister = listener;
    }

    private boolean isDateEqual(int pos) {
        return Utils.getDOBFromMillis(mActivities.get(pos - 1).getCreatedAt()).equals(Utils.getDOBFromMillis(mActivities.get(pos).getCreatedAt()));
    }

    public void updateAll(List<Activities> mActivities) {
        this.mActivities.addAll(mActivities);
        notifyDataSetChanged();
    }

    public void update(List<Activities> mActivities) {
        this.mActivities.clear();
        this.mActivities.addAll(mActivities);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemActivityBinding itemActivityBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_activity, parent, false);
        return new ActivityViewHolder(itemActivityBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ActivityViewHolder) {
            ((ActivityViewHolder) holder).setActivity(mActivities.get(position), mOnItemClickLister);
            ItemActivityBinding binding = ((ActivityViewHolder) holder).getmBinding();

            if (position == 0 || !isDateEqual(position)) {
                binding.rlDateCont.removeAllViews();
                ItemActivityDateBinding itemDate = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_activity_date, binding.rlDateCont, false);
                itemDate.tvDate.setText(Utils.getDOBFromMillis(mActivities.get(position).getCreatedAt()));
                binding.rlDateCont.addView(itemDate.getRoot());

            } else {
                binding.rlDateCont.removeAllViews();

            }


            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mActivities.get(position).isDate() ? DATE_VIEW_TYPE : ACTIVITY_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(Activities activities, int pos);
    }
}
