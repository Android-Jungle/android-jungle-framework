package com.jungle.apps.photos.module.category.data.manager;

import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.data.CategoryStrategy;
import com.jungle.apps.photos.module.category.provider.CategoryProviderManager;
import com.jungle.apps.photos.module.category.provider.FetchContentProvider;
import com.jungle.base.app.AppCore;
import com.jungle.base.utils.VersionUtils;

public class HotRecommendCategoryManager extends CategoryManager {

    private static final String NORMAL_ITEMS_FIELD = "all_items";

    public static HotRecommendCategoryManager getInstance() {
        return AppCore.getInstance().getManager(HotRecommendCategoryManager.class);
    }


    private int mHotRecommendProviderId = 0;
    private CategoryParser mHotRecommendCategoryParser =
            new CategoryParser(NORMAL_ITEMS_FIELD);


    public MgrType getCategoryType() {
        return MgrType.ForHotRecommend;
    }

    @Override
    protected String generateFetchUrl(
            String category, String tag, int fetchIndex, int count) {
        return String.format(CategoryStrategy.HOT_RECOMMEND_BASE_URL,
                VersionUtils.getChannelId(),
                VersionUtils.getAppVersionCode(),
                fetchIndex, count);
    }

    @Override
    protected CategoryInfo createDefaultCategoryInfo(
            String category, String key) {
        return new CategoryInfo(category, key);
    }

    @Override
    protected CategoryParser getCategoryParser() {
        return mHotRecommendCategoryParser;
    }

    @Override
    public int getCategoryProvider(String category, String key) {
        if (mHotRecommendProviderId == 0) {
            category = AppCore.getApplicationContext().getString(R.string.hot_recommend);

            CategoryInfo info = addCategoryInfo(category, category);
            FetchContentProvider provider = new FetchContentProvider(info, this, category);
            provider.SetDefaultCategoryName(category);
            mHotRecommendProviderId = CategoryProviderManager.getInstance().addProvider(provider);
        }

        return mHotRecommendProviderId;
    }

    public int getCategoryProvider() {
        return getCategoryProvider(null, null);
    }
}
