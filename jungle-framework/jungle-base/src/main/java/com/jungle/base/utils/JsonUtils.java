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
