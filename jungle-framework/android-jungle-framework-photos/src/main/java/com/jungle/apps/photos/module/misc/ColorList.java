package com.jungle.apps.photos.module.misc;

import java.util.Random;

public class ColorList {

    private static Random mRandom = new Random(System.currentTimeMillis());

    private static int[] mTagColors = new int[] {
            0xff76d0f5,
            0xff1fb7b2,
            0xff98a5f8,
            0xfff898be,
            0xfff0b21e,
            0xffff9036,
            0xfff0d01e,
            0xfff57a7a,
            0xff85aa40,
            0xfff77a00,
            0xff188eee,
    };


    public static int randomNextColor() {
        int index = Math.abs(mRandom.nextInt()) % mTagColors.length;
        return mTagColors[index];
    }
}
