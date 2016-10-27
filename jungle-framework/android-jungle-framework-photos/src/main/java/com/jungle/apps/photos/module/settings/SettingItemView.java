package com.jungle.apps.photos.module.settings;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jungle.apps.photos.R;

public class SettingItemView extends LinearLayout {

    public static interface OnSettingItemClickListener {
        void onItemClicked(SettingItemView settingItemView);
    }


    private View mLeftIconView;
    private TextView mItemText;
    private RelativeLayout mCustomizeZone;
    private String mItemClickAction;
    private OnSettingItemClickListener mItemClickListener;


    public SettingItemView(Context context) {
        super(context);

        initLayoutInternal(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initLayoutInternal(context, attrs);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initLayoutInternal(context, attrs);
    }

    private void initLayoutInternal(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_setting_item_view, this);

        mLeftIconView = findViewById(R.id.item_icon_view);
        mItemText = (TextView) findViewById(R.id.item_text_view);
        mCustomizeZone = (RelativeLayout) findViewById(R.id.item_customized_zone);

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(
                    attrs, R.styleable.SettingItemView);

            String itemText = arr.getString(
                    R.styleable.SettingItemView_itemText);
            boolean rightDetailIconVisible = arr.getBoolean(
                    R.styleable.SettingItemView_showDetailIcon, true);
            boolean topDividerVisible = arr.getBoolean(
                    R.styleable.SettingItemView_showTopDivider, false);
            boolean bottomDividerVisible = arr.getBoolean(
                    R.styleable.SettingItemView_showBottomDivider, true);
            int customizeLayoutResId = arr.getResourceId(
                    R.styleable.SettingItemView_customizeLayout, 0);
            ColorStateList itemColor = arr.getColorStateList(
                    R.styleable.SettingItemView_itemTextColor);
            Drawable leftIcon = arr.getDrawable(R.styleable.SettingItemView_itemLeftIcon);

            if (itemColor != null) {
                setItemTextColor(itemColor);
            } else {
                int color = arr.getColor(
                        R.styleable.SettingItemView_itemTextColor, -1);
                if (color != -1) {
                    setItemTextColor(color);
                }
            }

            setLeftIcon(leftIcon);
            setItemText(itemText);
            setTopDividerVisible(topDividerVisible);
            setBottomDividerVisible(bottomDividerVisible);
            setRightDetailIconVisible(rightDetailIconVisible);
            setCustomizeLayoutId(customizeLayoutResId);

            arr.recycle();
        }

        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClicked(SettingItemView.this);
                }
            }
        });
    }

    /**
     * 设置左侧图标.
     */
    public void setLeftIcon(Drawable drawable) {
        mLeftIconView.setBackgroundDrawable(drawable);
        mLeftIconView.setVisibility(drawable != null ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置 Item 文本.
     */
    public void setItemText(String itemText) {
        mItemText.setText(itemText);
    }

    /**
     * 设置 Item 文本颜色.
     */
    public void setItemTextColor(int color) {
        mItemText.setTextColor(color);
    }

    /**
     * 设置 Item 文本颜色.
     */
    public void setItemTextColor(ColorStateList color) {
        mItemText.setTextColor(color);
    }

    /**
     * 设置顶部分割线是否展示.
     */
    public void setTopDividerVisible(boolean visible) {
        View dividerView = findViewById(R.id.item_top_divider_line);
        dividerView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 设置底部分割线是否展示.
     */
    public void setBottomDividerVisible(boolean visible) {
        View dividerView = findViewById(R.id.item_bottom_divider_line);
        dividerView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 设置右边详情按钮是否展示.
     */
    public void setRightDetailIconVisible(boolean visible) {
        View detailIconView = findViewById(R.id.item_right_detail_icon);
        detailIconView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 设置自定义区域 LayoutId.
     */
    public void setCustomizeLayoutId(int layoutResId) {
        mCustomizeZone.removeAllViews();

        if (layoutResId != 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(layoutResId, mCustomizeZone);
        }
    }

    /**
     * 设置点击回调.
     */
    public void setItemClickAction(OnSettingItemClickListener listener) {
        mItemClickListener = listener;
    }
}
