package com.axiom.movies;

import com.axiom.movies.data.MovieJson;
import com.axiom.movies.data.ReviewJson;
import com.axiom.movies.data.TrailerJson;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieAPI {
    @GET("popular")
    Call<MovieJson> getPopularMovies(@Query("api_key") String apikey);

    @GET("top_rated")
    Call<MovieJson> getTopRatedMovies(@Query("api_key") String apikey);

    @GET("movie/{id}")
    Call<MovieJson> getMovie(@Path("id") int id, @Query("api_key") String apikey);

    @GET("{id}/reviews")
    Call<ReviewJson> getReviews(@Path("id") String id, @Query("api_key") String apikey);

    @GET("{id}/videos")
    Call<TrailerJson> getTrailers(@Path("id") String id, @Query("api_key") String apikey);

}
