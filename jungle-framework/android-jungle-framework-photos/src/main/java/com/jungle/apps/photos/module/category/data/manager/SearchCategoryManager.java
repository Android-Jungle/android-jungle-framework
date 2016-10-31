package com.jungle.apps.photos.module.category.data.manager;

import com.jungle.apps.photos.module.category.data.CategoryStrategy;
import com.jungle.base.app.AppCore;
import com.jungle.base.utils.VersionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchCategoryManager extends CategoryManager {

    private static final String SEARCH_ITEMS_FIELD = "all_items";

    public static SearchCategoryManager getInstance() {
        return AppCore.getInstance().getManager(SearchCategoryManager.class);
    }


    private CategoryParser mSearchCategoryParser =
            new CategoryParser(SEARCH_ITEMS_FIELD);

    public MgrType getCategoryType() {
        return MgrType.ForSearch;
    }

    @Override
    protected String generateFetchUrl(
            String category, String key, int fetchIndex, int count) {
        try {
            String keyEncode = null;
            keyEncode = URLEncoder.encode(key, "UTF-8");

            return String.format(CategoryStrategy.SEARCH_BASE_URL,
                    VersionUtils.getChannelId(),
                    VersionUtils.getAppVersionCode(),
                    keyEncode, fetchIndex, count);
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
        return mSearchCategoryParser;
    }
}
