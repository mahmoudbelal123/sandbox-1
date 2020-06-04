/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;
/*
 * adapter for notes
 */
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.LoadingViewHolder;
import com.appster.turtle.adapter.viewholder.NotesViewHolder;
import com.appster.turtle.databinding.ItemLoadMoreBinding;
import com.appster.turtle.databinding.ItemNotesListingBinding;
import com.appster.turtle.model.NotesModel;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NotesViewHolder.INotesClicked {

    private Context mContext;
    private List<NotesModel> mNotesList;
    private NotesViewHolder.INotesClicked mListener;
    private boolean mLoading = true;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public NotesAdapter(Context context, List<NotesModel> notesModelList, NotesViewHolder.INotesClicked listener) {
        mContext = context;
        mNotesList = notesModelList;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            ItemNotesListingBinding notesListingBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_notes_listing, parent, false);

            return new NotesViewHolder(notesListingBinding);
        } else if (viewType == VIEW_TYPE_LOADING) {
            ItemLoadMoreBinding itemLoadMoreBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_load_more, parent, false);
            return new LoadingViewHolder(itemLoadMoreBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotesViewHolder) {
            ((NotesViewHolder) holder).bindData(mContext, mNotesList.get(position), position, this);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.getMoreBinding().progressBar.setIndeterminate(true);
            loadingViewHolder.getMoreBinding().progressBar.setVisibility(mLoading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mNotesList == null ? 0 : mNotesList.size() + 1;
    }

    @Override
    public void onNoteClicked(int noteId, NotesModel model, int pos) {
        mListener.onNoteClicked(noteId, model, pos);
    }

    @Override
    public void onMenuClicked(NotesModel notesModel, int pos) {
        mListener.onMenuClicked(notesModel, pos);
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1;
    }

    public void setLoading(boolean loading) {
        this.mLoading = loading;
    }

    public void removeNote(int pos) {
        mNotesList.remove(pos);
        notifyDataSetChanged();
    }

    public void updateNote(int pos, NotesModel notes) {
        mNotesList.set(pos, notes);
        notifyDataSetChanged();
    }

    public void addNote(NotesModel notesModel) {
        mNotesList.add(0, notesModel);
        notifyDataSetChanged();
    }
}
