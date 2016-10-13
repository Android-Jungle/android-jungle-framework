/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.base.registry;

public class MockDBRegistry implements Registry {

    @Override
    public void close() {
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void forceSave() {
    }

    @Override
    public boolean hasKey(String key) {
        return false;
    }

    @Override
    public ValueType getValueType(String key) {
        return ValueType.None;
    }

    @Override
    public boolean removeKey(String key) {
        return false;
    }

    @Override
    public boolean writeValue(String key, int value) {
        return false;
    }

    @Override
    public boolean writeValue(String key, long value) {
        return false;
    }

    @Override
    public boolean writeValue(String key, float value) {
        return false;
    }

    @Override
    public boolean writeValue(String key, boolean value) {
        return false;
    }

    @Override
    public boolean writeValue(String key, byte[] value) {
        return false;
    }

    @Override
    public boolean writeValue(String key, String value) {
        return false;
    }

    @Override
    public int readIntValue(String key) {
        return 0;
    }

    @Override
    public long readLongValue(String key) {
        return 0;
    }

    @Override
    public float readFloatValue(String key) {
        return 0;
    }

    @Override
    public boolean readBooleanValue(String key) {
        return false;
    }

    @Override
    public byte[] readBinaryValue(String key) {
        return null;
    }

    @Override
    public String readStringValue(String key) {
        return null;
    }

    @Override
    public int readIntValue(String key, int defValue) {
        return 0;
    }

    @Override
    public long readLongValue(String key, long defValue) {
        return 0;
    }

    @Override
    public float readFloatValue(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean readBooleanValue(String key, boolean defValue) {
        return false;
    }

    @Override
    public byte[] readBinaryValue(String key, byte[] defValue) {
        return null;
    }

    @Override
    public String readStringValue(String key, String defValue) {
        return null;
    }
}
