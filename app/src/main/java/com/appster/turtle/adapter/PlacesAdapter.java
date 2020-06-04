package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemPlaceBinding;
import com.appster.turtle.ui.meetup.CreateMeetUpFragment;
import com.google.android.gms.location.places.AutocompletePrediction;

import java.util.Collections;
import java.util.List;

/**
 * Created by Dev on 06/03/18.
 * adapter for place api
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {
    private List<AutocompletePrediction> predictions = Collections.emptyList();
    private Context context;
    private CreateMeetUpFragment.OnPlaceSelected onPlaceSelected;

    public PlacesAdapter(List<AutocompletePrediction> predictions, Context context, CreateMeetUpFragment.OnPlaceSelected onPlaceSelected) {
        this.predictions = predictions;
        this.context = context;
        this.onPlaceSelected = onPlaceSelected;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaceViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_place, parent, false)
                        .getRoot());
    }

    @Override
    public void onBindViewHolder(final PlaceViewHolder holder, int position) {
        holder.binding.tvPlace.setText(predictions.get(holder.getAdapterPosition()).getFullText(null));
        holder.itemView.setOnClickListener(view -> onPlaceSelected.placeSelected(predictions.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        ItemPlaceBinding binding;

        PlaceViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
