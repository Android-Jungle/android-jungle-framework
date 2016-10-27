package com.jungle.apps.photos.module.favorite.data.pic;

import com.jungle.base.utils.FileUtils;
import com.jungle.simpleorm.BaseEntity;
import com.jungle.simpleorm.constraint.NotColumnField;
import com.jungle.simpleorm.constraint.PrimaryKey;

public class FavoriteEntity extends BaseEntity {

    public static enum ItemFlag {
        Added,
        Removed,
    }


    @PrimaryKey
    public long mGuid;
    public String mId;
    public String mLocalPath;
    public String mTitle;
    public String mSrcUrl;
    public long mFavTime;

    @NotColumnField
    public ItemFlag mFlag;


    public static String idEqualCondition(String id) {
        return "mId = '" + id + "'";
    }

    public boolean localFileExist() {
        return FileUtils.isFileExist(mLocalPath);
    }
}
