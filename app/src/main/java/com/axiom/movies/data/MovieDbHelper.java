package com.axiom.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.axiom.movies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "favouriteMovies.db";
    private static final int DATABASE_VERSION=1;

    public MovieDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        String SQ_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + " INTEGER PRIMARY KEY," +MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL," + MovieEntry.COLUMN_MOVIE_TITLE +" TEXT NOT NULL,"
                + MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL," + MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL,"   + MovieEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL,"  + MovieEntry.COLUMN_TRAILER1_KEY + " TEXT," +
                MovieEntry.COLUMN_TRAILER2_KEY + " TEXT);";
        db.execSQL(SQ_CREATE_MOVIES_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {

    }
}
