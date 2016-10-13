/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.base.registry;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.jungle.base.utils.DBUtils;

public class DBRegistry implements Registry {

    private static final String REGISTRY_TABLE = "tbl_registry";


    private SQLiteDatabase mDatabase;
    private String mDbPath;
    private int mVersion;


    public DBRegistry(String dbPath, int version) {
        mDbPath = dbPath;
        mVersion = version;

        init();
    }

    private void init() {
        mDatabase = SQLiteDatabase.openOrCreateDatabase(mDbPath, null);
        mDatabase.setVersion(mVersion);

        initDatabase();
    }

    private void initDatabase() {
        if (!DBUtils.isTableExist(mDatabase, REGISTRY_TABLE)) {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE ")
                    .append(REGISTRY_TABLE)
                    .append("(key VARCHAR PRIMARY KEY, ")
                    .append("type INTEGER DEFAULT '")
                    .append(ValueType.None.mRawType)
                    .append("',")
                    .append(" value BLOB);");

            mDatabase.beginTransaction();
            try {
                mDatabase.execSQL(builder.toString());
                mDatabase.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                mDatabase.endTransaction();
            }
        }
    }

    private boolean writeValueInternal(String key, ValueType type, byte[] value) {
        boolean bSuccess = true;

        if (value == null) {
            bSuccess = removeKey(key);
        } else {
            try {
                ContentValues values = new ContentValues();
                values.put("key", key);
                values.put("type", type.mRawType);
                values.put("value", value);

                mDatabase.replace(REGISTRY_TABLE, null, values);
            } catch (SQLException e) {
                e.printStackTrace();
                bSuccess = false;
            }
        }

        return bSuccess;
    }

    private byte[] readValueInternal(String key) {
        byte[] value = null;
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT value FROM ")
                .append(REGISTRY_TABLE)
                .append(" WHERE key='")
                .append(key)
                .append("';");

        Cursor cursor = mDatabase.rawQuery(builder.toString(), null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                value = cursor.getBlob(0);
            }

            cursor.close();
        }

        return value;
    }

    @Override
    public void close() {
        mDatabase.close();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void forceSave() {
        close();
        init();
    }

    @Override
    public boolean hasKey(String key) {
        return DBUtils.isTableHasKey(mDatabase, REGISTRY_TABLE, "key", key);
    }

    @Override
    public ValueType getValueType(String key) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT type FROM ")
                .append(REGISTRY_TABLE)
                .append(" WHERE key='")
                .append(key)
                .append("';");

        Cursor cursor = mDatabase.rawQuery(builder.toString(), null);
        if (cursor == null) {
            return ValueType.None;
        }

        if (!cursor.moveToNext()) {
            cursor.close();
            return ValueType.None;
        }

        ValueType type = ValueType.fromRaw(cursor.getInt(0));
        cursor.close();
        return type;
    }

    @Override
    public boolean removeKey(String key) {
        boolean bSuccess = true;

        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ")
                .append(REGISTRY_TABLE)
                .append(" WHERE key='")
                .append(key)
                .append("';");

        try {
            mDatabase.execSQL(builder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            bSuccess = false;
        }

        return bSuccess;
    }

    @Override
    public boolean writeValue(String key, int value) {
        return writeValueInternal(
                key, ValueType.Integer,
                String.valueOf(value).getBytes());
    }

    @Override
    public boolean writeValue(String key, long value) {
        return writeValueInternal(
                key, ValueType.Long,
                String.valueOf(value).getBytes());
    }

    @Override
    public boolean writeValue(String key, float value) {
        return writeValueInternal(
                key, ValueType.Float,
                String.valueOf(value).getBytes());
    }

    @Override
    public boolean writeValue(String key, boolean value) {
        return writeValueInternal(
                key, ValueType.Boolean,
                String.valueOf(value).getBytes());
    }

    @Override
    public boolean writeValue(String key, byte[] value) {
        return writeValueInternal(key, ValueType.Binary, value);
    }

    @Override
    public boolean writeValue(String key, String value) {
        return writeValueInternal(
                key, ValueType.String,
                TextUtils.isEmpty(value) ? null : value.getBytes());
    }

    @Override
    public int readIntValue(String key) {
        return readIntValue(key, 0);
    }

    @Override
    public long readLongValue(String key) {
        return readLongValue(key, 0);
    }

    @Override
    public float readFloatValue(String key) {
        return readFloatValue(key, 0.0f);
    }

    @Override
    public boolean readBooleanValue(String key) {
        return readBooleanValue(key, false);
    }

    @Override
    public byte[] readBinaryValue(String key) {
        return readBinaryValue(key, null);
    }

    @Override
    public String readStringValue(String key) {
        return readStringValue(key, null);
    }

    @Override
    public int readIntValue(String key, int defValue) {
        String value = readStringValue(key, null);
        if (TextUtils.isEmpty(value)) {
            return defValue;
        }

        int realValue = defValue;
        try {
            realValue = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return realValue;
    }

    @Override
    public long readLongValue(String key, long defValue) {
        String value = readStringValue(key, null);
        if (TextUtils.isEmpty(value)) {
            return defValue;
        }

        long realValue = defValue;
        try {
            realValue = Long.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return realValue;
    }

    @Override
    public float readFloatValue(String key, float defValue) {
        String value = readStringValue(key, null);
        if (TextUtils.isEmpty(value)) {
            return defValue;
        }

        float realValue = defValue;
        try {
            realValue = Float.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return realValue;
    }

    @Override
    public boolean readBooleanValue(String key, boolean defValue) {
        String value = readStringValue(key, null);
        if (TextUtils.isEmpty(value)) {
            return defValue;
        }

        return Boolean.valueOf(value);
    }

    @Override
    public byte[] readBinaryValue(String key, byte[] defValue) {
        byte[] raw = readValueInternal(key);
        if (raw == null) {
            return defValue;
        }

        return raw;
    }

    @Override
    public String readStringValue(String key, String defValue) {
        byte[] raw = readValueInternal(key);
        if (raw == null) {
            return defValue;
        }

        return new String(raw);
    }
}
