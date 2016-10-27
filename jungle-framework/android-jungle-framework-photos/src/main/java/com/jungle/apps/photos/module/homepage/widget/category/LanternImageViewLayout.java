package com.jungle.apps.photos.module.homepage.widget.category;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.jungle.apps.photos.R;

public class LanternImageViewLayout extends FrameLayout {

    public LanternImageViewLayout(Context context) {
        super(context);

        initLayout(context);
    }

    public LanternImageViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        initLayout(context);
    }

    public LanternImageViewLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_lantern_img_view, this);
    }

    public void setDescText(String descText) {
        TextView desc = (TextView) findViewById(R.id.lantern_desc);
        desc.setText(descText);
    }

    public ImageView getLanternImgView() {
        return (ImageView) findViewById(R.id.lantern_img);
    }
}
