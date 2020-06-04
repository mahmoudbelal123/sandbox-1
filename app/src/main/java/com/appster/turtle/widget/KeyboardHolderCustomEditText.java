/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.appster.turtle.ui.textpost.CreateTextPostActivity;

/**
 * Created by rohantaneja on 05-Sep-2017
 */

public class KeyboardHolderCustomEditText extends CustomEditText {

    Context mContext;

    public KeyboardHolderCustomEditText(Context context) {
        super(context);
        mContext = context;
    }

    public KeyboardHolderCustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public KeyboardHolderCustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public KeyboardHolderCustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mContext instanceof CreateTextPostActivity) {
                ((CreateTextPostActivity) mContext).finish();
            }
            return true;
        }
        return false;
    }


}
