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

package com.jungle.widgets.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jungle.widgets.R;

public class JungleLoadingLayout extends BaseLoadingLayout {

    public JungleLoadingLayout(Context context) {
        super(context);

        init(context, null);
    }

    public JungleLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public JungleLoadingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.layout_loading_page_view, this);
        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(
                    attrs, R.styleable.JungleLoadingLayout);

            // Btn Background.
            Drawable btnBkg = arr.getDrawable(R.styleable.JungleLoadingLayout_btnBackground);
            if (btnBkg != null) {
                setBtnBackground(btnBkg);
            }

            // Loading Page.
            Drawable loadingDrawable = arr.getDrawable(R.styleable.JungleLoadingLayout_loadingImgDrawable);
            if (loadingDrawable != null) {
                setLoadingDrawable(loadingDrawable);
            }

            int topMargin = arr.getDimensionPixelSize(
                    R.styleable.JungleLoadingLayout_loadingTopMargin, -1);
            setLoadingTopMargin(topMargin);

            int descResId = arr.getResourceId(
                    R.styleable.JungleLoadingLayout_loadingDesc, -1);
            String loadingDesc = descResId != -1
                    ? context.getString(descResId)
                    : arr.getString(R.styleable.JungleLoadingLayout_loadingDesc);
            if (loadingDesc != null) {
                setLoadingDesc(loadingDesc);
            }

            // Empty Page.
            Drawable emptyImg = arr.getDrawable(
                    R.styleable.JungleLoadingLayout_emptyImg);
            if (emptyImg != null) {
                setEmptyImage(emptyImg);
            }

            descResId = arr.getResourceId(
                    R.styleable.JungleLoadingLayout_emptyDesc, -1);
            String emptyDesc = descResId != -1
                    ? context.getString(descResId)
                    : arr.getString(R.styleable.JungleLoadingLayout_emptyDesc);
            if (emptyDesc != null) {
                setEmptyDescription(emptyDesc);
            }

            descResId = arr.getResourceId(
                    R.styleable.JungleLoadingLayout_emptyButtonText, -1);
            String emptyBtnText = descResId != -1
                    ? context.getString(descResId)
                    : arr.getString(R.styleable.JungleLoadingLayout_emptyButtonText);
            if (emptyBtnText != null) {
                setEmptyButtonText(emptyBtnText);
            }

            boolean showEmptyBtn = arr.getBoolean(
                    R.styleable.JungleLoadingLayout_showEmptyBtn, false);
            setShowEmptyBtn(showEmptyBtn);

            // Loading Failed Page.
            Drawable loadingFailedImg = arr.getDrawable(
                    R.styleable.JungleLoadingLayout_loadingFailedImg);
            if (loadingFailedImg != null) {
                setLoadingFailedImg(loadingFailedImg);
            }

            descResId = arr.getResourceId(
                    R.styleable.JungleLoadingLayout_loadingFailedDesc, -1);
            String loadingFailedDesc = descResId != -1
                    ? context.getString(descResId)
                    : arr.getString(R.styleable.JungleLoadingLayout_loadingFailedDesc);
            if (loadingFailedDesc != null) {
                setLoadingFailedDesc(loadingFailedDesc);
            }

            descResId = arr.getResourceId(
                    R.styleable.JungleLoadingLayout_loadingFailedButtonText, -1);
            String loadingFailedBtnText = descResId != -1
                    ? context.getString(descResId)
                    : arr.getString(R.styleable.JungleLoadingLayout_loadingFailedButtonText);
            if (loadingFailedBtnText != null) {
                setLoadingFailedButtonText(loadingFailedBtnText);
            }

            int failedTopMargin = arr.getDimensionPixelSize(
                    R.styleable.JungleLoadingLayout_loadingFailedTopMargin, -1);
            setLoadingFailedTopMargin(failedTopMargin);

            arr.recycle();
        }

        setPageState(PageState.Invisible);

        if (isInEditMode()) {
            return;
        }

        setClickable(true);
        findViewById(R.id.loading_failed_btn).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mReloadListener != null && mPageState == PageState.LoadingFailed) {
                            setPageState(PageState.Loading);
                            mReloadListener.onNeedReload();
                        }
                    }
                });

        findViewById(R.id.empty_btn).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mEmptyBtnListener != null) {
                            mEmptyBtnListener.onEmptyBtnClicked();
                        }
                    }
                });

        setPageState(PageState.Invisible);
    }

    public void setLoadingDrawable(Drawable drawable) {
        findViewById(R.id.loading_img_view).setBackgroundDrawable(drawable);
    }

    public void setShowEmptyBtn(boolean showEmptyBtn) {
        findViewById(R.id.empty_btn).setVisibility(showEmptyBtn ? View.VISIBLE : View.GONE);
    }

    public void setBtnBackground(Drawable drawable) {
        findViewById(R.id.empty_btn).setBackgroundDrawable(drawable);
        findViewById(R.id.loading_failed_btn).setBackgroundDrawable(drawable);
    }

    public void setLoadingFailedImg(Drawable drawable) {
        ImageView loadingFailedImg = (ImageView)
                findViewById(R.id.loading_failed_img_view);
        loadingFailedImg.setImageDrawable(drawable);
    }

    @Override
    protected void updateLoadingPage(boolean loading) {
        View loadingImgView = findViewById(R.id.loading_img_view);
        Drawable drawable = loadingImgView.getBackground();
        if (drawable == null) {
            return;
        }

        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) drawable;
            if (loading) {
                animation.start();
            } else {
                animation.stop();
            }
        } else {
            if (loading) {
                Animation animation = AnimationUtils.loadAnimation(
                        getContext(), R.anim.loading_rotate_anim);
                animation.setFillAfter(true);
                loadingImgView.startAnimation(animation);
            } else {
                loadingImgView.clearAnimation();
            }
        }
    }

    @Override
    protected void updatePages() {
        View emptyPage = findViewById(R.id.empty_desc_page);
        View loadingPage = findViewById(R.id.loading_desc_page);
        View loadingFailedPage = findViewById(R.id.loading_failed_view);

        emptyPage.setVisibility(View.GONE);
        loadingPage.setVisibility(View.GONE);
        loadingFailedPage.setVisibility(View.GONE);

        if (mPageState == PageState.Loading) {
            loadingPage.setVisibility(View.VISIBLE);
        } else if (mPageState == PageState.Empty) {
            emptyPage.setVisibility(View.VISIBLE);
        } else if (mPageState == PageState.LoadingFailed) {
            loadingFailedPage.setVisibility(View.VISIBLE);
        }
    }

    public void setLoadingTopMargin(int topMargin) {
        View loadingPage = findViewById(R.id.loading_desc_page);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) loadingPage.getLayoutParams();
        if (topMargin != -1) {
            params.topMargin = topMargin;
            params.addRule(RelativeLayout.CENTER_VERTICAL, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.topMargin = getResources().getDimensionPixelSize(
                    R.dimen.loading_layout_top_margin);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        loadingPage.setLayoutParams(params);
    }

    public void setLoadingFailedTopMargin(int topMargin) {
        View loadingFailedPage = findViewById(R.id.loading_failed_view);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) loadingFailedPage.getLayoutParams();
        if (topMargin != -1) {
            params.topMargin = topMargin;
            params.addRule(RelativeLayout.CENTER_VERTICAL, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            params.topMargin = getResources().getDimensionPixelSize(
                    R.dimen.loading_layout_top_margin);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }

        loadingFailedPage.setLayoutParams(params);
    }

    public void setEmptyImage(Drawable drawable) {
        ImageView img = (ImageView) findViewById(R.id.empty_page_image);
        img.setImageDrawable(drawable);
    }

    public void setEmptyDescription(String desc) {
        TextView descView = (TextView) findViewById(R.id.empty_page_description);
        descView.setText(desc);
    }

    public void setEmptyButtonText(String desc) {
        TextView descView = (TextView) findViewById(R.id.empty_btn);
        descView.setText(desc);
    }

    public void setEmptyImage(int resId) {
        setEmptyImage(getResources().getDrawable(resId));
    }

    public void setEmptyDescription(int resId) {
        setEmptyDescription(getResources().getString(resId));
    }

    public void setEmptyButtonText(int resId) {
        setEmptyButtonText(getResources().getString(resId));
    }

    public void setLoadingDesc(String desc) {
        TextView descView = (TextView) findViewById(R.id.loading_description_view);
        descView.setText(desc);
    }

    public void setLoadingFailedDesc(String desc) {
        TextView descView = (TextView) findViewById(R.id.loading_failed_page_description);
        descView.setText(desc);
    }

    public void setLoadingFailedButtonText(String desc) {
        TextView descView = (TextView) findViewById(R.id.loading_failed_btn);
        descView.setText(desc);
    }

    public void setLoadingFailedDesc(int resId) {
        setLoadingFailedDesc(getResources().getString(resId));
    }

    public void setLoadingFailedButtonText(int resId) {
        setLoadingFailedButtonText(getResources().getString(resId));
    }
}
