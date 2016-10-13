/*
 * Copyright (C) 2015. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2015/08/19
 */

package com.jungle.widgets.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VertWrapContentViewPager extends ViewPager {

    public static final int TAG_POSITION = "tag_position".hashCode();

    private boolean mScrollEnable = true;


    public VertWrapContentViewPager(Context context) {
        super(context);
        initLayout(context);
    }

    public VertWrapContentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public VertWrapContentViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        initLayout(context);
    }

    private void initLayout(Context context) {
        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                requestLayout();
                invalidate();
            }
        });
    }

    public void setScrollEnable(boolean enable) {
        mScrollEnable = enable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mScrollEnable) {
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mScrollEnable) {
            return false;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int position = getCurrentItem();

        int childHeight = 0;
        for (int i = 0; i < getChildCount(); ++i) {
            View childView = getChildAt(i);
            childView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                    0, MeasureSpec.UNSPECIFIED));
            int height = childView.getMeasuredHeight();

            Object tag = childView.getTag(TAG_POSITION);
            if (tag instanceof Integer) {
                int tagPos = (int) tag;
                if (position == tagPos) {
                    childHeight = height;
                    break;
                }
            }

            if (height > childHeight) {
                childHeight = height;
            }
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                childHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
