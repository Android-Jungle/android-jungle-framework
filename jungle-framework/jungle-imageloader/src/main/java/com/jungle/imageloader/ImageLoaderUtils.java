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
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jungle.base.manager.ThreadManager;
import com.jungle.base.misc.JungleSize;
import com.jungle.base.utils.ImageUtils;
import com.jungle.base.utils.MiscUtils;

import java.io.File;
import java.util.concurrent.Executor;

public class ImageLoaderUtils {

    public static final int ERROR_LOAD_FAILED = -1;
    public static final int ERROR_LOAD_EMPTY_IMAGE = -2;
    public static final int ERROR_HANDLE_IMAGE = -3;


    public interface ImageLoadListener {
        void onSuccess(Uri uri, Bitmap bitmap);

        void onFailed(int retCode);
    }


    public static String getImageResUri(int resId) {
        return String.format("%s://%s/%d",
                UriUtil.LOCAL_RESOURCE_SCHEME,
                MiscUtils.getPackageName(),
                resId);
    }

    public static String getImageAssetUri(String assetPath) {
        return String.format("%s://%s/%s",
                UriUtil.LOCAL_ASSET_SCHEME,
                MiscUtils.getPackageName(),
                assetPath);
    }

    public static String getImageFileUri(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        return Uri.fromFile(new File(filePath)).toString();
    }

    public static void displayImageByDrawableResId(
            ImageView imageView, int drawableResId) {

        if (imageView instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) imageView;
            draweeView.setController(null);
            draweeView.getHierarchy().setPlaceholderImage(drawableResId);
        } else {
            imageView.setImageResource(drawableResId);
        }
    }

    public static void displayImageByBitmapResId(
            ImageView imageView, int bitmapResId) {

        if (bitmapResId == 0) {
            return;
        }

        if (imageView instanceof DraweeView) {
            displayImage(imageView, getImageResUri(bitmapResId));
        } else {
            imageView.setImageResource(bitmapResId);
        }
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
        displayImage(imageView, uri, null);
    }

    public static void displayImage(
            final ImageView imageView, final Uri uri, final ControllerListener<ImageInfo> listener) {

        if (uri == null || imageView == null) {
            return;
        }

        if (imageView instanceof DraweeView) {
            DraweeView draweeView = (DraweeView) imageView;
            DraweeController controller = createDraweeController(draweeView, uri, listener);
            draweeView.setController(controller);
        } else {
            JungleSize size = getAppropriateSize(imageView);
            int maxSize = Math.max(size.mWidth, size.mHeight);

            ImageLoaderUtils.loadImageInternal(uri, maxSize, new ImageLoadListener() {
                @Override
                public void onSuccess(Uri uri, final Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                    if (listener != null) {
                        ImageInfo info = new ImageInfo() {
                            @Override
                            public int getWidth() {
                                return bitmap.getWidth();
                            }

                            @Override
                            public int getHeight() {
                                return bitmap.getHeight();
                            }

                            @Override
                            public QualityInfo getQualityInfo() {
                                return null;
                            }
                        };

                        listener.onFinalImageSet(uri.toString(), info, null);
                    }
                }

                @Override
                public void onFailed(int retCode) {
                    if (listener != null) {
                        listener.onFailure(uri.toString(), null);
                    }
                }
            });
        }
    }

    private static JungleSize getAppropriateSize(View view) {
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

    private static DraweeController createDraweeController(
            DraweeView draweeView, Uri uri, ControllerListener<ImageInfo> listener) {

        JungleSize size = getAppropriateSize(draweeView);
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(size.mWidth, size.mHeight))
                .setAutoRotateEnabled(true)
                .build();

        return Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setImageRequest(request)
                .setControllerListener(listener)
                .setAutoPlayAnimations(true)
                .setOldController(draweeView.getController())
                .build();
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
        ImageLoaderUtils.loadImageInternal(uri, maxSize, listener);
    }

    public static void loadOrigImage(Uri uri, ImageLoadListener listener) {
        ImageLoaderUtils.loadImageInternal(uri, 0, listener);
    }

    public static void prefetchToDiskCache(Uri uri) {
        ImageRequestBuilder builder = ImageRequestBuilder
                .newBuilderWithSource(uri);
        ImagePipeline pipeline = Fresco.getImagePipeline();
        pipeline.prefetchToDiskCache(builder.build(), null);
    }

    public static void prefetchToMemoryCache(Uri uri) {
        ImageRequestBuilder builder = ImageRequestBuilder
                .newBuilderWithSource(uri);
        ImagePipeline pipeline = Fresco.getImagePipeline();
        pipeline.prefetchToBitmapCache(builder.build(), null);
    }

    /**
     * Internal helper method for load Bitmap.
     *
     * @param maxSize 0 or less than 0 if want original bitmap.
     */
    private static void loadImageInternal(
            final Uri uri,
            final int maxSize,
            final ImageLoadListener listener) {

        if (uri == null) {
            listener.onFailed(-1);
            return;
        }

        ImageRequestBuilder builder = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setAutoRotateEnabled(true);
        if (maxSize > 0) {
            builder.setResizeOptions(new ResizeOptions(maxSize, maxSize));
        }

        ImagePipeline pipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> data =
                pipeline.fetchDecodedImage(builder.build(), null);

        Executor executor = new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                ThreadManager.getInstance().postOnUIHandler(command);
            }
        };

        data.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                if (bitmap == null) {
                    listener.onFailed(ERROR_LOAD_EMPTY_IMAGE);
                    return;
                }

                try {
                    if (maxSize <= 0) {
                        listener.onSuccess(uri, Bitmap.createBitmap(bitmap));
                    } else {
                        JungleSize size = ImageUtils.getMaxScaleSize(
                                bitmap.getWidth(), bitmap.getHeight(), maxSize);
                        listener.onSuccess(uri, Bitmap.createScaledBitmap(
                                bitmap, size.getWidth(), size.getHeight(), true));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailed(ERROR_HANDLE_IMAGE);
                }
            }

            @Override
            protected void onFailureImpl(
                    DataSource<CloseableReference<CloseableImage>> dataSource) {
                listener.onFailed(ERROR_LOAD_FAILED);
            }
        }, executor);
    }
}
