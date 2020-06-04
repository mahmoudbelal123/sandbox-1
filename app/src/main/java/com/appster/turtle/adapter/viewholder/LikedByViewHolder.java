/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder;
/*
 * view holder of likedBy
 */
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import com.appster.turtle.databinding.ItemLikedByBinding;
import com.appster.turtle.model.User;

public class LikedByViewHolder extends RecyclerView.ViewHolder {

    private final ItemLikedByBinding viewDataBinding;

    public LikedByViewHolder(Context context, ItemLikedByBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        this.viewDataBinding = viewDataBinding;
    }

    public ViewDataBinding getBinding() {
        return viewDataBinding;
    }

    public void setUser(final User user) {

        viewDataBinding.setUser(user);
    }
}
