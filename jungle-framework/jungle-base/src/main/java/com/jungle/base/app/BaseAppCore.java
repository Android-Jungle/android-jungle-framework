/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import com.jungle.base.common.Component;
import com.jungle.base.event.JungleEvent;
import com.jungle.base.manager.AppLifeManager;
import com.jungle.base.manager.AppManager;
import com.jungle.base.manager.EventManager;
import com.jungle.base.utils.LogUtils;
import com.jungle.base.utils.MiscUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseAppCore implements Component {

    private static final String TAG = "BaseAppCore";


    protected boolean mIsStarted = false;
    protected static BaseApplication mApplication;
    protected Map<Class<? extends AppManager>, AppManager> mManagerList =
            new HashMap<Class<? extends AppManager>, AppManager>();
    protected Map<Class<? extends AppManager>, AppManager> mRealManagerList =
            new HashMap<Class<? extends AppManager>, AppManager>();


    public BaseAppCore(BaseApplication app) {
        mApplication = app;
    }

    public static BaseAppCore getInstance() {
        return mApplication != null ? mApplication.getAppCore() : null;
    }

    public static BaseApplication getApplication() {
        return mApplication;
    }

    public static Context getApplicationContext() {
        return mApplication.getApplicationContext();
    }

    protected List<Class<? extends AppManager>> getLastTerminateList() {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtils.setDebug(isDebug());
        LogUtils.enableLogFile(false);
    }

    @Override
    public void onTerminate() {
        List<Class<? extends AppManager>> lastTerminateList = getLastTerminateList();
        List<AppManager> list = new ArrayList<>();

        for (AppManager mgr : mRealManagerList.values()) {
            boolean found = false;

            if (lastTerminateList != null) {
                for (Class<? extends AppManager> clazz : lastTerminateList) {
                    if (clazz.isAssignableFrom(mgr.getClass())) {
                        list.add(mgr);
                        found = true;
                    }
                }
            }

            if (!found) {
                mgr.onTerminate();
            }
        }

        for (AppManager mgr : list) {
            mgr.onTerminate();
        }

        mManagerList = null;
        mRealManagerList = null;
        LogUtils.enableLogFile(false);
    }

    public void preRestartApp() {
    }

    public void restartApp() {
        restartInternal();
        onTerminate();

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public final void start() {
        startInternal();
        mIsStarted = true;
    }

    public final boolean isStarted() {
        return mIsStarted;
    }

    public final boolean isDebug() {
        return mApplication.isDebug();
    }

    protected void startInternal() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mApplication.registerReceiver(mScreenLockReceiver, filter);
    }

    public void finishAll() {
        LogUtils.e(TAG, "App Will Normally **Finish** All!");

        mApplication.unregisterReceiver(mScreenLockReceiver);
        AppLifeManager.getInstance().finish();
    }

    private BroadcastReceiver mScreenLockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                LogUtils.i(TAG, "Screen On!");
                EventManager.getInstance().notify(JungleEvent.SCREEN_UNLOCKED);

            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                LogUtils.i(TAG, "Screen Off!");
                EventManager.getInstance().notify(JungleEvent.SCREEN_LOCKED);

            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                LogUtils.i(TAG, "Screen User Present!");
                EventManager.getInstance().notify(JungleEvent.SCREEN_USER_PRESENT);
            }
        }
    };

    public void openUrl(Context context, String url) {
        MiscUtils.openUrlByBrowser(context, url);
    }

    private void restartInternal() {
        Intent intent = new Intent();
        Context context = BaseAppCore.getApplicationContext();
        intent.setComponent(new ComponentName(
                context, MiscUtils.getMainLauncherActivity()));
        int flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            flags |= Intent.FLAG_ACTIVITY_CLEAR_TASK;
        }

        intent.addFlags(flags);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, intent.getFlags());

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent);
    }

    public final void addManager(AppManager mgr) {
        final Class<? extends AppManager> clazz = mgr.getClass();
        AppManager mgrInstance = mManagerList.get(clazz);

        if (mgrInstance == null) {
            mManagerList.put(clazz, mgr);
            mRealManagerList.put(clazz, mgr);
            mgr.onCreate();
        } else if (mgrInstance != mgr) {
            throw new RuntimeException(
                    String.format("Manager Already exist: %s", clazz.getName()));
        }
    }

    public final <T extends AppManager> T getManager(Class<T> clazz) {
        AppManager mgrInstance = mManagerList.get(clazz);
        if (mgrInstance != null) {
            return (T) mgrInstance;
        }

        for (Class<? extends AppManager> ref : mManagerList.keySet()) {
            // 如果有子类的 Manager,一样可以拿出来.
            if (clazz.isAssignableFrom(ref)) {
                mgrInstance = mManagerList.get(ref);

                // 加入列表,加速下次查找.
                mManagerList.put(clazz, mgrInstance);
                return (T) mgrInstance;
            }
        }

        try {
            // 没有找到 Manager,创建一个.
            mgrInstance = clazz.newInstance();
            addManager(mgrInstance);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) mgrInstance;
    }
}
