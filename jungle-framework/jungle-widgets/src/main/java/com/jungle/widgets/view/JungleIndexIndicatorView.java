/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.jungle.imageloader.R;

/**
 * 支持自定义的索引选中 View.
 *
 * @author arnozhang
 */
public class JungleIndexIndicatorView extends View {

    private int mIndexCount;
    private int mCurrSeledIndex;
    private int mIndexHorzMargin = 5;
    private int mIndexDrawWidth = 5;
    private int mIndexDrawHeight = 5;
    private int mSeledIndexDrawWidth = 5;
    private int mSeledIndexDrawHeight = 5;
    private Drawable mSeledDrawable;
    private Drawable mUnSeledDrawable;

    public JungleIndexIndicatorView(Context context) {
        super(context);
    }

    public JungleIndexIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initViewAttributes(context, attrs);
    }

    public JungleIndexIndicatorView(
            Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initViewAttributes(context, attrs);
    }

    private void initViewAttributes(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(
                attrs, R.styleable.JungleIndexIndicatorView);

        mCurrSeledIndex = 0;
        mIndexCount = arr.getInteger(
                R.styleable.JungleIndexIndicatorView_indicatorCount, 0);
        mIndexHorzMargin = arr.getDimensionPixelSize(
                R.styleable.JungleIndexIndicatorView_indicatorHorzMargin, 5);
        mIndexDrawWidth = arr.getDimensionPixelSize(
                R.styleable.JungleIndexIndicatorView_indicatorWidth, 5);
        mIndexDrawHeight = arr.getDimensionPixelSize(
                R.styleable.JungleIndexIndicatorView_indicatorHeight, 5);
        mSeledDrawable = arr.getDrawable(
                R.styleable.JungleIndexIndicatorView_seledIndicatorDrawable);
        mUnSeledDrawable = arr.getDrawable(
                R.styleable.JungleIndexIndicatorView_unSeledIndicatorDrawable);

        if (arr.hasValue(R.styleable.JungleIndexIndicatorView_seledIndicatorWidth)) {
            mSeledIndexDrawWidth = arr.getDimensionPixelSize(
                    R.styleable.JungleIndexIndicatorView_seledIndicatorWidth, 5);
        } else {
            mSeledIndexDrawWidth = mIndexDrawWidth;
        }

        if (arr.hasValue(R.styleable.JungleIndexIndicatorView_seledIndicatorHeight)) {
            mSeledIndexDrawHeight = arr.getDimensionPixelSize(
                    R.styleable.JungleIndexIndicatorView_seledIndicatorHeight, 5);
        } else {
            mSeledIndexDrawHeight = mIndexDrawHeight;
        }

        setIndexDrawSize(mIndexDrawWidth, mIndexDrawHeight);
        setSeledIndexDrawSize(mSeledIndexDrawWidth, mSeledIndexDrawHeight);
        arr.recycle();
    }

    /**
     * 设置索引个数.
     */
    public void setCount(int count) {
        mIndexCount = count;
        invalidate();
    }

    /**
     * 设置当前被选中的索引.
     */
    public void setCurrSeledIndex(int selIndex) {
        if (mCurrSeledIndex == selIndex
                || selIndex < 0 || selIndex >= mIndexCount) {
            return;
        }

        mCurrSeledIndex = selIndex;
        invalidate();
    }

    /**
     * 设置被选择索引绘制的 Drawable.
     */
    public void setSeledDrawableResId(int resId) {
        mSeledDrawable = getContext().getResources().getDrawable(resId);
        invalidate();
    }

    /**
     * 设置未被选择索引绘制的 Drawable.
     */
    public void setUnSeledDrawableResId(int resId) {
        mUnSeledDrawable = getContext().getResources().getDrawable(resId);
        invalidate();
    }

    /**
     * 设置两个索引之间的水平间距.
     */
    public void setIndexHorzMargin(int indexHorzMargin) {
        mIndexHorzMargin = indexHorzMargin;
        invalidate();
    }

    /**
     * 设置每个索引绘制大小.
     */
    public void setIndexDrawSize(int width, int height) {
        mIndexDrawWidth = width;
        mIndexDrawHeight = height;

        mUnSeledDrawable.setBounds(0, 0, mIndexDrawWidth, mIndexDrawHeight);
        invalidate();
    }

    /**
     * 设置被选中索引绘制的大小.
     */
    public void setSeledIndexDrawSize(int width, int height) {
        mSeledIndexDrawWidth = width;
        mSeledIndexDrawHeight = height;

        mSeledDrawable.setBounds(0, 0, mSeledIndexDrawWidth, mSeledIndexDrawHeight);
        invalidate();
    }

    private int getWidthNeedDrawSpace() {
        return mSeledIndexDrawWidth
                + (mIndexCount - 1) * mIndexDrawWidth
                + (mIndexCount - 1) * mIndexHorzMargin;
    }

    private int measureWidth(int widthMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int measuredWidth = 0;
        if (widthSpecMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSpecSize;
        } else {
            measuredWidth = getPaddingLeft() + getPaddingRight();
            measuredWidth += getWidthNeedDrawSpace();

            if (widthSpecMode == MeasureSpec.AT_MOST) {
                measuredWidth = Math.min(widthSpecSize, measuredWidth);
            }
        }

        return measuredWidth;
    }

    private int measureHeight(int heightMeasureSpec) {
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int measuredHeight = 0;
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            measuredHeight = heightSpecSize;
        } else {
            measuredHeight = getPaddingTop() + getPaddingBottom();
            measuredHeight += Math.max(mIndexDrawHeight, mSeledIndexDrawHeight);

            if (heightSpecMode == MeasureSpec.AT_MOST) {
                measuredHeight = Math.min(heightSpecSize, measuredHeight);
            }
        }

        return measuredHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measureWidth(widthMeasureSpec);
        int measuredHeight = measureHeight(heightMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSeledDrawable == null || mUnSeledDrawable == null || mIndexCount == 0) {
            return;
        }

        canvas.save();

        final int height = Math.max(mIndexDrawWidth, mSeledIndexDrawHeight);
        final int viewWidth = getMeasuredWidth();
        final int viewHeight = getMeasuredHeight();
        final int drawZoneWidth = getWidthNeedDrawSpace();

        final int horzTranslate = getPaddingLeft() +
                (viewWidth - getPaddingLeft() - getPaddingRight() - drawZoneWidth) / 2;
        final int vertTranslate = getPaddingTop() +
                (viewHeight - getPaddingTop() - getPaddingBottom() - height) / 2;

        // 平移到绘制区最左边,准备开始绘制.
        canvas.translate(horzTranslate, vertTranslate);

        for (int i = 0; i < mIndexCount; ++i) {
            int width = mIndexDrawWidth;
            if (mCurrSeledIndex == i) {
                width = mSeledIndexDrawWidth;
                mSeledDrawable.draw(canvas);
            } else {
                mUnSeledDrawable.draw(canvas);
            }

            canvas.translate(mIndexHorzMargin + width, 0);
        }

        canvas.restore();
    }
}
