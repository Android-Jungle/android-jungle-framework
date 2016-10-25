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

package com.jungle.imageloader;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import com.jungle.base.misc.JungleSize;

public interface ImageLoaderEngine {

    void prefetchToDiskCache(Uri uri);

    void prefetchToMemoryCache(Uri uri);

    String getImageResUri(int resId);

    String getImageAssetUri(String assetPath);

    void displayImage(ImageView imageView, Uri uri);

    void displayImageByDrawableResId(ImageView imageView, int drawableResId);

    void displayImageByBitmapResId(ImageView imageView, int bitmapResId);

    void loadImageInternal(
            Uri uri, int maxSize,
            ImageLoaderUtils.ImageLoadListener listener);

    JungleSize getAppropriateSize(View view, int width, int height);
}
