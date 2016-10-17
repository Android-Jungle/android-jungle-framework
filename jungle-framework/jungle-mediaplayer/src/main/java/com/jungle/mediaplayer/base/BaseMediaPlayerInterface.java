/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.base;

public interface BaseMediaPlayerInterface {

    void pause();

    void resume();

    void stop();

    void destroy();

    void seekTo(int millSeconds);

    void setVolume(float volume);

    int getDuration();

    int getCurrentPosition();

    int getBufferPercent();

    boolean isPlaying();

    boolean isPaused();

    boolean isLoading();

    boolean isLoadingFailed();

    boolean isPlayCompleted();
}
