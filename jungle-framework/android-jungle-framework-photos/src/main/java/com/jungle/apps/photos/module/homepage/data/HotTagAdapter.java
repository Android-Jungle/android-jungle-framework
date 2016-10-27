package com.jungle.apps.photos.module.homepage.data;

import android.view.View;

public interface HotTagAdapter {

    int getTagCount();

    View getTagView(int position);
}