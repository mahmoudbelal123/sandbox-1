package com.appster.turtle.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.CommentViewHolder;
import com.appster.turtle.adapter.viewholder.MeetupViewHolder;
import com.appster.turtle.adapter.viewholder.PollViewHolder;
import com.appster.turtle.adapter.viewholder.TextPostViewHolder;
import com.appster.turtle.adapter.viewholder.VideoViewHolderNew;
import com.appster.turtle.databinding.ItemCommentBinding;
import com.appster.turtle.databinding.ItemMeetupBinding;
import com.appster.turtle.model.Comment;
import com.appster.turtle.network.response.Media;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.CommentListener;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.media.MediaPreviewActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.github.sahasbhop.apngview.ApngImageLoader;

import java.util.ArrayList;

/*
 * adapter for comments
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PostListener, CommentListener {

    private final Context mContext;
    private Posts posts;
    private ArrayList<Comment> comments;
    private boolean isAlreadyEnable;

    private PostListener listener;
    private CommentListener commentListener;
    private boolean isOnlyComments;

    private ImageView mView;
    private String uriHEART = "assets://apng/heart_a.png";
    private String uriangry = "assets://apng/angry_a.png";
    private String uriangryLarge = "assets://apng/angry_large1.png";
    private String urisurprisedLarge = "assets://apng/surprised_large.png";
    private String urilikeLarge = "assets://apng/like_a_large.png";
    private String uritearLarge = "assets://apng/tear_large.png";
    private String urigrinLarge = "assets://apng/grin_large.png";
    private String urisurprised = "assets://apng/surprised_a.png";

    public void setOnClickListener(PostListener listener) {
        this.listener = listener;
    }


    public CommentsAdapter(Context context, ArrayList<Comment> comments, Posts post, PostListener postListener, CommentListener commentListener, boolean isOnlyComments) {
        mContext = context;
        this.comments = comments;
        this.listener = postListener;
        this.commentListener = commentListener;
        this.posts = post;
        this.isOnlyComments = isOnlyComments;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Constants.VIEW_TYPE.TEXT:
                return new TextPostViewHolder(mContext, DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_text_post, parent, false), this);
            case Constants.VIEW_TYPE.MEDIA:
                return new VideoViewHolderNew(mContext, DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_video_new, parent, false), this);
            case Constants.VIEW_TYPE.POLL:
                return new PollViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_poll, parent, false), this);
            case Constants.VIEW_TYPE.MEET_UP:
                return new MeetupViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_meetup, parent, false), this);
            case Constants.VIEW_TYPE.COMMENT:
                ItemCommentBinding itemCommentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_comment, parent, false);
                return new CommentViewHolder(itemCommentBinding, this);
            default:
                break;

        }

        return null;
    }

    public void updatePost(Posts posts) {
        this.posts = posts;
        notifyItemChanged(0);
    }


    public Object getItem(int positon) {

        return isOnlyComments ? comments.get(positon) : comments.get(positon - 1);

    }


    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof PollViewHolder) {
            ((PollViewHolder) holder).bindData(posts);
            ((PollViewHolder) holder).getPollBinding().cvCard.setRadius(0);
            ((PollViewHolder) holder).getPollBinding().cvCard.setCardElevation(0);
            ((PollViewHolder) holder).getPollBinding().cvCard.setBackgroundColor(ContextCompat.getColor(mContext, R.color.app_white));
            ((PollViewHolder) holder).getPollBinding().ivMenu.setVisibility(View.INVISIBLE);
            ((PollViewHolder) holder).getPollBinding().tvRoom.setVisibility(View.GONE);
            ((PollViewHolder) holder).getPollBinding().clReplyCommentLike.ivComment.setVisibility(View.INVISIBLE);
            ((PollViewHolder) holder).getPollBinding().clReplyCommentLike.tvCommentCount.setVisibility(View.INVISIBLE);
            setMarginZero(((PollViewHolder) holder).getPollBinding().cvCard);
            ((PollViewHolder) holder).setOnClickListener(this);
            ((PollViewHolder) holder).setViewForComment(true);
            ((PollViewHolder) holder).getPollBinding().getRoot().setBackgroundResource(R.color.app_white);
            ((PollViewHolder) holder).getPollBinding().clReplyCommentLike.ivLike.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        showDialog(v, position, posts, posts.getLikeStatus());
                    } catch (Exception e) {
                        LogUtils.LOGD("tag", e.getMessage());
                    }
                }
                return true;
            });
            ((PollViewHolder) holder).getPollBinding().clReplyCommentLike.tvLikeCount.setOnClickListener(view -> listener.onLikedBy(holder.getAdapterPosition(), posts.getPostId()));
        } else if (holder instanceof TextPostViewHolder) {
            ((TextPostViewHolder) holder).bindData(posts);
            ((TextPostViewHolder) holder).getTextPostBinding().cvCard.setRadius(0);
            ((TextPostViewHolder) holder).getTextPostBinding().cvCard.setCardElevation(0);
            ((TextPostViewHolder) holder).getTextPostBinding().ivMenu.setVisibility(View.INVISIBLE);
            ((TextPostViewHolder) holder).getTextPostBinding().getRoot().setBackgroundResource(R.color.app_white);
            ((TextPostViewHolder) holder).getTextPostBinding().tvRoom.setVisibility(View.GONE);
            ((TextPostViewHolder) holder).getTextPostBinding().clReplyCommentLike.ivComment.setVisibility(View.INVISIBLE);
            ((TextPostViewHolder) holder).getTextPostBinding().clReplyCommentLike.tvCommentCount.setVisibility(View.INVISIBLE);
            ((TextPostViewHolder) holder).getTextPostBinding().clReplyCommentLike.ivLike.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        showDialog(v, position, posts, posts.getLikeStatus());
                    } catch (Exception e) {
                        LogUtils.LOGD("tag", e.getMessage());
                    }
                }
                return true;
            });
            ((TextPostViewHolder) holder).getTextPostBinding().clReplyCommentLike.tvLikeCount.setOnClickListener(view -> listener.onLikedBy(holder.getAdapterPosition(), posts.getPostId()));

            setMarginZero(((TextPostViewHolder) holder).getTextPostBinding().cvCard);

        } else if (holder instanceof MeetupViewHolder) {
            MeetupViewHolder meetupViewHolder = ((MeetupViewHolder) holder);
            ItemMeetupBinding meetupBinding = meetupViewHolder.getMeetupBinding();
            meetupBinding.cvCard.setRadius(0);
            meetupBinding.cvCard.setCardElevation(0);
            setMarginZero(meetupBinding.cvCard);
            meetupBinding.getRoot().setBackgroundResource(R.color.app_white);
            meetupBinding.tvRoom.setVisibility(View.GONE);
            meetupBinding.clReplyCommentLike.ivComment.setVisibility(View.INVISIBLE);
            meetupBinding.clReplyCommentLike.tvCommentCount.setVisibility(View.INVISIBLE);
            meetupBinding.setPost(posts);
            meetupBinding.setUser(posts.getUsersListModel());
            meetupBinding.clReplyCommentLike.setPost(posts);
            meetupViewHolder.bindData(posts, mContext);
            ((MeetupViewHolder) holder).getMeetupBinding().ivMenu.setVisibility(View.INVISIBLE);

            ((MeetupViewHolder) holder).getMeetupBinding().clReplyCommentLike.ivLike.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        showDialog(v, position, posts, posts.getLikeStatus());
                    } catch (Exception e) {
                        LogUtils.LOGD("tag", e.getMessage());
                    }
                }
                return true;
            });

            meetupBinding.clReplyCommentLike.tvLikeCount.setOnClickListener(view -> listener.onLikedBy(holder.getAdapterPosition(), posts.getPostId()));

        } else if (holder instanceof VideoViewHolderNew) {

            VideoViewHolderNew videoViewHolder = (VideoViewHolderNew) holder;
            videoViewHolder.getVideoBinding().cvCard.setRadius(0);
            videoViewHolder.getVideoBinding().cvCard.setCardElevation(0);
            videoViewHolder.getVideoBinding().ivBack.setVisibility(View.INVISIBLE);
            videoViewHolder.getVideoBinding().getRoot().setBackgroundResource(R.color.app_white);

            videoViewHolder.getVideoBinding().tvRoom.setVisibility(View.GONE);

            videoViewHolder.getVideoBinding().bottomLayout.ivComment.setVisibility(View.INVISIBLE);
            videoViewHolder.getVideoBinding().bottomLayout.tvCommentCount.setVisibility(View.INVISIBLE);

            setMarginZero(videoViewHolder.getVideoBinding().cvCard);
            ((VideoViewHolderNew) holder).getVideoBinding().ivPostMenu.setVisibility(View.INVISIBLE);

            ((VideoViewHolderNew) holder).getVideoBinding().bottomLayout.ivLike.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        showDialog(v, position, posts, posts.getLikeStatus());
                    } catch (Exception e) {
                        LogUtils.LOGD("tag", e.getMessage());
                    }
                }
                return true;
            });
            ((VideoViewHolderNew) holder).getVideoBinding().bottomLayout.tvLikeCount.setOnClickListener(view -> listener.onLikedBy(holder.getAdapterPosition(), posts.getPostId()));
            videoViewHolder.setVideoPost(posts);

        } else if (holder instanceof CommentViewHolder) {

            LogUtils.LOGD("POSTIO", position + "");
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            commentViewHolder.bindData((Comment) getItem(position));


        }
    }

    private void setMarginZero(CardView view) {

        ((FrameLayout) view.getParent()).setBackgroundColor(ContextCompat.getColor(mContext, R.color.app_white));
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) ((FrameLayout) view.getParent()).getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        ((FrameLayout) view.getParent()).setPadding(0, 0, 0, 0);
        ((FrameLayout) view.getParent()).setLayoutParams(layoutParams);

    }

    @Override
    public int getItemViewType(int position) {
        if (isOnlyComments) {
            return Constants.VIEW_TYPE.COMMENT;
        } else {
            if (position == 0) {
                if (posts == null)
                    return Constants.VIEW_TYPE.MORE;
                return posts.getPostType();
            }
            return Constants.VIEW_TYPE.COMMENT;
        }

    }

    @Override
    public int getItemCount() {
        return isOnlyComments ? comments.size() : comments.size() + 1;
    }

    @Override
    public void onPollClick(int position, int postId, int answerId) {
        listener.onPollClick(position, postId, answerId);
    }

    @Override
    public void onLikedBy(int position, int postId) {
//onLikedby
    }


    @Override
    public void onLiked(int position, Posts postId, int likeStatus, int alreadyLike) {
        listener.onLiked(position, postId, likeStatus, alreadyLike);
    }

    @Override
    public void onClick(Posts post) {

        if (post.getPostType() != Constants.VIEW_TYPE.MEDIA)
            return;

        Media media = post.getPostDetail().getPostMediaList().get(0);
        Bundle b = new Bundle();

        if (Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.VIDEO) || Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.GIF)) {

            b.putString(Constants.BundleKey.VIDEO_URL, BuildConfig.ORIGINAL_IMAGE_BASE_URL + media.getMedia());
        } else
            b.putString(Constants.BundleKey.IMAGE_URL, BuildConfig.ORIGINAL_IMAGE_BASE_URL + media.getMedia());

        b.putParcelable(Constants.BundleKey.POST, post);

        ExplicitIntent.getsInstance().navigateForResult((Activity) mContext, MediaPreviewActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, b);

    }

    @Override
    public void onMeetupStatusChange(int position, int postId, int status, boolean isMeetupResponded) {
        listener.onMeetupStatusChange(position, postId, status, isMeetupResponded);
    }

    @Override
    public void onHasHTagClick(Posts post, int pos, String string) {
        listener.onHasHTagClick(post, pos, string);
    }

    @Override
    public void onCommentClicked(Posts post) {
//onLikedby
    }


    public void updateItem(int position, Posts posts) {
        posts.getPostDetail().setChoiceSelected(true);
        posts.getPostDetail().setShown(true);
        this.posts = posts;
        notifyItemChanged(position);
    }

    public void updateMeetupItem(int position, Posts post) {
        LogUtils.LOGD("updateMeetupItem", "Position: " + position + "post title: " + post.getPostDetail().getTitle());
        this.posts = post;
        notifyItemChanged(position);
    }


    @Override
    public void onMenuClicked(Posts post, int pos) {

        listener.onMenuClicked(post, pos);
    }


    @Override
    public void onCommentMenuClicked(Comment comment, int pos) {
        commentListener.onCommentMenuClicked(comment, pos);

    }

    @Override
    public void onCommentLiked(int position, int postId, int likeStatus) {

        commentListener.onCommentLiked(position, postId, likeStatus);
    }

    @Override
    public void onCommentLikedBy(int position, int postId, int likeStatus) {

        commentListener.onCommentLikedBy(position, postId, likeStatus);

    }

    public void updateItem(int position, Comment comment) {
        comments.set(isOnlyComments ? position : position - 1, comment);
        notifyItemChanged(position);
    }

    public void deleteItem(int position, Comment comment) {
        comments.remove(isOnlyComments ? position : position - 1);
        notifyItemRemoved(position);
    }

    private void showDialog(final View v, final int pos, final Posts postId, final int alreadyStatus) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_animation, null);
        final PopupWindow window = new PopupWindow(view, Utils.dpToPx(mContext, 235), Utils.dpToPx(mContext, 55), true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ApngImageLoader.ApngConfig apngConfig = new ApngImageLoader.ApngConfig(0, true);
        final ImageView ivSmile = view.findViewById(R.id.iv_smile);
        final ImageView ivLove = view.findViewById(R.id.iv_love);
        final ImageView ivAngry = view.findViewById(R.id.iv_angry);
        final ImageView ivSurprised = view.findViewById(R.id.iv_surprised);
        final ImageView ivTear = view.findViewById(R.id.iv_tear);
        final ImageView ivGrin = view.findViewById(R.id.iv_grin);


        String uri = "assets://apng/like_a.png";
        ApngImageLoader.getInstance().displayApng(uri, ivSmile, apngConfig);
        ApngImageLoader.getInstance().displayApng(uriHEART, ivLove, apngConfig);
        ApngImageLoader.getInstance().displayApng(uriangry, ivAngry, apngConfig);
        ApngImageLoader.getInstance().displayApng(urisurprised, ivSurprised, apngConfig);
        String uritear = "assets://apng/tear_a.png";
        ApngImageLoader.getInstance().displayApng(uritear, ivTear, apngConfig);
        String urigrin = "assets://apng/grin_a.png";
        ApngImageLoader.getInstance().displayApng(urigrin, ivGrin, apngConfig);

        window.showAsDropDown(v, v.getWidth() - window.getWidth(), 0);
        ivSmile.setOnClickListener(v112 -> {

            listener.onLiked(pos, postId, Constants.reactions.like, alreadyStatus);
            window.dismiss();

        });


        ivSmile.setOnTouchListener((v1, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivSmile, urilikeLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                listener.onLiked(pos, postId, Constants.reactions.like, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });


        ivLove.setOnClickListener(v12 -> {
            listener.onLiked(pos, postId, Constants.reactions.heart, alreadyStatus);
            window.dismiss();

        });


        ivLove.setOnTouchListener((v13, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivLove, uriHEART);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                listener.onLiked(pos, postId, Constants.reactions.heart, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });


        ivAngry.setOnClickListener(v14 -> {
            listener.onLiked(pos, postId, Constants.reactions.angry, alreadyStatus);
            window.dismiss();


        });

        ivAngry.setOnTouchListener((v15, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivGrin, uriangryLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                listener.onLiked(pos, postId, Constants.reactions.angry, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
        ivSurprised.setOnClickListener(v16 -> {
            listener.onLiked(pos, postId, Constants.reactions.surprised, alreadyStatus);
            window.dismiss();

        });

        ivSurprised.setOnTouchListener((v17, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivSurprised, urisurprisedLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                listener.onLiked(pos, postId, Constants.reactions.surprised, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
        ivTear.setOnClickListener(v18 -> {


            listener.onLiked(pos, postId, Constants.reactions.tear, alreadyStatus);
            window.dismiss();

        });


        ivTear.setOnTouchListener((v19, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivTear, uritearLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                listener.onLiked(pos, postId, Constants.reactions.tear, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
        ivGrin.setOnClickListener(v110 -> {
            listener.onLiked(pos, postId, Constants.reactions.grin, alreadyStatus);
            window.dismiss();
        });


        ivGrin.setOnTouchListener((v111, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivGrin, urigrinLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                listener.onLiked(pos, postId, Constants.reactions.grin, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
    }

    private void reactionOnFoucus(ImageView ivImage, String uriangryLarge) {
        mView = ivImage;
        isAlreadyEnable = true;
        ivImage.setElevation(10f);
        ApngImageLoader.getInstance().displayApng(uriangryLarge, ivImage, new ApngImageLoader.ApngConfig(0, true));
        ivImage.setScaleX(2f);
        ivImage.setScaleY(2f);
        ivImage.getLayoutParams().width = Utils.dpToPx(mContext, 40);
        ivImage.getLayoutParams().height = ivImage.getHeight() + 5;
        ivImage.requestLayout();


    }


    private void reactionOnFoucusReset(ImageView ivImage, String uriangryLarge) {
        ivImage.setElevation(0f);
        isAlreadyEnable = false;
        ApngImageLoader.getInstance().displayApng(uriangryLarge, ivImage, new ApngImageLoader.ApngConfig(0, true));
        ivImage.setScaleX(1f);
        ivImage.setScaleY(1f);
        ivImage.getLayoutParams().width = Utils.dpToPx(mContext, 40);
        ivImage.getLayoutParams().height = Utils.dpToPx(mContext, 45);
        ivImage.requestLayout();

    }

    private void setReactionAnimation(ImageView ivImage, String uri) {
        if (isAlreadyEnable) {
            if (mView.getId() == R.id.iv_smile) {
            } else if (mView.getId() == R.id.iv_love) {

            } else if (mView.getId() == R.id.iv_angry) {
                reactionOnFoucusReset(mView, uriangry);
            } else if (mView.getId() == R.id.iv_surprised) {
                reactionOnFoucusReset(mView, urisurprised);
            } else if (mView.getId() == R.id.iv_tear) {

            } else if (mView.getId() == R.id.iv_grin) {

            }
        }

        reactionOnFoucus(ivImage, uri);
    }
}
