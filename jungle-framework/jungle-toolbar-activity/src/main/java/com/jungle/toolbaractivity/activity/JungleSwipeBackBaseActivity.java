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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.jungle.toolbaractivity.R;
import com.jungle.toolbaractivity.layout.HorizontalSwipeBackLayout;
import com.jungle.toolbaractivity.toolbar.JungleToolBar;

public class JungleSwipeBackBaseActivity<T extends JungleToolBar> extends JungleBaseActivity<T> {

    private HorizontalSwipeBackLayout mSwipeBackLayout;
    private Drawable mCustomizedBackground;


    @Override
    protected View getContentViewWhenCreate(View view) {
        View contentView = null;
        if (canSlideRightToFinish()) {
            mSwipeBackLayout = new HorizontalSwipeBackLayout(this);
            mSwipeBackLayout.setSlideListener(new HorizontalSwipeBackLayout.OnSlideListener() {
                @Override
                public void onSlideFinished() {
                    finish();
                    overridePendingTransition(resolveActivityOpenAnimationResId(), 0);
                }
            });

            mSwipeBackLayout.addView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            contentView = mSwipeBackLayout;
        } else {
            contentView = view;
        }

        return contentView;
    }

    @Override
    protected void afterSetContentView() {
        if (mSwipeBackLayout != null) {
            Drawable drawable = mCustomizedBackground;
            if (drawable == null) {
                TypedArray arr = getTheme().obtainStyledAttributes(
                        new int[]{android.R.attr.windowBackground});
                drawable = arr.getDrawable(0);
                arr.recycle();
            }

            Window window = getWindow();
            window.setFormat(PixelFormat.TRANSLUCENT);
            window.getDecorView().setBackgroundResource(R.color.translucent_mask_color);
            mSwipeBackLayout.setDecorView(window.getDecorView());
            mSwipeBackLayout.setBackgroundDrawable(drawable);
        }
    }

    protected void setCustomizedBackground(Drawable drawable) {
        mCustomizedBackground = drawable;
    }

    protected ViewGroup getContentRootView() {
        return (ViewGroup) findViewById(android.R.id.content);
    }

    protected boolean canSlideRightToFinish() {
        return false;
    }

    private int resolveActivityOpenAnimationResId() {
        Resources.Theme theme = getTheme();
        TypedValue value = new TypedValue();
        theme.resolveAttribute(android.R.attr.windowAnimationStyle, value, true);

        TypedArray arr = theme.obtainStyledAttributes(value.resourceId,
                new int[]{android.R.attr.activityOpenEnterAnimation});
        int animationId = arr.getResourceId(0, 0);
        arr.recycle();

        return animationId;
    }

    public void setSwipeBackEnable(boolean enable) {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setSwipeBackEnable(enable);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSwipeBackLayout != null
                && mSwipeBackLayout.getSwipeBackEnable()
                && mSwipeBackLayout.handleRootTouchEvent(event)) {
            return true;
        }

        return super.onTouchEvent(event);
    }
}
