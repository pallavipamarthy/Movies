package com.axiom.movies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.axiom.movies.data.Movie;
import com.axiom.movies.data.MovieContract.MovieEntry;

import java.util.ArrayList;
import java.util.List;

public class FavouriteMovieLoader extends AsyncTaskLoader<List<Movie>> {

    FavouriteMovieLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {

        Cursor cursor = getContext().getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);
        List<Movie> favouriteMovies = new ArrayList<>();

        while (cursor.moveToNext()) {
            String movieId = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_ID));
            String movieTitle = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_TITLE));
            String overview = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_OVERVIEW));
            String posterPath = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_POSTER_PATH));
            String releaseDate = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_RELEASE_DATE));
            String rating = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_RATING));
            String trailerkey1 = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_TRAILER1_KEY));
            String trailerkey2 = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_TRAILER2_KEY));
            Movie currentMovie = new Movie(movieId, posterPath, overview, movieTitle, rating, releaseDate);
            currentMovie.setTrailerKey1(trailerkey1);
            currentMovie.setTrailerKey2(trailerkey2);
            favouriteMovies.add(currentMovie);
        }
        cursor.close();
        return favouriteMovies;
    }
}

