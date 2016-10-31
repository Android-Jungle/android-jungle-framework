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

package com.jungle.apps.photos.module.search.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.widget.CategoryTagItemLongClickListener;
import com.jungle.apps.photos.module.misc.ColorList;
import com.jungle.apps.photos.module.search.data.SearchHistoryEntity;
import com.jungle.apps.photos.module.search.data.SearchHistoryManager;
import com.jungle.base.manager.ThreadManager;
import com.jungle.widgets.dialog.JungleDialog;
import com.jungle.widgets.layout.FlowLayout;

import java.util.Collections;

public class SearchLayoutView extends FrameLayout {

    public static interface OnSearchListener {
        void onSearch(String searchKey);
    }


    private OnSearchListener mSearchListener;
    private FlowLayout mSearchContainer;
    private View mClearHistoryView;
    private SearchHistoryManager mHistoryMgr = new SearchHistoryManager();

    public SearchLayoutView(Context context) {
        super(context);

        initLayout(context);
    }

    public SearchLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initLayout(context);
    }

    public SearchLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_search_content, this);
        mSearchContainer = (FlowLayout) findViewById(R.id.search_history_container);
        mClearHistoryView = findViewById(R.id.clear_history_text);
        mClearHistoryView.setOnClickListener(
                mClearHistoryClickListener);

        ThreadManager.getInstance().postOnUIHandler(new Runnable() {
            @Override
            public void run() {
                mHistoryMgr.loadHistory();
                reloadData();
            }
        });
    }

    public void setSearchListener(OnSearchListener l) {
        mSearchListener = l;
    }

    private void reloadData() {
        Collections.sort(mHistoryMgr.getHistoryList(), SearchHistoryEntity.mComparator);

        Resources res = getContext().getResources();
        int horz = res.getDimensionPixelSize(R.dimen.search_tag_horz_space);
        int vert = res.getDimensionPixelSize(R.dimen.search_tag_vert_space);

        mClearHistoryView.setVisibility(mHistoryMgr.getHistoryList().isEmpty()
                ? View.GONE : View.VISIBLE);

        mSearchContainer.removeAllViews();
        for (SearchHistoryEntity entity : mHistoryMgr.getHistoryList()) {
            TextView view = (TextView) View.inflate(getContext(), R.layout.view_search_tag, null);
            view.setText(entity.mSearchKey);
            view.setTag(entity);
            view.setTextColor(ColorList.randomNextColor());
            view.setOnClickListener(mItemViewClickListener);
            view.setOnLongClickListener(mItemViewLongClickListener);
            view.performHapticFeedback(
                    HapticFeedbackConstants.LONG_PRESS,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            view.playSoundEffect(SoundEffectConstants.CLICK);

            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params.leftMargin = horz;
            params.rightMargin = horz;
            params.topMargin = vert;
            params.bottomMargin = vert;

            mSearchContainer.addView(view, params);
        }
    }

    private void removeTagFromList(String tag) {
        if (mHistoryMgr.removeHistoryItem(tag)) {
            reloadData();
        }
    }

    private OnClickListener mItemViewClickListener =
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchHistoryEntity entity = (SearchHistoryEntity) v.getTag();
                    entity.mSearchCount += 1;
                    mHistoryMgr.updateHistoryItem(entity);

                    if (mSearchListener != null) {
                        mSearchListener.onSearch(entity.mSearchKey);
                    }
                }
            };

    private OnLongClickListener mItemViewLongClickListener = new CategoryTagItemLongClickListener() {

        @Override
        protected String getCategory(View v) {
            return getTag(v);
        }

        @Override
        protected String getTag(View v) {
            SearchHistoryEntity entity = (SearchHistoryEntity) v.getTag();
            if (entity != null) {
                return entity.mSearchKey;
            }

            return null;
        }

        @Override
        protected int getDialogResId() {
            return R.layout.dialog_search_tag_menu;
        }

        @Override
        protected void initItems(final JungleDialog dialog, final String tag) {
            dialog.findViewById(R.id.remove_tag_from_list).setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            removeTagFromList(tag);
                        }
                    });
        }
    };

    private OnClickListener mClearHistoryClickListener =
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHistoryMgr.clearHistory();
                    reloadData();
                }
            };
}
