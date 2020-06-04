package com.appster.turtle.ui.friends;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityFriendListBinding;
import com.appster.turtle.ui.BaseActivity;
/**
 * A activity class for  friends list
 */
public class FriendListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFriendListBinding activityFriendListBinding;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private FriendListFragment myFriendsFragment, friendsRequestsFragment;
    private boolean isUserRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFriendListBinding = DataBindingUtil.setContentView(this, R.layout.activity_friend_list);
        initUI();
    }

    private void initUI() {
        setUpHeader(activityFriendListBinding.header.clHeader, getString(R.string.friends), "", R.drawable.ic_back_black);
        activityFriendListBinding.clSwitchTab.tvTab1.setText(R.string.my_friends);
        activityFriendListBinding.clSwitchTab.tvTab2.setText(R.string.requests);
        activityFriendListBinding.clSwitchTab.tvTab1.setOnClickListener(this);
        activityFriendListBinding.clSwitchTab.tvTab2.setOnClickListener(this);
        activityFriendListBinding.header.tvHeaderEnd.setText(getString(R.string.add));
        activityFriendListBinding.header.tvHeaderEnd.setBackground(getDrawable(R.drawable.round_accent_btn));
        activityFriendListBinding.header.tvHeaderEnd.setVisibility(View.VISIBLE);
        activityFriendListBinding.header.tvHeaderEnd.setEnabled(true);
        activityFriendListBinding.header.tvHeaderEnd.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.app_white));


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        activityFriendListBinding.container.setAdapter(mSectionsPagerAdapter);
        activityFriendListBinding.container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        activityFriendListBinding.clSwitchTab.tvTab2.setTextColor(ContextCompat.getColor(FriendListActivity.this, R.color.gray));
                        activityFriendListBinding.clSwitchTab.tvTab1.setTextColor(ContextCompat.getColor(FriendListActivity.this, R.color.black));
                        activityFriendListBinding.clSwitchTab.vTab2.setVisibility(View.INVISIBLE);
                        activityFriendListBinding.clSwitchTab.vTab1.setVisibility(View.VISIBLE);
                        activityFriendListBinding.header.tvHeaderEnd.setVisibility(View.VISIBLE);

                        break;
                    case 1:
                        activityFriendListBinding.clSwitchTab.tvTab1.setTextColor(ContextCompat.getColor(FriendListActivity.this, R.color.gray));
                        activityFriendListBinding.clSwitchTab.tvTab2.setTextColor(ContextCompat.getColor(FriendListActivity.this, R.color.black));
                        activityFriendListBinding.clSwitchTab.vTab2.setVisibility(View.VISIBLE);
                        activityFriendListBinding.header.tvHeaderEnd.setVisibility(View.INVISIBLE);
                        activityFriendListBinding.clSwitchTab.vTab1.setVisibility(View.INVISIBLE);

                        break;

                    default:
                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//
            }
        });


    }


    @Override
    public String getActivityName() {
        return FriendListActivity.class.getName();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (friendsRequestsFragment == null) {
                    friendsRequestsFragment = FriendListFragment.newInstance(position);
                }
                return friendsRequestsFragment;
            } else {
                if (myFriendsFragment == null) {
                    myFriendsFragment = FriendListFragment.newInstance(position);
                }
                return myFriendsFragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    activityFriendListBinding.clSwitchTab.tvTab2.setTextColor(ContextCompat.getColor(FriendListActivity.this, R.color.gray));
                    activityFriendListBinding.clSwitchTab.tvTab1.setTextColor(ContextCompat.getColor(FriendListActivity.this, R.color.black));
                    activityFriendListBinding.header.tvHeaderEnd.setVisibility(View.VISIBLE
                    );
                    return "MY_FRIENDS";
                case 1:
                    activityFriendListBinding.clSwitchTab.tvTab1.setTextColor(ContextCompat.getColor(FriendListActivity.this, R.color.gray));
                    activityFriendListBinding.clSwitchTab.tvTab2.setTextColor(ContextCompat.getColor(FriendListActivity.this, R.color.black));
                    activityFriendListBinding.header.tvHeaderEnd.setVisibility(View.INVISIBLE);
                    return "REQUEST";

            }
            return null;
        }
    }

    public void refreshFriends() {
        isUserRefresh = true;
    }

    public boolean isUserRefresh() {
        if (isUserRefresh) {
            isUserRefresh = false;
            return true;
        }
        return isUserRefresh;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_tab_2:
                activityFriendListBinding.container.setCurrentItem(1);
                break;

            case R.id.tv_tab_1:
                activityFriendListBinding.container.setCurrentItem(0);
                break;

            default:
                break;
        }
    }

    public void nextClicked(View view) {
        startActivity(new Intent(FriendListActivity.this, AddFriendsActivity.class));
    }
}
