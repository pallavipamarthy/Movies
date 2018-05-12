package com.axiom.movies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieJson {

    @SerializedName("results")
    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }
}