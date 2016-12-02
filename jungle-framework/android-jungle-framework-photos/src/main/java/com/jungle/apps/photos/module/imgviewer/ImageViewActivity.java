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

package com.jungle.apps.photos.module.imgviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.app.PhotoBaseActivity;
import com.jungle.apps.photos.base.component.AppUtils;
import com.jungle.apps.photos.base.manager.FileDownloadRequest;
import com.jungle.apps.photos.base.manager.HttpRequestManager;
import com.jungle.apps.photos.module.category.data.manager.CategoryManager;
import com.jungle.apps.photos.module.category.provider.CategoryContentProvider;
import com.jungle.apps.photos.module.category.provider.CategoryProviderManager;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteEntity;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteManager;
import com.jungle.apps.photos.module.imgviewer.widget.ImageOperateLayoutView;
import com.jungle.apps.photos.module.imgviewer.widget.ImageViewerLayoutView;
import com.jungle.base.manager.ThreadManager;
import com.jungle.share.ShareInfo;
import com.jungle.widgets.dialog.JungleToast;

public class ImageViewActivity extends PhotoBaseActivity {

    private static final String PROVIDER_ID = "ProviderId";
    private static final String SELECTED_ID = "SelectedId";
    private static final String IMAGE_VIEW_AD_ID = "16TLevUaApZiYNUOGRq5689i";
    private static final float AD_SHOW_RATE = 0.5f;


    public static void startImageViewActivity(Context context, int providerId) {
        Intent intent = new Intent(context, ImageViewActivity.class);
        intent.putExtra(PROVIDER_ID, providerId);
        context.startActivity(intent);
    }


    public static void startImageViewActivity(Context context, int providerId, String seledId) {
        Intent intent = new Intent(context, ImageViewActivity.class);
        intent.putExtra(PROVIDER_ID, providerId);
        intent.putExtra(SELECTED_ID, seledId);
        context.startActivity(intent);
    }


    private View mImageBlurView;
    private ImageButton mDownloadBtn;
    private ImageButton mFavoriteBtn;
    private FrameLayout mImageViewContainer;
    private ImageViewerLayoutView mImgViewerLayout;
    private ImageOperateLayoutView mImgOperateLayout;
    private CategoryContentProvider mContentProvider;
    private GestureDetector mDetector;
    private int mTransparentColor = 0;
    private int mGrayOverlayColor = 0;
    private boolean mIsSelectItemFirstChanged = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setOverlayToolbar(true);

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTitle(R.string.iamge_viewer_title);
        showTitleIcon(false);
        showRightZone(true);
        setContentView(R.layout.activity_image_view);
        setToolbarBackground(R.color.theme_purple);

        initView();
        initProvider();
    }

    private void initView() {
        mTransparentColor = getResources().getColor(
                android.R.color.transparent);
        mGrayOverlayColor = getResources().getColor(
                R.color.gray_overlay_color);

        mImageBlurView = findViewById(R.id.image_operate_blur_view);
        mImageViewContainer = (FrameLayout) findViewById(R.id.image_view_container);
        mImgViewerLayout = (ImageViewerLayoutView) findViewById(R.id.image_viewer_layout);
        mImgOperateLayout = (ImageOperateLayoutView) findViewById(R.id.image_operate_layout);

        mImgOperateLayout.setVisibilityListener(
                new ImageOperateLayoutView.OnVisibilityListener() {
                    @Override
                    public void onShow() {
                        mImageBlurView.setBackgroundColor(mGrayOverlayColor);
                    }

                    @Override
                    public void onHide() {
                        mImageBlurView.setBackgroundColor(mTransparentColor);
                    }
                });

        mImgOperateLayout.hideOperateLayoutWithoutAnim();

        LinearLayout rightZoneView = getCustomizedToolbar().getRightZoneView();
        View rightZone = View.inflate(this,
                R.layout.view_image_viewer_rightzone, rightZoneView);
        mDownloadBtn = (ImageButton) rightZone.findViewById(R.id.title_download_btn);
        mFavoriteBtn = (ImageButton) rightZone.findViewById(R.id.title_fav_btn);
        mDownloadBtn.setOnClickListener(mDownloadListener);
        mFavoriteBtn.setOnClickListener(mFavImageListener);

        mDetector = new GestureDetector(this, mGestureListener);
        mImgViewerLayout.setOnCustomTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mContentProvider.removeEventListener(mContentListener);
    }

    private void initProvider() {
        Intent intent = getIntent();
        String selectedId = intent.getStringExtra(SELECTED_ID);
        int providerId = intent.getIntExtra(PROVIDER_ID,
                CategoryProviderManager.INVALID_PROVIDER_ID);
        mContentProvider = CategoryProviderManager.getInstance().getProvider(providerId);
        mContentProvider.addEventListener(mContentListener);

        FavoriteManager.getInstance().addFavoriteEventListener(mFavoriteEventListener);

        mImgViewerLayout.setItemListener(mImageSelectListener);
        mImgViewerLayout.initImageViewer(mContentProvider, selectedId);
    }

    @Override
    protected boolean handleKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mImgOperateLayout.isShowing()) {
                switchOperateLayout();
                return true;
            }
        }

        return super.handleKeyUp(keyCode, event);
    }

    private FavoriteManager.OnFavoriteEventListener mFavoriteEventListener =
            new FavoriteManager.OnFavoriteEventListener() {
                private void notifyChanged() {
                    ThreadManager.getInstance().executeOnUIHandler(new Runnable() {
                        @Override
                        public void run() {
                            updateCurrFavoriteState();
                            updateDownloadState();
                            mImgViewerLayout.notifyItemDataSetChanged();
                        }
                    });
                }

                @Override
                public void onAddFavorite(String id) {
                    JungleToast.makeText(getContext(), R.string.favorite_success).show();
                    notifyChanged();
                }

                @Override
                public void onCancelFavorite(String id) {
                    notifyChanged();
                }

                @Override
                public void onClearFavorite() {
                    notifyChanged();
                }

                @Override
                public void onCountUpdated() {
                    notifyChanged();
                }
            };

    private GestureDetector.SimpleOnGestureListener mGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    switchOperateLayout();
                    return super.onSingleTapConfirmed(e);
                }
            };

    private void switchOperateLayout() {
        boolean showing = mImgOperateLayout.isShowing();

        if (mImgOperateLayout.isShowing()) {
            mImgOperateLayout.hideOperateLayout();
        } else {
            mImgOperateLayout.showOperateLayout(getShareInfo());
        }
    }

    private boolean isCurrFavorite() {
        CategoryManager.CategoryItem item = mImgViewerLayout.getCurrentItem();
        return item != null && FavoriteManager.getInstance().isFavorite(item.mId);
    }

    private void updateCurrFavoriteState() {
        mFavoriteBtn.setImageResource(isCurrFavorite()
                ? R.drawable.favorited : R.drawable.unfavorited);
    }

    private boolean isCurrDownloaded() {
        if (!isCurrFavorite()) {
            return false;
        }

        CategoryManager.CategoryItem item = mImgViewerLayout.getCurrentItem();
        if (item == null) {
            return false;
        }

        FavoriteEntity entity = FavoriteManager.getInstance().getFavoriteEntity(item.mId);
        return entity.localFileExist();
    }

    private void updateDownloadState() {
        if (!isCurrFavorite()) {
            mDownloadBtn.setVisibility(View.GONE);
            return;
        }

        mDownloadBtn.setVisibility(View.VISIBLE);
        mDownloadBtn.setImageResource(isCurrDownloaded()
                ? R.drawable.downloaded_to_local
                : R.drawable.undownload_to_local);
    }

    private void fetchMore() {
        if (!mContentProvider.isSupportFetchMore()) {
            return;
        }

        if (!mContentProvider.isCanFetchMore()) {
            JungleToast.makeText(getContext(), R.string.no_more_pic).show();
        } else {
            mContentProvider.fetchMore();
        }
    }

    private ImageViewerLayoutView.OnItemSelectListener mImageSelectListener =
            new ImageViewerLayoutView.OnItemSelectListener() {
                @Override
                public void onSelectedItemChanged(int position) {
                    if (mIsSelectItemFirstChanged) {
                        mIsSelectItemFirstChanged = false;
                    }

                    mImgOperateLayout.hideOperateLayout();
                    updateCurrFavoriteState();
                    updateDownloadState();

                    CategoryManager.CategoryItem item = mImgViewerLayout.getCurrentItem();
                    if (item != null && !TextUtils.isEmpty(item.mTitle)) {
                        setTitle(item.mTitle);
                    } else {
                        setTitle(mContentProvider.getDefaultCategoryName());
                    }

                    int count = mImgViewerLayout.getCurrItemCount();
                    if (count < 2 || position >= count - 2) {
                        fetchMore();
                    }
                }
            };

    private CategoryContentProvider.OnListener mContentListener =
            new CategoryContentProvider.OnListener() {
                @Override
                public void onContentChanged() {
                    mImgViewerLayout.notifyItemDataSetChanged();
                }

                @Override
                public void onFetchFailed() {
                }
            };

    private View.OnClickListener mFavImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CategoryManager.CategoryItem item = mImgViewerLayout.getCurrentItem();
            if (item == null) {
                return;
            }

            Context context = getContext();
            if (isCurrFavorite()) {
                if (!FavoriteManager.getInstance().cancelFavorite(item.mId)) {
                    JungleToast.makeText(context, R.string.cancel_favorite_failed).show();
                } else {
                    JungleToast.makeText(context, R.string.cancel_favorite_success).show();
                    updateCurrFavoriteState();
                    updateDownloadState();
                }
            } else {
                FavoriteManager.getInstance().addFavorite(context, item, mFavoriteListener);
            }

            if (mImgOperateLayout.isShowing()) {
                switchOperateLayout();
            }
        }
    };

    private View.OnClickListener mDownloadListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isCurrDownloaded()) {
                JungleToast.makeText(getContext(), R.string.download_succeeded).show();
            } else {
                downloadCurrentImage();
            }

            if (mImgOperateLayout.isShowing()) {
                switchOperateLayout();
            }
        }
    };

    private void downloadCurrentImage() {
        final CategoryManager.CategoryItem item = mImgViewerLayout.getCurrentItem();
        if (item == null) {
            return;
        }

        String favDir = AppUtils.getFavouritePicFile(item.mId);
        FileDownloadRequest request = new FileDownloadRequest(item.mSrcUrl, favDir,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FavoriteEntity entity = FavoriteManager.getInstance().getFavoriteEntity(item.mId);
                        entity.mLocalPath = response;

                        FavoriteManager.getInstance().synchronizeEntity(entity);
                        JungleToast.makeText(getContext(), R.string.download_succeeded).show();

                        updateDownloadState();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JungleToast.makeText(getContext(), R.string.download_failed).show();
                    }
                });
        HttpRequestManager.getInstance().add(request);
    }

    private ShareInfo getShareInfo() {
        CategoryManager.CategoryItem item = mImgViewerLayout.getCurrentItem();
        if (item == null) {
            return null;
        }

        ShareInfo shareInfo = new ShareInfo();
        shareInfo.mTitle = getString(R.string.share_title);
        shareInfo.mSummary = item.mTitle;
        shareInfo.mImageUrl = item.mSrcUrl;
        shareInfo.mShareUrl = item.mSrcUrl;
        shareInfo.mLocalPath = item.mLocalPath;

        return shareInfo;
    }

    private FavoriteManager.OnFavoriteListener mFavoriteListener =
            new FavoriteManager.OnFavoriteListener() {

                private boolean isSameItem(String id) {
                    CategoryManager.CategoryItem item = mImgViewerLayout.getCurrentItem();
                    return item != null && TextUtils.equals(item.mId, id);
                }

                @Override
                public void onFavoriteSuccess(String id) {
                }

                @Override
                public void onFavoriteFailed(String id) {
                    if (isSameItem(id)) {
                        JungleToast.makeText(getContext(), R.string.favorite_failed).show();
                    }
                }

                @Override
                public void onFavoriteCanceled(String id) {
                    if (isSameItem(id)) {
                        JungleToast.makeText(getContext(), R.string.favorite_canceled).show();
                    }
                }
            };
}
