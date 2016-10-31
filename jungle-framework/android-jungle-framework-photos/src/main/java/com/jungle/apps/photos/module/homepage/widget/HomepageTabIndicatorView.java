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
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.widgets.view.TabPageIndicatorView;

public class HomepageTabIndicatorView extends TabPageIndicatorView {

    private TextView mIndicatorText;
    private View mNewIcon;


    public HomepageTabIndicatorView(Context context) {
        super(context);
        initLayout(context);
    }

    public HomepageTabIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public HomepageTabIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_homepage_tab_indicator, this);
        mIndicatorText = (TextView) findViewById(R.id.indicator_text);
        mNewIcon = findViewById(R.id.new_icon);

        showNewIcon(false);
    }

    @Override
    protected void handleSelected(boolean selected) {
        View indicatorRootView = findViewById(R.id.indicator_root);
        indicatorRootView.setSelected(selected);
        mIndicatorText.setSelected(selected);
    }

    public void showNewIcon(boolean show) {
        mNewIcon.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setIndicatorText(int textResId) {
        setIndicatorText(getContext().getString(textResId));
    }

    public void setIndicatorText(String text) {
        mIndicatorText.setText(text);
    }
}
