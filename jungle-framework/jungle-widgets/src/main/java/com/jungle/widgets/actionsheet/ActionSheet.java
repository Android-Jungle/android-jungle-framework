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

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jungle.imageloader.R;

public class ActionSheet extends Dialog {

    public interface OnItemClickedListener {
        void onItemClicked(View actionSheetItem);
    }


    public static class ActionSheetItem {
        public static final int INVALID_RES_ID = -1;


        public ActionSheetItem(Context context) {
            mContext = context;
        }

        public ActionSheetItem setText(String text) {
            mText = text;
            return this;
        }

        public ActionSheetItem setDescText(String descText) {
            mDescText = descText;
            return this;
        }

        public ActionSheetItem setEnabled(boolean enabled) {
            mEnable = enabled;
            return this;
        }

        public ActionSheetItem setSeparate(boolean isSeparateView) {
            mIsSeparateView = isSeparateView;
            return this;
        }

        public ActionSheetItem setIconDrawable(Drawable iconDrawable) {
            mIconDrawable = iconDrawable;
            return this;
        }

        public ActionSheetItem setCustomizedView(View view) {
            mCustomizedView = view;
            return this;
        }

        public ActionSheetItem setText(int textResId) {
            setText(mContext.getString(textResId));
            return this;
        }

        public ActionSheetItem setDescText(int descTextResId) {
            setDescText(mContext.getString(descTextResId));
            return this;
        }

        public ActionSheetItem setIconDrawable(int iconDrawableResId) {
            setIconDrawable(mContext.getResources().getDrawable(iconDrawableResId));
            return this;
        }

        public ActionSheetItem setTextColorResId(int colorResId) {
            mTextColorResId = colorResId;
            return this;
        }

        public ActionSheetItem setTextColorStateResId(int colorStateResId) {
            mTextColorStateResId = colorStateResId;
            return this;
        }

        public ActionSheetItem setBackgroundResId(int backgroundResId) {
            mBackgroundResId = backgroundResId;
            return this;
        }

        public ActionSheetItem setCustomizedView(int viewResId) {
            View view = View.inflate(mContext, viewResId, null);
            return setCustomizedView(view);
        }

        public ActionSheetItem setClickedListener(OnItemClickedListener listener) {
            mClickedListener = listener;
            return this;
        }

        public ActionSheetItem setMatchParent(boolean isMatchParent) {
            mIsMatchParent = isMatchParent;
            return this;
        }

        private Context mContext;
        private Drawable mIconDrawable;
        private String mText;
        private String mDescText;
        private View mCustomizedView;
        private int mTextColorResId;
        private int mTextColorStateResId;
        private int mBackgroundResId;
        private OnItemClickedListener mClickedListener;
        private boolean mIsSeparateView = false;
        private boolean mIsMatchParent = false;
        private boolean mEnable = true;
    }


    private Context mContext;
    private LinearLayout mRootView;
    private LinearLayout mCurrentItemsContainer;
    private boolean mIsMatchParent;


    public ActionSheet(Context context) {
        super(context, R.style.Jungle_Style_ActionSheet);

        initLayout(context);
    }

    private void initLayout(Context context) {
        mContext = context;
        setContentView(R.layout.dialog_action_sheet);

        mRootView = (LinearLayout) findViewById(R.id.action_sheet_root);
    }

    @Override
    public void show() {
        updateLayoutParams();

        super.show();
    }

    private void updateLayoutParams() {
        Window wnd = getWindow();
        WindowManager.LayoutParams params = wnd.getAttributes();
        if (mIsMatchParent) {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;

        } else {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }

        params.gravity = Gravity.BOTTOM;
        wnd.setAttributes(params);
        wnd.setWindowAnimations(R.style.Jungle_Style_ActionSheet_Anim);
    }

    ActionSheet addItem(ActionSheetItem item) {
        if (item.mIsSeparateView) {
            ensureAddSeparateItem(item.mIsMatchParent);
        } else {
            ensureAddItem(item.mIsMatchParent);
        }

        if (item.mCustomizedView != null) {
            addCustomizedViewInternal(item);
        } else {
            addItemInternal(item);
        }

        return this;
    }

    public void setRootPadding(int left, int top, int right, int bottom) {
        mRootView.setPadding(left, top, right, bottom);
    }

    public void setMatchParent(boolean isMatchParent) {
        mIsMatchParent = isMatchParent;

        ViewGroup.LayoutParams params = mRootView.getLayoutParams();
        params.height = mIsMatchParent
                ? ViewGroup.LayoutParams.MATCH_PARENT
                : ViewGroup.LayoutParams.WRAP_CONTENT;
        mRootView.setLayoutParams(params);
    }

    private void ensureAddItem(boolean isMatchParent) {
        if (mCurrentItemsContainer == null) {
            mCurrentItemsContainer = addNewSeparateContainer(false, isMatchParent);
        }
    }

    private void ensureAddSeparateItem(boolean isMatchParent) {
        if (mCurrentItemsContainer == null) {
            mCurrentItemsContainer = addNewSeparateContainer(false, isMatchParent);
        } else {
            mCurrentItemsContainer = addNewSeparateContainer(true, isMatchParent);
        }
    }

    private void addCustomizedViewInternal(ActionSheetItem item) {
        if (item.mBackgroundResId != 0) {
            mCurrentItemsContainer.setBackgroundResource(item.mBackgroundResId);
        }

        int height = item.mIsMatchParent
                ? ViewGroup.LayoutParams.MATCH_PARENT
                : ViewGroup.LayoutParams.WRAP_CONTENT;
        mCurrentItemsContainer.addView(
                item.mCustomizedView,
                ViewGroup.LayoutParams.MATCH_PARENT, height);
    }

    private void addItemInternal(final ActionSheetItem item) {
        handlePreItemSeparateLine();

        final View newItemView = View.inflate(getContext(),
                R.layout.layout_actionsheet_item, null);
        mCurrentItemsContainer.addView(newItemView);
        if (item.mBackgroundResId != 0) {
            mCurrentItemsContainer.setBackgroundResource(item.mBackgroundResId);
        }

        ImageView iconImgView = (ImageView) newItemView.findViewById(R.id.actionsheet_icon);
        TextView textView = (TextView) newItemView.findViewById(R.id.actionsheet_text);
        TextView descTextView = (TextView) newItemView.findViewById(R.id.actionsheet_desc);
        View descPlaceHoldView = newItemView.findViewById(R.id.place_holder_view);
        View descTextViewContainer = newItemView.findViewById(R.id.actionsheet_desc_container);

        if (item.mIconDrawable != null) {
            iconImgView.setImageDrawable(item.mIconDrawable);
            iconImgView.setVisibility(View.VISIBLE);
            descPlaceHoldView.setVisibility(View.VISIBLE);

            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            descTextView.setGravity(Gravity.LEFT);
        } else {
            iconImgView.setVisibility(View.GONE);
            descPlaceHoldView.setVisibility(View.GONE);

            textView.setGravity(Gravity.CENTER);
            descTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        newItemView.setEnabled(item.mEnable);
        newItemView.setClickable(item.mEnable);
        textView.setText(item.mText);
        if (item.mTextColorResId != 0) {
            textView.setTextColor(mContext.getResources().getColor(item.mTextColorResId));
        } else if (item.mTextColorStateResId != 0) {
            textView.setTextColor(mContext.getResources().getColorStateList(item.mTextColorStateResId));
        } else {
            textView.setTextColor(mContext.getResources().getColor(item.mEnable
                    ? R.color.actionsheet_item_text_normal_color
                    : R.color.actionsheet_item_text_disable_color));
        }

        if (!TextUtils.isEmpty(item.mDescText)) {
            descTextView.setText(item.mDescText);
            descTextViewContainer.setVisibility(View.VISIBLE);
        } else {
            descTextViewContainer.setVisibility(View.GONE);
        }

        newItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                if (item.mClickedListener != null) {
                    item.mClickedListener.onItemClicked(newItemView);
                }
            }
        });
    }

    private void handlePreItemSeparateLine() {
        int count = mCurrentItemsContainer.getChildCount();
        if (count <= 0) {
            return;
        }

        View preItemView = mCurrentItemsContainer.getChildAt(count - 1);
        View separateLineView = preItemView.findViewById(R.id.actionsheet_divide_line);
        if (separateLineView != null) {
            separateLineView.setVisibility(View.VISIBLE);
        }
    }

    private LinearLayout addNewSeparateContainer(
            boolean isSeparateContainer, boolean isMatchParent) {
        LinearLayout container = (LinearLayout) View.inflate(mContext,
                R.layout.layout_actionsheet_item_container, null);

        int height = isMatchParent
                ? ViewGroup.LayoutParams.MATCH_PARENT
                : ViewGroup.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        if (isSeparateContainer) {
            params.topMargin = mContext.getResources().getDimensionPixelSize(
                    R.dimen.actionsheet_separate_container_margin);
        }

        mRootView.addView(container, params);
        return container;
    }
}
