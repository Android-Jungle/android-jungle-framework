package com.jungle.apps.photos.module.category.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.category.data.manager.CategoryManager;
import com.jungle.apps.photos.module.imgviewer.ImageViewActivity;
import com.jungle.base.manager.ThreadManager;
import com.jungle.imageloader.ImageLoaderUtils;
import com.jungle.widgets.dialog.JungleToast;
import com.jungle.widgets.loading.JungleLoadingLayout;

public class CategoryLayoutView extends FrameLayout implements CategoryView {

    private PullToRefreshListView mListView;
    private JungleLoadingLayout mLoadingPageView;
    private CategoryListAdapter mAdapter = new CategoryListAdapter();
    private CategoryPresenter mPresenter = new CategoryPresenter(this);
    private CategoryItemView.ViewType mViewType = CategoryItemView.ViewType.Category;


    public CategoryLayoutView(Context context) {
        super(context);
        initLayout(context);
    }

    public CategoryLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public CategoryLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_category, this);

        mListView = (PullToRefreshListView) findViewById(R.id.category_list);
        ILoadingLayout layout = mListView.getLoadingLayoutProxy();
        layout.setPullLabel(context.getString(R.string.pull_up_to_refresh));
        layout.setReleaseLabel(context.getString(R.string.pull_up_refresh_release));

        mLoadingPageView = (JungleLoadingLayout) findViewById(R.id.loading_page);
        mLoadingPageView.setReloadListener(mReloadListener);
    }

    public void setViewType(CategoryItemView.ViewType viewType) {
        mViewType = viewType;
    }

    public void setProviderId(int providerId) {
        mPresenter.setProviderId(providerId);
        mListView.setAdapter(mAdapter);

        if (mPresenter.isSupportFetchMore()) {
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            mListView.setOnRefreshListener(mRefreshListener);
        } else {
            mListView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
    }

    public void reloadData() {
        mPresenter.reloadData();
    }

    @Override
    public void setLoading() {
        mLoadingPageView.setPageState(JungleLoadingLayout.PageState.Loading);
    }

    @Override
    public void updateLoadingState(boolean isFailed) {
        if (mPresenter.isRefreshing()) {
            mListView.onRefreshComplete();
            mPresenter.setRefreshing(false);
        }

        if (isFailed) {
            mLoadingPageView.setPageState(
                    JungleLoadingLayout.PageState.LoadingFailed);
        } else {
            mAdapter.notifyDataSetChanged();
            mLoadingPageView.setPageState(mPresenter.isEmpty()
                    ? JungleLoadingLayout.PageState.Empty
                    : JungleLoadingLayout.PageState.Invisible);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private PullToRefreshBase.OnRefreshListener2<ListView> mRefreshListener =
            new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    if (mPresenter.isCanFetchMore()) {
                        mPresenter.fetchCategory();
                    } else {
                        ThreadManager.getInstance().postOnUIHandler(new Runnable() {
                            @Override
                            public void run() {
                                mListView.onRefreshComplete();
                                JungleToast.makeText(getContext(),
                                        R.string.no_more_pic).show();
                            }
                        });
                    }
                }
            };

    private JungleLoadingLayout.OnReloadListener mReloadListener =
            new JungleLoadingLayout.OnReloadListener() {
                @Override
                public void onNeedReload() {
                    mPresenter.fetchCategory();
                }
            };


    private class CategoryListAdapter extends BaseAdapter {

        private class ItemHolder {
            CategoryItemView mFirstItemView;
            CategoryItemView mSecondItemView;

            ItemHolder(View view) {
                mFirstItemView = (CategoryItemView)
                        view.findViewById(R.id.category_first_item);
                mSecondItemView = (CategoryItemView)
                        view.findViewById(R.id.category_second_item);
            }

            void updateItem(CategoryManager.CategoryItem firstItem,
                    CategoryManager.CategoryItem secondItem) {

                mFirstItemView.updateView(firstItem);
                mSecondItemView.updateView(secondItem);

                mFirstItemView.setTag(firstItem.mId);
                mSecondItemView.setTag(secondItem.mId);

                ImageLoaderUtils.displayImage(mFirstItemView.getImageView(), firstItem.mThumbUrl);
                ImageLoaderUtils.displayImage(mSecondItemView.getImageView(), secondItem.mThumbUrl);
            }
        }

        @Override
        public int getCount() {
            return mPresenter.getItemCount() / 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder = null;

            if (convertView == null) {
                convertView = View.inflate(getContext(),
                        R.layout.layout_category_view, null);
                holder = new ItemHolder(convertView);

                convertView.setTag(holder);
                holder.mFirstItemView.setOnClickListener(mItemClickListener);
                holder.mSecondItemView.setOnClickListener(mItemClickListener);

                holder.mFirstItemView.setViewType(mViewType);
                holder.mSecondItemView.setViewType(mViewType);
            } else {
                holder = (ItemHolder) convertView.getTag();
                holder.mFirstItemView.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
                holder.mSecondItemView.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
            }

            holder.updateItem(
                    mPresenter.getItem(position * 2),
                    mPresenter.getItem(position * 2 + 1));

            return convertView;
        }

        private CategoryManager.OnFetchEventListener mFetchEventListener =
                new CategoryManager.OnFetchEventListener() {
                    @Override
                    public void onCategoryUpdated(CategoryManager.CategoryInfo info) {
                        notifyDataSetChanged();
                    }
                };

        private OnClickListener mItemClickListener =
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = (String) v.getTag();
                        ImageViewActivity.startImageViewActivity(
                                getContext(), mPresenter.getProviderId(), id);
                    }
                };
    }
}
