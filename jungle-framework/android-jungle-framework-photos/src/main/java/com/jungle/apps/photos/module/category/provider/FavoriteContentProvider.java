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
