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

package com.jungle.apps.photos.module.homepage.widget.hot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.imageloader.ImageLoaderUtils;
import com.jungle.widgets.view.ClickEffectView;

public class HotImageDisplayView extends ClickEffectView {

    public HotImageDisplayView(Context context) {
        super(context);
        initLayout(context);
    }

    public HotImageDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public HotImageDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View v = View.inflate(
                context, R.layout.layout_hot_image_display, null);
        init(v);
    }

    public void setDisplayInfo(String groupName, String imgUrl) {
        TextView groupNameText = (TextView) findViewById(R.id.hot_group_name);
        ImageView hotImage = (ImageView) findViewById(R.id.hot_img_view);

        ImageLoaderUtils.displayImage(hotImage, imgUrl);
        groupNameText.setText(groupName);
    }
}
