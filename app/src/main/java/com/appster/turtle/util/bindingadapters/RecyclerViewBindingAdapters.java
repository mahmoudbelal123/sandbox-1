/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.bindingadapters;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewBindingAdapters {

//    @BindingAdapter("adapters")
//    public static void bindAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
//        recyclerView.setAdapter(adapter);
//    }

    @BindingAdapter("scrollListener")
    public static void bindScrollListener(RecyclerView recyclerView, RecyclerView.OnScrollListener scrollListener) {
        recyclerView.addOnScrollListener(scrollListener);
    }

//    @BindingAdapter("linearLayoutManager")
//    public static void bindLinearLayoutManager(RecyclerView recyclerView, LinearLayoutManager linearLayoutManager) {
//        recyclerView.setLayoutManager(linearLayoutManager);
//    }

    @BindingAdapter("itemDecoration")
    public static void bindLinearItemDecoration(RecyclerView recyclerView, RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }


}
