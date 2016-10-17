/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.player.render;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class SurfaceViewMediaRender extends MediaRender
        implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;


    public SurfaceViewMediaRender(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
    }

    @Override
    public void initRender() {
        SurfaceHolder videoHolder = mSurfaceView.getHolder();
        videoHolder.addCallback(this);
        mIsRenderValid = !videoHolder.isCreating();
    }

    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    @Override
    public View getRenderView() {
        return getSurfaceView();
    }

    @Override
    public void prepareMediaRender(MediaPlayer mediaPlayer) {
        mediaPlayer.setDisplay(mSurfaceView.getHolder());
    }

    @Override
    public void mediaRenderChanged(MediaPlayer mediaPlayer) {
        mediaPlayer.setDisplay(mSurfaceView.getHolder());
    }

    @Override
    public boolean isRenderCreating() {
        SurfaceHolder holder = mSurfaceView.getHolder();
        return holder == null || holder.isCreating();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsRenderValid = true;
        mListener.onRenderCreated();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRenderValid = false;
        mListener.onRenderDestroyed();
    }
}
