/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/13
 */

package com.jungle.toolbaractivity.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jungle.base.utils.MiscUtils;
import com.jungle.toolbaractivity.R;

public class GeneralToolBar extends JungleToolBar {

    public interface OnActionBarListener {
        void onBackButtonClicked();

        void onTitleIconClicked();

        void onTitleClicked();
    }


    /**
     * | BackButton | LogoImage | TitleText | ... Customized Zone ... | Right Zone |
     */
    private ImageButton mBackButton;
    private ImageView mTitleIcon;
    private TextView mTitleText;
    private LinearLayout mTitleZoneView;
    private FrameLayout mCustomizedZoneView;
    private FrameLayout mRightZoneView;
    private LinearLayout mToolbarContainer;
    private FrameLayout mToolbarExtraContainer;
    private OnActionBarListener mListener;


    public GeneralToolBar(Context context) {
        super(context);

        initToolbar(context);
    }

    public GeneralToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        initToolbar(context);
    }

    public GeneralToolBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initToolbar(context);
    }

    private void initToolbar(Context context) {
        setPadding(0, 0, 0, 0);
        setContentInsetsAbsolute(0, 0);
        View contentView = inflate(context, R.layout.layout_toolbar_content, this);

        // Init sub views.
        mBackButton = (ImageButton) contentView.findViewById(R.id.toolbar_back_btn);
        mTitleIcon = (ImageView) contentView.findViewById(R.id.toolbar_title_icon);
        mTitleText = (TextView) contentView.findViewById(R.id.toolbar_title_text);
        mTitleZoneView = (LinearLayout) contentView.findViewById(R.id.toolbar_title_zone);
        mCustomizedZoneView = (FrameLayout) contentView.findViewById(R.id.toolbar_customized_zone);
        mRightZoneView = (FrameLayout) contentView.findViewById(R.id.toolbar_right_zone);
        mToolbarContainer = (LinearLayout) contentView.findViewById(R.id.toolbar_container);
        mToolbarExtraContainer = (FrameLayout) contentView.findViewById(R.id.toolbar_extra_container);

        mTitleIcon.setImageDrawable(MiscUtils.getAppIcon());
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackButtonClicked();
            }
        });
    }

    public FrameLayout getRightZoneView() {
        return mRightZoneView;
    }

    public FrameLayout getCustomizedZoneView() {
        return mCustomizedZoneView;
    }

    public LinearLayout getToolbarContainer() {
        return mToolbarContainer;
    }

    public FrameLayout getToolbarExtraContainer() {
        return mToolbarExtraContainer;
    }

    public void setActionBarListener(OnActionBarListener listener) {
        mListener = listener;
    }

    public void showBackButton(boolean show) {
        mBackButton.setVisibility(show ? VISIBLE : GONE);
    }

    public void showTitleIcon(boolean show) {
        mTitleIcon.setVisibility(show ? VISIBLE : GONE);
    }

    public void showTitle(boolean show) {
        mTitleText.setVisibility(show ? VISIBLE : GONE);
    }

    public void showTitleZone(boolean show) {
        mTitleZoneView.setVisibility(show ? VISIBLE : GONE);
    }

    public void showRightZone(boolean show) {
        mRightZoneView.setVisibility(show ? VISIBLE : GONE);
    }

    public void setTitle(String title) {
        mTitleText.setText(title);
    }

    public void setTitle(int titleResId) {
        setTitle(getContext().getString(titleResId));
    }

    public void setTitleIcon(int titleIconResId) {
        mTitleIcon.setImageResource(titleIconResId);
    }

    public void setTitleIconClickable(boolean clickable) {
        mTitleIcon.setOnClickListener(clickable ? mTitleIconClickListener : null);
    }

    public void setTitleClickable(boolean clickable) {
        if (clickable) {
            mTitleText.setTextColor(getResources().getColorStateList(
                    R.color.toolbar_title_color_clickable));
        } else {
            mTitleText.setTextColor(getResources().getColor(
                    R.color.toolbar_title_color));
        }

        mTitleText.setOnClickListener(clickable ? mTitleClickListener : null);
    }

    protected void onBackButtonClicked() {
        if (mListener != null) {
            mListener.onBackButtonClicked();
        }
    }

    protected void onTitleClicked() {
        if (mListener != null) {
            mListener.onTitleClicked();
        }
    }

    protected void onTitleIconClicked() {
        if (mListener != null) {
            mListener.onTitleIconClicked();
        }
    }

    private View.OnClickListener mTitleClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTitleClicked();
                }
            };

    private View.OnClickListener mTitleIconClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTitleIconClicked();
                }
            };
}
