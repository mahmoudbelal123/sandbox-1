
package com.appster.turtle.adapter.viewholder;

import android.app.Activity;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemVideoBinding;
import com.appster.turtle.network.response.Media;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.media.MediaPreviewActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.StringUtils;
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
/*
 * view holder of view post
 */
public class VideoViewHolder extends RecyclerView.ViewHolder implements ToroPlayer {

    private final Context context;
    private ItemVideoBinding videoBinding;

    private SimpleExoPlayerViewHelper helper;
    private Posts post;
    private String proxyUrl;
    private Media media;

    public VideoViewHolder(Context context, ViewDataBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        this.context = context;
        videoBinding = (ItemVideoBinding) viewDataBinding;
    }

    public ItemVideoBinding getVideoBinding() {
        return videoBinding;
    }

    public void setVideoPost(final Posts post) {

        this.post = post;
        media = post.getPostDetail().getPostMediaList().get(0);


        videoBinding.ivView.setVisibility(View.GONE);
        videoBinding.ivPlay.setImageResource(R.drawable.ic_transparent);
        videoBinding.videoView.setVisibility(View.GONE);

        if (post.getPostDetail().getPostMediaList().get(0).getCaption().isEmpty())
            videoBinding.tvCaption.setVisibility(View.GONE);
        else
            videoBinding.tvCaption.setVisibility(View.VISIBLE);


        videoBinding.clReplyCommentLike.setPost(post);

        videoBinding.clReplyCommentLike.tvLikes.setText(String.valueOf(post.getLikesCount()));

        if (post.getLikeStatus() == Constants.LikeStatus.LIKED) {
            videoBinding.clReplyCommentLike.ivLike.setImageResource(R.drawable.ic_liked);
        } else
            videoBinding.clReplyCommentLike.ivLike.setImageResource(R.drawable.ic_like);

        if (Constants.MEDIA_TYPE.fromValue(media.getMediaType()) == null)
            return;


        if (Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.IMAGE)) {

            videoBinding.ivView.setVisibility(View.VISIBLE);
            videoBinding.ivPlay.setImageResource(R.drawable.ic_transparent);


            try {
                Glide.with(context)
                        .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + media.getMedia()))
//                        .centerCrop()
                        .placeholder(R.drawable.ic_grey)
                        .into(videoBinding.ivView)


                ;
            } catch (MalformedURLException e) {
                LogUtils.printStackTrace(e);
            }


        } else if (Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.VIDEO) || Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.GIF)) {
            videoBinding.videoView.setVisibility(View.VISIBLE);
            videoBinding.ivView.setVisibility(View.GONE);
            videoBinding.ivPlay.setImageResource(R.drawable.ic_play);

        } else {
            videoBinding.ivView.setVisibility(View.VISIBLE);
            videoBinding.ivView.setImageResource(R.drawable.ic_grey);
            videoBinding.videoView.setVisibility(View.GONE);
            videoBinding.ivPlay.setImageResource(R.drawable.ic_transparent);

        }


        StringUtils.highlightTextPart(videoBinding.tvCaption, post.getPostDetail().getText(), Constants.POST_QUERY);

        videoBinding.ivView.setOnClickListener(view -> {

            if (Constants.MEDIA_TYPE.fromValue(media.getMediaType()).equals(Constants.MEDIA_TYPE.IMAGE)) {

                Bundle b = new Bundle();
                b.putString(Constants.BundleKey.IMAGE_URL, BuildConfig.ORIGINAL_IMAGE_BASE_URL + media.getMedia());
                b.putParcelable(Constants.BundleKey.POST, post);

                ExplicitIntent.getsInstance().navigateTo((Activity) context, MediaPreviewActivity.class, b);
            }
        });

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

        if (media == null)
            return;

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
//
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

                    videoBinding.ivPlay.setVisibility(View.VISIBLE);
                    videoBinding.ivPlay.setImageResource(R.drawable.ic_play);
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


}
