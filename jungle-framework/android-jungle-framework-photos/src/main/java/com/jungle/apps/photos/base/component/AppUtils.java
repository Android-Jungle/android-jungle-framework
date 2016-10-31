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

package com.jungle.apps.photos.base.component;

import android.content.Context;
import android.os.Environment;
import com.jungle.apps.photos.R;
import com.jungle.base.app.AppCore;
import com.jungle.base.manager.CrashManager;
import com.jungle.base.manager.RegistryManager;
import com.jungle.base.manager.ThreadManager;
import com.jungle.base.registry.Registry;
import com.jungle.base.utils.FileUtils;
import com.jungle.base.utils.LogUtils;
import com.jungle.base.utils.MiscUtils;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {

    private static final String REGISTRY_DOWNLOAD_IMG_WHEN_FAV =
            "download_img_when_fav";


    /**
     * 获取图片缓存位置.
     */
    public static String getImageCacheDirectory() {
        return FileUtils.getExternalCachePath() + "imgcache/";
    }

    /**
     * 获取收藏图片位置.
     */
    public static String getFavouriteDirectory() {
        return getGlobalFavouriteDirectory();
    }

    public static String getFavouritePicFile(String id) {
        return getFavouriteDirectory() + id + ".jpg";
    }

    /**
     * 获取全局图片收藏位置.
     */
    public static String getGlobalFavouriteDirectory() {
        String picturePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath();
        picturePath += "/" + MiscUtils.getAppName() + "/";
        FileUtils.createPaths(picturePath);

        return picturePath;
    }

    /**
     * 获取用户收藏图片位置.
     */
    public static String getUserFavouriteDirectory() {
        String picturePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath();
        picturePath += "/" + MiscUtils.getAppName() + "/";
        FileUtils.createPaths(picturePath);

        return picturePath;
    }

    /**
     * 获取壁纸下载路径.
     */
    public static String getWallPaperDirectory() {
        String path = null;

        if (FileUtils.isSDCardMounted()) {
            path = FileUtils.getTempPath();
        } else {
            Context context = AppCore.getApplicationContext();
            path = context.getFilesDir().getParent() + "/";
        }

        path += "wallpaper/";
        FileUtils.createPaths(path);
        return path;
    }

    /**
     * 后台清理缓存.
     */
    public static void cleanAppCacheInBackground(final Runnable resultRunnable) {
        ThreadManager.getInstance().getFileHandler().post(new Runnable() {
            @Override
            public void run() {
                cleanAppCache();

                if (resultRunnable != null) {
                    ThreadManager.getInstance().executeOnUIHandler(resultRunnable);
                }
            }
        });
    }

    /**
     * 清理缓存.
     */
    public static void cleanAppCache() {
        List<String> cacheDirList = getCanCleanCacheDirectoryList();
        for (String dir : cacheDirList) {
            FileUtils.cleanDirectory(dir);
        }
    }

    /**
     * 获取可以当作缓存清理的目录列表.
     */
    public static List<String> getCanCleanCacheDirectoryList() {
        List<String> dirList = new ArrayList<String>();
        dirList.add(getImageCacheDirectory());
        dirList.add(getWallPaperDirectory());
        dirList.add(CrashManager.getDefaultDumpPath());
        dirList.add(LogUtils.getLogPath());

        return dirList;
    }

    /**
     * 获取 App 缓存文件大小.
     */
    public static long getCacheSizeBytes() {
        long cacheBytesSize = 0;

        List<String> cacheDirList = getCanCleanCacheDirectoryList();
        for (String dir : cacheDirList) {
            cacheBytesSize += FileUtils.getDirectorySize(dir);
        }

        return cacheBytesSize;
    }

    /**
     * 获取缓存大小文本描述.
     */
    public static String getCacheTextDescription() {
        return FileUtils.getSizeTextDescription(getCacheSizeBytes());
    }

    public static String getMainCategory() {
        return AppCore.getApplicationContext().getString(
                R.string.photobound_main_category);
    }

    private static Registry getAppositeRegistry() {
        return RegistryManager.getInstance().getGlobalRegistry();
    }

    public static void saveDownloadWhenFavPicBtn(boolean download) {
        getAppositeRegistry().writeValue(REGISTRY_DOWNLOAD_IMG_WHEN_FAV, download);
    }

    public static boolean isDownloadWhenFavPic() {
        return getAppositeRegistry().readBooleanValue(
                REGISTRY_DOWNLOAD_IMG_WHEN_FAV, true);
    }
}
