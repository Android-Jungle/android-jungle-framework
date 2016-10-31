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

package com.jungle.apps.photos.module.favorite.data.pic;

import com.jungle.apps.photos.base.manager.PhotosEntityManager;
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
