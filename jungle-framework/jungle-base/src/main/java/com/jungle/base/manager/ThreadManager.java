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

package com.jungle.base.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.jungle.base.app.AppCore;

public class ThreadManager implements AppManager {

    private static final String LOGIC_THREAD_HANDLER = "Jungle.Handler.Logic";
    private static final String FILE_THREAD_HANDLER = "Jungle.Handler.File";


    public static ThreadManager getInstance() {
        return AppCore.getInstance().getManager(ThreadManager.class);
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
