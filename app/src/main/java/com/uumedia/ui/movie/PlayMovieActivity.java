package com.uumedia.ui.movie;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.uumedia.R;
import com.uumedia.entity.Movie;

import java.util.List;

public class PlayMovieActivity extends AppCompatActivity {

    VideoView play_movie_vv;
    ImageView back_iv;

    String moviePath;
    int currentMovie;
    int size;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_movie);

        initView();
    }

    private void initView() {

        back_iv = (ImageView) findViewById(R.id.back_iv);
        play_movie_vv = (VideoView) findViewById(R.id.play_movie_vv);

        final List<Movie> list = getIntent().getParcelableArrayListExtra("movies");
        final int position = getIntent().getIntExtra("position", 0);
        currentMovie = position;
        size = list.size();

        moviePath = list.get(position).getPath();
        play_movie_vv.setVideoPath(moviePath); //设置播放路径
        play_movie_vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                play_movie_vv.start();
            }
        });
        // 设置video的控制器
        MediaController mediaController = new MediaController(this);
        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//后退
                if (currentMovie < size-1) {
                    currentMovie++;
                }
                play_movie_vv.setVideoPath(list.get(currentMovie).getPath()); //设置播放路径
                play_movie_vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        play_movie_vv.start();
                    }
                });

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {//前进

                if (currentMovie >= 1) {
                    currentMovie--;
                }
                play_movie_vv.setVideoPath(list.get(currentMovie).getPath()); //设置播放路径
                play_movie_vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        play_movie_vv.start();
                    }
                });
            }
        });
        play_movie_vv.setMediaController(mediaController);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        play_movie_vv.stopPlayback();
    }

}
