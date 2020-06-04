/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.media;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityMediaPreviewBinding;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.PostResponse;
import com.appster.turtle.network.response.Posts;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.ReactionByActivity;
import com.appster.turtle.ui.post.PostDetailActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.danikula.videocache.HttpProxyCacheServer;
import com.github.sahasbhop.apngview.ApngImageLoader;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A activity class for media preview
 */
public class MediaPreviewActivity extends BaseActivity {

    private boolean isImage;
    private String url;
    private ActivityMediaPreviewBinding binding;
    private Posts post;
    private boolean isAlreadyEnable;
    private ImageView mView;
    private ApngImageLoader.ApngConfig apngConfig;
    private String uri = "assets://apng/like_a.png";
    private String uriHEART = "assets://apng/heart_a.png";
    private String uriangry = "assets://apng/angry_a.png";
    private String uriangryLarge = "assets://apng/angry_large1.png";
    private String urisurprisedLarge = "assets://apng/surprised_large.png";
    private String urilikeLarge = "assets://apng/like_a_large.png";
    private String uritearLarge = "assets://apng/tear_large.png";
    private String urigrinLarge = "assets://apng/grin_large.png";
    private String urisurprised = "assets://apng/surprised_a.png";
    private String uritear = "assets://apng/tear_a.png";
    private String urigrin = "assets://apng/grin_a.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_preview);

        final Bundle b = getIntent().getExtras();
        post = b.getParcelable(Constants.BundleKey.POST);
        if (b.getString(Constants.BundleKey.IMAGE_URL) != null) {
            isImage = true;
            url = b.getString(Constants.BundleKey.IMAGE_URL);
        } else if (b.getString(Constants.BundleKey.VIDEO_URL) != null) {
            isImage = false;
            url = b.getString(Constants.BundleKey.VIDEO_URL);

        }

        initUI();
    }

    private void initUI() {


        binding.setPost(post);


        if (isImage) {
            try {

                binding.ivZoomageView.setVisibility(View.VISIBLE);
                binding.ivPlay.setVisibility(View.GONE);
                binding.rlVideoView.setVisibility(View.GONE);
                binding.ivZoomageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                GlideApp.with(MediaPreviewActivity.this)
                        .load(new URL(url))
                        .placeholder(R.drawable.ic_bg_black)
                        .into(binding.ivZoomageView)


                ;
            } catch (MalformedURLException e) {
                LogUtils.printStackTrace(e);
            }
        } else {
            binding.ivZoomageView.setVisibility(View.GONE);
            binding.ivPlay.setVisibility(View.VISIBLE);
            binding.rlVideoView.setVisibility(View.VISIBLE);

            playVideo();
        }

        binding.ivLike.setImageResource(R.drawable.ic_like_white);


        binding.ivLike.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                try {
                    showDialog(v, post.getLikeStatus());
                } catch (Exception e) {
                    LogUtils.LOGD("tag", e.getMessage());
                }
            }
            return true;
        });

        binding.ivComment.setOnClickListener(view -> {

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.POST_ID, post.getPostId());
            bundle.putBoolean(Constants.BundleKey.IS_COMMENT, true);
            ExplicitIntent.getsInstance().navigateForResult(MediaPreviewActivity.this, PostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_POST_DETAIL, bundle);
        });

        binding.tvComments.setOnClickListener(view -> {

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.POST_ID, post.getPostId());
            bundle.putBoolean(Constants.BundleKey.IS_COMMENT, true);
            ExplicitIntent.getsInstance().navigateForResult(MediaPreviewActivity.this, PostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_POST_DETAIL, bundle);
        });

        binding.tvLikes.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.POST, post.getPostId());
            ExplicitIntent.getsInstance().navigateTo(MediaPreviewActivity.this, ReactionByActivity.class, bundle);
        });
    }


    public void playVideoClicked(View view) {

        if (binding.videoView.getPlayer() != null) {

            if (binding.videoView.getPlayer().getPlaybackState() == Player.STATE_ENDED) {
                setupPlayer();
                binding.videoView.getPlayer().setPlayWhenReady(true);

                binding.ivPlay.setVisibility(View.GONE);
            } else if (!binding.videoView.getPlayer().getPlayWhenReady())
                binding.videoView.getPlayer().setPlayWhenReady(true);
            else
                binding.videoView.getPlayer().setPlayWhenReady(false);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE.REQUEST_POST_DETAIL:

                if (resultCode == RESULT_OK) {
                    post = data.getExtras().getParcelable(Constants.POST_QUERY);
                    initUI();

                    break;
                }
                break;


        }


    }

    @Override
    public String getActivityName() {
        return null;
    }


    private void checkCachedState() {
        HttpProxyCacheServer proxy = ApplicationController.getProxy(MediaPreviewActivity.this);
        boolean fullyCached = proxy.isCached(url);
        if (fullyCached) {
            hideProgressBar();
        }
    }


    private void setupPlayer() {
        binding.videoView.setUseController(false);
        HttpProxyCacheServer proxy = ApplicationController.getProxy(MediaPreviewActivity.this);
        String proxyUrl = proxy.getProxyUrl(url);

        SimpleExoPlayer exoPlayer = newSimpleExoPlayer();
        binding.videoView.setPlayer(exoPlayer);

        MediaSource videoSource = newVideoSource(proxyUrl);
        exoPlayer.prepare(videoSource);

    }

    private SimpleExoPlayer newSimpleExoPlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        return ExoPlayerFactory.newSimpleInstance(MediaPreviewActivity.this, trackSelector, loadControl);
    }

    private MediaSource newVideoSource(String url) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        String userAgent = Util.getUserAgent(MediaPreviewActivity.this, "AndroidVideoCache sample");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(MediaPreviewActivity.this, userAgent, bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(Uri.parse(url), dataSourceFactory, extractorsFactory, null, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.videoView.getPlayer() != null)
            binding.videoView.getPlayer().setPlayWhenReady(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.videoView.getPlayer() != null)
            binding.videoView.getPlayer().setPlayWhenReady(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binding.videoView.getPlayer() != null)
            binding.videoView.getPlayer().release();
    }

    private void playVideo() {
        checkCachedState();
        setupPlayer();
        binding.videoView.getPlayer().setPlayWhenReady(true);
        binding.videoView.getPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
        binding.videoView.getPlayer().addListener(new Player.EventListener() {

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
                if (playbackState == Player.STATE_ENDED) {
                    binding.ivPlay.setVisibility(View.VISIBLE);

                } else if (binding.videoView.getPlayer().getPlayWhenReady())
                    binding.ivPlay.setVisibility(View.GONE);
                else
                    binding.ivPlay.setVisibility(View.VISIBLE);
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

    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK);
        finish();

    }

    public void onLiked(int reaction, int alreadyStatus) {
        RetrofitRestRepository repository = ((ApplicationController) getApplication()).provideRepository();
        new BaseCallback<PostResponse>(MediaPreviewActivity.this, repository.getApiService()
                .likeDislikePost(post.getPostId(), reaction == alreadyStatus ? Constants.reactions.notLiked : reaction)) {
            @Override
            public void onSuccess(PostResponse likeDislike) {

                post = likeDislike.getData();
                if (likeDislike.getData().getLikeStatus() == Constants.reactions.like) {
                    binding.ivLike.setImageResource(R.drawable.ic_like_thumb);
                } else if (likeDislike.getData().getLikeStatus() == Constants.reactions.heart)
                    binding.ivLike.setImageResource(R.drawable.ic_heart);
                else if (likeDislike.getData().getLikeStatus() == Constants.reactions.angry)
                    binding.ivLike.setImageResource(R.drawable.ic_angry);
                else if (likeDislike.getData().getLikeStatus() == Constants.reactions.grin)
                    binding.ivLike.setImageResource(R.drawable.ic_grin);
                else if (likeDislike.getData().getLikeStatus() == Constants.reactions.surprised)
                    binding.ivLike.setImageResource(R.drawable.ic_surprised);
                else if (likeDislike.getData().getLikeStatus() == Constants.reactions.tear)
                    binding.ivLike.setImageResource(R.drawable.ic_tear);
                else
                    binding.ivLike.setImageResource(R.drawable.ic_like_white);

                binding.tvLikes.setText(String.valueOf(post.getLikesCount()));
                binding.setPost(post);

            }

            @Override
            public void onFail() {
//
            }
        };
    }


    private void showDialog(final View v, final int alreadyStatus) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_animation, null);
        final PopupWindow window = new PopupWindow(view, Utils.dpToPx(this, 235), Utils.dpToPx(this, 55), true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        apngConfig = new ApngImageLoader.ApngConfig(0, true);

        final RelativeLayout layout = view.findViewById(R.id.ll_reaction);
        final ImageView ivSmile = view.findViewById(R.id.iv_smile);
        final ImageView ivLove = view.findViewById(R.id.iv_love);
        final ImageView ivAngry = view.findViewById(R.id.iv_angry);
        final ImageView ivSurprised = view.findViewById(R.id.iv_surprised);
        final ImageView ivTear = view.findViewById(R.id.iv_tear);
        final ImageView ivGrin = view.findViewById(R.id.iv_grin);


        ApngImageLoader.getInstance().displayApng(uri, ivSmile, apngConfig);
        ApngImageLoader.getInstance().displayApng(uriHEART, ivLove, apngConfig);
        ApngImageLoader.getInstance().displayApng(uriangry, ivAngry, apngConfig);
        ApngImageLoader.getInstance().displayApng(urisurprised, ivSurprised, apngConfig);
        ApngImageLoader.getInstance().displayApng(uritear, ivTear, apngConfig);
        ApngImageLoader.getInstance().displayApng(urigrin, ivGrin, apngConfig);


        window.showAsDropDown(v, 0, 0);


        ivSmile.setOnClickListener(v1 -> {
            onLiked(Constants.reactions.like, alreadyStatus);
            window.dismiss();

        });


        ivSmile.setOnTouchListener((v12, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivSmile, urilikeLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                onLiked(Constants.reactions.like, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });


        ivLove.setOnClickListener(v13 -> {
            onLiked(Constants.reactions.heart, alreadyStatus);
            window.dismiss();

        });


        ivLove.setOnTouchListener((v14, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivLove, uriHEART);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                onLiked(Constants.reactions.heart, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });


        ivAngry.setOnClickListener(v15 -> {
            onLiked(Constants.reactions.angry, alreadyStatus);
            window.dismiss();


        });

        ivAngry.setOnTouchListener((v16, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivAngry, uriangryLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                onLiked(Constants.reactions.angry, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
        ivSurprised.setOnClickListener(v17 -> {
            onLiked(Constants.reactions.surprised, alreadyStatus);
            window.dismiss();

        });

        ivSurprised.setOnTouchListener((v18, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivSurprised, urisurprisedLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                onLiked(Constants.reactions.surprised, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
        ivTear.setOnClickListener(v19 -> {

            onLiked(Constants.reactions.tear, alreadyStatus);
            window.dismiss();

        });


        ivTear.setOnTouchListener((v110, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivTear, uritearLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                onLiked(Constants.reactions.tear, alreadyStatus);
                window.dismiss();
            }
            // TODO Auto-generated method stub
            return true;
        });
        ivGrin.setOnClickListener(v111 -> {

            onLiked(Constants.reactions.grin, alreadyStatus);
            window.dismiss();
        });


        ivGrin.setOnTouchListener((v112, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setReactionAnimation(ivGrin, urigrinLarge);
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                onLiked(Constants.reactions.grin, alreadyStatus);
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
        ivImage.getLayoutParams().width = Utils.dpToPx(this, 40);
        ivImage.getLayoutParams().height = ivImage.getHeight() + 5;
        ivImage.requestLayout();


    }


    private void reactionOnFoucusReset(ImageView ivImage, String uriangryLarge) {
        ivImage.setElevation(0f);
        isAlreadyEnable = false;
        ApngImageLoader.getInstance().displayApng(uriangryLarge, ivImage, new ApngImageLoader.ApngConfig(0, true));
        ivImage.setScaleX(1f);
        ivImage.setScaleY(1f);
        ivImage.getLayoutParams().width = Utils.dpToPx(this, 40);
        ivImage.getLayoutParams().height = Utils.dpToPx(this, 45);
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
