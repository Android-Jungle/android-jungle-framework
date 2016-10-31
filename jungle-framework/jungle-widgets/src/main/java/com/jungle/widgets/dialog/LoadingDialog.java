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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.jungle.widgets.R;

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
