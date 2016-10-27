package com.jungle.apps.photos.module.homepage.widget.personalcenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.data.manager.HotRecommendCategoryManager;
import com.jungle.apps.photos.module.category.widget.CategoryItemView;
import com.jungle.apps.photos.module.category.widget.CategoryLayoutView;

public class HotRecommendLayoutView extends FrameLayout {

    private CategoryLayoutView mCategoryLayoutView;


    public HotRecommendLayoutView(Context context) {
        super(context);
        initLayout(context);
    }

    public HotRecommendLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public HotRecommendLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(final Context context) {
        View.inflate(context, R.layout.layout_hot_recommend, this);

        int providerId = HotRecommendCategoryManager.getInstance().getCategoryProvider();
        mCategoryLayoutView = (CategoryLayoutView) findViewById(R.id.category_view);
        mCategoryLayoutView.setViewType(CategoryItemView.ViewType.HotRecommend);
        mCategoryLayoutView.setProviderId(providerId);
    }

    public void updateRecommend() {
        mCategoryLayoutView.reloadData();
    }
}
