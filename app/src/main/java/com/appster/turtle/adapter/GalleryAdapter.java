package com.appster.turtle.adapter;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ItemGalleryBinding;
import com.appster.turtle.ui.media.CameraActivity;
import com.appster.turtle.ui.notes.UploadAttachmentsActivity;
import com.appster.turtle.ui.notes.editNotes.EditNotesActivity;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.MediaUtil;
import com.appster.turtle.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
/*
 * adapter for image
 */
@SuppressWarnings("ALL")
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<String> imageList;


    public GalleryAdapter(Context mContext) {
        this.mContext = mContext;
        this.imageList = MediaUtil.getAllMedia(mContext) != null ? MediaUtil.getAllMedia(mContext) : new ArrayList<String>();
    }

    public GalleryAdapter(Context mContext, boolean getRecentImagesOnly) {
        this.mContext = mContext;

        List<String> mediaList;

        if (getRecentImagesOnly)
            mediaList = MediaUtil.getCameraImages(mContext);
        else
            mediaList = MediaUtil.getAllMedia(mContext);

        this.imageList = MediaUtil.getAllMedia(mContext) != null ? mediaList : new ArrayList<String>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_gallery, parent, false);
        return new GalleryAdapter.GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof GalleryViewHolder) {

            ((GalleryViewHolder) holder).setImage(imageList.get(position));
        }

        holder.itemView.setTag(imageList.get(position));

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ItemGalleryBinding mBinding;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

            mBinding.ivImage.setOnClickListener(this);
        }

        public void setImage(final String uri) {

            ((View) mBinding.ivImage.getParent()).setTag(uri);

            if (uri == null)
                return;

            int px = Utils.dpToPx(mContext, 72);

            GlideApp.with(mContext)
                    .load(new File(uri))
                    .centerCrop()
                    .override(px, px)
                    .placeholder(R.drawable.id_babble)
                    .into(mBinding.ivImage);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_image:

                    if (mContext instanceof CameraActivity) {
                        ((CameraActivity) mContext).selectGalleryItem((String) ((View) view.getParent()).getTag());
                    }
                    if (mContext instanceof UploadAttachmentsActivity) {
                        ((UploadAttachmentsActivity) mContext).selectGalleryItem((String) ((View) view.getParent()).getTag());
                    }
                    if (mContext instanceof EditNotesActivity) {
                        ((EditNotesActivity) mContext).selectGalleryItem((String) ((View) view.getParent()).getTag());
                    }

                    break;

                default:
                    break;
            }
        }
    }

    public List<String> getCameraImages() {
        // Get relevant columns for use later.
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

// Return only video and image metadata.
        String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

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

    private Bitmap getBitmap(File f) {

        if (f.getAbsolutePath().contains("mp4")) {
            return ThumbnailUtils.createVideoThumbnail(f.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);

        }
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 50;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            LogUtils.printStackTrace(e);

        }
        return null;
    }

}

