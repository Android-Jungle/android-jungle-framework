/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.event;

public abstract class SingleEventListener<T> implements EventListener<T> {

    @Override
    public void onEvent(Event event, T data) {
        onEvent(data);
    }

    protected abstract void onEvent(T data);
}
