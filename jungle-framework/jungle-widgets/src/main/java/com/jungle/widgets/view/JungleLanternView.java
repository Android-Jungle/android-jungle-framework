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
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.jungle.base.app.BaseActivity;
import com.jungle.base.app.LifeCycleListener;
import com.jungle.base.manager.ThreadManager;
import com.jungle.widgets.R;
import com.jungle.widgets.layout.FixedSpeedViewPager;

import java.util.HashMap;
import java.util.Map;

public class JungleLanternView extends FrameLayout {

    public interface IndicatorCreator {
        JungleIndexIndicatorView createIndicator(ViewGroup container);
    }

    public interface LanternViewAdapter {
        int getCount();

        View getView(int position);
    }


    private FixedSpeedViewPager mViewPager;
    private JungleIndexIndicatorView mIndicator;
    private LanternViewAdapter mLanternAdapter;
    private ViewPagerAdapter mAdapter;
    private ViewPager.OnPageChangeListener mPageChangeListener;
    private IndicatorCreator mIndicatorCreator;
    private boolean mAutoSwitch = true;
    private boolean mIsDestroyed = false;
    private int mSwitchIntervalMs = 5000;


    public JungleLanternView(Context context) {
        super(context);

        initLayout(context, null);
    }

    public JungleLanternView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initLayout(context, attrs);
    }

    public JungleLanternView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.layout_lantern_view, this);

        mAdapter = new ViewPagerAdapter();
        mViewPager = (FixedSpeedViewPager) findViewById(R.id.lantern_view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setSetItemListener(new FixedSpeedViewPager.OnSetItemListener() {
            @Override
            public void onSetNewItem() {
                scheduleLanternSwitch();
            }
        });

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(
                    attrs, R.styleable.JungleLanternView);
            mAutoSwitch = arr.getBoolean(
                    R.styleable.JungleLanternView_autoScroll, mAutoSwitch);
            mSwitchIntervalMs = arr.getInteger(
                    R.styleable.JungleLanternView_switchIntervalMs, mSwitchIntervalMs);

            arr.recycle();
        }

        mViewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset,
                            int positionOffsetPixels) {
                        if (mPageChangeListener != null) {
                            mPageChangeListener.onPageScrolled(
                                    position, positionOffset, positionOffsetPixels);
                        }
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (!mAdapter.isNeedExtraItem()) {
                            mIndicator.setCurrSeledIndex(position);
                            if (mPageChangeListener != null) {
                                mPageChangeListener.onPageSelected(position);
                            }

                            return;
                        }

                        int index = 0;
                        int rawCount = mAdapter.getRawCount();
                        if (position == rawCount + 1) {
                            index = 0;
                        } else if (position == 0) {
                            index = rawCount - 1;
                        } else {
                            index = position - 1;
                        }

                        mIndicator.setCurrSeledIndex(index);
                        mViewPager.setScrollDurationFactor(1);

                        if (mPageChangeListener != null) {
                            mPageChangeListener.onPageSelected(index);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        if (mPageChangeListener != null) {
                            mPageChangeListener.onPageScrollStateChanged(state);
                        }

                        if (!mAdapter.isNeedExtraItem()) {
                            return;
                        }

                        if (state == ViewPager.SCROLL_STATE_IDLE) {
                            int rawCount = mAdapter.getRawCount();
                            int currItemIndex = mViewPager.getCurrentItem();

                            if (currItemIndex == rawCount + 1) {
                                mViewPager.setCurrentItemManual(1, false);
                            } else if (currItemIndex == 0) {
                                mViewPager.setCurrentItemManual(rawCount, false);
                            }
                        }
                    }
                });

        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            activity.addLifeCycleListener(mLifeCycleListener);
        }
    }

    private LifeCycleListener mLifeCycleListener = new LifeCycleListener() {
        @Override
        protected void onDoFinish(BaseActivity activity) {
            super.onDoFinish(activity);
            doDestroy();
        }
    };

    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    public void setIndicatorCreator(IndicatorCreator creator) {
        mIndicatorCreator = creator;
    }

    public void setPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mPageChangeListener = listener;
    }

    public void showIndicator(boolean show) {
        if (mIndicator != null) {
            mIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void setAdapter(LanternViewAdapter adapter) {
        if (mIndicator == null && mIndicatorCreator != null) {
            mIndicator = mIndicatorCreator.createIndicator(this);
        }

        if (mIndicator == null) {
            View.inflate(getContext(), R.layout.layout_default_lantern_indicator, this);
            mIndicator = (JungleIndexIndicatorView) findViewById(R.id.default_indicator);
        }

        mLanternAdapter = adapter;
        mAdapter.notifyDataSetChanged();

        int count = mAdapter.getRawCount();
        mIndicator.setCount(count);

        if (mAdapter.isNeedExtraItem()) {
            scheduleLanternSwitch();
            mViewPager.setCurrentItem(1);
        } else {
            unScheduleLanternSwitch();
            mViewPager.setCurrentItem(0);
        }
    }

    public void setSwitchInterval(int intervalMs) {
        mSwitchIntervalMs = intervalMs;
    }

    public void doDestroy() {
        if (mIsDestroyed) {
            return;
        }

        unScheduleLanternSwitch();
        mLanternSwitchRunnable = null;
        mIsDestroyed = true;
    }

    private void scheduleLanternSwitch() {
        if (mLanternSwitchRunnable != null) {
            final Handler handler = ThreadManager.getInstance().getUIHandler();
            handler.removeCallbacks(mLanternSwitchRunnable);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handler.postDelayed(mLanternSwitchRunnable, mSwitchIntervalMs);
                }
            });
        }
    }

    private void unScheduleLanternSwitch() {
        ThreadManager.getInstance().getUIHandler().removeCallbacks(
                mLanternSwitchRunnable);
    }

    private class ViewPagerAdapter extends PagerAdapter {

        private Map<Integer, View> mViewList = new HashMap<>();

        private int getRawCount() {
            return mLanternAdapter != null ? mLanternAdapter.getCount() : 0;
        }

        @Override
        public void notifyDataSetChanged() {
            mViewList.clear();
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            int rawCount = getRawCount();
            return isNeedExtraItem() ? rawCount + 2 : rawCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mViewList.get(position);
            if (v == null && mLanternAdapter != null) {
                int realPosition = position;
                if (isNeedExtraItem()) {
                    realPosition = getDataIndexByPosition(position);
                }

                v = mLanternAdapter.getView(realPosition);
                mViewList.put(position, v);
            }

            if (v != null) {
                container.addView(v);
            }

            return v;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        public boolean isNeedExtraItem() {
            return mAutoSwitch && getRawCount() > 1;
        }

        private int getDataIndexByPosition(int position) {
            int itemCount = getRawCount();
            int index = position;

            if (isNeedExtraItem()) {
                if (position == 0) {
                    index = itemCount - 1;
                } else if (position == itemCount + 1) {
                    index = 0;
                } else {
                    index = position - 1;
                }
            }

            return index;
        }
    }

    private Runnable mLanternSwitchRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsDestroyed) {
                return;
            }

            int currItem = mViewPager.getCurrentItem();
            mViewPager.setScrollDurationFactor(5);
            mViewPager.setCurrentItemManual(currItem + 1, true);

            scheduleLanternSwitch();
        }
    };
}
