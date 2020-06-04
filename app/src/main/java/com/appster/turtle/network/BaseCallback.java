/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.network;

import com.appster.turtle.R;
import com.appster.turtle.ui.BaseActivity;
import com.appster.turtle.ui.Constants;
import com.appster.turtle.util.StringUtils;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.UnknownHostException;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorFailedException;
import rx.schedulers.Schedulers;

public abstract class BaseCallback<T extends BaseResponse> {

    private final WeakReference<BaseActivity> ref;
    private final Observable<T> observable;
    private final Subscription subscription;
    private boolean showSnackbarOnFail = true;
    private boolean isProgress = true;

    public BaseCallback(BaseActivity activity, Observable<T> observable, boolean isProgress) {
        ref = new WeakReference<>(activity);
        this.observable = observable;
        this.isProgress = isProgress;
        if (isProgress)
            ref.get().showProgressBar();
        subscription = setObservable();


    }

    public BaseCallback(BaseActivity activity, Observable<T> observable, boolean isProgress, boolean showSnackbarOnFail) {
        ref = new WeakReference<>(activity);
        this.observable = observable;
        this.isProgress = isProgress;
        this.showSnackbarOnFail = showSnackbarOnFail;
        if (isProgress)
            ref.get().showProgressBar();
        subscription = setObservable();


    }

    public BaseCallback(BaseActivity activity, Observable<T> observable) {
        ref = new WeakReference<>(activity);
        this.observable = observable;

        subscription = setObservable();

    }

    public void cancel() {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    private void showMessage(String message) {
        if (showSnackbarOnFail)
            StringUtils.displaySnackBar(ref.get().findViewById(android.R.id.content), message);


    }

    private Subscription setObservable() {


        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    if (isProgress)
                        ref.get().hideProgressBar();


                    if (throwable instanceof ConnectException || throwable instanceof UnknownHostException) {
                        showMessage(ref.get().getString(R.string.no_internet_msg));
                    } else {
                        showMessage(ref.get().getString(R.string.common_err_msg));
                    }
                    onFail();
                    //noinspection unchecked
                    return (T) (new BaseResponse(true));
                })
                .subscribe(response -> {

                    if (response.isError())
                        return;
                    if (isProgress)
                        ref.get().hideProgressBar();


                    if (response.getMeta() != null) {
                        if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_SUCCESS) {
                            try {
                                onSuccess(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.BANK_DETAILS_NOT_ADDED) {

                            //do not show snackbar
                            onFail();

                        } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_AUTH_ERROR || response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_AUTH_ERROR_2 || response.getMeta().getCode() == Constants.RESPONSE_CODE.UNIVERSITY_INACTIVE) {


                            ref.get().logoutAuth(response.getMeta().getMessage());

                        } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_AUTH_EMAIL_ERROR) {


                            onSuccess(response);

                        } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_AUTH_PASSWORD_ERROR) {


                            onSuccess(response);

                        } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.RESPONSE_AUTH_RESET_EMAIL_ERROR) {


                            onSuccess(response);

                        } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.UNIVERSITY_INACTIVE_USER) {


                            onSuccess(response);

                        } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.EMAIL_CHECK_FAIL) {
                            onSuccess(response);
                        } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.LOGIN_ID_ERROR) {
                            onSuccess(response);
                        } else if (response.getMeta().getCode() == Constants.RESPONSE_CODE.UNVI_CHECK) {
                            onSuccess(response);
                        } else {

                            showMessage(response.getMeta().getMessage());


                            onFail();
                        }
                    } else {
                        showMessage(ref.get().getString(R.string.common_err_msg));

                        onFail();
                    }

                }, throwable -> {
                    throw new OnErrorFailedException(throwable);
                });
    }


    /**
     * Invoked when Successful response sent from server.
     *
     * @param response Response from server
     */
    public abstract void onSuccess(T response);

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     */
    public abstract void onFail();


}
