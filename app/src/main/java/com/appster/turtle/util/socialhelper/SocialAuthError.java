/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.socialhelper;

import com.appster.turtle.util.LogUtils;

import static com.appster.turtle.util.LogUtils.makeLogTag;

/**
 * Wrapper class for handling errors via the listener
 */
public class SocialAuthError extends Throwable {

    private static final String TAG = makeLogTag(SocialAuthError.class);
    private final Exception innerException;


    /**
     * Constructor
     *
     * @param message User readable MESSAGE for the error
     * @param e       Inner exception that may be used for further debugging
     */
    public SocialAuthError(String message, Exception e) {
        super(message);
        this.innerException = e;
        LogUtils.LOGD("SocialAuthError", e.toString());
    }

    /**
     * Returns the inner exception
     *
     * @return Inner exception
     */
    public Exception getInnerException() {
        return innerException;
    }

}
