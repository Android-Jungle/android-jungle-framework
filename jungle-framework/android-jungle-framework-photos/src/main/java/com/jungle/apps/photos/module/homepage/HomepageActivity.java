package com.jungle.apps.photos.module.homepage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.component.PhotoEvent;
import com.jungle.apps.photos.module.favorite.FavoriteActivity;
import com.jungle.apps.photos.module.favorite.data.tag.FavoriteTagManager;
import com.jungle.apps.photos.module.homepage.widget.HomepageTabIndicatorView;
import com.jungle.apps.photos.module.homepage.widget.category.CategoryDisplayLayoutView;
import com.jungle.apps.photos.module.homepage.widget.hot.HotLayoutView;
import com.jungle.apps.photos.module.homepage.widget.personalcenter.HotRecommendLayoutView;
import com.jungle.apps.photos.module.misc.AboutActivity;
import com.jungle.apps.photos.module.settings.SettingActivity;
import com.jungle.base.app.AppCore;
import com.jungle.base.app.BaseActivity;
import com.jungle.base.event.Event;
import com.jungle.base.event.EventListener;
import com.jungle.base.manager.EventManager;
import com.jungle.widgets.dialog.DialogUtils;
import com.jungle.widgets.dialog.JungleDialog;
import com.jungle.widgets.dialog.JungleToast;
import com.jungle.widgets.view.AdjustBoundsImageView;
import com.jungle.widgets.view.TabPageIndicator;
import com.jungle.widgets.view.TabPageIndicatorView;


public class HomepageActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, HomepageActivity.class);
        context.startActivity(intent);
    }


    private long mLastBackKeyUpTime = 0;
    private HomepageAdapter mAdapter;
    private ViewPager mViewPager;
    private TabPageIndicator mTabIndicator;
    private DrawerLayout mDrawerLayout;
    private GeneralActionBar mGeneralActionBar;
    private ImageView mNavigateView;
    private JungleDialog mLoginDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        initActionBarInternal();
        initDrawer();
        initPager();

        FavoriteTagManager.getInstance().fetchFavoritedTags();
        EventManager.getInstance().addListener(
                PhotoEvent.HOT_PIC_UPDATED, mHotPicUpdatedListener);
        EventManager.getInstance().addListener(
                PhotoEvent.HOT_PIC_UPDATED_CLICKED, mHotPicUpdateClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAdapter.mCategoryLayoutView != null) {
            mAdapter.mCategoryLayoutView.doResume();
        }
    }

    private void initActionBarInternal() {
        mGeneralActionBar = getCustomizedActionBar();
        showTitleZone(false);

        FrameLayout customizedZoneView = mGeneralActionBar.getCustomizedZoneView();
        View view = View.inflate(this, R.layout.layout_homepage_actionbar, null);

        customizedZoneView.addView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mNavigateView = (ImageView) view.findViewById(R.id.navigate_view);
        AdjustBoundsImageView iconView = (AdjustBoundsImageView) view.findViewById(R.id.title_icon);
        iconView.setImageResource(R.drawable.title_icon);

        view.setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.user_background_view).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUserInfo(false);
                    }
                });

        findViewById(R.id.user_info_container).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUserInfo(true);
                    }
                });
    }

    private void showUserInfo(boolean needLogin) {
        closeDrawer();

        if (LoginManager.getInstance().isLogin()) {
            PhotoboundUserInfoDisplayActivity.startUserInfoDisplayActivity(
                    HomepageActivity.this);
        } else if (needLogin) {
            doLoginInternal();
        }
    }

    private void initPager() {
        mTabIndicator = (TabPageIndicator) findViewById(
                R.id.homepage_tab_indicator);
        mViewPager = (ViewPager) findViewById(R.id.homepage_view_pager);

        mAdapter = new HomepageAdapter();
        mViewPager.setAdapter(mAdapter);
        mTabIndicator.setViewPager(mViewPager);
        mTabIndicator.setAdapter(mIndicatorAdapter);
        mTabIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == HomepageTabInfo.HotRecommend.mPosition) {
                    showHotRecommendNewIcon(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
    protected boolean handleKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            long time = System.currentTimeMillis();
            if (time - mLastBackKeyUpTime <= 800) {
                exitApp();
            } else {
                mLastBackKeyUpTime = time;
                JungleToast.makeText(this, R.string.back_press_exit_tips).show();
            }

            return true;
        }

        return super.handleKeyUp(keyCode, event);
    }

    private EventListener mHotPicUpdatedListener = new EventListener() {
        @Override
        public void onEvent(Event event, Object data) {
            showHotRecommendNewIcon(true);

            HotRecommendLayoutView recommendView = mAdapter.mHotRecommendLayoutView;
            if (recommendView != null) {
                recommendView.updateRecommend();
            }
        }
    };

    private EventListener mHotPicUpdateClickListener = new EventListener() {
        @Override
        public void onEvent(Event event, Object data) {
            mViewPager.setCurrentItem(HomepageTabInfo.HotRecommend.mPosition);
        }
    };

    private void showHotRecommendNewIcon(boolean show) {
        HomepageTabIndicatorView view = (HomepageTabIndicatorView)
                mTabIndicator.getIndicatorView(HomepageTabInfo.HotRecommend.mPosition);
        view.showNewIcon(show);
    }

    private void dismissLoginDialog() {
        if (mLoginDialog != null) {
            mLoginDialog.dismiss();
            mLoginDialog = null;
        }
    }

    private void doLoginInternal() {
        dismissLoginDialog();

        mLoginDialog = DialogUtils.createFullyCustomizedDialog(
                this, R.layout.dialog_login);

        LoginView loginView = (LoginView) mLoginDialog.findViewById(R.id.login_view);
        loginView.showQQLoginBtn(false);
        loginView.showRegisterAccount(true);

        mLoginDialog.setCustomizedBackground(android.R.color.transparent);
        mLoginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mLoginDialog = null;
            }
        });

        mLoginDialog.show();
    }

    private static enum HomepageTabInfo {
        Category(R.string.homepage_tab_category_display, 0),
        Hot(R.string.homepage_tab_hot, 1),
        HotRecommend(R.string.homepage_hot_recommend, 2);

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
            } else if (position == HomepageTabInfo.HotRecommend.mPosition) {
                return HomepageTabInfo.HotRecommend;
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
        private HotRecommendLayoutView mHotRecommendLayoutView;


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
            } else if (position == HomepageTabInfo.HotRecommend.ordinal()) {
                if (mHotRecommendLayoutView == null) {
                    mHotRecommendLayoutView = new HotRecommendLayoutView(context);
                }

                v = mHotRecommendLayoutView;
            }

            if (v != null) {
                container.addView(v);
            }

            return v;
        }
    }
}
