/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.base.manager;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.jungle.base.app.AppCore;
import com.jungle.base.utils.FileUtils;
import com.jungle.base.utils.MiscUtils;
import com.jungle.base.utils.NetworkUtils;
import com.jungle.base.utils.VersionUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CrashManager implements AppManager {

    private static final String TAG = "Crash";
    private static final int DEFAULT_DUMP_INFO_SIZE = 512;
    private static final int UPLOAD_DUMP_INTERVAL = 10 * 1000;


    public static CrashManager getInstance() {
        return AppCore.getInstance().getManager(CrashManager.class);
    }


    public interface CrashHandlerListener {
        void onHandled(File dumpFile, Thread thread, Throwable ex);
    }


    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private CrashHandlerListener mCrashHandlerListener;
    private String mCrashDumpPath;


    @Override
    public void onCreate() {
        mCrashDumpPath = getDefaultDumpPath();

        // Upload anonymous user dump info.
        if (!checkCanUpload()) {
            ThreadManager.getInstance().getLogicHandler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            createUploadTask().upload();
                        }
                    }, UPLOAD_DUMP_INTERVAL);
        }
    }

    protected CrashUploadTask createUploadTask() {
        return new CrashUploadTask(null);
    }

    @Override
    public void onTerminate() {
    }

    private boolean checkCanUpload() {
        return !AppCore.getInstance().isDebug() && NetworkUtils.isWifi();
    }

    public static String getDefaultDumpPath() {
        return FileUtils.getTempPath() + "dump/";
    }

    public void setCrashDumpPath(String dumpPath) {
        if (TextUtils.isEmpty(dumpPath)) {
            mCrashDumpPath = getDefaultDumpPath();
        } else {
            mCrashDumpPath = dumpPath;
        }
    }

    public void setCrashHandlerListener(CrashHandlerListener l) {
        mCrashHandlerListener = l;
    }

    public void installCrashHandler() {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mCrashHandler);
    }

    public void unInstallCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(mDefaultCrashHandler);
        mDefaultCrashHandler = null;
    }

    private Thread.UncaughtExceptionHandler mMockCrashHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    String tag = TAG + "/HandleException";
                    Log.e(tag, "", ex);
                }
            };

    private Thread.UncaughtExceptionHandler mCrashHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    AppCore core = AppCore.getInstance();
                    if (!core.isDebug()) {
                        Log.e(TAG, "", ex);
                    }

                    Thread.setDefaultUncaughtExceptionHandler(mMockCrashHandler);
                    File dumpFile = dumpCrashInfo(thread, ex);

                    if (mCrashHandlerListener != null) {
                        mCrashHandlerListener.onHandled(dumpFile, thread, ex);
                    }

                    if (core.isDebug()) {
                        mDefaultCrashHandler.uncaughtException(thread, ex);
                    } else {
                        core.preRestartApp();
                        core.restartApp();
                    }
                }
            };

    private String generateTimeInfo() {
        File path = new File(mCrashDumpPath);
        if (!path.exists()) {
            path.mkdirs();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        return new SimpleDateFormat("yyyy-MM-dd___HH_mm_ss").format(new Date());
    }

    private String generateDumpInfo(Thread thread, Throwable ex) {
        StringBuilder builder = new StringBuilder();
        builder.append("DUMP: ThreadId -> ")
                .append(thread.getId())
                .append(", ThreadName -> ")
                .append(thread.getName())
                .append("\r\n\r\n");

        ByteArrayOutputStream stream = new ByteArrayOutputStream(DEFAULT_DUMP_INFO_SIZE);
        ex.printStackTrace(new PrintStream(stream));

        try {
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        builder.append(stream.toString());
        return builder.toString();
    }

    protected void saveDumpInfoForNextStart(DumpInfo info) {
        // you can save this info to Database.
        // when next start-up, read from Database to handle it.
    }

    private File dumpCrashInfo(Thread thread, Throwable ex) {
        final String info = generateDumpInfo(thread, ex);

        final String time = generateTimeInfo();
        final String fileName = mCrashDumpPath + "/CrashDump_" + time + ".trace";
        final File file = new File(fileName);
        PrintWriter writer = null;

        try {
            file.createNewFile();
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            DumpInfo dumpInfo = new DumpInfo();
            dumpInfo.mDumpMD5 = MiscUtils.generateMD5String(info);
            dumpInfo.mDumpTime = System.currentTimeMillis();
            dumpInfo.mDumpFilePath = fileName;
            saveDumpInfoForNextStart(dumpInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Time.
        writer.println(time);
        writer.println("-------------------------------------------------");
        writer.println();

        // App Version Info.
        writer.println(String.format("App Version: %s",
                VersionUtils.getAppVersionName()));
        writer.println(String.format("App VersionCode: %s",
                VersionUtils.getAppVersionCode()));
        writer.println();

        // Android Version Info.
        writer.println(String.format("OS Version: %s", Build.VERSION.RELEASE));
        writer.println();
        writer.println(String.format("SDK Version: %s", Build.VERSION.SDK_INT));
        writer.println();

        // Device Info.
        writer.println(String.format("Device Manufacturer Company: %s",
                Build.MANUFACTURER));
        writer.println(String.format("Device Model: %s", Build.MODEL));
        writer.println();

        // CPU Info.
        writer.println(String.format("CPU ABI: %s", Build.CPU_ABI));

        // App Info.
        writer.println(String.format("AppId: %s", VersionUtils.getAppId()));
        writer.println(String.format("AppVersion: %s", VersionUtils.getAppVersionName()));
        writer.println();

        // Separator.
        writer.println();
        writer.println("-------------------------------------------------");
        writer.println();

        // Dump Stack Info.
        writer.print(info);

        // Close Writer.
        writer.close();

        return file;
    }
}
