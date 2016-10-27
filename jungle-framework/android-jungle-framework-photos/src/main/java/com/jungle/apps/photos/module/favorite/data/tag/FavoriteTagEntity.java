package com.jungle.apps.photos.module.favorite.data.tag;

import com.jungle.simpleorm.BaseEntity;
import com.jungle.simpleorm.constraint.PrimaryKey;

public class FavoriteTagEntity extends BaseEntity {

    @PrimaryKey
    public String mTag;
}
