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

import android.graphics.Bitmap;
import android.net.Uri;
import com.jungle.base.app.AppCore;
import com.jungle.base.manager.AppManager;
import com.jungle.imageloader.ImageLoaderUtils;
import com.jungle.share.ShareHelper;

public class ShareManager extends ShareHelper implements AppManager {

    public static ShareManager getInstance() {
        return AppCore.getInstance().getManager(ShareManager.class);
    }


    @Override
    public void onCreate() {
        setShareImageLoader(new ShareHelper.ShareImageLoader() {
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

    @Override
    public void onTerminate() {
        destroy();
    }
}
