/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.base.registry;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import com.jungle.base.app.AppCore;

public class SharedPreferencesRegistry implements Registry {

    private SharedPreferences mPreferences;
    private String mPreferenceName;


    public SharedPreferencesRegistry(String name) {
        mPreferenceName = name;
        init(AppCore.getApplicationContext());
    }


    public SharedPreferencesRegistry(Context context, String name) {
        mPreferenceName = name;
        init(context);
    }

    private void init(Context context) {
        mPreferences = context.getSharedPreferences(
                mPreferenceName, Context.MODE_PRIVATE);
    }

    @Override
    public void close() {
        mPreferences.edit().commit();
        mPreferences = null;
    }

    @Override
    public boolean isValid() {
        return mPreferences != null;
    }

    @Override
    public void forceSave() {
        close();
        init(AppCore.getApplicationContext());
    }

    @Override
    public boolean hasKey(String key) {
        return mPreferences.contains(key);
    }

    @Override
    public ValueType getValueType(String key) {
        return ValueType.Unknown;
    }

    @Override
    public boolean removeKey(String key) {
        mPreferences.edit().remove(key).apply();
        return true;
    }

    @Override
    public boolean writeValue(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
        return true;
    }

    @Override
    public boolean writeValue(String key, long value) {
        mPreferences.edit().putLong(key, value).apply();
        return true;
    }

    @Override
    public boolean writeValue(String key, float value) {
        mPreferences.edit().putFloat(key, value).apply();
        return true;
    }

    @Override
    public boolean writeValue(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
        return true;
    }

    @Override
    public boolean writeValue(String key, byte[] value) {
        String encodeValue = Base64.encodeToString(value, Base64.DEFAULT);
        return writeValue(key, encodeValue);
    }

    @Override
    public boolean writeValue(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
        return true;
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
        return readFloatValue(key, 0);
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
        return mPreferences.getInt(key, defValue);
    }

    @Override
    public long readLongValue(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    @Override
    public float readFloatValue(String key, float defValue) {
        return mPreferences.getFloat(key, defValue);
    }

    @Override
    public boolean readBooleanValue(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    @Override
    public byte[] readBinaryValue(String key, byte[] defValue) {
        String value = readStringValue(key);
        if (TextUtils.isEmpty(value)) {
            return defValue;
        }

        return Base64.decode(value, Base64.DEFAULT);
    }

    @Override
    public String readStringValue(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }
}
