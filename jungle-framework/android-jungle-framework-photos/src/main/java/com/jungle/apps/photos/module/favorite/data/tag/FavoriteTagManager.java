package com.jungle.apps.photos.module.favorite.data.tag;

import com.jungle.apps.photos.base.component.WeakEventListener;
import com.jungle.base.app.AppCore;
import com.jungle.base.manager.AppManager;
import com.jungle.simpleorm.supporter.ORMSupporter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class FavoriteTagManager implements AppManager {

    public static FavoriteTagManager getInstance() {
        return AppCore.getInstance().getManager(GlobalFavoriteTagManager.class);
    }


    public static interface OnFavoriteTagListener {
        void onAddFavorite(String tag);

        void onCancelFavorite(String tag);

        void onFavoriteListUpdated();
    }


    protected List<String> mFavoriteTagList;
    private WeakEventListener<OnFavoriteTagListener> mFavoriteEventListener = new WeakEventListener<>();


    protected abstract ORMSupporter getORMSupporter();

    protected abstract List<String> initFavoriteList();

    public abstract boolean addFavoriteTag(String tag);

    public abstract boolean removeFavoriteTag(String tag);

    public abstract void fetchFavoritedTags();


    @Override
    public void onCreate() {
        mFavoriteTagList = initFavoriteList();
        if (mFavoriteTagList == null) {
            mFavoriteTagList = new ArrayList<>();
        }
    }

    @Override
    public void onTerminate() {
    }

    public void addFavoriteListener(OnFavoriteTagListener l) {
        mFavoriteEventListener.addEventListener(l);
    }

    public void removeFavoriteListener(OnFavoriteTagListener l) {
        mFavoriteEventListener.removeEventListener(l);
    }

    public List<String> getFavoriteTagList() {
        return mFavoriteTagList;
    }

    public int getFavoriteTagCount() {
        return mFavoriteTagList.size();
    }

    public boolean isTagFavorited(String tag) {
        return mFavoriteTagList.contains(tag);
    }

    protected void doAddFavoriteTag(String tag) {
        mFavoriteTagList.add(tag);
        notifyAddFavorite(tag);
    }

    protected void doRemoveFavoriteTag(String tag) {
        mFavoriteTagList.remove(tag);
        notifyCancelFavorite(tag);
    }

    protected void notifyAddFavorite(String tag) {
        for (WeakReference<OnFavoriteTagListener> ref : mFavoriteEventListener.getList()) {
            OnFavoriteTagListener listener = ref.get();
            if (listener != null) {
                listener.onAddFavorite(tag);
            }
        }
    }

    protected void notifyCancelFavorite(String tag) {
        for (WeakReference<OnFavoriteTagListener> ref : mFavoriteEventListener.getList()) {
            OnFavoriteTagListener listener = ref.get();
            if (listener != null) {
                listener.onCancelFavorite(tag);
            }
        }
    }

    protected void notifyFavoriteListUpdated() {
        for (WeakReference<OnFavoriteTagListener> ref : mFavoriteEventListener.getList()) {
            OnFavoriteTagListener listener = ref.get();
            if (listener != null) {
                listener.onFavoriteListUpdated();
            }
        }
    }
}
