/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.appster.turtle.R;

import java.util.HashMap;
import java.util.Map;

public class CustomTextView extends AppCompatTextView {
    private static final String TAG = "CustomTextView";
    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created
     * every time when they are referenced.
     */
    protected static Map<String, Typeface> mTypefaces; //NOSONAR

    private boolean emptyText = true;
    private static final int[] EMPTY_TEXT_STATE = new int[]{R.attr.state_empty_text};

    private CustomTextView mCustomTextView;
    boolean emptyOnFocus;
    private Drawable emptyBg, filledBg;


    public CustomTextView(Context context) {
        super(context);
        setIncludeFontPadding(false);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
        setIncludeFontPadding(false);
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.CustomEditText);

        emptyBg = a.getDrawable(R.styleable.CustomEditText_emptyBg);
        filledBg = a.getDrawable(R.styleable.CustomEditText_filledBg);
        emptyOnFocus = a.getBoolean(R.styleable.CustomEditText_emptyOnFocus, false);
        a.recycle();

    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
        setIncludeFontPadding(false);
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.CustomEditText);

        emptyBg = a.getDrawable(R.styleable.CustomEditText_emptyBg);
        filledBg = a.getDrawable(R.styleable.CustomEditText_filledBg);
        emptyOnFocus = a.getBoolean(R.styleable.CustomEditText_emptyOnFocus, false);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (emptyBg != null) {
            setBackground(emptyBg);
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        //set bg custom
        if (emptyOnFocus && !focused) {
            if (emptyBg != null && emptyText) {
                setBackground(emptyBg);
            } else if (filledBg != null && !emptyText) {
                setBackground(filledBg);
            }
        }

    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (!TextUtils.isEmpty(text)) {
            setEmptyTextState(false);
        } else {
            setEmptyTextState(true);
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        //set bg custom
        if (!emptyOnFocus) {
            if (emptyBg != null && emptyText) {
                setBackground(emptyBg);
            } else if (filledBg != null && !emptyText) {
                setBackground(filledBg);
            }
        }
        int[] state = super.onCreateDrawableState(extraSpace + 1);
        if (emptyText) {
            mergeDrawableStates(state, EMPTY_TEXT_STATE);
        }
        return state;
    }


    public void setEmptyTextState(boolean emptyTextState) {
        this.emptyText = emptyTextState;
        refreshDrawableState();
    }


    private void setCustomFont(Context ctx, AttributeSet attrs) {
        if (mTypefaces == null)
            mTypefaces = new HashMap<>();

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String customFont;
        if (a.hasValue(R.styleable.CustomTextView_customFont))
            customFont = a.getString(R.styleable.CustomTextView_customFont);
        else
            customFont = "fonts/spartan/Spartan-Regular.ttf";
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
}
