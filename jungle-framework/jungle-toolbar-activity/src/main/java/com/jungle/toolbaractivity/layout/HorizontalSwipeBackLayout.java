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

package com.jungle.toolbaractivity.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import com.jungle.toolbaractivity.R;

public class HorizontalSwipeBackLayout extends FrameLayout {

    public interface OnSlideListener {
        void onSlideFinished();
    }


    private static enum SlideState {
        None,
        SkipThisRound,
        HandleHorz
    }


    private float mLastX;
    private float mLastY;
    private long mDownTimestamp = 0;
    private int mTriggerSlideSlop = 0;
    private SlideState mSlideState = SlideState.None;
    private OnSlideListener mSlideListener;
    private View mDecorView;
    private Drawable mBkgDrawable;
    private Drawable mEdgeShadowDrawable;
    private int mShadowWidth;
    private float mTranslationX;
    private boolean mSwipeBackEnable = true;


    public HorizontalSwipeBackLayout(Context context) {
        super(context);
        initLayout(context);
    }

    public HorizontalSwipeBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public HorizontalSwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        Resources res = getResources();
        mShadowWidth = res.getDimensionPixelSize(R.dimen.activity_edge_shadow_width);
        mEdgeShadowDrawable = res.getDrawable(R.drawable.activity_left_edge_shadow);
        mTriggerSlideSlop = (int) (1.5f * ViewConfigurationCompat.getScaledPagingTouchSlop(
                ViewConfiguration.get(getContext())));
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackEnable = enable;
    }

    public boolean getSwipeBackEnable() {
        return mSwipeBackEnable;
    }

    public void setSlideListener(OnSlideListener listener) {
        mSlideListener = listener;
    }

    public void setDecorView(View view) {
        mDecorView = view;
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        mBkgDrawable = background;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!mSwipeBackEnable) {
            return super.onInterceptTouchEvent(event);
        }

        final int action = event.getActionMasked();
        final float x = event.getRawX();
        final float y = event.getRawY();
        final float horzOffset = Math.abs(x - mLastX);
        final float vertOffset = Math.abs(y - mLastY);

        if (action == MotionEvent.ACTION_DOWN) {
            mLastX = x;
            mLastY = y;
            mDownTimestamp = System.currentTimeMillis();
            mSlideState = SlideState.None;
        }

        if (mSlideState == SlideState.SkipThisRound) {
            return super.onInterceptTouchEvent(event);
        }

        if (action == MotionEvent.ACTION_MOVE
                && handleTouchMove(x, y, horzOffset, vertOffset)) {
            return true;
        }

        return super.onInterceptTouchEvent(event);
    }

    private boolean handleTouchMove(
            float x, float y, float horzOffset, float vertOffset) {

        if (mSlideState == SlideState.HandleHorz) {
            updateSlideOffset(horzOffset);
            return true;
        } else if (horzOffset > mTriggerSlideSlop || vertOffset > mTriggerSlideSlop) {
            if (vertOffset > horzOffset || x < mLastX) {
                mSlideState = SlideState.SkipThisRound;
            } else {
                mSlideState = SlideState.HandleHorz;
                updateSlideOffset(horzOffset);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mSwipeBackEnable) {
            return super.onTouchEvent(event);
        }

        return handleRootTouchEvent(event) || super.onTouchEvent(event);
    }

    public boolean handleRootTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN || mSlideState == SlideState.SkipThisRound) {
            return false;
        }

        final float x = event.getRawX();
        final float y = event.getRawY();
        final float horzOffset = Math.abs(x - mLastX);
        final float vertOffset = Math.abs(y - mLastY);

        if (action == MotionEvent.ACTION_MOVE
                && handleTouchMove(x, y, horzOffset, vertOffset)) {
            return true;
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            int width = getMeasuredWidth();
            long timeOffset = System.currentTimeMillis() - mDownTimestamp;
            mSlideState = SlideState.None;

            if (horzOffset > width / 2 || (timeOffset <= 600 && horzOffset > width / 6)) {
                continueAnimation(horzOffset, width);
            } else {
                updateSlideOffset(0);
            }

            return true;
        }

        return false;
    }

    private void updateSlideOffset(float offset) {
        int count = getChildCount();
        if (count == 0) {
            return;
        }

        mTranslationX = offset;
        for (int i = 0; i < count; ++i) {
            View child = getChildAt(0);
            child.setTranslationX(offset);
        }

        if (mDecorView != null) {
            int width = getMeasuredWidth();
            int alpha = (int) (0xb0 - (0xb0 - 0x10) * offset / width);
            int color = Color.argb(alpha, 0x00, 0x00, 0x00);
            mDecorView.setBackgroundColor(color);
        }

        invalidate();
    }

    private void continueAnimation(float horzOffset, int width) {
        ValueAnimator animator = ValueAnimator.ofFloat(horzOffset, width);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration((long) (150 * Math.abs(width - horzOffset) / width));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                updateSlideOffset(value);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mSlideListener != null) {
                    mSlideListener.onSlideFinished();
                }
            }
        });

        animator.start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!mSwipeBackEnable) {
            super.dispatchDraw(canvas);
            return;
        }

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        if (mBkgDrawable != null) {
            mBkgDrawable.setBounds((int) (mTranslationX + 0.5f), 0, width, height);
            mBkgDrawable.draw(canvas);
        } else {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            canvas.drawRect(mTranslationX, 0, width, height, paint);
        }

        if (mTranslationX > 0) {
            canvas.save();
            canvas.translate(mTranslationX - mShadowWidth, 0);
            mEdgeShadowDrawable.setBounds(0, 0, mShadowWidth, height);
            mEdgeShadowDrawable.draw(canvas);
            canvas.restore();
        }

        super.dispatchDraw(canvas);
    }
}
