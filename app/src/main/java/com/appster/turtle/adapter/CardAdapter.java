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
import com.appster.turtle.adapter.viewholder.CardViewHolder;
import com.appster.turtle.adapter.viewholder.EmptyViewHolder;
import com.appster.turtle.databinding.ItemCardBinding;
import com.appster.turtle.databinding.ItemCardEmptyBinding;
import com.appster.turtle.model.CardModel;

import java.util.ArrayList;
/*
 * adapter for card
 */
public class CardAdapter extends RecyclerView.Adapter implements CardViewHolder.ICardClicked {

    private final CardViewHolder.ICardClicked iCardClicked;
    private Context mContext;
    private ArrayList<CardModel> cardModels;
    private final static int TYPE_EMPTY = 2;

    public CardAdapter(Context context, ArrayList<CardModel> cardModels, CardViewHolder.ICardClicked iCardClicked) {
        mContext = context;
        this.cardModels = cardModels;
        this.iCardClicked = iCardClicked;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            ItemCardEmptyBinding cardBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_card_empty, parent, false);
            return new EmptyViewHolder(cardBinding);
        } else {
            ItemCardBinding cardBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_card, parent, false);
            return new CardViewHolder(cardBinding, CardAdapter.this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_EMPTY) {
            EmptyViewHolder viewHolder = (EmptyViewHolder) holder;
            viewHolder.setData(R.drawable.ic_credit_card, R.string.cards_empty_err);

        } else {
            CardViewHolder viewHolder = (CardViewHolder) holder;
            viewHolder.setData(cardModels.get(position));
        }
    }

    @Override
    public int getItemCount() {

        return cardModels.isEmpty() ? 1 : cardModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return cardModels.isEmpty() ? TYPE_EMPTY : super.getItemViewType(position);
    }

    @Override
    public void onCardDeleteClicked(CardModel pos) {
        iCardClicked.onCardDeleteClicked(pos);

    }


    @Override
    public void onCardClicked(CardModel card) {

        iCardClicked.onCardClicked(card);
    }
}
