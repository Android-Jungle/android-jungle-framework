/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.jungle.imageloader.R;

/**
 * 带额外点击效果的控件.(适用于 ImageView 等)
 *
 * @author arnozhang
 */
public class ClickEffectView extends FrameLayout {

    private Drawable mEffectDrawable;
    private Drawable mTransparentDrawable;
    private View mInternalView;
    private OnClickListener mClickListener;


    public ClickEffectView(Context context) {
        super(context);
    }

    public ClickEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickEffectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mClickListener = l;
    }

    public void init(View internalView) {
        mInternalView = internalView;
        addView(mInternalView, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        View v = new View(getContext());
        v.setClickable(true);
        v.setBackgroundResource(R.drawable.click_effect);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClick(ClickEffectView.this);
                }
            }
        });

        addView(v, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public View getInternalView() {
        return mInternalView;
    }
}
