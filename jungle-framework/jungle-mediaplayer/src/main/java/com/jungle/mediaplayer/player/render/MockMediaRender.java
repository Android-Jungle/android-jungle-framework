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

public class MockMediaRender extends MediaRender {

    @Override
    public void initRender() {
        mIsRenderValid = true;
    }

    @Override
    public View getRenderView() {
        return null;
    }

    @Override
    public void prepareMediaRender(MediaPlayer mediaPlayer) {
    }

    @Override
    public void mediaRenderChanged(MediaPlayer mediaPlayer) {
    }

    @Override
    public boolean isRenderCreating() {
        return false;
    }
}
