/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.jungle.imageloader.R;

public class ImageCropFrameLayout extends FrameLayout {

    private View mCropFrameView;


    public ImageCropFrameLayout(Context context) {
        super(context);
        initLayout(context);
    }

    public ImageCropFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public ImageCropFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_image_crop_frame, this);
        mCropFrameView = findViewById(R.id.crop_frame_view);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int left = mCropFrameView.getLeft();
        int right = mCropFrameView.getRight();
        int top = mCropFrameView.getTop();
        int bottom = mCropFrameView.getBottom();

        canvas.save();

        canvas.clipRect(0, 0, getWidth(), getHeight(), Region.Op.UNION);
        canvas.clipRect(left, top, right, bottom, Region.Op.XOR);
        canvas.drawColor(getResources().getColor(R.color.crop_image_frame));

        canvas.restore();
    }
}