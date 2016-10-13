/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.jungle.base.app.BaseAppCore;

public class ThreadManager implements AppManager {

    private static final String LOGIC_THREAD_HANDLER = "Jungle.Handler.Logic";
    private static final String FILE_THREAD_HANDLER = "Jungle.Handler.File";


    public static ThreadManager getInstance() {
        return BaseAppCore.getInstance().getManager(ThreadManager.class);
    }


    private Handler mUIHandler;
    private Handler mLogicHandler;
    private Handler mFileHandler;
    private HandlerThread mLogicHandlerThread;
    private HandlerThread mFileHandlerThread;


    @Override
    public void onCreate() {
    }

    @Override
    public void onTerminate() {
        if (mUIHandler != null) {
            mUIHandler.removeCallbacksAndMessages(null);
        }

        if (mLogicHandler != null) {
            mLogicHandler.removeCallbacksAndMessages(null);
        }

        if (mFileHandler != null) {
            mFileHandler.removeCallbacksAndMessages(null);
        }

        mUIHandler = null;
        mLogicHandler = null;
        mFileHandler = null;
        mLogicHandlerThread = null;
        mFileHandlerThread = null;
    }

    public Handler getUIHandler() {
        if (mUIHandler == null) {
            synchronized (this) {
                if (mUIHandler == null) {
                    mUIHandler = new Handler(Looper.getMainLooper());
                }
            }
        }

        return mUIHandler;
    }

    public Handler getLogicHandler() {
        if (mLogicHandler == null) {
            synchronized (this) {
                if (mLogicHandler == null) {
                    mLogicHandlerThread = new HandlerThread(LOGIC_THREAD_HANDLER);
                    mLogicHandlerThread.start();

                    mLogicHandler = new Handler(mLogicHandlerThread.getLooper());
                }
            }
        }

        return mLogicHandler;
    }

    public Handler getFileHandler() {
        if (mFileHandler == null) {
            synchronized (this) {
                if (mFileHandler == null) {
                    mFileHandlerThread = new HandlerThread(FILE_THREAD_HANDLER);
                    mFileHandlerThread.start();

                    mFileHandler = new Handler(mFileHandlerThread.getLooper());
                }
            }
        }

        return mFileHandler;
    }

    public void executeOnUIHandler(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            getUIHandler().post(runnable);
        }
    }

    public void postOnUIHandler(Runnable runnable) {
        getUIHandler().post(runnable);
    }

    public void postOnUIHandlerDelayed(Runnable runnable, int delayedMs) {
        getUIHandler().postDelayed(runnable, delayedMs);
    }
}
