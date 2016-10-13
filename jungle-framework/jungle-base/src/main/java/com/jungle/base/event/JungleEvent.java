/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.event;

public class JungleEvent {

    public static final Event APP_INITIALIZED = Event.create("app_initialized");
    public static final Event BEFORE_APP_CLEAN = Event.create("before_app_clean");

    public static final Event SWITCH_TO_FOREGROUND = Event.create("switch_to_foreground");
    public static final Event SWITCH_TO_BACKGROUND = Event.create("switch_to_background");

    public static final Event SCREEN_UNLOCKED = Event.create("screen_unlocked");
    public static final Event SCREEN_LOCKED = Event.create("screen_locked");
    public static final Event SCREEN_USER_PRESENT = Event.create("screen_user_present");
}
