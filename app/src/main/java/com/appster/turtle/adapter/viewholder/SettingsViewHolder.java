package com.appster.turtle.adapter.viewholder;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemSettingsBinding;
/*
 * view holder of setting
 */
public class SettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private IOnSettingSelected mListener;
    private ItemSettingsBinding mBinding;
    private int pos;

    public SettingsViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        mBinding = DataBindingUtil.bind(binding.getRoot());
    }

    public void bindData(String settingsTitle, int position, IOnSettingSelected listener) {
        mListener = listener;
        pos = position;

        mBinding.tvSettingsTitle.setText(settingsTitle);
        mBinding.clSettingsItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cl_settings_item:
                mListener.settingSelected(pos);
                break;
            default:
                break;
        }
    }

    public interface IOnSettingSelected {
        void settingSelected(int position);
    }
}
