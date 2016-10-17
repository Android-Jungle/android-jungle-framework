/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.player.render;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureViewMediaRender extends MediaRender
        implements TextureView.SurfaceTextureListener {

    private TextureView mTextureView;


    public TextureViewMediaRender(TextureView textureView) {
        mTextureView = textureView;
    }

    @Override
    public void initRender() {
        mTextureView.setSurfaceTextureListener(this);
        if (mTextureView.getSurfaceTexture() != null) {
            mListener.onRenderCreated();
        }
    }

    public TextureView getTextureView() {
        return mTextureView;
    }

    @Override
    public View getRenderView() {
        return getTextureView();
    }

    @Override
    public void prepareMediaRender(MediaPlayer mediaPlayer) {
        mediaPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
    }

    @Override
    public void mediaRenderChanged(MediaPlayer mediaPlayer) {
        mediaPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
    }

    @Override
    public boolean isRenderCreating() {
        return mTextureView.getSurfaceTexture() == null;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mIsRenderValid = true;
        mListener.onRenderCreated();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mIsRenderValid = false;
        mListener.onRenderDestroyed();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}
