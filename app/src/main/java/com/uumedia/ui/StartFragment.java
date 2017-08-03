package com.uumedia.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uumedia.ui.NewInstance;
import com.uumedia.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartFragment extends Fragment implements NewInstance {

    @BindView(R.id.icon_iv)
    ImageView icon_iv;
    @BindView(R.id.movie_tv)
    TextView movie_tv;
    @BindView(R.id.music_tv)
    TextView music_tv;

    Handler handler = new Handler();
    Runnable icon;
    Runnable text;
    int time = 0;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public StartFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public Fragment newInstance(int sectionNumber) {
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        initView();

        return rootView;
    }

    private void initView() {

        final ObjectAnimator animator = ObjectAnimator.ofFloat(icon_iv, "rotation", 0f, 360f);
        animator.setDuration(1500);
        repetAnmo(animator);
        repetText();
    }

    private void repetText() throws IllegalStateException {
        try {

            time++;

            switch (time % 3) {
                case 0:
                    music_tv.setTextColor(getResources().getColor(R.color.red));
                    movie_tv.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 1:
                    music_tv.setTextColor(getResources().getColor(R.color.black));
                    movie_tv.setTextColor(getResources().getColor(R.color.green));
                    break;
                case 2:
                    music_tv.setTextColor(getResources().getColor(R.color.green));
                    movie_tv.setTextColor(getResources().getColor(R.color.red));
                    break;
                default:
                    break;
            }

            text = new Runnable() {
                @Override
                public void run() {
                    repetText();
                }
            };
            handler.postDelayed(text, 800);
        } catch (Exception e) {
            Log.e("message","出错啦");
        }
    }

    private void repetAnmo(final ObjectAnimator anim) {

        icon = new Runnable() {
            @Override
            public void run() {
                anim.start();
                repetAnmo(anim);
            }
        };
        handler.postDelayed(icon, 1400);
    }
}
