package com.appster.turtle.adapter.viewholder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemMediaBottomBinding;
import com.appster.turtle.model.Event;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
/*
 * view holder of media
 */
public class MediaBottomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    ItemMediaBottomBinding mBinding;
    Activity mContext;

    public MediaBottomViewHolder(Activity context, ItemMediaBottomBinding mBinding) {
        super(mBinding.getRoot());
        mContext = context;
        this.mBinding = mBinding;

        mBinding.ivImage.setOnClickListener(this);
        RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) mBinding.ivImage.getLayoutParams();

        layoutParam.width = Utils.getScreenWidth(mContext) / 3;
        layoutParam.height = Utils.getScreenWidth(mContext) / 3;


        mBinding.ivImage.setLayoutParams(layoutParam);
    }

    public void setImage(final String uri) {

        ((View) mBinding.ivImage.getParent()).setTag(uri);


        if (uri == null)
            return;

        GlideApp.with(mContext)
                .load(new File(uri))
                .centerCrop()
                .placeholder(R.drawable.grey_bg)
                .into(mBinding.ivImage);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_image:

                View parent = ((View) view.getParent());
                EventBus.getDefault().post(new Event(Constants.eventI.ON_IMAGE_CLICK, (String) parent.getTag()));


                break;

            default:
                break;
        }
    }


}