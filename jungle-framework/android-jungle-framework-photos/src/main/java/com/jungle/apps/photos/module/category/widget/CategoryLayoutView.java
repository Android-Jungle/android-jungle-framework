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

package com.jungle.apps.photos.module.category.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.data.manager.CategoryManager;
import com.jungle.apps.photos.module.category.provider.CategoryContentProvider;
import com.jungle.apps.photos.module.category.provider.CategoryProviderManager;
import com.jungle.apps.photos.module.imgviewer.ImageViewActivity;
import com.jungle.base.manager.ThreadManager;
import com.jungle.imageloader.ImageLoaderUtils;
import com.jungle.widgets.dialog.JungleToast;
import com.jungle.widgets.loading.JungleLoadingLayout;

public class CategoryLayoutView extends FrameLayout {

    private PullToRefreshListView mListView;
    private JungleLoadingLayout mLoadingPageView;
    private CategoryListAdapter mAdapter = new CategoryListAdapter();
    private CategoryItemView.ViewType mViewType = CategoryItemView.ViewType.Category;


    private boolean mIsRefreshing = false;
    private int mProviderId = 0;
    private CategoryContentProvider mContentProvider;


    public CategoryLayoutView(Context context) {
        super(context);
        initLayout(context);
    }

    public CategoryLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public CategoryLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_category, this);

        mListView = (PullToRefreshListView) findViewById(R.id.category_list);
        ILoadingLayout layout = mListView.getLoadingLayoutProxy();
        layout.setPullLabel(context.getString(R.string.pull_up_to_refresh));
        layout.setReleaseLabel(context.getString(R.string.pull_up_refresh_release));

        mLoadingPageView = (JungleLoadingLayout) findViewById(R.id.loading_page);
        mLoadingPageView.setReloadListener(mReloadListener);
    }

    public void setViewType(CategoryItemView.ViewType viewType) {
        mViewType = viewType;
    }

    public void setProviderId(int providerId) {
        mProviderId = providerId;
        mContentProvider = CategoryProviderManager.getInstance().getProvider(providerId);

        if (mContentProvider.isFirstFetch() || mContentProvider.isEmpty()) {
            mLoadingPageView.setPageState(JungleLoadingLayout.PageState.Loading);
            fetchCategory();
        } else {
            updateLoadingState(false);
        }

        mContentProvider.addEventListener(mContentListener);
        mListView.setAdapter(mAdapter);

        if (mContentProvider.isSupportFetchMore()) {
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            mListView.setOnRefreshListener(mRefreshListener);
        } else {
            mListView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
    }

    private CategoryContentProvider.OnListener mContentListener =
            new CategoryContentProvider.OnListener() {
                @Override
                public void onContentChanged() {
                    updateLoadingState(false);
                }

                @Override
                public void onFetchFailed() {
                    updateLoadingState(true);
                }
            };

    private void fetchCategory() {
        mIsRefreshing = true;
        mContentProvider.fetchMore();
    }

    public void reloadData() {
        mLoadingPageView.setPageState(JungleLoadingLayout.PageState.Loading);
        mContentProvider.clear();
        fetchCategory();

        notifyDataSetChanged();
    }

    public void setRefreshing(boolean refreshing) {
        mIsRefreshing = refreshing;
    }

    public void updateLoadingState(boolean isFailed) {
        if (mIsRefreshing) {
            mListView.onRefreshComplete();
            setRefreshing(false);
        }

        if (isFailed) {
            mLoadingPageView.setPageState(
                    JungleLoadingLayout.PageState.LoadingFailed);
        } else {
            mAdapter.notifyDataSetChanged();
            mLoadingPageView.setPageState(mContentProvider.isEmpty()
                    ? JungleLoadingLayout.PageState.Empty
                    : JungleLoadingLayout.PageState.Invisible);
        }
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> mRefreshListener =
            new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    if (mContentProvider.isCanFetchMore()) {
                        fetchCategory();
                    } else {
                        ThreadManager.getInstance().postOnUIHandler(new Runnable() {
                            @Override
                            public void run() {
                                mListView.onRefreshComplete();
                                JungleToast.makeText(getContext(), R.string.no_more_pic).show();
                            }
                        });
                    }
                }
            };

    private JungleLoadingLayout.OnReloadListener mReloadListener =
            new JungleLoadingLayout.OnReloadListener() {
                @Override
                public void onNeedReload() {
                    fetchCategory();
                }
            };


    private class CategoryListAdapter extends BaseAdapter {

        private class ItemHolder {
            CategoryItemView mFirstItemView;
            CategoryItemView mSecondItemView;

            ItemHolder(View view) {
                mFirstItemView = (CategoryItemView)
                        view.findViewById(R.id.category_first_item);
                mSecondItemView = (CategoryItemView)
                        view.findViewById(R.id.category_second_item);
            }

            void updateItem(CategoryManager.CategoryItem firstItem,
                    CategoryManager.CategoryItem secondItem) {

                mFirstItemView.updateView(firstItem);
                mSecondItemView.updateView(secondItem);

                mFirstItemView.setTag(firstItem.mId);
                mSecondItemView.setTag(secondItem.mId);

                ImageLoaderUtils.displayImage(mFirstItemView.getImageView(), firstItem.mThumbUrl);
                ImageLoaderUtils.displayImage(mSecondItemView.getImageView(), secondItem.mThumbUrl);
            }
        }

        @Override
        public int getCount() {
            return mContentProvider.getItemCount() / 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder = null;

            if (convertView == null) {
                convertView = View.inflate(getContext(),
                        R.layout.layout_category_view, null);
                holder = new ItemHolder(convertView);

                convertView.setTag(holder);
                holder.mFirstItemView.setOnClickListener(mItemClickListener);
                holder.mSecondItemView.setOnClickListener(mItemClickListener);

                holder.mFirstItemView.setViewType(mViewType);
                holder.mSecondItemView.setViewType(mViewType);
            } else {
                holder = (ItemHolder) convertView.getTag();
                holder.mFirstItemView.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
                holder.mSecondItemView.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
            }

            holder.updateItem(
                    mContentProvider.getItem(position * 2),
                    mContentProvider.getItem(position * 2 + 1));

            return convertView;
        }

        private CategoryManager.OnFetchEventListener mFetchEventListener =
                new CategoryManager.OnFetchEventListener() {
                    @Override
                    public void onCategoryUpdated(CategoryManager.CategoryInfo info) {
                        notifyDataSetChanged();
                    }
                };

        private OnClickListener mItemClickListener =
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = (String) v.getTag();
                        ImageViewActivity.startImageViewActivity(
                                getContext(), mProviderId, id);
                    }
                };
    }
}
