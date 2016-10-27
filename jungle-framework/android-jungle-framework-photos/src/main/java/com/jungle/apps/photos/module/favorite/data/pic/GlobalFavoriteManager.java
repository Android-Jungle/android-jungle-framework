package com.jungle.apps.photos.module.favorite.data.pic;

import com.jungle.apps.photos.base.app.PhotosEntityManager;
import com.jungle.simpleorm.supporter.ORMSupporter;

public class GlobalFavoriteManager extends FavoriteManager {

    @Override
    public void synchronizeList() {
    }

    @Override
    protected ORMSupporter getORMSupporter() {
        return PhotosEntityManager.getInstance().getGlobalORMSupporter();
    }

    @Override
    public void doAddFavorite(FavoriteEntity entity) {
        synchronizeEntity(entity);
        notifyAddFavorite(entity.mId);
    }

    @Override
    public boolean doCancelFavorite(String id) {
        boolean result = doCancelFavoriteInternal(id);
        notifyCancelFavorite(id);
        return result;
    }
}
