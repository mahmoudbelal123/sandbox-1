/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemPollBinding;
import com.appster.turtle.model.Tag;
import com.appster.turtle.network.request.PollAnswer;
import com.appster.turtle.network.response.PostDetail;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;
import com.appster.turtle.widget.CustomTextView;

import java.util.List;

/**
 * Created by atul on 05/09/17.
 * To inject activity reference.
 * view holder of poll view
 */

public class PollViewHolder extends RecyclerView.ViewHolder {

    private final PostListener postListener;
    private ItemPollBinding pollBinding;
    private PostListener listener;
    private Resources resources;
    private Context context;

    private String query = "";
    private boolean isViewForComment;

    public boolean isViewForComment() {
        return isViewForComment;
    }

    public void setViewForComment(boolean viewForComment) {
        isViewForComment = viewForComment;
    }


    public void setOnClickListener(PostListener listener) {
        this.listener = listener;
    }

    public PollViewHolder(ViewDataBinding viewDataBinding, PostListener postListener) {
        super(viewDataBinding.getRoot());
        pollBinding = (ItemPollBinding) viewDataBinding;
        this.postListener = postListener;
    }

    public ItemPollBinding getPollBinding() {
        return pollBinding;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void bindData(final Posts post) {
        pollBinding.setPost(post);
        pollBinding.setUser(post.getUsersListModel());
        pollBinding.clReplyCommentLike.setPost(post);
        final PostDetail postDetail = post.getPostDetail();
        final List<PollAnswer> pollAnswers = postDetail.getPollAnswersList();
        resources = itemView.getResources();
        pollBinding.optAnswerViewContainer.removeAllViews();
        context = itemView.getContext();

        if (post.getPostDetail().isChoiceSelected()) {
            if (pollAnswers.size() < 3) {
                pollBinding.tvView.setVisibility(View.GONE);
            } else {
                post.getPostDetail().setShown(true);
                pollBinding.tvView.setText(context.getText(R.string.show_less));
            }
        } else {
            if (pollAnswers.size() < 3) {
                pollBinding.tvView.setVisibility(View.GONE);
            } else {
                if (post.getPostDetail().isShown()) {
                    pollBinding.tvView.setText(context.getText(R.string.show_less));
                } else {
                    pollBinding.tvView.setText(context.getText(R.string.view_all));
                }
            }
        }

        pollBinding.tvRoom.setOnClickListener(view -> {


            Bundle bundle = new Bundle();

            bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
            bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);

            ExplicitIntent.getsInstance().navigateTo((BaseActivity) view.getContext(), RoomActivityCoordinator.class, bundle);


        });

        pollBinding.getRoot().setOnClickListener(view -> listener.onClick(post));

        pollBinding.clReplyCommentLike.ivComment.setOnClickListener(view -> postListener.onCommentClicked(post));

        pollBinding.clReplyCommentLike.tvCommentCount.setOnClickListener(view -> postListener.onCommentClicked(post));

        setAnswers(post, getAdapterPosition(), pollAnswers, false);

        pollBinding.tvView.setOnClickListener(v -> {
            if (post.getPostDetail().isShown()) {
                if (pollBinding.optAnswerViewContainer.getChildCount() > 0)
                    pollBinding.optAnswerViewContainer.removeAllViews();
                post.getPostDetail().setShown(false);
                setAnswers(post, getAdapterPosition(), pollAnswers, false);

                if (pollAnswers.size() < 3) {
                    pollBinding.tvView.setVisibility(View.GONE);
                } else {
                    pollBinding.tvView.setText(context.getText(R.string.view_all));
                    if (isViewForComment()) {
                        postDetail.setChoiceSelected(false);
                    }
                }

            } else {
                post.getPostDetail().setShown(true);
                setAnswers(post, getAdapterPosition(), pollAnswers, true);
                if (pollBinding.optAnswerViewContainer.getChildCount() == pollAnswers.size()) {
                    pollBinding.tvView.setText(context.getText(R.string.show_less));
                    if (isViewForComment()) {
                        postDetail.setChoiceSelected(true);
                    }

                }
                pollBinding.tvView.setText(context.getText(R.string.show_less));

            }
        });
        List<Tag> tagList = post.getTags();


        StringBuilder tagName = new StringBuilder();
        if (post.getPostDetail().getTotResponse() != 0) {
            tagName = new StringBuilder(context.getResources().getQuantityString(R.plurals.votes, post.getPostDetail().getTotResponse(), post.getPostDetail().getTotResponse()) + " ");
        }
        if (tagList.size() > 0) {
            for (int index = 0; index < (tagList.size() > 2 ? 2 : tagList.size()); index++) {
                tagName.append(resources.getString(R.string.hash)).append(tagList.get(index).getValue()).append(resources.getString(R.string.space));
            }

        }
        // pollBinding.tvPollCount.setText(tagName);
        StringUtils.setOnClickInTag(pollBinding.tvPollCount, tagName.toString(), getAdapterPosition(), post, postListener);


        pollBinding.ivMenu.setOnClickListener(view -> postListener.onMenuClicked(post, getAdapterPosition()));


    }


    private void setAnswers(final Posts post, final int pos, final List<PollAnswer> pollAnswers, boolean isNewAdded) {

        if (post.getPostDetail().isChoiceSelected()) {
            if (pollBinding.optAnswerViewContainer.getChildCount() > 0)
                pollBinding.optAnswerViewContainer.removeAllViews();
            if (!post.getPostDetail().isShown()) {
                for (int index = 0; index < 2; index++) {
                    setData(index, post, pos, pollAnswers);
                }
            } else {
                for (int index = 0; index < pollAnswers.size(); index++) {
                    setData(index, post, pos, pollAnswers);
                }
            }
        } else {
            if (post.getPostDetail().isShown()) {
                if (isNewAdded) {
                    for (int index = 2; index < pollAnswers.size(); index++) {
                        setData(index, post, pos, pollAnswers);
                    }
                } else {
                    for (int index = 0; index < pollAnswers.size(); index++) {
                        setData(index, post, pos, pollAnswers);
                    }
                }
            } else {
                for (int index = isNewAdded ? 2 : 0; index < (isNewAdded ? pollAnswers.size() : 2); index++) {
                    setData(index, post, pos, pollAnswers);
                }
            }
        }
    }

    private void setData(int index, final Posts post, final int pos, final List<PollAnswer> pollAnswers) {

        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_tv_poll, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, Utils.dpToPx(context, resources.getDimension(R.dimen.margin_small)));
        layout.setLayoutParams(lp);
        final CustomTextView ansText = layout.findViewById(R.id.tv_ans);
        final CustomTextView voteText = layout.findViewById(R.id.tv_votes);
        RoundCornerProgressBar bar = layout.findViewById(R.id.animate_progress_bar);
        if (post.getPostDetail().isParticipated()) {
            if (post.getPostDetail().getUserAnswerId() == pollAnswers.get(index).getAnswerId()) {
                ansText.setTextColor(ContextCompat.getColor(context, R.color.app_white));
                bar.setProgressColor(ContextCompat.getColor(context, R.color.bright_teal));
            } else {
                ansText.setTextColor(ContextCompat.getColor(context, R.color.colorTxtNormal));
                bar.setProgressColor(ContextCompat.getColor(context, R.color.search_bg));
            }
            voteText.setVisibility(View.VISIBLE);
            // continue;
        } else {
            voteText.setVisibility(View.GONE);
            bar.setProgressColor(ContextCompat.getColor(context, R.color.search_bg));
        }


        ansText.setText(pollAnswers.get(index).getAnswer());
        voteText.setText(context.getResources().getQuantityString(R.plurals.votes, pollAnswers.get(index).getNoOfVote(), pollAnswers.get(index).getNoOfVote()));
        if (pollAnswers.get(index).getNoOfVote() != 0)
            bar.setProgress(findPercentage(pollAnswers.get(index).getNoOfVote(), post.getPostDetail().getTotResponse()));
        pollBinding.optAnswerViewContainer.addView(layout);
        layout.setOnClickListener(view -> {
            PollAnswer selPoll = pollAnswers.get(pollAnswers.indexOf(new PollAnswer(ansText.getText().toString())));
            listener.onPollClick(pos, post.getPostId(), selPoll.getAnswerId());
        });
    }

    private float findPercentage(int vote, int totalVote) {
        return ((float) vote * 100) / totalVote;
    }


}
