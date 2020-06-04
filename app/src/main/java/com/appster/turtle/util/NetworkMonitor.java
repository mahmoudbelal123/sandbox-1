/*
 *
 *   Copyright © 2017 Noise. All rights reserved.
 *   Created by Appster.
 *
 */

package com.appster.turtle.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

/*
    Utility for checking Network
 */
public class NetworkMonitor extends BroadcastReceiver {

    public static final int NETWORK_TYPE_FAST_3G = 2;
    public static final int NETWORK_TYPE_FAST_WIFI = 1;
    public static final int NETWORK_TYPE_NO_NETWORK = 4;
    public static final int NETWORK_TYPE_SLOW = 3;

    private static boolean mConnectionAvailable = false;
    private static Context mContext;

    private NetworkMonitor() {
    }

    public static void initialize(Context context) {
        NetworkMonitor.mContext = context;
        checkNetworkConnectivity(mContext);
    }

    public static void checkNetworkConnectivity(Context context) {
        try {
            mConnectionAvailable = false;
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                mConnectionAvailable = false;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Network[] networks = connectivity.getAllNetworks();
                    for (Network mNetwork : networks) {
                        NetworkInfo networkInfo = connectivity.getNetworkInfo(mNetwork);
                        if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                            mConnectionAvailable = true;
                            break;
                        }
                    }
                } else {
                    NetworkInfo[] info = connectivity.getAllNetworkInfo();
                    if (info != null) {
                        for (NetworkInfo anInfo : info) {
                            if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                                mConnectionAvailable = true;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.printStackTrace(e);
            mConnectionAvailable = false;
        }

    }

    public static String getNetworkOperatorName() {
        return ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
    }

    public static NetworkInfo getNetworkType(Context context) {
        if (context != null) {
            NetworkInfo networkinfo;
            try {
                networkinfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            } catch (Exception exception) {
                return null;
            }
            return networkinfo;
        } else {
            return null;
        }
    }

    public static NetState getNetwork() {
        int i = getNetworkType();
        switch (i) {
            case NETWORK_TYPE_SLOW:
                return NetState.NET_2G;
            case NETWORK_TYPE_FAST_3G:
                return NetState.NET_3G;
            case NETWORK_TYPE_FAST_WIFI:
                return NetState.NET_WIFI;
            default:
                return NetState.NET_UNKNOWN;
        }
    }

    public static boolean isNetworkAvailable() {
        return mConnectionAvailable;
    }

    private static int getNetworkType() {
        if (mContext != null) {
            ConnectivityManager connectivitymanager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivitymanager != null) {
                NetworkInfo networkinfo = connectivitymanager.getNetworkInfo(1);
                if (networkinfo != null && networkinfo.isConnected()) {
                    return 1;
                }
            }
            int i = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();
            if (i > 0 && i < 3) {
                return 3;
            }
            return i <= 2 ? 4 : 2;
        } else {
            return -1;
        }
    }

    public void onReceive(Context context, Intent intent) {
        checkNetworkConnectivity(context);
    }

    public enum NetState {
        NET_2G, NET_3G, NET_4G, NET_WIFI, NET_UNKNOWN
    }

}
