/**
 * Android photos application project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.apps.photos.module.homepage.widget.category;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.CategoryActivity;
import com.jungle.apps.photos.module.category.data.manager.NormalCategoryManager;
import com.jungle.apps.photos.module.homepage.data.CategoryDisplayDataFetcher;
import com.jungle.imageloader.ImageLoaderUtils;
import com.jungle.widgets.loading.JungleLoadingLayout;
import com.jungle.widgets.view.ClickEffectView;
import com.jungle.widgets.view.JungleLanternView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CategoryDisplayLayoutView extends FrameLayout {

    private static final float IMAGE_SCALE = 9.0f / 16.0f;
    private static final float AD_SHOW_RATE = 0.5f;
    private static final int MIN_SHOW_AD_INTERVAL = 1000 * 60;
    private static final String CATEGORY_DISPLAY_AD_ID = "16TLevUaApZiYNUOGizUfrYz";


    private boolean mLayoutInitialized = false;
    private long mLastCloseAdTimestamp = 0;
    private CategoryScrollView mScrollView;
    private JungleLanternView mLanternView;
    private LinearLayout mCategoryContainer;
    private CategoryDisplayDataFetcher mCategoryDataFetcher;
    private JungleLanternView.LanternViewAdapter mAdapter;
    private JungleLoadingLayout mLoadingPageView;


    public CategoryDisplayLayoutView(Context context) {
        super(context);

        initLayout(context);
    }

    public CategoryDisplayLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initLayout(context);
    }

    public CategoryDisplayLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_homepage_category_display, this);

        mCategoryDataFetcher = new CategoryDisplayDataFetcher(mOnDataListener);
        mAdapter = new CategoryLanternAdapter();

        mScrollView = (CategoryScrollView) findViewById(R.id.category_scrollview);
        mCategoryContainer = (LinearLayout) findViewById(R.id.category_container);
        mLanternView = (JungleLanternView) findViewById(R.id.homepage_lantern_view);
        mLoadingPageView = (JungleLoadingLayout) findViewById(R.id.loading_page);
        mLoadingPageView.setPageState(JungleLoadingLayout.PageState.Loading);

        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mCategoryDataFetcher.fetchCategory();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
            }
        });

        mCategoryDataFetcher.fetchCategory();
    }

    private CategoryDisplayDataFetcher.OnDataListener mOnDataListener =
            new CategoryDisplayDataFetcher.OnDataListener() {
                @Override
                public void onDataPrepared(boolean empty) {
                    SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
                    String updateTime = getResources().getString(R.string.last_update_label)
                            + format.format(new Date());
                    mScrollView.getLoadingLayoutProxy().setLastUpdatedLabel(updateTime);
                    mScrollView.onRefreshComplete();

                    mCategoryContainer.removeAllViews();
                    mLoadingPageView.setPageState(empty
                            ? JungleLoadingLayout.PageState.Empty
                            : JungleLoadingLayout.PageState.Invisible);

                    initLantern();
                    initCategory();
                }
            };

    private void initLantern() {
        mLanternView.setAdapter(mAdapter);
        getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(this);
                        doLayout();
                        return false;
                    }
                });
    }

    private void doLayout() {
        if (mLayoutInitialized) {
            return;
        }

        mLayoutInitialized = true;
        final int width = mLanternView.getMeasuredWidth();
        ViewGroup.LayoutParams param = (ViewGroup.LayoutParams)
                mLanternView.getLayoutParams();
        param.height = (int) (width * IMAGE_SCALE);
        mLanternView.setLayoutParams(param);
    }

    private void initCategory() {
        Context context = getContext();
        List<CategoryItem.CategoryItemInfo> list =
                mCategoryDataFetcher.getCategoryList();

        for (CategoryItem.CategoryItemInfo item : list) {
            CategoryItemLayoutView view = new CategoryItemLayoutView(context);
            view.setCategoryItemInfo(item);

            addDividerView(context, item);
            mCategoryContainer.addView(view);
        }
    }

    private void addDividerView(Context context, CategoryItem.CategoryItemInfo item) {
        View view = new View(context);

        int dividerHeightRes = item.mHasDivider
                ? R.dimen.category_display_divider_height
                : R.dimen.category_item_divider_offset;

        view.setBackgroundColor(getResources().getColor(R.color.main_bkg_gray_color));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(dividerHeightRes));
        mCategoryContainer.addView(view, params);
    }


    private class CategoryLanternAdapter implements JungleLanternView.LanternViewAdapter {

        @Override
        public int getCount() {
            return mCategoryDataFetcher.getLanternList().size();
        }

        @Override
        public View getView(int position) {
            List<CategoryItem.CategoryInfo> list =
                    mCategoryDataFetcher.getLanternList();
            CategoryItem.CategoryInfo info = list.get(position);

            LanternImageViewLayout view = new LanternImageViewLayout(getContext());
            view.setTag(info);
            view.setDescText(info.mCategoryTag);
            ImageLoaderUtils.displayImage(view.getLanternImgView(), info.mCategoryImgUrl);

            final ClickEffectView effectView = new ClickEffectView(getContext());
            effectView.init(view);
            effectView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getContext();
                    CategoryItem.CategoryInfo info = (CategoryItem.CategoryInfo)
                            effectView.getInternalView().getTag();
                    CategoryActivity.startCategoryActivity(
                            context, info.mCategoryTag,
                            NormalCategoryManager.getInstance().getCategoryProvider(
                                    info.mCategory,
                                    info.mCategoryTag));
                }
            });

            return effectView;
        }
    }
}
