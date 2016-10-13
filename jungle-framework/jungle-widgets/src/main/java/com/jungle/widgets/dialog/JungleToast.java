/*
 * Copyright (C) 2015. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2015/08/19
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
