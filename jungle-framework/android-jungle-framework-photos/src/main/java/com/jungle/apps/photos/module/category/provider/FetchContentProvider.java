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
