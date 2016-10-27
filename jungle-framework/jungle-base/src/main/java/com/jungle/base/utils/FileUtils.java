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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import com.jungle.base.R;
import com.jungle.base.app.AppCore;
import com.jungle.base.app.BaseApplication;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {

    public static final int KB = 1024;
    public static final int MB = KB * 1024;
    public static final long GB = MB * 1024;
    public static final long TB = GB * 1024;

    private static final int WRITE_BLOCK_SIZE = 4 * KB;


    public interface CancelableTask {
        boolean needCancel();
    }


    public static boolean isSDCardMounted() {
        return Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState());
    }

    /**
     * =>  /SDCard/
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    /**
     * =>   /sdcard/Android/data/com.xx.xx/cache/tmp/
     * <p/>
     * =>   /data/data/com.xx.xx/cache/tmp/
     */
    public static String getTempPath() {
        String cachePath = FileUtils.getExternalCachePath() + "tmp/";
        FileUtils.createPaths(cachePath);
        return cachePath;
    }

    /**
     * =>   /data/data/com.xx.xx/files/
     */
    public static String getAppFilePath() {
        String appFilePath = BaseApplication.getAppContext().getFilesDir().getPath() + "/";
        FileUtils.createPath(appFilePath);
        return appFilePath;
    }

    /**
     * =>   /data/data/com.xx.xx/databases/
     */
    public static String getDatabasePath() {
        File tempFile = BaseApplication.getAppContext().getDatabasePath("temp");
        String databasePath = tempFile.getParent();
        FileUtils.createPaths(databasePath);
        return databasePath + "/";
    }

    /**
     * =>   /data/data/com.xx.xx/databases/subDbPath/
     */
    public static String getDatabaseSubPath(String subDbPath) {
        String databasePath = FileUtils.getDatabasePath() + "/" + subDbPath + "/";
        FileUtils.createPath(databasePath);
        return databasePath;
    }

    /**
     * =>   /data/data/com.xx.xx/databases/dbName
     */
    public static String getDatabaseFilePath(String dbName) {
        String databasePath = FileUtils.getDatabasePath() + dbName;
        FileUtils.createFile(databasePath);
        return databasePath;
    }

    /**
     * =>   /sdcard/Android/data/com.xx.xx/cache/
     * <p/>
     * =>   /data/data/com.xx.xx/cache/
     */
    public static String getExternalCachePath() {
        String cachePath = null;
        if (isSDCardMounted()) {
            File path = BaseApplication.getAppContext().getExternalCacheDir();
            if (path != null) {
                cachePath = path.getPath() + "/";
            }
        } else {
            cachePath = BaseApplication.getAppContext().getCacheDir().getPath() + "/";
        }

        FileUtils.createPath(cachePath);
        return cachePath;
    }

    public static long getExternalCacheAvailableSize() {
        String path = null;
        if (isSDCardMounted()) {
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            path = Environment.getDataDirectory().getPath();
        }

        if (!TextUtils.isEmpty(path)) {
            StatFs stat = new StatFs(path);
            return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
        }

        return 0;
    }

    public static long getExternalCacheTotalSize() {
        String path = null;
        if (isSDCardMounted()) {
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            path = Environment.getDataDirectory().getPath();
        }

        if (!TextUtils.isEmpty(path)) {
            StatFs stat = new StatFs(path);
            return (long) stat.getBlockCount() * (long) stat.getBlockSize();
        }

        return 0;
    }

    public static void grantUriReadPermission(Uri uri) {
        FileUtils.grantUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    public static void grantUriReadWritePermission(Uri uri) {
        FileUtils.grantUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    public static void grantUriPermission(Uri uri, int permission) {
        try {
            AppCore.getApplicationContext().grantUriPermission(
                    MiscUtils.getPackageName(), uri, permission);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getFileSize(String filePath) {
        return getFileSize(new File(filePath));
    }

    public static long getFileSize(File file) {
        return file != null ? file.length() : 0;
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return file.exists();
    }

    public static boolean createPath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return file.mkdir();
    }

    public static boolean createPath(File file) {
        return file.mkdir();
    }

    public static boolean createPaths(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return file.mkdirs();
    }

    public static boolean createPaths(File file) {
        return file.mkdirs();
    }

    public static boolean createFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return createFile(file);
    }

    public static boolean createFile(File file) {
        if (!file.exists()) {
            try {
                createPaths(file.getParentFile());
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public static boolean copyFile(String srcPath, String dstPath) {
        InputStream inputStream = getFileInputStream(srcPath);
        if (inputStream == null) {
            return false;
        }

        OutputStream outputStream = getFileOutputStream(dstPath);
        if (outputStream == null) {
            closeStream(inputStream);
            return false;
        }

        outputStream = new BufferedOutputStream(outputStream);
        boolean result = writeToStream(inputStream, outputStream);
        closeStream(inputStream);
        closeStream(outputStream);

        return result;
    }

    public static boolean copyUriToFile(Uri srcUri, String destPath) {
        OutputStream outputStream = getFileOutputStream(destPath);
        if (outputStream == null) {
            return false;
        }

        boolean result = false;
        try {
            FileUtils.grantUriReadPermission(srcUri);
            ContentResolver resolver = BaseApplication.getAppContext().getContentResolver();
            InputStream inputStream = resolver.openInputStream(srcUri);

            outputStream = new BufferedOutputStream(outputStream);
            result = writeToStream(inputStream, outputStream);
            FileUtils.closeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FileUtils.closeStream(outputStream);
        return result;
    }

    public static boolean renameFile(String srcFile, String dstFile) {
        File src = new File(srcFile);
        if (!src.exists()) {
            return false;
        }

        File dst = new File(dstFile);
        dst.deleteOnExit();

        return src.renameTo(dst);
    }

    public static void closeStream(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean writeToStream(
            InputStream inputStream,
            OutputStream outputStream) {
        return writeToStream(inputStream, outputStream, null);
    }

    public static boolean writeToStream(
            InputStream inputStream,
            OutputStream outputStream,
            CancelableTask task) {

        try {
            byte[] buffer = new byte[WRITE_BLOCK_SIZE];

            while (true) {
                int length = inputStream.read(buffer);
                if (length <= 0) {
                    break;
                }

                if (task != null && task.needCancel()) {
                    break;
                }

                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean writeToStorage(
            InputStream inputStream, String filePath) {

        return writeToStorage(inputStream, filePath, null);
    }

    public static boolean writeToStorage(
            InputStream inputStream,
            String filePath,
            CancelableTask task) {

        if (inputStream == null) {
            return false;
        }

        boolean bRet = false;
        OutputStream outputStream = getFileOutputStream(filePath);
        if (outputStream != null) {
            outputStream = new BufferedOutputStream(outputStream);
            bRet = writeToStream(inputStream, outputStream, task);
            closeStream(outputStream);
        }

        return bRet;
    }

    public static boolean writeToStorage(String filePath, byte[] data) {
        if (TextUtils.isEmpty(filePath) || data == null) {
            return false;
        }

        boolean bRet = false;
        OutputStream outputStream = getFileOutputStream(filePath);
        if (outputStream != null) {
            outputStream = new BufferedOutputStream(outputStream);
            try {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }

            closeStream(outputStream);
            bRet = true;
        }

        return bRet;
    }

    public static InputStream getFileInputStream(String filePath) {
        if (!isFileExist(filePath)) {
            return null;
        }

        try {
            return new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static OutputStream getFileOutputStream(String filePath) {
        if (!isFileExist(filePath)) {
            createFile(filePath);
        }

        try {
            return new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static long getDirectorySizeInternal(File dir) {
        long size = 0;
        if (dir.isDirectory()) {
            File[] fileLists = dir.listFiles();
            if (fileLists != null && fileLists.length > 0) {
                for (File subDir : fileLists) {
                    size += getDirectorySizeInternal(subDir);
                }
            }
        } else {
            size += dir.length();
        }

        return size;
    }

    public static long getDirectorySize(String dir) {
        return TextUtils.isEmpty(dir) ? 0 : getDirectorySizeInternal(new File(dir));
    }

    public static boolean deleteFile(String filePath) {
        return deleteFile(new File(filePath));
    }

    public static boolean deleteFile(File file) {
        return file.delete();
    }

    private static void cleanDirectoryInternal(File dir) {
        if (!dir.exists()) {
            return;
        }

        if (dir.isDirectory()) {
            File[] fileLists = dir.listFiles();
            if (fileLists != null && fileLists.length > 0) {
                for (File subDir : fileLists) {
                    cleanDirectoryInternal(subDir);
                }
            }
        } else {
            dir.delete();
        }
    }

    public static void cleanDirectory(String dir) {
        if (TextUtils.isEmpty(dir)) {
            return;
        }

        cleanDirectoryInternal(new File(dir));
    }

    public static String getSizeTextDescription(long sizeBytes) {
        String desc = null;
        Context context = AppCore.getApplicationContext();
        if (sizeBytes >= GB) {
            desc = context.getString(R.string.gb_format, (float) sizeBytes / (float) GB);
        } else if (sizeBytes >= MB) {
            desc = context.getString(R.string.mb_format, (float) sizeBytes / (float) MB);
        } else if (sizeBytes >= KB) {
            desc = context.getString(R.string.kb_format, (float) sizeBytes / (float) KB);
        } else {
            desc = context.getString(R.string.bytes_format, (float) sizeBytes);
        }

        return desc;
    }

    public static String getFileUri(String filePath) {
        return Uri.fromFile(new File(filePath)).toString();
    }

    public static boolean isFileUri(String filePath) {
        return !TextUtils.isEmpty(filePath) && filePath.startsWith("file://");
    }

    public static String getFileMD5(String filePath) {
        File file = new File(filePath);
        return getFileMD5(file);
    }

    public static String getFileMD5(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return "";
        }
        try {
            FileInputStream stream = new FileInputStream(file);
            return getStreamMD5(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getStreamMD5(InputStream stream) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        byte buffer[] = new byte[1024];
        int len = 0;
        try {
            while ((len = stream.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(stream);
        }

        BigInteger integer = new BigInteger(1, digest.digest());
        return integer.toString(16);
    }

    public static String getUriMD5(Uri uri) {
        FileUtils.grantUriReadPermission(uri);

        try {
            ContentResolver resolver = BaseApplication.getAppContext().getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            return getStreamMD5(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}
