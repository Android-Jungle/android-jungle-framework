/**
 * Android photos application project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.apps.photos;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.jungle.apps.photos.module.homepage.HomepageActivity;
import com.jungle.base.app.BaseActivity;
import com.jungle.base.manager.ThreadManager;

import java.util.Random;

public class SplashActivity extends BaseActivity {

    private static final long SPLASH_AD_SHOW_INTERVAL = 2000;

    private int[] mSplashResList = new int[]{
            R.drawable.splash_0,
            R.drawable.splash_1,
            R.drawable.splash_2,
            R.drawable.splash_3
    };

    private long mSplashAdLoadTime = 0;
    private boolean mHomepageEntered = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        ImageView imgView = (ImageView) findViewById(R.id.splash_image);
        imgView.setImageBitmap(getSplashBitmap());

        ThreadManager.getInstance().postOnUIHandler(new Runnable() {
            @Override
            public void run() {
                // 闪屏时间内做一下初始化.
                doInitInternal();
            }
        });
    }

    private Bitmap getSplashBitmap() {
        Random random = new Random(System.currentTimeMillis());
        int index = Math.abs(random.nextInt()) % mSplashResList.length;

        BitmapDrawable drawable = (BitmapDrawable)
                getResources().getDrawable(mSplashResList[index]);
        return drawable.getBitmap();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    private void doInitInternal() {
        ThreadManager.getInstance().postOnUIHandlerDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (mSplashAdLoadTime == 0) {
                            closeSplash();
                        } else {
                            long elapse = System.currentTimeMillis() - mSplashAdLoadTime;
                            if (elapse > SPLASH_AD_SHOW_INTERVAL) {
                                closeSplash();
                            } else {
                                ThreadManager.getInstance().postOnUIHandlerDelayed(
                                        mCloseSplashRunnable,
                                        (int) (SPLASH_AD_SHOW_INTERVAL - elapse));
                            }
                        }
                    }
                }, 2000);
    }

    private Runnable mCloseSplashRunnable = new Runnable() {
        @Override
        public void run() {
            closeSplash();
        }
    };

    private void closeSplash() {
        if (mHomepageEntered) {
            return;
        }

        mHomepageEntered = true;
        HomepageActivity.start(this);

        finish();
    }
}
