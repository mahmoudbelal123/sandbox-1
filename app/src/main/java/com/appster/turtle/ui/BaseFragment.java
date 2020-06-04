/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.appster.turtle.util.LogUtils;
/*
* Base class for Base fragment
 */
@SuppressWarnings("ALL")
public abstract class BaseFragment extends Fragment {

    protected Handler handler = new Handler();
    protected boolean active;
    protected boolean alive;
    String TAG = "LIFE_CYCLE";
    private Toast toast;

    public abstract String getFragmentName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alive = true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alive = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void hideKeyboard(View view) {
        if (getActivity() != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    protected void showToast(String message) {
        if (getActivity() != null && message != null) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onDestroy() {
        alive = false;
        super.onDestroy();
    }

    public void showSnackBar(String message) {
        getBaseActivity().showSnackBar(message);
    }

    public void showSnackBar(String message, String buttonText, View.OnClickListener listener) {
        getBaseActivity().showSnackBar(message, buttonText, listener);
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void onStop() {
        super.onStop();
        hideKeyboard();
        hideHud();
        active = false;
    }

    protected void showProgressBar(String title, String message, View view, int delayTime) {
        try {
            if (isAlive() && getBaseActivity() != null)
                getBaseActivity().showProgressBar(title, message, view, delayTime);
        } catch (Exception e) {
            LogUtils.printStackTrace(e);

        }
    }

    protected void showProgressBar() {
        showProgressBar(null, null, null, 0);
    }

    public void showProgressBar(String msg) {
        showProgressBar(null, msg, null, 0);
    }

    protected void showProgressBar(int delayTime) {
        showProgressBar(null, null, null, delayTime);
    }

    public void showProgressBar(String msg, int delayTime) {
        showProgressBar(null, msg, null, delayTime);
    }

    public void hideHud() {
        try {
            if (getBaseActivity() != null)
                getBaseActivity().hideProgressBar();
        } catch (Exception e) {
            LogUtils.printStackTrace(e);

        }

    }

    public class ItemDecorationViewFrg extends RecyclerView.ItemDecoration {
        private int itemBottomOffset;
        private int itemTopOffset;
        private int itemStartOffset;
        private int itemEndOffset;

        public ItemDecorationViewFrg(Context context, int itemTopOffset, int itemBottomOffset) {

            this.itemTopOffset = itemTopOffset;
            this.itemBottomOffset = itemBottomOffset;
        }

        public ItemDecorationViewFrg(Context context, int itemTopOffset, int itemBottomOffset, int itemStartOffset, int itemEndOffset) {

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

}
