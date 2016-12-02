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

package com.jungle.apps.photos.module.category.data.manager;

import android.text.TextUtils;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jungle.apps.photos.base.component.WeakEventListener;
import com.jungle.apps.photos.base.manager.HttpRequestManager;
import com.jungle.apps.photos.module.category.data.CategoryStrategy;
import com.jungle.apps.photos.module.category.provider.CategoryContentProvider;
import com.jungle.apps.photos.module.category.provider.CategoryProviderManager;
import com.jungle.apps.photos.module.category.provider.FetchContentProvider;
import com.jungle.base.manager.AppManager;
import com.jungle.base.manager.ThreadManager;
import com.jungle.base.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CategoryManager implements AppManager {

    public static enum MgrType {
        ForNormal(1),
        ForSearch(2);

        public int mRawType;

        private MgrType(int rawType) {
            mRawType = rawType;
        }

        public static MgrType fromRawType(int rawType) {
            if (rawType == ForNormal.mRawType) {
                return ForNormal;
            } else if (rawType == ForSearch.mRawType) {
                return ForSearch;
            }

            return null;
        }
    }


    public static interface OnFetchResultListener {
        void onSuccess(int fetchedCount, CategoryInfo info);

        void onError(CategoryInfo info);
    }


    public static interface OnFetchEventListener {
        void onCategoryUpdated(CategoryInfo info);
    }


    public static enum CategoryType {
        Fetched,
        Favorite,
    }


    public static class CategoryItem {
        public String mId;
        public String mTitle;
        public String mThumbUrl;
        public String mSrcUrl;
        public String mLocalPath;
    }


    public static class CategoryInfo
            extends WeakEventListener<OnFetchEventListener> {

        public CategoryInfo(String category, String tag) {
            mCategory = category;
            mTag = tag;

            clear();
        }

        public CategoryType mCategoryType = CategoryType.Fetched;
        public String mCategory;
        public String mTag;
        public ArrayList<CategoryItem> mCategoryItems = new ArrayList<>();

        int mCurrFetchIndex;
        int mTotalCount;

        public boolean isEnd() {
            return mTotalCount != -1 && mCurrFetchIndex >= mTotalCount;
        }

        public void clear() {
            mCurrFetchIndex = 0;
            mTotalCount = -1;
            mCategoryItems.clear();
        }

        public void notifyUpdatedEvent() {
            for (WeakReference<OnFetchEventListener> ref : getList()) {
                OnFetchEventListener listener = ref.get();
                if (listener != null) {
                    listener.onCategoryUpdated(this);
                } else {
                    removeEventListener(ref);
                }
            }
        }
    }


    protected Map<String, CategoryInfo> mCategoryList = new HashMap<>();


    public abstract MgrType getCategoryType();

    protected abstract CategoryInfo createDefaultCategoryInfo(String category, String key);

    protected abstract CategoryParser getCategoryParser();

    protected abstract String generateFetchUrl(
            String category, String key, int fetchIndex, int count);

    @Override
    public void onCreate() {
    }

    @Override
    public void onTerminate() {
    }

    public Map<String, CategoryInfo> getCategoryList() {
        return mCategoryList;
    }

    public CategoryInfo getCategoryInfo(String key) {
        return mCategoryList.get(key);
    }

    public CategoryInfo addCategoryInfo(String category, String key) {
        CategoryInfo info = mCategoryList.get(key);
        if (info == null) {
            info = createDefaultCategoryInfo(category, key);
            mCategoryList.put(key, info);
        }

        return info;
    }

    public int getCategoryProvider(String category, final String key) {
        CategoryProviderManager mgr = CategoryProviderManager.getInstance();
        int providerId = mgr.findProvider(new CategoryProviderManager.Finder() {
            @Override
            public boolean isFind(CategoryContentProvider provider) {
                return TextUtils.equals(key, provider.getCategoryInfo().mTag);
            }
        });

        if (providerId == CategoryProviderManager.INVALID_PROVIDER_ID) {
            CategoryInfo info = addCategoryInfo(category, key);
            FetchContentProvider provider = new FetchContentProvider(info, this, key);
            providerId = CategoryProviderManager.getInstance().addProvider(provider);
        }

        return providerId;
    }

    public void fetchCategory(String category, String key, int count,
            OnFetchResultListener listener) {
        fetchCategory(category, key, -1, count, listener);
    }

    public void fetchCategory(
            String category, String key, int startIndex, int count,
            OnFetchResultListener listener) {

        int realFetchIndex = 0;
        int realFetchCount = count;

        CategoryInfo info = addCategoryInfo(category, key);
        if (startIndex == -1) {
            realFetchIndex = info.mCurrFetchIndex;
        } else if (info.isEnd()) {
            listener.onSuccess(0, info);
            return;

        } else {
            int realNeedCount = startIndex + count;
            if (realNeedCount <= info.mCurrFetchIndex) {
                listener.onSuccess(0, info);
                return;
            } else {
                realFetchCount = realNeedCount - info.mCurrFetchIndex;
            }
        }

        String url = generateFetchUrl(
                category, key, realFetchIndex, realFetchCount);
        if (TextUtils.isEmpty(url)) {
            listener.onError(info);
            return;
        }

        fetchCategoryInternal(getCategoryParser(), info, url, listener);
    }

    protected static class CategoryParser {

        public CategoryParser() {
        }

        public List<CategoryItem> parseCategory(JSONObject json, CategoryInfo info) {
            int max = JsonUtils.safeGetInt(json, "displayNum");
            info.mTotalCount = Math.max(max, info.mTotalCount);

            List<CategoryItem> resultList = new ArrayList<>();
            JSONArray items = JsonUtils.safeGetArray(json, "data");
            if (items != null) {
                parseCategoryArray(items, resultList);
            }

            return resultList;
        }
    }

    private static int parseCategoryArray(
            JSONArray items, List<CategoryItem> resultList) {

        int fetchedCount = 0;
        int itemCount = items.length();

        for (int i = 0; i < itemCount; ++i) {
            Object obj = null;
            try {
                obj = items.get(i);
                if (obj == null) {
                    continue;
                }

                if (obj instanceof JSONArray) {
                    JSONArray arr = (JSONArray) obj;
                    fetchedCount += parseCategoryArray(arr, resultList);
                } else if (obj instanceof JSONObject) {
                    JSONObject itemInfo = (JSONObject) obj;
                    CategoryItem item = parseCategoryItem(itemInfo);

                    if (item != null) {
                        ++fetchedCount;
                        resultList.add(item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return fetchedCount;
    }

    private static CategoryItem parseCategoryItem(JSONObject itemInfo) {
        CategoryItem item = new CategoryItem();
        item.mTitle = JsonUtils.safeGetString(itemInfo, "fromPageTitleEnc");
        item.mThumbUrl = JsonUtils.safeGetString(itemInfo, "thumbURL");

        if (itemInfo.has("replaceUrl")) {
            try {
                JSONArray array = itemInfo.getJSONArray("replaceUrl");
                item.mSrcUrl = array.getJSONObject(0).getString("ObjURL");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(item.mSrcUrl)) {
            item.mSrcUrl = JsonUtils.safeGetString(itemInfo, "middleURL");
        }

        item.mId = CategoryStrategy.generateImageId(item.mSrcUrl);
        if (TextUtils.isEmpty(item.mThumbUrl)) {
            item.mThumbUrl = item.mSrcUrl;
        }

        return item;
    }

    protected void fetchCategoryInternal(
            final CategoryParser parser, final CategoryInfo info,
            String url, final OnFetchResultListener listener) {

        if (TextUtils.isEmpty(url)) {
            listener.onError(info);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final List<CategoryItem> resultList = parser.parseCategory(response, info);

                        ThreadManager.getInstance().postOnUIHandler(new Runnable() {
                            @Override
                            public void run() {
                                int fetchedCount = resultList.size();
                                info.mCurrFetchIndex += fetchedCount;
                                info.mCategoryItems.addAll(resultList);

                                if (fetchedCount > 0) {
                                    info.notifyUpdatedEvent();
                                }

                                listener.onSuccess(fetchedCount, info);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ThreadManager.getInstance().postOnUIHandler(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(info);
                            }
                        });
                    }
                });

        HttpRequestManager.getInstance().add(request);
    }
}
