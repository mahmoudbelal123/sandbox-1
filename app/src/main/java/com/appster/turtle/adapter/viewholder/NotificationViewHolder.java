/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

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
import com.appster.turtle.adapter.NotificationAdapter;
import com.appster.turtle.databinding.ItemNotificationBinding;
import com.appster.turtle.model.MediaMeta;
import com.appster.turtle.model.MessageMeta;
import com.appster.turtle.model.Notification;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.ReactionByActivity;
import com.appster.turtle.ui.activity.ActivitiesNotificationActivity;
import com.appster.turtle.ui.notes.NotesPurchaseActivity;
import com.appster.turtle.ui.post.PostDetailActivity;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.util.ActivityMessage;
import com.appster.turtle.util.CustomTypefaceSpan;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.bindingadapters.CameraBindingAdapters;

import static com.appster.turtle.util.LogUtils.LOGD;

/**
 * Created by rohan on 10/10/17.
 * view holder of notification
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    private ItemNotificationBinding mBinding;
    private Context mContext;

    public NotificationViewHolder(ItemNotificationBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public void setNotification(final Context context, final Notification notification, final NotificationAdapter.OnItemClickListener mOnItemClickLister) {
        mContext = context;
        mBinding.tvNotificationText.setLinksClickable(true);
        mBinding.tvNotificationText.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.tvNotificationText.setText(getMessage(mBinding.getRoot().getContext(), notification), TextView.BufferType.SPANNABLE);

        if (notification.getIsRead().equals("F")) {
            mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(mContext, R.color.notification_unread));
        } else {
            mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(mContext, R.color.app_white));

        }


        mBinding.getRoot().setOnClickListener(v -> mOnItemClickLister.onItemClicked(notification, getAdapterPosition()));

        mBinding.btnRejected.setOnClickListener(v -> mOnItemClickLister.onAcceptReject(notification, getAdapterPosition(), 2));

        mBinding.btnAccpected.setOnClickListener(v -> mOnItemClickLister.onAcceptReject(notification, getAdapterPosition(), 1));
    }


    public SpannableStringBuilder getMessage(final Context context, final Notification notification) {

        final Typeface boldTypeFace = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.eina_01_semibold));
        Typeface normalTypeFace = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.eina_01_regular));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder("");
        mBinding.rlNotificationImg.setVisibility(View.INVISIBLE);
        mBinding.rlAccpectReject.setVisibility(View.GONE);
        mBinding.tvNotificationSubText.setText("");
        for (MediaMeta mediaMeta : notification.getMediaMeta()) {
            switch (mediaMeta.getMediaType()) {
                case ActivityMessage.MediaType.PICTURE_THUMB:
                    if (mediaMeta.getMediaType() != null && !mediaMeta.getMediaType().isEmpty()) {
                        CameraBindingAdapters.bindImageUrlWithImage(mBinding.ivNotificationImg, mediaMeta.getMediaType());
                    }
                    break;
                default:
                    break;


            }

        }

        switch (notification.getNotificationType()) {
            case ActivityMessage.NotificationType.FRIEND_REQUEST:
            case ActivityMessage.NotificationType.ROOM_JOIN_REQUEST:
            case ActivityMessage.NotificationType.ROOM_REQUEST:

                break;
            default:
                mBinding.rlAccpectReject.setVisibility(View.GONE);
                break;
        }

        switch (notification.getNotificationType()) {

            case ActivityMessage.NotificationType.POST_VIDEO_LIKE:
            case ActivityMessage.NotificationType.POST_VIDEO_COMMENT:
            case ActivityMessage.NotificationType.POST_GIF_COMMENT:
            case ActivityMessage.NotificationType.POST_GIF_LIKE:
                mBinding.ivVideoPlay.setVisibility(View.VISIBLE);
                break;

            case ActivityMessage.NotificationType.POST_PICTURE_COMMENT:
            case ActivityMessage.NotificationType.POST_PICTURE_LIKE:
                mBinding.ivVideoPlay.setVisibility(View.GONE);
                break;
            default:
                break;


        }


        for (final MessageMeta messageMeta : notification.getMessageMeta()) {

            int index = 0;
            int lastIndex = 0;

            switch (messageMeta.getType()) {

                case ActivityMessage.MessageType.MEDIA:
                    if (notification.getNotificationType() == ActivityMessage.NotificationType.POST_PICTURE_COMMENT ||
                            notification.getNotificationType() == ActivityMessage.NotificationType.POST_PICTURE_LIKE ||
                            notification.getNotificationType() == ActivityMessage.NotificationType.POST_VIDEO_LIKE ||
                            notification.getNotificationType() == ActivityMessage.NotificationType.POST_USER_TAG_MEDIA ||
                            notification.getNotificationType() == ActivityMessage.NotificationType.POST_PICTURE_COMMENT_LIKE ||
                            notification.getNotificationType() == ActivityMessage.NotificationType.POST_VIDEO_COMMENT_LIKE ||
                            notification.getNotificationType() == ActivityMessage.NotificationType.POST_GIF_COMMENT_LIKE ||
                            notification.getNotificationType() == ActivityMessage.NotificationType.POST_VIDEO_COMMENT || notification.getNotificationType() == ActivityMessage.NotificationType.BECOME_ROOM_ADMIN) {

                        if (messageMeta.getMessage() != null && !messageMeta.getMessage().isEmpty()) {

                            mBinding.rlNotificationImg.setVisibility(View.VISIBLE);
                            CameraBindingAdapters.bindImageUrlWithImage(mBinding.ivNotificationImg, messageMeta.getMessage());
                        }
                    }

                    break;

                case ActivityMessage.MessageType.ABOUT_TXT:

                    if (messageMeta.getMessage() != null && !messageMeta.getMessage().isEmpty()) {
                        mBinding.tvNotificationSubText.setText(messageMeta.getMessage());
                    }
                    break;

                case ActivityMessage.MessageType.STATUS:
                    if (notification.getNotificationType() == ActivityMessage.NotificationType.FRIEND_REQUEST) {
                        if (messageMeta.getData().get(0) == Constants.MyFriend.FRIEND_STATUS_PENDING) {
                            showViewAcppectReject();
                        } else if (messageMeta.getData().get(0) == Constants.MyFriend.FRIEND_STATUS_CONNECTED) {
                            showAccpect();
                        } else {
                            showReject();
                        }
                    } else if (notification.getNotificationType() == ActivityMessage.NotificationType.ROOM_REQUEST || notification.getNotificationType() == ActivityMessage.NotificationType.ROOM_JOIN_REQUEST) {
                        if (messageMeta.getData().get(0) == Constants.RoomMember.STATUS_PENDING) {
                            showViewAcppectReject();
                        } else if (messageMeta.getData().get(0) == Constants.RoomMember.STATUS_CONNECTED) {
                            showAccpect();
                        } else {
                            showReject();
                        }
                    } else {
                        mBinding.rlAccpectReject.setVisibility(View.GONE);
                    }

                    break;

                default:
                    stringBuilder.append(messageMeta.getMessage());
                    index = stringBuilder.toString().indexOf(messageMeta.getMessage());
                    lastIndex = index + messageMeta.getMessage().length();

                    break;
            }


            if (messageMeta.getType().equals(ActivityMessage.MessageType.NORMAL)) {
                stringBuilder.setSpan(new CustomTypefaceSpan("", normalTypeFace), index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {

                //set all other text to be bold
                stringBuilder.setSpan(new CustomTypefaceSpan("", boldTypeFace), index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                switch (messageMeta.getType()) {

                    case ActivityMessage.MessageType.PROFILE:

                        if (!messageMeta.getUrl().isEmpty()) {

                            mBinding.setMessageMeta(messageMeta);
                        }

                        stringBuilder.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                LOGD("ON SPAN CLICK", " Profile" + messageMeta.getMessage());
                                if (messageMeta.getData() != null && !messageMeta.getData().isEmpty()) {
                                    Bundle i = new Bundle();
                                    i.putInt(Constants.USER_ID, messageMeta.getData().get(0));
                                    ExplicitIntent.getsInstance().navigateTo((ActivitiesNotificationActivity) mContext, UserProfileActivity.class, i);
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                ds.setColor(ContextCompat.getColor(mContext, R.color.textcolor_room_name));
                                applyCustomTypeFace(ds, boldTypeFace);
                            }

                        }, index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        break;

                    case ActivityMessage.MessageType.ROOM:
                        stringBuilder.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                if (messageMeta.getData() != null && !messageMeta.getData().isEmpty())
                                    ((BaseActivity) context).openRoom(messageMeta.getData().get(0), getAdapterPosition());
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                ds.setColor(ContextCompat.getColor(mContext, R.color.textcolor_room_name));
                                applyCustomTypeFace(ds, boldTypeFace);
                            }
                        }, index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        break;

                    case ActivityMessage.MessageType.GROUP_PROFILE:
                        stringBuilder.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                if (notification.getNotificationType() == ActivityMessage.NotificationType.POST_GIF_LIKE ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.POST_GIF_COMMENT_LIKE ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.POST_MEETUP_COMMENT_LIKE ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.POST_MEETUP_LIKE ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.POST_PICTURE_COMMENT_LIKE ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.POST_PICTURE_LIKE ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.POST_VIDEO_COMMENT_LIKE ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.POST_VIDEO_LIKE ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.POST_POLL_COMMENT_LIKE ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.POST_POLL_LIKE) {
                                    Bundle b = new Bundle();
                                    b.putInt(Constants.BundleKey.POST, messageMeta.getData().get(0));
                                    ExplicitIntent.getsInstance().navigateTo((ActivitiesNotificationActivity) mContext, ReactionByActivity.class, b);
                                } else if (notification.getNotificationType() == ActivityMessage.NotificationType.POST_MEETUP_RESPONSE_ATTENDING || notification.getNotificationType() == ActivityMessage.NotificationType.POST_MEETUP_RESPONSE_INTERESTED) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(Constants.POST_ID, messageMeta.getData().get(0));
                                    ExplicitIntent.getsInstance().navigateForResult((BaseActivity) mContext, PostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_POST_DETAIL, bundle);


                                } else if (notification.getNotificationType() == ActivityMessage.NotificationType.NOTE_PURCHASED_EVENT ||
                                        notification.getNotificationType() == ActivityMessage.NotificationType.NOTE_REVIEW_EVENT) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(Constants.BundleKey.NOTE_ID, notification.getData().getNoteId());
                                    bundle.putBoolean(Constants.IS_NEWADD, true);
                                    ExplicitIntent.getsInstance().navigateTo((BaseActivity) mContext, NotesPurchaseActivity.class, bundle);
                                } else if (notification.getNotificationType() >= 2 && notification.getNotificationType() <= 7) {
                                    Bundle b = new Bundle();
                                    b.putInt(Constants.BundleKey.POST, messageMeta.getData().get(0));
                                    ExplicitIntent.getsInstance().navigateTo((ActivitiesNotificationActivity) mContext, PostDetailActivity.class, b);
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                ds.setColor(ContextCompat.getColor(mContext, R.color.textcolor_room_name));
                                applyCustomTypeFace(ds, boldTypeFace);
                                ds.setUnderlineText(true);
                            }
                        }, index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        break;

                    case ActivityMessage.MessageType.MEETUP:

                        stringBuilder.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                if (messageMeta.getData() != null && !messageMeta.getData().isEmpty()) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(Constants.POST_ID, messageMeta.getData().get(0));
                                    ExplicitIntent.getsInstance().navigateForResult((BaseActivity) mContext, PostDetailActivity.class, Constants.REQUEST_CODE.REQUEST_POST_DETAIL, bundle);
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                ds.setColor(ContextCompat.getColor(context, R.color.textcolor_room_name));
                                applyCustomTypeFace(ds, boldTypeFace);
                            }
                        }, index, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        break;

                    default:
                        break;


                }

            }


        }

        if (mBinding.tvNotificationSubText.getText().toString().trim().isEmpty()) {
            mBinding.tvNotificationSubText.setVisibility(View.GONE);
        } else {
            mBinding.tvNotificationSubText.setVisibility(View.VISIBLE);

        }


        return stringBuilder;

    }

    private void showAccpect() {
        mBinding.rlAccpectReject.setVisibility(View.VISIBLE);
        mBinding.btnRejected.setVisibility(View.GONE);
        mBinding.btnAccpected.setText(mContext.getString(R.string.accepted));
        mBinding.btnAccpected.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_orange_rounded));
        mBinding.btnAccpected.setTextColor(ContextCompat.getColor(mContext, R.color.app_white));
        mBinding.btnAccpected.setEnabled(false);
    }

    private void showViewAcppectReject() {
        mBinding.rlAccpectReject.setVisibility(View.VISIBLE);
        mBinding.btnAccpected.setVisibility(View.VISIBLE);
        mBinding.btnRejected.setVisibility(View.VISIBLE);
        mBinding.btnAccpected.setEnabled(true);
        mBinding.btnAccpected.setText(mContext.getString(R.string.accept));
        mBinding.btnAccpected.setBackground(ContextCompat.getDrawable(mContext, R.drawable.orange_btn_stroke));
        mBinding.btnRejected.setBackground(ContextCompat.getDrawable(mContext, R.drawable.orange_btn_stroke));
        mBinding.btnAccpected.setTextColor(ContextCompat.getColor(mContext, R.color.bright_teal));
        mBinding.btnRejected.setTextColor(ContextCompat.getColor(mContext, R.color.bright_teal));

    }

    private void showReject() {
        mBinding.rlAccpectReject.setVisibility(View.VISIBLE);
        mBinding.btnAccpected.setVisibility(View.VISIBLE);
        mBinding.btnRejected.setVisibility(View.GONE);
        mBinding.btnAccpected.setText(mContext.getString(R.string.rejected));
        mBinding.btnAccpected.setTextColor(ContextCompat.getColor(mContext, R.color.app_white));
        mBinding.btnAccpected.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_orange_rounded));
        mBinding.btnAccpected.setEnabled(false);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        paint.setTypeface(tf);
    }
}
