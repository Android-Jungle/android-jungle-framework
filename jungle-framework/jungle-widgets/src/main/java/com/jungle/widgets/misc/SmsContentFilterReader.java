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

package com.jungle.widgets.misc;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsContentFilterReader extends ContentObserver {

    private final Uri SMS_IN_BOX = Uri.parse("content://sms");
    public static final Pattern SIX_NUMBER_PATTERN = Pattern.compile("[0-9]{6}");
    public static final Pattern FOUR_NUMBER_PATTERN = Pattern.compile("[0-9]{4}");


    public interface OnFilterListener {
        void onFilter(String content);
    }


    private Context mContext;
    private Pattern mFilterPattern;
    private OnFilterListener mFilterListener;


    public SmsContentFilterReader(Context context, OnFilterListener listener) {
        this(context, new Handler(Looper.getMainLooper()), FOUR_NUMBER_PATTERN, listener);
    }

    public SmsContentFilterReader(Context context, Pattern pattern, OnFilterListener listener) {
        this(context, new Handler(Looper.getMainLooper()), pattern, listener);
    }

    public SmsContentFilterReader(
            Context context, Handler handler,
            Pattern pattern, OnFilterListener listener) {

        super(handler);
        mContext = context;
        mFilterPattern = pattern;
        mFilterListener = listener;
        mContext.getContentResolver().registerContentObserver(SMS_IN_BOX, true, this);
    }

    public void detachReader() {
        mContext.getContentResolver().unregisterContentObserver(this);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        ContentResolver resolver = mContext.getContentResolver();
        String[] projection = new String[]{"body"};
        String whereCondition = String.format(
                " date > %d", System.currentTimeMillis() - 1000 * 10);

        Cursor cursor = null;
        try {
            cursor = resolver.query(
                    SMS_IN_BOX, projection, whereCondition, null, "date desc");
            if (cursor == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (!cursor.moveToNext()) {
            cursor.close();
            return;
        }

        int index = cursor.getColumnIndex("body");
        if (index != -1) {
            String body = cursor.getString(index);
            Matcher matcher = mFilterPattern.matcher(body);
            if (matcher.find()) {
                String content = matcher.group();
                mFilterListener.onFilter(content);
            }
        }

        cursor.close();
    }
}
