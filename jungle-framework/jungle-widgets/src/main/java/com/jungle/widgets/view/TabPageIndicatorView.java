/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class TabPageIndicatorView extends FrameLayout {

    public TabPageIndicatorView(Context context) {
        super(context);
    }

    public TabPageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabPageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        handleSelected(selected);
    }


    protected abstract void handleSelected(boolean selected);
}
