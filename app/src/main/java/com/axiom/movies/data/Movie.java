package com.axiom.movies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    @SerializedName("id")
    private String mMovieId;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("overview")
    private String mOverview;
    @SerializedName("original_title")
    private String mMovieTitle;
    @SerializedName("vote_average")
    private String mRating;
    @SerializedName("release_date")
    private String mReleaseDate;
    private String mTrailerkey1;
    private String mTrailerkey2;

    public Movie(String movieId,
                 String imagePath,
                 String overview,
                 String movieTitle,
                 String rating,
                 String releaseDate) {
        mPosterPath = imagePath;
        mOverview = overview;
        mMovieTitle = movieTitle;
        mRating = rating;
        mReleaseDate = releaseDate;
        mMovieId = movieId;
        mTrailerkey1 = null;
        mTrailerkey2 = null;
    }

    private Movie(Parcel in) {
        mMovieId = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mMovieTitle = in.readString();
        mRating = in.readString();
        mReleaseDate = in.readString();
        mTrailerkey1 = in.readString();
        mTrailerkey2 = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public String getImagePath() {
        return mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getRating() {
        return mRating;
    }

    public String getYear() {
        return mReleaseDate;
    }

    public String getTrailerKey1() {
        return mTrailerkey1;
    }

    public void setTrailerKey1(String trailerkey1) {
        mTrailerkey1 = trailerkey1;
    }

    public String getTrailerKey2() {
        return mTrailerkey2;
    }

    public void setTrailerKey2(String trailerkey2) {
        mTrailerkey2 = trailerkey2;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mMovieId);
        parcel.writeString(mPosterPath);
        parcel.writeString(mOverview);
        parcel.writeString(mMovieTitle);
        parcel.writeString(mRating);
        parcel.writeString(mReleaseDate);
        parcel.writeString(mTrailerkey1);
        parcel.writeString(mTrailerkey2);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
