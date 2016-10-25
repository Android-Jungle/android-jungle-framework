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

package com.jungle.base.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.WindowManager;

public class DeviceUtils {

    public static float getSystemBrightnessPercent(Context context) {
        ContentResolver resolver = context.getContentResolver();

        float brightness = 0;
        try {
            brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            brightness /= 255.0f;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return brightness;
    }

    public static float getBrightnessPercent(Context context) {
        Activity activity = (Activity) context;
        WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
        return layout.screenBrightness;
    }

    public static void setBrightness(Context context, float percent) {
        if (!(context instanceof Activity)) {
            return;
        }

        if (percent < 0.01f) {
            percent = 0.01f;
        } else if (percent > 1.0f) {
            percent = 1.0f;
        }

        Activity activity = (Activity) context;
        WindowManager.LayoutParams layout = activity.getWindow().getAttributes();

        layout.screenBrightness = percent;
        activity.getWindow().setAttributes(layout);
    }
}
