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

package com.jungle.widgets.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.jungle.imageloader.R;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    public static enum Gravity {
        Top(1),
        Bottom(2),
        CenterVertical(3);

        int val;

        private Gravity(int v) {
            val = v;
        }

        static Gravity fromVal(int val) {
            if (val == Top.val) {
                return Top;
            } else if (val == Bottom.val) {
                return Bottom;
            } else if (val == CenterVertical.val) {
                return CenterVertical;
            }

            return CenterVertical;
        }
    }


    public static class LayoutParams extends MarginLayoutParams {

        public Gravity gravity = Gravity.CenterVertical;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray arr = c.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
            int gravityVal = arr.getInt(
                    R.styleable.FlowLayout_gravity, Gravity.CenterVertical.val);

            this.gravity = Gravity.fromVal(gravityVal);
            arr.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(int width, int height, Gravity gravity) {
            super(width, height);
            this.gravity = gravity;
        }
    }


    List<Integer> mRowHeightList = new ArrayList<Integer>();

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        width -= getPaddingLeft() + getPaddingRight();
        height -= getPaddingTop() + getPaddingBottom();

        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                width, MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = 0;

        if (heightMode == MeasureSpec.AT_MOST) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    height, MeasureSpec.AT_MOST);
        } else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    0, MeasureSpec.UNSPECIFIED);
        }

        int xPosition = getPaddingLeft();
        int yPosition = getPaddingTop();

        int rowCount = 0;
        int thisRowHeight = 0;
        final int childCount = getChildCount();

        mRowHeightList.clear();

        for (int i = 0; i < childCount; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams params = (LayoutParams) child.getLayoutParams();
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            int childWidth = child.getMeasuredWidth()
                    + params.leftMargin + params.rightMargin;
            int childHeight = child.getMeasuredHeight()
                    + params.topMargin + params.bottomMargin;

            thisRowHeight = Math.max(thisRowHeight, childHeight);
            if (xPosition + childWidth > width) {
                // Next line.
                mRowHeightList.add(thisRowHeight);

                xPosition = getPaddingLeft();
                yPosition += thisRowHeight;
                thisRowHeight = childHeight;
            }

            xPosition += childWidth;
        }

        mRowHeightList.add(thisRowHeight);

        final int realNeedVertSpace = yPosition + thisRowHeight;
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            height = realNeedVertSpace;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(height, realNeedVertSpace);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;

        int xPosition = getPaddingLeft();
        int yPosition = getPaddingTop();

        int rowIndex = 0;
        final int childCount = getChildCount();

        for (int i = 0; i < childCount; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            LayoutParams params = (LayoutParams) child.getLayoutParams();

            int childHorzSpace = childWidth + params.leftMargin + params.rightMargin;
            int childVertSpace = childHeight + params.topMargin + params.bottomMargin;
            int thisRowHeight = mRowHeightList.get(rowIndex);

            if (xPosition + childHorzSpace > width) {
                xPosition = getPaddingLeft();
                yPosition += thisRowHeight;
                ++rowIndex;
            }

            int top = yPosition + params.topMargin;
            int bottom = 0;

            if (params.gravity == Gravity.Top) {
                top = yPosition + params.topMargin;
                bottom = top + childHeight + params.bottomMargin;
            } else if (params.gravity == Gravity.Bottom) {
                bottom = yPosition + thisRowHeight;
                top = bottom - params.bottomMargin - childHeight;
            } else if (params.gravity == Gravity.CenterVertical) {
                top = yPosition + (thisRowHeight - childHeight) / 2;
                bottom = top + childHeight;
            }

            int left = xPosition + params.leftMargin;
            int right = left + childWidth;
            child.layout(left, top, right, bottom);

            xPosition += childHorzSpace;
        }
    }
}
