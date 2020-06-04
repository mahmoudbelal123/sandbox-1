/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.appster.turtle.R;
import com.appster.turtle.adapter.SettingsAdapter;
import com.appster.turtle.adapter.viewholder.SettingsViewHolder;
import com.appster.turtle.databinding.ActivitySettingsBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.payment.BankAccountActivity;
import com.appster.turtle.ui.payment.CardListActivity;
import com.appster.turtle.util.ExplicitIntent;

import java.util.Arrays;
/*
* Activity for setting
 */
public class SettingsActivity extends BaseActivity implements SettingsViewHolder.IOnSettingSelected {

    ActivitySettingsBinding mBinding;

    @Override
    public String getActivityName() {
        return getClass().getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        initUI();
    }

    private void initUI() {
        setUpHeader(mBinding.header.clHeader, getString(R.string.account_settings), "", R.drawable.ic_back_black);

        SettingsAdapter settingsAdapter = new SettingsAdapter(Arrays.asList(getSettingList()), this);

        mBinding.rvSettings.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvSettings.setAdapter(settingsAdapter);
    }

    private String[] getSettingList() {
        return new String[]{
                getString(R.string.change_password),
                getString(R.string.bank_details),
                getString(R.string.manage_payments),
                getString(R.string.privacy_policy),
                getString(R.string.terms_and_conditions),
                getString(R.string.invite)
        };
    }

    @Override
    public void settingSelected(int position) {
        switch (position) {
            case 0:
                ExplicitIntent.getsInstance().navigateTo(this, ChangePasswordActivity.class);
                break;

            case 1:

                Bundle b = new Bundle();
                b.putBoolean(Constants.BundleKey.FROM_SETTINGS, true);
                ExplicitIntent.getsInstance().navigateTo(this, BankAccountActivity.class, b);
                break;

            case 2:

                ExplicitIntent.getsInstance().navigateTo(this, CardListActivity.class);

                break;

            case 3:
                ExplicitIntent.getsInstance().navigateTo(this, PrivacyPolicyActivity.class);
                break;

            case 4:
                ExplicitIntent.getsInstance().navigateTo(this, TermsAndConditionsActivity.class);
                break;


            case 5:

                ExplicitIntent.getsInstance().navigateTo(SettingsActivity.this, InviteFriendsActivity.class);

                break;

            default:
                break;
        }
    }
}
