/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.misc;

public class BaseJungleSize<T> {

    public T mWidth;
    public T mHeight;

    public BaseJungleSize() {
    }

    public BaseJungleSize(T width, T height) {
        mWidth = width;
        mHeight = height;
    }

    public T getWidth() {
        return mWidth;
    }

    public T getHeight() {
        return mHeight;
    }

    public void setWidth(T width) {
        mWidth = width;
    }

    public void setHeight(T height) {
        mHeight = height;
    }

    public void set(T width, T height) {
        mWidth = width;
        mHeight = height;
    }

    public void set(BaseJungleSize size) {
        mWidth = (T) size.mWidth;
        mHeight = (T) size.mHeight;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BaseJungleSize)) {
            return false;
        }

        BaseJungleSize rhs = (BaseJungleSize) o;
        return mWidth == rhs.mWidth && mHeight == rhs.mHeight;
    }

    public boolean equals(T width, T height) {
        return mWidth == width && mHeight == height;
    }
}
