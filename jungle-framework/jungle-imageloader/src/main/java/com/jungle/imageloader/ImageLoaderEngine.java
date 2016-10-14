/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
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
