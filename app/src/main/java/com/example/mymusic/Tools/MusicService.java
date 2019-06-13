package com.example.mymusic.Tools;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.mymusic.Tools.Music;

import java.io.IOException;

public class MusicService extends Service {
    private MyServiceBroad broad;
    private Music music;
    private MediaPlayer player = new MediaPlayer();
    private int state = 1;
    private Thread thread;
    //1表示未播放   2表示正在播放  3表示暂停
    private int dur = 0, pro = 0;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //动态注册广播接收者
        broad = new MyServiceBroad();
        IntentFilter filter = new IntentFilter("service");
        registerReceiver(broad, filter);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //当音乐播放结束的时候，给界面发送消息
                //通知界面当前音乐播放结束
                Intent intent = new Intent("activity");
                intent.putExtra("over", true);
                sendBroadcast(intent);
            }
        });
    }

    //服务的广播接收者
    private class MyServiceBroad extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int newMusic = intent.getIntExtra("newMusic", -1);
            if (newMusic == 1) {
                music = (Music) intent
                        .getSerializableExtra("music");
                play();
                state = 2;
            }
            int contorl = intent.getIntExtra("contorl", -1);
            if (contorl == 1) {
                //当前如果是正在播放，就暂停音乐
                if (state == 2) {
                    state = 3;
                    player.pause();
                    //当前是暂停状态，播放状态
                } else if (state == 3) {
                    state = 2;
                    player.start();
                }
            }
            int progress = intent.getIntExtra("progress", -1);
            if (progress > 0) {
                dur = player.getDuration();
                pro = (int) (progress / 100.0 * dur);
                player.seekTo(pro);
            }
            //发送服务正在播放的歌曲的歌名
            //发送服务播放音乐的状态，未播放，正在播放，暂停
            Intent intent1 = new Intent("activity");
            intent1.putExtra("music", music);
            intent1.putExtra("state", state);
            sendBroadcast(intent1);
        }
    }

    public void play() {
        player.pause();
        player.stop();
        player.reset();
        try {
            player.setDataSource(music.getPath());
            player.prepare();
            player.start();
            //获取当前歌曲的总时长
            dur = player.getDuration();
            //将当前进度清零
            pro = 0;
            //单例模式
            if (thread == null) {
                thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        while (true) {
                            while ((pro < dur) && player.isPlaying()) {
                                try {
                                    sleep(1000);
                                    pro = player.getCurrentPosition();
                                    Intent intent = new Intent("activity");
                                    intent.putExtra("dur", dur);
                                    intent.putExtra("pro", pro);
                                    intent.putExtra("music", music);
                                    intent.putExtra("state", state);
                                    sendBroadcast(intent);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                };
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
