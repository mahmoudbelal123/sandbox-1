/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.socialhelper;

import android.content.Intent;

import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.util.StringUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.appster.turtle.util.LogUtils.makeLogTag;

public class SocialGoogleHelper implements GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 9001;
    private static final String TAG = makeLogTag(SocialGoogleHelper.class);
    public GoogleApiClient mGoogleApiClient; //NOSONAR
    BaseActivity mActivity = null;
    private SocialAuthListener mProfileListener = null;

    public SocialGoogleHelper(BaseActivity activity, SocialAuthListener listener) {
        this.mActivity = activity;
        this.mProfileListener = listener;

    }

    public void setActivity(BaseActivity mActivity, SocialAuthListener listener) {
        this.mActivity = mActivity;
        this.mProfileListener = listener;
    }


    public void initialize() {

        // [START configure_signin]
        // Configure sign-in to request the User's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this.mActivity)
                .enableAutoManage(this.mActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

    }

    public void googleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //LogUtils.LOGD(TAG, "onConnectionFailed:" + connectionResult);
        if (mProfileListener != null)
            mProfileListener.onError(new SocialAuthError("Null", new NullPointerException()));
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        // Signed in successfully, show authenticated UI. 
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
            final SocialProfile profile = new SocialProfile();
            profile.setSocialType(SocialType.GOOGLE.name());
            profile.setProviderId(acct.getId() + "");
            if (!StringUtils.isNullOrEmpty(acct.getDisplayName())) {
                String[] names = acct.getDisplayName().split(" ");
                if (names.length > 0)
                    profile.setFirstName(names[0]);
                if (names.length > 1)
                    profile.setLastName(names[1]);
            }
            profile.setEmail(acct.getEmail());
            profile.setProfileImageURL(acct.getPhotoUrl() + "");
            mProfileListener.onExecute(SocialType.GOOGLE, profile);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
            mProfileListener.onError(new SocialAuthError("Null", new NullPointerException()));
        }
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
    }

}
