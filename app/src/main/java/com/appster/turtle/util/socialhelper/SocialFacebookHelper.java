/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.socialhelper;

import android.os.Bundle;

import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.util.LogUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;

import java.util.Arrays;

import static com.appster.turtle.util.LogUtils.makeLogTag;

/**
 *
 */
public class SocialFacebookHelper {
    private static final String TAG = makeLogTag(SocialFacebookHelper.class);
    private static final String PROFILE_IMAGE_URL = "http://graph.facebook.com/%1$s/picture";
    private BaseActivity mActivity = null;
    private SocialAuthListener mProfileListener = null;
    private CallbackManager mCallbackManager;

    public SocialFacebookHelper(BaseActivity activity, SocialAuthListener listener) {
        this.mActivity = activity;
        this.mProfileListener = listener;
        // Facebook SDK initialize
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
    }

    public static void logout() {
        try {
            final LoginManager manager = LoginManager.getInstance();
            if (manager != null)
                manager.logOut();
        } catch (Exception x) {
            LogUtils.printStackTrace(x);

        }
    }

    public void setActivity(BaseActivity mActivity, SocialAuthListener listener) {
        this.mActivity = mActivity;
        this.mProfileListener = listener;
    }

    public CallbackManager getCallbackManager() {
        return mCallbackManager;
    }

    public void facebookLogin() {
        final LoginManager manager = LoginManager.getInstance();
        manager.setLoginBehavior(LoginBehavior.WEB_ONLY);
        manager.logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "email", "user_birthday"));
        // Callback registration

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mActivity.showProgressBar();
                // App code
                //ProfileTracker.getCurrentProfile();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        (object, response) -> {
                            mActivity.hideProgressBar();
                            if (object == null)
                                mProfileListener.onError(new SocialAuthError("Null", new NullPointerException()));
                            try {
                                // Application code
                                SocialProfile profile = new SocialProfile();
                                profile.setSocialType(SocialType.FACEBOOK.name());
                                if (object != null) {
                                    profile.setProviderId(object.optString("id", null));

                                    if (object.has("name")) {
                                        String[] names = object.getString("name").split(" ");
                                        if (names.length > 0)
                                            profile.setFirstName(names[0]);
                                        if (names.length > 1)
                                            profile.setLastName(names[1]);
                                    } else {
                                        profile.setFirstName(object.optString("first_name", null));
                                        profile.setLastName(object.optString("last_name", null));
                                    }
                                    profile.setProfileImageURL("http://graph.facebook.com/" + object.optString("id") + "/picture?type=square");
                                    profile.setEmail(object.optString("email", null));
                                    if (object.has("gender"))
                                        profile.setGender(object.optString("gender", null));

                                    profile.setProfileImageURL(String.format(PROFILE_IMAGE_URL,
                                            object.getString("id")));

                                    mProfileListener.onExecute(SocialType.FACEBOOK, profile);
                                }
                            } catch (JSONException e) {
                                LogUtils.printStackTrace(e);
                                mProfileListener.onError(new SocialAuthError("Exception", e));
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name,first_name,last_name,age_range,email,gender,birthday");


                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                LogUtils.LOGE(TAG, "");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                LogUtils.LOGE(TAG, "");
            }
        });
    }
}
