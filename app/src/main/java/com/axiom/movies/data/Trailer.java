package com.axiom.movies.data;

import com.google.gson.annotations.SerializedName;

public class Trailer {
    @SerializedName("key")
    private String mTrailerkey;

    public Trailer(String trailer) {
        mTrailerkey = trailer;
    }

    public String getTrailerKey() {
        return mTrailerkey;
    }
}

