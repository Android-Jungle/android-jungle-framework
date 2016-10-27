package com.jungle.apps.photos.module.category.widget;

import com.jungle.component.mvp.MVPView;

public interface CategoryView extends MVPView {

    void setLoading();

    void updateLoadingState(boolean isFailed);

    void notifyDataSetChanged();
}
