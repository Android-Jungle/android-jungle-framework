/*
 * Copyright (C) 2015. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2015/08/19
 */

package com.jungle.widgets.dialog;

import android.content.Context;

/**
 * 对话框辅助接口.
 *
 * @author arnozhang
 */
public class DialogUtils {

    private static JungleDialog createDialogInternal(Context context) {
        return new JungleDialog(context);
    }


    /**
     * 创建有两个按钮的对话框.
     */
    public static JungleDialog createDialog(
            Context context, String title, String msg,
            String leftBtnText, String rightBtnText,
            JungleDialog.OnDialogBtnClickListener leftListener,
            JungleDialog.OnDialogBtnClickListener rightListener) {

        JungleDialog dialog = createDialogInternal(context);
        dialog.setTitle(title)
                .setMsg(msg)
                .setLeftBtn(leftBtnText, leftListener)
                .setRightBtn(rightBtnText, rightListener)
                .setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static JungleDialog createDialog(
            Context context, int titleResId, String msg,
            int leftBtnTextResId, int rightBtnTextResId,
            JungleDialog.OnDialogBtnClickListener leftListener,
            JungleDialog.OnDialogBtnClickListener rightListener) {

        return createDialog(context,
                titleResId > 0 ? context.getString(titleResId) : null,
                msg,
                context.getString(leftBtnTextResId),
                context.getString(rightBtnTextResId),
                leftListener, rightListener);
    }

    public static JungleDialog createDialog(
            Context context, String msg,
            int leftBtnTextResId, int rightBtnTextResId,
            JungleDialog.OnDialogBtnClickListener leftListener,
            JungleDialog.OnDialogBtnClickListener rightListener) {

        return createDialog(context, 0, msg,
                leftBtnTextResId, rightBtnTextResId,
                leftListener, rightListener);
    }

    public static JungleDialog createDialog(
            Context context, int titleResId, int msgResId,
            int leftBtnTextResId, int rightBtnTextResId,
            JungleDialog.OnDialogBtnClickListener leftListener,
            JungleDialog.OnDialogBtnClickListener rightListener) {

        return createDialog(context,
                titleResId,
                context.getString(msgResId),
                leftBtnTextResId, rightBtnTextResId,
                leftListener, rightListener);
    }

    public static JungleDialog createDialog(
            Context context, int msgResId,
            int leftBtnTextResId, int rightBtnTextResId,
            JungleDialog.OnDialogBtnClickListener leftListener,
            JungleDialog.OnDialogBtnClickListener rightListener) {

        return createDialog(context,
                0,
                context.getString(msgResId),
                leftBtnTextResId, rightBtnTextResId,
                leftListener, rightListener);
    }

    /**
     * 创建有一个按钮的对话框.
     */
    public static JungleDialog createOneBtnDialog(
            Context context, String title,
            String msg, String btnText,
            JungleDialog.OnDialogBtnClickListener btnListener) {

        JungleDialog dialog = createDialogInternal(context);
        dialog.setTitle(title)
                .setMsg(msg)
                .setOnlyOneBtn(btnText, btnListener)
                .setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static JungleDialog createOneBtnDialog(
            Context context, int titleResId,
            String msg, int btnTextResId,
            JungleDialog.OnDialogBtnClickListener btnListener) {

        return createOneBtnDialog(
                context,
                titleResId > 0 ? context.getString(titleResId) : null,
                msg, context.getString(btnTextResId),
                btnListener);
    }

    public static JungleDialog createOneBtnDialog(
            Context context, String msg, int btnTextResId,
            JungleDialog.OnDialogBtnClickListener btnListener) {

        return createOneBtnDialog(
                context,
                null,
                msg, context.getString(btnTextResId),
                btnListener);
    }

    public static JungleDialog createOneBtnDialog(
            Context context, int msgResId, int btnTextResId,
            JungleDialog.OnDialogBtnClickListener btnListener) {

        return createOneBtnDialog(
                context,
                null,
                context.getString(msgResId),
                context.getString(btnTextResId),
                btnListener);
    }

    /**
     * 创建有一个按钮的对话框.
     */
    public static JungleDialog createOneBtnDialog(
            Context context, String title, String msg, String btnText) {

        return createOneBtnDialog(context, title, msg, btnText,
                JungleDialog.mDismissListener);
    }

    public static JungleDialog createOneBtnDialog(
            Context context, int titleResId, String msg, int btnTextResId) {

        return createOneBtnDialog(
                context,
                titleResId > 0 ? context.getString(titleResId) : null,
                msg, context.getString(btnTextResId));
    }

    public static JungleDialog createOneBtnDialog(
            Context context, String msg, int btnTextResId) {

        return createOneBtnDialog(
                context, null, msg, context.getString(btnTextResId));
    }

    public static JungleDialog createOneBtnDialog(
            Context context, int msgResId, int btnTextResId) {

        return createOneBtnDialog(
                context, context.getString(msgResId), btnTextResId);
    }

    /**
     * 创建一个自定义内容区域的对话框.
     * <p/>
     * 拥有两个按钮和 title.
     */
    public static JungleDialog createCustomizedDialog(
            Context context, int layoutResId,
            String title,
            String leftBtnText, String rightBtnText,
            JungleDialog.OnDialogBtnClickListener leftListener,
            JungleDialog.OnDialogBtnClickListener rightListener) {

        JungleDialog dialog = createDialogInternal(context);
        dialog.setCustomizedViewResId(layoutResId)
                .setTitle(title)
                .setLeftBtn(leftBtnText, leftListener)
                .setRightBtn(rightBtnText, rightListener)
                .setWidthWrapContent(true)
                .setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static JungleDialog createCustomizedDialog(
            Context context, int layoutResId,
            int titleResId,
            int leftBtnTextResId, int rightBtnTextResId,
            JungleDialog.OnDialogBtnClickListener leftListener,
            JungleDialog.OnDialogBtnClickListener rightListener) {

        return createCustomizedDialog(
                context, layoutResId,
                titleResId > 0 ? context.getString(titleResId) : null,
                context.getString(leftBtnTextResId),
                context.getString(rightBtnTextResId),
                leftListener,
                rightListener);
    }

    /**
     * 创建一个自定义内容区域的对话框.
     * <p/>
     * 拥有两个按钮.没有 title.
     */
    public static JungleDialog createCustomizedDialog(
            Context context, int layoutResId,
            String leftBtnText, String rightBtnText,
            JungleDialog.OnDialogBtnClickListener leftListener,
            JungleDialog.OnDialogBtnClickListener rightListener) {

        return createCustomizedDialog(
                context, layoutResId, null,
                leftBtnText, rightBtnText, leftListener, rightListener
        );
    }

    public static JungleDialog createCustomizedDialog(
            Context context, int layoutResId,
            int leftBtnTextResId, int rightBtnTextResId,
            JungleDialog.OnDialogBtnClickListener leftListener,
            JungleDialog.OnDialogBtnClickListener rightListener) {

        return createCustomizedDialog(
                context, layoutResId, null,
                context.getString(leftBtnTextResId),
                context.getString(rightBtnTextResId),
                leftListener, rightListener);
    }

    /**
     * 创建一个自定义内容区域的对话框.
     * <p/>
     * 只拥有一个按钮.拥有 title.
     */
    public static JungleDialog createCustomizedOneBtnDialog(
            Context context, int layoutResId,
            String title, String btnText,
            JungleDialog.OnDialogBtnClickListener btnListener) {

        JungleDialog dialog = createDialogInternal(context);
        dialog.setCustomizedViewResId(layoutResId)
                .setTitle(title)
                .setOnlyOneBtn(btnText, btnListener)
                .setWidthWrapContent(true);
        return dialog;
    }

    public static JungleDialog createCustomizedOneBtnDialog(
            Context context, int layoutResId,
            int titleResId, int btnTextResId,
            JungleDialog.OnDialogBtnClickListener btnListener) {

        return createCustomizedOneBtnDialog(
                context, layoutResId,
                titleResId > 0 ? context.getString(titleResId) : null,
                context.getString(btnTextResId),
                btnListener);
    }

    /**
     * 创建一个自定义内容区域的对话框.
     * <p/>
     * 只拥有一个按钮.没有 title.
     */
    public static JungleDialog createCustomizedOneBtnDialog(
            Context context, int layoutResId, String btnText,
            JungleDialog.OnDialogBtnClickListener btnListener) {

        return createCustomizedOneBtnDialog(
                context, layoutResId, null, btnText, btnListener);
    }

    public static JungleDialog createCustomizedOneBtnDialog(
            Context context, int layoutResId, int btnTextResId,
            JungleDialog.OnDialogBtnClickListener btnListener) {

        return createCustomizedOneBtnDialog(
                context, layoutResId, context.getString(btnTextResId), btnListener);
    }

    /**
     * 创建一个内容完全自定义的对话框.
     */
    public static JungleDialog createFullyCustomizedDialog(
            Context context, int layoutResId) {
        JungleDialog dialog = createDialogInternal(context);
        dialog.setFullyCustomized(layoutResId).setWidthWrapContent(true);
        return dialog;
    }
}
