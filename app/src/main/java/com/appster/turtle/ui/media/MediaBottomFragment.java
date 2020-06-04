/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.media;

import android.Manifest;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.adapter.MediaBottomAdapter;
import com.appster.turtle.adapter.viewholder.CameraBottomViewHolder;
import com.appster.turtle.databinding.FragmentMediaBottomBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.BaseFragment;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.SpannedGridLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
/**
 * A fragment class for media bottom
 */
public class MediaBottomFragment extends BaseFragment implements BaseActivity.PermissionI {

    public static final String IS_ALL = "IS_ALL";
    FragmentMediaBottomBinding mediaBottomBinding;
    ArrayList<String> imageList;
    private MediaBottomAdapter mediaBottomAdapter;
    com.appster.turtle.util.SpannedGridLayoutManager layoutManager;
    private Calendar cal = Calendar.getInstance();
    private Calendar calprev = Calendar.getInstance();
    private boolean isAllMedia;
    private int count = 0;

    public MediaBottomFragment() {
    }

    public static MediaBottomFragment newInstance(boolean isAllMedia) {
        MediaBottomFragment fragment = new MediaBottomFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_ALL, isAllMedia);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAllMedia = getArguments().getBoolean(IS_ALL);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mediaBottomBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_media_bottom, container, false);

        getBaseActivity().checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, this);


        return mediaBottomBinding.getRoot();
    }

    public void setCameraStop(boolean cameraNeeded) {
        boolean isCameraStop = cameraNeeded;
    }

    private void setImageData() {

        imageList = new ArrayList<>();
        generateQuery();


        layoutManager = new SpannedGridLayoutManager(
                position -> {
                    if (position == 0) {
                        return new SpannedGridLayoutManager.SpanInfo(2, 2);
                    } else {
                        return new SpannedGridLayoutManager.SpanInfo(1, 1);
                    }
                },
                3 /* Three columns */,
                1f /* We want our items to be 1:1 ratio */);


        mediaBottomAdapter = new MediaBottomAdapter(getActivity(), this, imageList);
        mediaBottomBinding.rvMediaBottom.setNestedScrollingEnabled(false);
        mediaBottomBinding.rvMediaBottom.setAdapter(mediaBottomAdapter);
        mediaBottomBinding.rvMediaBottom.setLayoutManager(layoutManager);


        mediaBottomBinding.rvMediaBottom.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {

                    count += 10;
                    generateQuery();
                }


            }
        });


    }

    public int getFirstItemPosition() {
        return layoutManager.getFirstVisibleItemPosition();
    }


    @Override
    public String getFragmentName() {
        return MediaBottomFragment.class.getName();
    }

    public void start() {

        count = 0;
        if (mediaBottomAdapter != null) {
            cal = Calendar.getInstance();
            calprev = Calendar.getInstance();

            if (CameraBottomViewHolder.cameraView != null && CameraBottomViewHolder.cameraView.isStarted())
                CameraBottomViewHolder.cameraView.destroy();
            imageList.clear();
            mediaBottomAdapter = null;
            setImageData();
        }


    }


    private void generateQuery() {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };
        String selection;

        if (!isAllMedia) {
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

        LogUtils.LOGD("QUERY", " DESC LIMIT 10 offset " + count);
        android.support.v4.content.CursorLoader cursorLoader = new android.support.v4.content.CursorLoader(
                getActivity(),
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC LIMIT 10 offset " + count);

        Cursor cursor = cursorLoader.loadInBackground();

        ArrayList<String> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());


        }

        if (!result.isEmpty()) {
            imageList.addAll(result);

            if (mediaBottomAdapter != null)
                mediaBottomAdapter.addAll();
        }


    }


    @Override
    public void onPermissionGiven() {
        setImageData();
    }
}
