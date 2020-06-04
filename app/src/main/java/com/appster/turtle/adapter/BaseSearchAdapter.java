/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemSelectorBinding;
import com.appster.turtle.ui.BaseActivity;
import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohantaneja on 28-Aug-2017
 * base search adapter
 */

public class BaseSearchAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ItemSelected itemSelected;
    private Context mContext;
    private List<T> data = new ArrayList<>();
    private ArrayList selectedData = new ArrayList<>();
    private int maxSelection;
    private boolean firstTime = true;

    public BaseSearchAdapter(Context mContext, ItemSelected itemSelected, int maxSelection) {
        this.mContext = mContext;
        this.itemSelected = itemSelected;
        this.maxSelection = maxSelection;
    }


    public void addAll(List<T> data) {


        if (selectedData.size() > 0) {
            for (T t : data) {
                if (!selectedData.contains(t)) {
                    this.data.add(t);
                }
            }
        } else
            this.data.addAll(data);

        notifyDataSetChanged();
    }

    public void addAll(boolean clearList, List<T> data) {
        if (clearList) {
            this.data.clear();
        }

        if (selectedData.size() > 0)
            this.data.addAll(0, selectedData);

        addAll(data);


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_selector, parent, false);
        return new RoomTagsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        RoomTagsViewHolder viewHolder = (RoomTagsViewHolder) holder;
        viewHolder.setData(data.get(position));
        holder.itemView.setTag(data.get(position));
    }

    public ArrayList getSelectedData() {
        return selectedData;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setSelectedData(ArrayList<Parcelable> selectedData) {
        if (firstTime) {
            this.selectedData.clear();
            this.selectedData.addAll(selectedData);
            firstTime = false;
        }

        if (selectedData.size() > 0)
            itemSelected.itemSelected(true);
        else
            itemSelected.itemSelected(false);

    }


    public class RoomTagsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemSelectorBinding mBinding;

        public RoomTagsViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            mBinding.tvTag.setOnClickListener(this);
        }

        private void setData(T data) {

            if (data instanceof AutocompletePrediction) {
                mBinding.tvTag.setText(((AutocompletePrediction) data).getFullText(null));

            } else {
                mBinding.tvTag.setText(data.toString());

                if (selectedData.contains((data)))
                    mBinding.tvTag.setTextColor(ContextCompat.getColor(mContext, R.color.bright_teal));
                else
                    mBinding.tvTag.setTextColor(ContextCompat.getColor(mContext, R.color.msg_black));

            }

        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_tag:

                    T item = (T) view.getTag();

                    if (maxSelection == 1) {
                        if (selectedData.contains((item))) {
                            selectedData.remove((item));
                        }
                        ArrayList items = new ArrayList();
                        items.add(item);
                        itemSelected.itemSelectionDone(items);
                        return;
                    }

                    if (selectedData.contains((item))) {
                        selectedData.remove((item));
                        mBinding.tvTag.setTextColor(ContextCompat.getColor(mContext, R.color.msg_black));
                    } else {

                        if (maxSelection > selectedData.size()) {

                            selectedData.add((item));
                            mBinding.tvTag.setTextColor(ContextCompat.getColor(mContext, R.color.bright_teal));
                        } else {
                            ((BaseActivity) mContext).showSnackBar(mContext.getString(R.string.max_2));
                        }


                    }


                    if (selectedData.size() > 0)
                        itemSelected.itemSelected(true);
                    else
                        itemSelected.itemSelected(false);
                    break;

                default:
                    break;
            }
        }
    }


    public interface ItemSelected<T> {

        void itemSelected(boolean isDone);

        void itemSelectionDone(ArrayList<T> item);


    }

}
