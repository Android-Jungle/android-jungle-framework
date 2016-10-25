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
