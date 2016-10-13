/*
 * Copyright (C) 2015. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2015/08/19
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
