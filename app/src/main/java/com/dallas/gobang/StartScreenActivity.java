package com.dallas.gobang;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;



public class StartScreenActivity extends AppCompatActivity {

    private Button startgamebymanandmachine = null;   //人机按钮
    private Button startgamebymanandman = null;       //人人按钮
    private Button helpbtn = null;                    //帮助按钮
    private Button aboutbtn = null;                   //关于按钮
    private Button exitbtn = null;                    //退出按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        startgamebymanandmachine = (Button) findViewById(R.id.startgamebymanandmachine);
        startgamebymanandman = (Button) findViewById(R.id.startgamebymanandman);
        helpbtn = (Button) findViewById(R.id.helpbtn);
        aboutbtn = (Button) findViewById(R.id.aboutbtn);
        exitbtn = (Button) findViewById(R.id.exitbtn);

        startgamebymanandmachine.setOnClickListener(new StartGameListener());
        startgamebymanandman.setOnClickListener(new StartGameListener());
        helpbtn.setOnClickListener(new HelpListener());
        aboutbtn.setOnClickListener(new AboutListener());
        exitbtn.setOnClickListener(new ExitListener());
    }


    //开始游戏  按钮监听器
    class StartGameListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startgamebymanandmachine:
                    Chessboard.setGamePattern(GamePattern.MANANDMACHINE);
                    break;
                case R.id.startgamebymanandman:
                    Chessboard.setGamePattern(GamePattern.MANANDMAN);
                    break;
                default:
                    break;
            }
            Intent intent = new Intent(StartScreenActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    //帮助按钮监听器
    class HelpListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(StartScreenActivity.this, HelpActivity.class);
            startActivity(intent);
        }
    }

    //关于按钮监听器
    class AboutListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(StartScreenActivity.this, AboutActivity.class);
            startActivity(intent);
        }
    }

    //退出按钮监听器
    class ExitListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            StartScreenActivity.this.finish();
        }
    }

}
