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
