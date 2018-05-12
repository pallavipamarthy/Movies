package com.axiom.movies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constants {

    //Please replace "xxxxxx" with your API KEY to the below constant eg: "api_key=mop3202fke0kek"
    public static final String API_KEY = "3fcc30037deb04fbcce0342f22a43b87";

    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500";
    public static final String YOUTUBE_VIDEO_QUERY = "http://www.youtube.com/watch?v=";

    public static final int GRID_TYPE_POPULAR = 1;
    public static final int GRID_TYPE_TOP_RATED = 2;
    public static final int GRID_TYPE_FAVOURITES = 3;

    public static final int DEVICE_TYPE_PHONE = 1;
    public static final int DEVICE_TYPE_TABLET = 2;
    public static final int DEVICE_TYPE_LARGE_TABLET = 3;

    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
