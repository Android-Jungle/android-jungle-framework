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

package com.jungle.apps.photos.base.manager;

import android.database.sqlite.SQLiteDatabase;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteEntity;
import com.jungle.apps.photos.module.favorite.data.tag.FavoriteTagEntity;
import com.jungle.base.app.AppCore;
import com.jungle.base.manager.AppManager;
import com.jungle.base.utils.FileUtils;
import com.jungle.simpleorm.supporter.ORMDatabaseListener;
import com.jungle.simpleorm.supporter.ORMSupporter;
import com.jungle.simpleorm.supporter.SQLiteORMSupporter;
import com.jungle.simpleorm.supporter.SimpleORMDatabaseListener;

public class PhotosEntityManager implements AppManager {

    public static PhotosEntityManager getInstance() {
        return AppCore.getInstance().getManager(PhotosEntityManager.class);
    }

    private static final int DB_VERSION = 1;
    private ORMSupporter mGlobalSupporter;


    protected ORMDatabaseListener getDBListener() {
        return new SimpleORMDatabaseListener() {

            @Override
            public void onCreated(ORMSupporter supporter, SQLiteDatabase db) {
                super.onCreated(supporter, db);

                supporter.createTable(FavoriteEntity.class);
                supporter.createTable(FavoriteTagEntity.class);
            }

            @Override
            public void onUpgrade(ORMSupporter supporter, SQLiteDatabase db,
                    int oldVersion, int newVersion) {

                super.onUpgrade(supporter, db, oldVersion, newVersion);
            }
        };
    }

    public ORMSupporter getGlobalORMSupporter() {
        return mGlobalSupporter;
    }

    @Override
    public void onCreate() {
        String filePath = FileUtils.getDatabaseFilePath("entity.db");
        mGlobalSupporter = new SQLiteORMSupporter(
                AppCore.getApplicationContext(), filePath, DB_VERSION, getDBListener());
    }

    @Override
    public void onTerminate() {
        mGlobalSupporter.close();
    }
}
