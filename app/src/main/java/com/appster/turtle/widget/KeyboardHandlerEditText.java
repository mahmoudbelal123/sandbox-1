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
import android.view.inputmethod.InputMethodManager;

public class KeyboardHandlerEditText extends android.support.v7.widget.AppCompatEditText {

    Context context;
    OnKeyboardHide onKeyboardHide;

    public KeyboardHandlerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setIncludeFontPadding(false);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
            InputMethodManager mgr = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
            // TODO: Hide your view as you do it in your activity
            if (onKeyboardHide != null)
                onKeyboardHide.onKeyBoardHide();
        }
        return false;
    }

    public void setOnKeyboardHide(OnKeyboardHide onKeyboardHide) {
        this.onKeyboardHide = onKeyboardHide;
    }

    public interface OnKeyboardHide {
        void onKeyBoardHide();
    }
}
