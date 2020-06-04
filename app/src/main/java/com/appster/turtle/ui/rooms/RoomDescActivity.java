package com.appster.turtle.ui.rooms;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityRoomDescBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;


public class RoomDescActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityRoomDescBinding activityRoomDescBinding = DataBindingUtil.setContentView(this, R.layout.activity_room_desc);

        activityRoomDescBinding.tvHead.setText(getIntent().getStringExtra(Constants.ROOM_NAME_KEY));
        activityRoomDescBinding.tvDesc.setText(getIntent().getStringExtra(Constants.ROOM_DESC_KEY));

        activityRoomDescBinding.ivClose.setOnClickListener(view -> finish());


    }

    @Override
    public String getActivityName() {
        return RoomDescActivity.class.getName();
    }
}
