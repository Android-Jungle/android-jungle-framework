package com.jungle.apps.photos.base.app;

import android.database.sqlite.SQLiteDatabase;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteEntity;
import com.jungle.apps.photos.module.favorite.data.tag.FavoriteTagEntity;
import com.jungle.base.app.AppCore;
import com.jungle.base.manager.AppManager;
import com.jungle.simpleorm.supporter.ORMDatabaseListener;
import com.jungle.simpleorm.supporter.ORMSupporter;
import com.jungle.simpleorm.supporter.SimpleORMDatabaseListener;

public class PhotosEntityManager implements AppManager {

    public static PhotosEntityManager getInstance() {
        return AppCore.getInstance().getManager(PhotosEntityManager.class);
    }


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
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onTerminate() {

    }
}
