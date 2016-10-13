/*
 * Copyright (C) 2015. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2015/09/10
 */

package com.jungle.widgets.layout;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.jungle.imageloader.R;

public class FixedScaleSupport {

    public static class MeasureSize {
        public int mWidthMeasureSpec;
        public int mHeightMeasureSpec;

        public MeasureSize(int width, int height) {
            mWidthMeasureSpec = width;
            mHeightMeasureSpec = height;
        }
    }


    private int mFixedOrientation = FixOrientation.VERTICAL;
    private float mAspectRatio = 0.0f;
    private View mView;


    public void init(View view, AttributeSet attrs) {
        mView = view;
        if (attrs == null) {
            return;
        }

        TypedArray arr = mView.getContext().obtainStyledAttributes(
                attrs, R.styleable.FixedScaleSupport);
        mFixedOrientation = arr.getInt(
                R.styleable.FixedScaleSupport_fixOrientation, FixOrientation.VERTICAL);
        mAspectRatio = arr.getFloat(R.styleable.FixedScaleSupport_viewAspectRatio, 0.0f);

        arr.recycle();
    }

    public void setFixedOrientation(int fixedOrientation) {
        mFixedOrientation = fixedOrientation;
        mView.requestLayout();
    }

    public void setViewAspectRatio(float whScale) {
        mAspectRatio = whScale;
        mView.requestLayout();
    }

    public MeasureSize doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAspectRatio > 0) {
            if (mFixedOrientation == FixOrientation.VERTICAL) {
                final int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) (widthSize / mAspectRatio);
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            } else {
                final int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                int width = (int) (heightSize * mAspectRatio);
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            }
        }

        return new MeasureSize(widthMeasureSpec, heightMeasureSpec);
    }
}
