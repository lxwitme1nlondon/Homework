package com.example.homework;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//音乐播放器的适配器
class MusicAdapter extends BaseAdapter {

    private final ArrayList<MusicInfo> musicList;  //装了音乐信息的listView
    private final Context context;

    public MusicAdapter(Context context, ArrayList<MusicInfo> musicList) {
        this.musicList = musicList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public MusicInfo getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.music_item, null);
            viewHolder = new ViewHolder();
            viewHolder.video_imageView = convertView.findViewById(R.id.video_imageView);
            viewHolder.video_title = convertView.findViewById(R.id.video_title);
            viewHolder.video_singer = convertView.findViewById(R.id.video_singer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MusicInfo item = getItem(position);
        //viewHolder.video_singer.setText("歌手:" + item.getArtist());
        //viewHolder.video_title.setText("歌名:" + item.getTitle());
        viewHolder.video_singer.setText(context.getString(R.string.singer_text, item.getArtist()));
        viewHolder.video_title.setText(context.getString(R.string.name_text, item.getTitle()));
        return convertView;
    }

    static class ViewHolder {
        ImageView video_imageView;
        TextView video_title;
        TextView video_singer;
    }
}