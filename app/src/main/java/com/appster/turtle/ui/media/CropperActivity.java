package com.appster.turtle.ui.media;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityCropperBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.config.InitialPosition;

import java.io.File;
/**
 * A activity class for cropper
 */
public class CropperActivity extends BaseActivity {

    private ActivityCropperBinding binding;
    private Uri destinationUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(CropperActivity.this, R.layout.activity_cropper);

        if (getIntent().getExtras() == null || getIntent().getExtras().getString(Constants.BundleKey.IMAGE_URL) == null) {
            setResult(RESULT_CANCELED);
            return;
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(Constants.BundleKey.IMAGE_CROPPER_IS_SQUARE)) {
            boolean isSquare = true;
        }

        Uri uri = Utils.getUri(CropperActivity.this, new File(getIntent().getExtras().getString(Constants.BundleKey.IMAGE_URL)));

        destinationUri = Utils.getUri(CropperActivity.this, Constants.CROPPED_IMAGE_URI);

        binding.cropView.setImageUri(uri);
        binding.cropView.setBackgroundColor(Color.WHITE);
        binding.cropView.setCropSaveCompleteListener(bitmapUri -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BundleKey.IMAGE_URL, bitmapUri.toString());
            Intent i = new Intent();
            i.putExtras(bundle);
            setResult(RESULT_OK, i);
            finish();


        });

        binding.cropView.configureImage()
                .setMinScale(0.2f)
                .setMaxScale(3f)
                .apply();

        binding.cropView.configureImage()
                .setScale(0.8f)
                .apply();

        binding.cropView.configureImage()
                .setImageInitialPosition(InitialPosition.CENTER_CROP)
                .apply();


        binding.cropView.setErrorListener(e -> showSnackBar(getString(R.string.please_try_again)));

    }

    @Override
    public String getActivityName() {
        return CropperActivity.class.getName();
    }

    public void onDoneClicked(View view) {

        LogUtils.LOGD("CROPPED DEST", binding.cropView + "");


        binding.cropView.crop(new CropIwaSaveConfig.Builder(destinationUri)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setQuality(100) //Hint for lossy compression formats
                .build());


    }
}
