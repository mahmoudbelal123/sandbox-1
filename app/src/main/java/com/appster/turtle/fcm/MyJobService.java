/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */
package com.appster.turtle.fcm;

import android.annotation.SuppressLint;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


/**
 * Created by atul on 03/04/17.
 * Service class to receive notification scheduled by Fire base Messaging Service class. The same is re-directed
 * to the corresponding screen or handler class.
 */

@SuppressLint("Registered")
public class MyJobService extends JobService {

    public static final String PUSH_ACTION_STRING = "com.fcm.msg";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

}
