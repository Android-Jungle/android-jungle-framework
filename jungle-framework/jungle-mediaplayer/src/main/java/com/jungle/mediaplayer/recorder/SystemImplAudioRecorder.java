/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.recorder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.text.TextUtils;
import com.jungle.base.utils.FileUtils;
import com.jungle.base.utils.MiscUtils;

import java.util.Set;

public class SystemImplAudioRecorder extends BaseAudioRecorder {

    protected MediaRecorder mMediaRecorder;
    protected boolean mIsRecording = false;


    public SystemImplAudioRecorder(RecorderListener listener) {
        super(listener);
    }

    @Override
    public boolean startRecord(final Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            MiscUtils.requestRuntimePermission(
                    activity, Manifest.permission.RECORD_AUDIO,
                    new MiscUtils.OnPermissionRequestListener() {
                        @Override
                        public void onResult(Set<String> grantedPermissions) {
                            if (grantedPermissions.contains(Manifest.permission.RECORD_AUDIO)) {
                                startRecordInternal();
                            } else {
                                mListener.onError(RecorderListener.Error.NoRecordPermission);
                            }
                        }
                    });
        } else {
            return startRecordInternal();
        }

        return true;
    }

    private boolean startRecordInternal() {
        if (!TextUtils.isEmpty(mOutputFile)) {
            mListener.onError(RecorderListener.Error.NoAudioOutputFile);
            return false;
        }

        try {
            initRecorder();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        mIsRecording = false;
        FileUtils.deleteFile(mOutputFile);
        mMediaRecorder.setOutputFile(mOutputFile);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mIsRecording = true;

            if (mListener != null) {
                mListener.onStartRecord();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            if (mListener != null) {
                mListener.onError(RecorderListener.Error.StartFailed);
            }
        }

        return false;
    }

    private void initRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
        }

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                mIsRecording = false;

                if (mListener != null) {
                    mListener.onError(RecorderListener.Error.RecordInternalFailed);
                }
            }
        });

        initRecorderFormat();
    }

    protected void initRecorderFormat() {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioChannels(1);
        mMediaRecorder.setAudioSamplingRate(16000);
        mMediaRecorder.setAudioEncodingBitRate(44100);
    }

    @Override
    public boolean stopRecord() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMediaRecorder = null;
        }

        mIsRecording = false;
        if (mListener != null) {
            mListener.onStopRecord();
        }

        return true;
    }

    @Override
    public boolean isRecording() {
        return mIsRecording;
    }

    @Override
    public void destroy() {
        if (mIsRecording) {
            stopRecord();
        }

        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
