/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemAvatarViewBinding;
import com.appster.turtle.model.AvatarModel;
import com.appster.turtle.ui.signUp.ChooseAvatarActivity;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.appster.turtle.util.bindingadapters.CameraBindingAdapters;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by atul on 19/09/17.
 * To inject activity reference.
 * adapter of avatar
 */

public class AvatarAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private String imagePath = "";
    private ArrayList<AvatarModel> avatarModels;


    public AvatarAdapter(Context context, ArrayList<AvatarModel> avatarModels) {
        mContext = context;
        this.avatarModels = avatarModels;
        this.avatarModels.add(1, new AvatarModel(0, "", 1));
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshImage(String imagePath) {
        this.imagePath = imagePath;
        notifyDataSetChanged();

    }

    public ArrayList<AvatarModel> getAvatarModels() {
        return avatarModels;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ItemAvatarViewBinding viewBinding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_avatar_view, container, false);
        viewBinding.setAvatar(avatarModels.get(position));

        ((View) viewBinding.usrProfPic.getParent()).setTag(avatarModels.get(position).getImageType());
        if (avatarModels.get(position).getImageType() == 1) {

            if (imagePath.isEmpty()) {
                viewBinding.usrImgBg.setBorderWidth(Utils.dpToPx(mContext, 4));
                viewBinding.usrImgBg.setBorderColor(ContextCompat.getColor(mContext, R.color.black));
                setImage(R.drawable.ic_pp_upload, viewBinding.usrProfPic);
            } else
                viewBinding.usrProfPic.setVisibility(View.VISIBLE);

            viewBinding.usrProfPicI.setVisibility(View.INVISIBLE);
            setImageUrl(imagePath, viewBinding.usrProfPic);

            ((View) viewBinding.usrProfPic.getParent()).setOnClickListener(view -> {
                if ((int) view.getTag() == 1)
                    ((ChooseAvatarActivity) mContext).openGallery();
            });

        } else {

            if (avatarModels.get(position).getImageType() == 3) {
                viewBinding.usrImgBg.setBorderWidth(Utils.dpToPx(mContext, 2));
                viewBinding.usrImgBg.setBorderColor(ContextCompat.getColor(mContext, R.color.bright_teal));
                setImage(R.drawable.ic_pp_cred, viewBinding.usrImgBg);
                viewBinding.usrProfPicI.setVisibility(View.VISIBLE);
                viewBinding.usrProfPic.setVisibility(View.INVISIBLE);
                CameraBindingAdapters.loadImage(viewBinding.usrProfPicI, avatarModels.get(position).getValue(), avatarModels.get(position).getImageType());


            } else {
                setImage(R.drawable.ic_transparent, viewBinding.usrImgBg);
                viewBinding.usrImgBg.setBorderWidth(Utils.dpToPx(mContext, 0));
                viewBinding.usrProfPic.setBorderWidth(Utils.dpToPx(mContext, 2));
                viewBinding.usrProfPic.setBorderColor(ContextCompat.getColor(mContext, R.color.bright_teal));
                viewBinding.usrProfPicI.setVisibility(View.INVISIBLE);
                viewBinding.usrProfPic.setVisibility(View.VISIBLE);
                CameraBindingAdapters.loadImage(viewBinding.usrImgBg, avatarModels.get(position).getValue(), avatarModels.get(position).getImageType());

            }
        }

        container.addView(viewBinding.getRoot());
        return viewBinding.getRoot();
    }


    public AvatarModel getItem(int pos) {
        return avatarModels.get(pos);
    }

    private void setImageUrl(String imagePath, ImageView imageView) {
        try {
            int px = Utils.dpToPx(mContext, 99);
            GlideApp.with(mContext)
                    .load(Uri.parse(imagePath))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .override(px, px)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_pp_upload)
                    .into(imageView);
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
    }

    private void setImage(int resId, ImageView imageView) {
        imageView.setImageResource(resId);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return avatarModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}
