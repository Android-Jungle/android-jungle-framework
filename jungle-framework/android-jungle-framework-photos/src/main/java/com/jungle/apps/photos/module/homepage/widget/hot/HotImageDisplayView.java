package com.jungle.apps.photos.module.homepage.widget.hot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.imageloader.ImageLoaderUtils;
import com.jungle.widgets.view.ClickEffectView;

public class HotImageDisplayView extends ClickEffectView {

    public HotImageDisplayView(Context context) {
        super(context);
        initLayout(context);
    }

    public HotImageDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public HotImageDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View v = View.inflate(
                context, R.layout.layout_hot_image_display, null);
        init(v);
    }

    public void setDisplayInfo(String groupName, String imgUrl) {
        TextView groupNameText = (TextView) findViewById(R.id.hot_group_name);
        ImageView hotImage = (ImageView) findViewById(R.id.hot_img_view);

        ImageLoaderUtils.displayImage(hotImage, imgUrl);
        groupNameText.setText(groupName);
    }
}
