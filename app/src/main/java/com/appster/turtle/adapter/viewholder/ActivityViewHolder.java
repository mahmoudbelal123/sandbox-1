package com.appster.turtle.adapter.viewholder;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.appster.turtle.R;
import com.appster.turtle.adapter.ActivityAdapter;
import com.appster.turtle.databinding.ItemActivityBinding;
import com.appster.turtle.model.Activities;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.activity.ActivitiesNotificationActivity;
import com.appster.turtle.ui.post.PostDetailActivity;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.util.ActivityMessage;
import com.appster.turtle.util.CustomTypefaceSpan;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.bindingadapters.CameraBindingAdapters;

/*
 *  view holder for activity
 */
public class ActivityViewHolder extends RecyclerView.ViewHolder {

    private final ItemActivityBinding mBinding;

    public ActivityViewHolder(ItemActivityBinding loadMoreBinding) {
        super(loadMoreBinding.getRoot());
        mBinding = loadMoreBinding;
    }

    public void setActivity(final Activities activity, final ActivityAdapter.OnItemClickListener mOnItemClickLister) {

        mBinding.tvActivityText.setLinksClickable(true);
        mBinding.tvActivityText.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.tvActivityText.setText(getMessage(mBinding.getRoot().getContext(), activity), TextView.BufferType.SPANNABLE);
        mBinding.getRoot().setOnClickListener(v -> mOnItemClickLister.onItemClicked(activity, getAdapterPosition()));
    }

    public ItemActivityBinding getmBinding() {
        return mBinding;
    }

    private SpannableStringBuilder getMessage(final Context context, Activities activities) {

        final Typeface boldTypeFace = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.eina_01_semibold));
        Typeface normalTypeFace = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.eina_01_regular));


        SpannableStringBuilder stringBuilder = new SpannableStringBuilder("");
        mBinding.rlActivityImage.setVisibility(View.GONE);
        mBinding.tvActivitySubText.setText("");

        switch (activities.getActivityType()) {

            case ActivityMessage.ActivityType.POST_VIDEO:
            case ActivityMessage.ActivityType.POST_VIDEO_LIKE:
            case ActivityMessage.ActivityType.POST_VIDEO_COMMENT:
            case ActivityMessage.ActivityType.POST_VIDEO_COMMENT_LIKE:
            case ActivityMessage.ActivityType.POST_GIF:
            case ActivityMessage.ActivityType.POST_GIF_COMMENT:
            case ActivityMessage.ActivityType.POST_GIF_COMMENT_LIKE:
            case ActivityMessage.ActivityType.POST_GIF_LIKE:
                mBinding.rlActivityImage.setVisibility(View.VISIBLE);
                mBinding.ivVideoPlay.setVisibility(View.VISIBLE);
                break;

            case ActivityMessage.ActivityType.POST_PICTURE:
            case ActivityMessage.ActivityType.POST_PICTURE_LIKE:
            case ActivityMessage.ActivityType.POST_PICTURE_COMMENT_LIKE:
            case ActivityMessage.ActivityType.POST_PICTURE_COMMENT:
                mBinding.rlActivityImage.setVisibility(View.VISIBLE);
                mBinding.ivVideoPlay.setVisibility(View.GONE);
                mBinding.ivActivityImg.setVisibility(View.VISIBLE);
                break;

            default:
                mBinding.rlActivityImage.setVisibility(View.GONE);
                break;
        }


        for (final Activities.MessageMeta messageMeta : activities.getMessageMeta()) {

            int index = 0;
            int lastIndex = 0;

            switch (messageMeta.getType()) {

                case ActivityMessage.MessageType.MEDIA:

                    if (messageMeta.getMessage() != null && !messageMeta.getMessage().isEmpty()) {
                        CameraBindingAdapters.bindImageUrlWithImage(mBinding.ivActivityImg, messageMeta.getMessage());
                    }

                    break;

                case ActivityMessage.MessageType.ABOUT_TXT:

                    if (messageMeta.getMessage() != null && !messageMeta.getMessage().isEmpty()) {
                        mBinding.tvActivitySubText.setText(messageMeta.getMessage());
                    }

                    break;

                default:
                    stringBuilder.append(messageMeta.getMessage());
                    index = stringBuilder.toString().indexOf(messageMeta.getMessage());
                    lastIndex = index + messageMeta.getMessage().length();
                    break;
            }


            switch (messageMeta.getType()) {

                case ActivityMessage.MessageType.NORMAL:

                    stringBuilder.setSpan(new CustomTypefaceSpan("", normalTypeFace), index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    break;

                case ActivityMessage.MessageType.PROFILE:
                    stringBuilder.setSpan(new CustomTypefaceSpan("", boldTypeFace), index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    stringBuilder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {

                            LogUtils.LOGD("ON SPAN CLICK", " Profile" + messageMeta.getMessage());
                            if (messageMeta.getData() != null && !messageMeta.getData().isEmpty()) {
                                Bundle i = new Bundle();
                                i.putInt(Constants.USER_ID, messageMeta.getData().get(0));
                                ExplicitIntent.getsInstance().navigateTo((ActivitiesNotificationActivity) context, UserProfileActivity.class, i);

                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(ContextCompat.getColor(context, R.color.textcolor_room_name));
                            applyCustomTypeFace(ds, boldTypeFace);
                        }

                    }, index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    break;

                case ActivityMessage.MessageType.ROOM:
                    stringBuilder.setSpan(new CustomTypefaceSpan("", boldTypeFace), index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    stringBuilder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            if (messageMeta.getData() != null && !messageMeta.getData().isEmpty())
                                ((BaseActivity) context).openRoom(Integer.parseInt(activities.getData().getRoomId()), getAdapterPosition());
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(ContextCompat.getColor(context, R.color.textcolor_room_name));
                            applyCustomTypeFace(ds, boldTypeFace);
                        }
                    }, index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    break;


                case ActivityMessage.MessageType.MEETUP:
                    stringBuilder.setSpan(new CustomTypefaceSpan("", boldTypeFace), index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    stringBuilder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            if (messageMeta.getData() != null && !messageMeta.getData().isEmpty()) {
                                Bundle bundle = new Bundle();
                                bundle.putInt(Constants.POST_ID, messageMeta.getData().get(0));
                                ExplicitIntent.getsInstance().navigateForResult((BaseActivity) context, PostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_POST_DETAIL, bundle);
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(ContextCompat.getColor(context, R.color.textcolor_room_name));
                            applyCustomTypeFace(ds, boldTypeFace);
                        }
                    }, index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    break;


            }

        }

        return stringBuilder;

    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        paint.setTypeface(tf);
    }


}
