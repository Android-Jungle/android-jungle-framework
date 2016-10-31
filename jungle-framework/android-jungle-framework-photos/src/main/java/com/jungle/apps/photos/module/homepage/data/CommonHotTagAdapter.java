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

package com.jungle.apps.photos.module.homepage.data;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.component.AppUtils;
import com.jungle.apps.photos.module.category.CategoryActivity;
import com.jungle.apps.photos.module.category.data.manager.SearchCategoryManager;
import com.jungle.apps.photos.module.category.widget.CategoryTagItemLongClickListener;
import com.jungle.apps.photos.module.misc.ColorList;

import java.util.List;

public class CommonHotTagAdapter implements HotTagAdapter {

    private Context mContext;
    protected List<String> mTagList;
    private boolean mEditable = false;


    public CommonHotTagAdapter(Context context, List<String> tagList) {
        this(context, tagList, true);
    }

    public CommonHotTagAdapter(Context context, List<String> tagList, boolean editable) {
        mContext = context;
        mTagList = tagList;
        mEditable = editable;
    }

    public void updateList(List<String> tagList) {
        mTagList = tagList;
    }

    @Override
    public int getTagCount() {
        return mTagList != null ? mTagList.size() : 0;
    }

    @Override
    public View getTagView(int position) {
        String tag = mTagList.get(position);
        TextView view = (TextView) View.inflate(mContext, R.layout.view_hot_tag, null);
        view.setText(tag);
        view.setTag(tag);
        view.setTextColor(ColorList.randomNextColor());
        view.setOnClickListener(mTagViewClickListener);

        if (mEditable) {
            view.setOnLongClickListener(mTagViewLongClickListener);

            view.performHapticFeedback(
                    HapticFeedbackConstants.LONG_PRESS,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            view.playSoundEffect(SoundEffectConstants.CLICK);
        }

        return view;
    }

    private View.OnClickListener mTagViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = (String) v.getTag();
            viewTagWithCategory(tag);
        }
    };

    private View.OnLongClickListener mTagViewLongClickListener =
            new CategoryTagItemLongClickListener() {
                @Override
                protected String getTag(View v) {
                    return (String) v.getTag();
                }

                @Override
                protected String getCategory(View v) {
                    return AppUtils.getMainCategory();
                }
            };

    private void viewTagWithCategory(String tag) {
        CategoryActivity.startCategoryActivity(
                mContext, tag,
                SearchCategoryManager.getInstance().getCategoryProvider(
                        AppUtils.getMainCategory(), tag));
    }
}
