/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.Gravity;

import com.appster.turtle.R;
import com.appster.turtle.util.LogUtils;

public class CustomButton extends AppCompatButton {
    private static final String TAG = "CustomButton";

    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomBackground(context, attrs);
        setCustomFont(context, attrs);
        setIncludeFontPadding(false);

    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomBackground(context, attrs);
        setCustomFont(context, attrs);
        setIncludeFontPadding(false);

    }

    @Override
    public void setIncludeFontPadding(boolean includepad) {
        super.setIncludeFontPadding(false);
    }

    private void setCustomBackground(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
        if (a.hasValue(R.styleable.CustomButton_customBackground)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(a.getDrawable(R.styleable.CustomButton_customBackground));
//            } else {
//                setBackgroundDrawable(a.getDrawable(R.styleable.CustomButton_customBackground));
//            }
        } else
            setBackgroundResource(R.drawable.round_corner_gray);
        a.recycle();

    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable") TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String customFont;
        if (a.hasValue(R.styleable.CustomTextView_customFont))
            customFont = a.getString(R.styleable.CustomTextView_customFont);
        else
            customFont = "Roboto-Regular.ttf";
        setCustomFont(ctx, customFont);
        a.recycle();
        setGravity(Gravity.CENTER);
        setPadding(getResources().getDimensionPixelSize(R.dimen.default_padding), 0, getResources().getDimensionPixelSize(R.dimen.default_padding), 0);
        setAllCaps(true);
    }

    public void setCustomFont(Context ctx, String asset) {
        try {
            Typeface tf = Typeface.createFromAsset(ctx.getAssets(), asset);
            setTypeface(tf);
        } catch (Exception e) {
            LogUtils.LOGE(TAG, e.getLocalizedMessage());
        }

    }
}
