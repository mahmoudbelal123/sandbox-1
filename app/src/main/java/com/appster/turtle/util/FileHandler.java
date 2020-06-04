/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import android.app.ActivityManager;
import android.content.Context;
import android.location.Location;
import android.os.Environment;

import com.appster.turtle.ui.BaseActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileHandler {

    private static final String TAG = FileHandler.class.getSimpleName();
    private static final String DIRECTORY_NAME = "LocationLog";
    private static final String COMMA = ",";
    final private File mFile;

    public FileHandler(String logFile) {

        File mediaDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), DIRECTORY_NAME);
        if (!mediaDir.exists()) {
            if (!mediaDir.mkdirs()) {
                LogUtils.LOGD("image upload", "Oops! Failed create " + DIRECTORY_NAME + " directory");
            }
        }
        mFile = new File(mediaDir.getPath() + File.separator + logFile);
        LogUtils.LOGD(TAG, mFile.getPath());
        if (!mFile.exists()) {
            try {
                mFile.createNewFile(); //NOSONAR
                addHeading();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LogUtils.printStackTrace(e);

            }
        }
    }

    public void addHeading() {
        BufferedWriter buf = null;
        FileWriter fileWriter = null;
        try {
            try {


                //BufferedWriter for performance, true to set append to file flag
                fileWriter = new FileWriter(mFile, true);
                buf = new BufferedWriter(fileWriter);
                buf.append("Type" + COMMA);
                buf.append("Time" + COMMA);
                buf.append("Priority" + COMMA);
                buf.append("Latitude" + COMMA + " Longitude" + COMMA);
                buf.append("Accuracy" + COMMA);
                buf.append("Battery");
                buf.newLine();
//                buf.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                LogUtils.printStackTrace(e);

            } finally {
                if (buf != null)
                    buf.close();
                if (fileWriter != null)
                    fileWriter.close();
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }

    }

    public void appendLog(Context context, Location location, float batteryLevel, String mPriority) {


        FileWriter fileWriter = null;
        BufferedWriter buf = null;

        try {
            try {

                SimpleDateFormat databaseDateTimeFormate = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
                String currentDateandTime = databaseDateTimeFormate.format(new Date());


                //BufferedWriter for performance, true to set append to file flag
                fileWriter = new FileWriter(mFile, true);
                buf = new BufferedWriter(fileWriter);
                if (BaseActivity.mActiveInstance != 0)
                    buf.append(isBackgroundRunning(context) ? "Background" : "Foreground").append(COMMA);
                else
                    buf.append("Killed" + COMMA);


                buf.append(currentDateandTime).append(COMMA);
                buf.append(mPriority).append(COMMA);
                buf.append(String.valueOf(location.getLatitude())).append(COMMA).append(String.valueOf(location.getLongitude())).append(COMMA);
                buf.append(String.valueOf(location.getAccuracy())).append(COMMA);
                buf.append(String.valueOf(batteryLevel)).append("");
                buf.newLine();


            } catch (Exception e) {
                // TODO Auto-generated catch block
                LogUtils.printStackTrace(e);

            } finally {
                if (buf != null)
                    buf.close();
                if (fileWriter != null)
                    fileWriter.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogUtils.printStackTrace(e);
        }
    }

    public static boolean isBackgroundRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            //If your app is the process in foreground, then it's not in running in background
                            return false;
                        }
                    }
                }
            }


            return true;
        }
        return false;
    }


}