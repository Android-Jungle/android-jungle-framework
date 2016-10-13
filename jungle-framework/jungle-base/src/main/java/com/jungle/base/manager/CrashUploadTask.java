/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.base.manager;

import com.jungle.base.utils.LogUtils;

import java.io.File;
import java.util.List;

public class CrashUploadTask {

    private static final String TAG = "CrashUploadTask";


    public interface OnListener {
        void onCompleted(CrashUploadTask task);
    }


    private boolean mInterrupted = false;
    private List<DumpInfo> mDumpList;
    private OnListener mListener;


    public CrashUploadTask(OnListener listener) {
        mListener = listener;
        mDumpList = loadDumpInfoList();
    }

    protected List<DumpInfo> loadDumpInfoList() {
        return null;
    }

    public void interrupt() {
        mInterrupted = true;
    }

    public void upload() {
        LogUtils.i(TAG, "Prepare Upload Crash! dumpCount: %d.",
                mDumpList != null ? mDumpList.size() : 0);

        if (mInterrupted || mDumpList == null || mDumpList.isEmpty()) {
            LogUtils.i(TAG, "Dump List is **empty** or task **interrupted**! will not handle.");

            notifyCompleted();
            return;
        }

        scheduleNextDump();
    }

    protected void removeAllDumpInfo() {
        // such as remove all from storage.
    }

    protected void uploadDumpInfo(final DumpInfo info) {
        // upload to your server & analysis.
    }

    protected void removeDumpInfo(DumpInfo info) {
        // such as remove from storage.
    }

    private void notifyCompleted() {
        if (mDumpList != null) {
            mDumpList.clear();
        }

        if (!mInterrupted) {
            removeAllDumpInfo();
        }

        if (mListener != null) {
            mListener.onCompleted(this);
        }
    }

    private void scheduleNextDump() {
        if (mDumpList == null) {
            return;
        }

        if (mDumpList.isEmpty()) {
            LogUtils.i(TAG, "All Dump for Upload Complete!");
            notifyCompleted();
            return;
        }

        DumpInfo info = mDumpList.get(0);
        mDumpList.remove(info);

        uploadDumpInfo(info);
    }

    protected void handleUploadOneComplete(DumpInfo info, boolean success) {
        removeDumpInfo(info);

        File file = new File(info.mDumpFilePath);
        file.delete();
        scheduleNextDump();
    }
}
