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

import com.jungle.apps.photos.module.category.data.manager.CategoryManager;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteEntity;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteManager;

public class FavoriteContentProvider extends CategoryContentProvider {

    private FavoriteManager mFavoriteManager;


    public FavoriteContentProvider(
            CategoryManager.CategoryInfo info, FavoriteManager manager) {
        super(info);
        mFavoriteManager = manager;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean isSupportFetchMore() {
        return false;
    }

    @Override
    public boolean isCanFetchMore() {
        return false;
    }

    @Override
    public void fetchMore() {
    }

    @Override
    public int getItemCount() {
        return mFavoriteManager.getFavoritesCount();
    }

    @Override
    public CategoryManager.CategoryItem getItem(int position) {
        FavoriteEntity entity = mFavoriteManager.getFavoriteEntity(position);
        return convertToCategoryItem(entity);
    }

    private CategoryManager.CategoryItem convertToCategoryItem(FavoriteEntity entity) {
        CategoryManager.CategoryItem item = new CategoryManager.CategoryItem();
        item.mId = entity.mId;
        item.mTitle = entity.mTitle;
        item.mThumbUrl = entity.mSrcUrl;
        item.mSrcUrl = entity.mSrcUrl;
        item.mLocalPath = entity.mLocalPath;
        return item;
    }
}
