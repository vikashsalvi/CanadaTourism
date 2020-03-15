package com.cc.cloud5409tourismapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueApiSingleton {

    public static RequestQueueApiSingleton mInstance;
    private RequestQueue mRquestQueue;
    private static Context mCtx;

    private RequestQueueApiSingleton(Context context) {
        mCtx = context.getApplicationContext();
        mRquestQueue = getRequestQueue();
    }

    public static synchronized  RequestQueueApiSingleton getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new RequestQueueApiSingleton(context.getApplicationContext());
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRquestQueue == null) {
            mRquestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRquestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
