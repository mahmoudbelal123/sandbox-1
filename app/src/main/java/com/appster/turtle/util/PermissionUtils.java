/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;

public class PermissionUtils {

    private PermissionUtils permissionUtils;
    private BaseActivity mActivity;

    public PermissionUtils(BaseActivity activity) {

        mActivity = activity;
    }


    /**
     * Callback Request Code - Constants.PERMISSION_REQUEST.REQUEST_MEDIA_PERMISSION
     *
     * @return
     */
    public boolean checkForMediaPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {


            return true;

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, Constants.PERMISSION_REQUEST.REQUEST_MEDIA_PERMISSION);
        }
        return false;

    }


    /**
     * Callback Request Code - Constants.PERMISSION_REQUEST.REQUEST_MEDIA_PERMISSION
     *
     * @return
     */
    public boolean checkForImagePermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {


            return true;

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, Constants.PERMISSION_REQUEST.REQUEST_MEDIA_PERMISSION);
        }
        return false;

    }


    /**
     * Callback Request code - Constants.PERMISSION_REQUEST.REQUEST_ALL_PERMISSION
     *
     * @param permissions
     * @return
     */
    public boolean checkForPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.requestPermissions(permissions, Constants.PERMISSION_REQUEST.REQUEST_ALL_PERMISSION);
        }
        return false;

    }


}
