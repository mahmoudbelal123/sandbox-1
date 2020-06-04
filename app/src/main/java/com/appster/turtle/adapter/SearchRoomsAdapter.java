/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.viewholder.searchrooms.SearchRoomsButtonViewHolder;
import com.appster.turtle.adapter.viewholder.searchrooms.SearchRoomsCategoryViewHolder;
import com.appster.turtle.adapter.viewholder.searchrooms.SearchRoomsRoomViewHolder;
import com.appster.turtle.databinding.ItemSearchRoomsButtonBinding;
import com.appster.turtle.databinding.ItemSearchRoomsCategoryHeaderBinding;
import com.appster.turtle.databinding.ItemSearchRoomsRoomBinding;
import com.appster.turtle.model.SearchRoomsModel;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.search.SearchRoomsAndPeopleActivity;
import com.appster.turtle.util.StringUtils;

import java.util.List;

/**
 * Created by rohan on 01/11/17.
 * adapter for room search
 */

public class SearchRoomsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SearchRoomsModel> mRoomsList;
    private boolean mShowMembersCount;

    public SearchRoomsAdapter(Context context, List<SearchRoomsModel> roomsList) {
        mContext = context;
        mRoomsList = roomsList;
    }

    public SearchRoomsAdapter(Context context, List<SearchRoomsModel> roomsList, boolean showMembersCount) {
        mShowMembersCount = showMembersCount;
        mContext = context;
        mRoomsList = roomsList;
    }

    @Override
    public int getItemViewType(int position) {
        if (!StringUtils.isNullOrEmpty(mRoomsList.get(position).getButtonText())) {
            return Constants.SearchExtraElementType.BUTTON;
        }
        if (!StringUtils.isNullOrEmpty(mRoomsList.get(position).getCategoryHead())) {
            return Constants.SearchExtraElementType.CATEGORY_HEADER;
        }

        return Constants.SearchExtraElementType.ROOM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Constants.SearchExtraElementType.BUTTON:
                ItemSearchRoomsButtonBinding mRoomsListButtonBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_search_rooms_button, parent, false);
                return new SearchRoomsButtonViewHolder(mRoomsListButtonBinding.getRoot());

            case Constants.SearchExtraElementType.CATEGORY_HEADER:
                ItemSearchRoomsCategoryHeaderBinding mRoomsListCategoryHeaderBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_search_rooms_category_header, parent, false);
                return new SearchRoomsCategoryViewHolder(mRoomsListCategoryHeaderBinding.getRoot());

            default:
                ItemSearchRoomsRoomBinding mRoomsListRoomBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_search_rooms_room, parent, false);
                return new SearchRoomsRoomViewHolder(mRoomsListRoomBinding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchRoomsRoomViewHolder) {
            ((SearchRoomsRoomViewHolder) holder).bindData(mRoomsList.get(position) , mContext);
        } else if (holder instanceof SearchRoomsCategoryViewHolder) {
            ((SearchRoomsCategoryViewHolder) holder).bindData(mRoomsList.get(position), position);
        } else if (holder instanceof SearchRoomsButtonViewHolder) {
            ((SearchRoomsButtonViewHolder) holder).bindData(mRoomsList.get(position), position);
        }

        holder.itemView.setOnTouchListener((view, motionEvent) -> {
            ((SearchRoomsAndPeopleActivity) mContext).hideKeyboard();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mRoomsList.size();
    }
}
