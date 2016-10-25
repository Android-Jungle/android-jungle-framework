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
