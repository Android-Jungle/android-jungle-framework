/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.base;

import android.content.Context;
import android.media.AudioManager;

import java.util.ArrayList;
import java.util.List;

public class AudioFocusPlayerListener implements BaseMediaPlayerListener {

    public interface OnAudioFocusListener {
        void onLoss();
    }


    protected Context mContext;
    protected OnAudioFocusListener mFocusListener;
    protected List<BaseMediaPlayerListener> mListenerList = new ArrayList<>();


    public AudioFocusPlayerListener(
            Context context, OnAudioFocusListener listener) {
        mContext = context;
        mFocusListener = listener;
    }

    public void addListener(BaseMediaPlayerListener listener) {
        if (listener != null) {
            mListenerList.add(listener);
        }
    }

    public void destroy() {
        releaseAudioFocus();
    }

    private void requestAudioFocus() {
        AudioManager audioManager = (AudioManager)
                mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(
                mAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    private void releaseAudioFocus() {
        AudioManager audioManager = (AudioManager)
                mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(mAudioFocusChangeListener);
    }

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Just transient. Do nothing.
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        mFocusListener.onLoss();
                        releaseAudioFocus();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // You can low volume.
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    }
                }
            };

    @Override
    public void onLoading() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onLoading();
        }
    }

    @Override
    public void onLoadFailed() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onLoadFailed();
        }

        releaseAudioFocus();
    }

    @Override
    public void onFinishLoading() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onFinishLoading();
        }
    }

    @Override
    public void onError(int what, boolean canReload, String message) {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onError(what, canReload, message);
        }

        releaseAudioFocus();
    }

    @Override
    public void onStartPlay() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onStartPlay();
        }

        requestAudioFocus();
    }

    @Override
    public void onPlayComplete() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onPlayComplete();
        }

        releaseAudioFocus();
    }

    @Override
    public void onStartSeek() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onStartSeek();
        }
    }

    @Override
    public void onSeekComplete() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onSeekComplete();
        }
    }

    @Override
    public void onResumed() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onResumed();
        }

        requestAudioFocus();
    }

    @Override
    public void onPaused() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onPaused();
        }

        releaseAudioFocus();
    }

    @Override
    public void onStopped() {
        for (BaseMediaPlayerListener listener : mListenerList) {
            listener.onStopped();
        }

        releaseAudioFocus();
    }
}
