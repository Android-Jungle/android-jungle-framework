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
