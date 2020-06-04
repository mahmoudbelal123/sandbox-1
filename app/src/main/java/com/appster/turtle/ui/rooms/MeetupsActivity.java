/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.rooms;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityMeetupsBinding;
import com.appster.turtle.databinding.LayoutRoomOptionsBinding;
import com.appster.turtle.model.Room;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.viewmodel.RoomViewModel;
import com.flipboard.bottomsheet.BottomSheetLayout;
/*
* Activity for meet up
 */
public class MeetupsActivity extends BaseActivity {

    ActivityMeetupsBinding mBinding;
    private Room room;
    private RoomViewModel mRoomViewModel;

    public LayoutRoomOptionsBinding getEditBottomSheetBinding() {
        return editBottomSheetBinding;
    }

    public BottomSheetLayout getmBottomSheetMembers() {
        return mBottomSheetMembers;
    }

    private LayoutRoomOptionsBinding editBottomSheetBinding;
    private BottomSheetLayout mBottomSheetMembers;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            room = mBundle.getParcelable(Constants.BundleKey.ROOM);
        }

        initDataBinding();
        initUI();

    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, room.getRoomName(), 0, true);
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_meetups);
        mBottomSheetMembers = mBinding.bottomSheetMembers;
        editBottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(MeetupsActivity.this), R.layout.layout_room_options, mBinding.bottomSheetMembers, false);
        mRoomViewModel = new RoomViewModel(this, room);
        mBinding.setViewModel(mRoomViewModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRoomViewModel.offset = 1;
        mRoomViewModel.getPosts(true, true);
    }
}
