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
