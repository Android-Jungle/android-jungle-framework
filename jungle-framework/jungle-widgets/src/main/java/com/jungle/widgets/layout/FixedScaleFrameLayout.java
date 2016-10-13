/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FixedScaleFrameLayout extends FrameLayout {

    private FixedScaleSupport mScaleSupport = new FixedScaleSupport();


    public FixedScaleFrameLayout(Context context) {
        super(context);
        initLayout(context, null);
    }

    public FixedScaleFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, attrs);
    }

    public FixedScaleFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs) {
        mScaleSupport.init(this, attrs);
    }

    public FixedScaleSupport getScaleSupport() {
        return mScaleSupport;
    }

    public void setWHScale(float whScale) {
        mScaleSupport.setViewAspectRatio(whScale);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        FixedScaleSupport.MeasureSize size = mScaleSupport.doMeasure(
                widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(size.mWidthMeasureSpec, size.mHeightMeasureSpec);
    }
}
