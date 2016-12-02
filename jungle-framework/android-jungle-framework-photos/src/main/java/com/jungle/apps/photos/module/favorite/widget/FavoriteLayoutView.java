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

package com.jungle.apps.photos.module.favorite.widget;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.component.AppUtils;
import com.jungle.apps.photos.base.manager.FileDownloadRequest;
import com.jungle.apps.photos.base.manager.HttpRequestManager;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteEntity;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteManager;
import com.jungle.apps.photos.module.imgviewer.ImageViewActivity;
import com.jungle.base.app.AppCore;
import com.jungle.base.app.BaseActivity;
import com.jungle.base.app.LifeCycleListener;
import com.jungle.base.manager.ThreadManager;
import com.jungle.base.utils.FileUtils;
import com.jungle.base.utils.ImageUtils;
import com.jungle.base.utils.MiscUtils;
import com.jungle.imageloader.ImageLoaderUtils;
import com.jungle.widgets.dialog.DialogUtils;
import com.jungle.widgets.dialog.JungleDialog;
import com.jungle.widgets.dialog.JungleToast;
import com.jungle.widgets.loading.JungleLoadingLayout;

import java.util.Set;

public class FavoriteLayoutView extends FrameLayout {

    public static interface FavoriteLayoutListener {
        void onFavoriteChanged();
    }


    private FavoriteAdapter mAdapter;
    private ListView mListView;
    private JungleLoadingLayout mLoadingPageView;
    private FavoriteLayoutListener mFavoriteListener;


    public FavoriteLayoutView(Context context) {
        super(context);
        initLayout(context);
    }

    public FavoriteLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public FavoriteLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_favorite_view, this);

        mListView = (ListView) findViewById(R.id.favorite_list_view);
        mLoadingPageView = (JungleLoadingLayout) findViewById(R.id.loading_page);

        mAdapter = new FavoriteAdapter();
        mListView.setAdapter(mAdapter);

        FavoriteManager.getInstance().addFavoriteEventListener(
                mFavoriteEventListener);

        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            activity.addLifeCycleListener(new LifeCycleListener() {
                @Override
                protected void onDestroy(BaseActivity activity) {
                    FavoriteManager.getInstance().removeFavoriteEventListener(
                            mFavoriteEventListener);
                }
            });
        }

        updateLoadingView();

        // 同步列表.
        FavoriteManager.getInstance().synchronizeList();
    }

    private void updateLoadingView() {
        mLoadingPageView.setPageState(mAdapter.getCount() == 0
                ? JungleLoadingLayout.PageState.Empty
                : JungleLoadingLayout.PageState.Invisible);
    }

    public void setFavoriteLayoutListener(FavoriteLayoutListener listener) {
        mFavoriteListener = listener;
    }

    public int getFavoriteCount() {
        return mAdapter.getCount();
    }

    private class ItemHolder {
        private ImageView mItemImg;
        private TextView mItemTitle;
        private TextView mItemFavTime;
        private TextView mItemSize;
        private FavoriteEntity mEntity;

        ItemHolder(View view) {
            mItemImg = (ImageView) view.findViewById(R.id.item_image);
            mItemTitle = (TextView) view.findViewById(R.id.item_title);
            mItemFavTime = (TextView) view.findViewById(R.id.item_fav_time);
            mItemSize = (TextView) view.findViewById(R.id.item_size);
        }

        void updateItem(FavoriteEntity entity) {
            mEntity = entity;
            if (entity == null) {
                return;
            }

            Bitmap bmp = null;
            if (entity.localFileExist()) {
                Resources res = AppCore.getApplicationContext().getResources();
                bmp = ImageUtils.decodeSampledBitmapFromFile(
                        entity.mLocalPath,
                        res.getDimensionPixelSize(R.dimen.fav_item_image_width),
                        res.getDimensionPixelSize(R.dimen.fav_item_image_height));
            }

            if (bmp != null) {
                mItemImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mItemImg.setImageBitmap(bmp);
            } else {
                mItemImg.setScaleType(ImageView.ScaleType.FIT_XY);
                mItemImg.setImageResource(R.drawable.small_image_view_loading);
                ImageLoaderUtils.displayImage(mItemImg, entity.mSrcUrl);
            }

            mItemTitle.setText(entity.mTitle);
            mItemFavTime.setText(MiscUtils.formatDateDesc(entity.mFavTime));

            updateSize(entity);
        }

        void updateSize(FavoriteEntity entity) {
            String sizeDesc = null;
            if (entity.localFileExist()) {
                sizeDesc = FileUtils.getSizeTextDescription(
                        FileUtils.getDirectorySize(entity.mLocalPath));
            } else {
                sizeDesc = getContext().getString(R.string.not_download_to_local);
            }

            mItemSize.setText(sizeDesc);
        }
    }

    private class FavoriteAdapter extends BaseAdapter {
        @Override
        public void notifyDataSetChanged() {
            if (mFavoriteListener != null) {
                mFavoriteListener.onFavoriteChanged();
            }

            ThreadManager.getInstance().executeOnUIHandler(new Runnable() {
                @Override
                public void run() {
                    FavoriteAdapter.super.notifyDataSetChanged();
                    updateLoadingView();
                }
            });
        }

        @Override
        public int getCount() {
            return FavoriteManager.getInstance().getFavoritesCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder = null;

            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.layout_favorite_item, null);
                holder = new ItemHolder(convertView);

                convertView.setTag(holder);
                convertView.setOnClickListener(mItemClickListener);
                convertView.setOnLongClickListener(mItemLongClickListener);

                convertView.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                convertView.playSoundEffect(SoundEffectConstants.CLICK);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }

            FavoriteEntity entity = FavoriteManager.getInstance().getFavoriteEntity(position);
            holder.updateItem(entity);

            return convertView;
        }
    }

    private void showFavoriteItemMenu(final ItemHolder holder, final FavoriteEntity entity) {
        Context context = getContext();
        final JungleDialog dialog = DialogUtils.createFullyCustomizedDialog(
                context, R.layout.dialog_favorite_item_menu);

        TextView title = (TextView) dialog.findViewById(R.id.item_title);
        title.setText(entity.mTitle);

        int visibility = entity.localFileExist() ? View.GONE : View.VISIBLE;
        View downloadView = dialog.findViewById(R.id.download_to_local);
        downloadView.setVisibility(visibility);
        dialog.findViewById(R.id.download_to_local_divide_line).setVisibility(visibility);

        dialog.findViewById(R.id.cancel_favorite).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                cancelFavorite(entity);
            }
        });

        dialog.findViewById(R.id.view_favorite).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                viewFavorite(entity);
            }
        });

        downloadView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                downloadFavImage(holder, entity);
            }
        });

        dialog.setCustomizedBackground(new ColorDrawable(
                context.getResources().getColor(R.color.white)));
        dialog.show();
    }

    private void downloadFavImage(final ItemHolder holder, final FavoriteEntity entity) {
        MiscUtils.requestRuntimePermission(
                (Activity) getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE,
                new MiscUtils.OnPermissionRequestListener() {
                    @Override
                    public void onResult(Set<String> grantedPermissions) {
                        String favDir = AppUtils.getFavouritePicFile(entity.mId);
                        FileDownloadRequest request = new FileDownloadRequest(entity.mSrcUrl, favDir,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        entity.mLocalPath = response;
                                        FavoriteManager.getInstance().synchronizeEntity(entity);
                                        JungleToast.makeText(getContext(), R.string.download_succeeded).show();
                                        holder.updateSize(entity);
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
                });
    }

    private void cancelFavorite(final FavoriteEntity entity) {
        Context context = getContext();
        DialogUtils.createDialog(context,
                R.string.cancel_favorite_confirm,
                R.string.cancel,
                R.string.ok,
                JungleDialog.mDismissListener,
                new JungleDialog.OnDialogBtnClickListener() {
                    @Override
                    public void onClick(JungleDialog dialog, JungleDialog.DialogBtn which) {
                        FavoriteManager.getInstance().cancelFavorite(entity.mId);
                        dialog.dismiss();
                    }
                }).show();
    }

    private OnClickListener mItemClickListener =
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemHolder holder = (ItemHolder) v.getTag();
                    if (holder.mEntity == null) {
                        return;
                    }

                    viewFavorite(holder.mEntity);
                }
            };

    private OnLongClickListener mItemLongClickListener =
            new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ItemHolder holder = (ItemHolder) v.getTag();
                    if (holder.mEntity == null) {
                        return false;
                    }

                    showFavoriteItemMenu(holder, holder.mEntity);
                    return true;
                }
            };

    private void viewFavorite(FavoriteEntity entity) {
        int providerId = FavoriteManager.getInstance().getProviderId();
        ImageViewActivity.startImageViewActivity(
                getContext(), providerId, entity.mId);
    }

    private FavoriteManager.OnFavoriteEventListener mFavoriteEventListener =
            new FavoriteManager.OnFavoriteEventListener() {
                @Override
                public void onAddFavorite(String id) {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelFavorite(String id) {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onClearFavorite() {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCountUpdated() {
                    mAdapter.notifyDataSetChanged();
                }
            };
}
