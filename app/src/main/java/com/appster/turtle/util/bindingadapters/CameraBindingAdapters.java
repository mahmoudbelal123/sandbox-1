/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.bindingadapters;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class CameraBindingAdapters {


    @BindingAdapter("addCameraListener")
    public static void bindTextWatcher(CameraView cameraView, CameraListener cameraListener) {
        cameraView.addCameraListener(cameraListener);
    }


    @BindingAdapter("onTouchListener")
    public static void bindTextWatcher(View view, View.OnTouchListener onTouchListener) {
        view.setOnTouchListener(onTouchListener);
    }

    @BindingAdapter("bitmap")
    public static void bindTextWatcher(ImageView view, Bitmap bitmap) {

//        if (bitmap != null)
        view.setImageBitmap(bitmap);

    }

    @BindingAdapter("sessionType")
    public static void bindTextWatcher(CameraView view, SessionType sessionType) {

//        if (bitmap != null)
        view.setSessionType(sessionType);

    }

    @BindingAdapter("gifuri")
    public static void bindTextWatcher(ImageView view, Uri uri) {

        int px = Utils.dpToPx(view.getContext(), 72);

        if (uri != null) {
            File file = new File(uri.getPath());

            Glide.with(view.getContext())
                    .load(file)
                    .override(px, px)
                    .placeholder(R.drawable.id_babble)
                    .into(view);
        }

    }

    @BindingAdapter("circleimage")
    public static void circleImage(ImageView view, Drawable res) {
        Glide.with(view.getContext())
                .load(res)
                .placeholder(R.drawable.usr_dummy_pic)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .signature(new ObjectKey(UUID.randomUUID().toString()))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_grey)
                .into(view);
    }


    @BindingAdapter("videouri")
    public static void bindTextWatcher(final VideoView view, Uri uri) {
        if (uri != null) {
            view.setVideoURI(uri);
            view.setOnPreparedListener(mp -> {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                float videoWidth = mp.getVideoWidth();
                float videoHeight = mp.getVideoHeight();

                Constants.videoHeight = videoHeight;
                Constants.videoWidth = videoWidth;


                mp.setLooping(true);
                float viewWidth = view.getWidth();
                lp.height = (int) (viewWidth * (videoHeight / videoWidth));
                view.setLayoutParams(lp);
                if (view.isPlaying())
                    return;
                view.start();
            });
        } else {
            if (view.isPlaying())
                view.stopPlayback();
        }
    }

    @BindingAdapter("imageurl")
    public static void bindImageUrl(final ImageView view, String url) {
        try {
            Glide.with(view.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.color.bright_teal)
                    .error(R.drawable.ic_pp_cred)
                    .into(view);
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }
    }

    @BindingAdapter("imageurl")
    public static void bindImageUrlWithImage(final ImageView view, String url) {
        try {
            Glide.with(view.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.grey_soild_roun_rect)
                    .error(R.drawable.grey_soild_roun_rect)
                    .into(view);
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }
    }

    @BindingAdapter("imageurl")
    public static void bindProImageUrl(final ImageView view, String url) {
        try {
            Glide.with(view.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_pp_cred)
                    .error(R.drawable.ic_pp_cred)
                    .into(view);
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);

        }
    }

    @BindingAdapter("recImageUrl")
    public static void recImageUrl(final ImageView view, String url) {
        try {
            Glide.with(view.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.color.unselected_grey)
                    .error(R.color.unselected_grey)
                    .into(view);
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }
    }

    @BindingAdapter("roomimageurl")
    public static void bindRoomImageUrl(final ImageView view, String url) {
        try {
            Glide.with(view.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_grey)
                    .centerCrop()
                    .into(view);
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }
    }

    @BindingAdapter("noteimageUrl")
    public static void bindNotImageUrl(final ImageView view, String url) {
        try {
            Glide.with(view.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                    .centerCrop()
                    .placeholder(R.drawable.bg)
                    .into(view);
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }
    }


    @BindingAdapter("circleimageurl")
    public static void bindCircleImageUrl(final ImageView view, String url) {
        try {
            Glide.with(view.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .signature(new ObjectKey(UUID.randomUUID().toString()))
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.group)
                    .into(view);
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }
    }

    public static void bindCircleImageUri(final ImageView view, Uri imageUri) {
        Glide.with(view.getContext())
                .load(imageUri)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .signature(new ObjectKey(UUID.randomUUID().toString()))
                .skipMemoryCache(true)
                .placeholder(R.drawable.group)
                .into(view);
    }

    public static void loadCircleImage(ImageView imageView, String url, int type) {

        try {
            if (type == Constants.IMAGE_INITIALS_ID) {
                Glide.with(imageView.getContext())
                        .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                        .placeholder(R.drawable.ic_transparent)
                        .into(imageView);
            } else {
                Glide.with(imageView.getContext())
                        .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                        .placeholder(R.drawable.ic_transparent)
                        .apply(RequestOptions.circleCropTransform())

                        .into(imageView);

            }
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }
    }

    public static void loadImage(ImageView imageView, String url, int type) {

        try {
            Glide.with(imageView.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_transparent)
                    .into(imageView);

        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }
    }

    @BindingAdapter("profileImageurl")
    public static void bindProfileImageUrl(final ImageView view, String url) {
        try {
            Glide.with(view.getContext())
                    .load(new URL(BuildConfig.ORIGINAL_IMAGE_BASE_URL + url))
                    .placeholder(R.drawable.ic_transparent)

                    .into(view)


            ;
        } catch (MalformedURLException e) {
            LogUtils.printStackTrace(e);
        }
    }


    @BindingAdapter("android:width")
    public static void setLayoutWidth(View view, float width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:height")
    public static void setLayoutHeight(View view, float height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) height;
        view.setLayoutParams(layoutParams);
    }


}
