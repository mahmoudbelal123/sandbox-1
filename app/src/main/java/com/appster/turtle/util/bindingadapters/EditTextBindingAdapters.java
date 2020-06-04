/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.bindingadapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.databinding.BindingAdapter;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditTextBindingAdapters {


    @BindingAdapter("textChangedListener")
    public static void bindTextWatcher(EditText editText, TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }


    @BindingAdapter("emojiFilter")
    public static void emojiFilter(EditText editText, boolean isEmojiFilter) {

        if (isEmojiFilter)
            editText.setFilters((new InputFilter[]{filter}));

    }


    public static void onlyEmojiFilter(EditText editText, boolean isEmojiFilter, int size) {
        if (isEmojiFilter)
            editText.setFilters((new InputFilter[]{EMOJI_FILTER, new InputFilter.LengthFilter(size)}));

    }

    public static void onlyEmojiFilter(EditText editText, boolean isEmojiFilter, boolean isWhiteSpace, int size) {
        if (isEmojiFilter)
            editText.setFilters((new InputFilter[]{EMOJI_FILTER, WHITE_SPACE_FILTER, new InputFilter.LengthFilter(size)}));

    }


    public static void emojiFilter(EditText editText, boolean isEmojiFilter, int size) {

        if (isEmojiFilter) {
            editText.setFilters((new InputFilter[]{filter, new InputFilter.LengthFilter(size)}));
        }
    }

    private static final InputFilter EMOJI_FILTER = (source, start, end, dest, dstart, dend) -> {
        for (int index = start; index < end; index++) {

            int type = Character.getType(source.charAt(index));

            if (type == Character.SURROGATE) {
                return "";
            }
        }
        return null;
    };

    private static final InputFilter WHITE_SPACE_FILTER = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++)
            if (Character.isWhitespace(source.charAt(i)))
                return "";

        return null;
    };


    private static final InputFilter filter = (source, start, end, dest, dstart, dend) -> {
        for (int i = start; i < end; i++) {
            if (!(Character.isLetterOrDigit(source.charAt(i)) || Character.isWhitespace(source.charAt(i)))) {
                return "";
            }
        }
        return null;
    };

    @BindingAdapter("align")
    public static void alignView(View view, int res) {

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_START, res);
        params.addRule(RelativeLayout.ALIGN_END, res);
        params.addRule(RelativeLayout.ALIGN_TOP, res);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, res);

        view.setLayoutParams(params);
    }

    @BindingAdapter("editorActionListener")
    public static void bindEditorActionListener(EditText editText, TextView.OnEditorActionListener onEditorActionListener) {
        editText.setOnEditorActionListener(onEditorActionListener);
    }

    @BindingAdapter("animatedVisibility")
    public static void setVisibility(final View view,
                                     final int visibility) {

        if (view.getVisibility() == visibility)
            return;

        view.setPivotY(view.getHeight());
        if (visibility == View.VISIBLE) {
            view.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(600)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            view.animate()
                    .translationY(view.getHeight())
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            view.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @BindingAdapter("animatedVisibilityRight")
    public static void setVisibilityRight(final View view,
                                          final int visibility) {

        if (view.getVisibility() == visibility)
            return;

        if (visibility == View.VISIBLE) {
            view.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(600)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            view.animate()
                    .translationX(view.getX())
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            view.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
