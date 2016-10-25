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

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import java.util.concurrent.Executor;

public class FrescoImageLoaderEngine implements ImageLoaderEngine {

    @Override
    public void prefetchToDiskCache(Uri uri) {
        ImageRequestBuilder builder = ImageRequestBuilder
                .newBuilderWithSource(uri);
        ImagePipeline pipeline = Fresco.getImagePipeline();
        pipeline.prefetchToDiskCache(builder.build(), null);
    }

    @Override
    public void prefetchToMemoryCache(Uri uri) {
        ImageRequestBuilder builder = ImageRequestBuilder
                .newBuilderWithSource(uri);
        ImagePipeline pipeline = Fresco.getImagePipeline();
        pipeline.prefetchToBitmapCache(builder.build(), null);
    }

    @Override
    public String getImageResUri(int resId) {
        return String.format("%s://%s/%d",
                UriUtil.LOCAL_RESOURCE_SCHEME,
                MiscUtils.getPackageName(),
                resId);
    }

    @Override
    public String getImageAssetUri(String assetPath) {
        return String.format("%s://%s/%s",
                UriUtil.LOCAL_ASSET_SCHEME,
                MiscUtils.getPackageName(),
                assetPath);
    }

    @Override
    public void displayImageByDrawableResId(ImageView imageView, int drawableResId) {
        if (imageView instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) imageView;
            draweeView.setController(null);
            draweeView.getHierarchy().setPlaceholderImage(drawableResId);
        } else {
            imageView.setImageResource(drawableResId);
        }
    }

    @Override
    public void displayImageByBitmapResId(ImageView imageView, int bitmapResId) {
        if (bitmapResId == 0) {
            return;
        }

        if (imageView instanceof DraweeView) {
            displayImage(imageView, getImageResUri(bitmapResId));
        } else {
            imageView.setImageResource(bitmapResId);
        }
    }

    private void displayImage(ImageView imageView, String uri) {
        try {
            displayImage(imageView, Uri.parse(uri), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayImage(ImageView imageView, Uri uri) {
        displayImage(imageView, uri, null);
    }

    private void displayImage(
            final ImageView imageView, final Uri uri,
            final ControllerListener<ImageInfo> listener) {

        if (uri == null || imageView == null) {
            return;
        }

        if (imageView instanceof DraweeView) {
            DraweeView draweeView = (DraweeView) imageView;
            DraweeController controller = createDraweeController(draweeView, uri, listener);
            draweeView.setController(controller);
        } else {
            JungleSize size = ImageLoaderUtils.getAppropriateSize(imageView);
            int maxSize = Math.max(size.mWidth, size.mHeight);

            loadImageInternal(uri, maxSize, new ImageLoaderUtils.ImageLoadListener() {
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

    /**
     * Internal helper method for load Bitmap.
     *
     * @param maxSize 0 or less than 0 if want original bitmap.
     */
    @Override
    public void loadImageInternal(
            final Uri uri,
            final int maxSize,
            final ImageLoaderUtils.ImageLoadListener listener) {

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
                    listener.onFailed(ImageLoaderUtils.ERROR_LOAD_EMPTY_IMAGE);
                    return;
                }

                final int width = bitmap.getWidth();
                final int height = bitmap.getHeight();
                try {
                    if (maxSize <= 0 || (width <= maxSize && height <= maxSize)) {
                        listener.onSuccess(uri, Bitmap.createBitmap(bitmap));
                    } else {
                        JungleSize size = ImageUtils.getMaxScaleSize(
                                bitmap.getWidth(), bitmap.getHeight(), maxSize);
                        listener.onSuccess(uri, Bitmap.createScaledBitmap(
                                bitmap, size.getWidth(), size.getHeight(), true));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    listener.onSuccess(uri, bitmap);
                }
            }

            @Override
            protected void onFailureImpl(
                    DataSource<CloseableReference<CloseableImage>> dataSource) {
                listener.onFailed(ImageLoaderUtils.ERROR_LOAD_FAILED);
            }
        }, executor);
    }

    private DraweeController createDraweeController(
            DraweeView draweeView, Uri uri, ControllerListener<ImageInfo> listener) {

        JungleSize size = ImageLoaderUtils.getAppropriateSize(draweeView);
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

    @Override
    public JungleSize getAppropriateSize(View view, int width, int height) {
        if (!(view instanceof DraweeView)) {
            return null;
        }

        DraweeView draweeView = (DraweeView) view;
        float ratio = draweeView.getAspectRatio();
        if (ratio > 0) {
            if (width > 0) {
                height = (int) (width / ratio);
            } else if (height > 0) {
                width = (int) (height * ratio);
            }
        }

        return new JungleSize(width, height);
    }
}
