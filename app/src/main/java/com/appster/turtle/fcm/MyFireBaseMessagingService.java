/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */
package com.appster.turtle.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.appster.turtle.R;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.notes.NotesPurchaseActivity;
import com.appster.turtle.ui.notes.ReviewListActivity;
import com.appster.turtle.ui.post.PostDetailActivity;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.util.ActivityMessage;
import com.appster.turtle.util.GlideApp;
import com.appster.turtle.util.LogUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by atul on 03/04/17.
 * This class will receive any new notification sent by FCM messaging server.
 */

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFireBaseMessagingService.class.getSimpleName();
    private NotificationManager notificationManager;

    /**
     * Called when MESSAGE is received.
     *
     * @param remoteMessage Object representing the MESSAGE received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> stringMap = remoteMessage.getData();
            if (stringMap != null) {
                scheduleJob(stringMap);
            }
        }
    }

    /**
     * Schedule a job using Fire base JobDispatcher.
     */
    @SuppressLint("WrongConstant")
    private void scheduleJob(Map<String, String> stringMap) {

        //notif count
        //MESSAGE
        String message = stringMap.get(Constants.MESSAGE);

        String jsonString = stringMap.get(Constants.DATA);
      //  String badge = stringMap.get(Constants.BADGE);
        //LogUtils.LOGD("TAG",stringMap.keySet().toString());
        JSONObject jsonObject = null;
        String url = "";
        Bitmap bitmap = null;
        int notificationId = 0;
        try {
            jsonObject = new JSONObject(jsonString);
            //senderIds
            List<Integer> senderIds = new ArrayList<>();

            if (jsonObject.has(Constants.SENDERID)) {
                senderIds.add(jsonObject.getInt(Constants.SENDERID));
            } else if (jsonObject.has(Constants.SENDERIDS)) {
                JSONArray senderIdsJsonArray = jsonObject.getJSONArray(Constants.SENDERIDS);
                for (int i = 0; i < senderIdsJsonArray.length(); i++) {
                    senderIds.add(senderIdsJsonArray.getInt(i));
                }
            }
            if (jsonObject.has(Constants.ATTACHEMENTS)) {

                if (jsonObject.getJSONObject(Constants.ATTACHEMENTS).has(Constants.ATTACHEMENTS_URL)) {
                    url = jsonObject.getJSONObject(Constants.ATTACHEMENTS).getString(Constants.ATTACHEMENTS_URL);
                }
            }

            //notificationId
            notificationId = jsonObject.getInt(Constants.NOTIFICATION_ID);
         //  ShortcutBadger.applyCount(MyFireBaseMessagingService.this, Integer.parseInt(badge));
        } catch (JSONException e) {
            LogUtils.printStackTrace(e);
        }

        if (!url.isEmpty()) {

            try {
                bitmap = GlideApp
                        .with(getApplicationContext())
                        .asBitmap()
                        .load(url)
                        .submit().get();

            } catch (InterruptedException | ExecutionException e) {
                LogUtils.LOGD(TAG, e.getMessage());
            }

        }

        NotificationCompat.Builder mBuilder;
        String name = "my_package_channel";
        String id = "M_CH_ID"; // The user-visible name of the channel.
        String description = "my_package_first_channel";
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (bitmap != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
                    mChannel.setDescription(description);
                    mChannel.enableVibration(true);
                    mChannel.setShowBadge(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notificationManager.createNotificationChannel(mChannel);
                }

                mBuilder =
                        new NotificationCompat.Builder(this, id);

            } else {
                mBuilder = new NotificationCompat.Builder(this);


            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.mipmap.ic_notification);
                int color = 0x000000;
                mBuilder.setColor(color);
            }else{
                mBuilder.setSmallIcon(R.mipmap.ic_app_icon);

            }

                    mBuilder.setContentTitle(getString(R.string.app_name))
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(message))
                    .setDefaults(Notification.DEFAULT_ALL).setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setPriority(Notification.PRIORITY_HIGH);

            // .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, name,  NotificationManager.IMPORTANCE_HIGH);
                    mChannel.setDescription(description);
                    mChannel.enableVibration(true);
                    mChannel.setShowBadge(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notificationManager.createNotificationChannel(mChannel);
                }
                mBuilder =
                        new NotificationCompat.Builder(this, id);

            } else {
                mBuilder = new NotificationCompat.Builder(this);

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.mipmap.ic_notification);
                int color = 0x000000;
                mBuilder.setColor(color);
            }else{
                mBuilder.setSmallIcon(R.mipmap.ic_app_icon);

            }
            mBuilder.setContentTitle(getString(R.string.app_name))
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setPriority(Notification.PRIORITY_HIGH);

            //
        }

        mBuilder.setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(sendNotify(jsonObject));
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        // notificationId allows you to update the notification later on.
        notificationManager.notify(notificationId, mBuilder.build());

        // TODO: 10/10/17 Handle notification click

        sendInAppNotification(message);

    }


    public Intent sendNotify(JSONObject jsonObject) {
        Intent resultIntent = new Intent(this, HomeMainActivity.class);

        try {
            int notificationType = jsonObject.getInt(Constants.NOTIFICATION_TYPE);

            if (notificationType == ActivityMessage.NotificationType.FRIEND_REQUEST) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.USER_ID, jsonObject.getInt(Constants.SENDERID));
                resultIntent = new Intent(this, UserProfileActivity.class);
                resultIntent.putExtras(bundle);
            } else if (notificationType == ActivityMessage.NotificationType.NOTE_REVIEW_EVENT) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.NOTE_ID, jsonObject.getInt(Constants.NOTEID));
                bundle.putBoolean(Constants.IS_NEWADD, true);
                resultIntent = new Intent(this, ReviewListActivity.class);
                resultIntent.putExtras(bundle);
            } else if (notificationType == ActivityMessage.NotificationType.NOTE_PURCHASED_EVENT) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.NOTE_ID, jsonObject.getInt(Constants.NOTEID));
                bundle.putBoolean(Constants.IS_NEWADD, true);
                resultIntent = new Intent(this, NotesPurchaseActivity.class);
                resultIntent.putExtras(bundle);

            } else if (notificationType == ActivityMessage.NotificationType.POST_TEXT_COMMENT ||
                    notificationType == ActivityMessage.NotificationType.POST_PICTURE_COMMENT ||
                    notificationType == ActivityMessage.NotificationType.POST_VIDEO_COMMENT ||
                    notificationType == ActivityMessage.NotificationType.POST_GIF_COMMENT ||
                    notificationType == ActivityMessage.NotificationType.POST_POLL_COMMENT ||
                    notificationType == ActivityMessage.NotificationType.POST_MEETUP_COMMENT ||
                    notificationType == ActivityMessage.NotificationType.POST_TEXT_LIKE ||
                    notificationType == ActivityMessage.NotificationType.POST_PICTURE_LIKE ||
                    notificationType == ActivityMessage.NotificationType.POST_VIDEO_LIKE ||
                    notificationType == ActivityMessage.NotificationType.POST_GIF_LIKE ||
                    notificationType == ActivityMessage.NotificationType.POST_POLL_LIKE ||
                    notificationType == ActivityMessage.NotificationType.COMMENT_TAG ||
                    notificationType == ActivityMessage.NotificationType.POST_TEXT_COMMENT_LIKE ||
                    notificationType == ActivityMessage.NotificationType.POST_PICTURE_COMMENT_LIKE ||
                    notificationType == ActivityMessage.NotificationType.POST_VIDEO_COMMENT_LIKE ||
                    notificationType == ActivityMessage.NotificationType.POST_GIF_COMMENT_LIKE ||
                    notificationType == ActivityMessage.NotificationType.POST_POLL_COMMENT_LIKE ||
                    notificationType == ActivityMessage.NotificationType.POST_MEETUP_RESPONSE_ATTENDING ||
                    notificationType == ActivityMessage.NotificationType.POST_MEETUP_RESPONSE_INTERESTED ||
                    notificationType == ActivityMessage.NotificationType.POST_POLL_RESPONSE ||
                    notificationType == ActivityMessage.NotificationType.POST_MEETUP_EDIT ||
                    notificationType == ActivityMessage.NotificationType.POST_USER_TAG ||
                    notificationType == ActivityMessage.NotificationType.POST_USER_TAG_MEDIA ||
                    notificationType == ActivityMessage.NotificationType.POST_MEETUP_LIKE) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.POST_ID, jsonObject.getInt(Constants.POSTID));

                resultIntent = new Intent(this, PostDetailActivity.class);
                resultIntent.putExtras(bundle);

            } else if (notificationType == ActivityMessage.NotificationType.ROOM_REQUEST ||
                    notificationType == ActivityMessage.NotificationType.BECOME_ROOM_ADMIN ||
                    notificationType == ActivityMessage.NotificationType.ROOM_JOIN_REQUEST) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.BundleKey.ROOM_ID, jsonObject.getInt(Constants.ROOMID));
                resultIntent = new Intent(this, RoomActivityCoordinator.class);
                resultIntent.putExtras(bundle);
            }
        } catch (JSONException e) {
            LogUtils.LOGD(TAG, e.getMessage());
        }

        return resultIntent;
    }

    private void sendInAppNotification(String message) {
        Intent inAppNotificationIntent = new Intent(Constants.Notification.EVENT_NOTIFICATION);

        if (!TextUtils.isEmpty(message))
            inAppNotificationIntent.putExtra(Constants.Notification.MESSAGE, message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(inAppNotificationIntent);
    }

}
