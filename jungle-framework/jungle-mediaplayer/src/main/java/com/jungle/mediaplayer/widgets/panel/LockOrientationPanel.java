/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.widgets.panel;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jungle.base.manager.ThreadManager;
import com.jungle.mediaplayer.R;

public class LockOrientationPanel extends FrameLayout
        implements View.OnClickListener {

    public interface OnLockChangedListener {
        void onChanged(boolean isLocked);
    }


    private View mLockIconView;
    private TextView mLockTipsView;
    private OnLockChangedListener mChangedListener;
    private boolean mIsLocked = false;


    public LockOrientationPanel(Context context) {
        super(context);
        initLayout(context);
    }

    public LockOrientationPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public LockOrientationPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_lock_orientation_panel, this);
        mLockIconView = findViewById(R.id.lock_icon);
        mLockTipsView = (TextView) findViewById(R.id.lock_tips);

        setClickable(true);
        setOnClickListener(this);
    }

    public void setLockChangedListener(OnLockChangedListener listener) {
        mChangedListener = listener;
    }

    public void doDestroy() {
        ThreadManager.getInstance().getUIHandler().removeCallbacks(mHidePanelRunnable);
    }

    private void showLock() {
        mLockIconView.setBackgroundResource(R.drawable.lock_screen_icon);
        mLockTipsView.setText(R.string.lock_orientation);
        showAndHideInternal();
    }

    private void showUnlock() {
        mLockIconView.setBackgroundResource(R.drawable.unlock_screen_icon);
        mLockTipsView.setText(R.string.unlock_orientation);
        showAndHideInternal();
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    public void showAndHide() {
        if (!mIsLocked) {
            showLock();
        } else {
            showUnlock();
        }
    }

    private void showAndHideInternal() {
        Handler handler = ThreadManager.getInstance().getUIHandler();
        handler.removeCallbacks(mHidePanelRunnable);
        handler.postDelayed(mHidePanelRunnable, 2000);

        setVisibility(View.VISIBLE);
    }

    private Runnable mHidePanelRunnable = new Runnable() {
        @Override
        public void run() {
            setVisibility(View.GONE);
        }
    };

    @Override
    public void onClick(View v) {
        mIsLocked = !mIsLocked;
        showAndHide();

        if (mChangedListener != null) {
            mChangedListener.onChanged(mIsLocked);
        }
    }
}
