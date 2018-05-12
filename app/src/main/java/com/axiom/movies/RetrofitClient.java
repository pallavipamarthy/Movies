package com.axiom.movies;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public static Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl(MOVIE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}

