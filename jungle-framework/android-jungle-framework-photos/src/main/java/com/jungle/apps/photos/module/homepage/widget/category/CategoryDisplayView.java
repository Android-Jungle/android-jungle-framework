package com.jungle.apps.photos.module.homepage.widget.category;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.CategoryActivity;
import com.jungle.apps.photos.module.category.data.manager.NormalCategoryManager;
import com.jungle.imageloader.ImageLoaderUtils;
import com.jungle.widgets.view.ClickEffectView;

public class CategoryDisplayView extends ClickEffectView {

    private CategoryItem.CategoryInfo mCategoryInfo;
    private CategoryItem.ItemTagPos mTagPos = CategoryItem.ItemTagPos.BottomLeft;
    private TextView mCategoryTagText;


    public CategoryDisplayView(Context context) {
        super(context);

        initLayout(context);
    }

    public CategoryDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initLayout(context);
    }

    public CategoryDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initLayout(context);
    }

    private void initLayout(Context context) {
        View v = (FrameLayout) View.inflate(
                context, R.layout.layout_category_display, null);
        mCategoryTagText = (TextView) v.findViewById(R.id.category_display_title);

        init(v);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryActivity.startCategoryActivity(
                        getContext(),
                        mCategoryInfo.mCategoryTag,
                        NormalCategoryManager.getInstance().getCategoryProvider(
                                mCategoryInfo.mCategory,
                                mCategoryInfo.mCategoryTag));
            }
        });
    }

    public void setCategoryInfo(CategoryItem.CategoryInfo categoryInfo) {
        mCategoryInfo = categoryInfo;

        mCategoryTagText.setText(mCategoryInfo.mCategoryTag);
        ImageView imageView = (ImageView) findViewById(R.id.category_display_img);
        ImageLoaderUtils.displayImage(imageView, mCategoryInfo.mCategoryImgUrl);
    }

    public void setCategoryTagPos(CategoryItem.ItemTagPos tagPos) {
        if (mTagPos == tagPos) {
            return;
        }

        mTagPos = tagPos;

        Context context = getContext();
        Resources res = context.getResources();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                mCategoryTagText.getLayoutParams();

        if (mTagPos == CategoryItem.ItemTagPos.Top ||
                mTagPos == CategoryItem.ItemTagPos.Bottom) {

            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = res.getDimensionPixelSize(
                    R.dimen.category_item_tag_top_height);
            params.leftMargin = 0;
            params.bottomMargin = 0;
            mCategoryTagText.setBackgroundColor(res.getColor(
                    R.color.category_item_tag_top_color));

            if (mTagPos == CategoryItem.ItemTagPos.Top) {
                params.gravity = Gravity.TOP;
            } else {
                params.gravity = Gravity.BOTTOM;
            }
        } else {
            int margin = res.getDimensionPixelSize(
                    R.dimen.category_item_bottom_left_margin);
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            params.leftMargin = margin;
            params.bottomMargin = margin;
            mCategoryTagText.setBackgroundResource(R.drawable.circle_mask);
        }

        mCategoryTagText.setLayoutParams(params);
    }
}
