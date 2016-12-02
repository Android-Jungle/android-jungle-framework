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

package com.jungle.apps.photos.module.homepage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.favorite.FavoriteActivity;
import com.jungle.apps.photos.module.favorite.data.tag.FavoriteTagManager;
import com.jungle.apps.photos.module.homepage.widget.HomepageTabIndicatorView;
import com.jungle.apps.photos.module.homepage.widget.HomepageToolbar;
import com.jungle.apps.photos.module.homepage.widget.category.CategoryDisplayLayoutView;
import com.jungle.apps.photos.module.homepage.widget.hot.HotLayoutView;
import com.jungle.apps.photos.module.misc.AboutActivity;
import com.jungle.apps.photos.module.settings.SettingActivity;
import com.jungle.base.app.AppCore;
import com.jungle.toolbaractivity.activity.JungleBaseActivity;
import com.jungle.widgets.dialog.JungleDialog;
import com.jungle.widgets.dialog.JungleToast;
import com.jungle.widgets.view.AdjustBoundsImageView;
import com.jungle.widgets.view.TabPageIndicator;
import com.jungle.widgets.view.TabPageIndicatorView;

public class HomepageActivity extends JungleBaseActivity<HomepageToolbar> {

    public static void start(Context context) {
        Intent intent = new Intent(context, HomepageActivity.class);
        context.startActivity(intent);
    }


    private long mLastBackKeyUpTime = 0;
    private HomepageAdapter mAdapter;
    private ViewPager mViewPager;
    private TabPageIndicator mTabIndicator;
    private DrawerLayout mDrawerLayout;
    private HomepageToolbar mToolbar;
    private ImageView mNavigateView;
    private JungleDialog mLoginDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        initActionBarInternal();
        initDrawer();
        initPager();

        FavoriteTagManager.getInstance().fetchFavoriteTags();
    }

    @Override
    protected HomepageToolbar createCustomizedToolbar() {
        LayoutInflater inflater = LayoutInflater.from(this);
        return (HomepageToolbar) inflater.inflate(
                R.layout.toolbar_homepage, mAppBarLayout, false);
    }

    private void initActionBarInternal() {
        mToolbar = getCustomizedToolbar();
        mNavigateView = (ImageView) mToolbar.findViewById(R.id.navigate_view);
        AdjustBoundsImageView iconView = (AdjustBoundsImageView) mToolbar.findViewById(R.id.title_icon);
        iconView.setImageResource(R.drawable.title_icon);

        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDrawer();
            }
        });
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.homepage_drawer);
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                mNavigateView.setImageResource(R.drawable.expand);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mNavigateView.setImageResource(R.drawable.collapse);
            }
        });

        findViewById(R.id.setting_item).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeDrawer();
                        startActivityInternal(SettingActivity.class);
                    }
                });

        findViewById(R.id.about_item).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeDrawer();
                        startActivityInternal(AboutActivity.class);
                    }
                });

        findViewById(R.id.my_favorite_item).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeDrawer();
                        startActivityInternal(FavoriteActivity.class);
                    }
                });
    }

    private void initPager() {
        mTabIndicator = (TabPageIndicator) findViewById(R.id.homepage_tab_indicator);
        mViewPager = (ViewPager) findViewById(R.id.homepage_view_pager);

        mAdapter = new HomepageAdapter();
        mViewPager.setAdapter(mAdapter);
        mTabIndicator.setViewPager(mViewPager);
        mTabIndicator.setAdapter(mIndicatorAdapter);
    }

    private void switchDrawer() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    private void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.START);
        mNavigateView.setImageResource(R.drawable.expand);
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawers();
        mNavigateView.setImageResource(R.drawable.collapse);
    }

    private void exitApp() {
        AppCore.getInstance().finishAll();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long time = System.currentTimeMillis();
            if (time - mLastBackKeyUpTime <= 800) {
                exitApp();
            } else {
                mLastBackKeyUpTime = time;
                JungleToast.makeText(this, R.string.back_press_exit_tips).show();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private static enum HomepageTabInfo {
        Category(R.string.homepage_tab_category_display, 0),
        Hot(R.string.homepage_tab_hot, 1);

        int mTitleResId;
        int mPosition;

        HomepageTabInfo(int resId, int pos) {
            mTitleResId = resId;
            mPosition = pos;
        }

        public static HomepageTabInfo getTabInfoByPosition(int position) {
            if (position == HomepageTabInfo.Category.mPosition) {
                return HomepageTabInfo.Category;
            } else if (position == HomepageTabInfo.Hot.mPosition) {
                return HomepageTabInfo.Hot;
            }

            return null;
        }
    }

    private TabPageIndicator.Adapter mIndicatorAdapter = new TabPageIndicator.Adapter() {
        @Override
        public int getCount() {
            return HomepageTabInfo.values().length;
        }

        @Override
        public TabPageIndicatorView getView(int position) {
            HomepageTabIndicatorView view = new HomepageTabIndicatorView(
                    HomepageActivity.this);

            HomepageTabInfo info = HomepageTabInfo.getTabInfoByPosition(position);
            view.setIndicatorText(info.mTitleResId);
            return view;
        }
    };

    private class HomepageAdapter extends PagerAdapter {

        private CategoryDisplayLayoutView mCategoryLayoutView;
        private HotLayoutView mHotLayoutView;


        @Override
        public int getCount() {
            return HomepageTabInfo.values().length;
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
        public CharSequence getPageTitle(int position) {
            HomepageTabInfo info = HomepageTabInfo.getTabInfoByPosition(position);
            if (info != null) {
                return getString(info.mTitleResId);
            }

            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Context context = HomepageActivity.this;
            View v = null;

            if (position == HomepageTabInfo.Category.ordinal()) {
                if (mCategoryLayoutView == null) {
                    mCategoryLayoutView = new CategoryDisplayLayoutView(context);
                }

                v = mCategoryLayoutView;
            } else if (position == HomepageTabInfo.Hot.ordinal()) {
                if (mHotLayoutView == null) {
                    mHotLayoutView = new HotLayoutView(context);
                }

                v = mHotLayoutView;
            }

            if (v != null) {
                container.addView(v);
            }

            return v;
        }
    }
}
