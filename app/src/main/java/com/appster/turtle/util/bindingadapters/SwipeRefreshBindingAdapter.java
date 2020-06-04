/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.bindingadapters;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by atul on 05/09/17.
 * To inject activity reference.
 */

public class SwipeRefreshBindingAdapter {

    @BindingAdapter("refreshListener")
    public static void bindRefreshListener(SwipeRefreshLayout refreshLayout, SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        refreshLayout.setOnRefreshListener(onRefreshListener);
    }

    @BindingAdapter("isRefreshing")
    public static void bindIsRefreshing(SwipeRefreshLayout refreshLayout, boolean isRefreshing) {
        refreshLayout.setRefreshing(isRefreshing);
    }

}
