/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemSearchBinding;
import com.appster.turtle.model.SearchRoomsModel;
import com.appster.turtle.ui.Constants;

/**
 * Created by rohan on 01/11/17.
 * view holder of search item
 */

public class SearchItemViewHolder extends RecyclerView.ViewHolder {

    private ItemSearchBinding mBinding;

    public SearchItemViewHolder(View root) {
        super(root);

        mBinding = DataBindingUtil.bind(root);
    }

    public void bindData(SearchRoomsModel room) {

        mBinding.tvName.setText(room.getRoomName());

        //for searched rooms

        switch (room.getUserReqStatus()) {
            case Constants.UserAddedToRoomStatus.ADDED:
                mBinding.ivAdd.setImageResource(R.drawable.ic_join_white);
                break;

            case Constants.UserAddedToRoomStatus.PENDING:
                mBinding.ivAdd.setImageResource(R.drawable.pending);
                break;

            default:
                mBinding.ivAdd.setImageResource(R.drawable.add_white);
                break;
        }

    }
}
