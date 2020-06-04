/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appster.turtle.R;
import com.appster.turtle.widget.CustomTextView;

import static com.appster.turtle.ui.Constants.BundleKey;


@SuppressWarnings("ALL")
public class IntroSlideFragment extends Fragment {


    private int mSlideIndex;
    private int mLayoutResId;

    public IntroSlideFragment() {
        //
    }

    public static IntroSlideFragment newInstance(int layoutResId, int slideIndex) {
        IntroSlideFragment introSlideFragment = new IntroSlideFragment();

        Bundle args = new Bundle();
        args.putInt(BundleKey.LAYOUT_ID, layoutResId);
        args.putInt(BundleKey.INDEX, slideIndex);
        introSlideFragment.setArguments(args);
        return introSlideFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(BundleKey.LAYOUT_ID)) {
            mLayoutResId = getArguments().getInt(BundleKey.LAYOUT_ID);
            mSlideIndex = getArguments().getInt(BundleKey.INDEX);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(mLayoutResId, container, false);
        ImageView joyride_image = rootView.findViewById(R.id.joyride_image);
        switch (mSlideIndex) {
            case 0:
                ((CustomTextView) rootView.findViewById(R.id.joyride_header_text)).setText(R.string.joyride_page1_text1);
                ((CustomTextView) rootView.findViewById(R.id.joyride_bottom_text)).setText(R.string.joyride_page1_text2);
                joyride_image.setImageResource(R.drawable.splash_screen_1);
                break;
            case 1:
                ((CustomTextView) rootView.findViewById(R.id.joyride_header_text)).setText(R.string.joyride_page2_text1);
                ((CustomTextView) rootView.findViewById(R.id.joyride_bottom_text)).setText(R.string.joyride_page2_text2);
                joyride_image.setImageResource(R.drawable.splash_screen_2);
                break;
            case 2:
                ((CustomTextView) rootView.findViewById(R.id.joyride_header_text)).setText(R.string.joyride_page3_text1);
                ((CustomTextView) rootView.findViewById(R.id.joyride_bottom_text)).setText(R.string.joyride_page3_text2);
                joyride_image.setImageResource(R.drawable.splash_screen_3);
                break;
            case 3:
                ((CustomTextView) rootView.findViewById(R.id.joyride_header_text)).setText(R.string.joyride_page4_text1);
                ((CustomTextView) rootView.findViewById(R.id.joyride_bottom_text)).setText(R.string.joyride_page4_text2);
                joyride_image.setImageResource(R.drawable.splash_screen_4);
                break;

            default:
                break;
        }

        return rootView;

    }

}