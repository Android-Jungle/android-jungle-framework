/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.jungle.base.manager.ThreadManager;

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

        setPageState(PageState.Invisible);

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
            ThreadManager.getInstance().postOnUIHandlerDelayed(new Runnable() {
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
