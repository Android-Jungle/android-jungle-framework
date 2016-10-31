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

package com.jungle.apps.photos.module.settings;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.app.PhotoBaseActivity;
import com.jungle.apps.photos.base.component.AppUtils;
import com.jungle.apps.photos.module.favorite.data.pic.FavoriteManager;
import com.jungle.base.manager.ThreadManager;
import com.jungle.base.utils.FileUtils;
import com.jungle.widgets.dialog.DialogUtils;
import com.jungle.widgets.dialog.JungleDialog;
import com.jungle.widgets.dialog.JungleToast;

public class SettingActivity extends PhotoBaseActivity {

    private static final String APP_WALL_ID = "1040903263124366";
    private static final int DEVELOPMENT_TRIGGER_COUNT = 5;


    private ToggleButton mDownloadImgWhenFavBtn;
    private SettingItemView mClearCacheItem;
    private SettingItemView mClearFavoritesItem;
    private SettingItemView mViewFavoritePathItem;
    private int mDevelopmentTriggerCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.setting_item_text);
        setContentView(R.layout.activity_settings);

        mClearCacheItem = (SettingItemView) findViewById(R.id.clear_cache);
        mClearFavoritesItem = (SettingItemView) findViewById(R.id.clear_favorites);
        mViewFavoritePathItem = (SettingItemView) findViewById(R.id.view_favorite_path);
        mDownloadImgWhenFavBtn = (ToggleButton) findViewById(R.id.setting_enable_switch);

        initItemViews();
    }

    private void initItemViews() {
        updateDownloadWhenFavPicBtn();
        updateCacheSizeView();
        updateFavoritesSizeView();

        mDownloadImgWhenFavBtn.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        AppUtils.saveDownloadWhenFavPicBtn(isChecked);
                    }
                });

        mClearCacheItem.setItemClickAction(new SettingItemView.OnSettingItemClickListener() {
            @Override
            public void onItemClicked(SettingItemView settingItemView) {
                clearCache();
            }
        });

        mClearFavoritesItem.setItemClickAction(new SettingItemView.OnSettingItemClickListener() {
            @Override
            public void onItemClicked(SettingItemView settingItemView) {
                DialogUtils.createDialog(SettingActivity.this,
                        R.string.clear_favorites_confirm,
                        R.string.cancel,
                        R.string.ok,
                        JungleDialog.mDismissListener,
                        new JungleDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(JungleDialog dialog, JungleDialog.DialogBtn which) {
                                clearFavorites();
                                dialog.dismiss();
                            }
                        }).setMsgMaxLines(2).show();
            }
        });

        mViewFavoritePathItem.setItemClickAction(new SettingItemView.OnSettingItemClickListener() {
            @Override
            public void onItemClicked(SettingItemView settingItemView) {
                String wording = String.format(
                        getString(R.string.favorite_path_wording),
                        AppUtils.getFavouriteDirectory());
                JungleToast.makeText(SettingActivity.this, wording).show();
            }
        });
    }

    private void updateDownloadWhenFavPicBtn() {
        mDownloadImgWhenFavBtn.setChecked(AppUtils.isDownloadWhenFavPic());
    }

    private void updateCacheSizeView() {
        ThreadManager.getInstance().getFileHandler().post(new Runnable() {
            @Override
            public void run() {
                final String cacheSizeDesc = AppUtils.getCacheTextDescription();

                ThreadManager.getInstance().postOnUIHandler(new Runnable() {
                    @Override
                    public void run() {
                        TextView cacheText = (TextView) mClearCacheItem
                                .findViewById(R.id.setting_cache_size_text);
                        cacheText.setText(cacheSizeDesc);
                    }
                });
            }
        });
    }

    private void updateFavoritesSizeView() {
        ThreadManager.getInstance().getFileHandler().post(new Runnable() {
            @Override
            public void run() {
                long favoritesSize = FileUtils.getDirectorySize(
                        AppUtils.getFavouriteDirectory());
                final String favoritesSizeDesc = FileUtils.getSizeTextDescription(
                        favoritesSize);

                ThreadManager.getInstance().postOnUIHandler(new Runnable() {
                    @Override
                    public void run() {
                        TextView favoritesText = (TextView) mClearFavoritesItem
                                .findViewById(R.id.setting_favorites_size_text);
                        int favoritesCount = FavoriteManager.getInstance().getFavoritesCount();

                        favoritesText.setText(String.format(
                                getString(R.string.favorite_desc_format),
                                favoritesSizeDesc,
                                favoritesCount));
                    }
                });
            }
        });
    }

    private void clearCache() {
        AppUtils.cleanAppCacheInBackground(new Runnable() {
            @Override
            public void run() {
                JungleToast.makeText(getContext(), R.string.clean_cache_complete).show();
                updateCacheSizeView();
            }
        });
    }

    private void clearFavorites() {
        ThreadManager.getInstance().getFileHandler().post(new Runnable() {
            @Override
            public void run() {
                FileUtils.cleanDirectory(AppUtils.getFavouriteDirectory());
                FavoriteManager.getInstance().clearFavorites();

                ThreadManager.getInstance().postOnUIHandler(new Runnable() {
                    @Override
                    public void run() {
                        JungleToast.makeText(getContext(), R.string.clean_favorites_complete).show();
                        updateFavoritesSizeView();
                    }
                });
            }
        });
    }
}
