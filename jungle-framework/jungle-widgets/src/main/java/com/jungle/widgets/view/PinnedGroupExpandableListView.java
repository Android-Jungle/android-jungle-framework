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

package com.jungle.widgets.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 * 可固定 GroupView 的 ExpandableListView.
 *
 * @author arnozhang
 */
public class PinnedGroupExpandableListView extends ExpandableListView {

    public static abstract class PinnedGroupAdapter
            extends BaseExpandableListAdapter {

        public abstract int getGroupViewResId();
        public abstract void refreshPinnedGroupView(
                View pinnedGroupView, int groupPosition);
    }

    private static enum PinnedGroupState {
        Invisible,      // 不可见.
        Pinned,         // 已固定.
        PushingUp       // 正在被向上推.
    }


    private View mPinnedGroupView;
    private OnScrollListener mOnScrollListener;
    private OnGroupClickListener mOnGroupClickListener;
    private boolean mIsDispatchEventOnPinnedGroupView = false;
    private int mPinnedGroupViewPushUpDistance;
    private PinnedGroupAdapter mAdapter;
    private PinnedGroupState mPinnedGroupState = PinnedGroupState.Invisible;

    public PinnedGroupExpandableListView(Context context) {
        super(context);

        init(context, null);
    }

    public PinnedGroupExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public PinnedGroupExpandableListView(
            Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    public View getPinnedGroupView() {
        return mPinnedGroupView;
    }

    private void init(Context context, AttributeSet attrs) {
        super.setOnScrollListener(mInnerScrollListener);
    }

    private final OnScrollListener mInnerScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {

            if (mPinnedGroupView != null) {
                updatePinnedGroupView(firstVisibleItem);
            }

            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(
                        view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }
    };

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);

        if (!(adapter instanceof PinnedGroupAdapter)) {
            return ;
        }

        mAdapter = (PinnedGroupAdapter) adapter;
        int pinnedGroupResId = mAdapter.getGroupViewResId();
        if (pinnedGroupResId == 0) {
            return ;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        mPinnedGroupView = inflater.inflate(pinnedGroupResId, this, false);
        if (mPinnedGroupView == null) {
            return ;
        }

        mPinnedGroupView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getActionMasked();

                if (action == MotionEvent.ACTION_UP) {
                    long pos = getExpandableListPosition(getFirstVisiblePosition());
                    int type = getPackedPositionType(pos);

                    if (type == PACKED_POSITION_TYPE_GROUP
                            || type == PACKED_POSITION_TYPE_CHILD) {
                        int groupPosition = getPackedPositionGroup(pos);
                        if (mOnGroupClickListener == null
                                || !mOnGroupClickListener.onGroupClick(
                                        PinnedGroupExpandableListView.this,
                                        v, groupPosition, mAdapter.getGroupId(groupPosition))) {

                            collapseGroup(groupPosition);
                        }
                    }
                }

                return false;
            }
        });

        requestLayout();
    }

    private void updatePinnedGroupView(int firstVisibleItem) {
        final long pos = getExpandableListPosition(firstVisibleItem);
        final int type = getPackedPositionType(pos);

        if (type == PACKED_POSITION_TYPE_NULL) {
            mPinnedGroupState = PinnedGroupState.Invisible;
        } else {
            int groupPosition = getPackedPositionGroup(pos);
            mAdapter.refreshPinnedGroupView(mPinnedGroupView, groupPosition);

            final int nextItemType = getPackedPositionType(
                    getExpandableListPosition(firstVisibleItem + 1));

            if (type == PACKED_POSITION_TYPE_GROUP) {
                if (nextItemType == PACKED_POSITION_TYPE_GROUP
                        || nextItemType == PACKED_POSITION_TYPE_NULL) {
                    mPinnedGroupState = PinnedGroupState.Invisible;
                } else {
                    mPinnedGroupState = PinnedGroupState.Pinned;
                }
            } else {
                // Child.
                View v = getChildAt(0);
                if (v.getBottom() > mPinnedGroupView.getMeasuredHeight()) {
                    mPinnedGroupState = PinnedGroupState.Pinned;
                } else {
                    if (nextItemType == PACKED_POSITION_TYPE_GROUP) {
                        mPinnedGroupState = PinnedGroupState.PushingUp;
                    } else {
                        mPinnedGroupState = PinnedGroupState.Pinned;
                    }
                }
            }
        }

        if (mPinnedGroupState == PinnedGroupState.Invisible) {
            mPinnedGroupView.setVisibility(View.GONE);
        } else {
            if (mPinnedGroupState == PinnedGroupState.PushingUp) {
                View v = getChildAt(1);
                mPinnedGroupViewPushUpDistance = mPinnedGroupView.getMeasuredHeight()
                        - (v != null ? v.getTop() : 0);
            } else {
                mPinnedGroupViewPushUpDistance = 0;
            }

            int widthSpec = MeasureSpec.makeMeasureSpec(
                    mPinnedGroupView.getMeasuredWidth(), MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(
                    mPinnedGroupView.getMeasuredHeight(), MeasureSpec.EXACTLY);

            mPinnedGroupView.setVisibility(View.VISIBLE);
            mPinnedGroupView.measure(widthSpec, heightSpec);
            mPinnedGroupView.layout(
                    0,
                    -mPinnedGroupViewPushUpDistance,
                    mPinnedGroupView.getMeasuredWidth(),
                    mPinnedGroupView.getMeasuredHeight() - mPinnedGroupViewPushUpDistance);
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void setOnGroupClickListener(OnGroupClickListener l) {
        mOnGroupClickListener = l;
        super.setOnGroupClickListener(l);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mPinnedGroupView != null) {
            measureChild(mPinnedGroupView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mPinnedGroupView != null) {
            updatePinnedGroupView(getFirstVisiblePosition());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mPinnedGroupView != null && mPinnedGroupView.getVisibility() == View.VISIBLE) {
            drawChild(canvas, mPinnedGroupView, getDrawingTime());
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mPinnedGroupView != null) {
            refreshAllChildView(mPinnedGroupView);
        }
    }

    private void refreshAllChildView(View v) {
        if (v instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) v;
            for (int i = 0; i < parent.getChildCount(); ++i) {
                refreshAllChildView(parent.getChildAt(i));
            }
        }

        v.refreshDrawableState();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        final float x = event.getX();
        final float y = event.getY();

        if (mIsDispatchEventOnPinnedGroupView) {
            boolean result = mPinnedGroupView != null
                    && mPinnedGroupView.dispatchTouchEvent(event);

            if (action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_UP) {
                return result;
            }
        }

        if (mPinnedGroupView != null
                && mPinnedGroupView.getVisibility() == View.VISIBLE
                && action == MotionEvent.ACTION_DOWN
                && x >= 0 && x <= mPinnedGroupView.getMeasuredWidth()
                && y >= 0 && y <= (mPinnedGroupView.getMeasuredHeight()
                        - mPinnedGroupViewPushUpDistance)
                && mPinnedGroupView.dispatchTouchEvent(event)) {

            mIsDispatchEventOnPinnedGroupView = true;
            return true;
        }

        return super.dispatchTouchEvent(event);
    }
}
