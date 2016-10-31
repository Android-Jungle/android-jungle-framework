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

package com.jungle.apps.photos.module.category.data;

import android.text.TextUtils;
import com.jungle.apps.photos.base.component.Md5FileNameGenerator;

public class CategoryStrategy {

    public static final int DEFAULT_FETCH_COUNT = 20;

    public static final String CATEGORY_BASE_URL =
            "http://www.jungle.com/photobound/content/get_pic_list?channel_id=%s&app_version=%d&category=%s&tag=%s&start=%d&len=%d";

    public static final String SEARCH_BASE_URL =
            "http://www.jungle.com/photobound/content/search_pic?channel_id=%s&app_version=%d&query=%s&start=%d&len=%d";

    public static final String HOT_RECOMMEND_BASE_URL =
            "http://www.jungle.com/photobound/content/get_recommendlist?channel_id=%s&app_version=%d&start=%d&len=%d";


    public static String generateImageId(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Md5FileNameGenerator md5Generator = new Md5FileNameGenerator();
        return md5Generator.generate(url);
    }
}
