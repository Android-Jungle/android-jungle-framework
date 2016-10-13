/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

public class VerticalAlignImageSpan extends ImageSpan {

    public VerticalAlignImageSpan(Bitmap b) {
        super(b);
    }

    public VerticalAlignImageSpan(Bitmap b, int verticalAlignment) {
        super(b, verticalAlignment);
    }

    public VerticalAlignImageSpan(Context context, Bitmap b) {
        super(context, b);
    }

    public VerticalAlignImageSpan(Context context, Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

    public VerticalAlignImageSpan(Drawable d) {
        super(d);
    }

    public VerticalAlignImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public VerticalAlignImageSpan(Drawable d, String source) {
        super(d, source);
    }

    public VerticalAlignImageSpan(Drawable d, String source, int verticalAlignment) {
        super(d, source, verticalAlignment);
    }

    public VerticalAlignImageSpan(Context context, Uri uri) {
        super(context, uri);
    }

    public VerticalAlignImageSpan(Context context, Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public VerticalAlignImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public VerticalAlignImageSpan(Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();

        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }

        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
            float x, int top, int y, int bottom, Paint paint) {

        Drawable b = getDrawable();

        canvas.save();
        int transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent / 2 - 5;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
