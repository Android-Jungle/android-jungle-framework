package com.jungle.apps.photos.module.category.provider;

import com.jungle.apps.photos.module.category.data.CategoryStrategy;
import com.jungle.apps.photos.module.category.data.manager.CategoryManager;

public class FetchContentProvider extends CategoryContentProvider {

    private String mCategory;
    private String mKey;
    private CategoryManager mCategoryMgr;

    public FetchContentProvider(CategoryManager.CategoryInfo info,
            CategoryManager mgr, String key) {
        super(info);

        mKey = key;
        mCategoryMgr = mgr;
    }

    @Override
    public void clear() {
        mIsFirstFetch = true;
        mCategoryInfo.clear();
    }

    @Override
    public boolean isSupportFetchMore() {
        return true;
    }

    @Override
    public boolean isCanFetchMore() {
        return mIsFirstFetch || !mCategoryInfo.isEnd();
    }

    @Override
    public void fetchMore() {
        if (mIsFirstFetch) {
            mCategoryMgr.fetchCategory(
                    mCategoryInfo.mCategory,
                    mKey, 0, CategoryStrategy.DEFAULT_FETCH_COUNT,
                    mCategoryFetchListener);
        } else {
            mCategoryMgr.fetchCategory(
                    mCategoryInfo.mCategory,
                    mKey, CategoryStrategy.DEFAULT_FETCH_COUNT,
                    mCategoryFetchListener);
        }
    }

    private CategoryManager.OnFetchResultListener mCategoryFetchListener =
            new CategoryManager.OnFetchResultListener() {
                @Override
                public void onSuccess(int fetchedCount,
                        CategoryManager.CategoryInfo info) {
                    mIsFirstFetch = false;
                    notifyContentChanged();
                }

                @Override
                public void onError(CategoryManager.CategoryInfo info) {
                    notifyFetchFailed();
                }
            };
}
