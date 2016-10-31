package com.jungle.apps.photos.base.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.jungle.apps.photos.R;
import com.jungle.toolbaractivity.toolbar.JungleToolBar;

public class BaseToolbar extends JungleToolBar {

    public BaseToolbar(Context context) {
        super(context);
        init(context);
    }

    public BaseToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.layout_base_toolbar, this);
    }

    public FrameLayout getRightZoneView() {
        return (FrameLayout) findViewById(R.id.actionbar_right_zone);
    }

    public FrameLayout getCustomizedZoneView() {
        return (FrameLayout) findViewById(R.id.actionbar_customized_zone);
    }

    public FrameLayout getToolbarExtraContainer() {
        return (FrameLayout) findViewById(R.id.actionbar_extra_container);
    }

    public LinearLayout getToolbarContainer() {
        return (LinearLayout) findViewById(R.id.actionbar_container);
    }
}
