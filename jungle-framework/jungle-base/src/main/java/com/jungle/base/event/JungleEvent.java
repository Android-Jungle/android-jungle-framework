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
