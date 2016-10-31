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
