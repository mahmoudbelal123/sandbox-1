package com.appster.turtle.ui.media;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityRectangleCropperBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;

import java.io.File;
/**
 * A activity class for Rectangle cropper
 */
public class RectangleCropperActivity extends BaseActivity {

    private ActivityRectangleCropperBinding binding;
    private Uri destinationUri;
    private boolean isSquare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(RectangleCropperActivity.this, R.layout.activity_rectangle_cropper);

        if (getIntent().getExtras() == null || getIntent().getExtras().getString(Constants.BundleKey.IMAGE_URL) == null) {
            setResult(RESULT_CANCELED);
            return;
        }


        Uri uri = Utils.getUri(RectangleCropperActivity.this, new File(getIntent().getExtras().getString(Constants.BundleKey.IMAGE_URL)));

        destinationUri = Utils.getUri(RectangleCropperActivity.this, Constants.CROPPED_IMAGE_URI);

        binding.cropView.setImageUri(uri);

        binding.cropView.setCropSaveCompleteListener(bitmapUri -> {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.BundleKey.IMAGE_URL, bitmapUri.toString());
            Intent i = new Intent();
            i.putExtras(bundle);
            setResult(RESULT_OK, i);
            finish();


        });

        binding.cropView.setErrorListener(e -> {
            setResult(RESULT_CANCELED);
            finish();
        });

    }

    @Override
    public String getActivityName() {
        return CropperActivity.class.getName();
    }

    public void onDoneClicked(View view) {

        LogUtils.LOGD("CROPPED DEST", destinationUri.toString());

        binding.cropView.crop(new CropIwaSaveConfig.Builder(destinationUri)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setQuality(100) //Hint for lossy compression formats
                .build());


    }
}
