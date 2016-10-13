/*
 * Copyright (C) 2015. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2015/08/19
 */

package com.jungle.widgets.dialog;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.jungle.imageloader.R;

public class LoadingDialog {

    public static JungleDialog showLoadingDialog(
            Context context, int loadingTextResId) {
        return showLoadingDialog(context, context.getString(loadingTextResId));
    }


    public static JungleDialog showLoadingDialog(
            Context context, String loadingText) {

        JungleDialog dialog = createDialog(context, loadingText);
        dialog.setCustomizedBackground(R.drawable.dialog_5x_radius_bkg);
        dialog.setWidthWrapContent(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    public static JungleDialog showLoadingDialog(
            Context context, int loadingTextResId, boolean cancelable) {
        return showLoadingDialog(context, context.getString(loadingTextResId), cancelable);
    }

    public static JungleDialog showLoadingDialog(
            Context context, String loadingText, boolean cancelable) {

        JungleDialog dialog = createDialog(context, loadingText);
        dialog.setCustomizedBackground(R.drawable.dialog_5x_radius_bkg);
        dialog.setWidthWrapContent(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }

    private static JungleDialog createDialog(Context context, String loadingText) {
        JungleDialog dialog = DialogUtils.createFullyCustomizedDialog(
                context, R.layout.dialog_loading);

        View loadingIcon = dialog.findViewById(R.id.loading_icon);
        TextView loadingTextView = (TextView) dialog.findViewById(R.id.loading_text);

        Animation rotateAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_rotate_anim);
        loadingIcon.startAnimation(rotateAnimation);
        loadingTextView.setText(loadingText);

        return dialog;
    }
}
