package com.uumedia.ui.movie;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uumedia.MainActivity;
import com.uumedia.utils.SearchUtils;
import com.uumedia.ui.NewInstance;
import com.uumedia.R;
import com.uumedia.common.CommonBaseAdapter;
import com.uumedia.common.CommonViewHolder;
import com.uumedia.entity.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieListFragment extends Fragment implements NewInstance,CommonBaseAdapter.OnItemClickListener<Movie> {

    @BindView(R.id.movie_rv)
    RecyclerView movie_rv;
    MainActivity mainActivity;

    @OnClick(R.id.back_iv)
    public void back(){

        mainActivity.setCurrentItem(1);

    }

    private List<Movie> list = new ArrayList<>();
    private CommonBaseAdapter<Movie> adapter;

    public MovieListFragment(MainActivity mainActivity) {
        this.mainActivity=mainActivity;
    }

    public Fragment newInstance(int sectionNumber) {
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {

        list.clear();

        list.addAll(SearchUtils.getMovieList(getActivity()));

        adapter=new CommonBaseAdapter<Movie>(getActivity(),list) {
            @Override
            protected void convert(CommonViewHolder holder, Movie item, int position) {

                holder.setText(R.id.title_tv,item.getTitle());
                holder.setText(R.id.size_tv,"视频大小："+item.getSize()/1024/1024+"M");

            }

            @Override
            protected int getItemViewLayoutId(int position, Movie item) {
                return R.layout.item_movie;
            }
        };

        adapter.setOnItemClickListener(this);
        movie_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        movie_rv.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, Movie item, int position) {

        Intent i=new Intent();
        i.putExtra("position",position);
        i.putParcelableArrayListExtra("movies", (ArrayList<? extends Parcelable>) list);
        i.setClass(getActivity(),PlayMovieActivity.class);
        startActivity(i);

    }
}
