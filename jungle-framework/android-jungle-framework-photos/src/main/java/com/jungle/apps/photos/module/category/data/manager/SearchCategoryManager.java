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

import com.jungle.apps.photos.module.category.data.CategoryStrategy;
import com.jungle.base.app.AppCore;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchCategoryManager extends CategoryManager {

    public static SearchCategoryManager getInstance() {
        return AppCore.getInstance().getManager(SearchCategoryManager.class);
    }


    private CategoryParser mSearchCategoryParser = new CategoryParser();

    public MgrType getCategoryType() {
        return MgrType.ForSearch;
    }

    @Override
    protected String generateFetchUrl(
            String category, String key, int fetchIndex, int count) {

        try {
            String keyEncode = null;
            keyEncode = URLEncoder.encode(key, "UTF-8");
            return CategoryStrategy.getSearchUrl(keyEncode, fetchIndex, count);
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
        return mSearchCategoryParser;
    }
}
