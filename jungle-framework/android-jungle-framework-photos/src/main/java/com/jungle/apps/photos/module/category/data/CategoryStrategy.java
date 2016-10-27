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
