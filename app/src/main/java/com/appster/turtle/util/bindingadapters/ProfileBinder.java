package com.appster.turtle.util.bindingadapters;

import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.view.View;

import com.appster.turtle.model.User;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.ui.profile.UserProfileActivity;
import com.appster.turtle.util.ExplicitIntent;

/**
 * Copyright © 2017 Noise. All rights reserved.
 * Created by navdeepbedi on 05/02/18.
 */

public class ProfileBinder {


    @BindingAdapter("bindShowProfile")
    public static void bindShowProfile(View view, final User user) {

        view.setOnClickListener(view1 -> {

            if (user.getRoles()[0] != 2 && user.getRoles()[0] != 3) {

                Bundle i = new Bundle();
                i.putInt(Constants.USER_ID, user.getUserId());
                ExplicitIntent.getsInstance().navigateTo((BaseActivity) view1.getContext(), UserProfileActivity.class, i);
            }

        });

    }

    @BindingAdapter("bindShowProfileId")
    public static void bindShowProfileId(View view, final int id) {

        view.setOnClickListener(view1 -> {


            Bundle i = new Bundle();
            i.putInt(Constants.USER_ID, id);
            ExplicitIntent.getsInstance().navigateTo((BaseActivity) view1.getContext(), UserProfileActivity.class, i);

        });

    }


}
