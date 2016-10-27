package com.jungle.apps.photos.base.manager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jungle.base.app.AppCore;
import com.jungle.base.manager.AppManager;

public class HttpRequestManager implements AppManager {

    public static HttpRequestManager getInstance() {
        return AppCore.getInstance().getManager(HttpRequestManager.class);
    }


    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        mRequestQueue = Volley.newRequestQueue(AppCore.getApplicationContext());
    }

    @Override
    public void onTerminate() {
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void add(Request request) {
        mRequestQueue.add(request);
    }
}
