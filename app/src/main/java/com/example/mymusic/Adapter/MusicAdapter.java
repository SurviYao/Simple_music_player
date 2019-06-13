package com.example.mymusic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mymusic.Tools.Music;
import com.example.mymusic.R;

import java.util.List;

public class MusicAdapter extends BaseAdapter {
    private Context context;
    private List<Music> oList;
    private LayoutInflater inflater;

    public MusicAdapter(Context context, List<Music> oList) {
        this.context = context;
        this.oList = oList;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return oList.size();
    }

    @Override
    public Object getItem(int position) {
        return oList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.music_item,null);
            holder.tv_item=convertView.findViewById(R.id.tv_item);
            convertView.setTag(holder);
        }else
            holder= (ViewHolder) convertView.getTag();
            holder.tv_item.setText(oList.get(position).getName());
             return convertView;
    }

    private class ViewHolder{
        private TextView tv_item;
    }
}
