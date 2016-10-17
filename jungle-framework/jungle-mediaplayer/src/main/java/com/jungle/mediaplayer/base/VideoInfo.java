/*
 * Copyright (C) 2016. All rights reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/10/17
 */

package com.jungle.mediaplayer.base;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class VideoInfo implements Parcelable {

    private String mStreamUrl;
    private int mCurrentPosition;

    public VideoInfo() {
    }

    public VideoInfo(String url) {
        mStreamUrl = url;
        mCurrentPosition = 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mStreamUrl);
        dest.writeInt(mCurrentPosition);
    }

    public static boolean validate(VideoInfo videoInfo) {
        if (videoInfo == null) {
            return false;
        }

        return !TextUtils.isEmpty(videoInfo.mStreamUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        public VideoInfo createFromParcel(Parcel source) {
            VideoInfo info = new VideoInfo(source.readString());
            info.mStreamUrl = source.readString();
            info.mCurrentPosition = source.readInt();
            return info;
        }

        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    public String getStreamUrl() {
        return mStreamUrl;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
    }
}
