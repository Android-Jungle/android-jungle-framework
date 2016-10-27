package com.jungle.apps.photos.module.homepage.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.widgets.view.TabPageIndicatorView;

public class HomepageTabIndicatorView extends TabPageIndicatorView {

    private TextView mIndicatorText;
    private View mNewIcon;


    public HomepageTabIndicatorView(Context context) {
        super(context);
        initLayout(context);
    }

    public HomepageTabIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public HomepageTabIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_homepage_tab_indicator, this);
        mIndicatorText = (TextView) findViewById(R.id.indicator_text);
        mNewIcon = findViewById(R.id.new_icon);

        showNewIcon(false);
    }

    @Override
    protected void handleSelected(boolean selected) {
        View indicatorRootView = findViewById(R.id.indicator_root);
        indicatorRootView.setSelected(selected);
        mIndicatorText.setSelected(selected);
    }

    public void showNewIcon(boolean show) {
        mNewIcon.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setIndicatorText(int textResId) {
        setIndicatorText(getContext().getString(textResId));
    }

    public void setIndicatorText(String text) {
        mIndicatorText.setText(text);
    }
}
