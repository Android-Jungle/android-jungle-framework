package com.jungle.apps.photos.module.search.data;

import com.jungle.simpleorm.BaseEntity;
import com.jungle.simpleorm.constraint.PrimaryKey;

import java.util.Comparator;

public class SearchHistoryEntity extends BaseEntity {

    @PrimaryKey
    public String mSearchKey;
    public int mSearchCount;

    public SearchHistoryEntity() {
    }

    public SearchHistoryEntity(String searchKey) {
        mSearchKey = searchKey;
        mSearchCount = 1;
    }

    public static final Comparator<SearchHistoryEntity> mComparator =
            new Comparator<SearchHistoryEntity>() {
                @Override
                public int compare(SearchHistoryEntity lhs, SearchHistoryEntity rhs) {
                    if (lhs.mSearchCount > rhs.mSearchCount) {
                        return -1;
                    } else if (lhs.mSearchCount < rhs.mSearchCount) {
                        return 1;
                    }

                    return 0;
                }
            };
}
