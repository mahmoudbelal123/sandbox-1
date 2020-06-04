/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.R;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.BaseResponse;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.GetRoomResponse;
import com.appster.turtle.network.response.PostsResponse;
import com.appster.turtle.ui.activity.ActivitiesNotificationActivity;
import com.appster.turtle.ui.media.MediaBottomFragment;
import com.appster.turtle.ui.rooms.HomeFragment;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.ui.tutorial.TutorialActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.widget.CustomProgressBar;
import com.appster.turtle.widget.CustomTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;

import static com.appster.turtle.ui.Constants.FRAGMENTS;

/*
 * Base class for activity
 */
@SuppressWarnings("ALL")
public abstract class BaseActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 102321;
    protected final String TAG1 = "BASE_ACTIVITY";
    protected boolean mAlive;
    private boolean mActive;
    private ProgressBar mProgressDialog;
    public static int mActiveInstance; //NOSONAR
    private AtomicBoolean mRequestPermissionsInProcess = new AtomicBoolean();
    private Snackbar snackbar;

    private BroadcastReceiver mNotificationReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mRequestPermissionsInProcess.set(false);
        mActiveInstance++;
        mAlive = true;
        mActive = true;

        mNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showNotificationSnackbar(intent.getStringExtra(Constants.Notification.MESSAGE));
                PreferenceUtil.setNotificationCount(PreferenceUtil.getNotification() + 1);

                if (onNotificationGet != null) {
                    onNotificationGet.onNotificationGet();
                }
            }
        };
    }


    public static BaseFragment getFragment(FRAGMENTS fragmentId) {
        BaseFragment fragment = null;
        switch (fragmentId) {
            case TEST_FRAGMENT:
                break;

            case HOME_FRAGMENT:
                fragment = new HomeFragment();
                break;

            case MEDIA_BOTTOM_FRAGMENT:
                fragment = new MediaBottomFragment();
                break;
        }
        return fragment;
    }


    abstract public String getActivityName();

    public void showSnackBar(String message) {
        Alert.showSnackBar(findViewById(android.R.id.content), message);
    }

    public void showSnackBar(String message, String buttonText, View.OnClickListener listener) {
        Alert.showSnackBar(findViewById(android.R.id.content), message, buttonText, listener);
    }

    public boolean isActive() {
        return mActive;
    }

    public boolean isAlive() {
        return mAlive;
    }

    @Override
    protected void onDestroy() {
        hideProgressBar();
        mAlive = false;
        Alert.dismissSnackBar();
        mActiveInstance--;

        super.onDestroy();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mActive = true;
    }

    private void showNotificationSnackbar(String message) {
        // Create the Snackbar
        Snackbar notificationSnackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) notificationSnackbar.getView();
        layout.setPadding(0, 0, 0, 0);
        // Hide the default snackbar text
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view
        View snackView = LayoutInflater.from(BaseActivity.this).inflate(R.layout.notification_snackbar, null);
        // Configure the view
        ((CustomTextView) snackView.findViewById(R.id.tv_snackbar_message)).setText(message);

        // Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
        // Show the Snackbar
        notificationSnackbar.show();
        snackView.setOnClickListener(v
                -> ExplicitIntent.getsInstance().navigateTo(BaseActivity.this, ActivitiesNotificationActivity.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mActive = true;
    }

    @Override
    protected void onStop() {
        mActive = false;
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAlive = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationReceiver, new IntentFilter(Constants.Notification.EVENT_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationReceiver);
    }

    public void showProgressBar() {
        showProgressBar(null, getString(R.string.loading), null, 0);
    }

    public void showProgressBar(int delayTime) {
        showProgressBar(null, null, null, delayTime);
    }

    public void showProgressBar(String msg) {
        showProgressBar(null, msg, null, 0);
    }

    public void showProgressBar(String msg, int delayTime) {
        showProgressBar(null, msg, null, delayTime);
    }

    public void showProgressBar(final String title, final String msg, final View view, int delayTime) {
        if (mProgressDialog != null) {
            hideProgressBar();
        }
        if (delayTime > 0 && !isAlive()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    processToShowDialog(title, msg, view);
                }
            }, delayTime);
        } else if (isAlive()) {
            if (delayTime > 0) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        processToShowDialog(title, msg, view);
                    }
                }, delayTime);
            } else {
                processToShowDialog(title, msg, view);
            }
        }
    }

    private void processToShowDialog(String title, String msg, View view) {
//        try {
//            mProgressDialog = ProgressDialog.show(new ContextThemeWrapper(BaseActivity.this,
//                    android.R.style.Theme_Holo_Light), title, msg, true, false);
//            mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            if (view != null)
//                mProgressDialog.setContentView(view);
//            else {
//                mProgressDialog.setContentView(R.layout.progress_view);
//
//            }
//        } catch (Exception e) {
//            LogUtils.LOGE(TAG1, e.toString());
//        }

        ViewGroup layout = (ViewGroup) findViewById(android.R.id.content)
                .getRootView();

        mProgressDialog = new CustomProgressBar(BaseActivity.this, null, android.R.attr.progressBarStyle);
        mProgressDialog.setIndeterminate(true);

        mProgressDialog.setVisibility(View.VISIBLE);


        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout rl = new RelativeLayout(BaseActivity.this);

        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressDialog);

        layout.addView(rl, params);

    }

    public void hideProgressBar() {
        try {
            if (mProgressDialog != null && mProgressDialog.getVisibility() == View.VISIBLE)
                mProgressDialog.setVisibility(View.GONE);
            mProgressDialog = null;
        } catch (Exception x) {
            LogUtils.LOGE(TAG1, x.toString());
        }
    }


    //Show toast message and cancel if any previous toast is displaying.

    public void showToast(String message) {
        showSnackBar(message);
    }

    public void hideKeyboard() {
        try {
            hideKeyboard(getCurrentFocus());
        } catch (Exception e) {
            LogUtils.printStackTrace(e);

        }
    }


    public BaseFragment getFragment(String fragmentName) {
        return (BaseFragment) getSupportFragmentManager().findFragmentByTag(fragmentName);

    }

    public void hideKeyboard(View view) {
        try {
            if (view != null) {
                view.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);

        }
    }

    public Fragment pushFragment(FRAGMENTS fragmentId, Bundle args, int containerViewId, boolean addToBackStack, boolean shouldAdd, ANIMATION_TYPE animationType) {
        try {
            BaseFragment fragment = getFragment(fragmentId);
            if (fragment == null) return null;
            if (args != null)
                fragment.setArguments(args);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (animationType) {
                case DEFAULT:
                case SLIDE:
                    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    break;

                case FADE:
                    ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                    break;
                case NONE:
                    break;
                default:
                    break;
            }
            if (shouldAdd)
                ft.add(containerViewId, fragment, fragment.getFragmentName());
            else
                ft.replace(containerViewId, fragment, fragment.getFragmentName());
            if (addToBackStack)
                ft.addToBackStack(fragment.getFragmentName());
            if (shouldAdd)
                ft.commit();
            else
                ft.commitAllowingStateLoss();
            return fragment;
        } catch (Exception x) {
            LogUtils.printStackTrace(x);

        }
        return null;
    }

    public void popFragment(FRAGMENTS fragmentId) {
        try {
            BaseFragment fragment = getFragment(fragmentId);
            if (fragment == null) return;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(getFragment(fragmentId));
            ft.commit();
        } catch (Exception x) {
            LogUtils.printStackTrace(x);

        }
    }


    public Fragment pushFragment(FRAGMENTS fragmentId, Bundle args, int containerViewId, boolean addToBackStack) {
        return pushFragment(fragmentId, args, containerViewId, addToBackStack, false, ANIMATION_TYPE.DEFAULT);
    }

    public Fragment pushFragment(FRAGMENTS fragmentId, Bundle args, int containerViewId, boolean addToBackStack, ANIMATION_TYPE animationType) {
        return pushFragment(fragmentId, args, containerViewId, addToBackStack, false, animationType);
    }


    public enum ANIMATION_TYPE {
        SLIDE, SLIDE_FROM_BOTTOM, FADE, DEFAULT, NONE
    }

    public void logout() {
        PreferenceUtil.setUser(null);
        Intent intent = new Intent(BaseActivity.this, TutorialActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


        RetrofitRestRepository mRepository = ((ApplicationController) getApplicationContext()).provideRepository();
        Observable<BaseResponse> observable = mRepository.getApiService().logout();
        new BaseCallback<BaseResponse>(this, observable) {
            @Override
            public void onSuccess(BaseResponse response) {
                PreferenceUtil.resetButSaveEmailAndToken();

            }

            @Override
            public void onFail() {
                PreferenceUtil.resetButSaveEmailAndToken();


            }
        };


    }


    public void logoutAuth(String msg) {

        Alert.logoutAlert(BaseActivity.this, "", msg, "OK", false, new Runnable() {
            @Override
            public void run() {

                PreferenceUtil.resetButSaveEmailAndToken();
                Intent intent = new Intent(BaseActivity.this, TutorialActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Constants.BundleKey.AUTH_ERR, msg);
                startActivity(intent);
            }
        });


    }

    public void logout(View view) {

        Alert.createYesNoAlert(BaseActivity.this, "Logout", "Are you sure you want to logout?", new Alert.OnAlertClickListener() {
            @Override
            public void onPositive(DialogInterface dialog) {

                logout();
            }

            @Override
            public void onNegative(DialogInterface dialog) {

                dialog.dismiss();
            }
        }).show();

    }

    public void onBackClicked(View view) {
        onBackPressed();
    }

    public void menuClicked(View view) {
    }


    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText) {
        setUpHeader(constraintLayout, middleText, null, 0, 0, false);
    }

    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, boolean showBottomBar) {
        setUpHeader(constraintLayout, middleText, null, 0, 0, showBottomBar);
    }

    protected void setUpHeader(boolean topView, ConstraintLayout constraintLayout, String middleText, String endText) {
        setUpHeader(topView, constraintLayout, middleText, endText, 0, 0, false);
    }

    protected void setUpHeader(boolean topView, ConstraintLayout constraintLayout, String middleText, String endText, int startIcon) {
        setUpHeader(topView, constraintLayout, middleText, endText, startIcon, 0, false);
    }

    protected void setUpHeader(boolean topView, ConstraintLayout constraintLayout, String middleText) {
        setUpHeader(topView, constraintLayout, middleText, null, 0, 0, false);
    }

    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, String endText) {
        setUpHeader(constraintLayout, middleText, endText, 0, 0, false);
    }

    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, String endText, boolean showBottomBar) {
        setUpHeader(constraintLayout, middleText, endText, 0, 0, showBottomBar);
    }

    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, int endIcon) {
        setUpHeader(constraintLayout, middleText, null, 0, endIcon, false);
    }


    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, int endIcon, boolean showBottomBar) {
        setUpHeader(constraintLayout, middleText, null, 0, endIcon, showBottomBar);
    }

    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, String endText, int startIcon) {
        setUpHeader(constraintLayout, middleText, endText, startIcon, 0, false);
    }

    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, String endText, int startIcon, boolean showBottomBar) {
        setUpHeader(constraintLayout, middleText, endText, startIcon, 0, showBottomBar);
    }

    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, int startIcon, int endIcon) {
        setUpHeader(constraintLayout, middleText, null, startIcon, endIcon, false);
    }

    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, int startIcon, int endIcon, boolean showBottomBorder) {
        setUpHeader(constraintLayout, middleText, null, startIcon, endIcon, showBottomBorder);
    }

    protected void setUpHeader(ConstraintLayout constraintLayout, String middleText, String endText, int startIcon, int endIcon, boolean showBottomBorder) {

        if (!StringUtils.isNullOrEmpty(middleText)) {
            ((TextView) constraintLayout.findViewById(R.id.tv_header_center)).setText(middleText);
        } else {
            constraintLayout.findViewById(R.id.tv_header_center).setVisibility(View.INVISIBLE);
        }

        if (!StringUtils.isNullOrEmpty(endText)) {
            TextView textView = constraintLayout.findViewById(R.id.tv_header_end);
            textView.setText(endText);
        } else {
            constraintLayout.findViewById(R.id.tv_header_end).setVisibility(View.INVISIBLE);
        }

        if (startIcon != 0) {
            ((ImageView) constraintLayout.findViewById(R.id.iv_icon_start)).setImageResource(startIcon);
        } else {
            ImageView ivBack = constraintLayout.findViewById(R.id.iv_icon_start);
            ivBack.setImageResource(R.drawable.ic_back_arrow_white);

            ivBack.setOnClickListener(v ->
                    finish());
        }

        if (endIcon != 0) {
            ((ImageView) constraintLayout.findViewById(R.id.iv_icon_end)).setImageResource(endIcon);
        } else {
            constraintLayout.findViewById(R.id.iv_icon_end).setVisibility(View.INVISIBLE);
        }

        if (!showBottomBorder) {
            constraintLayout.findViewById(R.id.v_header_bottom).setVisibility(View.INVISIBLE);
        }

    }

    protected void setUpHeader(boolean topView, ConstraintLayout constraintLayout, String middleText, String endText, int startIcon, int endIcon, boolean showBottomBorder) {

        if (!topView)
            constraintLayout.findViewById(R.id.v_header_top).setVisibility(View.GONE);

        if (!StringUtils.isNullOrEmpty(middleText)) {
            ((TextView) constraintLayout.findViewById(R.id.tv_header_center)).setText(middleText);
        } else {
            constraintLayout.findViewById(R.id.tv_header_center).setVisibility(View.INVISIBLE);
        }

        if (!StringUtils.isNullOrEmpty(endText)) {
            TextView textView = constraintLayout.findViewById(R.id.tv_header_end);
            textView.setText(endText);
        } else {
            constraintLayout.findViewById(R.id.tv_header_end).setVisibility(View.INVISIBLE);
        }

        if (startIcon != 0) {
            ((ImageView) constraintLayout.findViewById(R.id.iv_icon_start)).setImageResource(startIcon);
        } else {
            ImageView ivBack = constraintLayout.findViewById(R.id.iv_icon_start);
            ivBack.setImageResource(R.drawable.back_arrow);

            ivBack.setOnClickListener(v -> finish());
        }

        if (endIcon != 0) {
            ((ImageView) constraintLayout.findViewById(R.id.iv_icon_end)).setImageResource(endIcon);
        } else {
            constraintLayout.findViewById(R.id.iv_icon_end).setVisibility(View.INVISIBLE);
        }

        if (!showBottomBorder) {
            constraintLayout.findViewById(R.id.v_header_bottom).setVisibility(View.INVISIBLE);
        }

    }


    public class ItemDecorationView extends RecyclerView.ItemDecoration {
        private int itemBottomOffset;
        private int itemTopOffset;
        private int itemStartOffset;
        private int itemEndOffset;

        public ItemDecorationView(Context context, int itemTopOffset, int itemBottomOffset) {

            this.itemTopOffset = itemTopOffset;
            this.itemBottomOffset = itemBottomOffset;
        }

        public ItemDecorationView(Context context, int itemTopOffset, int itemBottomOffset, int itemStartOffset, int itemEndOffset) {

            this.itemTopOffset = itemTopOffset;
            this.itemBottomOffset = itemBottomOffset;
            this.itemStartOffset = itemStartOffset;
            this.itemEndOffset = itemEndOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(itemStartOffset, itemTopOffset, itemEndOffset, itemBottomOffset);
        }

    }

    public DecimalFormat getNumberFormatter() {
        return new DecimalFormat("##.###");
    }

    public void openRoom(int roomId, final int pos) {
        showProgressBar();
        RetrofitRestRepository repository = ((ApplicationController) getApplicationContext()).provideRepository();
        new BaseCallback<GetRoomResponse>(BaseActivity.this, repository.getApiService()
                .getRoom(roomId)) {
            @Override
            public void onSuccess(GetRoomResponse response) {

                if (response != null && response.getData() != null) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.BundleKey.ROOM, response.getData());
                    bundle.putInt(Constants.BundleKey.ROOM_ID, response.getData().getRoomId());
                    bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);
                    bundle.putBoolean(Constants.BundleKey.FROM_NOTIFICATION, true);
                    bundle.putInt(Constants.POS, pos);
                    ExplicitIntent.getsInstance().navigateForResult(BaseActivity.this, RoomActivityCoordinator.class, Constants.REQUEST_CODE.ROOM_UPDATED, bundle);
                }

            }

            @Override
            public void onFail() {

                hideProgressBar();
            }
        };
    }


    public void checkPermissions(String[] permissions, PermissionI permissionI) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.permissionI = permissionI;
            checkPermissionInternal(permissions);

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermissionInternal(String[] permissions) {
        ArrayList<String> requestPerms = new ArrayList<String>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {//&& !userDeniedPermissionAfterRationale(permission)
                requestPerms.add(permission);
            }
        }
        if (requestPerms.size() > 0 && !mRequestPermissionsInProcess.getAndSet(true)) {
            //  We do not have this essential permission, ask for it
            requestPermissions(requestPerms.toArray(new String[requestPerms.size()]), REQUEST_PERMISSION);
            return true;
        }
        if (requestPerms.size() == 0) {
            mRequestPermissionsInProcess.set(false);
            permissionI.onPermissionGiven();
        }


        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, final String[] permissions, int[] grantResults) {
        int count = 0;
        if (requestCode == REQUEST_PERMISSION) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                final String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                } else if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    count++;
                }

            }

            if (count == permissions.length)
                permissionI.onPermissionGiven();
            else
                showRationale(permissions, R.string.permission_denied);


        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showRationale(final String[] permissions, int promptResId) {

        int count = 0;
        for (int i = 0; i < permissions.length; i++) {

            if (!shouldShowRequestPermissionRationale(permissions[i])) {

                count++;
            }

        }

        if (count == 0) {
            snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Please Grant Permissions",
                    Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", v -> {
                mRequestPermissionsInProcess.set(false);
                checkPermissions(permissions, permissionI);
            });
            snackbar.show();
        } else {
            snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Please Grant Permissions",
                    Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", v -> {
                mRequestPermissionsInProcess.set(false);

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            });
            snackbar.show();
            mRequestPermissionsInProcess.set(false);
        }
    }


    private PermissionI permissionI;

    public void setOnNotificationGet(OnNotificationGet onNotificationGet) {
        this.onNotificationGet = onNotificationGet;
    }

    private OnNotificationGet onNotificationGet;

    public interface PermissionI {

        public void onPermissionGiven();
    }


    public interface OnNotificationGet {

        public void onNotificationGet();

        public void onUnreadNotification();
    }


}



