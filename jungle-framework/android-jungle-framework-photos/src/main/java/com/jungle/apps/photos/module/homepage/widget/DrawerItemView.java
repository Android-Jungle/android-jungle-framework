package com.jungle.apps.photos.module.homepage.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jungle.apps.photos.R;

public class DrawerItemView extends LinearLayout {

    public DrawerItemView(Context context) {
        super(context);
        initLayout(context, null);
    }

    public DrawerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, attrs);
    }

    public DrawerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        View.inflate(context, R.layout.layout_draw_item, this);

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(
                    attrs, R.styleable.DrawerItemView);
            String itemText = arr.getString(R.styleable.DrawerItemView_itemDesc);
            Drawable itemIcon = arr.getDrawable(R.styleable.DrawerItemView_itemIcon);

            setItemText(itemText);
            setItemIcon(itemIcon);

            arr.recycle();
        }
    }

    public void setItemText(String itemText) {
        TextView itemTextView = (TextView) findViewById(R.id.item_text);
        itemTextView.setText(itemText);
    }

    public void setItemIcon(Drawable drawable) {
        View itemIconView = findViewById(R.id.item_icon);
        itemIconView.setBackgroundDrawable(drawable);
    }
}
