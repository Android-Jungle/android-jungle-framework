/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.recorder;

import android.content.Context;

public abstract class BaseAudioRecorder {

    public interface OnRecorderListener {

        enum Error {
            StartFailed,
            RecordInternalFailed,
            NoRecordPermission,
            NoAudioOutputFile
        }


        void onError(Error error);

        void onStartRecord();

        void onStopRecord();
    }


    protected String mOutputFile;
    protected OnRecorderListener mListener;


    public BaseAudioRecorder(OnRecorderListener listener) {
        mListener = listener;
    }

    public void setOutputFile(String outputFile) {
        mOutputFile = outputFile;
    }

    public String getOutputFilePath() {
        return mOutputFile;
    }

    public abstract boolean isRecording();

    public abstract boolean startRecord(Context context);

    public abstract boolean stopRecord();

    public abstract void destroy();
}
