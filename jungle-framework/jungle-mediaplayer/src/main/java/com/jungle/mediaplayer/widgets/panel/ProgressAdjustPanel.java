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
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jungle.base.utils.MiscUtils;
import com.jungle.mediaplayer.R;

public class ProgressAdjustPanel extends FrameLayout {

    public ProgressAdjustPanel(Context context) {
        super(context);
        initLayout(context);
    }

    public ProgressAdjustPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public ProgressAdjustPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_progress_adjust_panel, this);
    }

    public void adjustForward(int currProgressMS, int totalDurationMS) {
        adjustInternal(R.drawable.fast_forward, currProgressMS, totalDurationMS);
    }

    public void adjustBackward(int currProgressMS, int totalDurationMS) {
        adjustInternal(R.drawable.fast_backward, currProgressMS, totalDurationMS);
    }

    private void adjustInternal(int iconResId, int currProgressMS, int totalDurationMS) {
        View adjustIconView = findViewById(R.id.adjust_icon);
        adjustIconView.setBackgroundResource(iconResId);

        TextView currProgress = (TextView) findViewById(R.id.curr_progress);
        TextView totalDuration = (TextView) findViewById(R.id.total_duration);

        currProgress.setText(MiscUtils.formatTime(currProgressMS));
        totalDuration.setText(MiscUtils.formatTime(totalDurationMS));

        setVisibility(View.VISIBLE);
    }

    public void hidePanel() {
        setVisibility(View.GONE);
    }
}
