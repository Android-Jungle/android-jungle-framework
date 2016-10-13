/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.jungle.base.app.BaseApplication;

public final class VersionUtils {

    /**
     * 2.1  API 7
     */
    public static boolean isEclair_MR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1;
    }

    /**
     * 2.2  API 8
     */
    public static boolean isFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * 2.3  API 9
     */
    public static boolean isGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * 3.0  API 11
     */
    public static boolean isHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 3.1  API 13
     */
    public static boolean isHoneycombMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    /**
     * 4.0  API 14
     */
    public static boolean isIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * 4.1  API 16
     */
    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 5.0  API 21
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 6.0  API 23
     */
    public static boolean isMarshMallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static int getSystemVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getAppVersionName() {
        Context context = BaseApplication.getAppContext();
        PackageInfo pkgInfo;

        try {
            pkgInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pkgInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getAppVersionCode() {
        Context context = BaseApplication.getAppContext();
        PackageInfo pkgInfo;

        try {
            pkgInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pkgInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getAppId() {
        String appId = MiscUtils.getMetaData("AppId");
        try {
            return Integer.parseInt(appId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getChannelId() {
        return MiscUtils.getMetaData("ChannelId");
    }
}
