/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class DBUtils {

    public static boolean isTableExist(SQLiteDatabase db, String tbl) {
        if (db == null || TextUtils.isEmpty(tbl)) {
            return false;
        }

        if (tbl.equals("sqlite_master")) {
            return true;
        }

        String sql = "SELECT count(*) FROM sqlite_master WHERE "
                + "type='table' AND name='" + tbl + "';";
        Cursor cursor = db.rawQuery(sql, null);

        boolean exist = false;
        if (cursor != null) {
            if (cursor.moveToNext() && cursor.getInt(0) > 0) {
                exist = true;
            }

            cursor.close();
        }

        return exist;
    }

    public static boolean dropTable(SQLiteDatabase db, String tbl) {
        if (db == null || TextUtils.isEmpty(tbl)) {
            return false;
        }

        String sql = "DROP TABLE IF EXISTS " + tbl + ";";
        db.rawQuery(sql, null);

        return true;
    }

    public static boolean isTableHasKey(SQLiteDatabase db, String tbl,
            String keyName, String key) {

        if (db == null
                || TextUtils.isEmpty(tbl)
                || TextUtils.isEmpty(keyName)
                || TextUtils.isEmpty(key)) {
            return false;
        }

        String sql = "SELECT count(*) FROM " + tbl + " WHERE "
                + keyName + "='" + key + "';";
        Cursor cursor = db.rawQuery(sql, null);

        boolean exist = false;
        if (cursor != null) {
            if (cursor.moveToNext() && cursor.getInt(0) > 0) {
                exist = true;
            }

            cursor.close();
        }

        return exist;
    }

    public static SQLiteDatabase openDB(String dbPath, String dbName) {
        FileUtils.createPaths(dbPath);
        String dbFullPath = dbPath + dbName;
        return openDB(dbFullPath);
    }

    public static SQLiteDatabase openDB(String dbFullPath) {
        FileUtils.createFile(dbFullPath);
        return SQLiteDatabase.openOrCreateDatabase(dbFullPath, null);
    }
}
