package com.example.mymusic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static android.widget.ImageView.OnClickListener;

public class LoadingActivity extends Activity {

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    private int[] dress = {
            R.mipmap.music_1,
            R.mipmap.music_2,
            R.mipmap.music_3,
            R.mipmap.music_4
    };

    private ImageView[] images =  new ImageView[dress.length];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        checkPermission(LoadingActivity.this);
        ViewPager vp = findViewById(R.id.vp);
        initImageView();
        vp.setAdapter(adapter);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                images[dress.length - 1].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent();
                        i.setClass(LoadingActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    void initImageView() {
        for (int i = 0; i < images.length; i++) {
            images[i] = new ImageView(this);
            ViewPager.LayoutParams Params = new ViewPager.LayoutParams();
            images[i].setLayoutParams(Params);
            images[i].setImageResource(dress[i]);
        }

    }

    PagerAdapter adapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(images[position]);
            return images[position];//返回加载值
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(images[position]);
        }
    };
    public void checkPermission(Activity activity) {
        //读写权限
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }
}
