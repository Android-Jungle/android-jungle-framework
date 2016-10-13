/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/01/06
 */

package com.jungle.imageloader;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.jungle.base.misc.JungleSize;
import com.jungle.base.utils.MiscUtils;

import java.io.File;

public class ImageLoaderUtils {

    public static final int ERROR_LOAD_FAILED = -1;
    public static final int ERROR_LOAD_EMPTY_IMAGE = -2;
    public static final int ERROR_HANDLE_IMAGE = -3;


    private static ImageLoaderEngine mImageLoaderEngine = new FrescoImageLoaderEngine();


    public interface ImageLoadListener {
        void onSuccess(Uri uri, Bitmap bitmap);

        void onFailed(int retCode);
    }

    public static void setImageLoaderEngine(FrescoImageLoaderEngine engine) {
        mImageLoaderEngine = engine;
    }

    public static String getImageResUri(int resId) {
        return mImageLoaderEngine.getImageResUri(resId);
    }

    public static String getImageAssetUri(String assetPath) {
        return mImageLoaderEngine.getImageAssetUri(assetPath);
    }

    public static String getImageFileUri(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        return Uri.fromFile(new File(filePath)).toString();
    }

    public static void displayImageByDrawableResId(
            ImageView imageView, int drawableResId) {

        mImageLoaderEngine.displayImageByDrawableResId(imageView, drawableResId);
    }

    public static void displayImageByBitmapResId(
            ImageView imageView, int bitmapResId) {

        mImageLoaderEngine.displayImageByBitmapResId(imageView, bitmapResId);
    }

    public static void displayImage(ImageView imageView, String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return;
        }

        try {
            displayImage(imageView, Uri.parse(imgUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayImage(ImageView imageView, Uri uri) {
        mImageLoaderEngine.displayImage(imageView, uri);
    }

    public static void loadImageByResId(int resId, ImageLoadListener listener) {
        if (resId == 0) {
            listener.onFailed(-1);
            return;
        }

        ImageLoaderUtils.loadImage(getImageResUri(resId), listener);
    }

    public static void loadImage(String url, ImageLoadListener listener) {
        if (TextUtils.isEmpty(url)) {
            listener.onFailed(-1);
            return;
        }

        try {
            ImageLoaderUtils.loadImage(Uri.parse(url), listener);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailed(-1);
        }
    }

    public static void loadImage(Uri uri, ImageLoadListener listener) {
        JungleSize size = MiscUtils.getScreenSize();
        int maxSize = Math.max(size.mWidth, size.mHeight);
        mImageLoaderEngine.loadImageInternal(uri, maxSize, listener);
    }

    public static void loadOrigImage(Uri uri, ImageLoadListener listener) {
        mImageLoaderEngine.loadImageInternal(uri, 0, listener);
    }

    public static void prefetchToDiskCache(Uri uri) {
        mImageLoaderEngine.prefetchToDiskCache(uri);
    }

    public static void prefetchToMemoryCache(Uri uri) {
        mImageLoaderEngine.prefetchToMemoryCache(uri);
    }

    public static JungleSize getAppropriateSize(View view) {
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        if (width <= 0 || height <= 0) {
            width = view.getWidth();
            height = view.getHeight();
        }

        JungleSize size = MiscUtils.getScreenSize();
        if (width <= 0 || height <= 0 || width > size.mWidth || height > size.mHeight) {
            width = size.mWidth;
            height = size.mHeight;
        }

        return new JungleSize(width, height);
    }
}
