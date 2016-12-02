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

package com.jungle.apps.photos.base.app;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.component.AppUtils;
import com.jungle.apps.photos.module.category.CategoryActivity;
import com.jungle.apps.photos.module.category.data.manager.SearchCategoryManager;
import com.jungle.apps.photos.module.search.data.SearchHistoryManager;
import com.jungle.apps.photos.module.search.widget.SearchLayoutView;
import com.jungle.base.manager.ThreadManager;
import com.jungle.toolbaractivity.activity.JungleSwipeBackBaseActivity;

public class PhotoBaseActivity extends JungleSwipeBackBaseActivity<BaseToolbar> {

    protected static final String ACTIVITY_TITLE = "ActivityTitle";


    private View mSearchZoneView;
    private boolean mSearchZoneShow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
    }

    @Override
    protected BaseToolbar createCustomizedToolbar() {
        LayoutInflater inflater = LayoutInflater.from(this);
        return (BaseToolbar) inflater.inflate(
                R.layout.toolbar_base, mAppBarLayout, false);
    }

    protected void showTitleIcon(boolean show) {
    }

    protected void showRightZone(boolean show) {
    }

    protected void showTitle(boolean show) {
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        TextView titleView = (TextView) getCustomizedToolbar().findViewById(R.id.actionbar_title_text);
        titleView.setText(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        TextView titleView = (TextView) getCustomizedToolbar().findViewById(R.id.actionbar_title_text);
        titleView.setText(title);
    }

    private void initToolbar() {
        setTitle(R.string.app_name);

        LinearLayout rightZoneView = getCustomizedToolbar().getRightZoneView();
        View.inflate(this, R.layout.view_actionbar_right_zone, rightZoneView);

        findViewById(R.id.action_search_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickSearch();
                    }
                });
    }

    protected void onClickSearch() {
        initSearchZone();

        SearchLayoutView searchLayoutView = new SearchLayoutView(this);
        searchLayoutView.setId(R.id.base_search_layout_content);
        searchLayoutView.setSearchListener(mSearchListener);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(searchLayoutView, params);

        showSearchZone(true);
    }

    private void showSearchZone(boolean show) {
        mSearchZoneShow = show;

        BaseToolbar toolbar = getCustomizedToolbar();
        toolbar.getToolbarExtraContainer().setVisibility(
                show ? View.VISIBLE : View.GONE);
        toolbar.getToolbarContainer().setVisibility(
                show ? View.GONE : View.VISIBLE);

        if (!show) {
            EditText editText = (EditText) findViewById(R.id.search_text);
            if (editText != null) {
                InputMethodManager mgr = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editText.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

            View searchLayoutView = findViewById(R.id.base_search_layout_content);
            if (searchLayoutView != null) {
                ViewGroup parent = (ViewGroup) searchLayoutView.getParent();
                parent.removeView(searchLayoutView);
            }
        }
    }

    @Override
    public final boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mSearchZoneShow) {
            showSearchZone(false);
            return true;
        }

        if (handleKeyUp(keyCode, event)) {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    protected void handleTitleClicked() {
        finish();
    }

    protected void handleBackButtonClicked() {
        finish();
    }

    protected boolean handleKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    private void initSearchZone() {
        if (mSearchZoneView == null) {
            FrameLayout customizedZoneView = getCustomizedToolbar().getToolbarExtraContainer();
            mSearchZoneView = View.inflate(this, R.layout.layout_search_zone, customizedZoneView);

            final EditText editText = (EditText)
                    mSearchZoneView.findViewById(R.id.search_text);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(
                        CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(
                        CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    updateSearchZone();
                }
            });

            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String content = editText.getText().toString();
                        if (!TextUtils.isEmpty(content)) {
                            // 开始搜索.
                            doSearch(content);
                        }

                        return true;
                    }

                    return false;
                }
            });

            findViewById(R.id.search_btn).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String content = editText.getText().toString();
                            if (!TextUtils.isEmpty(content)) {
                                // 开始搜索.
                                doSearch(content);
                            } else {
                                // 取消搜索.
                                showSearchZone(false);
                            }
                        }
                    });
        }

        final EditText editText = (EditText) mSearchZoneView.findViewById(R.id.search_text);
        editText.setText(null);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        // 自动呼出软键盘.
        ThreadManager.getInstance().executeOnUIHandler(new Runnable() {
            @Override
            public void run() {
                InputMethodManager mgr = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        updateSearchZone();
    }

    private void updateSearchZone() {
        EditText editText = (EditText)
                mSearchZoneView.findViewById(R.id.search_text);
        String content = editText.getText().toString();

        Button searchBtn = (Button) mSearchZoneView.findViewById(R.id.search_btn);
        searchBtn.setText(getString(TextUtils.isEmpty(content)
                ? R.string.cancel : R.string.search));
    }

    private SearchLayoutView.OnSearchListener mSearchListener =
            new SearchLayoutView.OnSearchListener() {
                @Override
                public void onSearch(String searchKey) {
                    doSearch(searchKey);
                }
            };

    private void doSearch(String searchKey) {
        if (TextUtils.isEmpty(searchKey)) {
            return;
        }

        showSearchZone(false);
        SearchHistoryManager.addHistoryItem(searchKey);

        CategoryActivity.startCategoryActivity(this, searchKey,
                SearchCategoryManager.getInstance().getCategoryProvider(
                        AppUtils.getMainCategory(),
                        searchKey));
    }
}
