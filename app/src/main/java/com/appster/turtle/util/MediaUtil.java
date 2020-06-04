/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import android.Manifest;
import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.appster.turtle.util.LogUtils.LOGE;

public class MediaUtil {

    public static Constants.MEDIA_TYPE getMediaType(String filePath) {
        if (filePath.contains(".pdf")) {
            return Constants.MEDIA_TYPE.PDF;
        } else if (filePath.contains(".gif")) {
            return Constants.MEDIA_TYPE.GIF;
        } else if (filePath.contains(".png") || filePath.contains(".jpeg") || filePath.contains(".jpg")) {
            return Constants.MEDIA_TYPE.IMAGE;
        } else {
            return Constants.MEDIA_TYPE.VIDEO;
        }
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<>());

            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }


    public interface FfmpegListener {

        void onSuccess();

        void onFail();
    }

    public static void execFFmpegBinary(final BaseActivity activity, FFmpeg ffmpeg, final String[] command, final FfmpegListener ffmpegListener) {
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    LOGE("TAG1", "FAIL");
                    ffmpegListener.onFail();

                }

                @Override
                public void onSuccess(String s) {
                    LOGE("TAG1", "Success" + s);

                    ffmpegListener.onSuccess();


                }

                @Override
                public void onProgress(String s) {
                    LOGE("TAG1", "Progress" + s);

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
            LogUtils.printStackTrace(e);

        }
    }

    public static boolean isVideoDurationCorrect(Context context, Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(context, uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);

        retriever.release();

        return timeInMillisec <= Constants.VIDEO_LENGTH;
    }


    ///////////////////////GET RECENT IMAGES//////////////////////////////
    public static List<String> getCameraImages(Context mContext) {
        return getRecentMedia(mContext, true);

    }

    public static List<String> getAllMedia(Context mContext) {

        return getRecentMedia(mContext, false);
    }

    public static List<String> getRecentMedia(Context mContext, boolean ifOnlyImages) {
        // Get relevant columns for use later.
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return null;


        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };
        String selection;

        if (ifOnlyImages) {
            selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " ) AND "
                    + MediaStore.Files.FileColumns.DATE_ADDED
                    + ">=" + getLastWeekDate() / 1000;
        } else {
            selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + " )";
//                    + " ) AND "
//                    + MediaStore.Files.FileColumns.DATE_ADDED + ">=" + getLastWeekDate() / 1000;


        }

        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );

        Cursor cursor = cursorLoader.loadInBackground();

        ArrayList<String> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());


        }

        return result;
    }


    private static long getLastWeekDate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        return cal.getTime().getTime();
    }


    public static CursorLoader getRecentMediaCursor(Context mContext, boolean ifOnlyImages) {
        // Get relevant columns for use later.
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return null;


        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };
        String selection;

        if (ifOnlyImages) {
            selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " ) ";
        } else {
            selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    + " ) ";

        }

        Uri queryUri = MediaStore.Files.getContentUri("external");


        return new CursorLoader(
                mContext,
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
    }


    //////////////////////////////////////////////////////////////////////////

}
