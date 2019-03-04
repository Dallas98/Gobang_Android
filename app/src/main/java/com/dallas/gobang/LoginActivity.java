package com.dallas.gobang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * 这是主界面
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);  //这个界面是开始界面

        //安卓全屏代码
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(2000);   //开始界面上全屏显示   2秒
                    //判断是否是第一次进入app
//                    Intent intent = new Intent(LoginActivity.this,
//                            GuideActivity.class);
//                    startActivity(intent);
//                    finish();
                    if(isRunStart(LoginActivity.this)){
                        //进入卡片页面
                        Intent intent = new Intent(LoginActivity.this,
                                GuideActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        //直接进入主界面
                        Intent intent1 = new Intent(LoginActivity.this,
                                StartScreenActivity.class);
                        startActivity(intent1);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 判断是否是第一次进入app
     * @param context
     * @return
     */
    public boolean isRunStart(Context context){
        SharedPreferences sp = context.getSharedPreferences("shared", MODE_PRIVATE);
        boolean isRunStart = sp.getBoolean("isRunStart", true);
        SharedPreferences.Editor editor = sp.edit();
        if(isRunStart){  //如果是第一次进入会是true
            //写入文件
            editor.putBoolean("isRunStart", false);   //则写入文件false(下次就不是了)
            editor.commit();
            return true;
        }else{
            return false;
        }
    }
}
