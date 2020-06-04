package com.appster.turtle.adapter.viewholder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import com.appster.turtle.databinding.ItemCameraBottomBinding;
import com.appster.turtle.model.Event;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.Utils;
import com.otaliastudios.cameraview.CameraView;

import org.greenrobot.eventbus.EventBus;
/*
 * view holder of camera
 */
public class CameraBottomViewHolder extends RecyclerView.ViewHolder {

    ItemCameraBottomBinding bottomBinding;
    public static CameraView cameraView; //NOSONAR


    public CameraBottomViewHolder(Activity activity, ItemCameraBottomBinding bottomBinding) {
        super(bottomBinding.getRoot());
        this.bottomBinding = bottomBinding;
        cameraView = bottomBinding.camera;
        RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) bottomBinding.camera.getLayoutParams();
        layoutParam.width = (Utils.getScreenWidth(activity) / 3) * 2;
        layoutParam.height = (Utils.getScreenWidth(activity) / 3) * 2;
        bottomBinding.camera.setLayoutParams(layoutParam);

        bottomBinding.overlay.setOnClickListener(view -> EventBus.getDefault().post(new Event(Constants.eventI.ON_CAMERA_CLICK)));


    }


    public ItemCameraBottomBinding getBottomBinding() {
        return bottomBinding;
    }


}
