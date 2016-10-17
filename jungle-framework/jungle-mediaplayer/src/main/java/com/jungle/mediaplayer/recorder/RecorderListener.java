/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.recorder;

public interface RecorderListener {

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