/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.widgets.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.jungle.mediaplayer.R;

public class AdjustPanel extends FrameLayout {

    private View mAdjustIconView;
    private ProgressBar mAdjustPercentProgress;


    public AdjustPanel(Context context) {
        super(context);
        initLayout(context);
    }

    public AdjustPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public AdjustPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_adjust_panel, this);
        mAdjustIconView = findViewById(R.id.adjust_icon);
        mAdjustPercentProgress = (ProgressBar) findViewById(R.id.adjust_percent);
    }

    public void adjustVolume(float percent) {
        adjust(R.drawable.adjust_volume, percent);
    }

    public void adjustBrightness(float percent) {
        adjust(R.drawable.adjust_brightness, percent);
    }

    public void hidePanel() {
        ViewGroup parent = (ViewGroup) getParent();
        parent.setVisibility(View.GONE);
    }

    private void adjust(int iconResId, float percent) {
        mAdjustIconView.setBackgroundResource(iconResId);
        mAdjustPercentProgress.setProgress((int) (percent * 100));

        ViewGroup parent = (ViewGroup) getParent();
        parent.setVisibility(View.VISIBLE);
    }
}
