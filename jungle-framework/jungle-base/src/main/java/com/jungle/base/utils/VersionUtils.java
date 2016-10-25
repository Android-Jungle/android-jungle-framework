/**
 * Android Jungle framework project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    /**
     * 7.0  API 24
     */
    public static boolean isNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
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

    public static String getAppId() {
        return MiscUtils.getMetaData("AppId");
    }

    public static String getChannelId() {
        return MiscUtils.getMetaData("ChannelId");
    }
}
