package com.axiom.movies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewJson {
    @SerializedName("results")
    private List<Review> reviewResults;

    public List<Review> getReviewResults() {
        return reviewResults;
    }
}
