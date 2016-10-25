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

package com.jungle.toolbaractivity.activity;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import com.jungle.base.app.BaseActivity;
import com.jungle.toolbaractivity.R;
import com.jungle.toolbaractivity.toolbar.JungleToolBar;

public class JungleBaseActivity<T extends JungleToolBar> extends BaseActivity {

    private boolean mOverlayToolbar = false;
    private boolean mNoToolbar = false;
    private T mCustomizedToolbar;
    private FrameLayout mRootContainer;
    protected AppBarLayout mAppBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        resolveStyles();
        if (mOverlayToolbar) {
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        }

        super.onCreate(savedInstanceState);

        View view = View.inflate(this, getContentLayoutResId(), null);
        View contentView = getContentViewWhenCreate(view);

        super.setContentView(contentView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        afterSetContentView();

        mRootContainer = findView(R.id.jungle_root_container);
        mAppBarLayout = findView(R.id.jungle_appbar_layout);

        initToolBar();
    }

    protected int getContentLayoutResId() {
        return mOverlayToolbar
                ? R.layout.activity_jungle_base_overlay
                : R.layout.activity_jungle_base;
    }

    protected void afterSetContentView() {
    }

    protected View getContentViewWhenCreate(View view) {
        return null;
    }

    protected ViewGroup getContentRootView() {
        return (ViewGroup) findViewById(android.R.id.content);
    }

    protected void showToolbar(boolean show) {
        mAppBarLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void resolveStyles() {
        TypedArray arr = getTheme().obtainStyledAttributes(new int[]{
                R.attr.jungle_overlayToolbar,
                R.attr.jungle_noToolbar,
                R.attr.jungle_slideRightToFinish});
        if (!mOverlayToolbar) {
            if (arr.hasValue(0)) {
                mOverlayToolbar = arr.getBoolean(0, false);
            }
        }

        if (arr.hasValue(1)) {
            mNoToolbar = arr.getBoolean(1, false);
        }

        arr.recycle();
    }

    public boolean isOverlayToolbar() {
        return mOverlayToolbar;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(layoutResID, mRootContainer, false);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, null);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (params == null) {
            mRootContainer.addView(view);
        } else {
            mRootContainer.addView(view, params);
        }
    }

    protected T createCustomizedToolbar() {
        return null;
    }

    protected void hideToolbarShadow() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAppBarLayout.setElevation(0);

            if (mCustomizedToolbar != null) {
                mCustomizedToolbar.setElevation(0);
            }
        }
    }

    private void initToolBar() {
        if (mNoToolbar) {
            mAppBarLayout.setVisibility(View.GONE);
            return;
        }

        mCustomizedToolbar = createCustomizedToolbar();
        if (mCustomizedToolbar == null) {
            return;
        }

        mAppBarLayout.addView(mCustomizedToolbar);
        setSupportActionBar(mCustomizedToolbar);
        if (mOverlayToolbar) {
            mCustomizedToolbar.setBackgroundResource(android.R.color.transparent);
        }
    }

    public T getCustomizedToolbar() {
        return mCustomizedToolbar;
    }

    public void setOverlayToolbar(boolean overLay) {
        mOverlayToolbar = overLay;
    }

    public void setToolbarBackground(int resId) {
        if (mCustomizedToolbar != null) {
            mCustomizedToolbar.setBackgroundResource(resId);
        }
    }

    public void setToolBarBackgroundColor(int color) {
        if (mCustomizedToolbar != null) {
            mCustomizedToolbar.setBackgroundColor(color);
        }
    }

    public void setToolbarBackground(Drawable drawable) {
        if (mCustomizedToolbar != null) {
            mCustomizedToolbar.setBackgroundDrawable(drawable);
        }
    }
}
