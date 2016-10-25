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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.jungle.base.common.HoldWeakRef;
import com.jungle.base.common.OnActivityResultListener;
import com.jungle.base.common.OnRequestPermissionsResultListener;
import com.jungle.base.manager.AppLifeManager;
import com.jungle.base.manager.EventManager;
import com.jungle.base.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private static enum LifeChangeType {
        None,
        Create,
        Start,
        Resume,
        Pause,
        Stop,
        Destroy,
        DoFinish,
    }


    public static void startActivityInternal(
            Context context, Class<? extends Activity> clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }


    private boolean mIsDestroyed = false;
    private boolean mIsFinished = false;
    private List<WeakReference<LifeCycleListener>> mLifeCycleListener =
            new LinkedList<>();
    private List<OnActivityResultListener> mActivityResultListener =
            new LinkedList<>();
    private List<OnRequestPermissionsResultListener> mRequestPermissionsResultListener =
            new LinkedList<>();


    public boolean isActivityDestroyed() {
        return mIsDestroyed;
    }

    public boolean isActivityFinished() {
        return mIsFinished;
    }

    public Context getContext() {
        return this;
    }

    protected void initAppCore() {
        AppCore core = AppCore.getInstance();
        if (core.isStarted()) {
            return;
        }

        core.start();
        while (!core.isStarted()) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAppCore();
        notifyLifeCycleChange(LifeChangeType.Create);
        AppLifeManager.getInstance().activityCreated(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LogUtils.e(TAG, "OnDestroy: %s.", getClass().getSimpleName());

        mIsDestroyed = true;
        notifyLifeCycleChange(LifeChangeType.Destroy);
        AppLifeManager.getInstance().activityDestroyed(this);

        mLifeCycleListener.clear();
        mActivityResultListener.clear();
        mRequestPermissionsResultListener.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();

        notifyLifeCycleChange(LifeChangeType.Start);
    }

    @Override
    protected void onStop() {
        super.onStop();

        notifyLifeCycleChange(LifeChangeType.Stop);
    }

    @Override
    protected void onPause() {
        super.onPause();

        notifyLifeCycleChange(LifeChangeType.Pause);
        AppLifeManager.getInstance().activityPaused(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        notifyLifeCycleChange(LifeChangeType.Resume);
        AppLifeManager.getInstance().activityResumed(this);
    }

    @Override
    public void finish() {
        super.finish();

        notifyLifeCycleChange(LifeChangeType.DoFinish);
        mIsFinished = true;
        EventManager.getInstance().tryRemoveAllEventListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtils.e(TAG, "onActivityResult: %s. [requestCode = %d, resultCode = %d.]",
                getClass().getSimpleName(), requestCode, resultCode);

        Set<OnActivityResultListener> removeSet = new HashSet<>();
        for (OnActivityResultListener listener : mActivityResultListener) {
            if (listener.onActivityResult(this, requestCode, resultCode, data)) {
                removeSet.add(listener);
            }
        }

        for (Iterator<OnActivityResultListener> iterator =
             mActivityResultListener.iterator();
             iterator.hasNext(); ) {

            if (removeSet.contains(iterator.next())) {
                iterator.remove();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Set<OnRequestPermissionsResultListener> removeSet = new HashSet<>();
        for (OnRequestPermissionsResultListener listener : mRequestPermissionsResultListener) {
            if (listener.onRequestPermissionsResult(this, requestCode, permissions, grantResults)) {
                removeSet.add(listener);
            }
        }

        for (Iterator<OnRequestPermissionsResultListener> iterator =
             mRequestPermissionsResultListener.iterator();
             iterator.hasNext(); ) {

            if (removeSet.contains(iterator.next())) {
                iterator.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findView(@IdRes int resId) {
        return (T) findViewById(resId);
    }

    protected void showToast(int toastResId) {
        Toast.makeText(this, toastResId, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    public void addActivityResultListener(OnActivityResultListener listener) {
        if (listener == null) {
            return;
        }

        mActivityResultListener.add(listener);
    }

    public void removeActivityResultListener(OnActivityResultListener listener) {
        if (listener == null) {
            return;
        }

        mActivityResultListener.remove(listener);
    }

    public void addRequestPermissionResultListener(OnRequestPermissionsResultListener listener) {
        if (listener == null) {
            return;
        }

        mRequestPermissionsResultListener.add(listener);
    }

    public void removeRequestPermissionResultListener(OnRequestPermissionsResultListener listener) {
        if (listener == null) {
            return;
        }

        mRequestPermissionsResultListener.remove(listener);
    }

    public void addLifeCycleListener(@HoldWeakRef LifeCycleListener listener) {
        if (listener == null) {
            return;
        }

        mLifeCycleListener.add(new WeakReference<LifeCycleListener>(listener));
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        if (listener == null) {
            return;
        }

        for (WeakReference<LifeCycleListener> ref : mLifeCycleListener) {
            LifeCycleListener inner = ref.get();
            if (inner == null || inner == listener) {
                mLifeCycleListener.remove(ref);
                break;
            }
        }
    }

    private void notifyLifeCycleChange(LifeChangeType type) {
        Iterator<WeakReference<LifeCycleListener>> iterator = mLifeCycleListener.iterator();
        while (iterator.hasNext()) {
            LifeCycleListener listener = iterator.next().get();
            if (listener == null) {
                iterator.remove();
                continue;
            }

            if (type == LifeChangeType.Create) {
                listener.onCreate(this);
            } else if (type == LifeChangeType.Start) {
                listener.onStart(this);
            } else if (type == LifeChangeType.Resume) {
                listener.onResume(this);
            } else if (type == LifeChangeType.Pause) {
                listener.onPause(this);
            } else if (type == LifeChangeType.Stop) {
                listener.onStop(this);
            } else if (type == LifeChangeType.Destroy) {
                listener.onDestroy(this);
            } else if (type == LifeChangeType.DoFinish) {
                listener.onDoFinish(this);
            }
        }
    }

    protected void startActivityInternal(Class<? extends Activity> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
}
