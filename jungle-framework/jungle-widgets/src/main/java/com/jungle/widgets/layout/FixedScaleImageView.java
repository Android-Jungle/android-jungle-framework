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

package com.jungle.widgets.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FixedScaleImageView extends ImageView {

    private FixedScaleSupport mScaleSupport = new FixedScaleSupport();


    public FixedScaleImageView(Context context) {
        super(context);
        initLayout(context, null);
    }

    public FixedScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, attrs);
    }

    public FixedScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs) {
        mScaleSupport.init(this, attrs);
    }

    public FixedScaleSupport getScaleSupport() {
        return mScaleSupport;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        FixedScaleSupport.MeasureSize size = mScaleSupport.doMeasure(
                widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(size.mWidthMeasureSpec, size.mHeightMeasureSpec);
    }
}
