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
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import com.jungle.base.manager.ThreadManager;
import com.jungle.imageloader.R;

public class ScaleableImageView extends ImageView {

    private static final float MIN_SCALE = 0.1f;
    private static final float MAX_SCALE = 10.0f;


    private static enum MoveMode {
        None,
        OnePointDown,
        Drag,
        Zoom
    }

    public interface OnGestureTouchListener {
        void OnSingleTapUp();

        void onLongPress();
    }


    private MoveMode mMoveMode = MoveMode.None;
    private Matrix mMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();
    private PointF mFirstPoint = new PointF();
    private PointF mCenterPoint = new PointF();
    private float mLastDistance = 0;

    private PointF mTranslate = new PointF();
    private float mScale = 0;
    private boolean mAlwaysCenter = false;
    private boolean mUpdateInit = false;
    private boolean mFitToParent = false;
    private OnGestureTouchListener mTouchListener;
    private long mTouchDownTimeStamp = 0;
    private boolean mSkipNextUp = false;


    public ScaleableImageView(Context context) {
        super(context);
        initImageView(context, null);
    }

    public ScaleableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initImageView(context, attrs);
    }

    public ScaleableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initImageView(context, attrs);
    }

    private void initImageView(Context context, AttributeSet attrs) {
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(mMatrix);

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(
                    attrs, R.styleable.ScaleableImageView);
            mFitToParent = arr.getBoolean(
                    R.styleable.ScaleableImageView_fitToParent, false);
            arr.recycle();
        }
    }

    public PointF getTranslate() {
        return mTranslate;
    }

    public float getScale() {
        return mScale;
    }

    public Matrix getCurrentMatrix() {
        return mMatrix;
    }

    public void setFitToParent(boolean fitToParent) {
        mFitToParent = fitToParent;
    }

    public void setScale(float scale) {
        mScale = scale;

        mMatrix.setScale(mScale, mScale);
        setImageMatrix(mMatrix);
        center();
    }

    public void setGestureTouchListener(OnGestureTouchListener listener) {
        mTouchListener = listener;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateInitDrawable();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        updateInitDrawable();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        updateInitDrawable();
    }

    private void updateInitDrawable() {
        final int viewWidth = getMeasuredWidth();
        final int viewHeight = getMeasuredHeight();

        if (viewWidth > 0 && viewHeight > 0) {
            updateInitDrawableInternal();
        } else {
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            updateInitDrawableInternal();
                        }
                    });

                    getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });

            invalidate();
        }
    }

    private void updateInitDrawableInternal() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            final int width = drawable.getIntrinsicWidth();
            final int height = drawable.getIntrinsicHeight();
            final int viewWidth = getMeasuredWidth();
            final int viewHeight = getMeasuredHeight();

            float horzScale = 0;
            if (width > viewWidth || mFitToParent) {
                horzScale = (float) viewWidth / (float) width;
            }

            float vertScale = 0;
            if (height > viewHeight || mFitToParent) {
                vertScale = (float) viewHeight / (float) height;
            }

            mMatrix.reset();
            mSavedMatrix.reset();
            float scale = Math.min(horzScale, vertScale);
            if (scale != 0) {
                mSavedMatrix.postScale(scale, scale, width / 2, height / 2);
                mMatrix.postScale(scale, scale, width / 2, height / 2);
            }

            postCenter();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        postCenter();
    }

    private void postCenter() {
        post(new Runnable() {
            @Override
            public void run() {
                center();
                setImageMatrix(mMatrix);
            }
        });
    }

    public void setAlwaysCenter(boolean alwaysCenter) {
        mAlwaysCenter = alwaysCenter;
    }

    private Runnable mLongPressRunnable = new Runnable() {
        @Override
        public void run() {
            final int time = ViewConfiguration.getLongPressTimeout();
            if (mMoveMode != MoveMode.OnePointDown
                    || System.currentTimeMillis() - mTouchDownTimeStamp < time) {
                return;
            }

            if (mTouchListener != null) {
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                playSoundEffect(SoundEffectConstants.CLICK);

                mSkipNextUp = true;
                mTouchListener.onLongPress();
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mSavedMatrix.set(mMatrix);
                mFirstPoint.set(x, y);
                mMoveMode = MoveMode.OnePointDown;
                mTouchDownTimeStamp = System.currentTimeMillis();
                mSkipNextUp = false;
                ThreadManager.getInstance().postOnUIHandlerDelayed(
                        mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mLastDistance = calcSpace(event);
                if (mLastDistance > 10.0f) {
                    mSavedMatrix.set(mMatrix);
                    calcCenterPoint(event);
                    mMoveMode = MoveMode.Zoom;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mMoveMode == MoveMode.OnePointDown) {
                    final int TRIGGER_MOVE_OFFSET = 50;
                    if (Math.abs(x - mFirstPoint.x) >= TRIGGER_MOVE_OFFSET
                            || Math.abs(y - mFirstPoint.y) >= TRIGGER_MOVE_OFFSET) {
                        mMoveMode = MoveMode.Drag;
                    }
                }

                if (mMoveMode == MoveMode.Drag) {
                    mMatrix.set(mSavedMatrix);

                    mTranslate.x = x - mFirstPoint.x;
                    mTranslate.y = y - mFirstPoint.y;
                    mMatrix.postTranslate(mTranslate.x, mTranslate.y);
                } else if (mMoveMode == MoveMode.Zoom) {
                    float distance = calcSpace(event);
                    if (distance > 10.0f) {
                        mMatrix.set(mSavedMatrix);

                        mScale = distance / mLastDistance;
                        mMatrix.postScale(mScale, mScale, mCenterPoint.x, mCenterPoint.y);
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mMoveMode = MoveMode.None;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ThreadManager.getInstance().getUIHandler().removeCallbacks(mLongPressRunnable);

                if (mMoveMode == MoveMode.OnePointDown) {
                    if (mTouchListener != null && !mSkipNextUp) {
                        mTouchListener.OnSingleTapUp();
                    }
                }
                mMoveMode = MoveMode.None;
                break;

            default:
                break;
        }

        setImageMatrix(mMatrix);

        if (mAlwaysCenter) {
            ensureViewPosition();
            center();
        }

        return true;
    }

    private void ensureViewPosition() {
        if (mMoveMode != MoveMode.Zoom) {
            return;
        }

        float pos[] = new float[9];
        mMatrix.getValues(pos);

        if (pos[0] < MIN_SCALE) {
            mMatrix.setScale(MIN_SCALE, MIN_SCALE);
        }

        if (pos[0] > MAX_SCALE) {
            mMatrix.set(mSavedMatrix);
        }
    }

    private void center() {
        center(true, true);
    }

    private void center(boolean centerHorz, boolean centerVert) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Matrix matrix = new Matrix();
        RectF rc = new RectF(0, 0, width, height);
        matrix.set(mMatrix);
        matrix.mapRect(rc);

        width = (int) rc.width();
        height = (int) rc.height();

        float deltaX = 0;
        float deltaY = 0;

        if (centerHorz) {
            int viewWidth = getMeasuredWidth();
            if (width <= viewWidth) {
                deltaX = (viewWidth - width) / 2 - rc.left;
            } else if (rc.left > 0) {
                deltaX = -rc.left;
            } else if (rc.right < viewWidth) {
                deltaX = viewWidth - rc.right;
            } else if (width > viewWidth) {
                deltaX = (viewWidth - width) / 2 - rc.left;
            }
        }

        if (centerVert) {
            int viewHeight = getMeasuredHeight();
            if (height <= viewHeight) {
                deltaY = (viewHeight - height) / 2 - rc.top;
            } else if (rc.top > 0) {
                deltaY = -rc.top;
            } else if (rc.bottom < viewHeight) {
                deltaY = viewHeight - rc.bottom;
            } else if (height > viewHeight) {
                deltaY = (viewHeight - height) / 2 - rc.top;
            }
        }

        mMatrix.postTranslate(deltaX, deltaY);
    }

    private float calcSpace(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void calcCenterPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        mCenterPoint.set(x / 2.0f, y / 2.0f);
    }
}
