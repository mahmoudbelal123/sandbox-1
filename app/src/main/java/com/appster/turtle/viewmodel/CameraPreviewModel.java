/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.viewmodel;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.appster.turtle.ApplicationController;
import com.appster.turtle.BR;
import com.appster.turtle.R;
import com.appster.turtle.model.Sticker;
import com.appster.turtle.model.Tag;
import com.appster.turtle.model.User;
import com.appster.turtle.network.BaseCallback;
import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.network.response.PostsResponse;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.media.AddCaptionActivity;
import com.appster.turtle.ui.media.CameraActivity;
import com.appster.turtle.ui.media.TextOnImageActivity;
import com.appster.turtle.util.Alert;
import com.appster.turtle.util.BitmapUtils;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.MediaUtil;
import com.appster.turtle.util.Utils;
import com.appster.turtle.widget.ColorGradientDrawable;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.otaliastudios.cameraview.CameraUtils;
import com.xiaopo.flying.sticker.TextSticker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.appster.turtle.util.LogUtils.LOGE;

@SuppressWarnings("ALL")
public class CameraPreviewModel extends BaseObservable {

    private final BaseActivity activity;
    private byte[] bytes;
    private Bitmap bitmap;
    private Uri uri;
    private boolean preview;
    private boolean textEditing;
    private RetrofitRestRepository repository;
    private FFmpeg ffmpeg;
    private String textToAdd;
    private int color;
    private String imageText;
    private String caption = "";
    private File videoMergedFile;
    private File gifMergedFile;
    private File palleteMergedFile;

    private String mPostMessage;
    private boolean isGif;

    public CameraPreviewModel(BaseActivity activity) {
        this.activity = activity;
        this.repository = ((ApplicationController) activity.getApplicationContext()).provideRepository(false);
        ffmpeg = FFmpeg.getInstance(activity);
        loadFFMpegBinary();
    }


    @Bindable
    public boolean getPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
        notifyPropertyChanged(BR.preview);
    }

    @Bindable
    public boolean getTextEditing() {
        return textEditing;
    }

    public void setTextEditing(boolean textEditing) {
        this.textEditing = textEditing;
        notifyPropertyChanged(BR.textEditing);
    }


    @Bindable
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
        notifyPropertyChanged(BR.caption);
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
        setPreview(true);
        previewPicture();
    }


    /**
     * Preview Image
     */
    private void previewPicture() {
        CameraUtils.decodeBitmap(bytes, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {

                setBitmap(bitmap);


            }
        });
    }


    @Bindable
    public Bitmap getBitmap() {
        return resize(bitmap);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;

        if (bitmap != null)
            ((CameraActivity) activity).setStickerViewSize(bitmap.getWidth(), bitmap.getHeight());
        notifyPropertyChanged(BR.bitmap);
    }


    @Bindable
    public Uri getUri() {
        return uri;
    }

    /**
     * Set video or gif URI and update bitmap if image
     *
     * @param uri
     */
    public void setUri(Uri uri) {

        if (uri != null && (uri.toString().contains("jpeg") || uri.toString().contains("jpg") || uri.toString().contains("png"))) {

            isGif = false;

            bitmap = getBitmapFromPath(uri.getPath());
            if (bitmap != null) {
                setPreview(true);
                setBitmap(bitmap);
                return;
            }


        }
//        else  if (uri != null && (uri.toString().contains("gif")))
//        {
//            GlideApp.with(activity)
//                    .load(new File(uri.getPath()))
//                    .centerCrop()
//                    .placeholder(R.drawable.id_babble)
//                    .into();
//
//        }


        this.uri = uri;
        if (uri != null && uri.toString().contains("gif")) {
            isGif = true;
            setPreview(true);
            convertGIFtoVideo();
        } else {
            isGif = false;

            if (uri != null) {


                if (!MediaUtil.isVideoDurationCorrect(activity, getUri())) {

                    cropVideo();
                } else {

                    setPreview(true);
                    notifyPropertyChanged(BR.uri);
                }
            } else {
                setPreview(true);
                notifyPropertyChanged(BR.uri);
            }
        }


    }

    private void cropVideo() {
        videoMergedFile = new File(Environment.getExternalStorageDirectory() + "/video" + System.currentTimeMillis() + ".mp4");

//        y -i -ss 00:00:00.00 input_path -vcodec copy -acodec copy -t 00:00:00.00 -strict -2 output_path
//        String[] cmd = {"y","-i","-ss 00:00:00.00", "" + uri.getPath().toString().replace("file://////", ""), "-vcodec","copy","-acodec","copy","-t","00:03:00.00","-strict","-2", videoMergedFile.getPath()};

//        -i in.mp4 -ss [start] -t [duration] -c copy out.mp4
        String[] cmd = {"-i", "" + uri.getPath().replace("file://////", ""), "-ss", "00:00:00.00", "-t", "00:03:00.00", "-c", "copy", videoMergedFile.getPath()};
        MediaUtil.execFFmpegBinary(activity, ffmpeg, cmd, new MediaUtil.FfmpegListener() {

            @Override
            public void onSuccess() {


                uri = Utils.getUri(activity, videoMergedFile);

                setPreview(true);
                notifyPropertyChanged(BR.uri);
//                    postMedia();


            }

            @Override
            public void onFail() {
                activity.hideProgressBar();

            }
        });
    }


    private Bitmap resize(Bitmap bitmap) {

//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//        bitmap= BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//
        if (bitmap == null)
            return null;

        if (bitmap.getHeight() > 1000 && bitmap.getWidth() > 1000)
            return Bitmap.createScaledBitmap(bitmap,
                    (int) (bitmap.getWidth() * 0.5), (int) (bitmap.getHeight() * 0.5), false);

        else
            return bitmap;
    }


    ArrayList<Sticker> stickers = new ArrayList<>();
    public View.OnClickListener textEditingListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View view) { //NOSONAR

            onStickerClicked(null);

        }
    };

    /**
     * On Stickers clicked
     *
     * @param textSticker
     */
    public void onStickerClicked(TextSticker textSticker) {
        Sticker sticker = new Sticker();
        int position = -1;
        if (textSticker != null) {
            sticker.setText(textSticker.getText());
            sticker.setStickerHash(textSticker.hashCode());

            if (stickers.contains(sticker)) {
                position = stickers.indexOf(sticker);
            }

            sticker.setColor(stickers.get(position).getColor());

        }


        Bundle bundle = new Bundle();

        if (position == -1) {
            int color = 0;
            if (((CameraActivity) activity).getCameraBinding().rlPreviewControl.ivCameraText.getBackground() instanceof ColorDrawable) {
                ColorDrawable colorDrawable = ((ColorDrawable) ((CameraActivity) activity).getCameraBinding().rlPreviewControl.ivCameraText.getBackground());
                color = colorDrawable == null ? 0 : colorDrawable.getColor();

            } else {
                ColorGradientDrawable colorDrawable = ((ColorGradientDrawable) ((CameraActivity) activity).getCameraBinding().rlPreviewControl.ivCameraText.getBackground());
                color = colorDrawable == null ? 0 : colorDrawable.getmColor();
            }

            sticker.setColor(color != 0 ? color : Color.BLACK);
            sticker.setText("");
        }

        bundle.putParcelable(Constants.BundleKey.IMAGE_TEXT, sticker);
        bundle.putInt(Constants.BundleKey.IMAGE_TEXT_POS, position);

        ExplicitIntent.getsInstance().navigateForResult(activity, TextOnImageActivity.class, Constants.REQUEST_CODE.REQUEST_TEXT_IMAGE, bundle);


    }

    @Bindable
    public ArrayList<Sticker> getStickers() {
        return stickers;
    }

//    public void addSticker() {
//        stickers.add(sticker);
////        notifyPropertyChanged(BR.stickers);
//    }

    /**
     * Add a Sticker
     *
     * @param sticker
     * @param pos
     */
    public void addSticker(Sticker sticker, int pos) {


        if (stickers != null) {

            if (pos == -1) {
                TextSticker stickerView = new TextSticker(activity);
                stickerView.setText(sticker.getText());
                stickerView.setTextColor(sticker.getColor());
                stickerView.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                stickerView.resizeText();
                stickerView.setAlpha(255);
                sticker.setStickerHash(stickerView.hashCode());
                stickers.add(sticker);

                ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.addSticker(stickerView);

            } else {

                TextSticker stickerView = (TextSticker) ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.getCurrentSticker();
                stickerView.setText(sticker.getText());
                stickerView.setTextColor(sticker.getColor());
                stickerView.resizeText();
                stickerView.setAlpha(255);

                sticker.setStickerHash(stickerView.hashCode());

                stickers.set(pos, sticker);
                ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.replace(stickerView);


            }


        }

    }


    public View.OnClickListener userClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View view) {


            validateMedia();

        }
    };

//    private View useView;

    public void onUserClick() {

        activity.showProgressBar();

//        useView = view;
//        useView.setClickable(false);
        validateMedia();
    }

    private ArrayList<Tag> tagList = new ArrayList<>();
    private ArrayList<User> usertagList = new ArrayList<>();

    public View.OnClickListener captionClickListener = new View.OnClickListener() { //NOSONAR
        @Override
        public void onClick(View view) {


            Bundle bundle = new Bundle();
            if (tagList.size() > 0)
                bundle.putParcelableArrayList(Constants.TAGS, tagList);

            if (usertagList.size() > 0)
                bundle.putParcelableArrayList(Constants.USER_TAGS, usertagList);

            if (!getCaption().isEmpty()) {
                bundle.putString(Constants.CAPTION, caption);
            }
            bundle.putInt(Constants.BundleKey.ROOM_ID, ((CameraActivity) activity).getRoom());

            ExplicitIntent.getsInstance().navigateForResult(activity, AddCaptionActivity.class, Constants.REQUEST_CODE.REQUEST_ADD_TAG, bundle);
        }
    };

    public ArrayList<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<? extends Parcelable> tagList) {
        this.tagList = (ArrayList<Tag>) tagList;
    }

    public void setUserTagList(ArrayList<User> tagList) {
        this.usertagList = tagList;
    }


    private void validateMedia() {


        ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.setDrawingCacheEnabled(true);
        ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.buildDrawingCache();


        if (bitmap != null) {
            if (((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.getStickerCount() > 0) {

                bitmap = BitmapUtils.mergeImages(bitmap, ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.getDrawingCache());
                postMedia();

            } else
                postMedia();
        } else if (getUri() != null) {

            if (!uri.toString().contains(".gif")) {
                validateVideo();
            } else {
//                postMedia();

//                convertGIFtoVideo(((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.getStickerCount() > 0);

//                genPalette(((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.getDrawingCache());

            }
        }

    }

    private ArrayList<Integer> getTaggedUser(ArrayList<User> arrayList) {
        ArrayList<Integer> intUser = new ArrayList<>();
        for (User user : arrayList)
            intUser.add(user.getUserId());

        return intUser;
    }


    public void postMedia() {

        try {
            ArrayList<Integer> roomIds = new ArrayList<>();
//        ArrayList<Tag> tagList = new ArrayList<>();

            HashMap<String, RequestBody> task = new HashMap();

            RequestBody body = null;
            String filePath;

//            activity.showProgressBar();

            if (getUri() != null) {
                filePath = getUri().toString().replace("file:/", "");
                File file = new File(filePath);
                body = RequestBody.create(MediaType.parse(getMimeType(getUri())), file);
            } else {
                filePath = savebitmap("file").getPath().replace("file:/", "");
                File file = new File(filePath);
                body = RequestBody.create(MediaType.parse("image/*"), file);
            }

            roomIds.add(((CameraActivity) activity).getRoom());

            for (int i = 0; i < tagList.size(); i++) {

                task.put("tags[" + i + "].id", createPartFromString(tagList.get(i).getId() + ""));
                task.put("tags[" + i + "].value", createPartFromString(tagList.get(i).getValue()));
            }


//            List<Integer> userTagIds = usertagList.stream().map(urEntity -> urEntity.getUserId()).collect(Collectors.toList());
            List<Integer> userTagIds = getTaggedUser(usertagList);

            task.put("taggedUserIds", createPartFromString(TextUtils.join(",", userTagIds)));
            task.put("caption", createPartFromString(caption));
            task.put("roomIds", createPartFromString(((CameraActivity) activity).getRoom() + ""));
            task.put("postId", createPartFromString("0"));
            task.put("visibility", createPartFromString("0"));

            if (!isGif)
                task.put("mediaType", createPartFromString(MediaUtil.getMediaType(filePath).ordinal() + ""));
            else
                task.put("mediaType", createPartFromString(Constants.MEDIA_TYPE.GIF.ordinal() + ""));


            rx.Observable<PostsResponse> observable = repository.getApiService().addImagePost(body, task);


            switch (MediaUtil.getMediaType(filePath).ordinal()) {
                case 0:

                    observable = repository.getApiService().addImagePost(body, task);
                    mPostMessage = activity.getString(R.string.image_posted_successfully);
                    ;

                    break;
                case 1:
                    observable = repository.getApiService().addVideoPost(body, task);
                    if (isGif)
                        mPostMessage = activity.getString(R.string.image_posted_successfully);
                    else
                        mPostMessage = activity.getString(R.string.video_posted_successfully);


                    break;
                case 2:
                    observable = repository.getApiService().addGifPost(body, task);
                    mPostMessage = activity.getString(R.string.image_posted_successfully); //or change to GIF posted successfully?

                    break;
                case 3:
                    observable = repository.getApiService().addImagePost(body, task);
                    mPostMessage = activity.getString(R.string.image_posted_successfully);

                    break;

            }


            new BaseCallback<PostsResponse>(activity, observable) {
                @Override
                public void onSuccess(PostsResponse response) {

                    try {
                        if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                            Alert.createAlert(activity, "", mPostMessage).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {

                                    activity.hideProgressBar();
//                                    activity.finish();
                                    finishActivity();


                                }
                            }).show();

                        }

                    } catch (Exception e) {
                        LogUtils.printStackTrace(e);
                    }
                }

                @Override
                public void onFail() {
                    activity.hideProgressBar();

                }
            };
        } catch (Exception e) {
            LogUtils.printStackTrace(e);

        }
    }

    public void finishActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BundleKey.ROOM_ID, ((CameraActivity) activity).getPosition());
//        bundle.putParcelable(Constants.BundleKey.ROOM, ((CameraActivity) activity).getRoom());
        Intent intent = new Intent(Constants.POST_BROADCAST);
        intent.putExtras(bundle);


        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
        activity.finish();

//        ExplicitIntent.getsInstance().navigateTo(activity, RoomActivityCoordinator.class, bundle,Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        if (descriptionString == null)
            return RequestBody.create(MultipartBody.FORM, "");
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);

    }

    public String getMimeType(Uri uri) {
        String mimeType = null;

        try {
            if (uri != null) {
                if (uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                    ContentResolver cr = activity.getContentResolver();
                    mimeType = cr.getType(uri);
                } else if (uri.toString().contains(activity.getString(R.string.gif_extension))) {

                    return activity.getString(R.string.mime_gif);
                } else {
                    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                            .toString());
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                            fileExtension.toLowerCase());
                }
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
        return mimeType;
    }

    private File savebitmap(String filename) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, filename + ".png");
        if (file.exists()) {
            file.delete(); //NOSONAR
            file = new File(extStorageDirectory, filename + ".jpeg");
        }
        try {
            // make a new bitmap from your file

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
        return file;

    }

    private File savebitmapNew(Bitmap bitmap, String filename) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, filename + ".png");
        if (file.exists()) {
            file.delete(); //NOSONAR
            file = new File(extStorageDirectory, filename + ".png");
        }
        try {
            // make a new bitmap from your file

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }
        return file;

    }

    public Bitmap getBitmapFromPath(String path) {
        try {
            Bitmap bitmap = null;
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

            return bitmap;
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
            return null;
        }
    }

    private void getVideoFormat() {
//        -i “original-file.mp4” -vcodec libx264 -acodec aac “output-file.mp4”

        videoMergedFile = new File(Environment.getExternalStorageDirectory() + "/video" + System.currentTimeMillis() + ".mp4");

        String[] cmd = {"-i", "" + uri.getPath().replace("file://////", ""), "-vcodec", "libx264", "-preset", "ultrafast", "-crf", "24", "-acodec", "aac", videoMergedFile.getPath()};
        MediaUtil.execFFmpegBinary(activity, ffmpeg, cmd, new MediaUtil.FfmpegListener() {

            @Override
            public void onSuccess() {

                validateVideo();


            }

            @Override
            public void onFail() {

                activity.hideProgressBar();

            }
        });
    }


    private void validateVideo() {

//        activity.showProgressBar();
        if (((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.getStickerCount() > 0) {

            ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.setDrawingCacheEnabled(true);
            ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.buildDrawingCache();

            if (MediaUtil.isVideoDurationCorrect(activity, getUri())) {
                addTextToVideo(((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.getDrawingCache());
            } else {
                cropVideo(true);
            }
        } else {

            if (MediaUtil.isVideoDurationCorrect(activity, getUri())) {
                postMedia();
            } else {
                cropVideo(false);
            }
        }

    }


    private void cropVideo(final boolean isSticker) {
        videoMergedFile = new File(Environment.getExternalStorageDirectory() + "/video" + System.currentTimeMillis() + ".mp4");

//        y -i -ss 00:00:00.00 input_path -vcodec copy -acodec copy -t 00:00:00.00 -strict -2 output_path
//        String[] cmd = {"y","-i","-ss 00:00:00.00", "" + uri.getPath().toString().replace("file://////", ""), "-vcodec","copy","-acodec","copy","-t","00:03:00.00","-strict","-2", videoMergedFile.getPath()};

//        -i in.mp4 -ss [start] -t [duration] -c copy out.mp4
        String[] cmd = {"-i", "" + uri.getPath().replace("file://////", ""), "-ss", "00:00:00.00", "-t", "00:03:00.00", "-c", "copy", videoMergedFile.getPath()};
        MediaUtil.execFFmpegBinary(activity, ffmpeg, cmd, new MediaUtil.FfmpegListener() {

            @Override
            public void onSuccess() {

                if (isSticker) {
                    uri = Utils.getUri(activity, videoMergedFile);
                    addTextToVideo(((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.getDrawingCache());
                } else {
                    uri = Utils.getUri(activity, videoMergedFile);
                    postMedia();
                }


            }

            @Override
            public void onFail() {
                activity.hideProgressBar();


            }
        });
    }

    private void convertGIFtoVideo() {
        videoMergedFile = new File(Environment.getExternalStorageDirectory() + "/video" + System.currentTimeMillis() + ".mp4");

        //ffmpeg -f gif -i infile.gif outfile.mp4
        ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.setDrawingCacheEnabled(true);
        ((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.buildDrawingCache();

//        ffmpeg -i in.gif -c:v libx264 -pix_fmt yuv420p -movflags +faststart out.mp4
//        ffmpeg -i animated.gif -movflags faststart -pix_fmt yuv420p -vf "scale=trunc(iw/2)*2:trunc(ih/2)*2" video.mp4


//        String[] cmd = { "-i", "" + uri.getPath().replace("file://////", ""),"-c:v", "libx264","-pix_fmt","yuv420p","-movflags","+faststart", videoMergedFile.getPath()};
        String[] cmd = {"-i", "" + uri.getPath().replace("file://////", ""), "-movflags", "faststart", "-pix_fmt", "yuv420p", "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2", videoMergedFile.getPath()};

//        String[] cmd = {"-f", "gif", "-i", "" + uri.getPath().replace("file://////", ""),"-vf", "scale=400:-1","-pix_fmt","yuv420p","-movflags","+faststart", videoMergedFile.getPath()};
        MediaUtil.execFFmpegBinary(activity, ffmpeg, cmd, new MediaUtil.FfmpegListener() {

            @Override
            public void onSuccess() {

                setUri(Utils.getUri(activity, videoMergedFile));
                activity.hideProgressBar();


//                if (isSticker) {


//                    uri = Utils.getUri(activity, videoMergedFile);

//                    final VideoView view = ((CameraActivity) activity).getCameraBinding().rlPreviewControl.vvPreview;
//                    view.setVideoURI(uri);
//                    ((CameraActivity) activity).getCameraBinding().rlPreviewControl.vvPreview.setVisibility(View.VISIBLE);
//
//                    view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mp) {
//                            ViewGroup.LayoutParams lp = view.getLayoutParams();
//                            float videoWidth = mp.getVideoWidth();
//                            float videoHeight = mp.getVideoHeight();
//
//                            Constants.videoHeight = videoHeight;
//                            Constants.videoWidth = videoWidth;
//
//
//                            mp.setLooping(true);
//                            float viewWidth = view.getWidth();
//                            lp.height = (int) (viewWidth * (videoHeight / videoWidth));
//                            view.setLayoutParams(lp);
//                            if (view.isPlaying())
//                                return;
//                            view.start();
//                            ((CameraActivity) activity).getCameraBinding().rlPreviewControl.ivGif.setVisibility(View.GONE);
//
//                            addTextToVideo(((CameraActivity) activity).getCameraBinding().rlPreviewControl.stickerView.getDrawingCache());
//
//
//                        }
//                    });

//                } else {
//                    uri = Utils.getUri(activity, videoMergedFile);
//                    postMedia();
//                }


            }

            @Override
            public void onFail() {
                activity.hideProgressBar();


            }
        });
    }
//
//    private void genPalette(final Bitmap bitmap) {
//        Bitmap foreground = Bitmap.createScaledBitmap(bitmap, (int) Constants.videoWidth, (int) Constants.videoHeight, false);
//
//        File file = savebitmapNew(foreground, "file");
//        palleteMergedFile = new File(Environment.getExternalStorageDirectory() + "/video" + System.currentTimeMillis() + ".png");
//
////        ffmpeg -v error -i image.gif -vf 'palettegen' palette.png -y;
//
//        String[] cmd = {"-v", "error", "-i", "" + uri.getPath().replace("file://////", ""), "-vf", "palettegen", palleteMergedFile.getPath()};
//
//        MediaUtil.execFFmpegBinary(activity, ffmpeg, cmd, new MediaUtil.FfmpegListener() {
//            @Override
//            public void onSuccess() {
////                activity.hideProgressBar();
//
//                if (palleteMergedFile.exists()) {
////                    uri = Utils.getUri(activity, gifMergedFile);
////                    postMedia();
//                    addTextToGif(bitmap);
//
//                }
//            }
//
//            @Override
//            public void onFail() {
//                activity.hideProgressBar();
//            }
//        });
//    }
//
//    private void addTextToGif(Bitmap bitmap) {
//        Bitmap foreground = Bitmap.createScaledBitmap(bitmap, (int) Constants.videoWidth, (int) Constants.videoHeight, false);
//
//        File file = savebitmapNew(foreground, "file");
//        gifMergedFile = new File(Environment.getExternalStorageDirectory() + "/video" + System.currentTimeMillis() + ".gif");
//
//
////        String[] cmd = {"-i", "" + uri.getPath().replace("file://////", ""), "-i","" + file.getPath(),"-filter_complex" ," [0:v][1:v] overlay=0:0",  gifMergedFile.getPath()};
////        String[] cmd = {"-i", "" + uri.getPath().replace("file://////", ""), "-i","" + file.getPath(),"-filter_complex" ,"[0:v][1:v] overlay=0:0[v];[v][1]paletteuse",  gifMergedFile.getPath()};
//
////        ffmpeg -v error -i image.gif -i watermark.gif -i palette.png -filter_complex '[1:v]scale=80:30, [0:v]overlay=(main_w-overlay_w) - main_h/30:(main_h-overlay_h) - main_h/30, paletteuse' -an image-watermark.gif -y
//
//        String[] cmd = {"-v", "error", "-i", "" + uri.getPath().replace("file://////", ""), "-i", file.getPath(), "-i", palleteMergedFile.getPath(), "-filter_complex", "[1:v]scale=320:-1 , [0:v]overlay=0:0, paletteuse", "-an", gifMergedFile.getPath(), "-y"};
//
//        MediaUtil.execFFmpegBinary(activity, ffmpeg, cmd, new MediaUtil.FfmpegListener() {
//            @Override
//            public void onSuccess() {
//                activity.hideProgressBar();
//
//                if (gifMergedFile.exists()) {
//                    uri = Utils.getUri(activity, gifMergedFile);
////                    postMedia();
//                }
//            }
//
//            @Override
//            public void onFail() {
//                activity.hideProgressBar();
//            }
//        });
//    }

    private void addTextToVideo(Bitmap bitmap) {


        Bitmap foreground = Bitmap.createScaledBitmap(bitmap, (int) Constants.videoWidth, (int) Constants.videoHeight, false);

        File file = savebitmapNew(foreground, "file");
        videoMergedFile = new File(Environment.getExternalStorageDirectory() + "/video" + System.currentTimeMillis() + ".mp4");


//
//            execFFmpegBinary(new String[]{"ffmpeg -i " + uri.getPath().toString().replace("file://////", "") + " -i " + file.getAbsolutePath() + " -filter_complex \"[0:v][1:v] overlay=(W-w)/2:(H-h)/2:enable='between(t,0,20)'\" -pix_fmt yuv420p -c:a copy /output.mp4\n"});
//            execFFmpegBinary(new String[]{"ffmpeg -i " + uri.getPath().toString().replace("file://////", "") + ""});
//            execFFmpegBinary(new String[]{"ffmpeg -i " + file.getAbsolutePath() + ""});

//            String[] cmd = new String[]{ "-i", uri.getPath().toString().replace("file://////", ""), "-i", ""+file.getPath(), "-filter_complex", "overlay=0:main_h-overlay_h",videoMergedFile.getPath()};
//   -codec:v libx264 -crf 18 -preset slow -pix_fmt yuv420p -c:a aac -strict -2
//        String[] cmd = {"-i", "" + uri.getPath().replace("file://////", ""), "-i", "" + file.getPath(), "-filter_complex", "[0:v][1:v] overlay=0:0:enable='between(t,0,180)'", "-preset", "ultrafast", "-crf", "24", videoMergedFile.getPath()};
        String[] cmd = {"-i", "" + uri.getPath().replace("file://////", ""), "-i", "" + file.getPath(), "-filter_complex", "[0:v][1:v] overlay=0:0:enable='between(t,0,180)'", "-codec:v", "libx264", "-crf", "18", "-preset", "slow", "-pix_fmt", "yuv420p", "-c:a", "aac", "-strict", "-2", videoMergedFile.getPath()};
        MediaUtil.execFFmpegBinary(activity, ffmpeg, cmd, new MediaUtil.FfmpegListener() {
            @Override
            public void onSuccess() {
                if (videoMergedFile.exists()) {
                    uri = Utils.getUri(activity, videoMergedFile);
                    postMedia();
                }
            }

            @Override
            public void onFail() {
                activity.hideProgressBar();
            }
        });
    }

    private void loadFFMpegBinary() {
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                    LOGE("TAG1", "NOT SUPPORTED");

                }
            });
        } catch (FFmpegNotSupportedException e) {
            LOGE("TAG1", "NOT SUPPORTED");

        }
    }


    @Bindable
    public String getTextToAdd() {
        return textToAdd;
    }

    public void setTextToAdd(String textToAdd) {
        this.textToAdd = textToAdd;
        notifyPropertyChanged(BR.textToAdd);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setImageText(String imageText) {
        this.imageText = imageText;
    }
}
