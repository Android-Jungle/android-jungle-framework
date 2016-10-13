/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.common;

import android.content.Intent;
import com.jungle.base.app.BaseActivity;

public interface OnActivityResultListener {

    /**
     * @return true if need remove this listener. false to keep it.
     */
    boolean onActivityResult(
            BaseActivity activity, int requestCode,
            int resultCode, Intent data);
}
