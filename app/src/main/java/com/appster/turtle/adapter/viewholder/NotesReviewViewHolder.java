package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import com.appster.turtle.databinding.ItemNotesReviewBinding;
import com.appster.turtle.network.response.NotesReview;

/**
 * Created by rohantaneja on 26/09/17.
 * view holder on review notes
 */

public class NotesReviewViewHolder extends RecyclerView.ViewHolder {

    public ItemNotesReviewBinding getmBinding() {
        return mBinding;
    }

    ItemNotesReviewBinding mBinding;
    Context mContext;

    public NotesReviewViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        mBinding = DataBindingUtil.bind(viewDataBinding.getRoot());
    }

    public void bindData(Context context, NotesReview notesModel) {
        mContext = context;
        mBinding.setReview(notesModel);


    }


}
