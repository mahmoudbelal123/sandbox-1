/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */
package com.appster.turtle.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.appster.turtle.R;

/**
 * Created by atul on 02/01/17.
 * Singleton class to manage start of activity and fragment all over the application
 */

@SuppressWarnings("ALL")
public class ExplicitIntent {

    private static ExplicitIntent sInstance;

    public static ExplicitIntent getsInstance() {
        if (sInstance == null)
            sInstance = new ExplicitIntent();
        return sInstance;
    }

    private ExplicitIntent() {

    }

    public void navigateTo(Activity activity, Class<?> aClass) {
        Intent intent = new Intent(activity, aClass);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void navigateTo(Activity activity, Class<?> aClass, Bundle bundle) {
        Intent intent = new Intent(activity, aClass);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void navigateTo(Activity activity, Class<?> aClass, int flag) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(flag);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void navigateTo(Activity activity, Class<?> aClass, Bundle bundle, int flag) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(flag);
        if (bundle != null)
            intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void navigateForResult(Activity activity, Class<?> aClass, int requestCode) {
        Intent intent = new Intent(activity, aClass);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void navigateForResult(Activity activity, Class<?> aClass, int requestCode, Bundle bundle) {
        Intent intent = new Intent(activity, aClass);
        if (bundle != null)
            intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void navigateForResult(Activity activity, Fragment fragment, Class<?> aClass, int requestCode) {
        Intent intent = new Intent(activity, aClass);
        fragment.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void navigateForResult(Activity activity, Fragment fragment, Class<?> aClass, int requestCode, Bundle bundle) {
        Intent intent = new Intent(activity, aClass);
        if (bundle != null)
            intent.putExtras(bundle);
        fragment.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void startService(Activity activity, Class<?> aClass, Bundle bundle) {
        Intent intent = new Intent(activity, aClass);
        if (bundle != null)
            intent.putExtras(bundle);
        activity.startService(intent);
    }

    public void startService(Context context, Class<?> aClass, Bundle bundle) {
        Intent intent = new Intent(context, aClass);
        intent.putExtras(bundle);
        context.startService(intent);
    }

}
