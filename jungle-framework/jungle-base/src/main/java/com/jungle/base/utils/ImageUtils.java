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

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import com.jungle.base.app.BaseApplication;
import com.jungle.base.misc.JungleSize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public interface OnHandleImageListener {
        void onHandled(Bitmap bitmap);
    }


    public static BitmapFactory.Options getResourceOptions(
            Resources res, int resId) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        return options;
    }

    public static BitmapFactory.Options getResourceOptions(String filePath) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (!TextUtils.isEmpty(filePath)) {
            BitmapFactory.decodeFile(filePath, options);
        }

        return options;
    }

    public static BitmapFactory.Options getResourceOptions(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        if (uri != null) {
            try {
                ContentResolver resolver = BaseApplication.getAppContext().getContentResolver();
                InputStream stream = resolver.openInputStream(uri);
                BitmapFactory.decodeStream(stream, new Rect(), options);

                FileUtils.closeStream(stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return options;
    }

    public static JungleSize getImageSize(Resources res, int resId) {
        BitmapFactory.Options options = getResourceOptions(res, resId);
        return new JungleSize(options.outWidth, options.outHeight);
    }

    public static JungleSize getImageSize(String filePath) {
        BitmapFactory.Options options = getResourceOptions(filePath);
        return new JungleSize(options.outWidth, options.outHeight);
    }

    public static JungleSize getImageSize(Uri uri) {
        BitmapFactory.Options options = getResourceOptions(uri);
        return new JungleSize(options.outWidth, options.outHeight);
    }

    public static int calcInSampleSize(
            BitmapFactory.Options options, int requestWidth, int requestHeight) {

        return calcInSampleSize(
                options.outWidth, options.outHeight, requestWidth, requestHeight);
    }

    public static int calcInSampleSize(
            int width, int height, int requestWidth, int requestHeight) {

        int inSampleSize = 1;
        if (requestWidth <= 0 || requestHeight <= 0) {
            return inSampleSize;
        }

        if (width > requestWidth || height > requestHeight) {
            int widthRatio = Math.round((float) width / (float) requestWidth);
            int heightRatio = Math.round((float) height / (float) requestHeight);

            inSampleSize = Math.min(widthRatio, heightRatio);
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(
            Resources res, int resId) {
        return decodeSampledBitmapFromResource(res, resId, 0, 0);
    }

    public static Bitmap decodeSampledBitmapFromResource(
            Resources res, int resId, int requestWidth, int requestHeight) {

        BitmapFactory.Options options = getResourceOptions(res, resId);
        options.inSampleSize = calcInSampleSize(
                options, requestWidth, requestHeight);
        options.inJustDecodeBounds = false;

        try {
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (OutOfMemoryError | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap decodeSampledBitmapFromUri(Uri uri) {
        return decodeSampledBitmapFromUri(uri, 0, 0);
    }

    public static Bitmap decodeSampledBitmapFromUri(
            Uri uri, int requestWidth, int requestHeight) {

        if (uri == null) {
            return null;
        }

        FileUtils.grantUriReadPermission(uri);
        BitmapFactory.Options options = getResourceOptions(uri);
        options.inSampleSize = calcInSampleSize(
                options, requestWidth, requestHeight);
        options.inJustDecodeBounds = false;

        ContentResolver resolver = BaseApplication.getAppContext().getContentResolver();

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(uri), new Rect(), options);
            if (bitmap != null) {
                int degree = ImageUtils.getImageRotateDegree(uri);
                if (degree != 0) {
                    Bitmap rotatedBitmap = ImageUtils.rotateBitmap(bitmap, degree);
                    bitmap.recycle();
                    return rotatedBitmap;
                }
            }

            return bitmap;
        } catch (FileNotFoundException | OutOfMemoryError e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath) {
        return decodeSampledBitmapFromFile(filePath, 0, 0);
    }

    public static Bitmap decodeSampledBitmapFromFile(
            String filePath, int requestWidth, int requestHeight) {

        BitmapFactory.Options options = getResourceOptions(filePath);
        options.inSampleSize = calcInSampleSize(
                options, requestWidth, requestHeight);
        options.inJustDecodeBounds = false;

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            if (bitmap != null) {
                int degree = ImageUtils.getImageRotateDegree(filePath);
                if (degree != 0) {
                    Bitmap rotatedBitmap = ImageUtils.rotateBitmap(bitmap, degree);
                    bitmap.recycle();
                    return rotatedBitmap;
                }
            }

            return bitmap;
        } catch (OutOfMemoryError | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap generateMaskBitmap(Bitmap src, Bitmap mask) {
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap bkg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bkg);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // draw mask.
        Rect dstRc = new Rect(0, 0, width, height);
        Rect maskRc = new Rect(0, 0, mask.getWidth(), mask.getHeight());
        canvas.drawBitmap(mask, maskRc, dstRc, paint);

        // draw src.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, dstRc, dstRc, paint);

        return bkg;
    }

    public static Bitmap generateMaskBitmap(Bitmap src, Drawable maskDrawable) {
        return generateMaskBitmap(src, maskDrawable, src.getWidth(), src.getHeight());
    }

    public static Bitmap generateMaskBitmap(
            Bitmap src, Drawable maskDrawable, final int width, final int height) {

        int bitmapWidth = src.getWidth();
        int bitmapHeight = src.getHeight();

        if (bitmapWidth == 0 || bitmapHeight == 0 || width == 0 || height == 0) {
            return null;
        }

        float ratio = (float) width / (float) height;
        float bitmapRatio = (float) bitmapWidth / (float) bitmapHeight;
        Rect srcRc = new Rect();
        if (bitmapRatio >= ratio) {
            // use src-width as base.
            int mappedWidth = (int) (ratio * bitmapHeight);
            srcRc.top = 0;
            srcRc.bottom = bitmapHeight;
            srcRc.left = Math.abs((bitmapWidth - mappedWidth) / 2);
            srcRc.right = srcRc.left + mappedWidth;
        } else {
            // use src-height as base.
            int mappedHeight = (int) (bitmapWidth / ratio);
            srcRc.left = 0;
            srcRc.right = bitmapWidth;
            srcRc.top = Math.abs((bitmapHeight - mappedHeight) / 2);
            srcRc.bottom = srcRc.top + mappedHeight;
        }

        Bitmap bkg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bkg);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // draw mask.
        Rect dstRc = new Rect(0, 0, width, height);
        maskDrawable.setBounds(dstRc);
        maskDrawable.draw(canvas);

        // draw src.
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, srcRc, dstRc, paint);

        return bkg;
    }

    public static Bitmap generateMaskBitmap(Resources res, Bitmap src, int maskResId) {
        Bitmap mask = ImageUtils.decodeSampledBitmapFromResource(res, maskResId, 0, 0);
        return generateMaskBitmap(src, mask);
    }

    public static Bitmap generateMaskDrawableBitmap(
            Resources res, Bitmap src, int maskResId) {
        return generateMaskBitmap(src, res.getDrawable(maskResId));
    }

    public static Bitmap generateMaskDrawable(
            Drawable src, Drawable maskDrawable,
            int width, int height) {

        if (width <= 0 || height <= 0) {
            return null;
        }

        if (src instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) src).getBitmap();
            return generateMaskBitmap(bitmap, maskDrawable, width, height);
        } else {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            src.setBounds(new Rect(0, 0, width, height));
            src.draw(canvas);

            Bitmap generatedBitmap = generateMaskBitmap(bitmap, maskDrawable);
            bitmap.recycle();

            return generatedBitmap;
        }
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, String filePath) {
        return saveBitmapToFile(bitmap, filePath, Bitmap.CompressFormat.JPEG, 100);
    }

    public static boolean saveBitmapToFile(
            Bitmap bitmap, String filePath,
            Bitmap.CompressFormat format, int quality) {

        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(format, quality, out);

            out.flush();
            out.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static JungleSize getMaxScaleSize(Bitmap bitmap, int maxSize) {
        return getMaxScaleSize(bitmap.getWidth(), bitmap.getHeight(), maxSize);
    }

    public static JungleSize getMaxScaleSize(int realWidth, int realHeight, int maxSize) {
        if (maxSize <= 0) {
            return new JungleSize(realWidth, realHeight);
        }

        float ratio = realHeight != 0 ? (float) realWidth / (float) realHeight : 1;
        if (ratio == 0) {
            ratio = 1;
        }

        int width = realWidth;
        int height = realHeight;
        if (realWidth > realHeight) {
            if (realWidth > maxSize) {
                width = maxSize;
                height = (int) (width / ratio);
            }
        } else {
            if (realHeight > maxSize) {
                height = maxSize;
                width = (int) (height * ratio);
            }
        }

        return new JungleSize(Math.max(width, 1), Math.max(height, 1));
    }

    public static Drawable generateRoundDrawable(float radius) {
        return ImageUtils.generateRoundDrawable(radius, radius, radius, radius);
    }

    public static Drawable generateRoundDrawable(
            float leftRadius, float topRadius, float rightRadius, float bottomRadius) {

        ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(new float[]{
                leftRadius, topRadius, topRadius, rightRadius,
                rightRadius, bottomRadius, bottomRadius, leftRadius
        }, null, null));

        Paint paint = shape.getPaint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        return shape;
    }

    public static int getImageRotateDegree(Uri uri) {
        if (uri == null) {
            return 0;
        }

        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_FILE.compareToIgnoreCase(scheme) == 0) {
            return getImageRotateDegree(uri.getPath());
        }

        ContentResolver resolver = BaseApplication.getAppContext().getContentResolver();
        Cursor cursor = resolver.query(
                uri, new String[]{MediaStore.Images.Media.ORIENTATION},
                null, null, null);

        int degree = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                degree = cursor.getInt(
                        cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
            }

            cursor.close();
        }

        return degree;
    }

    public static int getImageRotateDegree(String filePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return degree;
    }

    public static String getImagePublicStoragePath() {
        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath();

        if (!path.endsWith("/") && !path.endsWith("\\")) {
            path += "/";
        }

        path += MiscUtils.getAppName() + "/";
        FileUtils.createPath(path);

        return path;
    }
}
