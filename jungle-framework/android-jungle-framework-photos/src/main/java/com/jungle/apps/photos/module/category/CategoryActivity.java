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

package com.jungle.apps.photos.module.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.app.PhotoBaseActivity;
import com.jungle.apps.photos.module.category.provider.CategoryProviderManager;
import com.jungle.apps.photos.module.category.widget.CategoryLayoutView;

public class CategoryActivity extends PhotoBaseActivity {

    private static final String PROVIDER_ID = "ProviderId";


    public static void startCategoryActivity(
            Context context, String title, int providerId) {

        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtra(ACTIVITY_TITLE, title);
        intent.putExtra(PROVIDER_ID, providerId);
        context.startActivity(intent);
    }


    private CategoryLayoutView mCategoryLayoutView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        String title = intent.getStringExtra(ACTIVITY_TITLE);
        setTitle(title);

        int providerId = intent.getIntExtra(PROVIDER_ID,
                CategoryProviderManager.INVALID_PROVIDER_ID);
        mCategoryLayoutView = (CategoryLayoutView) findViewById(R.id.category_view);
        mCategoryLayoutView.setProviderId(providerId);
    }
}
