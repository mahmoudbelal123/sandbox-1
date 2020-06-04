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
import com.appster.turtle.adapter.viewholder.NotesReviewViewHolder;
import com.appster.turtle.databinding.ItemNotesReviewBinding;
import com.appster.turtle.network.response.NotesReview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohantaneja on 26/09/17.
 * adapter for review notes
 */

public class NotesReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<NotesReview> mReviewList = new ArrayList<>();

    public NotesReviewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemNotesReviewBinding notesListingBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_notes_review, parent, false);
        return new NotesReviewViewHolder(notesListingBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotesReviewViewHolder) {
            ((NotesReviewViewHolder) holder).bindData(mContext, mReviewList.get(position));

            ((NotesReviewViewHolder) holder).getmBinding().ratingBar.setRating(mReviewList.get(position).getRating());
        }


    }

    @Override
    public int getItemCount() {
        return mReviewList != null ? mReviewList.size() : 0;
    }


    public void addAll(List<NotesReview> data) {
        mReviewList.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        mReviewList.clear();
        notifyDataSetChanged();
    }
}
