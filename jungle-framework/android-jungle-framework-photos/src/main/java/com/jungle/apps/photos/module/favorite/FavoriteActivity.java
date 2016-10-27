package com.jungle.apps.photos.module.favorite;

import android.os.Bundle;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.base.app.PhotoBaseActivity;
import com.jungle.apps.photos.module.favorite.widget.FavoriteLayoutView;

public class FavoriteActivity extends PhotoBaseActivity {

    FavoriteLayoutView mFavoriteView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);

        mFavoriteView = (FavoriteLayoutView) findViewById(R.id.favorite_view);
        mFavoriteView.setFavoriteLayoutListener(
                new FavoriteLayoutView.FavoriteLayoutListener() {
                    @Override
                    public void onFavoriteChanged() {
                        updateTitle();
                    }
                });

        updateTitle();
    }

    private void updateTitle() {
        int count = mFavoriteView.getFavoriteCount();
        if (count == 0) {
            setTitle(R.string.my_favorite);
        } else {
            setTitle(String.format(getString(R.string.my_favorite_with_count), count));
        }
    }
}
