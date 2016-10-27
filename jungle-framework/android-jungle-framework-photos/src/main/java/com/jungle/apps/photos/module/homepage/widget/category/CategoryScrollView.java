package com.jungle.apps.photos.module.homepage.widget.category;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import com.handmark.pulltorefresh.library.OverscrollHelper;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class CategoryScrollView extends PullToRefreshScrollView {

    public CategoryScrollView(Context context) {
        super(context);
    }

    public CategoryScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryScrollView(Context context, Mode mode) {
        super(context, mode);
    }

    public CategoryScrollView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    protected ScrollView createRefreshableView(Context context, AttributeSet attrs) {
        ScrollView scrollView = new CategoryInnerScrollView(context, attrs);
        scrollView.setId(com.handmark.pulltorefresh.library.R.id.scrollview);
        return scrollView;
    }

    public class CategoryInnerScrollView extends ScrollView {

        private float mLastX = 0;
        private float mLastY = 0;


        public CategoryInnerScrollView(Context context) {
            super(context);
        }

        public CategoryInnerScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CategoryInnerScrollView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(CategoryScrollView.this, deltaX, scrollX, deltaY, scrollY,
                    getScrollRange(), isTouchEvent);

            return returnValue;
        }

        /**
         * Taken from the AOSP ScrollView source
         */
        private int getScrollRange() {
            int scrollRange = 0;
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                scrollRange = Math.max(0, child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
            }
            return scrollRange;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            int action = ev.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                mLastX = ev.getX();
                mLastY = ev.getY();
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (Math.abs(ev.getX() - mLastX) > Math.abs(ev.getY() - mLastY)) {
                    return false;
                }
            }

            return super.onInterceptTouchEvent(ev);
        }
    }
}

