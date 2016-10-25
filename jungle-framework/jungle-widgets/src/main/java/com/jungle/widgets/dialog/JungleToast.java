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

package com.jungle.widgets.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.jungle.imageloader.R;

public class JungleToast {

    public static enum IconType {
        None,
        Information,
        Success,
        Error,
    }


    private Toast mToast;

    public JungleToast(Context context) {
        View v = View.inflate(context, R.layout.layout_toast_view, null);
        int yOffset = context.getResources().getDimensionPixelSize(
                R.dimen.toast_vert_offset);

        mToast = new Toast(context);
        mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, yOffset);
        mToast.setView(v);

        setIconType(IconType.None);
    }

    public JungleToast setIconType(IconType iconType) {
        View v = mToast.getView().findViewById(R.id.toast_icon);
        if (v != null && v instanceof ImageView) {
            ImageView icon = (ImageView) v;

            if (iconType == IconType.None) {
                icon.setVisibility(View.GONE);
            } else {
                icon.setVisibility(View.VISIBLE);

                if (iconType == IconType.Information) {
                    icon.setImageResource(R.drawable.toast_icon_information);
                } else if (iconType == IconType.Success) {
                    icon.setImageResource(R.drawable.toast_icon_success);
                } else if (iconType == IconType.Error) {
                    icon.setImageResource(R.drawable.toast_icon_error);
                }
            }
        }

        return this;
    }

    public JungleToast setText(String text) {
        View v = mToast.getView().findViewById(R.id.toast_msg);
        if (v != null && v instanceof TextView) {
            TextView msgText = (TextView) v;
            msgText.setText(text);
        }

        return this;
    }

    public JungleToast setDuration(int duration) {
        mToast.setDuration(duration);
        return this;
    }

    public void show() {
        mToast.show();
    }

    public static void show(Context context, String text) {
        JungleToast.makeText(context, text).show();
    }

    public static void show(Context context, int text) {
        JungleToast.makeText(context, text).show();
    }

    public static JungleToast makeText(Context context, String msg) {
        JungleToast toast = new JungleToast(context);
        toast.setText(msg).setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    public static JungleToast makeText(Context context, String msg, int duration) {
        JungleToast toast = new JungleToast(context);
        toast.setText(msg).setDuration(duration);
        return toast;
    }

    public static JungleToast makeText(Context context, int msgResId) {
        return makeText(context, context.getString(msgResId));
    }

    public static JungleToast makeText(Context context, int msgResId, int duration) {
        return makeText(context, context.getString(msgResId), duration);
    }
}
