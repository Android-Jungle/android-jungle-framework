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
