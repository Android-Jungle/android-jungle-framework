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

package com.jungle.widgets.actionsheet;

import android.content.Context;
import android.view.View;
import com.jungle.imageloader.R;

import java.util.ArrayList;
import java.util.List;

public class ActionSheetUtils {

    public static ActionSheet createActionSheet(Context context) {
        return new ActionSheet(context);
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    public static Builder newBuilderWithCancelItemWithColorRes(
            Context context, final int colorResId) {
        return new Builder(context) {
            @Override
            public ActionSheet build() {
                addItem(R.string.cancel)
                        .setTextColorResId(colorResId)
                        .setMatchParent(true)
                        .setSeparate(true);

                return super.build();
            }
        };
    }

    public static Builder newBuilderWithCancelItemWithColorStateRes(
            Context context, final int textColorStateId) {
        return new Builder(context) {
            @Override
            public ActionSheet build() {
                addItem(R.string.cancel)
                        .setTextColorStateResId(textColorStateId)
                        .setMatchParent(true)
                        .setSeparate(true);

                return super.build();
            }
        };
    }

    public static Builder newBuilderWithCancelItem(Context context) {
        return newBuilderWithCancelItemWithColorStateRes(context, 0);
    }


    public static class Builder {

        private Context mContext;
        private List<ActionSheet.ActionSheetItem> mSheetItems = new ArrayList<>();
        private ActionSheet mActionSheet;


        private Builder(Context context) {
            mContext = context;
            mActionSheet = createActionSheet(mContext);
        }

        public ActionSheet getActionSheet() {
            return mActionSheet;
        }

        public ActionSheet.ActionSheetItem addItem() {
            ActionSheet.ActionSheetItem item = new ActionSheet.ActionSheetItem(mContext);
            mSheetItems.add(item);
            return item;
        }

        public ActionSheet.ActionSheetItem addItem(int textResId) {
            return addItem(mContext.getString(textResId));
        }

        public ActionSheet.ActionSheetItem addItem(String text) {
            ActionSheet.ActionSheetItem item = new ActionSheet.ActionSheetItem(mContext);
            mSheetItems.add(item);

            return item.setText(text);
        }

        public ActionSheet.ActionSheetItem addItem(int textResId,
                ActionSheet.OnItemClickedListener listener) {
            return addItem(mContext.getString(textResId, listener));
        }

        public ActionSheet.ActionSheetItem addItem(String text,
                ActionSheet.OnItemClickedListener listener) {
            ActionSheet.ActionSheetItem item = new ActionSheet.ActionSheetItem(mContext);
            mSheetItems.add(item);

            return item.setText(text).setClickedListener(listener);
        }

        public ActionSheet.ActionSheetItem addCustomizedView(int viewResId) {
            ActionSheet.ActionSheetItem item = new ActionSheet.ActionSheetItem(mContext);
            mSheetItems.add(item);

            return item.setCustomizedView(viewResId);
        }

        public ActionSheet.ActionSheetItem addCustomizedView(View view) {
            ActionSheet.ActionSheetItem item = new ActionSheet.ActionSheetItem(mContext);
            mSheetItems.add(item);

            return item.setCustomizedView(view);
        }

        public Builder setNoPadding() {
            mActionSheet.setRootPadding(0, 0, 0, 0);
            return this;
        }

        public ActionSheet build() {
            for (ActionSheet.ActionSheetItem item : mSheetItems) {
                mActionSheet.addItem(item);
            }

            mActionSheet.setCancelable(true);
            mActionSheet.setCanceledOnTouchOutside(true);
            return mActionSheet;
        }
    }
}
