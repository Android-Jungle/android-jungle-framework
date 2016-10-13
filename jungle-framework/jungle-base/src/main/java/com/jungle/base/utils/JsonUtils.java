/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static boolean safeGetBoolean(JSONObject json, String node) {
        if (json.has(node)) {
            try {
                return json.getBoolean(node);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static int safeGetInt(JSONObject json, String node) {
        if (json.has(node)) {
            try {
                return json.getInt(node);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static double safeGetDouble(JSONObject json, String node) {
        if (json.has(node)) {
            try {
                return json.getDouble(node);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static long safeGetLong(JSONObject json, String node) {
        if (json.has(node)) {
            try {
                return json.getLong(node);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static String safeGetString(JSONObject json, String node) {
        if (json.has(node)) {
            try {
                return json.getString(node);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static JSONArray safeGetArray(JSONObject json, String node) {
        if (json.has(node)) {
            try {
                return json.getJSONArray(node);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static JSONObject safeGetObject(JSONObject json, String node) {
        if (json.has(node)) {
            try {
                return json.getJSONObject(node);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
