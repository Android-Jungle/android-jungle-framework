/**
 * Android Jungle framework project.
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

package com.jungle.widgets.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class TabPageIndicator extends LinearLayout {

    public interface Adapter {
        int getCount();

        TabPageIndicatorView getView(int position);
    }


    private Adapter mAdapter;
    private TabPageIndicatorView[] mIndicatorViews;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mPageChangeListener;
    private int mCurrentPosition = -1;


    public TabPageIndicator(Context context) {
        super(context);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        updateLayout();
        updateCurrItemByViewPager();
    }

    public void setViewPager(ViewPager viewPager) {
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }

        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageScrolled(
                            position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageSelected(position);
                }

                setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });

        updateCurrItemByViewPager();
    }

    private void updateCurrItemByViewPager() {
        if (mViewPager == null) {
            return;
        }

        int currPosition = mViewPager.getCurrentItem();
        if (currPosition != mCurrentPosition) {
            setCurrentItem(currPosition);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mPageChangeListener = listener;
    }

    private void updateLayout() {
        removeAllViews();

        if (mAdapter == null) {
            return;
        }

        int count = mAdapter.getCount();
        mIndicatorViews = new TabPageIndicatorView[count];

        for (int i = 0; i < count; ++i) {
            final int position = i;
            TabPageIndicatorView indicatorView = mAdapter.getView(position);

            indicatorView.setClickable(true);
            indicatorView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCurrentItem(position);
                }
            });

            LayoutParams params = getIndicatorLayoutParams();
            addView(indicatorView, params);

            mIndicatorViews[i] = indicatorView;
        }
    }

    private void setCurrentItem(int position) {
        if (mCurrentPosition == position
                || mIndicatorViews == null
                || mIndicatorViews.length == 0) {
            return;
        }

        mCurrentPosition = position;
        for (int i = 0; i < mIndicatorViews.length; ++i) {
            TabPageIndicatorView view = mIndicatorViews[i];
            view.setSelected(mCurrentPosition == i);
        }

        int currPosition = mViewPager.getCurrentItem();
        if (mCurrentPosition != currPosition) {
            mViewPager.setCurrentItem(mCurrentPosition);
        }
    }

    public TabPageIndicatorView getIndicatorView(int position) {
        if (mIndicatorViews == null
                || position < 0
                || position >= mIndicatorViews.length) {
            return null;
        }

        return mIndicatorViews[position];
    }

    private LayoutParams getIndicatorLayoutParams() {
        LayoutParams params = null;
        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        }

        params.weight = 1;
        return params;
    }
}
