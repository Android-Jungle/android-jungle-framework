/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.player;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import com.jungle.base.manager.ThreadManager;
import com.jungle.base.misc.JungleSize;
import com.jungle.base.utils.LogUtils;
import com.jungle.base.utils.NetworkUtils;
import com.jungle.mediaplayer.base.BaseMediaPlayerInterface;
import com.jungle.mediaplayer.base.BaseMediaPlayerListener;
import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.player.render.MediaRender;
import com.jungle.mediaplayer.player.render.MockMediaRender;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMediaPlayer
        implements BaseMediaPlayerInterface, MediaRender.Listener {

    protected static final String TAG = "MediaPlayer";


    protected interface NotifyListenerRunnable {
        void run(BaseMediaPlayerListener listener);
    }


    protected Context mContext;
    protected MediaRender mMediaRender;
    protected List<BaseMediaPlayerListener> mPlayerListeners = new ArrayList<>();
    protected VideoInfo mVideoInfo;
    protected int mVideoWidth;
    protected int mVideoHeight;
    protected boolean mIsLoading;
    protected boolean mIsLoadingFailed;
    protected boolean mMediaPlayerIsPrepared;
    protected boolean mVideoSizeInitialized;
    protected int mBufferPercent;
    protected int mVideoContainerZoneWidth;
    protected int mVideoContainerZoneHeight;
    protected boolean mAutoPlayWhenHolderCreated;
    protected boolean mAutoResumeWhenHolderCreated = true;
    protected boolean mIsPaused = false;


    public BaseMediaPlayer(Context context) {
        this(context, new MockMediaRender());
    }

    public BaseMediaPlayer(Context context, MediaRender render) {
        mContext = context;
        mMediaRender = render;
        render.setListener(this);
        render.initRender();
    }

    public void play(VideoInfo videoInfo) {
        LogUtils.e(TAG, "Pre-Play Video.");

        if (!NetworkUtils.hasNetwork()) {
            LogUtils.e(TAG, "No Network! Will not Play!");
            notifyLoadFailed();
            return;
        }

        mIsPaused = false;
        mIsLoading = true;
        mIsLoadingFailed = false;
        mVideoSizeInitialized = false;
        mMediaPlayerIsPrepared = false;
        mVideoInfo = videoInfo;

        Handler handler = ThreadManager.getInstance().getUIHandler();
        handler.removeCallbacks(mLoadingFailedRunnable);
        handler.postDelayed(mLoadingFailedRunnable, 30 * 1000);
    }

    public void addPlayerListener(BaseMediaPlayerListener listener) {
        mPlayerListeners.add(listener);
    }

    @Override
    public void destroy() {
        ThreadManager.getInstance().getUIHandler().removeCallbacks(
                mLoadingFailedRunnable);
    }

    public void setAutoResume(boolean autoResume) {
        mAutoResumeWhenHolderCreated = autoResume;
    }

    public abstract boolean hasVideoPlay();

    protected abstract void playWithMediaRender();

    protected abstract void surfaceHolderChanged();

    public MediaRender getMediaRender() {
        return mMediaRender;
    }

    @Override
    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public boolean isLoadingFailed() {
        return mIsLoadingFailed;
    }

    public boolean isLoadingOrPlaying() {
        return isLoading() || isPlaying();
    }

    @Override
    public int getBufferPercent() {
        return mBufferPercent;
    }

    @Override
    public boolean isPlayCompleted() {
        return false;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onRenderCreated() {
        if (mAutoPlayWhenHolderCreated) {
            mAutoPlayWhenHolderCreated = false;
            playWithMediaRender();
        } else {
            surfaceHolderChanged();

            if (mAutoResumeWhenHolderCreated) {
                resume();
            }
        }
    }

    @Override
    public void onRenderDestroyed() {
        pause();
    }

    protected void updateMediaRenderSize() {
        updateMediaRenderSize(mVideoContainerZoneWidth, mVideoContainerZoneHeight, true);
    }

    public static JungleSize calcVideoSize(
            int containerWidth, int containerHeight,
            int videoWidth, int videoHeight) {

        if (videoWidth == 0
                || videoHeight == 0
                || containerWidth == 0
                || containerHeight == 0) {
            return null;
        }

        int width = 0;
        int height = 0;
        float containerRatio = (float) containerWidth / (float) containerHeight;
        float ratio = (float) videoWidth / (float) videoHeight;
        if (ratio == containerRatio) {
            width = containerWidth;
            height = containerHeight;
        } else if (containerRatio > ratio) {
            height = containerHeight;
            width = (int) (height * ratio);
        } else {
            width = containerWidth;
            height = (int) (width / ratio);
        }

        return new JungleSize(width, height);
    }

    public void updateMediaRenderSize(int containerWidth, int containerHeight, boolean force) {
        if (mVideoContainerZoneWidth == containerWidth
                && mVideoContainerZoneHeight == containerHeight
                && !force) {
            return;
        }

        mVideoContainerZoneWidth = containerWidth;
        mVideoContainerZoneHeight = containerHeight;

        JungleSize size = calcVideoSize(
                mVideoContainerZoneWidth, mVideoContainerZoneHeight,
                mVideoWidth, mVideoHeight);
        if (size == null) {
            return;
        }

        View renderView = mMediaRender.getRenderView();
        if (renderView != null) {
            ViewGroup.LayoutParams params = renderView.getLayoutParams();
            params.width = size.mWidth;
            params.height = size.mHeight;
            renderView.setLayoutParams(params);
        }
    }

    protected Runnable mLoadingFailedRunnable = new Runnable() {
        @Override
        public void run() {
            notifyLoadFailed();
        }
    };

    protected void notifyListener(NotifyListenerRunnable runnable) {
        for (BaseMediaPlayerListener listener : mPlayerListeners) {
            runnable.run(listener);
        }
    }

    protected void notifyLoading() {
        LogUtils.e(TAG, "MediaPlayer Loading...");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onLoading();
            }
        });
    }

    protected void notifyFinishLoading() {
        LogUtils.e(TAG, "MediaPlayer Finish Loading!");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onFinishLoading();
            }
        });
    }

    protected void notifyLoadFailed() {
        mIsLoading = false;
        mIsLoadingFailed = true;
        LogUtils.e(TAG, "MediaPlayer Load **Failed**!!");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onLoadFailed();
            }
        });
    }

    protected void notifyError(int what, String message) {
        notifyError(what, true, message);
    }

    protected void notifyError(final int what, final boolean canReload, final String message) {
        mIsLoading = false;
        mIsLoadingFailed = true;
        LogUtils.e(TAG, "MediaPlayer Error. what = %d, message = %s.", what, message);

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onError(what, canReload, message);
            }
        });
    }

    protected void notifyStartPlay() {
        LogUtils.e(TAG, "MediaPlayer Will Play!");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onStartPlay();
            }
        });
    }

    protected void notifyPlayComplete() {
        LogUtils.e(TAG, "MediaPlayer Play Current Complete!");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onPlayComplete();
            }
        });
    }

    protected void notifyStartSeek() {
        LogUtils.e(TAG, "Video Start Seek!");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onStartSeek();
            }
        });
    }

    protected void notifySeekComplete() {
        LogUtils.e(TAG, "Video Seek Complete!");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onSeekComplete();
            }
        });
    }

    protected void notifyPaused() {
        LogUtils.e(TAG, "MediaPlayer Paused.");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onPaused();
            }
        });
    }

    protected void notifyResumed() {
        LogUtils.e(TAG, "MediaPlayer Resumed.");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onResumed();
            }
        });
    }

    protected void notifyStopped() {
        LogUtils.e(TAG, "MediaPlayer Stopped!");

        notifyListener(new NotifyListenerRunnable() {
            @Override
            public void run(BaseMediaPlayerListener listener) {
                listener.onStopped();
            }
        });
    }
}
