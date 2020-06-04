package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemVideoNewBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.network.response.Media;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.PostListener;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.CustomTypefaceSpan;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.Utils;
import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import java.net.MalformedURLException;
import java.net.URL;

import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.SimpleExoPlayerViewHelper;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;

import static android.content.ContentValues.TAG;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 07/03/18.
 * view holder of media post
 */

public class VideoViewHolderNew extends RecyclerView.ViewHolder implements ToroPlayer {
    private final Context context;
    private final PostListener postListener;
    private ItemVideoNewBinding videoBinding;

    private SimpleExoPlayerViewHelper helper;
    private Posts post;
    private String proxyUrl;
    private Media media;
    private String query = "";

    public VideoViewHolderNew(Context context, ViewDataBinding viewDataBinding, PostListener postListener) {
        super(viewDataBinding.getRoot());
        this.context = context;
        videoBinding = (ItemVideoNewBinding) viewDataBinding;
        this.postListener = postListener;

        videoBinding.tvCaption.setHashtagEnabled(true);
        videoBinding.tvCaption.setMentionEnabled(true);

    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ItemVideoNewBinding getVideoBinding() {
        return videoBinding;
    }

    public void setVideoPost(final Posts post) {
        this.post = post;
        videoBinding.setPost(post);
        videoBinding.bottomLayout.setPost(post);
        media = post.getPostDetail().getPostMediaList().get(0);

        if (Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.VIDEO) || Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.GIF)) {
            videoBinding.videoView.setVisibility(View.VISIBLE);
            videoBinding.ivMedia.setImageResource(R.drawable.ic_transparent);
            videoBinding.ivPlay.setImageResource(R.drawable.ic_play);
        } else if (Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.IMAGE)) {

            videoBinding.videoView.setVisibility(View.INVISIBLE);
            videoBinding.ivPlay.setImageResource(R.drawable.ic_transparent);
            try {
                Glide.with(context)
                        .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + media.getMedia()))
                        .placeholder(R.drawable.ic_grey)
                        .into(videoBinding.ivMedia)


                ;
            } catch (MalformedURLException e) {
                LogUtils.LOGD(TAG, e.getMessage());
            }
        }

        videoBinding.tvRoom.setOnClickListener(view -> {


            Bundle bundle = new Bundle();

            bundle.putInt(Constants.BundleKey.ROOM_ID, post.getRooms().getId());
            bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);

            ExplicitIntent.getsInstance().navigateTo((BaseActivity) view.getContext(), RoomActivityCoordinator.class, bundle);


        });

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoBinding.flImageUser.getLayoutParams();
        layoutParams.width = layoutParams.height;
        videoBinding.flImageUser.setLayoutParams(layoutParams);

        videoBinding.ivPostMenu.setOnClickListener(view -> postListener.onMenuClicked(post, getAdapterPosition()));


        videoBinding.getRoot().setOnClickListener(view -> postListener.onClick(post));

        videoBinding.rlVideoClick.setOnClickListener(view -> postListener.onClick(post));

        videoBinding.bottomLayout.ivComment.setOnClickListener(view -> postListener.onCommentClicked(post));

        videoBinding.bottomLayout.tvCommentCount.setOnClickListener(view -> postListener.onCommentClicked(post));


        videoBinding.tvCaption.setTagList(post.getTaggedUsers());

        videoBinding.tvCaption.setOnMentionClickListener((textView, s) -> {

            int userId = 0;
            for (User user : post.getTaggedUsers()) {
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

        videoBinding.tvCaption.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        if (post.getPostDetail().getPostMediaList().get(0).getCaption().isEmpty()) {
            videoBinding.tvCaption.getLayoutParams().height = Utils.dpToPx(context, 4);
        }

        videoBinding.tvCaption.setOnHashtagClickListener((textView, s) -> {
            LogUtils.LOGD("String", s);
            if (!StringUtils.isSpecialChacter(s)) {
                postListener.onHasHTagClick(post, getAdapterPosition(), s);
            }

            return null;
        });


        videoBinding.tvCaption.setQuery(query);


    }


    @NonNull
    @Override
    public View getPlayerView() {
        return videoBinding.videoView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {

        if (helper == null && Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.VIDEO) || Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.GIF)) {

            String url = BuildConfig.ORIGINAL_IMAGE_BASE_URL + post.getPostDetail().getPostMediaList().get(0).getMedia();
            HttpProxyCacheServer proxy = ApplicationController.getProxy(context);
            proxy.registerCacheListener((file, s, i) -> {

            }, url);
            proxyUrl = proxy.getProxyUrl(url);


            helper = new SimpleExoPlayerViewHelper(container, this, Uri.parse(proxyUrl));

            helper.setEventListener(new Player.EventListener() {

                @Override
                public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
//
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                    switch (playbackState) {
                        case PlaybackState.STATE_PLAYING:
//                            videoBinding.videoView.getPlayer().setVolume(0f);
                            videoBinding.ivPlay.setImageResource(R.drawable.ic_transparent);
                            break;

                        case PlaybackState.STATE_BUFFERING:
                        case PlaybackState.STATE_STOPPED:
                        case PlaybackState.STATE_PAUSED:

                            videoBinding.ivPlay.setVisibility(View.VISIBLE);
                            videoBinding.ivPlay.setImageResource(R.drawable.ic_play);
                            break;

                    }

                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
//
                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
//
                }

                @Override
                public void onPositionDiscontinuity(int reason) {
//
                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//
                }

                @Override
                public void onSeekProcessed() {
//
                }
            });

//
            helper.initialize(playbackInfo);
        }

    }

    @Override
    public void play() {
        if (helper != null) helper.play();
    }

    @Override
    public void pause() {
        if (helper != null) helper.pause();
    }

    @Override
    public boolean isPlaying() {
        return helper != null && helper.isPlaying();
    }

    @Override
    public void release() {
        if (helper != null) {
            helper.release();
            helper = null;
        }
    }


    @Override
    public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
    }

    @Override
    public int getPlayerOrder() {
        return getAdapterPosition();
    }

    @Override
    public void onSettled(Container container) {

    }


    private void setTagColor(TextView view, String pTagString, final int pos, final Posts posts) {
        SpannableString string = new SpannableString(pTagString);
        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            LogUtils.LOGD("Hash", String.format("Clicked %s!", tag));
                            postListener.onHasHTagClick(posts, pos, tag);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#000000"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    Typeface semiBold = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.eina_01_semibold));
                    string.setSpan(new CustomTypefaceSpan(" ", semiBold)
                            , start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }


        }

        if (!query.isEmpty()) {

            int index = TextUtils.indexOf(string, query);
            while (index >= 0) {

                string.setSpan(new BackgroundColorSpan(ContextCompat.getColor(view.getContext(), R.color.text_highlight)), index, index
                        + query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                index = TextUtils.indexOf(string, query, index + query.length());
            }
        }


        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(string);

    }


}
