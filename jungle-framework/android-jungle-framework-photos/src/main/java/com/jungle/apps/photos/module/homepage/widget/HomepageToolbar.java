/**
 * Android photos application project.
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

package com.jungle.apps.photos.module.homepage.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.jungle.apps.photos.R;
import com.jungle.toolbaractivity.toolbar.JungleToolBar;

public class HomepageToolbar extends JungleToolBar {

    public HomepageToolbar(Context context) {
        super(context);
        init(context);
    }

    public HomepageToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomepageToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.layout_homepage_actionbar, this);
    }
}
