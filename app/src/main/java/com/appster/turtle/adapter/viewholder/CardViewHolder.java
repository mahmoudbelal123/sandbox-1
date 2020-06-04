/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemCardBinding;
import com.appster.turtle.model.CardModel;

import static com.stripe.android.model.Card.BRAND_RESOURCE_MAP;
/*
 * view holder of card
 */
public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ItemCardBinding cardBinding;
    private ICardClicked cardClicked;
    private CardModel cardModel;
    int pos;


    public CardViewHolder(ItemCardBinding cardBinding, ICardClicked cardClicked) {
        super(cardBinding.getRoot());
        this.cardBinding = cardBinding;
        this.cardClicked = cardClicked;

        cardBinding.cvCard.setOnClickListener(this);
        cardBinding.ivCardMenu.setOnClickListener(this);
    }

    public void setData(CardModel cardModel) {
        this.cardModel = cardModel;

        cardBinding.setCard(cardModel);

        cardBinding.ivBrand.setImageResource(BRAND_RESOURCE_MAP.get(cardModel.getBrand()));


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_card_menu:

                cardClicked.onCardDeleteClicked(cardModel);
                break;
            case R.id.cv_card:
                cardClicked.onCardClicked(cardModel);
                break;
            default:
                break;
        }
    }


    public interface ICardClicked {
        void onCardDeleteClicked(CardModel card);

        void onCardClicked(CardModel card);
    }


}
