package com.jungle.apps.photos.module.category.provider;

import android.util.SparseArray;
import com.jungle.base.app.AppCore;
import com.jungle.base.manager.AppManager;

public class CategoryProviderManager implements AppManager {

    public static final int INVALID_PROVIDER_ID = -1;


    public static CategoryProviderManager getInstance() {
        return AppCore.getInstance().getManager(CategoryProviderManager.class);
    }


    public static interface Finder {
        boolean isFind(CategoryContentProvider provider);
    }


    private int mCurrCookie = 0;
    private SparseArray<CategoryContentProvider> mProviderList =
            new SparseArray<CategoryContentProvider>();


    @Override
    public void onCreate() {
    }

    @Override
    public void onTerminate() {
        mProviderList.clear();
    }

    public int addProvider(CategoryContentProvider provider) {
        int providerId = ++mCurrCookie;
        mProviderList.put(providerId, provider);
        provider.setProviderId(providerId);
        return providerId;
    }

    public CategoryContentProvider removeProvider(int providerId) {
        CategoryContentProvider provider = mProviderList.get(providerId);
        mProviderList.remove(providerId);
        return provider;
    }

    public CategoryContentProvider getProvider(int providerId) {
        return mProviderList.get(providerId);
    }

    public int findProvider(Finder finder) {
        int count = mProviderList.size();
        for (int i = 0; i < count; ++i) {
            CategoryContentProvider provider = mProviderList.valueAt(i);
            if (finder.isFind(provider)) {
                return mProviderList.keyAt(i);
            }
        }

        return INVALID_PROVIDER_ID;
    }
}
