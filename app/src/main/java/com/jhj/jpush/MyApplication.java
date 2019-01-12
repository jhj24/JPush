package com.jhj.jpush;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jhj on 19-1-12.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
