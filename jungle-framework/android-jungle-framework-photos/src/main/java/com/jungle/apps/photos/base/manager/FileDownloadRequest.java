/**
 * Android photos application project.
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

package com.jungle.apps.photos.base.manager;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.jungle.base.utils.FileUtils;

public class FileDownloadRequest extends Request<String> {

    private Response.Listener<String> mListener;
    private String mFilePath;


    public FileDownloadRequest(String url, String filePath,
            Response.Listener<String> listener, Response.ErrorListener errorListener) {

        super(Method.GET, url, errorListener);
        mListener = listener;
        mFilePath = filePath;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if (FileUtils.writeToStorage(mFilePath, response.data)) {
            return Response.success(mFilePath, HttpHeaderParser.parseCacheHeaders(response));
        }

        return Response.error(new VolleyError());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
