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

package com.jungle.widgets.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public abstract class BaseLoadingLayout extends FrameLayout {

    public static enum PageState {
        Loading,        // Loading 状态.
        LoadingFailed,  // 加载失败.
        Empty,          // 空白页状态.
        Invisible       // 不可见状态.
    }


    public interface OnReloadListener {
        void onNeedReload();
    }

    public interface OnEmptyBtnListener {
        void onEmptyBtnClicked();
    }


    protected PageState mPageState = null;
    protected OnReloadListener mReloadListener;
    protected OnEmptyBtnListener mEmptyBtnListener;


    public BaseLoadingLayout(Context context) {
        super(context);
        init(context, null);
    }

    public BaseLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseLoadingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (getBackground() == null) {
            setBackgroundColor(getResources().getColor(android.R.color.white));
        }

        if (isInEditMode()) {
            return;
        }

        setClickable(true);
    }

    public void setLoading() {
        setPageState(PageState.Loading);
    }

    public void setLoadingFailed() {
        setPageState(PageState.LoadingFailed);
    }

    public void setEmpty() {
        setPageState(PageState.Empty);
    }

    public void setInvisible() {
        setPageState(PageState.Invisible);
    }

    public void setPageState(PageState state) {
        if (mPageState == state) {
            return;
        }

        mPageState = state;
        updatePages();
        setVisibility(state == PageState.Invisible ? View.GONE : View.VISIBLE);

        if (state == PageState.Loading) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateLoadingPage(true);
                }
            }, 100);
        } else {
            updateLoadingPage(false);
        }
    }

    protected abstract void updatePages();

    protected abstract void updateLoadingPage(boolean loading);

    public void setReloadListener(OnReloadListener listener) {
        mReloadListener = listener;
    }

    public void setEmptyBtnListener(OnEmptyBtnListener listener) {
        mEmptyBtnListener = listener;
    }
}
