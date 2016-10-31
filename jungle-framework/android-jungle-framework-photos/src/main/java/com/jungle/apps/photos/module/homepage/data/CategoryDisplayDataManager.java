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

import android.text.TextUtils;
import com.jungle.apps.photos.module.homepage.widget.category.CategoryItem;
import com.jungle.base.utils.MiscUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryDisplayDataManager {

    private static final String LANTERNS = "lanterns";
    private static final String CATEGORIES = "categories";
    private static final String ITEMS = "items";
    private static final String CATEGORY = "category";
    private static final String TYPE = "type";
    private static final String TAG = "tag";
    private static final String URL = "url";
    private static final String HAS_DIVIDER = "has_divider";


    private List<CategoryItem.CategoryInfo> mLanternList =
            new ArrayList<CategoryItem.CategoryInfo>();

    private List<CategoryItem.CategoryItemInfo> mCategoryList =
            new ArrayList<CategoryItem.CategoryItemInfo>();


    public CategoryDisplayDataManager() {
        loadCategoryData();
    }

    public List<CategoryItem.CategoryInfo> getLanternList() {
        return mLanternList;
    }

    public List<CategoryItem.CategoryItemInfo> getCategoryList() {
        return mCategoryList;
    }

    private void loadCategoryData() {
        String categoryJson = MiscUtils.getAssetContent("categorylist.json");
        if (TextUtils.isEmpty(categoryJson)) {
            return;
        }

        JSONObject json = null;
        try {
            json = new JSONObject(categoryJson);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        try {
            initLantern(json.getJSONArray(LANTERNS));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            initCategory(json.getJSONArray(CATEGORIES));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initLantern(JSONArray lanterns) {
        int count = lanterns.length();
        try {
            for (int i = 0; i < count; ++i) {
                JSONObject lantern = lanterns.getJSONObject(i);
                CategoryItem.CategoryInfo info = new CategoryItem.CategoryInfo(
                        lantern.has(CATEGORY) ? lantern.getString(CATEGORY) : null,
                        lantern.getString(TAG),
                        lantern.getString(URL));

                mLanternList.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initCategory(JSONArray categories) {
        int count = categories.length();
        try {
            for (int i = 0; i < count; ++i) {
                JSONObject category = categories.getJSONObject(i);
                String typeDesc = category.getString(TYPE);
                CategoryItem.ItemType type = CategoryItem.getItemTypeFromString(typeDesc);
                if (type == null) {
                    continue;
                }

                CategoryItem.CategoryItemInfo info = new CategoryItem.CategoryItemInfo();
                info.mLayoutInfo = CategoryItem.getLayoutInfo(type);

                if (category.has(HAS_DIVIDER)) {
                    info.mHasDivider = category.getBoolean(HAS_DIVIDER);
                } else {
                    info.mHasDivider = false;
                }

                JSONArray items = category.getJSONArray(ITEMS);
                int itemCount = items.length();
                for (int j = 0; j < itemCount; ++j) {
                    JSONObject item = items.getJSONObject(j);
                    info.addItem(new CategoryItem.CategoryInfo(
                            item.has(CATEGORY) ? item.getString(CATEGORY) : null,
                            item.getString(TAG),
                            item.getString(URL)));
                }

                mCategoryList.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
