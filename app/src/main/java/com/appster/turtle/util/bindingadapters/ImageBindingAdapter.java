package com.appster.turtle.util.bindingadapters;

import android.databinding.BindingAdapter;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Utils;

/**
 * Created  on 24/04/18 .
 */

public class ImageBindingAdapter {

    @BindingAdapter("llwidth")
    public static void setLayoutWidth(ImageView view, int likestatus) {
        if (likestatus == 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(layoutParams);
        } else if (likestatus == Constants.reactions.heart || likestatus == Constants.reactions.like) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = Utils.dpToPx(view.getContext(), 30);
            view.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = Utils.dpToPx(view.getContext(), 30);
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("llheight")
    public static void setLayoutHeight(ImageView view, float likestatus) {
        if (likestatus == 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(layoutParams);
            view.setPadding(0, 0, 0, 0);
        } else if (likestatus == Constants.reactions.heart || likestatus == Constants.reactions.like) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = Utils.dpToPx(view.getContext(), 30);
            view.setPadding(0, 0, 0, 0);
            view.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = Utils.dpToPx(view.getContext(), 30);
            view.setLayoutParams(layoutParams);
            view.setPadding(0, Utils.dpToPx(view.getContext(), -2), 0, 0);
        }
    }


    @BindingAdapter("llwidthpreview")
    public static void setLayoutWidthPreview(ImageView view, int likestatus) {
        if (likestatus == 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(layoutParams);
        } else if (likestatus == Constants.reactions.heart || likestatus == Constants.reactions.like) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = Utils.dpToPx(view.getContext(), 27);
            view.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = Utils.dpToPx(view.getContext(), 27);
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("llheightprivew")
    public static void setLayoutHeightPreview(ImageView view, float likestatus) {
        if (likestatus == 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(layoutParams);
        } else if (likestatus == Constants.reactions.heart || likestatus == Constants.reactions.like) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = Utils.dpToPx(view.getContext(), 27);
            view.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = Utils.dpToPx(view.getContext(), 27);
            view.setLayoutParams(layoutParams);

        }
    }


    @BindingAdapter("llMargin")
    public static void setMargin(ImageView view, float likestatus) {
        if (likestatus != 0) {
            view.setCropToPadding(true);
            view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }


}
