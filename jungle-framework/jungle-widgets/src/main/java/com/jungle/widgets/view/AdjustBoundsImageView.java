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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.jungle.imageloader.R;

/**
 * 可自动缩放调节大小的 ImageView.
 * Android 系统的 ImageView 只有在 minSDKVersion>=18 的时候才能自动调节.
 */
public class AdjustBoundsImageView extends ImageView {

    public static enum AdjustType {
        Horizontal(1),
        Vertical(2);

        int mVal = 0;
        AdjustType(int val) {
            mVal = val;
        }

        static AdjustType fromVal(int adjustType) {
            if (adjustType == Horizontal.mVal) {
                return Horizontal;
            } else if (adjustType == Vertical.mVal) {
                return Vertical;
            }

            return null;
        }
    }


    private boolean mNeedAdjust = false;
    private AdjustType mAdjustType = AdjustType.Vertical;


    public AdjustBoundsImageView(Context context) {
        super(context);

        init(context, null);
    }

    public AdjustBoundsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public AdjustBoundsImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(
                    attrs, R.styleable.AdjustBoundsImageView);
            int adjustType = arr.getInt(
                    R.styleable.AdjustBoundsImageView_adjustType,
                    AdjustType.Vertical.mVal);
            mAdjustType = AdjustType.fromVal(adjustType);

            arr.recycle();
        }
    }

    public void setAdjustType(AdjustType adjustType) {
        mAdjustType = adjustType;
        forceLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        final int width = drawable.getIntrinsicWidth();
        final int height = drawable.getIntrinsicHeight();

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mAdjustType == AdjustType.Vertical) {
            if (width == 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else {
                int imgAdjustHeight = widthSize * height / width;
                setMeasuredDimension(widthSize, imgAdjustHeight);
            }
        } else if (mAdjustType == AdjustType.Horizontal) {
            if (height == 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else {
                int imgAdjustWidth = heightSize * width / height;
                setMeasuredDimension(imgAdjustWidth, heightSize);
            }
        }
    }
}
