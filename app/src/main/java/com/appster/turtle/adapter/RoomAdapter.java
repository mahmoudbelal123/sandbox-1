/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.LoadingViewHolder;
import com.appster.turtle.adapter.viewholder.MeetupViewHolder;
import com.appster.turtle.adapter.viewholder.PollViewHolder;
import com.appster.turtle.adapter.viewholder.TextPostViewHolder;
import com.appster.turtle.adapter.viewholder.VideoViewHolder;
import com.appster.turtle.adapter.viewholder.VideoViewHolderNew;
import com.appster.turtle.databinding.ItemLoadMoreBinding;
import com.appster.turtle.databinding.ItemMeetupBinding;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.appster.turtle.util.LogUtils.LOGE;

/**
 * Created by atul on 05/09/17.
 * To inject activity reference.
 * adapter for room
 */

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int position = -1;
    private List<Posts> postsList;
    private OnClickListener listener;
    private IUpdateMeetupStatus meetupStatusChangeListener;
    private final Handler handler = new Handler();
    private Context mContext;

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public RoomAdapter(Context context) {
        mContext = context;
        postsList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {

            case Constants.VIEW_TYPE.MEDIA:
                return new VideoViewHolder(mContext, DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_video, parent, false));
            case Constants.VIEW_TYPE.POLL:
            case Constants.VIEW_TYPE.MEET_UP:
            case Constants.VIEW_TYPE.MORE:
                ItemLoadMoreBinding loadMoreBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_load_more, parent, false);
                return new LoadingViewHolder(loadMoreBinding);

            default:
                return null;
        }

    }


    public Posts getItem(int positon) {
        return postsList.get(positon);
    }


    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Posts posts = postsList.get(position);
        if (holder instanceof PollViewHolder) {
            PollViewHolder pollViewHolder = (PollViewHolder) holder;
            pollViewHolder.getPollBinding().setPost(posts);
            pollViewHolder.bindData(posts);

        } else if (holder instanceof TextPostViewHolder) {
            TextPostViewHolder textPostViewHolder = (TextPostViewHolder) holder;
            textPostViewHolder.bindData(posts);
        } else if (holder instanceof MeetupViewHolder) {
            MeetupViewHolder meetupViewHolder = ((MeetupViewHolder) holder);
            ItemMeetupBinding meetupBinding = meetupViewHolder.getMeetupBinding();
            meetupBinding.setPost(posts);
            meetupBinding.setUser(posts.getUsersListModel());
            meetupBinding.clReplyCommentLike.setPost(posts);
            meetupViewHolder.bindData(posts, mContext);

            meetupBinding.clReplyCommentLike.ivLike.setOnClickListener(view -> listener.onLiked(holder.getAdapterPosition(), posts.getPostId(), posts.getLikeStatus()));

            meetupBinding.clReplyCommentLike.tvLikeCount.setOnClickListener(view -> listener.onLikedBy(holder.getAdapterPosition(), posts.getPostId()));


        } else if (holder instanceof VideoViewHolderNew) {

            VideoViewHolderNew videoViewHolder = (VideoViewHolderNew) holder;
            videoViewHolder.setVideoPost(posts);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < postsList.size()) {
            Posts post = postsList.get(position);
            if (post == null)
                return Constants.VIEW_TYPE.MORE;
            return post.getPostType();
        }
        return 0;
    }

    public boolean isLoadMore() {
        return position > -1;
    }

    public void showLoadMore() {
        postsList.add(null);
        position = postsList.size() - 1;
        handler.post(() -> notifyItemChanged(position));
    }

    public void hideLoadMore() {
        if (position > -1 && postsList.size() > position) {
            postsList.remove(position);
            notifyItemChanged(position);
            position = -1;
        }
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public void addAll(ArrayList<Posts> postsList) {
        this.postsList.addAll(postsList);
        notifyDataSetChanged();
    }

    public void replaceAll(ArrayList<Posts> postsList) {
        this.postsList.clear();
        this.postsList.addAll(postsList);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        postsList.remove(position);
        notifyDataSetChanged();
    }

    public interface OnClickListener {
        void onClick(int position, int postId, int answerId);


        void onLikedBy(int position, int postId);

        void onLiked(int position, int postId, int likeStatus);

        void onDownArrowClick(int position, int postId);
    }

    public void updateItem(int position, Posts posts) {
        postsList.set(position, posts);
        notifyItemChanged(position);
    }

    public void updateMeetupItem(int position, Posts post) {
        LOGE("updateMeetupItem", "Position: " + position + "post title: " + post.getPostDetail().getTitle());
        postsList.set(position, post);
        notifyItemChanged(position);
    }

    public void setOnMeetupStatusChangeListener(IUpdateMeetupStatus meetupStatusChangeListener) {
        this.meetupStatusChangeListener = meetupStatusChangeListener;
    }

    public interface IUpdateMeetupStatus {
        void updateMeetupStatus(int position, int postId, int status, boolean reset);
    }

}
