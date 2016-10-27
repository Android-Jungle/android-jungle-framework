package com.jungle.apps.photos.module.category.data.manager;

import android.text.TextUtils;
import com.jungle.apps.photos.base.component.AppUtils;
import com.jungle.apps.photos.module.category.data.CategoryStrategy;
import com.jungle.base.app.AppCore;
import com.jungle.base.utils.VersionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NormalCategoryManager extends CategoryManager {

    private static final String NORMAL_ITEMS_FIELD = "all_items";

    public static NormalCategoryManager getInstance() {
        return AppCore.getInstance().getManager(NormalCategoryManager.class);
    }


    private CategoryParser mNormalCategoryParser =
            new CategoryParser(NORMAL_ITEMS_FIELD);


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

            return String.format(CategoryStrategy.CATEGORY_BASE_URL,
                    VersionUtils.getChannelId(),
                    VersionUtils.getAppVersionCode(),
                    categoryEncode, tagEncode, fetchIndex, count);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected CategoryInfo createDefaultCategoryInfo(
            String category, String key) {
        return new CategoryInfo(category, key);
    }

    @Override
    protected CategoryParser getCategoryParser() {
        return mNormalCategoryParser;
    }
}
