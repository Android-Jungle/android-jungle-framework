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

package com.jungle.apps.photos.module.category.data.manager;

import android.text.TextUtils;
import com.jungle.apps.photos.base.component.AppUtils;
import com.jungle.apps.photos.module.category.data.CategoryStrategy;
import com.jungle.base.app.AppCore;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NormalCategoryManager extends CategoryManager {

    public static NormalCategoryManager getInstance() {
        return AppCore.getInstance().getManager(NormalCategoryManager.class);
    }


    private CategoryParser mNormalCategoryParser = new CategoryParser();


    public MgrType getCategoryType() {
        return MgrType.ForNormal;
    }

    @Override
    protected String generateFetchUrl(
            String category, String tag, int fetchIndex, int count) {

        if (TextUtils.isEmpty(category)) {
            category = AppUtils.getMainCategory();
        }

        try {
            String categoryEncode = null;
            String tagEncode = null;
            categoryEncode = URLEncoder.encode(category, "UTF-8");
            tagEncode = URLEncoder.encode(tag, "UTF-8");

            return CategoryStrategy.getCategoryUrl(categoryEncode, tagEncode, fetchIndex, count);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected CategoryInfo createDefaultCategoryInfo(String category, String key) {
        return new CategoryInfo(category, key);
    }

    @Override
    protected CategoryParser getCategoryParser() {
        return mNormalCategoryParser;
    }
}
