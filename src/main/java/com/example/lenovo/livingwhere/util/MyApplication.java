package com.example.lenovo.livingwhere.util;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.livingwhere.entity.CurrentUserObj;

/**
 * Created by Halo on 2015/8/31.
 */
public class MyApplication extends Application {

    private static MyApplication app;
    public static RequestQueue mQueue;//Volley请求队列application仅此一个
    //问韩寒
    CurrentUserObj user;

    @Override
    public void onCreate()// 初始化全局变量
    {
        super.onCreate();
        app = this;
        mQueue = Volley.newRequestQueue(this);
    }

    public static MyApplication getApp() {
        return app;
    }
}
