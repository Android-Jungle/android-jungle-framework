package com.jungle.apps.photos.module.homepage.widget.hot;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.homepage.data.HotTagAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HotGroupDisplayView extends FrameLayout {

    public static class ImageGravity {
        public static final int Left = 0;
        public static final int Right = 1;
    }


    public static class HotInfo {
        String mName;
        String mImgUrl;
        String mClickTag;
        int mImgGravity = HotGroupDisplayView.ImageGravity.Left;
        List<String> mTagsList = new ArrayList<String>();
    }


    public static interface HotGroupTagAdapter extends HotTagAdapter {
        HotInfo getHotInfo();
    }


    private int mImgGravity = ImageGravity.Left;
    private HotTagLayoutView mTagFlowLayout;
    private HotImageDisplayView mHotInfoView;
    private HotGroupTagAdapter mAdapter;


    public HotGroupDisplayView(Context context) {
        super(context);
    }

    public HotGroupDisplayView(Context context, int imgGravity) {
        super(context);
        mImgGravity = imgGravity;
        initView(context);
    }

    public HotGroupDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HotGroupDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        int resId = mImgGravity == ImageGravity.Left
                ? R.layout.layout_hot_diaplay_left
                : R.layout.layout_hot_diaplay_right;

        View.inflate(context, resId, this);

        mHotInfoView = (HotImageDisplayView) findViewById(R.id.hot_info_view);
        mTagFlowLayout = (HotTagLayoutView) findViewById(R.id.hot_img_tags);
    }

    public void setAdapter(HotGroupTagAdapter adapter) {
        mAdapter = adapter;
        mTagFlowLayout.setAdapter(adapter);

        updateView();
    }

    private void updateView() {
        if (mAdapter == null) {
            return;
        }

        final HotInfo info = mAdapter.getHotInfo();
        if (info == null) {
            return ;
        }

        mHotInfoView.setDisplayInfo(info.mName, info.mImgUrl);

        if (!TextUtils.isEmpty(info.mClickTag) || !info.mTagsList.isEmpty()) {
            mHotInfoView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String click = info.mClickTag;

                    if (TextUtils.isEmpty(click)) {
                        int count = info.mTagsList.size();
                        int index = Math.abs(new Random(System.currentTimeMillis()).nextInt()) % count;
                        click = info.mTagsList.get(index);
                    }

                    if (!TextUtils.isEmpty(click)) {
//                        CategoryActivity.startCategoryActivity(
                        //                                getContext(),
                        //                                click,
                        //                                SearchCategoryManager.getInstance().getCategoryProvider(
                        //                                        AppUtils.getMainCategory(),
                        //                                        click));
                    }
                }
            });
        }
    }
}
