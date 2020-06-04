/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import android.app.Activity;
import android.app.Dialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Helper for Google Play services-related operations.
 */
public class PlayServicesUtils {

    public static boolean checkGooglePlaySevices(final Activity activity) {
        final int googlePlayServicesCheck = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        switch (googlePlayServicesCheck) {
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCheck, activity, 0);
                dialog.setOnCancelListener(dialogInterface -> activity.finish());
                dialog.show();
        }
        return false;
    }

}
