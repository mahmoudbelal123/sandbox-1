/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appster.turtle.R;
import com.appster.turtle.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class DefaultIndicatorController implements IndicatorController {
    private static final int FIRST_PAGE_NUM = 0;
    private Context mContext;
    private LinearLayout mDotLayout;
    private List<ImageView> mDots;
    private int mSlideCount;

    @Override
    public View newInstance(@NonNull Context context) {
        mContext = context;
        mDotLayout = (LinearLayout) View.inflate(context, R.layout.default_indicator, null);
        return mDotLayout;
    }

    @Override
    public void initialize(int slideCount) {
        mDots = new ArrayList<>();
        mSlideCount = slideCount;

        for (int i = 0; i < slideCount; i++) {
            ImageView dot = new ImageView(mContext);
            dot.setImageDrawable(Utils.getDrawable(mContext, R.drawable.indicator_dot_grey));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            mDotLayout.addView(dot, params);
            mDots.add(dot);
        }

        selectPosition(FIRST_PAGE_NUM);
    }

    @Override
    public void selectPosition(int index) {
        for (int i = 0; i < mSlideCount; i++) {
            int drawableId = (i == index) ? (R.drawable.indicator_dot_white) : (R.drawable.indicator_dot_grey);
            Drawable drawable = Utils.getDrawable(mContext, drawableId);
            mDots.get(i).setImageDrawable(drawable);
        }
    }
}
