/**
 * Android Jungle framework project.
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

package com.jungle.base.app;

import android.app.Application;
import android.content.Context;
import com.jungle.base.event.JungleEvent;
import com.jungle.base.manager.EventManager;
import com.jungle.base.utils.NetworkUtils;

public class BaseApplication extends Application {

    private static BaseApplication mApplication;

    private AppCore mAppCore;


    @Override
    public void onCreate() {
        super.onCreate();

        mApplication = this;

        NetworkUtils.initializeNetworkUtils(this);
        EventManager.getInstance().notify(JungleEvent.APP_INITIALIZED);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetworkUtils.unInitializeNetworkUtils(this);

        mApplication = null;
    }

    protected void initAppCore() {
        mAppCore = createAppCore();
        mAppCore.onCreate();

        if (mAppCore.isStarted()) {
            return;
        }

        mAppCore.start();
        while (!mAppCore.isStarted()) {
        }
    }

    protected AppCore createAppCore() {
        return new AppCore(this);
    }

    public AppCore getAppCore() {
        return mAppCore;
    }

    public boolean isDebug() {
        return false;
    }

    public static Context getAppContext() {
        return mApplication;
    }

    public static BaseApplication getApp() {
        return mApplication;
    }

    public void doClean() {
        EventManager.getInstance().notify(JungleEvent.BEFORE_APP_CLEAN);

        mAppCore.onTerminate();
        mAppCore = null;
    }
}
