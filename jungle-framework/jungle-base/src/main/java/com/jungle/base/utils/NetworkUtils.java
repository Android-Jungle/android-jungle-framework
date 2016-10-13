/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import com.jungle.base.app.BaseApplication;
import com.jungle.base.common.HoldWeakRef;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    private static BroadcastReceiver mNetworkBroadcastReceiver;


    public static enum NetType {
        None,
        Wifi,
        G2,
        G3,
        G4,
        Cable,
    }

    public static class NetworkSwitchListener
            implements NetworkStateListener {

        @Override
        public void onNone2Mobile() {
            onConnectNetwork();
        }

        @Override
        public void onNone2Wifi() {
            onConnectNetwork();
        }

        @Override
        public void onMobile2Wifi() {
            onNetworkSwitch();
        }

        @Override
        public void onMobile2None() {
            onDisconnectNetwork();
        }

        @Override
        public void onWifi2None() {
            onDisconnectNetwork();
        }

        @Override
        public void onWifi2Mobile() {
            onNetworkSwitch();
        }


        public void onDisconnectNetwork() {
        }

        public void onConnectNetwork() {
        }

        public void onNetworkSwitch() {
        }
    }


    public interface NetworkStateListener {
        void onNone2Mobile();

        void onNone2Wifi();

        void onMobile2Wifi();

        void onMobile2None();

        void onWifi2None();

        void onWifi2Mobile();
    }

    public static class SimpleNetworkStateListener
            implements NetworkStateListener {
        @Override
        public void onNone2Mobile() {
        }

        @Override
        public void onNone2Wifi() {
        }

        @Override
        public void onMobile2Wifi() {
        }

        @Override
        public void onMobile2None() {
        }

        @Override
        public void onWifi2None() {
        }

        @Override
        public void onWifi2Mobile() {
        }
    }


    private static enum NetworkChangeType {
        Invalid,
        None2Mobile,
        None2Wifi,
        Mobile2Wifi,
        Mobile2None,
        Wifi2None,
        Wifi2Mobile,
    }


    private static boolean mNetTypeInitialized = false;
    private static NetworkUtils.NetType mCurrNetType = NetworkUtils.NetType.None;
    private static List<WeakReference<NetworkStateListener>> mNetworkStateListener =
            new LinkedList<WeakReference<NetworkStateListener>>();


    public static NetType getNetworkType() {
        mCurrNetType = getNetworkTypeNoOverride();
        return mCurrNetType;
    }

    public static NetType getNetworkTypeNoOverride() {
        return getNetworkTypeInternal(BaseApplication.getApp());
    }

    public static boolean hasNetwork() {
        return getInitializedNetType() != NetType.None;
    }

    public static boolean isWifi() {
        return isWifi(getInitializedNetType());
    }

    public static boolean isMobile() {
        return isMobile(getInitializedNetType());
    }

    private static NetType getInitializedNetType() {
        if (!mNetTypeInitialized) {
            getNetworkType();
            mNetTypeInitialized = true;
        }

        return mCurrNetType;
    }

    public static boolean isWifi(NetType netType) {
        return netType == NetType.Wifi;
    }

    public static boolean isMobile(NetType netType) {
        return netType == NetType.G2 || netType == NetType.G3 || netType == NetType.G4;
    }

    public static void addNetworkStateListener(@HoldWeakRef NetworkStateListener listener) {
        mNetworkStateListener.add(new WeakReference<NetworkStateListener>(listener));
    }

    public static void removeNetworkStateListener(NetworkStateListener l) {
        for (Iterator<WeakReference<NetworkStateListener>> iterator =
             mNetworkStateListener.iterator(); iterator.hasNext(); ) {

            WeakReference<NetworkStateListener> ref = iterator.next();
            NetworkStateListener listener = ref.get();
            if (listener == null || listener == l) {
                iterator.remove();
            }
        }
    }

    public static void initializeNetworkUtils(Context context) {
        mNetworkBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkUtils.NetType oldNetType = mCurrNetType;
                mCurrNetType = getNetworkType();

                LogUtils.e(TAG, "NetworkUtils onReceive!! oldType = %s, newType = %s.",
                        oldNetType.toString(), mCurrNetType.toString());

                if (oldNetType == mCurrNetType) {
                    return;
                }

                NetworkChangeType type = NetworkChangeType.Invalid;
                if (oldNetType == NetType.None) {
                    if (isMobile(mCurrNetType)) {
                        type = NetworkChangeType.None2Mobile;
                    } else if (isWifi(mCurrNetType)) {
                        type = NetworkChangeType.None2Wifi;
                    }
                } else {
                    if (isMobile(oldNetType)) {
                        if (mCurrNetType == NetType.None) {
                            type = NetworkChangeType.Mobile2None;
                        } else if (isWifi(mCurrNetType)) {
                            type = NetworkChangeType.Mobile2Wifi;
                        }
                    } else if (isWifi(oldNetType)) {
                        if (mCurrNetType == NetType.None) {
                            type = NetworkChangeType.Wifi2None;
                        } else if (isMobile(mCurrNetType)) {
                            type = NetworkChangeType.Wifi2Mobile;
                        }
                    }
                }

                notifyStateChange(type);
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mNetworkBroadcastReceiver, intentFilter);

        getNetworkType();
    }

    public static void unInitializeNetworkUtils(Context context) {
        context.unregisterReceiver(mNetworkBroadcastReceiver);
    }

    private static void notifyStateChange(NetworkChangeType type) {
        if (type == NetworkChangeType.Invalid) {
            return;
        }

        if (type == NetworkChangeType.None2Mobile) {
            LogUtils.i(TAG, "None -> 2/3/4/G");
        } else if (type == NetworkChangeType.None2Wifi) {
            LogUtils.i(TAG, "None -> Wifi");
        } else if (type == NetworkChangeType.Mobile2Wifi) {
            LogUtils.i(TAG, "2/3/4/G -> Wifi");
        } else if (type == NetworkChangeType.Mobile2None) {
            LogUtils.i(TAG, "2/3/4/G -> None");
        } else if (type == NetworkChangeType.Wifi2None) {
            LogUtils.i(TAG, "Wifi -> None");
        } else if (type == NetworkChangeType.Wifi2Mobile) {
            LogUtils.i(TAG, "Wifi -> 2/3/4/G");
        }

        for (WeakReference<NetworkStateListener> ref : mNetworkStateListener) {
            NetworkStateListener listener = ref.get();
            if (listener == null) {
                continue;
            }

            if (type == NetworkChangeType.None2Mobile) {
                listener.onNone2Mobile();
            } else if (type == NetworkChangeType.None2Wifi) {
                listener.onNone2Wifi();
            } else if (type == NetworkChangeType.Mobile2Wifi) {
                listener.onMobile2Wifi();
            } else if (type == NetworkChangeType.Mobile2None) {
                listener.onMobile2None();
            } else if (type == NetworkChangeType.Wifi2None) {
                listener.onWifi2None();
            } else if (type == NetworkChangeType.Wifi2Mobile) {
                listener.onWifi2Mobile();
            }
        }
    }

    public static NetType getNetworkTypeInternal(Context context) {
        ConnectivityManager mgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mgr.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            return NetType.None;
        }

        switch (networkInfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
                return NetType.Wifi;

            case ConnectivityManager.TYPE_ETHERNET:
                return NetType.Cable;

            case ConnectivityManager.TYPE_MOBILE:
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return NetType.G2;

                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return NetType.G3;

                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetType.G4;

                    default:
                        String typeName = networkInfo.getSubtypeName();
                        if (typeName.equalsIgnoreCase("TD-SCDMA")
                                || typeName.equalsIgnoreCase("WCDMA")
                                || typeName.equalsIgnoreCase("CDMA2000")) {
                            return NetType.G3;
                        }
                        break;
                }
                break;

            default:
                break;
        }

        return NetType.G2;
    }
}
