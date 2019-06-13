package com.example.mymusic.Utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.mymusic.Tools.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicUtil {

    /*
     * 通过内容接收者获得本地音乐列表
     */
    public List<Music> getMusicList(Context context) {
        List<Music> oList = new ArrayList<>();
        //获得内容接收者对象
        ContentResolver resolver=context.getContentResolver();
        //查询音乐，并返回至cursor保存
        @SuppressLint("Recycle")
        Cursor cursor=resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,null, null,null);
        //循环读取所有音乐
        assert cursor != null;
        while (cursor.moveToNext()){
            //获得歌名
            String name=cursor.getString
                    (cursor.getColumnIndex
                            (MediaStore.Audio.Media.TITLE));
           //获得音乐路径
            String path=cursor.getString(
                    cursor.getColumnIndex
                            (MediaStore.Audio.Media.DATA));
           //将查询的信息保存至音乐实体类
            Music music=new Music(name,path);
            //将每一首歌都保存至音乐集合0
            oList.add(music);
        }
        return oList;
    }
}
