/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util.socialhelper;

/**
 * Callback interface for dialog requests.
 */

public interface SocialAuthListener<T> {

    /**
     * Called when data recived from server. Executed by the thread that
     * initiated the dialog.
     *
     * @param provider provider name
     * @param t        return type
     */
    void onExecute(SocialType provider, T t);

    /**
     * Called when a dialog has an error. Executed by the thread that initiated
     * the dialog.
     */
    void onError(SocialAuthError e);

}
