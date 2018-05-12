package com.axiom.movies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.axiom.movies.data.Movie;
import com.axiom.movies.data.MovieContract.MovieEntry;
import com.axiom.movies.data.MovieDbHelper;
import com.axiom.movies.data.Review;
import com.axiom.movies.data.ReviewJson;
import com.axiom.movies.data.Trailer;
import com.axiom.movies.data.TrailerJson;
import com.axiom.movies.utilities.Constants;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieDetailActivity extends AppCompatActivity {

    String favmovieId;
    List<String> mTrailers;
    TextView mNoReviewTextView;
    TextView[] mAuthorTextViewList;
    TextView[] mReviewTextViewList;
    LinearLayout.LayoutParams mAuthorLP;
    ImageView mTrailer1ImageView;
    ImageView mTrailer2ImageView;
    Movie mCurrentMovie;
    MovieDbHelper mDbHelper;
    ImageView favouriteView;
    ShareActionProvider mShareActionProvider;

    @BindView(R.id.movie_image_view)
    ImageView movieImageView;
    @BindView(R.id.plot_text_view)
    TextView movieOverviewTextView;
    @BindView(R.id.movie_title_text_view)
    TextView movieTitleTextView;
    @BindView(R.id.rating_value_text_view)
    TextView movieRatingTextView;
    @BindView(R.id.year_text_view)
    TextView movieReleaseTextView;
    @BindView(R.id.review_layout)
    LinearLayout mReviewLinearLayout;
    @BindView(R.id.trailer1_layout)
    LinearLayout mTrailer1Layout;
    @BindView(R.id.trailer2_layout)
    LinearLayout mTrailer2Layout;
    @BindViews({R.id.divider_line, R.id.divider_line2, R.id.divider_line3})
    List<View> views;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_layout);
        ButterKnife.bind(this);

        setTitle(getString(R.string.movie_detail_title));

        mCurrentMovie = getIntent().getParcelableExtra("movie_obj");

        //Load the backdrop image of current movie with Glide
        String imagePath = Constants.POSTER_BASE_URL + mCurrentMovie.getImagePath();
        Glide.with(this).load(imagePath).into(movieImageView);

        //Set data of current movie to different view
        movieOverviewTextView.setText(mCurrentMovie.getOverview());
        movieTitleTextView.setText(mCurrentMovie.getMovieTitle());
        String ratingText = String.format(getResources().getString(R.string.rating_text), mCurrentMovie.getRating());
        movieRatingTextView.setText(ratingText);
        String month = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM, yyyy");
        try {
            month = sdf.format(new SimpleDateFormat("yyyy-M-dd").parse(mCurrentMovie.getYear()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        movieReleaseTextView.setText(month);

        final String movieId = mCurrentMovie.getMovieId();

        MovieAPI reviewService = RetrofitClient.getClient().create(MovieAPI.class);
        Call<ReviewJson> jsonCall = reviewService.getReviews(movieId, Constants.API_KEY);
        jsonCall.enqueue(mReviewJsonCallback);

        MovieAPI trailerService = RetrofitClient.getClient().create(MovieAPI.class);
        Call<TrailerJson> trailerJsonCall = trailerService.getTrailers(movieId, Constants.API_KEY);
        trailerJsonCall.enqueue(mTrailerJsonCallback);

        //If in favourite movies gridtype,check if we already have the trailerkey values
        String trailer1key = String.valueOf(mCurrentMovie.getTrailerKey1());
        String trailer2key = String.valueOf(mCurrentMovie.getTrailerKey2());

        mTrailers = null;
        if (trailer1key != null) {
            mTrailers = new ArrayList<String>();
            mTrailers.add(trailer1key);
        }
        if (trailer2key != null) {
            if (mTrailers == null) {
                mTrailers = new ArrayList<String>();
            }
            mTrailers.add(trailer2key);
        }

        //Onclick trailer imageview sends an intent to open given video in youtube
        mTrailer1ImageView = (ImageView) findViewById(R.id.trailer_video1_image_view);
        mTrailer1ImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String youtubeURL = Constants.YOUTUBE_VIDEO_QUERY + mTrailers.get(0);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL)));
            }
        });

        mTrailer2ImageView = (ImageView) findViewById(R.id.trailer_video2_image_view);
        mTrailer2ImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String youtubeURL = Constants.YOUTUBE_VIDEO_QUERY + mTrailers.get(1);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL)));
            }
        });

        // Create an instance of DbHelper to open readable database
        mDbHelper = new MovieDbHelper(this);
        String[] projection = {MovieEntry.COLUMN_MOVIE_ID};
        String selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {movieId};

        //Query the database of favourite movies to get a cursor object
        Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI, projection, selection, selectionArgs, null);
        favouriteView = (ImageView) findViewById(R.id.favourite_image_view);
        while (cursor.moveToNext()) {
            //Retrieve favourite movieIds from the cursor object
            favmovieId = cursor.getString(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_ID));

            //Check if any of favourite movieIds retrieved equals current movieId
            if (favmovieId.equals(movieId)) {
                favouriteView.setImageResource(R.mipmap.favourite_icon_selected);
                favouriteView.setTag("image2");
            }
        }
        cursor.close();

        // favouriteView on click checks if the button is already clicked
        favouriteView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ((favouriteView.getTag() != null && favouriteView.getTag().toString().equals("image2"))) {
                    favouriteView.setImageResource(R.mipmap.favourite_icon);
                    favouriteView.setTag("image1");
                    //Delete the movie from the database
                    int movieDeleted = getContentResolver().delete(MovieEntry.CONTENT_URI, MovieEntry.COLUMN_MOVIE_ID + "=" + mCurrentMovie.getMovieId(), null);
                    if (movieDeleted > 0) {
                        Toast.makeText(MovieDetailActivity.this, getString(R.string.removed_from_fav_toast), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //If favourite view is not already selected,add the movie to favourites list
                    favouriteView.setImageResource(R.mipmap.favourite_icon_selected);
                    favouriteView.setTag("image2");
                    addMovie();

                    favouriteView.setActivated(true);
                }
            }
        });
    }

    //Method to add movies to database
    private void addMovie() {
        String movieTitle = mCurrentMovie.getMovieTitle();
        String movieRating = mCurrentMovie.getRating();
        String moviePosterPath = mCurrentMovie.getImagePath();
        String movieReleaseDate = mCurrentMovie.getYear();
        String movieId = mCurrentMovie.getMovieId();
        String movieOverview = mCurrentMovie.getOverview();
        String trailerkey1 = null;
        String trailerkey2 = null;
        //Get the trailer keys from trailerlist
        if (mTrailers != null && !mTrailers.isEmpty()) {
            trailerkey1 = mTrailers.get(0);
            if (mTrailers.size() > 1) {
                trailerkey2 = mTrailers.get(1);
            }
        }

        //Put the values retrieved above into content values
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
        values.put(MovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
        values.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, movieOverview);
        values.put(MovieEntry.COLUMN_POSTER_PATH, moviePosterPath);
        values.put(MovieEntry.COLUMN_MOVIE_RATING, movieRating);
        values.put(MovieEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
        values.put(MovieEntry.COLUMN_TRAILER1_KEY, trailerkey1);
        values.put(MovieEntry.COLUMN_TRAILER2_KEY, trailerkey2);

        //Add movie values to database
        Uri newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        if (newUri != null) {
            Toast.makeText(this, getString(R.string.added_to_fav_toast_text), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Implementation for user clicks on different menu items.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_item_share:
                //share intent to share trailer
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Constants.YOUTUBE_VIDEO_QUERY + mTrailers.get(0));
                startActivity(Intent.createChooser(intent, "Share via"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private Callback<ReviewJson> mReviewJsonCallback = new Callback<ReviewJson>() {
        @Override
        public void onResponse(Call<ReviewJson> call, Response<ReviewJson> response) {
            List<Review> reviewList = response.body().getReviewResults();
            setReviewsToViews(reviewList);
        }

        @Override
        public void onFailure(Call<ReviewJson> call, Throwable t) {

        }
    };

    private void setReviewsToViews(List<Review> mReviews) {
        if (mReviews.size() == 0) {
            String emptyView = getResources().getString(R.string.empty_reviews_text);
            mNoReviewTextView = new TextView(MovieDetailActivity.this);
            mNoReviewTextView.setTextSize(getResources().getDimension(R.dimen.textSize));
            mNoReviewTextView.setText(emptyView);
            mReviewLinearLayout.addView(mNoReviewTextView);

        } else {
            //Create lists for review and author textviews
            int reviewCount = mReviews.size();

            mReviewTextViewList = new TextView[reviewCount];
            mAuthorTextViewList = new TextView[reviewCount];

            //layout params for textview
            mAuthorLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mAuthorLP.setMargins(5, 5, 5, 5);

            //Set review and author data to textviews,also set layout params and other attributes to the textviews
            //Add all these views to the linear layout
            for (int i = 0; i < reviewCount; i++) {
                Review currentReview = mReviews.get(i);
                mAuthorTextViewList[i] = new TextView(MovieDetailActivity.this);
                mReviewTextViewList[i] = new TextView(MovieDetailActivity.this);
                mReviewTextViewList[i].setLayoutParams(mAuthorLP);
                mAuthorTextViewList[i].setLayoutParams(mAuthorLP);
                mAuthorTextViewList[i].setGravity(Gravity.START);
                mAuthorTextViewList[i].setTypeface(null, Typeface.BOLD_ITALIC);
                mAuthorTextViewList[i].setTextSize(getResources().getDimension(R.dimen.textSize));
                mAuthorTextViewList[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.author_text));
                mAuthorTextViewList[i].setText(currentReview.getAuthor() + " " + getString(R.string.author_text));
                mReviewTextViewList[i].setText(currentReview.getContent().replaceAll("(?m)^[ \t]*\r?\n", ""));
                mReviewTextViewList[i].setTextSize(getResources().getDimension(R.dimen.textSize));
                mReviewLinearLayout.addView(mAuthorTextViewList[i]);
                mReviewLinearLayout.addView(mReviewTextViewList[i]);

                //Create separator line views between the review textviews
                View separatorline = new View(MovieDetailActivity.this);
                separatorline.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5));
                separatorline.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.divider_line));
                mReviewLinearLayout.addView(separatorline);
            }
        }
    }

    private Callback<TrailerJson> mTrailerJsonCallback = new Callback<TrailerJson>() {
        @Override
        public void onResponse(Call<TrailerJson> call, Response<TrailerJson> response) {
            List<Trailer> trailerResults = response.body().getTrailerResults();
            mTrailers.add(String.valueOf(trailerResults.get(0)));
            mTrailers.add(String.valueOf(trailerResults.get(1)));
            setTrailersToViews(mTrailers);
        }

        @Override
        public void onFailure(Call<TrailerJson> call, Throwable t) {

        }
    };

    private void setTrailersToViews(List<String> trailerKeys) {
        if (mTrailers.size() == 0) {
            //Set the trailerlayouts to GONE if trailerlist is empty
            mTrailer1Layout.setVisibility(View.GONE);
            mTrailer2Layout.setVisibility(View.GONE);
            //Also remove the separator lines
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
        } else if (mTrailers.size() == 1) {
            //If only one trailer key is present,remove the second trailer layout
            mTrailer2Layout.setVisibility(View.GONE);
            views.get(2).setVisibility(View.GONE);
        } else if (mTrailers.size() == 2) {
            mTrailer1Layout.setVisibility(View.VISIBLE);
            mTrailer2Layout.setVisibility(View.VISIBLE);
            for (View view : views) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

}