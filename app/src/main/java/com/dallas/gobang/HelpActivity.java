package com.dallas.gobang;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelpActivity extends AppCompatActivity {

    private Button btn_back = null;
    ToggleButton toggle;
    static private MediaPlayer mediaPlayer;
    static boolean play = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new BackListener());
        toggle = findViewById(R.id.open);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.back_music);
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inti();
    }

    void inti() {
        if (!play) {
            toggle.setChecked(true);
        } else {
            toggle.setChecked(false);
        }
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!play) {
                    toggle.setChecked(true);
                } else {
                    toggle.setChecked(false);
                }
                if (!isChecked) {
                    mediaPlayer.start();
                    play = true;
                    toggle.setChecked(false);
                } else {
                    mediaPlayer.pause();
                    play = false;
                    toggle.setChecked(true);
                }
            }
        };
        toggle.setOnCheckedChangeListener(listener);
    }


    //返回按钮监听器
    class BackListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HelpActivity.this.finish();
        }
    }
}
