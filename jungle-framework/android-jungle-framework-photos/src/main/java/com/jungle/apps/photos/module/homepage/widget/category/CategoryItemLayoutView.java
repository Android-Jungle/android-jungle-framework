package com.jungle.apps.photos.module.homepage.widget.category;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import com.jungle.apps.photos.R;

public class CategoryItemLayoutView extends LinearLayout {

    private CategoryItem.CategoryItemInfo mCategoryItemInfo;

    private static int[] mSubCategoryViewId = new int[] {
            R.id.category_item_0,
            R.id.category_item_1,
            R.id.category_item_2
    };


    public CategoryItemLayoutView(Context context) {
        super(context);
    }

    public CategoryItemLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryItemLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCategoryItemInfo(CategoryItem.CategoryItemInfo categoryItemInfo) {
        mCategoryItemInfo = categoryItemInfo;

        setOrientation(LinearLayout.HORIZONTAL);
        View.inflate(getContext(), mCategoryItemInfo.mLayoutInfo.mLayoutResId, this);

        getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(this);
                        mCategoryItemInfo.mLayoutInfo.mSizeComputer.onComputeSize(CategoryItemLayoutView.this);
                        return false;
                    }
                });

        updateLayout();
    }

    private void updateLayout() {
        CategoryItem.ItemTagPos tagPos = CategoryItem.ItemTagPos.BottomLeft;

        for (int i = 0; i < mCategoryItemInfo.mCategoryList.size()
                && i < mSubCategoryViewId.length; ++i) {

            if (i < mCategoryItemInfo.mLayoutInfo.mItemTagPos.length) {
                tagPos = mCategoryItemInfo.mLayoutInfo.mItemTagPos[i];
            }

            CategoryDisplayView view = (CategoryDisplayView)
                    findViewById(mSubCategoryViewId[i]);
            view.setCategoryTagPos(tagPos);
            view.setCategoryInfo(mCategoryItemInfo.mCategoryList.get(i));
        }
    }
}
