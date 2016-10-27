package com.jungle.apps.photos.module.homepage.widget.hot;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.homepage.data.HotTagAdapter;
import com.jungle.widgets.layout.FlowLayout;

public class HotTagLayoutView extends FlowLayout {

    private HotTagAdapter mAdapter;

    public HotTagLayoutView(Context context) {
        super(context);
    }

    public HotTagLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HotTagLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(HotTagAdapter adapter) {
        mAdapter = adapter;
        notifyTagsSetChanged();
    }

    public HotTagAdapter getAdapter() {
        return mAdapter;
    }

    public void notifyTagsSetChanged() {
        removeAllViews();

        if (mAdapter == null) {
            return ;
        }

        Resources res = getContext().getResources();
        int horz = res.getDimensionPixelSize(R.dimen.hot_tag_horz_space);
        int vert = res.getDimensionPixelSize(R.dimen.hot_tag_vert_space);

        int count = mAdapter.getTagCount();
        for (int i = 0; i < count; ++i) {
            View v = mAdapter.getTagView(i);
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params.leftMargin = horz;
            params.rightMargin = horz;
            params.topMargin = vert;
            params.bottomMargin = vert;
            addView(v, params);
        }
    }
}
