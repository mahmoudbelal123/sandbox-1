/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;
import com.appster.turtle.R;
import com.appster.turtle.adapter.GalleryAdapter;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.media.CameraActivity;
import com.appster.turtle.util.ImagePickerUtils;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.Utils;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.Size;

import java.io.File;

public class CameraViewModel extends BaseObservable {

    private final CameraView camera;
    private BaseActivity activity;
    private boolean mCapturingPicture;
    private boolean mCapturingPictureWhileVideo;
    private boolean mCapturingVideo;

    // To show stuff in the callback
    private Size mCaptureNativeSize;
    private long mCaptureTime;
    private CountDownTimer timer;
    private boolean isVideoRecording;
    private SessionType sessionType = SessionType.PICTURE;
    private CameraPreviewModel cameraPreviewModel;
    private boolean isRecentImages;
    private long videoStartTime;
    private int cameraType;
    public static final int CAMERA_TYPE_VIDEO = 1;
    public static final int CAMERA_TYPE_PICTURE = 2;
    private boolean flash;

    public CameraViewModel(BaseActivity activity, CameraView camera, CameraPreviewModel cameraPreviewModel) {
        this.activity = activity;
        this.camera = camera;
        this.cameraPreviewModel = cameraPreviewModel;
        cameraType = CAMERA_TYPE_PICTURE;
        camera.setSessionType(sessionType);
    }

    @Bindable
    public CameraListener getCameraListener() {

        return new CameraListener() {

            @Override
            public void onCameraOpened(CameraOptions options) {

                LogUtils.LOGD("Camera", "CAMERA OPEN");
                if (isVideoRecording) {
                    setCameraType(CAMERA_TYPE_VIDEO);
                    captureVideo();
                }

            }

            @Override
            public void onCameraClosed() {

                LogUtils.LOGD("Camera", "CAMERA CLOSED");


            }

            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                mCapturingPicture = false;
                switchFlash(false);

                if (mCapturingVideo) {
                    mCapturingPictureWhileVideo = true;
                    Toast.makeText(activity, "Captured while taking video. Size=" + mCaptureNativeSize, Toast.LENGTH_SHORT).show();
                    cameraPreviewModel.setBytes(jpeg);
                    return;
                }

//                previewPicture(jpeg);
                cameraPreviewModel.setBytes(jpeg);
            }

            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                mCapturingVideo = false;
                switchFlash(false);

                if (mCapturingPictureWhileVideo) {
                    mCapturingPictureWhileVideo = false;
                    setSessionType(SessionType.PICTURE);
                    return;
                }
//                previewVideo(Uri.fromFile(video));
//                cameraPreviewModel.setUri(Uri.fromFile(video));
                cameraPreviewModel.setUri(Utils.getUri(activity, video));
            }
        };
    }

    public CameraPreviewModel getCameraPreviewModel() {
        return cameraPreviewModel;
    }

    public void capturePhoto() {
        if (mCapturingPicture) return;
        mCapturingPicture = true;
        mCaptureTime = System.currentTimeMillis();
        mCaptureNativeSize = camera.getCaptureSize();
        message("Capturing picture...", false);
        camera.capturePicture();
    }

    public void captureVideo() {
        if (camera.getSessionType() != SessionType.VIDEO) {
            message("Can't record video while session type is 'picture'.", false);
            return;
        }
        if (mCapturingPicture || mCapturingVideo) return;
        mCapturingVideo = true;
        message("Recording for 3 min...", true);
        File f = new File(Environment.getExternalStorageDirectory(), activity.getString(R.string.turtle_folder));
        if (!f.exists()) {
            f.mkdirs();
        }
        videoStartTime = System.currentTimeMillis();
        camera.startCapturingVideo(new File(f, "new.mp4"), Constants.VIDEO_LENGTH);
    }


    public View.OnClickListener toggleCameraClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View view) { //NOSONAR
            if (mCapturingPicture) return;
            switch (camera.toggleFacing()) {
                case BACK:
                    message("Switched to back camera!", false);
                    break;

                case FRONT:
                    message("Switched to front camera!", false);
                    break;
            }
        }
    };

    public View.OnClickListener galleryClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View view) { //NOSONAR
            ImagePickerUtils.addWithGif(false, activity.getSupportFragmentManager(), (CameraActivity) activity);
        }
    };

    public View.OnClickListener toggleFlashClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View view) {
            if (mCapturingPicture) return;
            switch (camera.getFlash()) {


                case OFF:
                    switchFlash(true);
                    break;
                case ON:
                    switchFlash(false);
                    break;
                case TORCH:
                    switchFlash(false);
                    break;


//
            }


        }
    };

    @Bindable
    public boolean isFlash() {
        return flash;
    }

    private void switchFlash(boolean isOn) {
        if (isOn) {
            camera.setFlash(Flash.TORCH);
            flash = true;

        } else {
            camera.setFlash(Flash.OFF);
            flash = false;

        }

        notifyPropertyChanged(BR.flash);
    }

    private void message(String content, boolean important) {
        int length = important ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
      //  LogUtils.LOGD(activity.getActivityName(), content);
    }

    @Bindable
    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {


        this.sessionType = sessionType;
        notifyPropertyChanged(BR.sessionType);
    }

    @Bindable
    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
    //    LogUtils.LOGD("Camera", "CAMERA TYPE" + (cameraType == CAMERA_TYPE_PICTURE ? " PICTURE" : " VIDEO"));

        this.cameraType = cameraType;
        notifyPropertyChanged(BR.cameraType);
    }

    public View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            public boolean actionUpFlag; //NOSONAR
            private float initialTouchX;
            private float initialTouchY;
            private long lastTouchDown;
            private int CLICK_ACTION_THRESHHOLD = 200;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        LogUtils.LOGD("Camera", "ACTION DOWN");
                        lastTouchDown = System.currentTimeMillis();
                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        timer = new CountDownTimer(300, 100) {
                            public void onTick(long millisUntilFinished) {


                            }

                            public void onFinish() {
      //                          LogUtils.LOGD("Camera", "300 ticker onfinish video session");
//                                cameraBinding.ivCapture.setImageResource(R.drawable.ic_capture_video);
                                setSessionType(SessionType.VIDEO);
                                isVideoRecording = true;


                            }
                        }.start();

                        return true;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        LogUtils.LOGD("Camera", "ACTION UP");

                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        timer.cancel();
                        if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD && (Xdiff < 10 && Ydiff < 10)) {
                            //clicked!!!
                            LogUtils.LOGD("Camera", "CLICK THRSHOLD");
                            capturePhoto();

                            isVideoRecording = false;

                            return true;
                        } else {

                            if (System.currentTimeMillis() - videoStartTime < 1500) {
                                LogUtils.LOGD("Camera", "Ticker 2000 click");

                                capturePhoto();

                                isVideoRecording = false;
                                return true;

                            }
                            LogUtils.LOGD("Camera", "CAMERA STOP");
                            isVideoRecording = false;

                            camera.stopCapturingVideo();

                            return true;
                        }


                }
                return false;
            }
        };


    }

    //NOSONAR
    public View.OnClickListener recentImageClickListener = view -> {

        setRecentImages(!isRecentImages());
        final Handler handler = new Handler();
        handler.postDelayed(() -> ((ImageView) view).setImageResource(isRecentImages() ? R.drawable.white_dash : R.drawable.ic_camera_roll), 300);


    };

    @Bindable
    public boolean isRecentImages() {
        return isRecentImages;
    }

    public void setRecentImages(boolean recentImages) {
        isRecentImages = recentImages;
        notifyPropertyChanged(BR.recentImages);
    }

    public GalleryAdapter getGalleryAdapter() {
        return new GalleryAdapter(activity);
    }

    @Bindable
    public LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

    }


}
