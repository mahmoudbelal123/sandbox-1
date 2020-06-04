/*
 *
 *   Copyright © 2017 Noise . All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder;
/*
 * view holder of notes
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemNotesListingBinding;
import com.appster.turtle.model.NotesModel;

public class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private NotesModel mNotes;
    private INotesClicked mListener;
    private int mNotesPosition;

    private final ItemNotesListingBinding mBinding;


    public NotesViewHolder(ItemNotesListingBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        mBinding = viewDataBinding;
    }

    public void bindData(Context context, NotesModel notesModel, int position, INotesClicked listener) {
        mListener = listener;
        mNotes = notesModel;
        mNotesPosition = position;

        mBinding.setNotes(notesModel);

        mBinding.tvNotesPrice.setText(String.format(context.getString(R.string.dollar_prefix), notesModel.getFormattedPrice()));
        mBinding.getRoot().setOnClickListener(this);
        mBinding.ivMenu.setOnClickListener(this);

        mBinding.tvRating.setText(String.valueOf(notesModel.getAvgRating()));

        if (notesModel.getAvgRating() > 0) {
            mBinding.tvRating.setVisibility(View.VISIBLE);
            mBinding.ratingBar.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvRating.setVisibility(View.INVISIBLE);
            mBinding.ratingBar.setVisibility(View.INVISIBLE);


        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cl_notes:

                mListener.onNoteClicked(mNotes.getId(), mNotes, getAdapterPosition());
                break;
            case R.id.iv_menu:
                mListener.onMenuClicked(mNotes, mNotesPosition);
                break;
            default:
                break;
        }
    }

    public interface INotesClicked {
        void onNoteClicked(int noteId, NotesModel notesModel, int pos);

        void onMenuClicked(NotesModel notesModel, int pos);
    }
}
