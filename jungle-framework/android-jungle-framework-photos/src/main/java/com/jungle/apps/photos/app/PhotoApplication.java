/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.apps.photos.app;

import com.jungle.base.app.AppCore;
import com.jungle.base.app.BaseApplication;

public class PhotoApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AppCore createAppCore() {
        return new AppCore(this);
    }
}
