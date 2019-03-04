package com.dallas.gobang;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Chessboard chess = null;
    public static TextView locationInfo = null;
    public static Button btn_restart = null;
    public static Button btn_undo = null;
    private Button btn_back = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chess = (Chessboard)findViewById(R.id.chess);
        locationInfo = (TextView)findViewById(R.id.location);
        btn_restart = (Button)findViewById(R.id.btn_restart);
        btn_undo = (Button)findViewById(R.id.btn_undo);
        btn_back = (Button)findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new OnClickBackListener());
    }
    //返回按钮功能
    class OnClickBackListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            MainActivity.this.finish();
        }
    }
}
