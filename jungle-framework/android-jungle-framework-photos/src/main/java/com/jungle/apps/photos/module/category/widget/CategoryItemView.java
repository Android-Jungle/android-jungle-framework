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

package com.jungle.apps.photos.module.category.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.data.manager.CategoryManager;
import com.jungle.widgets.view.ClickEffectView;

public class CategoryItemView extends ClickEffectView {

    public static enum ViewType {
        Category,
        HotRecommend,
    }


    private float mWidthHeightScale = 1.0f / 1.0f;
    private boolean mIsSizeInitialized = false;
    private ViewType mViewType = ViewType.Category;
    private TextView mDescView;


    public CategoryItemView(Context context) {
        super(context);
        initLayout(context);
    }

    public CategoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public CategoryItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View view = View.inflate(context, R.layout.view_category_item, null);
        init(view);

        mDescView = (TextView) findViewById(R.id.category_description);
        setViewType(ViewType.Category);
        updateViewSize();
    }

    private void updateViewSize() {
        mIsSizeInitialized = false;
        getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(this);
                        calcSize();
                        return false;
                    }
                });
    }

    private void calcSize() {
        if (mIsSizeInitialized) {
            return;
        }

        mIsSizeInitialized = true;

        View imgView = findViewById(R.id.category_image);
        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        int width = imgView.getMeasuredWidth();
        params.height = (int) (width / mWidthHeightScale);

        imgView.setLayoutParams(params);
    }

    public void setViewType(ViewType viewType) {
        mViewType = viewType;

        if (mViewType == ViewType.HotRecommend) {
            mDescView.setVisibility(View.GONE);
        } else {
            mDescView.setVisibility(View.VISIBLE);
        }
    }

    public ImageView getImageView() {
        return (ImageView) findViewById(R.id.category_image);
    }

    public void setDefaultImageScale(float whScale) {
        mWidthHeightScale = whScale;
        updateViewSize();
    }

    public void updateView(CategoryManager.CategoryItem item) {
        if (mViewType != ViewType.HotRecommend) {
            mDescView.setText(item.mTitle);
        }
    }
}
