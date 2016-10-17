/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import com.jungle.base.R;
import com.jungle.base.app.AppCore;
import com.jungle.base.app.BaseActivity;
import com.jungle.base.app.BaseApplication;
import com.jungle.base.common.OnRequestPermissionsResultListener;
import com.jungle.base.misc.JungleSize;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MiscUtils {

    public interface OnPermissionRequestListener {
        void onResult(Set<String> grantedPermissions);
    }


    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static void startActivity(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

    public static Drawable getAppIcon() {
        String pkgName = MiscUtils.getPackageName();
        PackageManager mgr = BaseApplication.getAppContext().getPackageManager();

        try {
            ApplicationInfo info = mgr.getApplicationInfo(pkgName, 0);
            return mgr.getApplicationIcon(info);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getPackageName() {
        return BaseApplication.getAppContext().getPackageName();
    }

    public static String getAppName() {
        String pkgName = MiscUtils.getPackageName();
        PackageManager mgr = BaseApplication.getAppContext().getPackageManager();

        try {
            ApplicationInfo info = mgr.getApplicationInfo(pkgName, 0);
            return String.valueOf(mgr.getApplicationLabel(info));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pkgName;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static void setImmersiveMode(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int option = decorView.getSystemUiVisibility();

        if (Build.VERSION.SDK_INT >= 14) {
            option ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        if (Build.VERSION.SDK_INT >= 16) {
            option ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 19) {
            //option ^= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }

        decorView.setSystemUiVisibility(option);
    }

    public static void rateAppInMarkets(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getAssetContent(String path) {
        StringBuilder builder = new StringBuilder();

        AssetManager mgr = BaseApplication.getApp().getAssets();

        try {
            InputStreamReader reader = new InputStreamReader(mgr.open(path));
            BufferedReader buffReader = new BufferedReader(reader);

            String line = "";
            while ((line = buffReader.readLine()) != null) {
                builder.append(line);
                builder.append("\r\n");
            }

            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public static String getMetaData(String metaKey) {
        Context context = BaseApplication.getAppContext();
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (info.metaData != null && info.metaData.containsKey(metaKey)) {
                Object value = info.metaData.get(metaKey);
                return value != null ? value.toString() : null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getIntMetaData(String metaKey) {
        Context context = BaseApplication.getAppContext();
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (info.metaData != null && info.metaData.containsKey(metaKey)) {
                return info.metaData.getInt(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean installApk(Context context, String apkPath) {
        File apkFile = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile),
                "application/vnd.android.package-archive");

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isScreenAutoRotate(Context context) {
        int gravity = 0;
        try {
            gravity = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return gravity == 1;
    }

    public static boolean openUrlByBrowser(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        final String HTTP_TAG = "http://";
        url = url.trim();
        if (url.indexOf(HTTP_TAG) != 0) {
            url = HTTP_TAG + url;
        }

        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean callPhoneNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean dialPhoneNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static boolean isPhoneNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        Pattern pattern = Pattern.compile("^[1]([3|4|5|7|8][0-9])[0-9]{8}$");
        return pattern.matcher(str).matches();
    }

    public static boolean isEmailAddress(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(str);
        return matcher.matches();
    }

    public static boolean isAccountId(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        Pattern pattern = Pattern.compile("^[0-9A-Za-z_].*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static void writeBytesToParcel(Parcel dest, byte[] buff) {
        int len = buff != null ? buff.length : 0;
        dest.writeInt(len);
        if (len > 0) {
            dest.writeByteArray(buff);
        }
    }

    public static byte[] readBytesFromParcel(Parcel source) {
        int len = source.readInt();
        if (len > 0) {
            byte[] buff = new byte[len];
            source.readByteArray(buff);
            return buff;
        }

        return null;
    }

    public static byte[] generateMD5Binary(String value) {
        if (value == null) {
            return null;
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(value.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] generateMD5Binary(byte[] value) {
        if (value == null) {
            return null;
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(value);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] generateMD5BinaryX2(String value) {
        byte[] md5x1 = generateMD5Binary(value);
        return generateMD5Binary(md5x1);
    }

    public static String generateMD5String(String value) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] hash = md5.digest(value.getBytes("UTF-8"));

            StringBuilder builder = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                int temp = b & 0xff;
                if (temp < 0x10) {
                    builder.append("0");
                }

                builder.append(Integer.toHexString(temp));
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static String getFormattedMD5(byte[] content) {
        byte[] md5 = generateMD5Binary(content);
        if (md5 == null || md5.length == 0) {
            return null;
        }

        return serializationBytesToHex(md5);
    }

    public static String getHash(String value) {
        if (value == null) {
            return null;
        }

        return getHash(value.getBytes());
    }

    public static String getHash(byte[] value) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(value);
            BigInteger bi = new BigInteger(md5.digest()).abs();
            return bi.toString(Character.MAX_RADIX);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return serializationBytesToHex(value);
    }

    public static String serializationBytesToHex(byte[] src) {
        if (src == null || src.length <= 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder(src.length * 2);
        for (byte ch : src) {
            builder.append(String.format("%02x", ch & 0xff));
        }

        return builder.toString();
    }

    public static int getApplicationPid() {
        Context context = BaseApplication.getAppContext();
        String pkgName = getPackageName();

        ActivityManager manager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningList = manager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : runningList) {
            if (TextUtils.equals(pkgName, info.processName)) {
                return info.pid;
            }
        }

        return 0;
    }

    public static List<Integer> getApplicationPidList() {
        Context context = BaseApplication.getAppContext();
        String pkgName = getPackageName();

        ActivityManager manager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningList = manager.getRunningAppProcesses();

        List<Integer> result = new ArrayList<Integer>();
        for (ActivityManager.RunningAppProcessInfo info : runningList) {
            if (info.processName.contains(pkgName)) {
                result.add(info.pid);
            }
        }

        return result;
    }

    public static void openNetworkSetting(Context context) {
        Intent intent = null;
        if (VersionUtils.getSystemVersion() > Build.VERSION_CODES.HONEYCOMB_MR2) {
            intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        } else {
            intent = new Intent(Settings.ACTION_SETTINGS);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static String getDeviceId() {
        TelephonyManager mgr = (TelephonyManager)
                BaseApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        try {
            deviceId = mgr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(deviceId)) {
            deviceId = "";
        }

        return MiscUtils.generateMD5String(deviceId);
    }

    public static String getDeviceInfo() {
        // Nexus 5, API-19, google/hammerhead/...../release-keys, TIME-1403892907000, CPU_ABI-armeabi-v7a
        //
        return new StringBuilder()
                .append(Build.MODEL)
                .append(", API-").append(Build.VERSION.SDK_INT)
                .append(", ").append(Build.FINGERPRINT)
                .append(", TIME-").append(Build.TIME)
                .append(", CPU_ABI-").append(Build.CPU_ABI)
                .toString();
    }

    public static String formatSimpleDateDesc(long millSeconds) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(millSeconds));
    }

    public static String formatDateDesc(long millSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH);
        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(millSeconds);
        int nowYear = calendar.get(Calendar.YEAR);
        int nowMonth = calendar.get(Calendar.MONTH);
        int nowDay = calendar.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat format = null;
        if (thisYear == nowYear && thisMonth == nowMonth && thisDay == nowDay) {
            format = new SimpleDateFormat("HH:mm");
        } else if (thisYear == nowYear) {
            format = new SimpleDateFormat("MM-dd HH:mm");
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }

        return format.format(new Date(millSeconds));
    }

    public static String formatFullDateDesc(long millSeconds) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(millSeconds));
    }

    public static String getMainLauncherActivity() {
        try {
            Context context = BaseApplication.getAppContext();
            PackageManager manager = context.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);
            Collections.sort(list, new ResolveInfo.DisplayNameComparator(manager));

            String packageName = MiscUtils.getPackageName();
            for (ResolveInfo info : list) {
                if (TextUtils.equals(packageName, info.activityInfo.packageName)) {
                    return info.activityInfo.name;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] bundleToBytes(Bundle bundle) {
        Parcel accountInfoParcel = Parcel.obtain();
        bundle.writeToParcel(accountInfoParcel, 0);
        byte[] bytes = accountInfoParcel.marshall();

        accountInfoParcel.recycle();
        return bytes;
    }

    public static Bundle bytesToBundle(byte[] bytes) {
        Bundle bundle = new Bundle();
        if (bytes == null || bytes.length == 0) {
            return bundle;
        }

        Parcel accountInfoParcel = Parcel.obtain();
        accountInfoParcel.unmarshall(bytes, 0, bytes.length);
        accountInfoParcel.setDataPosition(0);
        bundle.readFromParcel(accountInfoParcel);
        accountInfoParcel.recycle();

        return bundle;
    }

    public static String ensureString(String value) {
        return value != null ? value : "";
    }

    public static boolean isSameDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        today.setTimeInMillis(System.currentTimeMillis());

        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameWeek(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        today.setTimeInMillis(System.currentTimeMillis());

        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR);
    }

    public static String formatTime(long timeMs) {
        if (timeMs <= 0) {
            return "00:00";
        }

        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60 % 60;
        long hours = totalSeconds / 3600;

        Formatter formatter = new Formatter();
        return hours > 0
                ? formatter.format("%d:%02d:%02d", new Object[]{hours, minutes, seconds}).toString()
                : formatter.format("%02d:%02d", new Object[]{minutes, seconds}).toString();
    }

    public static void playSound(Context context, int soundResId) {
        final MediaPlayer player = MediaPlayer.create(context, soundResId);
        if (player == null) {
            return;
        }

        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);

        float volume = 1.0f;
        if (maxVolume > 0) {
            volume = (float) currVolume / (float) maxVolume;
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.release();
            }
        });

        player.setVolume(volume, volume);
        player.start();
    }

    public static String getTimeProgressDesc(int progressSeconds) {
        int hour = progressSeconds / 3600;
        int minute = (progressSeconds - hour * 3600) / 60;
        int second = progressSeconds - hour * 3600 - minute * 60;

        Context context = BaseApplication.getAppContext();
        if (hour != 0) {
            return context.getString(R.string.hour_time_format, hour, minute, second);
        } else if (minute != 0) {
            return context.getString(R.string.minute_time_format, minute, second);
        }

        return context.getString(R.string.second_time_format, second);
    }

    public static void switchScreenOnOff(Context context, boolean keepOn) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            if (keepOn) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

    public static void scanMediaFile(String file) {
        if (TextUtils.isEmpty(file)) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(file)));
        AppCore.getApplicationContext().sendBroadcast(intent);
    }

    public static JungleSize getScreenSize() {
        DisplayMetrics metrics = AppCore.getApplicationContext()
                .getResources().getDisplayMetrics();

        return new JungleSize(metrics.widthPixels, metrics.heightPixels);
    }

    public static void requestRuntimePermission(Activity activity, String permission) {
        requestRuntimePermission(activity, permission, null);
    }

    public static void requestRuntimePermission(
            Activity activity, String permission,
            OnPermissionRequestListener listener) {

        requestRuntimePermission(activity, new String[]{permission}, listener);
    }

    public static void requestRuntimePermission(
            Activity activity, String[] permissions,
            final OnPermissionRequestListener listener) {

        final Set<String> grantedPermissions = new HashSet<>();
        if (permissions == null || permissions.length <= 0) {
            if (listener != null) {
                listener.onResult(grantedPermissions);
            }

            return;
        }

        List<String> noGrantedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                noGrantedPermissions.add(permission);
            }
        }

        if (noGrantedPermissions.isEmpty()) {
            if (listener != null) {
                Collections.addAll(grantedPermissions, permissions);
                listener.onResult(grantedPermissions);
            }

            return;
        }

        final int REQUEST_PERMISSION_CODE = 1000;
        if (listener != null && activity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) activity;
            baseActivity.addRequestPermissionResultListener(
                    new OnRequestPermissionsResultListener() {
                        @Override
                        public boolean onRequestPermissionsResult(
                                BaseActivity activity, int requestCode,
                                String[] permissions, int[] grantResults) {

                            if (requestCode != REQUEST_PERMISSION_CODE) {
                                return false;
                            }

                            if (permissions == null || grantResults == null) {
                                listener.onResult(grantedPermissions);
                                return true;
                            }

                            int count = Math.min(permissions.length, grantResults.length);
                            for (int i = 0; i < count; ++i) {
                                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                    grantedPermissions.add(permissions[i]);
                                }
                            }

                            listener.onResult(grantedPermissions);
                            return true;
                        }
                    });
        }

        ActivityCompat.requestPermissions(activity,
                noGrantedPermissions.toArray(new String[noGrantedPermissions.size()]),
                REQUEST_PERMISSION_CODE);
    }

    public static String getPackageSign(Context context, String packageName) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        for (int i = 0; i < info.signatures.length; ++i) {
            byte[] bytes = info.signatures[i].toByteArray();
            if (bytes != null) {
                md5.update(bytes);
            }
        }

        return MiscUtils.serializationBytesToHex(md5.digest());
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static void copyTextToClipboard(String data) {
        ClipboardManager clipboardManager = (ClipboardManager)
                AppCore.getApplicationContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clipData = ClipData.newPlainText("text", data);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static Bitmap takeViewScreenshot(View view) {
        if (view == null) {
            return null;
        }

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }
}
