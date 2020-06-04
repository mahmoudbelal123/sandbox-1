/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.appster.turtle.R;
import com.appster.turtle.model.Interest;
import com.appster.turtle.widget.CustomTextView;

import java.util.ArrayList;
/*
 * adapter for interest
 */
@SuppressWarnings("ALL")
public class InterestAdapter extends ArrayAdapter<Interest> {

    private ArrayList<Interest> items = new ArrayList<>();
    private Context context;

    public InterestAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Interest getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_interest_dropdown, null);

        }
        ((CustomTextView) convertView).setText(items.get(position).getValue());

        convertView.setPadding(20, 20, 20, 20);
        return convertView;
    }

    public void addAll(ArrayList<Interest> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ((TextView) convertView).setText(items.get(position).getValue());
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {

        return mFilter;
    }


    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            InterestAdapter.super.clear();
            InterestAdapter.super.addAll(items);
            notifyDataSetChanged();
        }
    };

}

