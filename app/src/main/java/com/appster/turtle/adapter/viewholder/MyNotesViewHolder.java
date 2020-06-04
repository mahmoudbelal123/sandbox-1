
package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemNotesListingBinding;
import com.appster.turtle.model.NotesModel;
import com.daimajia.swipe.SwipeLayout;

/**
 * Created by rohantaneja on 26/09/17.
 * view holder of my notes
 */

public class MyNotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, SwipeLayout.SwipeListener {

    ItemNotesListingBinding mBinding;
    NotesModel mNotes;
    Context mContext;
    private INotesClicked mListener;
    private int mNotesPosition;

    public MyNotesViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        mBinding = DataBindingUtil.bind(viewDataBinding.getRoot());
    }

    public void bindData(Context context, NotesModel notesModel, int position, INotesClicked listener) {
        mListener = listener;
        mContext = context;
        mNotes = notesModel;
        mNotesPosition = position;
        mBinding.setNotes(notesModel);
        mBinding.tvNotesPrice.setText("$" + notesModel.getFormattedPrice());
        mBinding.tvNotesTitle.setOnClickListener(this);
        mBinding.clNotes.setOnClickListener(this);
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

    @Override
    public void onStartOpen(SwipeLayout layout) {
//
    }

    @Override
    public void onOpen(SwipeLayout layout) {
//
    }

    @Override
    public void onStartClose(SwipeLayout layout) {
//
    }

    @Override
    public void onClose(SwipeLayout layout) {
//
    }

    @Override
    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
//
    }

    @Override
    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
//
    }

    public interface INotesClicked {
        void onNoteClicked(int noteId, NotesModel model, int pos);

        void onDeleteClicked(int noteId, int position);

        void onMenuClicked(NotesModel notesModel, int pos);
    }
}
