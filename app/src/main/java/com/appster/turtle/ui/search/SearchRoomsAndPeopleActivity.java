/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.search;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivitySearchRoomsAndPeopleBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;

/*
* Activity for Search room and peple
 */
public class SearchRoomsAndPeopleActivity extends BaseActivity implements View.OnClickListener {

    ActivitySearchRoomsAndPeopleBinding mBinding;
    private ViewPagerAdapter mViewPagerAdapter;
    private int mViewPagerCurrentPosition;

    private String mRoomsSearchText;
    private String mPeopleSearchText;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_rooms_and_people);
        initUI();
    }

    private void initUI() {

        mBinding.tvTabPeople.setOnClickListener(this);
        mBinding.tvTabRooms.setOnClickListener(this);
        mBinding.clSearchRoomsPeople.ivClose.setOnClickListener(this);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //set up view pager with its adapter
        mBinding.vpSearchRoomsPeople.setAdapter(mViewPagerAdapter);
        mBinding.vpSearchRoomsPeople.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    roomsTabSelected();
                } else {
                    peopleTabSelected();
                }
            }

            @Override
            public void onPageSelected(int position) {
                mViewPagerCurrentPosition = position;
                searchQueryChanged(mBinding.clSearchRoomsPeople.etSearch.getText().toString().trim());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//
            }
        });

        mBinding.vpSearchRoomsPeople.setOnClickListener(view -> hideKeyboard());

        mBinding.clSearchRoomsPeople.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchQueryChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
//
            }
        });
    }

    private void searchQueryChanged(CharSequence charSequence) {

        if (mViewPagerCurrentPosition == 1) {
            ((SearchPeopleFragment) mViewPagerAdapter.getItem(mViewPagerCurrentPosition)).setCurrentPage(1);
            ((SearchPeopleFragment) mViewPagerAdapter.getItem(mViewPagerCurrentPosition)).searchPeople(String.valueOf(charSequence));
        } else {
            ((SearchRoomsFragment) mViewPagerAdapter.getItem(mViewPagerCurrentPosition)).setCurrentPage(1);
            ((SearchRoomsFragment) mViewPagerAdapter.getItem(mViewPagerCurrentPosition)).searchRooms(String.valueOf(charSequence));
        }
    }

    private void roomsTabSelected() {
        hideKeyboard();

        mBinding.vRoomsFooter.setVisibility(View.VISIBLE);
        mBinding.vPeopleFooter.setVisibility(View.GONE);

        mBinding.tvTabRooms.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        mBinding.tvTabPeople.setTextColor(ContextCompat.getColor(this, R.color.gray));
    }

    private void peopleTabSelected() {
        hideKeyboard();

        mBinding.vPeopleFooter.setVisibility(View.VISIBLE);
        mBinding.vRoomsFooter.setVisibility(View.GONE);

        mBinding.tvTabRooms.setTextColor(ContextCompat.getColor(this, R.color.gray));
        mBinding.tvTabPeople.setTextColor(ContextCompat.getColor(this, android.R.color.white));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_tab_rooms:
                mBinding.vpSearchRoomsPeople.setCurrentItem(0);
                roomsTabSelected();
                break;

            case R.id.tv_tab_people:
                mBinding.vpSearchRoomsPeople.setCurrentItem(1);
                peopleTabSelected();
                break;

            case R.id.iv_close:
                mBinding.clSearchRoomsPeople.etSearch.setText("");
                break;

            default:
                break;
        }
    }

    public void setRoomsSearchQuery(String roomsSearchText) {
        mRoomsSearchText = roomsSearchText;
    }

    public String getRoomsSearchQuery() {
        return mRoomsSearchText != null ? mRoomsSearchText : "";
    }

    public void setPeopleSearchQuery(String peopleSearchText) {
        mPeopleSearchText = peopleSearchText;
    }

    public String getPeopleSearchQuery() {
        return mPeopleSearchText != null ? mPeopleSearchText : "";
    }

    public String getCurrentSearchText() {
        return mBinding.clSearchRoomsPeople.etSearch.getText().toString().trim();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        SearchRoomsFragment searchRoomsFragment;
        SearchPeopleFragment searchPeopleFragment;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {

                if (searchRoomsFragment == null) {
                    searchRoomsFragment = SearchRoomsFragment.newInstance(position);
                }

                return searchRoomsFragment;

            } else {

                if (searchPeopleFragment == null) {
                    searchPeopleFragment = SearchPeopleFragment.newInstance(position);
                }

                return searchPeopleFragment;
            }
        }

        public BaseFragment getCurrentFragment() {

            int position = mBinding.vpSearchRoomsPeople.getCurrentItem();

            if (position == 0)
                return searchRoomsFragment;
            else
                return searchPeopleFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
