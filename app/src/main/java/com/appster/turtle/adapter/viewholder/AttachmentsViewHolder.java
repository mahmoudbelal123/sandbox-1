package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemNotesAttachmentBinding;
import com.appster.turtle.model.Attachment;
import com.appster.turtle.ui.notes.NotesFinalActivity;
import com.appster.turtle.ui.notes.NotesPurchaseActivity;
/*
 * view holder of attachments
 */
public class AttachmentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ItemNotesAttachmentBinding mBinding;
    private Attachment mAttachment;
    private int mAttachmentPosition;
    private IAttachmentDeleted mListener;
    private IAttachmentIdDeleted mListenerId;

    public AttachmentsViewHolder(ItemNotesAttachmentBinding binding, IAttachmentDeleted listener) {
        super(binding.getRoot());
        mBinding = binding;
        mListener = listener;
    }

    public AttachmentsViewHolder(ItemNotesAttachmentBinding binding, IAttachmentDeleted listener, IAttachmentIdDeleted listenerId) {
        super(binding.getRoot());
        mBinding = binding;
        mListener = listener;
        mListenerId = listenerId;
    }

    public void bindData(Context mContext, Attachment mAttachment, int position, boolean showDeleteIcon) {

        if (!showDeleteIcon) {
            mBinding.ivAttachmentDelete.setVisibility(View.GONE);
        }

        bindData(mContext, mAttachment, position);
    }


    public void bindData(Context mContext, Attachment attachment, int position) {

        mAttachmentPosition = position;
        mAttachment = attachment;

        if (mContext instanceof NotesFinalActivity || mContext instanceof NotesPurchaseActivity) {
            mBinding.ivAttachmentDelete.setVisibility(View.GONE);
        }
        mBinding.tvAttachmentTitle.setText(mAttachment.getFileName());
        mBinding.ivAttachmentDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_attachment_delete:
                if (mListener != null)
                    mListener.removeAttachment(mAttachmentPosition);
                if (mListenerId != null)
                    mListenerId.removeAttachmentWithId(mAttachment.getId());
                break;
            default:
                break;
        }
    }

    public interface IAttachmentDeleted {
        void removeAttachment(int position);
    }

    public interface IAttachmentIdDeleted {
        void removeAttachmentWithId(int id);
    }
}
