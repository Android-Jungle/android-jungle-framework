/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
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
