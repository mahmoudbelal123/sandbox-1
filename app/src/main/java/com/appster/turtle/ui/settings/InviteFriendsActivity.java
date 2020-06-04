/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.ui.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.appster.turtle.R;
import com.appster.turtle.databinding.ActivityHomeBinding;
import com.appster.turtle.databinding.ActivityInviteFriendsBinding;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.util.LogUtils;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.group.Group;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.util.ArrayList;
import java.util.List;
/*
* Activity for invite friend
 */
public class InviteFriendsActivity extends BaseActivity implements BaseActivity.PermissionI {
    private static final int RESULT_PICK_CONTACT = 10223;
    private ActivityHomeBinding homeBinding;
    private boolean isPermissionGiven;
    private boolean isEmail;
    private List<Group> groups;
    private List<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityInviteFriendsBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_invite_friends);

        setUpHeader(mBinding.header.clHeader, getString(R.string.invite_friend), R.drawable.back_arrow, 0);


    }

    @Override
    public String getActivityName() {
        return InviteFriendsActivity.class.getName();
    }

    public void inivteClick(View view) {
        switch (view.getId()) {
            case R.id.tv_via_email:
            case R.id.tv_via_email_des:
//                isEmail = true;
//                fetchContacts();

                sendEmail();
                break;
            case R.id.tv_via_sms:
            case R.id.tv_via_sms_des:
//                isEmail = false;
//                fetchContacts();
                sendSMS();

                break;

        }

    }

    private void fetchContacts() {

        if (isPermissionGiven) {

            Intent intent = new Intent(this, ContactPickerActivity.class)
                    .putExtra(ContactPickerActivity.EXTRA_THEME, R.style.Theme_Light_Base)
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                    .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, true)
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
            startActivityForResult(intent, RESULT_PICK_CONTACT);
        } else
            checkPermissions(new String[]{Manifest.permission.READ_CONTACTS}, this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_PICK_CONTACT && resultCode == Activity.RESULT_OK &&
                data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {

            contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
            groups = (List<Group>) data.getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA);

            if (isEmail)
                sendEmail();
            else
                sendSMS();

        }
    }

    @Override
    public void onPermissionGiven() {
        isPermissionGiven = true;
        fetchContacts();
    }

    private void sendSMS() {

        StringBuffer sBuffer = new StringBuffer("smsto:");


        try {
            if (groups != null && !groups.isEmpty()) {
                for (Group group : groups) {
                    for (Contact contact : group.getContacts()) {

                        sBuffer.append(contact.getPhone(0)).append(";");
                    }
                }
            }
            if (contacts != null && !contacts.isEmpty()) {
                for (Contact contact : contacts) {
                    sBuffer.append(contact.getPhone(0)).append(";");

                }
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }

        if (sBuffer.toString().contains(";"))
            sBuffer.deleteCharAt(sBuffer.length() - 1);

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(sBuffer.toString()));
        smsIntent.putExtra("sms_body", getString(R.string.email_text));
        startActivity(smsIntent);
    }

    private void sendEmail() {

        ArrayList<String> emails = new ArrayList<>();
        try {
            if (groups != null && !groups.isEmpty()) {
                for (Group group : groups) {
                    for (Contact contact : group.getContacts()) {

                        emails.add(contact.getEmail(0));
                    }
                }
            }
            if (contacts != null && !contacts.isEmpty()) {
                for (Contact contact : contacts) {
                    emails.add(contact.getEmail(0));


                }
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
        }


        Intent intent = null;
        intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, emails.toArray(new String[emails.size()]));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_sub));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.email_text));
        intent.setType("message/rfc822");
        startActivity(intent);
    }
}
