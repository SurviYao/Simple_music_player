package com.example.mymusic.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.mymusic.LoadingActivity;
import com.example.mymusic.MainActivity;

public class WelcomeActivity extends Activity {

    private boolean isFirstUse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         new Thread(){
             @Override
             public void run() {
                 super.run();
                 try {
                     /*
                      * 线程休眠时间
                      */
                     Thread.sleep(100);
                     //SharedPreferences存储
                     @SuppressLint("WorldReadableFiles")
                     SharedPreferences preferences = getSharedPreferences("isFirstUse",MODE_PRIVATE);
                     isFirstUse = preferences.getBoolean("isFirstUse", true);
                     /*
                      *判断是否第一次启动程序
                      *如果是第一次启动GuideActivity.class，如果不是这进入主界面
                      */
                     if (isFirstUse) {
                         startActivity(new Intent(WelcomeActivity.this, LoadingActivity.class));
                     } else {
                         startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                     }
                     finish();
                     SharedPreferences.Editor editor = preferences.edit();
                     editor.putBoolean("isFirstUse", false);
                     editor.apply();
                 } catch (Exception e) {
                    e.printStackTrace();
                 }
             }
         }.start();
    }

}
