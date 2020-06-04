/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.widget;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;

public class ColorGradientDrawable extends GradientDrawable {
    private int mColor;

    @Override
    public void setColor(@ColorInt int argb) {
        mColor = argb;
        super.setColor(argb);
    }

    public int getmColor() {
        return mColor;
    }
}