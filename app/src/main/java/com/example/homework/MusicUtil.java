package com.example.homework;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MusicUtil {

    public ArrayList<MusicInfo> musicInfoList;//将音乐信息填充到该集合中

    //获取手机上所有歌曲
    public ArrayList<MusicInfo> getMusicInfo(Context context) {
        musicInfoList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        //通过mediaStore获取本地歌曲
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null && cursor.getCount() > 0) {
            MusicInfo musicInfo;
            while (cursor.moveToNext()) {
                musicInfo = new MusicInfo();
                //设置单曲的歌手
                musicInfo.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                //设置单曲的名称
                musicInfo.setTitle((cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));
                //设置单曲的时长
                musicInfo.setDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                //设置单曲的路径
                musicInfo.setUrl(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                musicInfoList.add(musicInfo);
            }
        }
        cursor.close();
        return musicInfoList;
    }

    /**
     * 时间格式化
     *
     * @param oldTime
     * @return
     */
    public static String parseTime(int oldTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");// 时间格式
        String newTime = sdf.format(new Date(oldTime));
        return newTime;
    }
}