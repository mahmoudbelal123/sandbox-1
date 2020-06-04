package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.AttachmentsViewHolder;
import com.appster.turtle.databinding.ItemNotesAttachmentBinding;
import com.appster.turtle.model.Attachment;
import com.appster.turtle.ui.notes.editNotes.EditNotesActivity;
import com.appster.turtle.ui.notes.editNotes.EditNotesAttachmentsActivity;

import java.util.List;
/*
 * adapter for attachments
 */
public class AttachmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        AttachmentsViewHolder.IAttachmentDeleted, AttachmentsViewHolder.IAttachmentIdDeleted {

    private Context mContext;
    private List<Attachment> mAttachmentsList;
    private boolean mShowDeleteIcon;
    private AttachmentsViewHolder.IAttachmentDeleted mListener;
    private AttachmentsViewHolder.IAttachmentIdDeleted mAttachmentIdDeletedListener;

    public AttachmentsAdapter(Context context, List<Attachment> attachmentsList, AttachmentsViewHolder.IAttachmentDeleted listener, boolean showDeleteIcon) {
        mContext = context;
        mAttachmentsList = attachmentsList;
        mListener = listener;
        mShowDeleteIcon = showDeleteIcon;
    }

    public AttachmentsAdapter(Context context, List<Attachment> attachmentsList, AttachmentsViewHolder.IAttachmentDeleted listener) {
        mContext = context;
        mAttachmentsList = attachmentsList;
        mListener = listener;
    }

    public AttachmentsAdapter(Context context, List<Attachment> attachmentsList, AttachmentsViewHolder.IAttachmentIdDeleted listenerId, AttachmentsViewHolder.IAttachmentDeleted listener) {
        mContext = context;
        mAttachmentsList = attachmentsList;
        mListener = listener;
        mAttachmentIdDeletedListener = listenerId;
    }

    public AttachmentsAdapter(Context context, List<Attachment> attachmentsList) {
        mContext = context;
        mAttachmentsList = attachmentsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemNotesAttachmentBinding itemNotesAttachmentBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_notes_attachment, parent, false);
        if (mContext instanceof EditNotesAttachmentsActivity)
            return new AttachmentsViewHolder(itemNotesAttachmentBinding, this, this);

        return new AttachmentsViewHolder(itemNotesAttachmentBinding, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AttachmentsViewHolder) {

            if (mContext instanceof EditNotesActivity) {
                ((AttachmentsViewHolder) holder).bindData(mContext, mAttachmentsList.get(position), position, mShowDeleteIcon);
            } else if (mContext instanceof EditNotesAttachmentsActivity) {
                ((AttachmentsViewHolder) holder).bindData(mContext, mAttachmentsList.get(position), position);
            } else
                ((AttachmentsViewHolder) holder).bindData(mContext, mAttachmentsList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return mAttachmentsList != null ? mAttachmentsList.size() : 0;
    }

    @Override
    public void removeAttachment(int position) {
        mListener.removeAttachment(position);
    }

    @Override
    public void removeAttachmentWithId(int id) {
        mAttachmentIdDeletedListener.removeAttachmentWithId(id);
    }
}
