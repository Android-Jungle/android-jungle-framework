/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.base.registry;

public interface Registry {

    public static enum ValueType {
        None(0),
        Integer(1),
        Long(2),
        Float(3),
        Boolean(4),
        Binary(5),
        String(6),
        Unknown(7);

        public int mRawType = 0;
        private ValueType(int rawType) {
            mRawType = rawType;
        }

        public static ValueType fromRaw(int rawType) {
            if (rawType == Integer.mRawType) {
                return Integer;
            } else if (rawType == Long.mRawType) {
                return Long;
            } else if (rawType == Float.mRawType) {
                return Float;
            } else if (rawType == Boolean.mRawType) {
                return Boolean;
            } else if (rawType == Binary.mRawType) {
                return Binary;
            } else if (rawType == String.mRawType) {
                return String;
            }

            return None;
        }
    }


    void close();
    boolean isValid();
    void forceSave();

    boolean hasKey(String key);
    ValueType getValueType(String key);

    // Remove key.
    boolean removeKey(String key);

    // Write value to Registry.
    boolean writeValue(String key, int value);
    boolean writeValue(String key, long value);
    boolean writeValue(String key, float value);
    boolean writeValue(String key, boolean value);
    boolean writeValue(String key, byte[] value);
    boolean writeValue(String key, String value);

    // Read value from Registry.
    int readIntValue(String key);
    long readLongValue(String key);
    float readFloatValue(String key);
    boolean readBooleanValue(String key);
    byte[] readBinaryValue(String key);
    String readStringValue(String key);

    // Read value from Registry with default value.
    int readIntValue(String key, int defValue);
    long readLongValue(String key, long defValue);
    float readFloatValue(String key, float defValue);
    boolean readBooleanValue(String key, boolean defValue);
    byte[] readBinaryValue(String key, byte[] defValue);
    String readStringValue(String key, String defValue);
}
