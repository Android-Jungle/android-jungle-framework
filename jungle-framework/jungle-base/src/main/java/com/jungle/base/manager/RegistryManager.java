/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.base.manager;

import android.database.sqlite.SQLiteDatabase;
import com.jungle.base.app.AppCore;
import com.jungle.base.registry.Registry;
import com.jungle.base.registry.SharedPreferencesRegistry;
import com.jungle.base.utils.FileUtils;

public class RegistryManager implements AppManager {

    private static final String REGISTRY_DATABASE_NAME = "registry.db";


    public static RegistryManager getInstance() {
        return AppCore.getInstance().getManager(RegistryManager.class);
    }


    private Registry mGlobalRegistry;
    private SQLiteDatabase mRegistryDatabase;


    @Override
    public void onCreate() {
        mGlobalRegistry = createRegistryInternal(REGISTRY_DATABASE_NAME);
    }

    @Override
    public void onTerminate() {
        closeRegistry(mGlobalRegistry);
    }

    private void closeRegistry(Registry registry) {
        if (registry != null) {
            registry.close();
        }
    }

    public Registry getGlobalRegistry() {
        return mGlobalRegistry;
    }

    protected String getGlobalRegistryPath() {
        return FileUtils.getDatabaseFilePath(REGISTRY_DATABASE_NAME);
    }

    protected Registry createRegistryInternal(String name) {
        return new SharedPreferencesRegistry(name);
    }
}
