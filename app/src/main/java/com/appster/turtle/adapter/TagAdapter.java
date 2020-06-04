package com.appster.turtle.adapter;

/*
  Copyright © 2017 Noise. All rights reserved.
  Created by navdeepbedi on 27/02/18.
  adapter for tag
 */

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemTagBinding;
import com.appster.turtle.model.Interest;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isSelected;
    private List<Interest> mInterestList;
    private OnInterestSelected mListener;

    public TagAdapter(List<Interest> mInterestList, OnInterestSelected mListener) {
        this.mInterestList = mInterestList;
        this.mListener = mListener;
    }

    public TagAdapter(List<Interest> mInterestList, OnInterestSelected mListener, boolean isSelected) {
        this.mInterestList = mInterestList;
        this.mListener = mListener;
        this.isSelected = isSelected;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TagHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TagHolder) {
            ((TagHolder) holder).bindData(mInterestList.get(position));
            ((TagHolder) holder).getmBinding().rlBg.setOnClickListener(view -> mListener.onInterestSelected(holder.getAdapterPosition()));

        }
    }

    @Override
    public int getItemCount() {
        return mInterestList.size();
    }


    private class TagHolder extends RecyclerView.ViewHolder {

        private final ItemTagBinding mBinding;

        public TagHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mBinding = DataBindingUtil.bind(binding.getRoot());

        }

        public ItemTagBinding getmBinding() {
            return mBinding;
        }

        public void bindData(final Interest interest) {
            mBinding.tvTag.setText(interest.getValue());
            mBinding.tvTag.setTextColor(ContextCompat.getColor(mBinding.tvTag.getContext(), interest.isSelected() ? R.color.app_white : R.color.bright_teal));

            mBinding.rlBg.setBackgroundResource(interest.isSelected() ? R.drawable.rounded_bg_light : R.drawable.orange_btn_stroke);


            mBinding.iv.setImageResource(isSelected ? R.drawable.ic_cross_white_small : interest.isSelected() ? R.drawable.ic_tick_white : R.drawable.ic_combined_shape);


        }
    }

    public interface OnInterestSelected {

        void onInterestSelected(int interest);
    }
}
