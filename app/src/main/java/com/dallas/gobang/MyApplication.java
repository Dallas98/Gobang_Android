package com.dallas.gobang;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

/**
 * 全局获取Context的技巧   这个类用于可以全局获取Context
 * 有时候需要用到Context,弹出Toast,启动活动,发送广播,操作数据库,使用通知等时候都需要Context
 * 而有时候获取Context有点难度.所以这里有个方法可以解决上诉难题.
 *
 * Android提供了一个Application类,每当应用程序启动的时候,系统就会自动将这个类初始化.
 * 而我们可以定制一个自己的Application类,以便于管理程序内一些全局的状态信息,比如说Context
 *
 * 定制一个自己的Application,需要继承自Application,在onCreate()方法中初始化得到一个应用级别的Context,
 新建一个静态方法,
 * 可以返回Context;  接下来需要告知系统,当程序启动时应该初始化MyApplication类,而不是默认的Application类.
 在AndroidManifest.xml
 * 文件的<application>标签下进行指定就可以了.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        //通过调用getApplicationContext()方法得到一个应用级别的Context
        super.onCreate();
        context = getApplicationContext();
    }

    //静态方法全局都可调用来获取Context
    public static Context getContext(){
        return context;
    }

}
