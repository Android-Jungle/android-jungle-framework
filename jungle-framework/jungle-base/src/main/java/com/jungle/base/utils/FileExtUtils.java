/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;
import com.jungle.base.R;
import com.jungle.base.app.BaseActivity;
import com.jungle.base.app.BaseAppCore;
import com.jungle.base.common.OnActivityResultListener;
import com.jungle.base.manager.ThreadManager;

import java.io.File;

public class FileExtUtils {

    public static enum FileType {
        Unknown,
        Audio,
        Video,
        Picture,
        Application,
        Ppt,
        Doc,
        Xls,
        Pdf,
        Txt,
        Xml,
        Zip,
    }


    private static SparseArray<FileType> mFileTypeSetMap = new SparseArray<>();


    public interface OnCleanDirectoryAsyncCallback {
        void onDirectoryCleaned();
    }

    public interface OnGetDirectorySizeCallback {
        void onGetDirectorySize(long dirBytesSize);
    }


    public interface OnChooseFileListener {
        void onChosen(Uri fileUri);

        void onCanceled();
    }


    public static void chooseFile(
            final BaseActivity activity,
            final int chooseRequestCode,
            final OnChooseFileListener listener) {

        chooseFile(activity, chooseRequestCode, null, listener);
    }

    public static void chooseFile(
            final BaseActivity activity,
            final int chooseRequestCode,
            String chooseTitle,
            final OnChooseFileListener listener) {

        if (listener != null) {
            activity.addActivityResultListener(new OnActivityResultListener() {
                @Override
                public boolean onActivityResult(BaseActivity activity,
                        int requestCode, int resultCode, Intent data) {

                    if (chooseRequestCode == requestCode) {
                        if (resultCode == Activity.RESULT_OK) {
                            listener.onChosen(data.getData());
                        } else {
                            listener.onCanceled();
                        }

                        return true;
                    }

                    return false;
                }
            });
        }

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (TextUtils.isEmpty(chooseTitle)) {
            chooseTitle = BaseAppCore.getApplicationContext().getString(R.string.choose_file);
        }

        Intent chooseIntent = Intent.createChooser(intent, chooseTitle);
        activity.startActivityForResult(chooseIntent, chooseRequestCode);
    }

    public static String getPathFromUri(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        return getPathFromUri(Uri.parse(uri));
    }

    public static String getPathFromUri(Uri uri) {
        if (uri != null && !TextUtils.isEmpty(uri.toString())) {
            String scheme = uri.getScheme();
            if (ContentResolver.SCHEME_FILE.compareToIgnoreCase(scheme) == 0) {
                return uri.getPath();
            }
        }
        {
            // scheme 为 content:// 时在某些机型上有兼容性问题,故不做处理.
        }

        return null;
    }

    public static boolean isUriExist(Uri uri) {
        if (uri == null) {
            return false;
        }

        String scheme = uri.getScheme();
        if (scheme == null) {
            return false;
        }

        FileUtils.grantUriReadPermission(uri);
        boolean exist = false;
        if (ContentResolver.SCHEME_FILE.compareToIgnoreCase(scheme) == 0) {
            exist = FileUtils.isFileExist(uri.getPath());
        } else {
            Cursor cursor = BaseAppCore.getApplicationContext().getContentResolver().query(
                    uri, new String[]{MediaStore.Files.FileColumns.SIZE},
                    null, null, null);

            if (cursor != null) {
                exist = cursor.moveToFirst()
                        && cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE) > -1;
                cursor.close();
            }
        }

        return exist;
    }

    public static boolean isUriExist(String uri) {
        return !TextUtils.isEmpty(uri) && isUriExist(Uri.parse(uri));
    }

    public static long getUriSizeBytes(String uri) {
        if (!TextUtils.isEmpty(uri)) {
            return getUriSizeBytes(Uri.parse(uri));
        }

        return 0;
    }

    public static long getUriSizeBytes(Uri uri) {
        if (uri == null) {
            return 0;
        }

        String scheme = uri.getScheme();
        if (scheme == null) {
            return 0;
        }

        FileUtils.grantUriReadPermission(uri);

        long size = 0;
        if (ContentResolver.SCHEME_FILE.compareToIgnoreCase(scheme) == 0) {
            size = new File(uri.getPath()).length();
        } else {
            Cursor cursor = BaseAppCore.getApplicationContext().getContentResolver().query(
                    uri, new String[]{MediaStore.Files.FileColumns.SIZE},
                    null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                    if (columnIndex > -1) {
                        try {
                            size = cursor.getLong(columnIndex);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                cursor.close();
            }
        }

        return size;
    }

    public static String getNameFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }

        String scheme = uri.getScheme();
        if (scheme == null) {
            return null;
        }

        String fileName = null;
        if (ContentResolver.SCHEME_FILE.compareToIgnoreCase(scheme) == 0) {
            File file = new File(uri.getPath());
            fileName = file.getName();
        } else {
            Cursor cursor = BaseAppCore.getApplicationContext().getContentResolver().query(
                    uri, new String[]{MediaStore.Files.FileColumns.DISPLAY_NAME},
                    null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    if (columnIndex > -1) {
                        try {
                            fileName = cursor.getString(columnIndex);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                cursor.close();
            }
        }

        if (!TextUtils.isEmpty(fileName)) {
            return fileName;
        }

        return new File(uri.getEncodedPath()).getName();
    }

    public static String getLocalPathFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }

        String scheme = uri.getScheme();
        if (scheme == null) {
            return null;
        }

        String filePath = null;
        if (ContentResolver.SCHEME_FILE.compareToIgnoreCase(scheme) == 0) {
            filePath = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.compareToIgnoreCase(scheme) == 0) {
            Cursor cursor = BaseAppCore.getApplicationContext().getContentResolver().query(
                    uri, new String[]{MediaStore.Files.FileColumns.DATA},
                    null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                    if (columnIndex > -1) {
                        try {
                            filePath = cursor.getString(columnIndex);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                cursor.close();
            }
        }

        return filePath;
    }

    private static void initFileTypeMap() {
        if (mFileTypeSetMap.size() > 0) {
            return;
        }

        String[] audio = new String[]{
                "m4a", "mp3", "mid", "xmf", "ogg", "wav", "wma",
                "acc", "ape"
        };

        addFileTypes(FileType.Audio, audio);

        String[] video = new String[]{
                "3gp", "mp4", "avi", "rm", "rmvb", "wmv", "mkv",
                "flv", "mpg", "ram", "mov", "asf"
        };

        addFileTypes(FileType.Video, video);

        String[] picture = new String[]{
                "jpg", "gif", "jpeg", "bmp", "png"
        };

        addFileTypes(FileType.Picture, picture);

        String[] application = new String[]{
                "apk"
        };

        addFileTypes(FileType.Application, application);

        String[] ppt = new String[]{
                "ppt", "pptx"
        };

        addFileTypes(FileType.Ppt, ppt);

        String[] doc = new String[]{
                "doc", "docx", "wps"
        };

        addFileTypes(FileType.Doc, doc);

        String[] xls = new String[]{
                "xls", "xlsx", "xlsb"
        };

        addFileTypes(FileType.Xls, xls);

        String[] txt = new String[]{
                "txt"
        };

        addFileTypes(FileType.Txt, txt);

        String[] pdf = new String[]{
                "pdf"
        };

        addFileTypes(FileType.Pdf, pdf);

        String[] xml = new String[]{
                "xml", "html", "xhtml", "css"
        };

        addFileTypes(FileType.Xml, xml);

        String[] zip = new String[]{
                "zip", "rar", "tar", "7z", "gz", "gtar"
        };

        addFileTypes(FileType.Zip, zip);
    }

    private static void addFileTypes(FileType type, String[] extList) {
        for (String ext : extList) {
            mFileTypeSetMap.put(ext.hashCode(), type);
        }
    }

    public static FileType getFileType(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return FileType.Unknown;
        }

        initFileTypeMap();

        int index = fileName.lastIndexOf(".");
        String ext = fileName.substring(index + 1);
        FileType type = mFileTypeSetMap.get(ext.hashCode());
        if (type != null) {
            return type;
        }

        return FileType.Unknown;
    }

    public static boolean openFileUri(Context context, FileType fileType, String localFileUri) {
        return FileExtUtils.isUriExist(localFileUri)
                && openFileUri(context, fileType, Uri.parse(localFileUri));
    }

    public static boolean openFile(Context context, FileType fileType, String localFilePath) {
        return FileUtils.isFileExist(localFilePath)
                && openFileUri(context, fileType, Uri.fromFile(new File(localFilePath)));
    }

    public static boolean openFileUri(Context context, FileType fileType, Uri uri) {
        if (uri == null) {
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "*/*";

        switch (fileType) {
            case Audio:
                type = "audio/*";
                break;
            case Video:
                type = "video/*";
                break;
            case Picture:
                type = "image/*";
                break;
            case Txt:
                type = "text/plain";
                break;
            case Xml:
                type = "text/html";
                break;
            case Application:
                type = "application/vnd.android.package-archive";
                break;
            case Ppt:
                type = "application/vnd.ms-powerpoint";
                break;
            case Doc:
                type = "application/msword";
                break;
            case Xls:
                type = "application/vnd.ms-excel";
                break;
            case Pdf:
                type = "application/pdf";
                break;
            case Zip:
                type = "application/x-zip-compressed";
                break;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uri, type);
        FileUtils.grantUriReadPermission(uri);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void cleanDirectoryAsync(final String dir,
            final OnCleanDirectoryAsyncCallback cbk) {
        ThreadManager.getInstance().getFileHandler().post(new Runnable() {
            @Override
            public void run() {
                FileUtils.cleanDirectory(dir);

                if (cbk != null) {
                    cbk.onDirectoryCleaned();
                }
            }
        });
    }

    public static void getDirectorySizeAsync(final String dir,
            final OnGetDirectorySizeCallback cbk) {
        if (cbk == null) {
            return;
        }

        ThreadManager.getInstance().getFileHandler().post(new Runnable() {
            @Override
            public void run() {
                cbk.onGetDirectorySize(FileUtils.getDirectorySize(dir));
            }
        });
    }
}
