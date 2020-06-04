/*
 *
 *   Copyright © 2017 Noise . All rights reserved.
 *   Created by Appster.
 *
 */
package com.appster.turtle.adapter.viewholder;

import android.support.v7.widget.RecyclerView;

import com.appster.turtle.databinding.ItemLoadMoreBinding;


/**
 * Created by atul on 20/03/17.
 * To inject activity reference.
 *
 * view holder of attachments
 */

public class LoadingViewHolder extends RecyclerView.ViewHolder {

    public ItemLoadMoreBinding getMoreBinding() {
        return moreBinding;
    }

    private final ItemLoadMoreBinding moreBinding;

    public LoadingViewHolder(ItemLoadMoreBinding loadMoreBinding) {
        super(loadMoreBinding.getRoot());
        moreBinding = loadMoreBinding;
    }

    public void setIndeterminate(boolean indeterminate) {
        moreBinding.progressBar.setIndeterminate(indeterminate);
    }
}
