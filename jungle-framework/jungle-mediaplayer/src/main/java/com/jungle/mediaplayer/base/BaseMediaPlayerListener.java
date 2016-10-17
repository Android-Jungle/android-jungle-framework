/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.base;

public interface BaseMediaPlayerListener {

    void onLoading();

    void onLoadFailed();

    void onFinishLoading();

    void onError(int what, boolean canReload, String message);

    void onStartPlay();

    void onPlayComplete();

    void onStartSeek();

    void onSeekComplete();

    void onResumed();

    void onPaused();

    void onStopped();
}
