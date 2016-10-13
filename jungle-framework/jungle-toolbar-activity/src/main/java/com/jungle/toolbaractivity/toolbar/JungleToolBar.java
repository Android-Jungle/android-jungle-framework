/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.toolbaractivity.toolbar;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

public class JungleToolBar extends Toolbar {

    public JungleToolBar(Context context) {
        super(context);
        initToolbar();
    }

    public JungleToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initToolbar();
    }

    public JungleToolBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initToolbar();
    }

    private void initToolbar() {
    }

    @Override
    public void setTitle(int resId) {
    }

    @Override
    public void setTitle(CharSequence title) {
    }
}
