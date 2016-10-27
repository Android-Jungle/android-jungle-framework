package com.jungle.apps.photos.module.imgviewer.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ScalableImageView extends ImageView {

    private static final float DEFAULT_MIN_SCALE = 1.0f;
    private static final float DEFAULT_MAX_SCALE = 10.0f;


    private static enum MoveMode {
        None,
        Drag,
        Zoom
    }


    private MoveMode mMoveMode = MoveMode.None;
    private Matrix mMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();
    private PointF mFirstPoint = new PointF();
    private PointF mCenterPoint = new PointF();
    private float mLastDistance = 0;
    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;
    private boolean mWaitInitPosition = false;
    private OnTouchListener mOnTouchListener;


    public ScalableImageView(Context context) {
        super(context);
        initImageView();
    }

    public ScalableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initImageView();
    }

    public ScalableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initImageView();
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    public void setMaxScale(float maxScale) {
        mMaxScale = maxScale;
    }

    public void resetScale() {
        mMatrix.reset();
        mSavedMatrix.reset();
        setImageMatrix(mMatrix);
    }

    private void initImageView() {
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(mMatrix);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        postInitCenter();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        postInitCenter();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        postInitCenter();
    }

    public void setOnCustomTouchListener(OnTouchListener l) {
        mOnTouchListener = l;
    }

    private void postInitCenter() {
        mWaitInitPosition = true;

        post(new Runnable() {
            @Override
            public void run() {
                resetScale();

                Drawable drawable = getDrawable();
                if (drawable != null) {
                    float width = drawable.getIntrinsicWidth();
                    float height = drawable.getIntrinsicHeight();
                    float scale = 1.0f;

                    float viewWidth = getMeasuredWidth();
                    float viewHeight = getMeasuredHeight();

                    scale = Math.min(viewWidth / width, viewHeight / height);
                    mMatrix.postScale(scale, scale);
                }

                center();
                setImageMatrix(mMatrix);

                mWaitInitPosition = false;
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mWaitInitPosition) {
            super.onDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mSavedMatrix.set(mMatrix);
                mFirstPoint.set(x, y);
                mMoveMode = MoveMode.Drag;
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
                if (mMoveMode == MoveMode.Drag) {
                    mMatrix.set(mSavedMatrix);
                    mMatrix.postTranslate(x - mFirstPoint.x, y - mFirstPoint.y);
                } else if (mMoveMode == MoveMode.Zoom) {
                    float distance = calcSpace(event);
                    if (distance > 10.0f) {
                        mMatrix.set(mSavedMatrix);
                        float scale = distance / mLastDistance;
                        mMatrix.postScale(scale, scale, mCenterPoint.x, mCenterPoint.y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                mMoveMode = MoveMode.None;
                break;

            default:
                break;
        }

        setImageMatrix(mMatrix);
        ensureViewPosition();
        center();

        if (mOnTouchListener != null) {
            MotionEvent motionEvent = MotionEvent.obtain(event);
            mOnTouchListener.onTouch(null, motionEvent);
        }

        return true;
    }

    private void ensureViewPosition() {
        if (mMoveMode != MoveMode.Zoom) {
            return;
        }

        float pos[] = new float[9];
        mMatrix.getValues(pos);
        float currScale = pos[0];

        if (currScale < mMinScale) {
            mMatrix.setScale(mMinScale, mMinScale);
        }

        if (currScale > mMaxScale) {
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
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rc.left;
            } else if (rc.left > 0) {
                deltaX = -rc.left;
            } else if (rc.right < viewWidth) {
                deltaX = viewWidth - rc.right;
            }
        }

        if (centerVert) {
            int viewHeight = getMeasuredHeight();
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rc.top;
            } else if (rc.top > 0) {
                deltaY = -rc.top;
            } else if (rc.bottom < viewHeight) {
                deltaY = viewHeight - rc.bottom;
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
