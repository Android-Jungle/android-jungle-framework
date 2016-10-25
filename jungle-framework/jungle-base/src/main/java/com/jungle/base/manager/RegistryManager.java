/**
 * Android Jungle framework project.
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
