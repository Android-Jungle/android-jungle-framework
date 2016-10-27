package com.jungle.apps.photos.module.imgviewer.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.data.manager.CategoryManager;
import com.jungle.apps.photos.module.category.provider.CategoryContentProvider;
import com.jungle.base.utils.FileUtils;
import com.jungle.base.utils.ImageUtils;
import com.jungle.imageloader.ImageLoaderUtils;

import java.util.Stack;

public class ImageViewerLayoutView extends FrameLayout {

    public static interface OnItemSelectListener {
        void onSelectedItemChanged(int position);
    }


    private ViewPager mImgViewPager;
    private ImageViewerAdapter mAdapter;
    private OnItemSelectListener mItemSelectListener;
    private CategoryContentProvider mContentProvider;
    private OnTouchListener mOnTouchListener;


    public ImageViewerLayoutView(Context context) {
        super(context);
        initLayout(context);
    }

    public ImageViewerLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public ImageViewerLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_image_viewer, this);

        mImgViewPager = (ViewPager) findViewById(R.id.image_view_pager);
        mImgViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i2) {
            }

            @Override
            public void onPageSelected(int position) {
                notifyItemSelectChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void setOnCustomTouchListener(OnTouchListener l) {
        mOnTouchListener = l;
    }

    public void initImageViewer(CategoryContentProvider provider, String seledId) {
        mContentProvider = provider;
        mAdapter = new ImageViewerAdapter();
        mImgViewPager.setAdapter(mAdapter);

        if (mAdapter.getCount() > 0) {
            int itemId = 0;
            if (!TextUtils.isEmpty(seledId)) {
                itemId = getLocationIndex(seledId);
            }

            mImgViewPager.setCurrentItem(itemId);
            notifyItemSelectChange(itemId);
        }
    }

    public void setItemListener(OnItemSelectListener l) {
        mItemSelectListener = l;
    }

    private void notifyItemSelectChange(int position) {
        if (mItemSelectListener != null) {
            mItemSelectListener.onSelectedItemChanged(position);
        }
    }

    public int getCurrItemCount() {
        return mAdapter.getCount();
    }

    public void notifyItemDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public CategoryManager.CategoryItem getCurrentItem() {
        int currIndex = mImgViewPager.getCurrentItem();
        if (currIndex >= 0 && currIndex < mContentProvider.getItemCount()) {
            return mContentProvider.getItem(currIndex);
        }

        return null;
    }

    private int getLocationIndex(String id) {
        int count = mContentProvider.getItemCount();
        for (int i = 0; i < count; ++i) {
            CategoryManager.CategoryItem item = mContentProvider.getItem(i);
            if (TextUtils.equals(id, item.mId)) {
                return i;
            }
        }

        return 0;
    }

    private class ImageViewerAdapter extends PagerAdapter {

        private Stack<ScalableImageView> mRecycleViewList =
                new Stack<ScalableImageView>();

        @Override
        public int getCount() {
            return mContentProvider.getItemCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ScalableImageView v = (ScalableImageView) object;
            container.removeView(v);
            mRecycleViewList.push(v);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ScalableImageView v = null;
            if (mRecycleViewList.isEmpty()) {
                v = new ScalableImageView(getContext());
                v.setOnCustomTouchListener(mOnTouchListener);
            } else {
                v = mRecycleViewList.pop();
            }

            updateImageView(v, position);

            container.addView(v);
            return v;
        }

        private void updateImageView(ScalableImageView view, int position) {
            CategoryManager.CategoryItem item = mContentProvider.getItem(position);
            view.resetScale();
            view.setTag(item);

            Bitmap bitmap = null;
            CategoryManager.CategoryInfo info = mContentProvider.getCategoryInfo();
            if (info.mCategoryType == CategoryManager.CategoryType.Favorited) {
                if (FileUtils.isFileExist(item.mLocalPath)) {
                    bitmap = ImageUtils.decodeSampledBitmapFromFile(item.mLocalPath);
                }
            }

            if (bitmap != null) {
                view.setImageBitmap(bitmap);
            } else {
                ImageLoaderUtils.displayImage(view, item.mSrcUrl);
            }
        }
    }
}
