package com.appster.turtle.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityReactionByBinding;


/**
 * Created  on 26/04/18 .
 * Activity for reaction by
 */

public class ReactionByActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    ActivityReactionByBinding mBinding;
    private int postId;
    private SectionsPagerAdapter sectionsPagerAdapter;

    private int[] tabIcons = {
            R.drawable.ic_like_thumb,
            R.drawable.ic_heart,
            R.drawable.ic_angry,
            R.drawable.ic_surprised,
            R.drawable.ic_tear,
            R.drawable.ic_grin

    };

    @Override
    public String getActivityName() {
        return getClass().getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reaction_by);
        Bundle b = getIntent().getExtras();
        postId = b.getInt(Constants.BundleKey.POST);
        mBinding.radiogroup.setOnCheckedChangeListener(this);
        initUI();
    }


    private void setupTabIcons() {
        mBinding.slidingTabs.getTabAt(0).setCustomView(getTab(0));
        mBinding.slidingTabs.getTabAt(1).setCustomView(getTab(1));
        mBinding.slidingTabs.getTabAt(2).setCustomView(getTab(2));
        mBinding.slidingTabs.getTabAt(3).setCustomView(getTab(3));
        mBinding.slidingTabs.getTabAt(4).setCustomView(getTab(4));
        mBinding.slidingTabs.getTabAt(5).setCustomView(getTab(5));


    }

    private View getTab(int i) {
        View view = getLayoutInflater().inflate(R.layout.reaction_tab_icon, null);
        ((ImageView) view.findViewById(R.id.icon)).setImageResource(tabIcons[i]);
        return view;
    }

    private void initUI() {
        setUpHeader(true, mBinding.header.clHeader, getString(R.string.reaction), "", R.drawable.back_arrow);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mBinding.container.setAdapter(sectionsPagerAdapter);
        mBinding.slidingTabs.setupWithViewPager(mBinding.container);
        setupTabIcons();


        likeSelected();
        mBinding.container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        likeSelected();
                        break;
                    case 1:
                        heartSelected();
                        break;
                    case 2:
                        angrySelected();
                        break;
                    case 3:
                        surprisedSelected();
                        break;
                    case 4:
                        tearSelected();
                        break;
                    case 5:
                        grinSelected();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
//
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//
            }
        });

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.iv_like:
                mBinding.container.setCurrentItem(0);
                likeSelected();
                break;
            case R.id.iv_heart:
                mBinding.container.setCurrentItem(1);
                heartSelected();
                break;
            case R.id.iv_angry:
                mBinding.container.setCurrentItem(2);
                angrySelected();
                break;
            case R.id.iv_surprised:
                mBinding.container.setCurrentItem(3);
                surprisedSelected();
                break;
            case R.id.iv_tear:
                mBinding.container.setCurrentItem(4);
                tearSelected();
                break;

            case R.id.iv_grinRadio:
                mBinding.container.setCurrentItem(5);
                grinSelected();
                break;

            default:
                break;
        }
    }


    private void likeSelected() {
        mBinding.ivLike.setCompoundDrawablesRelative(null, null, null, ContextCompat.getDrawable(this, R.drawable.selector_button_reaction));
        mBinding.ivHeart.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivGrinRadio.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivAngry.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivSurprised.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivTear.setCompoundDrawablesRelative(null, null, null, null);

    }

    private void heartSelected() {
        mBinding.ivHeart.setCompoundDrawablesRelative(null, null, null, ContextCompat.getDrawable(this, R.drawable.selector_button_reaction));
        mBinding.ivLike.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivGrinRadio.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivAngry.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivSurprised.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivTear.setCompoundDrawablesRelative(null, null, null, null);

    }

    private void angrySelected() {
        mBinding.ivAngry.setCompoundDrawablesRelative(null, null, null, ContextCompat.getDrawable(this, R.drawable.selector_button_reaction));
        mBinding.ivLike.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivGrinRadio.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivHeart.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivSurprised.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivTear.setCompoundDrawablesRelative(null, null, null, null);

    }

    private void surprisedSelected() {
        mBinding.ivSurprised.setCompoundDrawablesRelative(null, null, null, ContextCompat.getDrawable(this, R.drawable.selector_button_reaction));
        mBinding.ivLike.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivGrinRadio.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivAngry.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivHeart.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivTear.setCompoundDrawablesRelative(null, null, null, null);

    }

    private void tearSelected() {
        mBinding.ivTear.setCompoundDrawablesRelative(null, null, null, ContextCompat.getDrawable(this, R.drawable.selector_button_reaction));
        mBinding.ivLike.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivGrinRadio.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivAngry.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivHeart.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivSurprised.setCompoundDrawablesRelative(null, null, null, null);
    }

    private void grinSelected() {
        mBinding.ivGrinRadio.setCompoundDrawablesRelative(null, null, null, ContextCompat.getDrawable(this, R.drawable.selector_button_reaction));
        mBinding.ivLike.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivTear.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivAngry.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivHeart.setCompoundDrawablesRelative(null, null, null, null);
        mBinding.ivSurprised.setCompoundDrawablesRelative(null, null, null, null);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        ReactionByFragment likeFragment, heartFragment, surpiseFragment, tearFragment, grinFragment, angryFragment;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ReactionByFragment getCurrentFragment() {
            int position = mBinding.container.getCurrentItem();

            if (position == 0) {
                return likeFragment;
            } else if (position == 1) {
                return heartFragment;
            } else if (position == 2) {
                return angryFragment;
            } else if (position == 3) {
                return surpiseFragment;
            } else if (position == 4) {
                return tearFragment;
            } else {
                return grinFragment;
            }
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (likeFragment == null) {
                    return ReactionByFragment.newInstance(Constants.reactions.like, postId);
                }

                return likeFragment;
            } else if (position == 1) {
                if (heartFragment == null) {
                    return ReactionByFragment.newInstance(Constants.reactions.heart, postId);

                }
                return heartFragment;
            } else if (position == 2) {
                if (angryFragment == null) {
                    return ReactionByFragment.newInstance(Constants.reactions.angry, postId);

                }
                return angryFragment;
            } else if (position == 3) {
                if (surpiseFragment == null) {
                    return ReactionByFragment.newInstance(Constants.reactions.surprised, postId);

                }
                return surpiseFragment;
            } else if (position == 4) {
                if (tearFragment == null) {
                    return ReactionByFragment.newInstance(Constants.reactions.tear, postId);

                }
                return tearFragment;
            } else {
                if (grinFragment == null) {
                    return ReactionByFragment.newInstance(Constants.reactions.grin, postId);

                }
                return grinFragment;
            }
        }


        @Override
        public int getCount() {
            return 6;
        }
    }

}
