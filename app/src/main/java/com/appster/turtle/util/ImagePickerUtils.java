/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 *
 */

public class ImagePickerUtils extends Fragment {

    public interface OnImagePickerListener {

        void success(String name, String path);

        void fail(String message);
    }

    public static final String TAG = ImagePickerUtils.class.getSimpleName();
    private static final int CAMERA_PIC_REQUEST = 2000;
    private static final int IMAGE_PICKER_REQUEST = CAMERA_PIC_REQUEST + 1;
    private static final int MEMORY_PERMISSION_REQUEST = IMAGE_PICKER_REQUEST + 1;
    private OnImagePickerListener listener;
    private Boolean isCamera;
    private String mediaPath;
    private boolean isGif;

    public static void add(boolean isCamera, @NonNull FragmentManager manager, @NonNull OnImagePickerListener listener) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(ImagePickerUtils.newInstance(isCamera, listener), TAG);
        transaction.commit();
    }

    public static void addWithGif(boolean isCamera, @NonNull FragmentManager manager, @NonNull OnImagePickerListener listener) {

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(ImagePickerUtils.newInstance(true, isCamera, listener), TAG);
        transaction.commit();
    }

    private static ImagePickerUtils newInstance(boolean isCamera, @NonNull OnImagePickerListener listener) {
        ImagePickerUtils fragment = new ImagePickerUtils();

        fragment.setOnImagePickerListener(listener);
        fragment.setCamera(isCamera);
        return fragment;
    }

    private static ImagePickerUtils newInstance(boolean isGif, boolean isCamera, @NonNull OnImagePickerListener listener) {
        ImagePickerUtils fragment = new ImagePickerUtils();

        fragment.isGif = isGif;
        fragment.setOnImagePickerListener(listener);
        fragment.setCamera(isCamera);
        return fragment;
    }

    public void setOnImagePickerListener(OnImagePickerListener listener) {
        this.listener = listener;
    }


    public void setCamera(Boolean camera) {
        isCamera = camera;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (isStoragePermissionGranted()) {
//            showGalleryDialog();
//        }


        if (isCamera) {
            mediaPath = BitmapUtils.scaledImagePath();
            File file = new File(mediaPath);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //Some devices doesn't work without it.
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Utils.getUri(getContext(), file, true));
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        } else {
            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, IMAGE_PICKER_REQUEST);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                LogUtils.LOGD(TAG, "Permission is granted");
                return true;
            } else {
                LogUtils.LOGD(TAG, "Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MEMORY_PERMISSION_REQUEST);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            LogUtils.LOGD(TAG, "Permission is granted");
            return true;
        }
    }

    private void showGalleryDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo");
        builder.setItems(items, (dialog, which) -> {
            if (items[which].equals("Take Photo")) {
                dialog.dismiss();
                mediaPath = BitmapUtils.scaledImagePath();
                File file = new File(mediaPath);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Some devices doesn't work without it.

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Utils.getUri(getContext(), file, true));
//                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            } else if (items[which].equals("Choose from Library")) {
                dialog.dismiss();
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(galleryIntent, IMAGE_PICKER_REQUEST);
            } else if (items[which].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private String handleCameraResult() {
        //Scale down the image to reduce size.
        mediaPath = BitmapUtils.scaleImage(getContext(), mediaPath, BitmapUtils.DEFAULT_PHOTO_WIDH,
                BitmapUtils.DEFAULT_PHOTO_HEIGHT);
//        return mediaPath != null ? /*"file:///" +*/ mediaPath : null;
        return mediaPath;
    }

    private String handleGalleryResult(Intent intent) {
        String path = BitmapUtils.getImagePath(getContext(), intent);

        if (path != null && path.endsWith(".gif") && isGif) {
            return path;
        }


        if (TextUtils.isEmpty(path)) {

            Uri selectedImageUri = intent.getData();
            String tempPath = getPath(selectedImageUri, getActivity());
            String url = intent.getData().toString();
            if (url.startsWith("content://com.google.android.apps.photos.content")) {
                try {
                    InputStream is = getActivity().getContentResolver().openInputStream(selectedImageUri);
                    if (is != null) {
                        Bitmap pictureBitmap = BitmapFactory.decodeStream(is);
                        //You can use this bitmap according to your purpose or Set bitmap to imageview
                        mediaPath = Environment.getExternalStorageDirectory().toString() + "/temp.png";
                        saveImage(mediaPath, pictureBitmap);

                        return mediaPath;
                    }
                } catch (FileNotFoundException e) {
                    LogUtils.LOGD(TAG, e.getMessage());
                }
            }
        } else {
            mediaPath = BitmapUtils.scaleImage(getContext(), path, BitmapUtils.DEFAULT_PHOTO_WIDH,
                    BitmapUtils.DEFAULT_PHOTO_HEIGHT);
            return /*"file:///" +*/ mediaPath;
        }


        return null;
    }

    private void saveImage(String mediaPath, Bitmap bmp) {

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mediaPath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            LogUtils.LOGD(TAG, e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LogUtils.LOGD(TAG, e.getMessage());
            }
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        try (Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            LogUtils.LOGD(TAG, e.getMessage());

        }
        return "";
    }

    //Runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (MEMORY_PERMISSION_REQUEST == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LogUtils.LOGD(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            showGalleryDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String filePath = null;
            switch (requestCode) {
                case CAMERA_PIC_REQUEST:
                    filePath = handleCameraResult();
                    break;
                case IMAGE_PICKER_REQUEST:
                    filePath = handleGalleryResult(data);
                    break;
            }
            if (filePath != null) {
                listener.success(filePath.substring(filePath.lastIndexOf("/") + 1), filePath);
            } else {
                listener.fail("Unable to get path");
            }
        } else {
            listener.fail("Unable to get path");
        }
    }
}
