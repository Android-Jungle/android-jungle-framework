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

package com.jungle.apps.photos.module.favorite.data.tag;

import android.text.TextUtils;
import com.jungle.apps.photos.base.manager.PhotosEntityManager;
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

        if (getORMSupporter().replace(entity)) {
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
    public void fetchFavoriteTags() {
    }
}
