/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.rooms;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityPrivacyBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
/*
* Activity for privacy
 */
public class PrivacyActivity extends BaseActivity {

    private ActivityPrivacyBinding mBinding;
    private int roomType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(PrivacyActivity.this, R.layout.activity_privacy);

        initUI();
    }

    private void initUI() {

        setUpHeader(false, mBinding.clHeader.clHeader, getString(R.string.privacy_header_small), "Done", R.drawable.back_arrow);

        mBinding.clHeader.ivIconStart.setImageResource(R.drawable.back_arrow);
        mBinding.clHeader.tvHeaderEnd.setEnabled(true);
        mBinding.clHeader.tvHeaderEnd.setOnClickListener(view -> saveNewPrivacy());

        Bundle b = getIntent().getExtras();
        roomType = b.getInt(Constants.BundleKey.ROOM_TYPE, 0);

        setRoomTypeSelected(roomType);

        mBinding.clRoomTypePrivate.ivRoomType.setOnClickListener(view -> setRoomTypeSelected(Constants.ROOM_TYPE.PRIVATE));

        mBinding.clRoomTypePublic.ivRoomType.setOnClickListener(view -> setRoomTypeSelected(Constants.ROOM_TYPE.PUBLIC));

        mBinding.clRoomTypeHidden.ivRoomType.setOnClickListener(view -> setRoomTypeSelected(Constants.ROOM_TYPE.HIDDEN));

    }

    private void setRoomTypeSelected(int roomType) {
        this.roomType = roomType;
        switch (roomType) {
            case 0:
                mBinding.clRoomTypePrivate.ivRoomType.setImageResource(R.drawable.circle_tick_stroke);
                mBinding.clRoomTypePublic.ivRoomType.setImageResource(R.drawable.circle_unticked);
                mBinding.clRoomTypeHidden.ivRoomType.setImageResource(R.drawable.circle_unticked);
                break;

            case 1:
                mBinding.clRoomTypePrivate.ivRoomType.setImageResource(R.drawable.circle_unticked);
                mBinding.clRoomTypePublic.ivRoomType.setImageResource(R.drawable.circle_tick_stroke);
                mBinding.clRoomTypeHidden.ivRoomType.setImageResource(R.drawable.circle_unticked);
                break;

            case 2:
                mBinding.clRoomTypePrivate.ivRoomType.setImageResource(R.drawable.circle_unticked);
                mBinding.clRoomTypePublic.ivRoomType.setImageResource(R.drawable.circle_unticked);
                mBinding.clRoomTypeHidden.ivRoomType.setImageResource(R.drawable.circle_tick_stroke);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        saveNewPrivacy();
    }

    private void saveNewPrivacy() {
        Bundle b = new Bundle();
        b.putInt(Constants.BundleKey.ROOM_TYPE, roomType);
        Intent i = new Intent();
        i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public String getActivityName() {
        return PrivacyActivity.class.getName();
    }
}
