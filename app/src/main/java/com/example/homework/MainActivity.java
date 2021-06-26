package com.example.homework;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.example.homework.MusicUtil.parseTime;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    private ListView musicListView;              //lsitview展示数据的
    private SeekBar mSeekBar;                    //seekbar 进度条显示
    private TextView mCurrentTimeTv;             //播放时的时间
    private TextView mTotalTimeTv;               //歌曲总时间
    private ImageView up;                        //上一曲按钮
    private ImageView play;                      //播放暂停按钮
    private ImageView next;                      //下一曲按钮
    private TextView now;                        //当前播放歌曲名称
    private TextView now_name;                   //当前歌手
    private ImageView stop;                      //停止播放
    private ImageView random;                    //随机播放模式
    private ImageView repeat;                    //单曲循环模式
    private int flag = 0;                        //1：随机播放  2：单曲循环

    private ArrayList<MusicInfo> musicList;  //装了音乐信息的listView
    private int currentPosition;    //当前音乐播放位置
    private MediaPlayer mediaPlayer;
    private final MusicUtil util = new MusicUtil();

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            // 展示给进度条和当前时间
            int progress = mediaPlayer.getCurrentPosition();
            mSeekBar.setProgress(progress);
            mCurrentTimeTv.setText(parseTime(progress));
            // 继续定时发送数据
            updateProgress();
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //界面全屏化
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        final ImageView abulm = findViewById(R.id.image_abulm_on);
        new CountDownTimer(100, 1000) {//设置图片旋转
            public void onTick(long millisUntilFinished) {
            }

            int i = 1;

            @Override
            public void onFinish() {
                abulm.setPivotX(Float.parseFloat(String.valueOf(abulm.getWidth() / 2)));
                abulm.setPivotY(Float.parseFloat(String.valueOf(abulm.getHeight() / 2)));//图片中心为支点  
                abulm.setRotation(i);//每秒转动1
                i = i + 1;
                if (i == 361) i = 1;
                start();
            }
        }.start();

        //实例化控件
        musicListView = findViewById(R.id.musicListView);
        mSeekBar = findViewById(R.id.seekBar);
        mCurrentTimeTv = findViewById(R.id.current_time_tv);
        mTotalTimeTv = findViewById(R.id.total_time_tv);
        up = findViewById(R.id.previous);
        play = findViewById(R.id.play_pause);
        next = findViewById(R.id.next);
        now = findViewById(R.id.now);
        now_name = findViewById(R.id.now_name);
        stop = findViewById(R.id.stop);
        random = findViewById(R.id.play_random);
        repeat = findViewById(R.id.play_repeat);

        mediaPlayer = new MediaPlayer();

        //设置点击事件
        up.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        stop.setOnClickListener(this);
        random.setOnClickListener(this);
        repeat.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);//监听音乐播放完毕事件，自动下一曲
        initData();
    }


    /**
     * 初始化数据
     */
    private void initData() {
        musicList = new ArrayList<>();
        musicList = util.getMusicInfo(this);
        MusicAdapter adapter = new MusicAdapter(this, musicList);
        musicListView.setAdapter(adapter);
        //adapter item的点击事件
        musicListView.setOnItemClickListener((parent, view, position, id) -> {
            currentPosition = position;     //获取当前点击条目的位置
            changeMusic(currentPosition);   //切歌
            play.setImageResource(R.drawable.ic_pause_normal);
            String title = musicList.get(currentPosition).getTitle();
            String artist = musicList.get(currentPosition).getArtist();
            now.setText(title);       //展示当前播放的歌名
            now_name.setText(artist);
        });

    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.previous) {//上一曲
            changeMusic(--currentPosition);
            play.setImageResource(R.drawable.ic_pause_normal);
            String title = musicList.get(currentPosition).getTitle();
            String artist = musicList.get(currentPosition).getArtist();

            now.setText(title); //展示上一曲的歌名
            now_name.setText(artist);
        } else if (view.getId() == R.id.play_pause) {//暂停/播放
            // 首次点击播放按钮，默认播放第0首
            if (mediaPlayer == null) {
                changeMusic(0);
                String title = musicList.get(currentPosition + 1).getTitle();
                String artist = musicList.get(currentPosition + 1).getArtist();
                now.setText(title);
                now_name.setText(artist);
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.ic_play_normal);
                } else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.ic_pause_normal);
                }
            }
        } else if (view.getId() == R.id.next) {// 下一首
            changeMusic(++currentPosition);
            play.setImageResource(R.drawable.ic_pause_normal);
            String title = musicList.get(currentPosition).getTitle();
            String artist = musicList.get(currentPosition).getArtist();
            now.setText(title);//展示下一首的歌名
            now_name.setText(artist);

        } else if (view.getId() == R.id.stop) {
            play.setImageResource(R.drawable.ic_play_normal);
            mediaPlayer.stop();
        } else if (view.getId() == R.id.play_random) {//随机播放
            random.setImageResource(R.drawable.ic_shuffle_glow);
            repeat.setImageResource(R.drawable.ic_repeat);
            flag = 1;
        } else if (view.getId() == R.id.play_repeat) {//单曲循环
            repeat.setImageResource(R.drawable.ic_repeat_glow);
            random.setImageResource(R.drawable.ic_shuffle);
            flag = 2;
        }
    }

    private void changeMusic(int position) {
        if (position < 0) {
            currentPosition = position = musicList.size() - 1;
        } else if (position > musicList.size() - 1) {
            currentPosition = position = 0;
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        try {
            // 切歌之前先重置，释放掉之前的资源
            mediaPlayer.reset();
            // 设置播放源
            mediaPlayer.setDataSource(musicList.get(position).getUrl());
            // 开始播放前的准备工作，加载多媒体资源，获取相关信息
            mediaPlayer.prepare();

            // 开始播放
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSeekBar.setProgress(0);//将进度条初始化
        mSeekBar.setMax(mediaPlayer.getDuration());//设置进度条最大值为歌曲总时间
        mTotalTimeTv.setText(parseTime(mediaPlayer.getDuration()));//显示歌曲总时长

        updateProgress();//更新进度条
    }

    private void updateProgress() {
        // 使用Handler每间隔1s发送一次空消息，通知进度条更新
        Message msg = Message.obtain();// 获取一个现成的消息
        // 使用MediaPlayer获取当前播放时间除以总时间的进度
        msg.arg1 = mediaPlayer.getCurrentPosition();
        mHandler.sendMessageDelayed(msg, INTERNAL_TIME);
    }

    private static final int INTERNAL_TIME = 1000;// 音乐进度间隔时间

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    // 当手停止拖拽进度条时执行该方法
    // 获取拖拽进度
    // 将进度对应设置给MediaPlayer
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        mediaPlayer.seekTo(progress);
    }

    //自动播放下一曲
    //这个回调是 当一首歌播放完毕后会进入
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (flag == 1) {
            currentPosition = new Random().nextInt(musicList.size());
            changeMusic(currentPosition);
            Toast.makeText(MainActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
            String title = musicList.get(currentPosition).getTitle();
            String artist = musicList.get(currentPosition).getArtist();
            now.setText(title);//展示下一首的歌名
            now_name.setText(artist);
        }
        if (flag == 2) {
            currentPosition++;
            currentPosition--;
            changeMusic(currentPosition);
            Toast.makeText(MainActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            String title = musicList.get(currentPosition).getTitle();
            String artist = musicList.get(currentPosition).getArtist();
            now.setText(title);//展示下一首的歌名
            now_name.setText(artist);
        } else {//顺序播放
            changeMusic(++currentPosition);
            String title = musicList.get(currentPosition).getTitle();
            String artist = musicList.get(currentPosition).getArtist();
            now_name.setText(artist);
            now.setText(title);//展示下一首的歌名
        }
    }
}
