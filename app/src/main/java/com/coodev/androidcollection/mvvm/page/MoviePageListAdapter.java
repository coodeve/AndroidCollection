package com.coodev.androidcollection.mvvm.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.coodev.androidcollection.R;
import com.coodev.androidcollection.entity.Movies;

public class MoviePageListAdapter extends PagedListAdapter<Movies.Movie, MoviePageListAdapter.MovieViewHolder> {

    private static DiffUtil.ItemCallback<Movies.Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movies.Movie>() {


        @Override
        public boolean areItemsTheSame(@NonNull Movies.Movie oldItem, @NonNull Movies.Movie newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Movies.Movie oldItem, @NonNull Movies.Movie newItem) {
            return oldItem.equals(newItem);
        }
    };

    private Context mContext;

    public MoviePageListAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.list_item_movie,parent,false);
        return new MovieViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movies.Movie item = getItem(position);
        if (item != null) {
            // 加载图片等处理
        }else{
            // 空数据处理
        }
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView title;
        TextView year;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
