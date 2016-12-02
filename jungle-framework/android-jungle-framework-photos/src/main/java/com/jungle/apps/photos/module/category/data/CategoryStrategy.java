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

    public static final String BASE_URL =
            "http://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&is=&fp=result&queryWord=%s&cl=2&lm=-1&ie=utf-8&oe=utf-8&adpicid=&st=-1&z=0&ic=0&word=%s&pn=%d&rn=%d";


    public static String getCategoryUrl(String category, String tag, int index, int count) {
        String key = String.format("%s+%s", category, tag);
        return getSearchUrl(key, index, count);
    }

    public static String getSearchUrl(String searchKey, int index, int count) {
        return String.format(BASE_URL, searchKey, (index + 1) * count, count);
    }


    public static String generateImageId(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Md5FileNameGenerator md5Generator = new Md5FileNameGenerator();
        return md5Generator.generate(url);
    }
}
