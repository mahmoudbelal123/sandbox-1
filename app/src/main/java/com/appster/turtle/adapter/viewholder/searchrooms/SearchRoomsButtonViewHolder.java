/*
 *
 *   Copyright © 2017 Noise . All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder.searchrooms;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.databinding.ItemSearchRoomsButtonBinding;
import com.appster.turtle.model.SearchRoomsModel;

/**
 * Created by rohan on 01/11/17.
 * view holder for search room
 */

public class SearchRoomsButtonViewHolder extends RecyclerView.ViewHolder {

    ItemSearchRoomsButtonBinding mBinding;

    public SearchRoomsButtonViewHolder(View root) {
        super(root);

        mBinding = DataBindingUtil.bind(root);
    }

    public void bindData(SearchRoomsModel room, int position) {
        mBinding.tvViewMoreInvite.setText(room.getButtonText());
    }
}
