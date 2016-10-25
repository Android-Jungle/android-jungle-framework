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

package com.jungle.base.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import com.jungle.base.app.BaseActivity;
import com.jungle.base.common.OnActivityResultListener;
import com.jungle.base.misc.JungleSize;

import java.io.File;
import java.util.Set;

public class ImageExtUtils {

    private static final int WIFI_IMAGE_MAX_SIZE = 1024;
    private static final int WIFI_IMAGE_QUALITY = 80;

    private static final int MOBILE_NETWORK_IMAGE_MAX_SIZE = 800;
    private static final int MOBILE_IMAGE_QUALITY = 60;

    private static final int MOBILE_G2_NETWORK_IMAGE_MAX_SIZE = 600;
    private static final int MOBILE_G2_IMAGE_QUALITY = 40;


    public static class CompressConfig {
        public int mMaxSize;
        public int mQuality;


        public CompressConfig() {
        }

        public CompressConfig(int maxSize, int quality) {
            update(maxSize, quality);
        }

        public void update(int maxSize, int quality) {
            mMaxSize = maxSize;
            mQuality = quality;
        }
    }


    public interface OnChooseImageListener {
        void onChosen(Uri imageUri);

        void onCanceled();
    }


    public interface OnCaptureImageByCameraListener {
        void onSuccess(String filePath);

        void onCanceled();
    }


    public static void chooseImage(
            final BaseActivity activity,
            final int chooseRequestCode,
            final OnChooseImageListener listener) {

        if (listener != null) {
            activity.addActivityResultListener(new OnActivityResultListener() {
                @Override
                public boolean onActivityResult(
                        BaseActivity activity, int requestCode, int resultCode, Intent data) {

                    if (chooseRequestCode == requestCode) {
                        if (resultCode == Activity.RESULT_OK) {
                            listener.onChosen(data.getData());
                        } else {
                            listener.onCanceled();
                        }
                    }

                    return true;
                }
            });
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, chooseRequestCode);
    }

    public static boolean saveBitmapToPathWithCompress(Bitmap bitmap, String filePath) {
        return saveBitmapToPathWithCompress(bitmap, filePath, null);
    }

    public static CompressConfig getCompressConfig() {
        CompressConfig config = new CompressConfig();
        NetworkUtils.NetType type = NetworkUtils.getNetworkType();

        if (type == NetworkUtils.NetType.Wifi) {
            config.update(WIFI_IMAGE_MAX_SIZE, WIFI_IMAGE_QUALITY);
        } else if (type == NetworkUtils.NetType.G2) {
            config.update(MOBILE_G2_NETWORK_IMAGE_MAX_SIZE, MOBILE_G2_IMAGE_QUALITY);
        } else {
            config.update(MOBILE_NETWORK_IMAGE_MAX_SIZE, MOBILE_IMAGE_QUALITY);
        }

        return config;
    }

    public static boolean saveBitmapToPathWithCompress(
            Bitmap bitmap, String filePath, JungleSize outBitmapSize) {

        CompressConfig config = getCompressConfig();
        return saveBitmapToPathWithCompress(
                bitmap, filePath, config.mMaxSize, config.mQuality, outBitmapSize);
    }

    public static boolean saveBitmapToPathWithCompress(
            Bitmap bitmap, String filePath, int maxSize, int quality,
            JungleSize outBitmapSize) {

        final int bmpWidth = bitmap.getWidth();
        final int bmpHeight = bitmap.getHeight();
        JungleSize size = ImageUtils.getMaxScaleSize(
                bmpWidth, bmpHeight, maxSize);

        if (outBitmapSize != null) {
            outBitmapSize.set(size);
        }

        if (bmpWidth != size.getWidth() && bmpHeight != size.getHeight()) {
            bitmap = ImageUtils.resizeBitmap(bitmap, size.getWidth(), size.getHeight());
        }

        return ImageUtils.saveBitmapToFile(
                bitmap, filePath, Bitmap.CompressFormat.JPEG, quality);
    }

    public static void captureImageByCamera(
            final Activity activity, final String filePath, final int captureRequestCode,
            final OnCaptureImageByCameraListener listener) {

        MiscUtils.requestRuntimePermission(activity, Manifest.permission.CAMERA,
                new MiscUtils.OnPermissionRequestListener() {
                    @Override
                    public void onResult(Set<String> grantedPermissions) {
                        if (grantedPermissions != null
                                && grantedPermissions.contains(Manifest.permission.CAMERA)) {

                            File file = new File(filePath);
                            Uri uri = Uri.fromFile(file);

                            if (activity instanceof BaseActivity && listener != null) {
                                BaseActivity baseActivity = (BaseActivity) activity;
                                baseActivity.addActivityResultListener(
                                        new OnActivityResultListener() {
                                            @Override
                                            public boolean onActivityResult(
                                                    BaseActivity activity, int requestCode,
                                                    int resultCode, Intent data) {

                                                if (requestCode == captureRequestCode) {
                                                    if (resultCode == Activity.RESULT_OK) {
                                                        listener.onSuccess(filePath);
                                                    } else {
                                                        listener.onCanceled();
                                                    }
                                                }

                                                return true;
                                            }
                                        });
                            }

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            activity.startActivityForResult(intent, captureRequestCode);
                        }
                    }
                });
    }
}
