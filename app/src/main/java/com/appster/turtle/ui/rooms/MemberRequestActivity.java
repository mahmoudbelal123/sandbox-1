package com.appster.turtle.ui.rooms;

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
import com.appster.turtle.databinding.ActivityMemberRequestBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.ExplicitIntent;
import com.flipboard.bottomsheet.BottomSheetLayout;
/*
* Activity for request member
 */
public class MemberRequestActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMemberRequestBinding activityMemberBinding;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Room mRoom;
    private boolean isRefresh;
    private boolean isOnlyRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMemberBinding = DataBindingUtil.setContentView(this, R.layout.activity_member_request);
        mRoom = getIntent().getExtras().getParcelable(Constants.BundleKey.ROOM);
        initUI();
    }

    private void initUI() {
        setUpHeader(false, activityMemberBinding.header.clHeader, getString(R.string.member_cap), getString(R.string.add), R.drawable.back_arrow);
        activityMemberBinding.clSwitchTab.tvTab1.setText(R.string.Members);
        activityMemberBinding.clSwitchTab.tvTab2.setText(R.string.requests);
        activityMemberBinding.clSwitchTab.tvTab1.setOnClickListener(this);
        activityMemberBinding.clSwitchTab.tvTab2.setOnClickListener(this);

        activityMemberBinding.header.ivIconStart.setOnClickListener(this);
        activityMemberBinding.header.tvHeaderEnd.setEnabled(true);
        activityMemberBinding.header.tvHeaderEnd.setOnClickListener(this);

        isOnlyRoom = !mRoom.isUserAdmin() || getIntent().getExtras().getBoolean(Constants.BundleKey.IS_ONLY_MEMBERS, false);

        if (isOnlyRoom) {
            activityMemberBinding.clSwitchTab.getRoot().setVisibility(View.GONE);
            activityMemberBinding.header.tvHeaderEnd.setVisibility(View.INVISIBLE);
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        activityMemberBinding.container.setAdapter(mSectionsPagerAdapter);
        activityMemberBinding.container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        activityMemberBinding.clSwitchTab.tvTab2.setTextColor(ContextCompat.getColor(MemberRequestActivity.this, R.color.gray));
                        activityMemberBinding.clSwitchTab.tvTab1.setTextColor(ContextCompat.getColor(MemberRequestActivity.this, R.color.black));
                        activityMemberBinding.clSwitchTab.vTab2.setVisibility(View.INVISIBLE);
                        activityMemberBinding.clSwitchTab.vTab1.setVisibility(View.VISIBLE);

                        break;
                    case 1:
                        activityMemberBinding.clSwitchTab.tvTab1.setTextColor(ContextCompat.getColor(MemberRequestActivity.this, R.color.gray));
                        activityMemberBinding.clSwitchTab.tvTab2.setTextColor(ContextCompat.getColor(MemberRequestActivity.this, R.color.black));
                        activityMemberBinding.clSwitchTab.vTab2.setVisibility(View.VISIBLE);
                        activityMemberBinding.clSwitchTab.vTab1.setVisibility(View.INVISIBLE);

                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
//
            }
        });


    }

    public void refresh() {
        isRefresh = true;
    }

    public boolean isRefresh() {
        if (isRefresh) {
            isRefresh = false;
            return true;
        }
        return isRefresh;
    }

    public BottomSheetLayout getBottomSheet() {
        return activityMemberBinding.bottomSheetMembers;
    }

    @Override
    public String getActivityName() {
        return MemberRequestActivity.class.getName();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        MemberFragment purchaseFragment;
        RequestsFragment postFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (purchaseFragment == null) {
                    purchaseFragment = MemberFragment.newInstance(position, mRoom);
                }
                return purchaseFragment;
            } else {
                if (postFragment == null) {
                    postFragment = RequestsFragment.newInstance(position, mRoom);
                }
                return postFragment;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return isOnlyRoom ? 1 : 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    activityMemberBinding.clSwitchTab.tvTab2.setTextColor(ContextCompat.getColor(MemberRequestActivity.this, R.color.gray));
                    activityMemberBinding.clSwitchTab.tvTab1.setTextColor(ContextCompat.getColor(MemberRequestActivity.this, R.color.black));
                    return getString(R.string.my_friends_cap);
                case 1:
                    activityMemberBinding.clSwitchTab.tvTab1.setTextColor(ContextCompat.getColor(MemberRequestActivity.this, R.color.gray));
                    activityMemberBinding.clSwitchTab.tvTab2.setTextColor(ContextCompat.getColor(MemberRequestActivity.this, R.color.black));
                    return getString(R.string.request_cap);

                default:
                    break;

            }
            return null;
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_tab_2:

                activityMemberBinding.container.setCurrentItem(1);

                break;

            case R.id.tv_tab_1:
                activityMemberBinding.container.setCurrentItem(0);

                break;

            case R.id.tv_header_end:
                Bundle bun = getIntent().getExtras();
                bun.putBoolean(Constants.BundleKey.ROOM_REFRESH, true);
                ExplicitIntent.getsInstance().navigateForResult(MemberRequestActivity.this, AddMemberActivity.class, Constants.REQUEST_CODE.REQUEST_REFRESH, bun);
                break;

            case R.id.iv_icon_start:
                Intent i = new Intent();
                i.putExtra(Constants.BundleKey.ROOM_ID, mRoom.getRoomId());
                i.putExtra(Constants.BundleKey.ROOM_FROM_USER, true);
                setResult(RESULT_OK, i);
                finish();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
