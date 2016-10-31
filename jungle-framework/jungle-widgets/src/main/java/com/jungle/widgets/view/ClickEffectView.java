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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.jungle.widgets.R;

/**
 * 带额外点击效果的控件.(适用于 ImageView 等)
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
