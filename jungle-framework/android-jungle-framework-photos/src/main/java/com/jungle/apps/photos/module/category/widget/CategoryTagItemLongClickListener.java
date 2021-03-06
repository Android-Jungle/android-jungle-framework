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

package com.jungle.apps.photos.module.category.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.CategoryActivity;
import com.jungle.apps.photos.module.category.data.manager.SearchCategoryManager;
import com.jungle.apps.photos.module.favorite.data.tag.FavoriteTagManager;
import com.jungle.widgets.dialog.DialogUtils;
import com.jungle.widgets.dialog.JungleDialog;
import com.jungle.widgets.dialog.JungleToast;

public abstract class CategoryTagItemLongClickListener implements View.OnLongClickListener {

    protected abstract String getCategory(View v);

    protected abstract String getTag(View v);

    protected int getDialogResId() {
        return R.layout.dialog_hot_tag_menu;
    }

    protected void initItems(JungleDialog dialog, String tag) {
    }

    @Override
    public boolean onLongClick(View v) {
        final String category = getCategory(v);
        final String tag = getTag(v);

        if (TextUtils.isEmpty(tag)) {
            return false;
        }

        final Context context = v.getContext();
        final JungleDialog dialog = DialogUtils.createFullyCustomizedDialog(
                context, getDialogResId());

        TextView title = (TextView) dialog.findViewById(R.id.tag_title);
        title.setText(tag);

        TextView switchView = (TextView) dialog.findViewById(
                R.id.switch_favorite_tag);
        boolean isFavorited = FavoriteTagManager.getInstance().isTagFavorited(tag);
        switchView.setText(isFavorited
                ? context.getString(R.string.cancel_favorite_tag)
                : context.getString(R.string.add_to_favorite_tag));

        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                FavoriteTagManager mgr = FavoriteTagManager.getInstance();
                if (mgr.isTagFavorited(tag)) {
                    if (!mgr.removeFavoriteTag(tag)) {
                        JungleToast.makeText(context, R.string.cancel_favorite_tag_failed).show();
                    }
                } else {
                    if (!mgr.addFavoriteTag(tag)) {
                        JungleToast.makeText(context, R.string.add_to_favorite_tag_failed).show();
                    }
                }
            }
        });

        dialog.findViewById(R.id.view_favorite_tag).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        viewTagWithCategory(context, category, tag);
                    }
                });

        initItems(dialog, tag);

        dialog.show();
        return true;
    }

    private void viewTagWithCategory(Context context, String category, String tag) {
        CategoryActivity.startCategoryActivity(
                context, tag,
                SearchCategoryManager.getInstance().getCategoryProvider(category, tag));
    }
}
