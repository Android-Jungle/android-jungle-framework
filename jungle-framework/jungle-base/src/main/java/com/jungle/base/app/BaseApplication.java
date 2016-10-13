/*
 * Copyright (C) 2015. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.app;

import android.app.Application;
import android.content.Context;
import com.jungle.base.event.JungleEvent;
import com.jungle.base.manager.EventManager;
import com.jungle.base.utils.NetworkUtils;

public class BaseApplication extends Application {

    private static BaseApplication mApplication;

    private BaseAppCore mAppCore;


    @Override
    public void onCreate() {
        super.onCreate();

        mApplication = this;
        mAppCore = createAppCore();
        mAppCore.onCreate();

        NetworkUtils.initializeNetworkUtils(this);
        EventManager.getInstance().notify(JungleEvent.APP_INITIALIZED);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetworkUtils.unInitializeNetworkUtils(this);

        mApplication = null;
    }

    protected BaseAppCore createAppCore() {
        return new BaseAppCore(this);
    }

    public BaseAppCore getAppCore() {
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
