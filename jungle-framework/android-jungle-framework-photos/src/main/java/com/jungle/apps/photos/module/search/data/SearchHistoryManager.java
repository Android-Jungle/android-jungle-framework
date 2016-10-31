package com.jungle.apps.photos.module.search.data;

import android.text.TextUtils;
import com.jungle.apps.photos.base.manager.PhotosEntityManager;
import com.jungle.simpleorm.supporter.ORMSupporter;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryManager {

    private List<SearchHistoryEntity> mSearchHistoryList = null;

    private static ORMSupporter getORMSupporter() {
        return PhotosEntityManager.getInstance().getGlobalORMSupporter();
    }

    public void loadHistory() {
        mSearchHistoryList = getORMSupporter().query(SearchHistoryEntity.class);
        if (mSearchHistoryList == null) {
            mSearchHistoryList = new ArrayList<SearchHistoryEntity>();
        }
    }

    public void saveHistory() {
        ORMSupporter supporter = getORMSupporter();
        for (SearchHistoryEntity entity : mSearchHistoryList) {
            supporter.update(entity);
        }
    }

    public void clearHistory() {
        mSearchHistoryList.clear();
        getORMSupporter().removeAll(SearchHistoryEntity.class);
    }

    public List<SearchHistoryEntity> getHistoryList() {
        return mSearchHistoryList;
    }

    public SearchHistoryEntity getHistoryEntity(int index) {
        if (index >= 0 && index < mSearchHistoryList.size()) {
            return mSearchHistoryList.get(index);
        }

        return null;
    }

    public void addHistoryItem(SearchHistoryEntity entity) {
        mSearchHistoryList.add(entity);
        getORMSupporter().insertNew(entity);
    }

    public void updateHistoryItem(SearchHistoryEntity entity) {
        getORMSupporter().update(entity);
    }

    public boolean removeHistoryItem(String searchKey) {
        ORMSupporter supporter = getORMSupporter();
        SearchHistoryEntity removeEntity = new SearchHistoryEntity();
        removeEntity.mSearchKey = searchKey;
        supporter.remove(removeEntity);

        for (SearchHistoryEntity entity : mSearchHistoryList) {
            if (TextUtils.equals(entity.mSearchKey, searchKey)) {
                mSearchHistoryList.remove(entity);
                return true;
            }
        }

        return false;
    }

    public static void addHistoryItem(String searchKey) {
        ORMSupporter supporter = getORMSupporter();
        SearchHistoryEntity entity = supporter.queryByPrimary(SearchHistoryEntity.class, searchKey);
        if (entity != null) {
            ++entity.mSearchCount;
        } else {
            entity = new SearchHistoryEntity(searchKey);
        }

        supporter.update(entity);
    }
}
