package com.dallas.gobang;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity {

    private Button btn_back2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        btn_back2 = (Button)findViewById(R.id.btn_back2);
        btn_back2.setOnClickListener(new BackListener());
    }

    //返回按钮监听器
    class BackListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            AboutActivity.this.finish();
        }
    }
}
