/**
 * Android Jungle framework project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
