/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.SettingsViewHolder;

import java.util.List;
/*
 * adapter for settings
 */
public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SettingsViewHolder.IOnSettingSelected {

    private List<String> mSettingsList;
    private SettingsViewHolder.IOnSettingSelected mListener;

    public SettingsAdapter(List<String> settingsList, SettingsViewHolder.IOnSettingSelected listener) {
        mSettingsList = settingsList;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SettingsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_settings, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SettingsViewHolder) {
            ((SettingsViewHolder) holder).bindData(mSettingsList.get(position), position, this);
        }
    }

    @Override
    public int getItemCount() {
        return mSettingsList != null ? mSettingsList.size() : 0;
    }

    @Override
    public void settingSelected(int position) {
        mListener.settingSelected(position);
    }
}
