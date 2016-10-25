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

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jungle.imageloader.R;

/**
 * Customized support dialog.
 */
public class JungleDialog extends Dialog {

    public static class DialogConfig {

        public int mLeftBtnDrawableResId = R.drawable.dialog_alert_left_btn_bg;
        public int mRightBtnDrawableResId = R.drawable.dialog_alert_right_btn_bg;

        public int mLeftBtnColorResId = R.color.dialog_btn_text_color;
        public int mRightBtnColorResId = R.color.dialog_btn_text_color;


        public DialogConfig() {
        }

        public DialogConfig(DialogConfig rhs) {
            mLeftBtnDrawableResId = rhs.mLeftBtnDrawableResId;
            mRightBtnDrawableResId = rhs.mRightBtnDrawableResId;
            mLeftBtnColorResId = rhs.mLeftBtnColorResId;
            mRightBtnColorResId = rhs.mRightBtnColorResId;
        }
    }


    public static enum DialogBtn {
        LEFT,
        RIGHT
    }

    public interface OnDialogBtnClickListener {
        void onClick(JungleDialog dialog, DialogBtn which);
    }

    public static abstract class OnSimpleDialogBtnClickListener
            implements OnDialogBtnClickListener {

        @Override
        public void onClick(JungleDialog dialog, DialogBtn which) {
            onClick(dialog);
        }

        protected abstract void onClick(JungleDialog dialog);
    }


    public static final OnDialogBtnClickListener mDismissListener =
            new OnSimpleDialogBtnClickListener() {
                @Override
                protected void onClick(JungleDialog dialog) {
                    dialog.dismiss();
                }
            };


    private static DialogConfig mDialogConfig = new DialogConfig();
    private TextView mTitleTextView;
    private TextView mMsgTextView;
    private Button mLeftBtn;
    private Button mRightBtn;
    private OnDialogBtnClickListener mLeftClickListener;
    private OnDialogBtnClickListener mRightClickListener;
    private Object mTag;
    private boolean mIsWidthWrapContent = false;
    private boolean mIsCreated = false;


    public JungleDialog(Context context) {
        super(context, R.style.Jungle_Style_SimpleDialog);
        initLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsCreated = true;
        updateWidth();
    }

    private void initLayout() {
        setContentView(R.layout.dialog_simple_layout);

        mTitleTextView = (TextView) findViewById(R.id.dialog_title);
        mMsgTextView = (TextView) findViewById(R.id.dialog_msg_text);
        mLeftBtn = (Button) findViewById(R.id.dialog_left_btn);
        mRightBtn = (Button) findViewById(R.id.dialog_right_btn);

        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftClickListener != null) {
                    mLeftClickListener.onClick(
                            JungleDialog.this, DialogBtn.LEFT);
                }
            }
        });

        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightClickListener != null) {
                    mRightClickListener.onClick(
                            JungleDialog.this, DialogBtn.RIGHT);
                }
            }
        });

        mTitleTextView.setVisibility(View.GONE);
        mMsgTextView.setVisibility(View.GONE);

        Resources res = getContext().getResources();
        mLeftBtn.setBackgroundResource(mDialogConfig.mLeftBtnDrawableResId);
        mRightBtn.setBackgroundResource(mDialogConfig.mRightBtnDrawableResId);
        mLeftBtn.setTextColor(res.getColorStateList(mDialogConfig.mLeftBtnColorResId));
        mRightBtn.setTextColor(res.getColorStateList(mDialogConfig.mRightBtnColorResId));
    }

    public static void setCustomDialogConfig(DialogConfig config) {
        if (config != null) {
            mDialogConfig = config;
        }
    }

    public JungleDialog setWidthWrapContent(boolean wrapContent) {
        mIsWidthWrapContent = wrapContent;
        if (mIsCreated) {
            updateWidth();
        }

        return this;
    }

    private void updateWidth() {
        int width = mIsWidthWrapContent
                ? ViewGroup.LayoutParams.WRAP_CONTENT
                : ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 设置自定义区域.
     */
    JungleDialog setCustomizedViewResId(int layoutResId) {
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layoutResId, null);

        LinearLayout customizedLayout = (LinearLayout)
                findViewById(R.id.customized_view_layout);
        customizedLayout.setVisibility(View.VISIBLE);
        customizedLayout.addView(view);

        return this;
    }

    /**
     * 将对话框设置为完全自定义.
     */
    JungleDialog setFullyCustomized(int layoutResId) {
        setCustomizedViewResId(layoutResId);

        findViewById(R.id.dialog_horz_divider).setVisibility(View.GONE);
        findViewById(R.id.dialog_bottom_btn_zone).setVisibility(View.GONE);

        return this;
    }

    /**
     * 设置对话框背景.
     */
    public JungleDialog setCustomizedBackground(Drawable drawable) {
        findViewById(R.id.dialog_root_view).setBackgroundDrawable(drawable);
        return this;
    }

    public JungleDialog setCustomizedBackground(int drawableResId) {
        findViewById(R.id.dialog_root_view).setBackgroundResource(drawableResId);
        return this;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    public JungleDialog setTitle(String title) {
        mTitleTextView.setText(title);
        mTitleTextView.setVisibility(!TextUtils.isEmpty(title) ? View.VISIBLE : View.GONE);
        return this;
    }

    public void setTitle(int titleResId) {
        setTitle(getContext().getString(titleResId));
    }

    public JungleDialog setMsg(String msg) {
        mMsgTextView.setText(msg);
        mMsgTextView.setVisibility(!TextUtils.isEmpty(msg) ? View.VISIBLE : View.GONE);
        return this;
    }

    public JungleDialog setMsg(int msgResId) {
        setMsg(getContext().getString(msgResId));
        return this;
    }

    public JungleDialog setLeftBtnClickListener(
            OnDialogBtnClickListener leftListener) {
        mLeftClickListener = leftListener;
        return this;
    }

    public JungleDialog setRightBtnClickListener(
            OnDialogBtnClickListener rightListener) {
        mRightClickListener = rightListener;
        return this;
    }

    public JungleDialog setLeftBtnText(String leftBtnText) {
        mLeftBtn.setText(leftBtnText);
        return this;
    }

    public JungleDialog setLeftBtnText(int leftBtnTextResId) {
        setLeftBtnText(getContext().getString(leftBtnTextResId));
        return this;
    }

    public JungleDialog setRightBtnText(String rightBtnText) {
        mRightBtn.setText(rightBtnText);
        return this;
    }

    public JungleDialog setRightBtnText(int rightBtnTextResId) {
        setRightBtnText(getContext().getString(rightBtnTextResId));
        return this;
    }

    public JungleDialog setLeftBtnTextSize(float pixelSize) {
        mLeftBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelSize);
        return this;
    }

    public JungleDialog setRightBtnTextSize(float pixelSize) {
        mRightBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelSize);
        return this;
    }

    public JungleDialog setTitleTextSize(float pixelSize) {
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelSize);
        return this;
    }

    public JungleDialog setMsgTextSize(float pixelSize) {
        mMsgTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelSize);
        return this;
    }

    public JungleDialog setLeftBtn(
            String leftBtnText, OnDialogBtnClickListener leftListener) {
        setLeftBtnText(leftBtnText);
        setLeftBtnClickListener(leftListener);
        return this;
    }

    public void setLeftBtn(
            int leftBtnTextResId, OnDialogBtnClickListener leftListener) {
        setLeftBtnText(leftBtnTextResId);
        setLeftBtnClickListener(leftListener);
    }

    public JungleDialog setRightBtn(
            String rightBtnText, OnDialogBtnClickListener rightListener) {
        setRightBtnText(rightBtnText);
        setRightBtnClickListener(rightListener);
        return this;
    }

    public void setRightBtn(
            int rightBtnTextResId, OnDialogBtnClickListener rightListener) {
        setRightBtnText(rightBtnTextResId);
        setRightBtnClickListener(rightListener);
    }

    public JungleDialog setOnlyOneBtn(
            String btnText, OnDialogBtnClickListener btnListener) {
        mLeftBtn.setText(btnText);
        mLeftBtn.setBackgroundResource(R.drawable.dialog_alert_btn_bg);
        mLeftClickListener = btnListener;

        View dividerView = findViewById(R.id.dialog_vert_divider);
        dividerView.setVisibility(View.GONE);
        mRightBtn.setVisibility(View.GONE);

        return this;
    }

    public JungleDialog setMsgMaxLines(int maxLines) {
        mMsgTextView.setSingleLine(false);
        mMsgTextView.setMaxLines(maxLines);
        return this;
    }

    public JungleDialog setMsgMultiLines() {
        return setMsgMaxLines(100);
    }

    public JungleDialog enableLeftBtn(boolean enable) {
        mLeftBtn.setEnabled(enable);
        return this;
    }

    public JungleDialog enableRightBtn(boolean enable) {
        mRightBtn.setEnabled(enable);
        return this;
    }

    public JungleDialog enableOnlyOneBtn(boolean enable) {
        return enableLeftBtn(enable);
    }

    public JungleDialog setLeftBtnTextColor(int color) {
        mLeftBtn.setTextColor(color);
        return this;
    }

    public JungleDialog setLeftBtnTextColor(ColorStateList colorStateList) {
        mLeftBtn.setTextColor(colorStateList);
        return this;
    }

    public JungleDialog setRightBtnTextColor(int color) {
        mRightBtn.setTextColor(color);
        return this;
    }

    public JungleDialog setRightBtnTextColor(ColorStateList colorStateList) {
        mRightBtn.setTextColor(colorStateList);
        return this;
    }

    public JungleDialog setOneBtnTextColor(int color) {
        mLeftBtn.setTextColor(color);
        return this;
    }

    public JungleDialog setOneBtnTextColor(ColorStateList colorStateList) {
        mLeftBtn.setTextColor(colorStateList);
        return this;
    }
}
