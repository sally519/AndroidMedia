package com.uumedia.ui.music;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.uumedia.MainActivity;
import com.uumedia.R;
import com.uumedia.common.CommonBaseAdapter;
import com.uumedia.common.CommonViewHolder;
import com.uumedia.utils.SearchUtils;
import com.uumedia.utils.SpUtil;
import com.uumedia.utils.VoiceUtils;
import com.uumedia.entity.Music;
import com.uumedia.ui.NewInstance;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MusicListFragment extends Fragment implements NewInstance, CommonBaseAdapter.OnItemClickListener<Music> {

    private List<Music> list = new ArrayList<>();
    private CommonBaseAdapter<Music> adapter;
    int model = PlayAction.QUEUE;
    int nowSong = 1;
    boolean isVoiceSeekBarShow = false;
    MainActivity mainActivity;

    @BindView(R.id.music_rv)
    RecyclerView music_rv;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.play_music_im)
    ImageView play_music_im;
    @BindView(R.id.model_ib)
    ImageButton model_ib;
    @BindView(R.id.song_title)
    TextView song_title;
    @BindView(R.id.current_time_tv)
    TextView current_time_tv;
    @BindView(R.id.total_time_tv)
    TextView total_time_tv;
    @BindView(R.id.voice_sb)
    SeekBar voice_sb;

    @OnClick(R.id.voice_iv)
    public void changeVoice() {

        voice_sb.setMax(VoiceUtils.getMaxVolume(getActivity()));
        voice_sb.setProgress(VoiceUtils.getCurrentVolume(getActivity()));
        if (isVoiceSeekBarShow) {
            voice_sb.setVisibility(View.INVISIBLE);
            isVoiceSeekBarShow = false;
        } else {
            voice_sb.setVisibility(View.VISIBLE);
            isVoiceSeekBarShow = true;
        }

    }

    @OnClick(R.id.back_iv)
    public void backToProvicePage() {
        mainActivity.setCurrentItem(1);
    }

    @OnClick(R.id.model_ib)
    public void changePlayModel() {

        if (model == PlayAction.QUEUE) {
            model = PlayAction.CYCLE;
        } else if (model == PlayAction.CYCLE) {
            model = PlayAction.RANDOM;
        } else {
            model = PlayAction.QUEUE;
        }

        switch (model) {
            case PlayAction.QUEUE:
                changePlayModel(PlayAction.QUEUE, getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
                break;
            case PlayAction.CYCLE:
                changePlayModel(PlayAction.CYCLE, getResources().getDrawable(R.drawable.ic_repeat_one_black_24dp));
                break;
            case PlayAction.RANDOM:
                changePlayModel(PlayAction.RANDOM, getResources().getDrawable(R.drawable.ic_shuffle_black_24dp));
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.provice_ib)
    public void playProviceSong() {
        if (myBinder != null) {
            if (myBinder.isPlaying()) {
                boolean isNowSongOutOfSize = nowSong - 1 < 1 || nowSong + 1 > list.size();
                if (isNowSongOutOfSize) {
                    nowSong = list.size();
                }
                switchModelToPlay(PlayAction.PROVICE_SONG);
            }
        }
    }

    @OnClick(R.id.next_ib)
    public void playNextSong() {
        if (myBinder != null) {
            if (myBinder.isPlaying()) {
                boolean isLastSong = nowSong == list.size() - 1;
                if (isLastSong) {
                    nowSong = PlayAction.PROVICE_SONG;
                }
                switchModelToPlay(PlayAction.NEXT_SONG);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.play_music_im)
    public void puase() {
        if (myBinder != null) {
            if (myBinder.isPlaying()) {
                onMusicPause(PlayAction.PAUSE, conn, getActivity().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));
            } else {
                onMusicPause(PlayAction.RESTART, conn, getActivity().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
            }
        } else {
            double v = Math.random() * list.size();
            nowSong = (int) v;
            song_title.setText(list.get((int) v).getTitle());
            onMusicPause(list.get((int) v).getPath(), conn, getActivity().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recept();
                    seekBar.setMax(myBinder.getDuration());
                    total_time_tv.setText(myBinder.getDuration() / 1000 / 60 + ":" + myBinder.getDuration() / 1000 % 60);
                }
            }, 50);
        }
    }

    private void changePlayModel(int value, Drawable drawable) {
        SpUtil.SetIntergerSF(getActivity(), PlayAction.MODEL, value);
        model_ib.setBackground(drawable);
    }

    private void onMusicPause(String restart, ServiceConnection conn, Drawable drawable) {
        Intent intent = new Intent(getActivity(), MusicIntentService.class);
        intent.putExtra(PlayAction.MUSIC_PLAY_ACTION, restart);
        getActivity().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        getActivity().getApplicationContext().startService(intent);
        play_music_im.setBackground(drawable);
    }

    private void switchModelToPlay(int i) {
        switch (model) {
            case PlayAction.QUEUE:
                nowSong = nowSong + i;
                break;
            case PlayAction.CYCLE:
                break;
            case PlayAction.RANDOM:
                nowSong = (int) (Math.random() * list.size());
                break;
            default:
                break;
        }
        playMusic(nowSong);
    }

    private void playMusic(int nowSong) {
        Intent intent = new Intent(getActivity(), MusicIntentService.class);
        intent.putExtra(PlayAction.MUSIC_PLAY_ACTION, list.get(this.nowSong).getPath());
        getActivity().getApplicationContext().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        getActivity().getApplicationContext().startService(intent);
        song_title.setText(list.get(nowSong).getTitle());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recept();
                seekBar.setMax(myBinder.getDuration());
                total_time_tv.setText(myBinder.getDuration() / 1000 / 60 + ":" + myBinder.getDuration() / 1000 % 60);
            }
        }, 50);
    }

    Handler handler = new Handler();
    Runnable runable = new Runnable() {
        @Override
        public void run() {
            if (myBinder.isPlaying()) {
                current_time_tv.setText(myBinder.getCurrentPosition() / 1000 / 60 + ":" + myBinder.getCurrentPosition() / 1000 % 60);
                seekBar.setProgress(myBinder.getCurrentPosition());
                if (myBinder.getDuration() - myBinder.getCurrentPosition() < 2000) {
                    playNextSong();
                } else {
                    recept();
                }
            }
        }
    };

    MusicIntentService.MyBinder myBinder;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicIntentService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public MusicListFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {

        list.clear();
        list.addAll(SearchUtils.getMusicList(getActivity()));

        adapter = new CommonBaseAdapter<Music>(getActivity(), list) {
            @Override
            protected void convert(CommonViewHolder holder, Music item, int position) {

                holder.setText(R.id.title_tv, item.getTitle());
                holder.setText(R.id.size_tv, "歌手：" + item.getSinger());

            }

            @Override
            protected int getItemViewLayoutId(int position, Music item) {
                return R.layout.item_music;
            }
        };

        adapter.setOnItemClickListener(this);
        music_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        music_rv.setAdapter(adapter);

        voice_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                VoiceUtils.setVolume(getActivity(), seekBar.getProgress());
                voice_sb.setVisibility(View.INVISIBLE);
                isVoiceSeekBarShow = false;

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Intent intent = new Intent(getActivity(), MusicIntentService.class);
                intent.putExtra(PlayAction.MUSIC_PLAY_ACTION, PlayAction.SET_PROGRESS);
                intent.putExtra("time", seekBar.getProgress());
                getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
                getActivity().getApplicationContext().startService(intent);

            }
        });

        model = SpUtil.getIntergerSF(getActivity(), PlayAction.MODEL);
        switch (model) {
            case PlayAction.QUEUE:
                model_ib.setBackground(getResources().getDrawable(R.drawable.ic_repeat_black_24dp));
                break;
            case PlayAction.CYCLE:
                model_ib.setBackground(getResources().getDrawable(R.drawable.ic_repeat_one_black_24dp));
                break;
            case PlayAction.RANDOM:
                model_ib.setBackground(getResources().getDrawable(R.drawable.ic_shuffle_black_24dp));
                break;
            default:
                break;
        }
    }

    @Override
    public Fragment newInstance(int sectionNumber) {
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemClick(View view, Music item, int position) {

        onMusicPause(item.getPath(), conn, getActivity().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recept();
                seekBar.setMax(myBinder.getDuration());
                total_time_tv.setText(myBinder.getDuration() / 1000 / 60 + ":" + myBinder.getDuration() / 1000 % 60);
            }
        }, 50);
        nowSong = position;
        song_title.setText(item.getTitle());
    }

    private void recept() {
        handler.postDelayed(runable, 100);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        if (myBinder != null) {
            if (myBinder.isPlaying()) {
                play_music_im.setBackground(getActivity().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
            }
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(conn);
        super.onDestroy();
    }
}
