/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.media;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityTextOnImageBinding;
import com.appster.turtle.model.Sticker;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Utils;
/**
 * A activity class for text on image
 */
public class TextOnImageActivity extends BaseActivity {
    private ActivityTextOnImageBinding binding;

    private Sticker sticker;
    private int position = 0;
    private boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sticker = new Sticker();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_text_on_image);
        binding.etImageText.requestFocus();
        binding.colorPickerView.setColorListener(color -> {

            if (color != 0) {
                if (!isFirst) {
                    binding.etImageText.setTextColor(color);
                    binding.ivCameraText.setBackground(Utils.getBackgroundDrawable(color));
                }
                isFirst = false;
            }
        });

        if (getIntent().getExtras() != null) {
            sticker = getIntent().getParcelableExtra(Constants.BundleKey.IMAGE_TEXT);
            position = getIntent().getIntExtra(Constants.BundleKey.IMAGE_TEXT_POS, -1);
            isFirst = true;
            binding.etImageText.setText(sticker.getText());
            binding.etImageText.setSelection(sticker.getText().length());
            binding.etImageText.setTextColor(sticker.getColor());
            binding.ivCameraText.setBackground(Utils.getBackgroundDrawable(sticker.getColor()));
        }

        binding.etImageText.setOnKeyboardHide(() -> onBackPressed());

        binding.etImageText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                onBackPressed();
                return true;
            }
            return false;
        });

    }


    @Override
    public void onBackPressed() {


        Bundle bundle = new Bundle();
        Sticker sticker = new Sticker();
        int color = binding.etImageText.getCurrentTextColor();
        sticker.setColor(color != 0 ? color : Color.BLACK);
        sticker.setText(binding.etImageText.getText().toString());

        bundle.putParcelable(Constants.BundleKey.IMAGE_TEXT, sticker);
        bundle.putInt(Constants.BundleKey.IMAGE_TEXT_POS, position);
        Intent i = new Intent();
        i.putExtras(bundle);

        setResult(RESULT_OK, i);
        finish();


    }


    @Override
    public String getActivityName() {
        return null;
    }
}
