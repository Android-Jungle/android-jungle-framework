/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.event;

public class Event {

    public static Event create(String eventName) {
        return new Event(eventName);
    }

    public static Event create() {
        return new Event();
    }


    private String mEventName;


    private Event() {
    }

    private Event(String eventName) {
        mEventName = eventName;
    }

    @Override
    public String toString() {
        return mEventName;
    }
}
