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
import android.text.Editable;
import android.text.TextWatcher;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityAddNameBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.StringUtils;
/*
* Activity for Add name
 */
public class AddNameActivity extends BaseActivity implements TextWatcher {
    private ActivityAddNameBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(AddNameActivity.this, R.layout.activity_add_name);

        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.clHeader.clHeader, getString(R.string.edit_room_name), R.drawable.ic_back_half_arrow, 0);
        Bundle b = getIntent().getExtras();
        String roomName = b.getString(Constants.BundleKey.ROOM_NAME);

        mBinding.tvName.setText(roomName);
        mBinding.tvName.addTextChangedListener(this);


        mBinding.clHeader.ivIconStart.setOnClickListener(view -> saveNewRoomName());

    }


    @Override
    public String getActivityName() {
        return AddNameActivity.class.getName();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (mBinding.tvName.getText().toString().trim().length() > 1) {
            mBinding.vSearchFooter.setBackgroundResource(R.color.bright_teal);

        } else {
            mBinding.vSearchFooter.setBackgroundResource(R.color.edit_text_footer_grey);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
//
    }

    @Override
    public void onBackPressed() {
        saveNewRoomName();
    }

    private boolean isValidRoomName() {
        return mBinding.tvName.getText().toString().trim().length() >= 1
                && mBinding.tvName.getText().toString().trim().length() <= 20;
    }

    private void saveNewRoomName() {
        if (mBinding.tvName.getText().toString().isEmpty()) {

            StringUtils.displaySnackBar(mBinding.tvName, "Please enter a Room Name");
            return;
        }

        if (!isValidRoomName()) {
            StringUtils.displaySnackBar(mBinding.tvName, "Room name length too long");
            return;
        }

        Bundle b = new Bundle();
        //convert first letter to caps
        String etRoomName = mBinding.tvName.getText().toString().trim();
        b.putString(Constants.BundleKey.ROOM_NAME, etRoomName.substring(0, 1).toUpperCase() + etRoomName.substring(1));
        Intent i = new Intent();
        i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();
    }
}
