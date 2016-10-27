package com.jungle.apps.photos.module.homepage.widget.category;

import android.view.View;
import android.view.ViewGroup;
import com.jungle.apps.photos.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryItem {

    public static enum ItemType {
        Horz_LOne_3x1,              // |            |

        Horz_LOne_3x2,              // |            |
                                    // |            |

        Horz_LOne_SOne_3x1,         // |        |   |

        Horz_SOne_LOne_3x1,         // |    |       |

        Horz_SThree_3x1,            // |    |   |   |

        Horz_SThree_3x2,            // |    |   |   |
                                    // |    |   |   |

        Vert_LOne_STwo_3x2,         // |        |   |
                                    // |        |---|
                                    // |        |   |

        Vert_STwo_LOne_3x2,         // |    |       |
                                    // |----|       |
                                    // |    |       |

        Vert_SOne_LTwo_3x2,         // |    |       |
                                    // |    |-------|
                                    // |    |       |

        Vert_LTwo_SOne_3x2,         // |        |   |
                                    // |--------|   |
                                    // |        |   |
    }


    public static interface ItemSizeComputer {
        void onComputeSize(View view);
    }

    public static class ItemScaleSizeComputer implements ItemSizeComputer {
        private float mScaleWH;

        ItemScaleSizeComputer(float scale) {
            mScaleWH = scale;
        }

        @Override
        public void onComputeSize(View view) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int width = view.getMeasuredWidth();
            params.height = (int) (width / mScaleWH);
            view.setLayoutParams(params);
        }
    }

    private static ItemSizeComputer m3x1Computer =
            new ItemScaleSizeComputer(3.0f / 1.0f);

    private static ItemSizeComputer m3x2Computer =
            new ItemScaleSizeComputer(3.0f / 2.0f);


    public static enum ItemTagPos {
        Top,
        Bottom,
        BottomLeft,
    }


    public static class ItemLayoutInfo {
        public int mLayoutResId;
        public ItemTagPos[] mItemTagPos;
        public ItemSizeComputer mSizeComputer;
    }


    public static class CategoryInfo {
        public String mCategory;
        public String mCategoryTag;
        public String mCategoryImgUrl;

        public CategoryInfo(String category, String tag, String url) {
            mCategory = category;
            mCategoryTag = tag;
            mCategoryImgUrl = url;
        }
    }

    public static class CategoryItemInfo {
        public boolean mHasDivider = false;
        public ItemLayoutInfo mLayoutInfo;
        public List<CategoryInfo> mCategoryList = new ArrayList<CategoryInfo>();

        public void addItem(CategoryInfo info) {
            mCategoryList.add(info);
        }
    }


    private static Map<ItemType, ItemLayoutInfo> mLayoutInfoList =
            new HashMap<ItemType, ItemLayoutInfo>();

    private static Map<ItemType, ItemTagPos[]> mLayoutTagPosList =
            new HashMap<ItemType, ItemTagPos[]>();


    static {
        initTagPosInfo();
        initCategoryInfo();
    }


    private static void initCategoryInfo() {
        addCategoryInfo(ItemType.Horz_LOne_3x1,
                R.layout.layout_horz_lone_3x1,
                m3x1Computer);

        addCategoryInfo(ItemType.Horz_LOne_3x2,
                R.layout.layout_horz_lone_3x2,
                m3x2Computer);

        addCategoryInfo(ItemType.Horz_LOne_SOne_3x1,
                R.layout.layout_horz_lone_sone_3x1,
                m3x1Computer);

        addCategoryInfo(ItemType.Horz_SOne_LOne_3x1,
                R.layout.layout_horz_sone_lone_3x1,
                m3x1Computer);

        addCategoryInfo(ItemType.Horz_SThree_3x1,
                R.layout.layout_horz_sthree_3x1,
                m3x1Computer);

        addCategoryInfo(ItemType.Horz_SThree_3x2,
                R.layout.layout_horz_sthree_3x2,
                m3x2Computer);

        addCategoryInfo(ItemType.Vert_LOne_STwo_3x2,
                R.layout.layout_vert_lone_stwo_3x2,
                m3x2Computer);

        addCategoryInfo(ItemType.Vert_STwo_LOne_3x2,
                R.layout.layout_vert_stwo_lone_3x2,
                m3x2Computer);

        addCategoryInfo(ItemType.Vert_SOne_LTwo_3x2,
                R.layout.layout_vert_sone_ltwo_3x2,
                m3x2Computer);

        addCategoryInfo(ItemType.Vert_LTwo_SOne_3x2,
                R.layout.layout_vert_ltwo_sone_3x2,
                m3x2Computer);
    }

    private static void initTagPosInfo() {
        addTagPosInfo(ItemType.Horz_LOne_3x1,
                new ItemTagPos[]{ItemTagPos.BottomLeft});

        addTagPosInfo(ItemType.Horz_LOne_3x2,
                new ItemTagPos[]{ItemTagPos.Bottom});

        addTagPosInfo(ItemType.Horz_LOne_SOne_3x1,
                new ItemTagPos[]{ItemTagPos.BottomLeft});

        addTagPosInfo(ItemType.Horz_SOne_LOne_3x1,
                new ItemTagPos[]{ItemTagPos.BottomLeft});

        addTagPosInfo(ItemType.Horz_SThree_3x1,
                new ItemTagPos[]{ItemTagPos.BottomLeft});

        addTagPosInfo(ItemType.Horz_SThree_3x2,
                new ItemTagPos[]{ItemTagPos.Top});

        addTagPosInfo(ItemType.Vert_LOne_STwo_3x2,
                new ItemTagPos[]{ItemTagPos.Top,
                        ItemTagPos.BottomLeft, ItemTagPos.BottomLeft});

        addTagPosInfo(ItemType.Vert_STwo_LOne_3x2,
                new ItemTagPos[]{ItemTagPos.BottomLeft,
                        ItemTagPos.BottomLeft, ItemTagPos.Bottom});

        addTagPosInfo(ItemType.Vert_SOne_LTwo_3x2,
                new ItemTagPos[]{ItemTagPos.Top, ItemTagPos.BottomLeft});

        addTagPosInfo(ItemType.Vert_LTwo_SOne_3x2,
                new ItemTagPos[]{ItemTagPos.BottomLeft,
                        ItemTagPos.BottomLeft, ItemTagPos.Top});
    }

    public static ItemLayoutInfo getLayoutInfo(ItemType itemType) {
        return mLayoutInfoList.get(itemType);
    }

    private static void addCategoryInfo(
            ItemType itemType, int resLayoutId,
            ItemSizeComputer computer) {

        ItemLayoutInfo item = new ItemLayoutInfo();
        item.mLayoutResId = resLayoutId;
        item.mItemTagPos = mLayoutTagPosList.get(itemType);
        item.mSizeComputer = computer;

        mLayoutInfoList.put(itemType, item);
    }

    private static void addTagPosInfo(ItemType type, ItemTagPos[] pos) {
        mLayoutTagPosList.put(type, pos);
    }

    public static ItemType getItemTypeFromString(String str) {
        for (ItemType type : ItemType.values()) {
            if (type.toString().equals(str)) {
                return type;
            }
        }

        return null;
    }
}
