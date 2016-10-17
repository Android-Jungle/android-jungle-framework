/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;

import java.util.LinkedList;

public class BlurProxy {

    private static final float RADIUS = 4;
    private static final float SCALE_FACTOR = 8;


    public interface BlurCallBack {
        void onCompletedBlur(Bitmap bitmap);
    }


    private Context mContext;
    private View mSrcView;
    private View mDestView;
    private float mBlurRadius = RADIUS;
    private float mBlurScaleFactor = SCALE_FACTOR;


    public BlurProxy(Context context) {
        mContext = context;
    }

    public BlurProxy(Context context, float radius, float scaleFactor) {
        mContext = context;
        mBlurRadius = radius;
        mBlurScaleFactor = scaleFactor;
    }

    public Context getContext() {
        return mContext;
    }

    public void doBlur(View srcView, View descView) {
        if (srcView == null || descView == null) {
            return;
        }

        mSrcView = srcView;
        mDestView = descView;

        mDestView.getViewTreeObserver().addOnGlobalLayoutListener(
                mGlobalLayoutListener);
    }

    private OnGlobalLayoutListener mGlobalLayoutListener = new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            ViewTreeObserver observer = mSrcView.getViewTreeObserver();
            if (observer != null) {
                observer.addOnPreDrawListener(new OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {

                        ViewTreeObserver observer = mSrcView.getViewTreeObserver();
                        if (observer != null) {
                            observer.removeOnPreDrawListener(this);
                        }

                        mSrcView.setDrawingCacheEnabled(true);
                        mSrcView.buildDrawingCache();

                        int[] srcLocation = new int[2];
                        int[] descLocation = new int[2];
                        mSrcView.getLocationInWindow(srcLocation);
                        mDestView.getLocationInWindow(descLocation);
                        int x = descLocation[0] - srcLocation[0];
                        int y = descLocation[1] - srcLocation[1];
                        blurredView(mSrcView.getDrawingCache(),
                                mDestView, x, y);
                        return true;
                    }
                });
            }
        }
    };

    public void doBlur(final View parentView, int x, int y, int width, int height,
            final BlurCallBack callback) {

        parentView.setDrawingCacheEnabled(true);
        parentView.buildDrawingCache();
        Bitmap srcBitmap = parentView.getDrawingCache();
        if (srcBitmap == null) {
            return;
        }

        Bitmap overlay = Bitmap.createBitmap((int) (width / mBlurScaleFactor),
                (int) (height / mBlurScaleFactor), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-x / mBlurScaleFactor, -y / mBlurScaleFactor);
        canvas.scale(1 / mBlurScaleFactor, 1 / mBlurScaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);

        Rect src = new Rect(x, y, x + width, y + height);
        Rect dest = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(srcBitmap, src, dest, paint);

        ParamRunnable<Bitmap> runnable = new ParamRunnable<Bitmap>() {
            @Override
            public void runWithParam(Bitmap param) {
                Bitmap bitmap = BlurAlgorithm.doBlur(mContext, param, (int) mBlurRadius, true);
                ParamRunnable<Bitmap> runnable = new ParamRunnable<Bitmap>() {

                    @Override
                    public void runWithParam(Bitmap param1) {
                        if (callback != null) {
                            callback.onCompletedBlur(param1);
                        }
                    }
                };

                runnable.pushParam(bitmap);
                parentView.post(runnable);
            }
        };

        runnable.pushParam(overlay);
        new Thread(runnable).start();
    }

    public void blurredView(Bitmap bitmap, final View view, int x, int y) {
        if (bitmap == null) {
            return;
        }

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / mBlurScaleFactor),
                (int) (view.getMeasuredHeight() / mBlurScaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / mBlurScaleFactor, -view.getTop() / mBlurScaleFactor);
        canvas.scale(1 / mBlurScaleFactor, 1 / mBlurScaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        int visibleWidth = view.getWidth();
        int visibleHeight = view.getHeight();

        Rect src = new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
        Rect dest = new Rect(x, y, x + visibleWidth, y + visibleHeight);
        canvas.drawBitmap(bitmap, src, dest, paint);

        ParamRunnable<Pair<View, Bitmap>> runnable = new ParamRunnable<Pair<View, Bitmap>>() {

            @Override
            public void runWithParam(Pair<View, Bitmap> obj) {
                // 进行高斯模糊操作
                Bitmap bitmap = BlurAlgorithm.doBlur(
                        mContext, obj.second, (int) mBlurRadius, true);

                ParamRunnable<Pair<View, Bitmap>> runnable = new ParamRunnable<Pair<View, Bitmap>>() {
                    @Override
                    public void runWithParam(Pair<View, Bitmap> param) {

                        if (Build.VERSION.SDK_INT < 16) {
                            param.first.setBackgroundDrawable(
                                    new BitmapDrawable(mContext.getResources(),
                                            param.second));
                        } else {
                            param.first.setBackground(
                                    new BitmapDrawable(mContext.getResources(),
                                            param.second));
                        }

                        param.first.invalidate();
                    }
                };

                runnable.pushParam(new Pair<View, Bitmap>(obj.first, bitmap));
                view.post(runnable);
            }
        };

        runnable.pushParam(new Pair<View, Bitmap>(view, overlay));
        new Thread(runnable).start();
    }


    public abstract class ParamRunnable<T> implements Runnable {
        private final LinkedList<T> mDataList = new LinkedList<T>();

        public void pushParam(T o) {
            synchronized (mDataList) {
                mDataList.add(o);
            }
        }

        public void run() {
            LinkedList<T> temp;
            synchronized (mDataList) {
                temp = (LinkedList<T>) mDataList.clone();
                mDataList.clear();
            }

            for (T o : temp) {
                runWithParam(o);
            }
        }

        public abstract void runWithParam(T o);
    }
}
