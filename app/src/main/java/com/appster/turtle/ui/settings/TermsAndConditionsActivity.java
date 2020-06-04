/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.settings;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appster.turtle.BuildConfig;
import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityPrivacyPolicyBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
/*
* Activity for term and condition
 */
@SuppressLint("SetJavaScriptEnabled")
public class TermsAndConditionsActivity extends BaseActivity {

    private ActivityPrivacyPolicyBinding mBinding;


    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy);

        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, getString(R.string.terms_and_conditions_caps));
        mBinding.header.ivIconStart.setImageResource(R.drawable.ic_back_black);

        mBinding.wvPrivacyPolicy.getSettings().setJavaScriptEnabled(true);

        String url = BuildConfig.BASE_URL_TERMS_AND_CONDITIONS + Constants.URLS.TERMS_AND_CONDITIONS;
        mBinding.wvPrivacyPolicy.setWebViewClient(notFullscreenWebView);
        mBinding.wvPrivacyPolicy.loadUrl(url);

    }

    private WebViewClient notFullscreenWebView = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            view.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mBinding.progressBar1.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    };
}
