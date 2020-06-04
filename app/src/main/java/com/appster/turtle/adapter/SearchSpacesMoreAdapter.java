/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemSearchBinding;
import com.appster.turtle.model.SearchRoomsNewModel;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.ui.search.SearchRoomClickListner;
import com.appster.turtle.util.ExplicitIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 01/11/17.
 * adapter for search spaces
 */

public class SearchSpacesMoreAdapter extends RecyclerView.Adapter<SearchSpacesMoreAdapter.SeeMoreViewHolder> {

    public List<SearchRoomsNewModel> getmRoomsList() {
        return mRoomsList;
    }

    public void setmRoomsList(List<SearchRoomsNewModel> mRoomsList) {
        this.mRoomsList = mRoomsList;
        notifyDataSetChanged();
    }

    private List<SearchRoomsNewModel> mRoomsList = new ArrayList<>();
    private SearchRoomClickListner mRoomClickListner;

    public SearchSpacesMoreAdapter(Context context,SearchRoomClickListner listner) {
        Context mContext = context;
        mRoomClickListner = listner;
    }


    @Override
    public int getItemCount() {
        return mRoomsList.size();
    }

    public class SeeMoreViewHolder extends RecyclerView.ViewHolder {
        private ItemSearchBinding searchBinding;

        private SeeMoreViewHolder(ItemSearchBinding searchBinding) {
            super(searchBinding.getRoot());
            this.searchBinding = searchBinding;
        }

        public ItemSearchBinding getBinding() {
            return searchBinding;
        }

    }

    @Override
    public SeeMoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemSearchBinding mItemBinder = DataBindingUtil.bind(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false));

        return new SeeMoreViewHolder(mItemBinder);
    }

    @Override
    public void onBindViewHolder(final SeeMoreViewHolder holder, int position) {
        if (holder instanceof SeeMoreViewHolder) {
            final SearchRoomsNewModel roomsModel = mRoomsList.get(position);

            if (roomsModel != null) {
                final ItemSearchBinding searchBinding = holder.getBinding();

                searchBinding.tvName.setText(roomsModel.getRoomName());
               // LogUtils.LOGD("isGlobal", roomsModel.isGlobalRoom() + " ");
                String type = roomsModel.isGlobalRoom() ? "Global" : "Campus";
                searchBinding.tvType.setText(type.concat("/").concat(roomsModel.getMembersCount() + " members"));

                searchBinding.ivAdd.setVisibility(View.VISIBLE);
                searchBinding.ivAccept.setVisibility(View.GONE);
                searchBinding.ivDecline.setVisibility(View.GONE);
                searchBinding.tvResponseLabel.setVisibility(View.GONE);

                if (roomsModel.getUserReqStatus() == Constants.UserAddedRoomStatus.PENDING) {
                    //request pending
                    if (roomsModel.isRequestedByMe()) {
                        searchBinding.ivAdd.setImageResource(R.drawable.ic_pending_req);
                    } else {
                        searchBinding.ivAdd.setVisibility(View.INVISIBLE);
                        searchBinding.ivAccept.setVisibility(View.VISIBLE);
                        searchBinding.ivDecline.setVisibility(View.VISIBLE);
                        searchBinding.tvResponseLabel.setVisibility(View.VISIBLE);
                    }

                } else if (roomsModel.getUserReqStatus() == Constants.UserAddedRoomStatus.ADDED) {

                    searchBinding.ivAdd.setImageResource(R.drawable.ic_accepted_req);
                } else {
                    searchBinding.ivAdd.setImageResource(R.drawable.ic_add_grey);

                }

                searchBinding.tvName.setOnClickListener(view -> {


                    Bundle bundle = new Bundle();

                    bundle.putInt(Constants.BundleKey.ROOM_ID, roomsModel.getRoomId());
                    bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);

                    ExplicitIntent.getsInstance().navigateTo((BaseActivity) view.getContext(), RoomActivityCoordinator.class, bundle);

                });

                searchBinding.getRoot().setOnClickListener(view -> {


                    Bundle bundle = new Bundle();

                    bundle.putInt(Constants.BundleKey.ROOM_ID, roomsModel.getRoomId());
                    bundle.putBoolean(Constants.BundleKey.ROOM_FROM_USER, true);

                    ExplicitIntent.getsInstance().navigateTo((BaseActivity) view.getContext(), RoomActivityCoordinator.class, bundle);

                });

                searchBinding.ivAccept.setOnClickListener(view -> mRoomClickListner.onInviteClick(searchBinding, roomsModel, holder.getAdapterPosition(), true));

                searchBinding.ivDecline.setOnClickListener(view -> mRoomClickListner.onInviteClick(searchBinding, roomsModel, holder.getAdapterPosition(), false));


                searchBinding.ivAdd.setOnClickListener(view -> mRoomClickListner.onRoomClick(searchBinding, roomsModel, holder.getAdapterPosition()));

            }
        }

    }
}
