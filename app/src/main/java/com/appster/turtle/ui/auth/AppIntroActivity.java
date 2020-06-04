/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.auth;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.appster.turtle.R;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.widget.DefaultIndicatorController;
import com.appster.turtle.widget.IndicatorController;
import com.appster.turtle.widget.ViewPagerTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * A activity class for Intro
 */
public class AppIntroActivity extends AppCompatActivity {

    private static final boolean M_SHOW_SKIP = true;
    private static final boolean M_SHOW_DONE = true;
    private IntroSlidePagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private ArrayList<Fragment> mFragmentsList = new ArrayList<>();
    private int mSlidesNumber;
    private IndicatorController mController;
    private View mSkipButton;
    private View mNextButton;

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        PreferenceUtil.setJoyRideComplete(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);
        mSkipButton = findViewById(R.id.skip);
        mNextButton = findViewById(R.id.next);
        final TextView doneButton = findViewById(R.id.done);
        mSkipButton.setOnClickListener(v -> onSkipPressed());
        mNextButton.setOnClickListener(v -> mPager.setCurrentItem(mPager.getCurrentItem() + 1));
        doneButton.setOnClickListener(v -> onDonePressed());
        mPagerAdapter = new IntroSlidePagerAdapter(super.getSupportFragmentManager(), mFragmentsList);
        mPager = findViewById(R.id.viewPager);

        mPager.setAdapter(this.mPagerAdapter);
        /*
         *  ViewPager.setOnPageChangeListener is now deprecated. Use addOnPageChangeListener() instead of it.
         */
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
            }

            @Override
            public void onPageSelected(int position) {
                if (mSlidesNumber > 1)
                    mController.selectPosition(position);
                if (position == mSlidesNumber - 1) {
                    mSkipButton.setVisibility(View.INVISIBLE);
                    mNextButton.setVisibility(View.GONE);
                    if (M_SHOW_DONE) {
                        doneButton.setVisibility(View.VISIBLE);
                    } else {
                        doneButton.setVisibility(View.INVISIBLE);
                    }
                } else {
                    mSkipButton.setVisibility(View.VISIBLE);
                    doneButton.setVisibility(View.GONE);
                    mNextButton.setVisibility(View.VISIBLE);
                }

                if (!M_SHOW_SKIP) {
                    mSkipButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//
            }
        });

        init(savedInstanceState);
        mSlidesNumber = mFragmentsList.size();
        if (mSlidesNumber == 1) {
            mNextButton.setVisibility(View.GONE);
            doneButton.setVisibility(View.VISIBLE);
        } else {
            initController();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public ViewPager getPager() {
        return mPager;
    }

    private void initController() {
        if (mController == null)
            mController = new DefaultIndicatorController();
        FrameLayout indicatorContainer = findViewById(R.id.indicator_container);
        indicatorContainer.addView(mController.newInstance(this));
        mController.initialize(mSlidesNumber);
    }

    private void addSlide(@NonNull Fragment fragment) {
        mFragmentsList.add(fragment);
        mPagerAdapter.notifyDataSetChanged();
    }

    @NonNull
    public List<Fragment> getSlides() {
        return mPagerAdapter.getFragments();
    }

    public void setFadeAnimation() {
        mPager.setPageTransformer(true, new ViewPagerTransformer(ViewPagerTransformer.TransformType.FADE));
    }

    public void setZoomAnimation() {
        mPager.setPageTransformer(true, new ViewPagerTransformer(ViewPagerTransformer.TransformType.ZOOM));
    }

    public void setFlowAnimation() {
        mPager.setPageTransformer(true, new ViewPagerTransformer(ViewPagerTransformer.TransformType.FLOW));
    }

    public void setSlideOverAnimation() {
        mPager.setPageTransformer(true, new ViewPagerTransformer(ViewPagerTransformer.TransformType.SLIDE_OVER));
    }

    public void setDepthAnimation() {
        mPager.setPageTransformer(true, new ViewPagerTransformer(ViewPagerTransformer.TransformType.DEPTH));
    }

    private void init(@Nullable Bundle savedInstanceState) {
        addSlide(IntroSlideFragment.newInstance(R.layout.item_intro, 0));
        addSlide(IntroSlideFragment.newInstance(R.layout.item_intro, 1));
        addSlide(IntroSlideFragment.newInstance(R.layout.item_intro, 2));
        addSlide(IntroSlideFragment.newInstance(R.layout.item_intro, 3));
    }

    private void onSkipPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onDonePressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onKeyDown(int code, KeyEvent event) {
        if (code == KeyEvent.KEYCODE_ENTER || code == KeyEvent.KEYCODE_BUTTON_A) {
            ViewPager vp = this.findViewById(R.id.viewPager);
            if (vp.getCurrentItem() == vp.getAdapter().getCount() - 1) {
                onDonePressed();
            } else {
                vp.setCurrentItem(vp.getCurrentItem() + 1);
            }
            return false;
        }
        return super.onKeyDown(code, event);
    }


}
