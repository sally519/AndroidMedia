package com.uumedia.ui.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Shaoqiang.Zhang on 2017/5/17.
 */

public class MusicIntentService extends Service {

    MediaPlayer mediaPlayer;
    MyBinder myBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            switch (intent.getStringExtra(PlayAction.MUSIC_PLAY_ACTION)) {
                case PlayAction.PAUSE:
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                    break;
                case PlayAction.RESTART:
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                    break;
                case PlayAction.SET_PROGRESS:
                    //拖完进度条以后定位到指定位置播放
                    if(mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(intent.getIntExtra("time", 0));
                    }
                    break;
                default:
                    play(intent.getStringExtra(PlayAction.MUSIC_PLAY_ACTION));
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void play(String path) {
        try {

            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public class MyBinder extends Binder {

        public int getCurrentPosition() {
            return mediaPlayer.getCurrentPosition();
        }

        public int getDuration() {
            return mediaPlayer.getDuration();
        }

        public boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }
    }
}
