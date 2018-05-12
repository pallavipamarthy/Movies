package com.axiom.movies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerJson {

    @SerializedName("results")
    private List<Trailer> trailerResult;

    public List<Trailer> getTrailerResults() {
        return trailerResult;
    }
}
