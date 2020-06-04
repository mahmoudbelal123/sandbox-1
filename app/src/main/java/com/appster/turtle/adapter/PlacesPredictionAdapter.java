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
import com.appster.turtle.adapter.viewholder.PlacesPredictionViewHolder;
import com.appster.turtle.databinding.ItemPlacePredictionBinding;
import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.ArrayList;

/**
 * Created by rohan on 16/10/17.
 * adater for place predications
 */

public class PlacesPredictionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<AutocompletePrediction> mPlacesPredictionList;

    public PlacesPredictionAdapter(Context context, ArrayList<AutocompletePrediction> placesPredictionsList) {
        mContext = context;
        mPlacesPredictionList = placesPredictionsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPlacePredictionBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_place_prediction, parent, false);
        return new PlacesPredictionViewHolder(mContext, binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PlacesPredictionViewHolder) holder).bindData(mPlacesPredictionList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPlacesPredictionList != null ? mPlacesPredictionList.size() : 0;
    }
}
