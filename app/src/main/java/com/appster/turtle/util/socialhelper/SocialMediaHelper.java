/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.socialhelper;

import android.content.Intent;

import com.appster.turtle.ui.BaseActivity;

import static com.appster.turtle.util.LogUtils.makeLogTag;

public class SocialMediaHelper {
    private static final String TAG = makeLogTag(SocialMediaHelper.class);
    private static SocialGoogleHelper socialGoogleHelper;
    BaseActivity mActivity = null;
    private SocialType mLoginType = SocialType.DEFAULT;
    private SocialAuthListener mProfileListener = null;
    private SocialFacebookHelper mFacebookHelper;

    public SocialMediaHelper(BaseActivity activity, SocialAuthListener listener) {
        this.mActivity = activity;
        this.mProfileListener = listener;
    }

    public static void logoutSession() {
        SocialFacebookHelper.logout();
        logoutGoogleClient();
    }

    public static void logoutGoogleClient() {
        if (socialGoogleHelper != null && socialGoogleHelper.mGoogleApiClient != null) {
            if (!socialGoogleHelper.mGoogleApiClient.isConnected())
                socialGoogleHelper.mGoogleApiClient.connect();

            if (socialGoogleHelper.mGoogleApiClient.isConnected())
                socialGoogleHelper.signOut();
        }
    }

    public void clear() {
        mActivity = null;
        socialGoogleHelper = null;
        mFacebookHelper = null;
    }

    public void setSocialType(SocialType socialType) {
        mLoginType = socialType;
    }

    public void initProcess() {
        switch (mLoginType) {
            case FACEBOOK:
                if (mFacebookHelper == null)
                    mFacebookHelper = new SocialFacebookHelper(mActivity, mProfileListener);
                else
                    mFacebookHelper.setActivity(mActivity, mProfileListener);

                mFacebookHelper.facebookLogin();
                break;
            case GOOGLE:
                if (socialGoogleHelper == null) {
                    socialGoogleHelper = new SocialGoogleHelper(mActivity, mProfileListener);
                    socialGoogleHelper.initialize();
                } else
                    socialGoogleHelper.setActivity(mActivity, mProfileListener);

                socialGoogleHelper.googleLogin();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (mLoginType) {
            case FACEBOOK:
                mFacebookHelper.getCallbackManager().onActivityResult(requestCode, resultCode, data);
                break;
            case GOOGLE:
                switch (requestCode) {
                    case SocialGoogleHelper.RC_SIGN_IN:
                        socialGoogleHelper.onActivityResult(requestCode, resultCode, data);
                        break;
                }
                break;
        }
    }

}
