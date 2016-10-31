package com.jungle.apps.photos.module.homepage.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.jungle.apps.photos.R;
import com.jungle.toolbaractivity.toolbar.JungleToolBar;

public class HomepageToolbar extends JungleToolBar {

    public HomepageToolbar(Context context) {
        super(context);
        init(context);
    }

    public HomepageToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomepageToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.layout_homepage_actionbar, this);
    }
}
