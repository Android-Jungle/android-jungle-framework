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
