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
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.homepage.data.HotTagAdapter;
import com.jungle.widgets.layout.FlowLayout;

public class HotTagLayoutView extends FlowLayout {

    private HotTagAdapter mAdapter;

    public HotTagLayoutView(Context context) {
        super(context);
    }

    public HotTagLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HotTagLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(HotTagAdapter adapter) {
        mAdapter = adapter;
        notifyTagsSetChanged();
    }

    public HotTagAdapter getAdapter() {
        return mAdapter;
    }

    public void notifyTagsSetChanged() {
        removeAllViews();

        if (mAdapter == null) {
            return ;
        }

        Resources res = getContext().getResources();
        int horz = res.getDimensionPixelSize(R.dimen.hot_tag_horz_space);
        int vert = res.getDimensionPixelSize(R.dimen.hot_tag_vert_space);

        int count = mAdapter.getTagCount();
        for (int i = 0; i < count; ++i) {
            View v = mAdapter.getTagView(i);
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params.leftMargin = horz;
            params.rightMargin = horz;
            params.topMargin = vert;
            params.bottomMargin = vert;
            addView(v, params);
        }
    }
}
