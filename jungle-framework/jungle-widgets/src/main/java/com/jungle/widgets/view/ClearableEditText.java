/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.widgets.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import com.jungle.imageloader.R;

public class ClearableEditText extends EditText {

    public interface OnTextClearedListener {
        void afterTextCleared();
    }


    private int mIconWidth;
    private int mIconHeight;
    private Drawable mClearIconDrawable;
    private OnTextClearedListener mTextClearedListener;


    public ClearableEditText(Context context) {
        super(context);
        initLayout(context, null);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, attrs);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        int defaultSize = getResources().getDimensionPixelSize(
                R.dimen.clear_icon_default_size);
        mIconWidth = defaultSize;
        mIconHeight = defaultSize;

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(
                    attrs, R.styleable.ClearableEditText);

            mIconWidth = arr.getDimensionPixelSize(
                    R.styleable.ClearableEditText_clearIconWidth, defaultSize);
            mIconHeight = arr.getDimensionPixelOffset(
                    R.styleable.ClearableEditText_clearIconHeight, defaultSize);
            mClearIconDrawable = arr.getDrawable(R.styleable.ClearableEditText_clearIcon);

            arr.recycle();
        }

        if (mClearIconDrawable == null) {
            mClearIconDrawable = getResources().getDrawable(R.drawable.default_clear_icon);
        }

        if (mClearIconDrawable != null) {
            mClearIconDrawable.setBounds(0, 0, mIconWidth, mIconHeight);
        }

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() != MotionEvent.ACTION_UP) {
                    return false;
                }

                Drawable[] drawables = getCompoundDrawables();
                Drawable rightDrawable = drawables[2];
                if (rightDrawable == null) {
                    return false;
                }

                int leftZoneWidth = getWidth() - getPaddingRight() - rightDrawable.getIntrinsicWidth();
                boolean tappedInClearIcon = event.getX() > leftZoneWidth;
                if (tappedInClearIcon) {
                    setText(null);
                    showClearButton(false);

                    if (mTextClearedListener != null) {
                        mTextClearedListener.afterTextCleared();
                    }
                }

                return false;
            }
        });

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showClearButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void setTextClearedListener(OnTextClearedListener listener) {
        mTextClearedListener = listener;
    }

    private void showClearButton() {
        String text = getText().toString();
        if (!TextUtils.isEmpty(text) && isFocused()) {
            showClearButton(true);
        } else {
            showClearButton(false);
        }
    }

    private void showClearButton(boolean show) {
        Drawable drawable = show ? mClearIconDrawable : null;
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], drawable, drawables[3]);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);

        if (focused) {
            setCursorVisible(true);
            showClearButton();
        } else {
            setCursorVisible(false);
            showClearButton(false);
        }
    }
}