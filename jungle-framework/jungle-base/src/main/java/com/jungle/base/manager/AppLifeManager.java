/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.manager;

import android.app.Activity;
import com.jungle.base.app.BaseAppCore;
import com.jungle.base.common.DeepWeakReference;
import com.jungle.base.event.JungleEvent;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class AppLifeManager implements AppManager {

    public static AppLifeManager getInstance() {
        return BaseAppCore.getInstance().getManager(AppLifeManager.class);
    }


    private int mResumedActivityCount = 0;
    private List<DeepWeakReference<Activity>> mValidActivityList = new LinkedList<>();


    @Override
    public void onCreate() {
    }

    @Override
    public void onTerminate() {
    }

    public void finish() {
        int size = mValidActivityList.size();
        if (size != 0) {
            for (int i = size - 1; i >= 0; --i) {
                WeakReference<Activity> ref = mValidActivityList.get(i);
                Activity activity = ref.get();
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
        }

        mValidActivityList.clear();

        BaseAppCore.getApplication().doClean();
        System.exit(0);
    }

    public boolean isForeground() {
        return mResumedActivityCount > 0;
    }

    public boolean isBackground() {
        return mResumedActivityCount == 0;
    }

    public void activityCreated(Activity activity) {
        for (DeepWeakReference<Activity> ref : mValidActivityList) {
            if (activity == ref.get()) {
                return;
            }
        }

        mValidActivityList.add(new DeepWeakReference<Activity>(activity));
    }

    public void activityDestroyed(Activity activity) {
        mValidActivityList.remove(new DeepWeakReference<Activity>(activity));
    }

    public Activity getCurrentActivity() {
        if (mValidActivityList.isEmpty()) {
            return null;
        }

        DeepWeakReference<Activity> ref = mValidActivityList.get(
                mValidActivityList.size() - 1);
        return ref != null ? ref.get() : null;
    }

    public void activityResumed(Activity activity) {
        boolean isOldBackground = isBackground();

        ++mResumedActivityCount;
        if (isOldBackground && isForeground()) {
            EventManager.getInstance().notify(
                    JungleEvent.SWITCH_TO_FOREGROUND, null);
        }
    }

    public void activityPaused(Activity activity) {
        boolean isOldForeground = isForeground();

        --mResumedActivityCount;
        if (mResumedActivityCount < 0) {
            mResumedActivityCount = 0;
        }

        if (isOldForeground && isBackground()) {
            EventManager.getInstance().notify(
                    JungleEvent.SWITCH_TO_BACKGROUND, null);
        }
    }
}
