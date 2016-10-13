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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import com.jungle.base.misc.JungleSize;
import com.jungle.base.utils.ImageUtils;
import com.jungle.imageloader.R;
import com.jungle.widgets.layout.FixedScaleFrameLayout;

public class ImageCropLayout extends FrameLayout {

    private ScaleableImageView mImageView;
    private FixedScaleFrameLayout mCropFrameView;
    private Bitmap mBitmap;
    private int mImageMaxSize = 0;


    public ImageCropLayout(Context context) {
        super(context);
        initLayout(context, null);
    }

    public ImageCropLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, attrs);
    }

    public ImageCropLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.layout_image_crop, this);

        if (isInEditMode()) {
            return;
        }

        mCropFrameView = (FixedScaleFrameLayout) findViewById(R.id.crop_frame_view);
        mImageView = (ScaleableImageView) findViewById(R.id.crop_image_view);
        mImageView.setAlwaysCenter(false);

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(
                    attrs, R.styleable.ImageCropLayout);
            float whScale = arr.getFloat(R.styleable.ImageCropLayout_cropFrameAspectRatio, 1.0f);
            boolean showDashCircle = arr.getBoolean(R.styleable.ImageCropLayout_showDashCircle, true);

            setCropFrameWhScale(whScale);
            showDashCircle(showDashCircle);

            arr.recycle();
        }
    }

    public void setImageMaxSize(int imageMaxSize) {
        mImageMaxSize = imageMaxSize;
    }

    public void setCropFrameWhScale(float whScale) {
        mCropFrameView.setWHScale(whScale);
    }

    public void showDashCircle(boolean show) {
        View dashCircleView = findViewById(R.id.dash_circle);
        dashCircleView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private Point getDecodeSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    public void setImagePath(String filePath) {
        Point decodeSize = getDecodeSize();
        mBitmap = ImageUtils.decodeSampledBitmapFromFile(filePath, decodeSize.x, decodeSize.y);
        mImageView.setImageBitmap(mBitmap);
        updateScale();
    }

    public void setImagePath(Uri uri) {
        Point decodeSize = getDecodeSize();
        mBitmap = ImageUtils.decodeSampledBitmapFromUri(uri, decodeSize.x, decodeSize.y);
        mImageView.setImageBitmap(mBitmap);
        updateScale();
    }

    private void updateScale() {
        if (mBitmap == null) {
            return;
        }

        int bmpWidth = mBitmap.getWidth();
        int bmpHeight = mBitmap.getHeight();
        int width = mCropFrameView.getMeasuredWidth();
        int height = mCropFrameView.getMeasuredHeight();

        if (bmpWidth == 0 || bmpHeight == 0 || width == 0 || height == 0) {
            return;
        }

        float bmpRatio = (float) bmpWidth / (float) bmpHeight;
        float ratio = (float) width / (float) height;
        float scale = 0;
        if (bmpRatio > ratio) {
            scale = (float) height / (float) bmpHeight;
        } else {
            scale = (float) width / (float) bmpWidth;
        }

        mImageView.setScale(scale);
    }

    public Bitmap getCropBitmap() {
        return mBitmap;
    }

    public boolean cropImage(String croppedFilePath) {
        int imgWidth = mImageView.getMeasuredWidth();
        int imgHeight = mImageView.getMeasuredHeight();

        int targetWidth = mCropFrameView.getMeasuredWidth();
        int targetHeight = mCropFrameView.getMeasuredHeight();

        Bitmap bitmap = Bitmap.createBitmap(
                imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        mImageView.draw(canvas);

        bitmap = Bitmap.createBitmap(
                bitmap,
                (imgWidth - targetWidth) / 2,
                (imgHeight - targetHeight) / 2,
                targetWidth,
                targetHeight);

        if (mImageMaxSize > 0) {
            JungleSize size = ImageUtils.getMaxScaleSize(bitmap, mImageMaxSize);
            bitmap = ImageUtils.resizeBitmap(bitmap, size.mWidth, size.mHeight);
        }

        return ImageUtils.saveBitmapToFile(bitmap, croppedFilePath);
    }
}
