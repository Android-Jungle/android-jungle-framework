package com.jungle.apps.photos.module.favorite.data.tag;

import android.text.TextUtils;
import com.jungle.apps.photos.base.app.PhotosEntityManager;
import com.jungle.simpleorm.supporter.ORMSupporter;

import java.util.ArrayList;
import java.util.List;

public class GlobalFavoriteTagManager extends FavoriteTagManager {

    @Override
    protected ORMSupporter getORMSupporter() {
        return PhotosEntityManager.getInstance().getGlobalORMSupporter();
    }

    @Override
    protected List<String> initFavoriteList() {
        List<FavoriteTagEntity> entityList = getORMSupporter().query(FavoriteTagEntity.class);
        List<String> tagsList = new ArrayList<>();
        if (entityList != null) {
            for (FavoriteTagEntity entity : entityList) {
                tagsList.add(entity.mTag);
            }
        }

        return tagsList;
    }

    @Override
    public boolean addFavoriteTag(String tag) {
        if (TextUtils.isEmpty(tag) || isTagFavorited(tag)) {
            return false;
        }

        FavoriteTagEntity entity = new FavoriteTagEntity();
        entity.mTag = tag;

        if (getORMSupporter().update(entity)) {
            doAddFavoriteTag(tag);
            return true;
        }

        return false;
    }

    @Override
    public boolean removeFavoriteTag(String tag) {
        FavoriteTagEntity entity = new FavoriteTagEntity();
        entity.mTag = tag;
        getORMSupporter().remove(entity);

        doRemoveFavoriteTag(tag);
        return true;
    }

    @Override
    public void fetchFavoritedTags() {
    }
}
