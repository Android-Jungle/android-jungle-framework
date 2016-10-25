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

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LogUtils {

    private static final String CLEAN_OLD_LOG_THREAD = "CleanOldLogThread";
    private static final long CLEAN_OLD_LOG_TIME = 3 * 24 * 60 * 60 * 1000;
    private static final int FLUSH_INTERVAL_COUNT = 10;

    private static boolean mEnableLogFile = false;
    private static boolean mOldLogCleaned = false;
    private static LogDumper mLogDumper;
    private static boolean mDebug = false;


    private static String buildLogMsg(String msg) {
        StackTraceElement trace = new Throwable().fillInStackTrace().getStackTrace()[2];
        return new StringBuilder()
                .append(trace.getClassName())
                .append(".")
                .append(trace.getMethodName())
                .append("(")
                .append(trace.getLineNumber())
                .append("): ")
                .append(msg)
                .toString();
    }

    public static void setDebug(boolean debug) {
        mDebug = debug;
    }

    public static void enableLogFile(boolean enable) {
        if (!mOldLogCleaned) {
            mOldLogCleaned = true;

            new Thread(mCleanOldLogRunnable, CLEAN_OLD_LOG_THREAD).start();
        }

        if (enable == mEnableLogFile) {
            return;
        }

        if (enable && !FileUtils.isSDCardMounted()) {
            return;
        }

        mEnableLogFile = enable;

        if (!mEnableLogFile) {
            mLogDumper.stop();
            mLogDumper = null;
        } else {
            mLogDumper = new LogDumper();
            mLogDumper.start();
        }
    }

    public static boolean isLogFileEnabled() {
        return mEnableLogFile;
    }


    public static String getLogPath() {
        return FileUtils.getTempPath() + "logs/";
    }

    private static class LogDumper {

        private BufferedOutputStream mLogOutput;
        private boolean mStop = false;


        private Thread mLogThread = new Thread("LogThread") {
            @Override
            public void run() {
                super.run();

                int writeCount = 0;
                Runtime runtime = Runtime.getRuntime();

                try {
                    Process process = runtime.exec(getFullLogcatCommand());
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    while (!mStop) {
                        String log = reader.readLine();
                        if (log != null && mLogOutput != null) {
                            mLogOutput.write(log.getBytes());
                            mLogOutput.write("\n".getBytes());

                            ++writeCount;
                            if (writeCount > FLUSH_INTERVAL_COUNT) {
                                mLogOutput.flush();
                                writeCount = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        private static String getFullLogcatCommand() {
            return "logcat -b main -v long";
        }

        private static String getLogcatCommand() {
            List<Integer> pidList = MiscUtils.getApplicationPidList();
            if (pidList.isEmpty()) {
                return getFullLogcatCommand();
            }

            StringBuilder builder = new StringBuilder();
            builder.append("logcat -b main -v time | grep -E \"");

            // format to: pid1|pid2|pidx|com.xx.xx
            //
            for (int pid : pidList) {
                builder.append(pid);
                builder.append("|");
            }

            builder.append(MiscUtils.getPackageName());
            builder.append("\"");

            return builder.toString();
        }

        private String getLogFile() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
            String currTime = format.format(new Date());

            String path = getLogPath();
            FileUtils.createPaths(path);
            return path + currTime + ".log";
        }

        public void start() {
            String logFile = getLogFile();
            FileUtils.createFile(logFile);

            File file = new File(logFile);
            if (!file.exists()) {
                return;
            }

            try {
                mLogOutput = new BufferedOutputStream(new FileOutputStream(file));
                mLogThread.start();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            mStop = true;

            if (mLogOutput != null) {
                try {
                    mLogOutput.flush();
                    mLogOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mLogOutput = null;
            }

            mLogThread = null;
        }
    }

    private static Runnable mCleanOldLogRunnable = new Runnable() {
        @Override
        public void run() {
            File logPath = new File(getLogPath());
            File[] logList = logPath.listFiles();
            if (logList == null) {
                return;
            }

            long currTime = System.currentTimeMillis();
            for (File log : logList) {
                if (currTime - log.lastModified() > CLEAN_OLD_LOG_TIME) {
                    try {
                        LogUtils.i("CleanLog", "Clean Old Log. %s.", log.toString());
                        log.delete();
                    } catch (Exception e) {
                    }
                }
            }
        }
    };


    //
    // For Release & Debug.
    //
    public static void v(String tag, String msg) {
        Log.v(tag, buildLogMsg(msg));
    }

    public static void v(String tag, String msg, Throwable tr) {
        Log.v(tag, buildLogMsg(msg), tr);
    }

    public static void v(String tag, String fmt, Object... args) {
        Log.v(tag, buildLogMsg(String.format(fmt, args)));
    }

    public static void i(String tag, String msg) {
        Log.i(tag, buildLogMsg(msg));
    }

    public static void i(String tag, String msg, Throwable tr) {
        Log.i(tag, buildLogMsg(msg), tr);
    }

    public static void i(String tag, String fmt, Object... args) {
        Log.i(tag, buildLogMsg(String.format(fmt, args)));
    }

    public static void d(String tag, String msg) {
        Log.d(tag, buildLogMsg(msg));
    }

    public static void d(String tag, String msg, Throwable tr) {
        Log.d(tag, buildLogMsg(msg), tr);
    }

    public static void d(String tag, String fmt, Object... args) {
        Log.d(tag, buildLogMsg(String.format(fmt, args)));
    }

    public static void w(String tag, String msg) {
        Log.w(tag, buildLogMsg(msg));
    }

    public static void w(String tag, String msg, Throwable tr) {
        Log.w(tag, buildLogMsg(msg), tr);
    }

    public static void w(String tag, String fmt, Object... args) {
        Log.w(tag, buildLogMsg(String.format(fmt, args)));
    }

    public static void e(String tag, String msg) {
        Log.e(tag, buildLogMsg(msg));
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, buildLogMsg(msg), tr);
    }

    public static void e(String tag, String fmt, Object... args) {
        Log.e(tag, buildLogMsg(String.format(fmt, args)));
    }

    //
    // For Debug only.
    //
    public static void dv(String tag, String msg) {
        if (mDebug) {
            Log.v(tag, buildLogMsg(msg));
        }
    }

    public static void dv(String tag, String msg, Throwable tr) {
        if (mDebug) {
            Log.v(tag, buildLogMsg(msg), tr);
        }
    }

    public static void dv(String tag, String fmt, Object... args) {
        if (mDebug) {
            Log.v(tag, buildLogMsg(String.format(fmt, args)));
        }
    }

    public static void di(String tag, String msg) {
        if (mDebug) {
            Log.i(tag, buildLogMsg(msg));
        }
    }

    public static void di(String tag, String msg, Throwable tr) {
        if (mDebug) {
            Log.i(tag, buildLogMsg(msg), tr);
        }
    }

    public static void di(String tag, String fmt, Object... args) {
        if (mDebug) {
            Log.i(tag, buildLogMsg(String.format(fmt, args)));
        }
    }

    public static void dd(String tag, String msg) {
        if (mDebug) {
            Log.d(tag, buildLogMsg(msg));
        }
    }

    public static void dd(String tag, String msg, Throwable tr) {
        if (mDebug) {
            Log.d(tag, buildLogMsg(msg), tr);
        }
    }

    public static void dd(String tag, String fmt, Object... args) {
        if (mDebug) {
            Log.d(tag, buildLogMsg(String.format(fmt, args)));
        }
    }

    public static void dw(String tag, String msg) {
        if (mDebug) {
            Log.w(tag, buildLogMsg(msg));
        }
    }

    public static void dw(String tag, String msg, Throwable tr) {
        if (mDebug) {
            Log.w(tag, buildLogMsg(msg), tr);
        }
    }

    public static void dw(String tag, String fmt, Object... args) {
        if (mDebug) {
            Log.w(tag, buildLogMsg(String.format(fmt, args)));
        }
    }

    public static void de(String tag, String msg) {
        if (mDebug) {
            Log.e(tag, buildLogMsg(msg));
        }
    }

    public static void de(String tag, String msg, Throwable tr) {
        if (mDebug) {
            Log.e(tag, buildLogMsg(msg), tr);
        }
    }

    public static void de(String tag, String fmt, Object... args) {
        if (mDebug) {
            Log.e(tag, buildLogMsg(String.format(fmt, args)));
        }
    }
}
