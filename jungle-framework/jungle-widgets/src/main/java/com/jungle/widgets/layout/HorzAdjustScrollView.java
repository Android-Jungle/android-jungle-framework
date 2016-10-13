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
import android.view.MotionEvent;
import android.widget.ScrollView;

public class HorzAdjustScrollView extends ScrollView {

    private float mLastX = 0;
    private float mLastY = 0;


    public HorzAdjustScrollView(Context context) {
        super(context);
        initLayout(context);
    }

    public HorzAdjustScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public HorzAdjustScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mLastX = event.getX();
            mLastY = event.getY();
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (Math.abs(event.getX() - mLastX) > Math.abs(event.getY() - mLastY)) {
                return false;
            }
        }

        return super.onInterceptTouchEvent(event);
    }
}