package com.axiom.movies.data;


import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("author")
    private String mAuthor;
    @SerializedName("content")
    private String mContent;

    public Review(String author,String content)
    {
        mAuthor = author;
        mContent = content;
    }

    public String getAuthor(){
        return mAuthor;
    }
    public String getContent(){
        return mContent;
    }
}
