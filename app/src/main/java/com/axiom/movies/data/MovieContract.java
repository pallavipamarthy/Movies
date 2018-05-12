package com.axiom.movies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popularmovies2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    private MovieContract() {
    }

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public final static String TABLE_NAME = "movies";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MOVIE_ID = "movie_id";
        public final static String COLUMN_MOVIE_TITLE = "title";
        public final static String COLUMN_MOVIE_OVERVIEW = "overview";
        public final static String COLUMN_POSTER_PATH = "poster_path";
        public final static String COLUMN_MOVIE_RATING = "rating";
        public final static String COLUMN_RELEASE_DATE = "release_date";
        public final static String COLUMN_TRAILER1_KEY = "trailer1_key";
        public final static String COLUMN_TRAILER2_KEY = "trailer2_key";
    }
}
