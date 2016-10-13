/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.layout;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HorzAdjustSwipeRefreshLayout extends SwipeRefreshLayout {

    private float mLastX = 0;
    private float mLastY = 0;


    public HorzAdjustSwipeRefreshLayout(Context context) {
        super(context);
    }

    public HorzAdjustSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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
