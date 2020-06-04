package com.appster.turtle.adapter.viewholder;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.appster.turtle.databinding.ItemCommentBinding;
import com.appster.turtle.model.Comment;
import com.appster.turtle.model.User;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.CommentListener;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.util.ExplicitIntent;

/*
 * view holder of comment
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private ItemCommentBinding itemCommentBinding;
    private CommentListener listener;

    public CommentViewHolder(ItemCommentBinding itemCommentBinding, CommentListener listener) {
        super(itemCommentBinding.getRoot());
        this.itemCommentBinding = itemCommentBinding;
        this.listener = listener;

        itemCommentBinding.tvText.setHashtagEnabled(false);
        itemCommentBinding.tvText.setMentionEnabled(true);

    }

    public void bindData(final Comment comment) {
        itemCommentBinding.setComment(comment);

        itemCommentBinding.ivLike.setOnClickListener(view -> listener.onCommentLiked(getAdapterPosition(), comment.getCommentId(), comment.getReaction() == 0 ? 1 : 0));

        itemCommentBinding.ivMenu.setOnClickListener(view -> listener.onCommentMenuClicked(comment, getAdapterPosition()));

        itemCommentBinding.tvLikeCount.setOnClickListener(view -> {

            if (comment.getTotalLikesCount() > 0)
                listener.onCommentLikedBy(getAdapterPosition(), comment.getCommentId(), 0);
        });

        itemCommentBinding.tvText.setOnMentionClickListener((textView, s) -> {

            int userId = 0;
            for (User user : comment.getTaggedUsers()) {
                if (user != null && user.getUserName().equalsIgnoreCase(s)) {
                    userId = user.getUserId();
                    break;
                }
                //something here
            }

            if (userId == 0)
                return null;

            Bundle i = new Bundle();
            i.putInt(Constants.USER_ID, userId);
            ExplicitIntent.getsInstance().navigateTo((BaseActivity) textView.getContext(), UserProfileActivity.class, i);


            return null;
        });

        itemCommentBinding.tvText.setTagList(comment.getTaggedUsers());
    }


}
