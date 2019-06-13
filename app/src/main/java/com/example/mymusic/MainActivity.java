package com.example.mymusic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymusic.Adapter.MusicAdapter;
import com.example.mymusic.Tools.Music;
import com.example.mymusic.Tools.MusicService;
import com.example.mymusic.Tools.MyVIPActivity;
import com.example.mymusic.Utils.MusicUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {
    private DrawerLayout dl;
    private Button btn_mode;
    private TextView tv_name, tv_cur_time, tv_total_time;
    private ListView lv_music;
    private SeekBar seekBar;
    private ImageView iv_play;
    private List<Music> oList = new ArrayList<>();
    private MyActivityBroad broad;
    private Music music;
    private int index = 0;//用于保存当前播放的音乐的下标
    private int mode = 1;

    //1表示顺序播放 2表示循环播放  3单曲循环  4随机播放
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        //添加拖动事件，根据用户拖动的进度，改变当前音乐播放的进度
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //获得用户拖动的进度
                int progress = seekBar.getProgress();
                //把进度发给服务，
                // 告诉服务将播放的音乐按照拖动条的进度比例进行同步播放
                Intent intent = new Intent("service");
                intent.putExtra("progress", progress);
                sendBroadcast(intent);
            }
        });

        broad = new MyActivityBroad();
        IntentFilter filter = new IntentFilter("activity");
        registerReceiver(broad, filter);

        oList = new MusicUtil().getMusicList(this);
        if (oList != null && oList.size() > 0) {
            MusicAdapter adapter = new MusicAdapter(this, oList);
            lv_music.setAdapter(adapter);
            lv_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    music = oList.get(position);
                    index = position;
                    Intent intent = new Intent("service");
                    intent.putExtra("music", oList.get(position));
                    intent.putExtra("newMusic", 1);
                    sendBroadcast(intent);
                }
            });
        }
    }

    /*应用关闭后销毁后台服务*/
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        this.unregisterReceiver(broad);
        show("后台Service服务已销毁");
        super.onDestroy();
    }

    /*四种不同播放模式*/
    private PopupWindow window;

    public void showPopupWindow() {
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.popup_layout, null);
        ImageView iv_sx = view.findViewById(R.id.iv_sx);
        ImageView iv_xh = view.findViewById(R.id.iv_xh);
        ImageView iv_dq = view.findViewById(R.id.iv_dq);
        ImageView iv_sj = view.findViewById(R.id.iv_sj);
        iv_sx.setOnClickListener(listener);
        iv_dq.setOnClickListener(listener);
        iv_sj.setOnClickListener(listener);
        iv_xh.setOnClickListener(listener);
        window = new PopupWindow(view, btn_mode.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
        window.showAsDropDown(btn_mode);
    }

    /*双击退出*/
    private long lostClickTime = 0;

    @Override
    public void onBackPressed() {
        //按键时间差
        if (lostClickTime <= 0) {
            show("再按一次退出应用");
            lostClickTime = System.currentTimeMillis();
        } else {
            long currentClickTime = System.currentTimeMillis();
            if (currentClickTime - lostClickTime < 1000) {
                finish();
            } else {
                lostClickTime = currentClickTime;
                show("再按一次退出应用");
                finish();
            }
        }
    }

    private class MyActivityBroad extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            music = (Music) intent.getSerializableExtra("music");
            if (music != null) {
                tv_name.setText(music.getName());
            }
            boolean over = intent.getBooleanExtra("over", false);
            if (over) {
                Intent intent1 = new Intent("service");
                switch (mode) {
                    case 1:
                        //顺序播放
                        if (index == oList.size() - 1) {
                            show("列表播放结束");
                        } else {
                            music = oList.get(++index);
                        }
                        break;
                    case 2:
                        //循环播放
                        index = ++index % oList.size();
                        music = oList.get(index);
                        break;
                    case 3:
                        //单曲播放
                        music = oList.get(index);
                        break;
                    case 4:
                        //随机播放  0-4
                        music = oList.get(new Random().nextInt(oList.size()));
                        break;
                }
                intent1.putExtra("music", music);
                intent1.putExtra("newMusic", 1);
                sendBroadcast(intent1);
            }
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 1:
                    //未播放
                    iv_play.setImageResource(R.mipmap.play_button_default);
                    break;
                case 2:
                    //播放
                    iv_play.setImageResource(R.mipmap.pause_button_default);
                    break;
                case 3:
                    //暂停
                    iv_play.setImageResource(R.mipmap.play_button_default);
                    break;
            }
            //毫秒
            //当前播放的音乐的总时长
            int dur = intent.getIntExtra("dur", -1);
            //当前播放的音乐所播放的进度
            int pro = intent.getIntExtra("pro", -1);
            if (dur > 0) {
                tv_total_time.setText(getTime(dur));
            }
            if (pro > 0) {
                tv_cur_time.setText(getTime(pro));
            }
            if (dur > 0 && pro > 0) {
                int progress = (int) (pro * 100.0 / dur);
                seekBar.setProgress(progress);
            }
        }
    }

    public String getTime(int time) {
        String result = "";
        int total = time / 1000;//总共的秒数
        int munite = total / 60;//分钟
        int secod = total % 60;//秒数
        result = (munite < 10 ? "0" + munite : munite) + ":" + (secod < 10 ? "0" + secod : secod);
        return result;
    }

    /*点击事件*/
    private View.OnClickListener listener = new View.OnClickListener() {
        @SuppressLint("RtlHardcoded")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_mode:
                    showPopupWindow();
                    break;
                case R.id.btn_exit:
                    /* onBackPressed();*/
                    dl.openDrawer(Gravity.LEFT);
                    break;
                case R.id.iv_sx:
                    mode = 1;
                    show("顺序播放");
                    window.dismiss();
                    break;
                case R.id.iv_xh:
                    mode = 2;
                    show("循环播放");
                    window.dismiss();
                    break;
                case R.id.iv_dq:
                    mode = 3;
                    show("单曲循环");
                    window.dismiss();
                    break;
                case R.id.iv_sj:
                    mode = 4;
                    show("随机播放");
                    window.dismiss();
                    break;
                case R.id.iv_play:
                    play();
                    break;
                case R.id.iv_up:
                    up();
                    break;
                case R.id.iv_next:
                    next();
                    break;
                case R.id.btn_back:
                    finish();
                    break;
                /*侧拉菜单跳转设置*/
                case R.id.btn_setting:
                    Intent i = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(i);
                    break;
                /*自动关闭*/
                case R.id.btn_time_back:
                    show("软件将在5秒后自动关闭");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                /*我的云盘*/
                case R.id.btn_cl_pan:
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://pan.baidu.com");
                    intent.setData(content_url);
                    startActivity(intent);
                    break;
                case R.id.btn_cl_hy:
                    Intent hy_intent = new Intent(MainActivity.this, MyVIPActivity.class);
                    startActivity(hy_intent);
                    break;
                case R.id.btn_cl_xx:
                    show("哪来那么多消息，洗洗睡吧");
                    break;
                case R.id.btn_cl_ll:
                    show("运营商你家开的?");
                    break;
                case R.id.btn_cl_pf:
                    show("没钱哪来好看的衣服");
                    break;
                case R.id.btn_cl_yx:
                    show("索尼大法天下无双");
                    break;
                case R.id.btn_cl_gj:
                    show("你要锤子还是榔头?");
                    break;
                case R.id.btn_cl_cl:
                    Toast.makeText(MainActivity.this,"我爱你，爱着你，就想老鼠爱大米",Toast.LENGTH_LONG).show();
                    break;
                case R.id.btn_cl_sq:
                    show("索半斤天下第一");
                    break;
            }
        }
    };


    public void show(String info) {
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    public void play() {
        Intent intent = new Intent("service");
        //判断用户是否是第一次进入，默认选中第一首音乐进行播放
        if (music == null) {
            music = oList.get(index);
            intent.putExtra("music", music);
            intent.putExtra("newMusic", 1);
        } else {
            //暂停，播放
            intent.putExtra("contorl", 1);
        }
        sendBroadcast(intent);
    }

    public void up() {
        //当前播放的音乐为第一首音乐的时候
        //点击上一首，则播放列表的最后一首
        if (index == 0) {
            index = oList.size();
        }
        music = oList.get(--index % oList.size());
        Intent intent = new Intent("service");
        intent.putExtra("music", music);
        intent.putExtra("newMusic", 1);
        sendBroadcast(intent);
    }

    public void next() {
        music = oList.get(++index % oList.size());
        Intent intent = new Intent("service");
        intent.putExtra("music", music);
        intent.putExtra("newMusic", 1);
        sendBroadcast(intent);
    }

    /**
     * 初始化控件
     */
    public void initView() {
        Button btn_time_back = findViewById(R.id.btn_time_back);
        Button btn_back = findViewById(R.id.btn_back);
        Button btn_setting = findViewById(R.id.btn_setting);
        Button btn_cl_pan = findViewById(R.id.btn_cl_pan);
        Button btn_cl_hy = findViewById(R.id.btn_cl_hy);
        Button btn_cl_cl = findViewById(R.id.btn_cl_cl);
        Button btn_cl_gj = findViewById(R.id.btn_cl_gj);
        Button btn_cl_sq = findViewById(R.id.btn_cl_sq);
        Button btn_cl_yx = findViewById(R.id.btn_cl_yx);
        Button btn_cl_ll = findViewById(R.id.btn_cl_ll);
        Button btn_cl_pf = findViewById(R.id.btn_cl_pf);
        Button btn_cl_xx = findViewById(R.id.btn_cl_xx);
        dl = findViewById(R.id.drawer_main_layout);
        lv_music = findViewById(R.id.lv_music);
        seekBar = findViewById(R.id.seekbar);
        tv_cur_time = findViewById(R.id.tv_cur_time);
        tv_name = findViewById(R.id.tv_name);
        tv_total_time = findViewById(R.id.tv_total_time);
        ImageView iv_next = findViewById(R.id.iv_next);
        iv_play = findViewById(R.id.iv_play);
        ImageView iv_up = findViewById(R.id.iv_up);
        Button btn_exit = findViewById(R.id.btn_exit);
        btn_mode = findViewById(R.id.btn_mode);
        iv_play.setOnClickListener(listener);
        iv_up.setOnClickListener(listener);
        iv_next.setOnClickListener(listener);
        btn_mode.setOnClickListener(listener);
        btn_exit.setOnClickListener(listener);
        btn_back.setOnClickListener(listener);
        btn_time_back.setOnClickListener(listener);
        btn_setting.setOnClickListener(listener);
        btn_cl_pan.setOnClickListener(listener);
        btn_cl_hy.setOnClickListener(listener);
        btn_cl_cl.setOnClickListener(listener);
        btn_cl_gj.setOnClickListener(listener);
        btn_cl_sq.setOnClickListener(listener);
        btn_cl_yx.setOnClickListener(listener);
        btn_cl_ll.setOnClickListener(listener);
        btn_cl_pf.setOnClickListener(listener);
        btn_cl_xx.setOnClickListener(listener);
        //开启Service服务
        startService(new Intent(this, MusicService.class));
    }
    /*

     */
    /*图片的点击*//*

    Bitmap bp=null;
    float scaleWidth;
    float scaleHeight;
    boolean num=false;
    @SuppressLint("ClickableViewAccessibility")
    public void imageButton(){
        DisplayMetrics dm=new DisplayMetrics();//创建矩阵
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        imageview=(ImageView)findViewById(R.id.iv_cl);
        bp=BitmapFactory.decodeResource(getResources(),R.mipmap.my);
        int width=bp.getWidth();
        int height=bp.getHeight();
        int w=dm.widthPixels; //得到屏幕的宽度
        int h=dm.heightPixels; //得到屏幕的高度
        scaleWidth=((float)w)/width;
        scaleHeight=((float)h)/height;
        imageview.setImageBitmap(bp);
        imageview.setImageBitmap(bp);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:  //当屏幕检测到第一个触点按下之后就会触发到这个事件。
                if(num==true)        {
                    Matrix matrix=new Matrix();
                    matrix.postScale(scaleWidth,scaleHeight);

                    Bitmap newBitmap=Bitmap.createBitmap(bp, 0, 0, bp.getWidth(), bp.getHeight(), matrix, true);
                    imageview.setImageBitmap(newBitmap);
                    num=false;
                }
                else{
                    Matrix matrix=new Matrix();
                    matrix.postScale(1.0f,1.0f);
                    Bitmap newBitmap=Bitmap.createBitmap(bp, 0, 0, bp.getWidth(), bp.getHeight(), matrix, true);
                    imageview.setImageBitmap(newBitmap);
                    num=true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
*/

}
