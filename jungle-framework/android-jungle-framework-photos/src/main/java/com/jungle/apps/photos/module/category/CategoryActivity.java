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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
