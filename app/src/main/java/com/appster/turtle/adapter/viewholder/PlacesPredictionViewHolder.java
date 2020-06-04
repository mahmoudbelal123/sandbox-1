/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemPlacePredictionBinding;
import com.appster.turtle.ui.meetup.CreateMeetupActivity;
import com.google.android.gms.location.places.AutocompletePrediction;

/**
 * Created by rohan on 16/10/17.
 * view holder of place
 */

public class PlacesPredictionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private ItemPlacePredictionBinding mBinding;
    private AutocompletePrediction mPrediction;

    public PlacesPredictionViewHolder(Context context, ItemPlacePredictionBinding binding) {
        super(binding.getRoot());
        mContext = context;
        mBinding = binding;
    }

    public ItemPlacePredictionBinding getBinding() {
        return mBinding;
    }

    public void bindData(AutocompletePrediction prediction) {
        mPrediction = prediction;
        mBinding.tvPrediction.setText(prediction.getFullText(null));
        mBinding.getRoot().setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_prediction:
                ((CreateMeetupActivity) mContext).findPlaceById(mPrediction.getPlaceId(), mPrediction.getFullText(null).toString());
                break;

            default:
                break;
        }
    }
}
