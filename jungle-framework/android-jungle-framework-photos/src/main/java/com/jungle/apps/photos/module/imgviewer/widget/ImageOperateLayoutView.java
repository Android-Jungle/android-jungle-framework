package com.jungle.apps.photos.module.imgviewer.widget;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.jungle.apps.photos.R;
import com.jungle.apps.photos.module.imgviewer.PhotoManager;
import com.jungle.base.utils.FileUtils;
import com.jungle.widgets.dialog.JungleToast;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ImageOperateLayoutView extends FrameLayout {

    private static final int ANIM_BASE_SHOW_INTERVAL = 100;
    private static final int ANIM_VARIABLE_SHOW_INTERVAL = 300;

    public static interface OnVisibilityListener {
        void onShow();

        void onHide();
    }


    private View mSetToWallPaper;
    private View mShareToQQ;
    private View mShareToQZone;
    private View mShareToWXFriend;
    private View mShareToWXFriendGroup;
    private ShareManager.ShareInfo mShareInfo;
    private Random mRandom = new Random(System.currentTimeMillis());
    private Set<Integer> mColorSet = new HashSet<Integer>();
    private OnVisibilityListener mVisibilityListener;
    private int mHidingViewCount = 0;


    public ImageOperateLayoutView(Context context) {
        super(context);

        initLayout(context);
    }

    public ImageOperateLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initLayout(context);
    }

    public ImageOperateLayoutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_image_operate_view, this);

        mSetToWallPaper = findViewById(R.id.set_to_wallpaper);
        mShareToQQ = findViewById(R.id.share_to_qq);
        mShareToQZone = findViewById(R.id.share_to_qzone);
        mShareToWXFriend = findViewById(R.id.share_to_wx_friend);
        mShareToWXFriendGroup = findViewById(R.id.share_to_wx_friend_group);

        mShareToQQ.setOnClickListener(mShareClickListener);
        mShareToQZone.setOnClickListener(mShareClickListener);
        mShareToWXFriend.setOnClickListener(mShareClickListener);
        mShareToWXFriendGroup.setOnClickListener(mShareClickListener);

        mSetToWallPaper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setToWallPaper();
                hideOperateLayout();
            }
        });
    }

    public void setVisibilityListener(OnVisibilityListener listener) {
        mVisibilityListener = listener;
    }

    public void showOperateLayout(ShareManager.ShareInfo shareInfo) {
        mShareInfo = shareInfo;
        setVisibility(View.VISIBLE);

        setShowAnimation(mSetToWallPaper);
        setShowAnimation(mShareToQQ);
        setShowAnimation(mShareToQZone);
        setShowAnimation(mShareToWXFriend);
        setShowAnimation(mShareToWXFriendGroup);

        if (mVisibilityListener != null) {
            mVisibilityListener.onShow();
        }
    }

    private void prepareHide() {
        mShareInfo = null;
        mHidingViewCount = 0;
    }

    public void hideOperateLayout() {
        prepareHide();

        setHideAnimation(mSetToWallPaper);
        setHideAnimation(mShareToQQ);
        setHideAnimation(mShareToQZone);
        setHideAnimation(mShareToWXFriend);
        setHideAnimation(mShareToWXFriendGroup);

        if (mVisibilityListener != null) {
            mVisibilityListener.onHide();
        }
    }

    public void hideOperateLayoutWithoutAnim() {
        prepareHide();
        setVisibility(View.GONE);

        if (mVisibilityListener != null) {
            mVisibilityListener.onHide();
        }
    }

    private void setShowAnimation(View view) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.img_operate_view_show);
        anim.setDuration(ANIM_BASE_SHOW_INTERVAL + mRandom.nextInt(ANIM_VARIABLE_SHOW_INTERVAL));

        view.setVisibility(View.VISIBLE);
        view.startAnimation(anim);
    }

    private void setHideAnimation(final View view) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.img_operate_view_hide);
        anim.setDuration(ANIM_BASE_SHOW_INTERVAL + mRandom.nextInt(ANIM_VARIABLE_SHOW_INTERVAL));
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);

                ++mHidingViewCount;
                if (mHidingViewCount >= 5) {
                    setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(anim);
    }

    public boolean isShowing() {
        return View.VISIBLE == getVisibility();
    }

    private OnClickListener mShareClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            handleShare(v.getId());
            hideOperateLayout();
        }
    };

    private void setToWallPaper() {
        if (mShareInfo == null) {
            return;
        }

        if (TextUtils.isEmpty(mShareInfo.mLocalPath)
                || !FileUtils.isFileExist(mShareInfo.mLocalPath)) {
            PhotoManager.getInstance().setWallPaper(
                    getContext(), mShareInfo.mImageUrl);
            return;
        }

        PhotoManager.getInstance().setWallPaperLocal(mShareInfo.mLocalPath);
    }

    private void handleShare(int id) {
        if (mShareInfo == null) {
            return;
        }

        Activity activity = (Activity) getContext();
        switch (id) {
            case R.id.share_to_qq:
                ShareManager.getInstance().shareToQQ(
                        activity,
                        mShareInfo,
                        mShareListener);
                break;

            case R.id.share_to_qzone:
                ShareManager.getInstance().shareToQZone(
                        activity,
                        mShareInfo,
                        mShareListener);
                break;

            case R.id.share_to_wx_friend:
                ShareManager.getInstance().shareToWXFriend(
                        activity, mShareInfo);
                break;

            case R.id.share_to_wx_friend_group:
                ShareManager.getInstance().shareToWXFriendsGroup(
                        activity, mShareInfo);
                break;

            default:
                break;
        }
    }

    private ShareManager.OnShareListener mShareListener =
            new ShareManager.OnShareListener() {
                @Override
                public void onSuccess() {
                    Context context = getContext();
                    JungleToast.makeText(context,
                            context.getString(R.string.share_succeeded),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onFailed(String message) {
                    Context context = getContext();
                    String errorMsg = context.getString(R.string.share_failed);
                    if (!TextUtils.isEmpty(message)) {
                        errorMsg += " - " + message;
                    }

                    JungleToast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                }
            };
}
