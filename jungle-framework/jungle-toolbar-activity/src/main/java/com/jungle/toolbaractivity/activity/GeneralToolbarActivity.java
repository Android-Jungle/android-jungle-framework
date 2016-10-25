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

package com.jungle.toolbaractivity.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import com.jungle.toolbaractivity.R;
import com.jungle.toolbaractivity.toolbar.GeneralToolBar;

public class GeneralToolbarActivity
        extends JungleSwipeBackBaseActivity<GeneralToolBar> {

    private GeneralToolBar mGeneralToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDefaultToolbar();
    }

    @Override
    protected GeneralToolBar createCustomizedToolbar() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mGeneralToolBar = (GeneralToolBar) inflater.inflate(
                R.layout.layout_general_toolbar, mAppBarLayout, false);
        return mGeneralToolBar;
    }

    private void initDefaultToolbar() {
        setTitleClickable(true);

        mGeneralToolBar.setActionBarListener(
                new GeneralToolBar.OnActionBarListener() {
                    @Override
                    public void onBackButtonClicked() {
                        handleBackButtonClicked();
                    }

                    @Override
                    public void onTitleClicked() {
                        handleTitleClicked();
                    }

                    @Override
                    public void onTitleIconClicked() {
                    }
                });
    }

    protected void handleBackButtonClicked() {
        finish();
    }

    protected void handleTitleClicked() {
        finish();
    }

    public void showBackButton(boolean show) {
        mGeneralToolBar.showBackButton(show);
    }

    public void showTitleIcon(boolean show) {
        mGeneralToolBar.showTitleIcon(show);
    }

    public void showTitle(boolean show) {
        mGeneralToolBar.showTitle(show);
    }

    public void showTitleZone(boolean show) {
        mGeneralToolBar.showTitleZone(show);
    }

    public void showRightZone(boolean show) {
        mGeneralToolBar.showRightZone(show);
    }

    public void setTitle(String title) {
        mGeneralToolBar.setTitle(title);
    }

    public void setTitle(int titleResId) {
        mGeneralToolBar.setTitle(titleResId);
    }

    public void setTitleIcon(int titleIconResId) {
        mGeneralToolBar.setTitleIcon(titleIconResId);
    }

    public void setTitleClickable(boolean clickable) {
        mGeneralToolBar.setTitleClickable(clickable);
    }
}
