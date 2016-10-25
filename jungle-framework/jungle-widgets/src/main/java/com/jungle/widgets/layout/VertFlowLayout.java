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
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VertFlowLayout extends ViewGroup {

    public interface VertFlowAdapter {
        int getCount();

        View getView(View convertView, int position, VertFlowLayout parent);
    }


    private static class ChildLayoutInfo {
        View mView;
        int mPosition;
        int mHeight;

        int getVertSpace() {
            return mHeight;
        }
    }

    private int mInvisibleHeight = 0;
    private int mVisibleHeight = 0;
    private int mCurrVisibleMinIndex = 0;
    private int mCurrVisibleMaxIndex = 0;
    private Stack<View> mRecycleViewList = new Stack<>();
    private Map<Integer, ChildLayoutInfo> mInvisibleChildLayoutList = new HashMap<>();
    private Map<Integer, ChildLayoutInfo> mVisibleChildLayoutList = new HashMap<>();


    private VertFlowAdapter mAdapter;


    public VertFlowLayout(Context context) {
        super(context);
    }

    public VertFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VertFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(VertFlowAdapter adapter) {
        mAdapter = adapter;
    }

    public boolean isCompleted() {
        return mCurrVisibleMaxIndex == mAdapter.getCount();
    }

    public void setShowVertOffset(int offset, int height) {
        if (height > mVisibleHeight && !isCompleted()) {
            int position = mCurrVisibleMaxIndex + 1;
            View v = getRecycleView(position);

            ChildLayoutInfo info = new ChildLayoutInfo();
            info.mView = v;
            info.mPosition = position;
            mVisibleChildLayoutList.put(position, info);

            return ;
        }
    }

    private View getRecycleView(int position) {
        View convertView = null;
        if (!mRecycleViewList.isEmpty()) {
            convertView = mRecycleViewList.pop();
        }

        return mAdapter.getView(convertView, position, this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int currHeight = mInvisibleHeight + getPaddingTop();

        for (ChildLayoutInfo info : mVisibleChildLayoutList.values()) {
            int width = info.mView.getMeasuredWidth();
            int height = info.mView.getMeasuredHeight();

            info.mHeight = height;
            info.mView.layout(paddingLeft, currHeight,
                    paddingLeft + width, currHeight + height);

            currHeight += height;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        widthSize -= getPaddingLeft() + getPaddingRight();
        heightSize -= getPaddingTop() + getPaddingBottom();


        int visibleHeight = 0;
        int childMeasureWidthSpec;
        if (heightMode == MeasureSpec.AT_MOST) {
            childMeasureWidthSpec = MeasureSpec.makeMeasureSpec(
                    widthSize, MeasureSpec.AT_MOST);
        } else {
            childMeasureWidthSpec = MeasureSpec.makeMeasureSpec(
                    0, MeasureSpec.UNSPECIFIED);
        }

        int childMeasureHeightSpec = MeasureSpec.makeMeasureSpec(
                0, MeasureSpec.UNSPECIFIED);

        for (ChildLayoutInfo info : mVisibleChildLayoutList.values()) {
            info.mView.measure(childMeasureWidthSpec, childMeasureHeightSpec);
            info.mHeight = info.mView.getMeasuredHeight();
            visibleHeight += info.mHeight;
        }

        updateVisibleHeight(visibleHeight);

        int heightRealSize = 0;
        if (heightMode == MeasureSpec.EXACTLY) {
            heightRealSize = heightSize;
        } else {
            heightRealSize = mVisibleHeight + mInvisibleHeight;

            if (heightMode == MeasureSpec.AT_MOST) {
                heightRealSize = Math.min(heightRealSize, heightSize);
            }
        }

        setMeasuredDimension(widthSize, heightRealSize);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        for (ChildLayoutInfo info : mVisibleChildLayoutList.values()) {
            info.mView.draw(canvas);
        }
    }

    private void updateInvisibleHeight() {
        mInvisibleHeight = 0;
        for (ChildLayoutInfo info : mInvisibleChildLayoutList.values()) {
            mInvisibleHeight += info.getVertSpace();
        }
    }

    private void updateVisibleHeight(int visibleHeight) {
        mVisibleHeight = visibleHeight;
    }
}
