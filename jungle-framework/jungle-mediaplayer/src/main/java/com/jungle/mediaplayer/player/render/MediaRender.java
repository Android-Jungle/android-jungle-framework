/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.player.render;

import android.media.MediaPlayer;
import android.view.View;

public abstract class MediaRender {

    public interface Listener {
        void onRenderCreated();

        void onRenderDestroyed();
    }


    protected boolean mIsRenderValid;
    protected Listener mListener;


    public abstract void initRender();

    public abstract View getRenderView();

    public abstract void prepareMediaRender(MediaPlayer mediaPlayer);

    public abstract void mediaRenderChanged(MediaPlayer mediaPlayer);

    public abstract boolean isRenderCreating();

    public boolean isRenderValid() {
        return mIsRenderValid;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }
}
