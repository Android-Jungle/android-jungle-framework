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

package com.jungle.apps.photos.module.category.provider;

import com.jungle.apps.photos.base.component.WeakEventListener;
import com.jungle.apps.photos.module.category.data.manager.CategoryManager;

public abstract class CategoryContentProvider
        extends WeakEventListener<CategoryContentProvider.OnListener> {

    public static interface OnListener {
        void onContentChanged();

        void onFetchFailed();
    }


    private int mProviderId = 0;
    protected CategoryManager.CategoryInfo mCategoryInfo;
    protected boolean mIsFirstFetch = true;
    protected String mDefaultCategoryName;


    public CategoryContentProvider(CategoryManager.CategoryInfo info) {
        mCategoryInfo = info;
        mCategoryInfo.addEventListener(mFetchEventListener);
    }

    public boolean isFirstFetch() {
        return mIsFirstFetch;
    }

    public int getProviderId() {
        return mProviderId;
    }

    public void setProviderId(int providerId) {
        mProviderId = providerId;
    }


    public abstract void clear();

    public abstract boolean isSupportFetchMore();

    public abstract boolean isCanFetchMore();

    public abstract void fetchMore();

    public String getDefaultCategoryName() {
        return mDefaultCategoryName;
    }

    public void setDefaultCategoryName(String categoryName) {
        mDefaultCategoryName = categoryName;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public int getItemCount() {
        return mCategoryInfo.mCategoryItems.size();
    }

    public CategoryManager.CategoryItem getItem(int position) {
        return mCategoryInfo.mCategoryItems.get(position);
    }

    public CategoryManager.CategoryInfo getCategoryInfo() {
        return mCategoryInfo;
    }

    protected void notifyContentChanged() {
        notifyEvent(new NotifyRunnable<OnListener>() {
            @Override
            public void notify(OnListener listener) {
                listener.onContentChanged();
            }
        });
    }

    protected void notifyFetchFailed() {
        notifyEvent(new NotifyRunnable<OnListener>() {
            @Override
            public void notify(OnListener listener) {
                listener.onFetchFailed();
            }
        });
    }

    private CategoryManager.OnFetchEventListener mFetchEventListener =
            new CategoryManager.OnFetchEventListener() {
                @Override
                public void onCategoryUpdated(CategoryManager.CategoryInfo info) {
                    notifyContentChanged();
                }
            };
}
