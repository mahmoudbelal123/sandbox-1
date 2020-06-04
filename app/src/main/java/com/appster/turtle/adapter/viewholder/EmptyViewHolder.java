package com.appster.turtle.adapter.viewholder;

import android.support.v7.widget.RecyclerView;

import com.appster.turtle.databinding.ItemCardEmptyBinding;
/*
 * view holder of empty
 */
@SuppressWarnings("ALL")
public class EmptyViewHolder extends RecyclerView.ViewHolder {


    ItemCardEmptyBinding cardBinding;


    public EmptyViewHolder(ItemCardEmptyBinding cardBinding) {
        super(cardBinding.getRoot());
        this.cardBinding = cardBinding;
    }

    public void setData(int imageRes, int stringRes) {
        cardBinding.ivEmpty.setImageResource(imageRes);
        cardBinding.tvEmpty.setText(stringRes);

    }

    public void setData(int imageRes, String text) {
        cardBinding.ivEmpty.setImageResource(imageRes);
        cardBinding.tvEmpty.setText(text);

    }
}
