/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class WrapContentGridLayoutManager extends GridLayoutManager {

    public WrapContentGridLayoutManager(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WrapContentGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public WrapContentGridLayoutManager(
            Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onMeasure(
            RecyclerView.Recycler recycler, RecyclerView.State state,
            int widthSpec, int heightSpec) {

        int height = 0;
        int itemCount = getItemCount();
        int width = View.MeasureSpec.getSize(widthSpec);

        if (itemCount > 0) {
            try {
                View view = recycler.getViewForPosition(0);
                if (view != null) {
                    int childMeasureWidthSpec = widthSpec;
                    int spanCount = getSpanCount();
                    int widthMode = View.MeasureSpec.getMode(widthSpec);
                    if (widthMode == View.MeasureSpec.EXACTLY
                            || widthMode == View.MeasureSpec.AT_MOST) {
                        childMeasureWidthSpec = View.MeasureSpec.makeMeasureSpec(
                                width / spanCount, widthMode);
                    }

                    heightSpec = View.MeasureSpec.makeMeasureSpec(
                            0, View.MeasureSpec.UNSPECIFIED);
                    view.measure(childMeasureWidthSpec, heightSpec);

                    int row = itemCount / spanCount;
                    if (itemCount % spanCount != 0) {
                        ++row;
                    }

                    height = view.getMeasuredHeight() * row;
                }
            } catch (Exception e) {
            }
        }

        setMeasuredDimension(width, height);
    }
}
