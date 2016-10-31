package com.jungle.apps.photos.module.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.app.PhotoBaseActivity;

public class SearchActivity extends PhotoBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        initSearchZone();
    }

    private void initSearchZone() {
        showTitle(false);
        showRightZone(false);

        FrameLayout customizedZoneView = getCustomizedToolbar().getCustomizedZoneView();
        View.inflate(this, R.layout.layout_search_zone, customizedZoneView);

        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchText = (EditText) findViewById(R.id.search_text);
                String searchContent = searchText.getText().toString();
                if (!TextUtils.isEmpty(searchContent)) {
                    doSearch(searchContent);
                }
            }
        });
    }

    private void doSearch(String searchContent) {
    }
}
