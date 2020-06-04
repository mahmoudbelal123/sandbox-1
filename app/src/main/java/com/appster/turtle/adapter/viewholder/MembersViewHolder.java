/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemMemberAddedBinding;
import com.appster.turtle.model.User;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.ui.rooms.RequestsFragment;
import com.appster.turtle.util.CustomTypefaceSpan;

/**
 * Created by rohantaneja on 12-Sep-2017
 * veiw holder for members
 */

public class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private BaseFragment fragment;
    private ItemMemberAddedBinding mBinding;
    private User mUser;
    private IMembers mListener;

    public MembersViewHolder(View itemView) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
        mBinding.clMemberAdded.setOnClickListener(this);
    }

    public MembersViewHolder(View itemView, BaseFragment fragment) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
        mBinding.clMemberAdded.setOnClickListener(this);
        this.fragment = fragment;
    }

    public void bindData(Context context, User user, IMembers listener) {
        Context mContext = context;
        mUser = user;
        mListener = listener;

        mBinding.setUser(user);

        String userNameAndUsernameString = mUser.getFullName() + " " + mUser.getName();
        SpannableString userNameAndUsernameSpannableString = new SpannableString(userNameAndUsernameString);

        Typeface einaSemiBoldTypeface = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.eina_01_semibold));

        userNameAndUsernameSpannableString.setSpan(new CustomTypefaceSpan("", einaSemiBoldTypeface), 0, mUser.getFullName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userNameAndUsernameSpannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.textcolor_room_name)), 0, mUser.getFullName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mBinding.tvUserName.setText(userNameAndUsernameSpannableString);

        mBinding.ivAccept.setVisibility(View.GONE);
        mBinding.ivDecline.setVisibility(View.GONE);

        if (fragment != null && fragment instanceof RequestsFragment) {

            mBinding.ivAccept.setVisibility(View.VISIBLE);
            mBinding.ivDecline.setVisibility(View.VISIBLE);

            mBinding.ivAccept.setOnClickListener(this);
            mBinding.ivDecline.setOnClickListener(this);

        }


        if (user.isAdmin()) {
            mBinding.tvUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.crown, 0);
        } else
            mBinding.tvUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cl_member_added:
                mListener.onMemberSelected(mUser.getUserId(), getAdapterPosition());
                break;
            case R.id.iv_accept:
                mListener.onMemberAccept(mUser.getUserId());
                break;
            case R.id.iv_decline:
                mListener.onMemberDecline(mUser.getUserId());
                break;
            default:
                break;
        }
    }

    public interface IMembers {
        void onMemberSelected(int userId, int pos);

        void onMemberAccept(int userId);

        void onMemberDecline(int userId);
    }

}
