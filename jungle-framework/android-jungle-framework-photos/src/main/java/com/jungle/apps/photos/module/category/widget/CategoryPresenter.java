package com.jungle.apps.photos.module.category.widget;

import com.jungle.component.mvp.MVPPresenter;
import com.jungle.apps.photos.module.category.data.manager.CategoryManager;
import com.jungle.apps.photos.module.category.provider.CategoryContentProvider;
import com.jungle.apps.photos.module.category.provider.CategoryProviderManager;

public class CategoryPresenter extends MVPPresenter<CategoryView> {

    private int mCurrIndex = 0;
    private int mMaxCount = Integer.MAX_VALUE;
    private boolean mIsRefreshing = false;
    private boolean mIsFirstFetching = true;
    private int mProviderId = 0;
    private CategoryContentProvider mContentProvider;


    public CategoryPresenter(CategoryView mvpView) {
        super(mvpView);
    }

    public void setProviderId(int providerId) {
        mProviderId = providerId;
        mContentProvider = CategoryProviderManager.getInstance().getProvider(providerId);

        if (mContentProvider.isFirstFetch() || mContentProvider.isEmpty()) {
            mView.setLoading();
            fetchCategory();
        } else {
            mView.updateLoadingState(false);
        }

        mContentProvider.addEventListener(mContentListener);
    }

    public void reloadData() {
        mView.setLoading();
        mContentProvider.clear();
        fetchCategory();

        mView.notifyDataSetChanged();
    }

    public void fetchCategory() {
        mIsRefreshing = true;
        mContentProvider.fetchMore();
    }

    public int getProviderId() {
        return mProviderId;
    }

    public boolean isCanFetchMore() {
        return mContentProvider.isCanFetchMore();
    }

    public boolean isSupportFetchMore() {
        return mContentProvider.isSupportFetchMore();
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    public void setRefreshing(boolean refreshing) {
        mIsRefreshing = refreshing;
    }

    public boolean isEmpty() {
        return mContentProvider.isEmpty();
    }

    public int getItemCount() {
        return mContentProvider.getItemCount();
    }

    public CategoryManager.CategoryItem getItem(int position) {
        return mContentProvider.getItem(position);
    }

    private CategoryContentProvider.OnListener mContentListener =
            new CategoryContentProvider.OnListener() {
                @Override
                public void onContentChanged() {
                    mView.updateLoadingState(false);
                }

                @Override
                public void onFetchFailed() {
                    mView.updateLoadingState(true);
                }
            };
}
