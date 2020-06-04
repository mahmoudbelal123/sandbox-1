
/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.Display;
import android.view.View;

import com.appster.turtle.BuildConfig;
import com.appster.turtle.widget.ColorGradientDrawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utils for app
 */
public class Utils {
    public static final String TAG = "Utils";

    @Nullable
    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return context.getDrawable(drawableId);

        //noinspection deprecation
        return context.getResources().getDrawable(drawableId);
    }


    public static boolean isSimulator() {
        boolean isSimulator = "google_sdk".equals(Build.PRODUCT)
                || "vbox86p".equals(Build.PRODUCT)
                || "sdk".equals(Build.PRODUCT);
        LogUtils.LOGD(TAG, "Build.PRODUCT= " + Build.PRODUCT + "  isSimulator= "
                + isSimulator);

        return isSimulator;
    }

    public static int dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static CharSequence getTransformation(CharSequence source, View view) {
        PasswordTransformationMethod mt = PasswordTransformationMethod.getInstance();
        Field dotField;
        try {
            dotField = PasswordTransformationMethod.class.getDeclaredField("DOT");
            dotField.setAccessible(true);
            dotField.set(null, '\u25cf');
//            dotField.set(null,'\u2b24');
            return mt.getTransformation(source, view);

        } catch (Exception e) {
            LogUtils.printStackTrace(e);
            return null;
        }
    }

    public static String getDayOfMonthSuffix(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }

        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }


    public static String convertLocalTimeToUTCString(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(calendar.getTime());
    }

    public static Uri getUri(Context context, File file, boolean isFileProvider) {
        if (isFileProvider) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        }
        return Uri.fromFile(file);

    }

    public static Uri getUri(Context context, File file) {
        return getUri(context, file, false);
    }


    public static String getDOBFromMillis(long millis) {

        Date date = new Date(millis);
        DateFormat format = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
        format.setTimeZone(TimeZone.getDefault());//your zone
        formatted = format.format(date);
        return formatted;
    }

    public static String getYear(String yy) {
//        try {
//            DateFormat formatter = new SimpleDateFormat("yy");
//            Date date = null;
//            date = (Date) formatter.parse(yy);
//            formatter = new SimpleDateFormat("yyyy");
//            return formatter.format(date);
//
//        } catch (Exception e) {
//            LogUtils.printStackTrace(e);
//        }
        return 20 + yy;
    }

    public static boolean compareYear(String year) {
        try {

            DateFormat formatter = new SimpleDateFormat("yyyy", Locale.getDefault());
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(formatter.parse(year));

            return calendar.get(Calendar.YEAR) >= (Calendar.getInstance().get(Calendar.YEAR));

        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
        return false;
    }

    public static String getDOB(Date date) {

        DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        return format.format(date);
    }

    public static String getBase64(String path) {
        Bitmap bm = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static ColorGradientDrawable getBackgroundDrawable(int color) {
        ColorGradientDrawable background = new ColorGradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        background.setColor(color);
        return background;

    }

    public static float pxFromDp(float dp, Context mContext) {
        return dp * mContext.getResources().getDisplayMetrics().density;
    }


    public static String getIntK(int num) {
        double dNum = num;
        DecimalFormat format = new DecimalFormat("0.#");
//        System.out.println(format.format(price));
        if (num >= 1000) {
            dNum = dNum / 1000f;
        }

        return format.format(dNum);
    }


    public static long getDate(String dateStr) {
//        03-07-2018 04:02:20
        try {
            SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            return df.parse(dateStr).getTime();

        } catch (ParseException e) {
            LogUtils.LOGD(TAG, e.getMessage());
            return 0l;
        }
    }

    public static String getLocaleDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public static String changeTime(String dateStr) {
        long difference = Calendar.getInstance().getTime().getTime() - getDate(dateStr);

        long second = difference / 1000;
        int week = (int) (second / (24 * 60 * 60 * 7));
        int day = (int) (second / (24 * 60 * 60));
        int hour = (int) (second / (60 * 60));
        int min = (int) (second / 60);
        String time;
        if (week >= 1) {
            time = getLocaleDate(getDate(dateStr));
        } else if (day >= 1) {
            time = day + "d";
        } else if (hour >= 1) {
            time = hour + "h";
        } else if (min >= 1) {
            time = min + "m";
        } else if (second > 0) {
            time = second + "sec";
        } else {
            time = 0 + "sec";
        }
        return time;
    }


    public static String deeplinkRoom(int roomId) {
        return deeplink(1, roomId);
    }

    public static String deeplinkPost(int postId) {
        return deeplink(1, postId);
    }

    public static String deeplinkComment(int postId) {
        return deeplink(1, postId);
    }

    private static String deeplink(int filter, int roomId) {

        return "link?filter=" + filter + "&id=" + roomId;
    }

}
