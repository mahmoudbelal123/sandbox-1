/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemSearchPeopleMoreBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.search.SearchPeopleClickListner;
import com.appster.turtle.util.CustomTypefaceSpan;

import java.util.List;

/**
 * Created by rohan on 01/11/17.
 * adapter room search people
 */

public class SearchHomePeopleMoreAdapter extends RecyclerView.Adapter<SearchHomePeopleMoreAdapter.SeeMoreViewHolder> {

    private Context mContext;
    private List<User> mUsersList;
    private SearchPeopleClickListner mListner;

    public SearchHomePeopleMoreAdapter(Context context, List<User> roomsList, SearchPeopleClickListner mListner) {
        mContext = context;
        mUsersList = roomsList;
        this.mListner = mListner;
    }


    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public class SeeMoreViewHolder extends RecyclerView.ViewHolder {
        private ItemSearchPeopleMoreBinding searchBinding;

        private SeeMoreViewHolder(ItemSearchPeopleMoreBinding searchBinding) {
            super(searchBinding.getRoot());
            this.searchBinding = searchBinding;
        }

        public ItemSearchPeopleMoreBinding getBinding() {
            return searchBinding;
        }

    }

    @Override
    public SeeMoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemSearchPeopleMoreBinding mItemBinder = DataBindingUtil.bind(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_people_more, parent, false));

        return new SeeMoreViewHolder(mItemBinder);
    }

    @Override
    public void onBindViewHolder(final SeeMoreViewHolder holder, final int position) {
        if (holder instanceof SeeMoreViewHolder) {
            final User popularUser = mUsersList.get(holder.getAdapterPosition());

            if (popularUser != null) {
                final ItemSearchPeopleMoreBinding searchBinding = holder.getBinding();

                String fullName = popularUser.getFullName();
                String userName = " @" + popularUser.getUserName();
                String fullString = fullName + userName;

                Typeface fontBold = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.eina_01_semibold));
                Typeface fontNormal = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.eina_01_regular));

                SpannableStringBuilder SS = new SpannableStringBuilder(fullString);

                SS.setSpan(new CustomTypefaceSpan("", fontBold), 0, fullName.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                SS.setSpan(new CustomTypefaceSpan("", fontNormal), fullName.length(), fullString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                SS.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.username_color)), fullName.length(), fullString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                searchBinding.tvName.setText(SS);
                searchBinding.setUser(popularUser);

                searchBinding.tvCollage.setText(popularUser.getUniversityBaseModel().getDisplayName());

                searchBinding.ivAdd.setVisibility(View.VISIBLE);
                searchBinding.ivAccept.setVisibility(View.GONE);
                searchBinding.ivDecline.setVisibility(View.GONE);
                searchBinding.tvResponseLabel.setVisibility(View.GONE);

                if (popularUser.getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_PENDING) {

                    if (popularUser.isRequestedByMe()) {
                        searchBinding.ivAdd.setImageResource(R.drawable.ic_pending_req);
                    } else {
                        searchBinding.ivAdd.setVisibility(View.INVISIBLE);
                        searchBinding.ivAccept.setVisibility(View.VISIBLE);
                        searchBinding.ivDecline.setVisibility(View.VISIBLE);
                        searchBinding.tvResponseLabel.setVisibility(View.VISIBLE);
                    }


                } else if (popularUser.getUserReqStatus() == Constants.MyFriend.FRIEND_STATUS_NOT_CONNECTED ||
                        popularUser.getUserReqStatus() == Constants.MyFriend.STATUS_REJECTED) {

                    searchBinding.ivAdd.setImageResource(R.drawable.ic_add_grey);
                } else {
                    searchBinding.ivAdd.setImageResource(R.drawable.ic_accepted_req);

                }


                searchBinding.ivAdd.setOnClickListener(view -> mListner.onUserClick(searchBinding, popularUser, holder.getAdapterPosition()));

                searchBinding.ivAccept.setOnClickListener(view -> mListner.onInvite(searchBinding, popularUser, holder.getAdapterPosition(), true));

                searchBinding.ivDecline.setOnClickListener(view -> mListner.onInvite(searchBinding, popularUser, holder.getAdapterPosition(), false));

            }
        }

    }
}
