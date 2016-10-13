/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
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

public class JungleSwipeBackBaseActivity<T extends JungleToolBar>
        extends JungleBaseActivity<T> {

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
