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
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;

import com.appster.turtle.R;

import java.util.HashMap;
import java.util.Map;

public class CustomAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    private static final String TAG = "CustomAutoCompleteTextView";
    int size;
    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created
     * every time when they are referenced.
     */
    protected static Map<String, Typeface> mTypefaces; //NOSONAR

    public CustomAutoCompleteTextView(Context context) {
        super(context);
        setIncludeFontPadding(false);

    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
        setIncludeFontPadding(false);

    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
        setIncludeFontPadding(false);

    }


    private void setCustomFont(Context ctx, AttributeSet attrs) {
        if (mTypefaces == null)
            mTypefaces = new HashMap<>();

        @SuppressLint("CustomViewStyleable") TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String customFont;
        if (a.hasValue(R.styleable.CustomTextView_customFont))
            customFont = a.getString(R.styleable.CustomTextView_customFont);
        else
            customFont = "Roboto-Regular.ttf";
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public void setCustomFont(Context ctx, String path) {
        Typeface typeface;
        if (mTypefaces.containsKey(path)) {
            typeface = mTypefaces.get(path);
        } else {
            typeface = Typeface.createFromAsset(ctx.getAssets(), path);
            mTypefaces.put(path, typeface);
        }
        setTypeface(typeface);
    }

    public void setSize(int size) {
        this.size = size;
    }
}
