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

package com.jungle.base.manager;

import android.app.Activity;
import com.jungle.base.app.AppCore;
import com.jungle.base.common.DeepWeakReference;
import com.jungle.base.event.JungleEvent;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class AppLifeManager implements AppManager {

    public static AppLifeManager getInstance() {
        return AppCore.getInstance().getManager(AppLifeManager.class);
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

        AppCore.getApplication().doClean();
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
