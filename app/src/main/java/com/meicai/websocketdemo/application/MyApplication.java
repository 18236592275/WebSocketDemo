package com.meicai.websocketdemo.application;

import android.app.Application;

import com.meicai.mcpushlibrary.McPush;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //通过service启动服务
        McPush.init(this);
    }
}
