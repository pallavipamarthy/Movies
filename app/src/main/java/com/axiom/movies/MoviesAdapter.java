package com.axiom.movies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.axiom.movies.data.Movie;
import com.axiom.movies.utilities.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> mMoviesList;
    private Context mContext;

    //Pass data objects and context to constructor
    MoviesAdapter(Context context, List<Movie> moviesList) {
        mContext = context;
        mMoviesList = moviesList;
    }

    @Override
    //inflate each item in the grid layout
    @NonNull
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.grid_item, viewGroup, false);
        ButterKnife.bind(this, view);
        return new MovieViewHolder(view);
    }

    //create view holder with views
    //Onclick on each grid item sends an intent to launch detail activity
    class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_poster_image_view)
        ImageView mMovieImageView;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.movie_poster_image_view)
        public void onClick(View v) {
            Intent intent = new Intent(mContext, MovieDetailActivity.class);
            intent.putExtra("movie_obj", mMoviesList.get(getAdapterPosition()));
            mContext.startActivity(intent);
        }
    }


    //Binding data to the views
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        String imageUrl = mMoviesList.get(position).getImagePath();
        String imagePath = Constants.POSTER_BASE_URL + imageUrl;
        Glide.with(mContext).load(imagePath).override(720, 1108).placeholder(R.mipmap.default_placeholder).into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    //Set movie object passed from load finished to here.
    public void setData(List<Movie> moviesList) {
        mMoviesList.clear();
        mMoviesList = moviesList;
        notifyDataSetChanged();
    }
}
