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
import com.appster.turtle.adapter.viewholder.MyNotesViewHolder;
import com.appster.turtle.databinding.ItemNotesListingBinding;
import com.appster.turtle.model.NotesModel;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

/**
 * Created by rohantaneja on 26/09/17.
 * adapter for my notes
 */

public class MyNotesAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> implements MyNotesViewHolder.INotesClicked {

    Context mContext;
    List<NotesModel> mNotesList;
    private MyNotesViewHolder.INotesClicked mListener;

    public MyNotesAdapter(Context context, List<NotesModel> notesModelList, MyNotesViewHolder.INotesClicked listener) {
        mContext = context;
        mNotesList = notesModelList;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemNotesListingBinding notesListingBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_notes_listing, parent, false);
        return new MyNotesViewHolder(notesListingBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyNotesViewHolder) {
            ((MyNotesViewHolder) holder).bindData(mContext, mNotesList.get(position), position, this);
        }

    }

    @Override
    public int getItemCount() {
        return mNotesList != null ? mNotesList.size() : 0;
    }

    @Override
    public void onNoteClicked(int noteId, NotesModel model, int pos) {
        mListener.onNoteClicked(noteId, model, pos);
        mItemManger.closeAllItems();
    }

    @Override
    public void onDeleteClicked(int noteId, int pos) {
        mListener.onDeleteClicked(noteId, pos);
        mItemManger.closeAllItems();

    }

    @Override
    public void onMenuClicked(NotesModel notesModel, int pos) {
        mListener.onMenuClicked(notesModel, pos);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.sl_parent;
    }

    public void removeNote(int pos) {
        mNotesList.remove(pos);
        notifyDataSetChanged();
    }

}
