package com.dallas.gobang;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;


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
