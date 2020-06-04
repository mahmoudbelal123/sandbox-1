package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.HeaderItemViewholder;
import com.appster.turtle.adapter.viewholder.LoadingViewHolder;
import com.appster.turtle.adapter.viewholder.MeetupViewHolder;
import com.appster.turtle.adapter.viewholder.PollViewHolder;
import com.appster.turtle.adapter.viewholder.TextPostViewHolder;
import com.appster.turtle.adapter.viewholder.VideoViewHolderNew;
import com.appster.turtle.databinding.ItemHeaderPostBinding;
import com.appster.turtle.databinding.ItemLoadMoreBinding;
import com.appster.turtle.databinding.ItemMeetupBinding;
import com.appster.turtle.databinding.ItemPollBinding;
import com.appster.turtle.databinding.ItemTextPostBinding;
import com.appster.turtle.databinding.ItemVideoNewBinding;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.model.Room;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.post.PostDetailActivity;
import com.appster.turtle.util.CustomTypefaceSpan;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.github.sahasbhop.apngview.ApngImageLoader;

import java.util.ArrayList;

/*
 * adapter for posts
 */
public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PostListener {

    private final Context mContext;
    private PostListener postListener;
    private ArrayList<Posts> postsList;
    PopupWindow window;
    private boolean isAlreadyEnable;
    private int mScreen;
    private boolean isHome;

    public boolean isIsfromHome() {
        return isfromHome;
    }

    public void setIsfromHome(boolean isfromHome) {
        this.isfromHome = isfromHome;
    }

    private boolean isfromHome;

    public ArrayList<Room> getRoom() {
        return room;
    }

    public void setRoom(ArrayList<Room> room) {
        this.room = room;
    }

    private ArrayList<Room> room;

    private String query = "";
    private ImageView mView;
    private ApngImageLoader.ApngConfig apngConfig;
    private String uri = "assets://apng/like_a.png";
    private String uriHEART = "assets://apng/heart_a.png";
    private String uriangry = "assets://apng/angry_a.png";
    private String urisurprisedLarge = "assets://apng/surprised_large.png";
    private String urilikeLarge = "assets://apng/like_a_large.png";
    private String uritearLarge = "assets://apng/tear_large.png";
    private String urigrinLarge = "assets://apng/grin_large.png";
    private String urisurprised = "assets://apng/surprised_a.png";
    private String uritear = "assets://apng/tear_a.png";
    private String urigrin = "assets://apng/grin_a.png";


    public void setQuery(String query) {
        this.query = query;
    }

    public PostsAdapter(Context context, ArrayList<Posts> postsList, PostListener postListener) {
        mContext = context;
        this.postsList = postsList;
        this.postListener = postListener;


    }


    public void setHomeScreen(int screen) {
        mScreen = screen;
    }

    public int getScreen() {
        return mScreen;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setIsHome(boolean isHome) {
        this.isHome = isHome;


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
            case Constants.VIEW_TYPE.MORE:
                ItemLoadMoreBinding loadMoreBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_load_more, parent, false);
                return new LoadingViewHolder(loadMoreBinding);

            case Constants.VIEW_TYPE.HEADER:
                ItemHeaderPostBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_header_post, parent, false);
                return new HeaderItemViewholder(binding, mContext);

            default:
                return null;
        }

    }


    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public Posts getItem(int positon) {
        return postsList.get(positon);
    }


    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        Posts temp = null;
        try {
            temp = postsList.get(position);
        } catch (Exception e) {
        }

        final Posts posts = temp;


        if (holder instanceof PollViewHolder) {
            ((PollViewHolder) holder).setQuery(query);
            ((PollViewHolder) holder).bindData(posts);

            ((PollViewHolder) holder).setOnClickListener(this);


            ItemPollBinding pollBinding = ((PollViewHolder) holder).getPollBinding();
            setRoomName(pollBinding.tvRoom, posts.getRooms().getValue());
            pollBinding.clReplyCommentLike.ivLike.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        if (window != null && window.isShowing()) {
                            window.dismiss();
                        } else
                            showDialog(v, position, posts, posts.getLikeStatus());

                        LogUtils.LOGD("tag", posts.getPostDetail().isChoiceSelected() + "  " + posts.getPostDetail().isShown());
                    } catch (Exception e) {
                        LogUtils.LOGD("tag", e.getMessage());
                    }
                }
                return true;
            });
            pollBinding.clReplyCommentLike.tvLikeCount.setOnClickListener(v -> postListener.onLikedBy(holder.getAdapterPosition(), posts.getPostId()));
        } else if (holder instanceof TextPostViewHolder) {
            ((TextPostViewHolder) holder).setQuery(query);
            ((TextPostViewHolder) holder).bindData(posts);
            ItemTextPostBinding textPostBinding = ((TextPostViewHolder) holder).getTextPostBinding();
            setRoomName(textPostBinding.tvRoom, posts.getRooms().getValue());

            textPostBinding.clReplyCommentLike.ivLike.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        if (window != null && window.isShowing()) {
                            window.dismiss();
                        } else
                            showDialog(v, position, posts, posts.getLikeStatus());
                    } catch (Exception e) {
                        LogUtils.LOGD("tag", e.getMessage());
                    }
                }
                return true;
            });
            textPostBinding.clReplyCommentLike.tvLikeCount.setOnClickListener(view -> postListener.onLikedBy(holder.getAdapterPosition(), posts.getPostId()));


        } else if (holder instanceof MeetupViewHolder) {
            MeetupViewHolder meetupViewHolder = ((MeetupViewHolder) holder);
            meetupViewHolder.setQuery(query);

            ItemMeetupBinding meetupBinding = meetupViewHolder.getMeetupBinding();
            setRoomName(meetupBinding.tvRoom, posts.getRooms().getValue());

            meetupBinding.setPost(posts);
            meetupBinding.setUser(posts.getUsersListModel());
            meetupBinding.clReplyCommentLike.setPost(posts);
            meetupViewHolder.bindData(posts, mContext);


            meetupBinding.clReplyCommentLike.ivLike.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        if (window != null && window.isShowing()) {
                            window.dismiss();
                        } else
                            showDialog(v, position, posts, posts.getLikeStatus());
                    } catch (Exception e) {
                        LogUtils.LOGD("tag", e.getMessage());
                    }
                }
                return true;
            });

            meetupBinding.clReplyCommentLike.tvLikeCount.setOnClickListener(view -> postListener.onLikedBy(holder.getAdapterPosition(), posts.getPostId()));

        } else if (holder instanceof VideoViewHolderNew) {

            VideoViewHolderNew videoViewHolder = (VideoViewHolderNew) holder;
            videoViewHolder.setQuery(query);
            videoViewHolder.setVideoPost(posts);

            ItemVideoNewBinding videoBinding = videoViewHolder.getVideoBinding();
            setRoomName(videoBinding.tvRoom, posts.getRooms().getValue());
            videoBinding.bottomLayout.ivLike.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        if (window != null && window.isShowing()) {
                            window.dismiss();
                        } else
                            showDialog(v, position, posts, posts.getLikeStatus());
                    } catch (Exception e) {
                        LogUtils.LOGD("tag", e.getMessage());
                    }
                }
                return true;
            });
            videoBinding.bottomLayout.tvLikeCount.setOnClickListener(v -> postListener.onLikedBy(holder.getAdapterPosition(), posts.getPostId()));
        } else if (holder instanceof HeaderItemViewholder) {
            HeaderItemViewholder viewholder = (HeaderItemViewholder) holder;
            viewholder.setData(getRoom(), mContext, isHome(), getScreen());
        }

    }

    public void setRoomName(TextView tv, String roomName) {


        SpannableString string = new SpannableString("in " + roomName);

        Typeface semiBold = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.eina_01_semibold));

        string.setSpan(new CustomTypefaceSpan(" ", semiBold)
                , 3, roomName.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.bright_teal)),
                3, roomName.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        tv.setText(mContext instanceof HomeMainActivity ? string : "");

        if (mContext instanceof HomeMainActivity)
            tv.setVisibility(View.VISIBLE);
        else
            tv.setVisibility(View.GONE);


    }

    @Override
    public int getItemViewType(int position) {
        if (isfromHome) {
            if (isPositionHeader(position))
                return Constants.VIEW_TYPE.HEADER;
        }

        if (position < postsList.size()) {
            Posts post = postsList.get(position);
            if (post == null)
                return Constants.VIEW_TYPE.MORE;
            return post.getPostType();
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if (isfromHome && postsList.size() == 0) {
            return 1;
        }

        return postsList.size();
    }

    @Override
    public void onPollClick(int position, int postId, int answerId) {
        postListener.onPollClick(position, postId, answerId);
    }

    @Override
    public void onLikedBy(int position, int postId) {
//
    }

    @Override
    public void onLiked(int position, Posts postId, int likeStatus, int alreadyLikeStatus) {
        postListener.onLiked(position, postId, likeStatus, alreadyLikeStatus);
    }

    @Override
    public void onClick(Posts post) {
        boolean isPoll = false;
        if (post.getPostType() == Constants.VIEW_TYPE.POLL) {
            isPoll = true;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.POST_ID, post.getPostId());
        bundle.putBoolean(Constants.BundleKey.IS_COMMENT, false);
        if (isPoll) {
            bundle.putBoolean(Constants.IS_POLL_CHOICE, post.getPostDetail().isShown());
        }
        ExplicitIntent.getsInstance().navigateForResult((BaseActivity) mContext, PostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_POST_DETAIL, bundle);
    }

    @Override
    public void onCommentClicked(Posts post) {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.POST_ID, post.getPostId());
        bundle.putBoolean(Constants.BundleKey.IS_COMMENT, true);
        ExplicitIntent.getsInstance().navigateForResult((BaseActivity) mContext, PostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_POST_DETAIL, bundle);
    }


    @Override
    public void onMeetupStatusChange(int position, int postId, int status, boolean reset) {
        postListener.onMeetupStatusChange(position, postId, status, reset);

    }

    @Override
    public void onHasHTagClick(Posts post, int pos, String string) {
        postListener.onHasHTagClick(post, pos, string);
    }


    public void updateItem(int position, Posts posts, boolean isFlagged, boolean isLikeStatus) {
        if (!isFlagged) {
            posts.getPostDetail().setChoiceSelected(true);
            posts.getPostDetail().setShown(true);
        }
        postsList.set(position, posts);
        notifyItemChanged(position);
    }


    public void setFromRoom(boolean fromRoom) {
        boolean isFromRoom = fromRoom;
    }


    public interface IUpdateMeetupStatus {
        void updateMeetupStatus(int position, int postId, int status, boolean reset);
    }

    public void updateMeetupItem(int position, Posts post) {
        LogUtils.LOGD("updateMeetupItem", "Position: " + position + "post title: " + post.getPostDetail().getTitle());
        postsList.set(position, post);
        notifyItemChanged(position);
    }


    @Override
    public void onMenuClicked(Posts post, int pos) {

        postListener.onMenuClicked(post, pos);
    }

    public void removeItem(int pos) {
        postsList.remove(pos);
        notifyDataSetChanged();
    }

    public void clear() {
        postsList.clear();
        notifyDataSetChanged();
    }

    private void showDialog(final View v, final int pos, final Posts post, final int alreadyStatus) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_animation, null);
        window = new PopupWindow(view, Utils.dpToPx(mContext, 235), Utils.dpToPx(mContext, 55), true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        apngConfig = new ApngImageLoader.ApngConfig(0, true);
        window.setElevation(1f);
        window.setOutsideTouchable(true);
        final ImageView ivSmile = view.findViewById(R.id.iv_smile);
        final ImageView ivLove = view.findViewById(R.id.iv_love);
        final ImageView ivAngry = view.findViewById(R.id.iv_angry);
        final ImageView ivSurprised = view.findViewById(R.id.iv_surprised);
        final ImageView ivTear = view.findViewById(R.id.iv_tear);
        final ImageView ivGrin = view.findViewById(R.id.iv_grin);

        if (window.isShowing()) {
            window.dismiss();
        } else {


            ApngImageLoader.getInstance().displayApng(uri, ivSmile, apngConfig);
            ApngImageLoader.getInstance().displayApng(uriHEART, ivLove, apngConfig);
            ApngImageLoader.getInstance().displayApng(uriangry, ivAngry, apngConfig);
            ApngImageLoader.getInstance().displayApng(urisurprised, ivSurprised, apngConfig);
            ApngImageLoader.getInstance().displayApng(uritear, ivTear, apngConfig);
            ApngImageLoader.getInstance().displayApng(urigrin, ivGrin, apngConfig);
            window.showAsDropDown(v, v.getWidth() - window.getWidth(), -1);
        }


        ivSmile.setOnClickListener(v1 -> {
            postListener.onLiked(pos, post, Constants.reactions.like, alreadyStatus);
            window.dismiss();

        });


        ivSmile.setOnTouchListener((v12, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivSmile, urilikeLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                postListener.onLiked(pos, post, Constants.reactions.like, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });


        ivLove.setOnClickListener(v13 -> {
            postListener.onLiked(pos, post, Constants.reactions.heart, alreadyStatus);
            window.dismiss();

        });


        ivLove.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivLove, uriHEART);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                postListener.onLiked(pos, post, Constants.reactions.heart, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });


        ivAngry.setOnClickListener(v15 -> {
            postListener.onLiked(pos, post, Constants.reactions.angry, alreadyStatus);
            window.dismiss();


        });

        ivAngry.setOnTouchListener((v16, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivGrin, urigrinLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                postListener.onLiked(pos, post, Constants.reactions.angry, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
        ivSurprised.setOnClickListener(v17 -> {
            postListener.onLiked(pos, post, Constants.reactions.surprised, alreadyStatus);
            window.dismiss();

        });

        ivSurprised.setOnTouchListener((v18, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivSurprised, urisurprisedLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                postListener.onLiked(pos, post, Constants.reactions.surprised, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
        ivTear.setOnClickListener(v19 -> {

            postListener.onLiked(pos, post, Constants.reactions.tear, alreadyStatus);
            window.dismiss();

        });


        ivTear.setOnTouchListener((v110, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivTear, uritearLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                postListener.onLiked(pos, post, Constants.reactions.tear, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
        ivGrin.setOnClickListener(v111 -> {

            postListener.onLiked(pos, post, Constants.reactions.grin, alreadyStatus);
            window.dismiss();
        });


        ivGrin.setOnTouchListener((v112, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivGrin, urigrinLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                postListener.onLiked(pos, post, Constants.reactions.grin, alreadyStatus);
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
        ApngImageLoader.getInstance().displayApng(uriangryLarge, ivImage, apngConfig);
        if (ivImage.getId() == R.id.iv_like || ivImage.getId() == R.id.iv_love) {
            ivImage.setScaleX(1.5f);
            ivImage.setScaleY(1.5f);
        } else {
            ivImage.setScaleX(2f);
            ivImage.setScaleY(2f);
        }
        ivImage.getLayoutParams().width = Utils.dpToPx(mContext, 40);
        ivImage.getLayoutParams().height = ivImage.getHeight() + 5;
        ivImage.requestLayout();


    }


    private void reactionOnFoucusReset(ImageView ivImage, String uriangryLarge) {
        ivImage.setElevation(0f);
        isAlreadyEnable = false;
        ApngImageLoader.getInstance().displayApng(uriangryLarge, ivImage, apngConfig);
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
