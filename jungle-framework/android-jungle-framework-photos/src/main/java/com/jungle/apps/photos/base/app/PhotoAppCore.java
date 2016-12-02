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

package com.jungle.apps.photos.base.app;

import android.graphics.Bitmap;
import android.net.Uri;
import com.jungle.apps.photos.base.manager.PhotosEntityManager;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteManager;
import com.jungle.apps.photos.module.favorite.data.tag.FavoriteTagManager;
import com.jungle.base.app.AppCore;
import com.jungle.base.app.BaseApplication;
import com.jungle.base.utils.FileUtils;
import com.jungle.base.utils.NetworkUtils;
import com.jungle.imageloader.ImageLoaderUtils;
import com.jungle.share.ShareHelper;

public class PhotoAppCore extends AppCore {

    public PhotoAppCore(BaseApplication app) {
        super(app);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    protected void startInternal() {
        super.startInternal();

        NetworkUtils.getNetworkType();
        PhotosEntityManager.getInstance();
        FavoriteManager.getInstance();
        FavoriteTagManager.getInstance();
        ImageLoaderUtils.initImageLoader(getApplication(), getImageCacheDirectory());
        ShareHelper.getInstance().setShareImageLoader(new ShareHelper.ShareImageLoader() {
            @Override
            public void loadImage(String url, final Callback callback) {
                ImageLoaderUtils.loadImage(url, new ImageLoaderUtils.ImageLoadListener() {
                    @Override
                    public void onSuccess(Uri uri, Bitmap bitmap) {
                        callback.onSuccess(bitmap);
                    }

                    @Override
                    public void onFailed(int retCode) {
                        callback.onFailed(retCode);
                    }
                });
            }
        });
    }

    protected String getImageCacheDirectory() {
        return FileUtils.getExternalCachePath();
    }
}
