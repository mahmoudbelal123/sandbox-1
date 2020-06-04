/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.locationlog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;

import com.appster.turtle.util.FileHandler;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
/**
 * A recevier class for Location
 */
public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        if (LocationResult.hasResult(intent)) {
            FileHandler fileHandler = new FileHandler("LocationLogs_Out.csv");

            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                // use the Location
                LogUtils.LOGD("Location", "Lat : " + location.getLatitude() + ", " + "Long : " + location.getLongitude());
                LogUtils.LOGD("Accuracy", "Accuracy level : " + location.getAccuracy());
                LogUtils.LOGD("Battery", "Battery level : " + getBatteryLevel(context) + "%");

                String priorityString = "";
                switch (PreferenceUtil.getPriority()) {
                    case LocationRequest.PRIORITY_LOW_POWER:
                        priorityString = "PRIORITY_LOW_POWER";
                        break;
                    case LocationRequest.PRIORITY_NO_POWER:
                        priorityString = "PRIORITY_NO_POWER";
                        break;
                    case LocationRequest.PRIORITY_HIGH_ACCURACY:
                        priorityString = "PRIORITY_HIGH_ACCURACY";

                        break;
                    case LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY:
                        priorityString = "PRIORITY_BALANCED_POWER_ACCURACY";

                        break;
                    default:
                        break;

                }
                fileHandler.appendLog(context, location, getBatteryLevel(context), priorityString);

            }
        }

    }


    public float getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }


}

