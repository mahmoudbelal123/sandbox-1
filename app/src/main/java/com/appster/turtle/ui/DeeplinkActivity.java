package com.appster.turtle.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;

import com.appster.turtle.model.UserBaseModel;
import com.appster.turtle.ui.home.HomeMainActivity;
import com.appster.turtle.ui.rooms.RoomActivityCoordinator;
import com.appster.turtle.ui.tutorial.TutorialActivity;
import com.appster.turtle.util.ExplicitIntent;
import com.appster.turtle.util.LogUtils;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;

import java.util.List;
/*
* Activity for deeplink
 */
public class DeeplinkActivity extends AppCompatActivity {

    private int filter = 0;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            if (getIntent().getData() != null) {
                Uri uri = getIntent().getData();// this is the url
                filter = Integer.parseInt(uri.getAuthority());
                List<String> segments = uri.getPathSegments();// this is the url segments

                if (segments != null && !segments.isEmpty())
                    id = Integer.parseInt(segments.get(0));
            }

        } catch (Exception e) {
            LogUtils.LOGD("ERROR", e.getMessage());
        }

        navigateIfSignedIn();

    }

    private void navigateIfSignedIn() {
        UserBaseModel userBaseModel = PreferenceUtil.getUser();
        if (userBaseModel != null && userBaseModel.isEmailVerified() && userBaseModel.isProfileComplete() && !StringUtils.isNullOrEmpty(PreferenceUtil.getToken())) {
            finish();

            try {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(sendNotify());
                stackBuilder.getPendingIntent(
                        0, PendingIntent.FLAG_UPDATE_CURRENT).send();

            } catch (PendingIntent.CanceledException e) {
                LogUtils.LOGD("ERROR", e.getMessage());
            }
        } else {
            finish();
            ExplicitIntent.getsInstance().navigateTo(DeeplinkActivity.this, TutorialActivity.class);
        }
    }


    public Intent sendNotify() {
        Intent resultIntent = new Intent(this, HomeMainActivity.class);


        if (filter == 1) { //ROOM
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.BundleKey.ROOM_ID, id);
            resultIntent = new Intent(this, RoomActivityCoordinator.class);
            resultIntent.putExtras(bundle);
        }


        return resultIntent;
    }
}
