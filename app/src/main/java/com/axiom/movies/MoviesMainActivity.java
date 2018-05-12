package com.axiom.movies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.axiom.movies.data.Movie;
import com.axiom.movies.data.MovieJson;
import com.axiom.movies.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Displays grid of movies.
 */
public class MoviesMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private MoviesAdapter mMoviesAdapter;
    private static final int MOVIE_LOADER_ID = 1;
    private ArrayList<Movie> mCurrentList;
    private NetworkInfoReceiver mNetworkInfoReceiver;
    private int mCurrentGridType = 1;
    @BindView(R.id.progress_bar_view)
    ProgressBar mProgressBar;

    @BindView(R.id.movie_recycler_view)
    RecyclerView mMovieGridRecyclerView;
    @BindView(R.id.no_connection_layout)
    LinearLayout mNoConnectionLinearLayout;
    @BindView(R.id.no_movies_text_view)
    TextView emptyMoviesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_main_layout);
        ButterKnife.bind(this);

        //Restore the current list and title if available
        if (savedInstanceState != null) {
            mCurrentList = savedInstanceState.getParcelableArrayList("current_list");
            setTitle(savedInstanceState.getCharSequence("current_title"));
        }

        if (mCurrentList == null) {
            mCurrentList = new ArrayList<Movie>();
        }

        GridLayoutManager layoutManager = null;

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            int columnCount = getColumnCount(true);
            layoutManager = new GridLayoutManager(MoviesMainActivity.this, columnCount);
        } else if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            int columnCount = getColumnCount(false);
            layoutManager = new GridLayoutManager(MoviesMainActivity.this, columnCount);
        }
        mMovieGridRecyclerView.setLayoutManager(layoutManager);

        // Pass the movieList to the adapter and set adapter to recyclerview
        mMoviesAdapter = new MoviesAdapter(MoviesMainActivity.this, mCurrentList);
        mMovieGridRecyclerView.setAdapter(mMoviesAdapter);

        mCurrentGridType = Constants.GRID_TYPE_POPULAR;

        getPopularMovies();

        //Check for network connection and initiate the loader manager
        if (Constants.isNetworkConnected(this)) {
            if (mCurrentList.isEmpty()) {
                mCurrentGridType = Constants.GRID_TYPE_POPULAR;
                getPopularMovies();
            }
        } else {
            // If no network connection found,set an empty view
            mNoConnectionLinearLayout.setVisibility(View.VISIBLE);
        }

        mNetworkInfoReceiver = new NetworkInfoReceiver();
        IntentFilter networkFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkInfoReceiver, networkFilter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //save the current list and title
        savedInstanceState.putParcelableArrayList("current_list", mCurrentList);
        savedInstanceState.putCharSequence("current_title", getTitle());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkInfoReceiver);
        if (getLoaderManager().getLoader(MOVIE_LOADER_ID) != null) {
            getLoaderManager().destroyLoader(MOVIE_LOADER_ID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private int getColumnCount(boolean isPotrait) {
        int deviceType = getResources().getInteger(R.integer.deviceType);
        switch (deviceType) {
            //Checking for device type : phone,tablet
            case Constants.DEVICE_TYPE_PHONE:
                if (isPotrait) {
                    return 2; // spancount for potrait mode - phone
                } else {
                    return 3; // spancount for landscape mode - phone
                }
            case Constants.DEVICE_TYPE_TABLET:
                if (isPotrait) {
                    return 3;
                } else {
                    return 4;
                }
            case Constants.DEVICE_TYPE_LARGE_TABLET:
                if (isPotrait) {
                    return 4;
                } else {
                    return 5;
                }
            default:
                return 2; //settting phone spancount as default value
        }
    }

    private class NetworkInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.isNetworkConnected(context)) {
                if (mCurrentGridType == Constants.GRID_TYPE_POPULAR) {
                    getPopularMovies();
                } else if (mCurrentGridType == Constants.GRID_TYPE_TOP_RATED) {
                    getTopRatedMovies();
                }
            } else {
                mCurrentGridType = Constants.GRID_TYPE_FAVOURITES;
                LoaderManager loaderManager = getSupportLoaderManager();
                loaderManager.restartLoader(MOVIE_LOADER_ID, null, MoviesMainActivity.this);
            }
        }
    }

    private Callback<MovieJson> mMovieJsonCallback = new Callback<MovieJson>() {
        @Override
        public void onResponse(Call<MovieJson> call, Response<MovieJson> response) {
            List<Movie> moviesList = response.body().getResults();
            mProgressBar.setVisibility(View.GONE);
            // set received movie list to adapter
            mCurrentList = (ArrayList<Movie>) moviesList;

            if (moviesList != null && moviesList.isEmpty()) {
                mNoConnectionLinearLayout.setVisibility(View.INVISIBLE);
                emptyMoviesTextView.setVisibility(View.VISIBLE);
            }

            // Set the movies list data to adapter.
            if (moviesList != null) {
                if (!moviesList.isEmpty()) {
                    emptyMoviesTextView.setVisibility(View.INVISIBLE);
                    mNoConnectionLinearLayout.setVisibility(View.INVISIBLE);
                }
                mMoviesAdapter.setData(moviesList);
            }
        }

        @Override
        public void onFailure(Call<MovieJson> call, Throwable t) {

        }
    };

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new FavouriteMovieLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> moviesList) {
        // set received movie list to adapter
        mCurrentList = (ArrayList<Movie>) moviesList;
        mProgressBar.setVisibility(View.GONE);

        if (moviesList != null && moviesList.isEmpty()) {
            mNoConnectionLinearLayout.setVisibility(View.INVISIBLE);
            emptyMoviesTextView.setVisibility(View.VISIBLE);
        }

        // Set the movies list data to adapter.
        if (moviesList != null) {
            if (!moviesList.isEmpty()) {
                emptyMoviesTextView.setVisibility(View.INVISIBLE);
                mNoConnectionLinearLayout.setVisibility(View.INVISIBLE);
            }
            mMoviesAdapter.setData(moviesList);
        }
        getLoaderManager().destroyLoader(MOVIE_LOADER_ID);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    private void getPopularMovies() {
        MovieAPI service = RetrofitClient.getClient().create(MovieAPI.class);
        Call<MovieJson> jsonCall = service.getPopularMovies(Constants.API_KEY);
        jsonCall.enqueue(mMovieJsonCallback);
    }

    private void getTopRatedMovies() {
        MovieAPI service = RetrofitClient.getClient().create(MovieAPI.class);
        Call<MovieJson> jsonCall = service.getTopRatedMovies(Constants.API_KEY);
        jsonCall.enqueue(mMovieJsonCallback);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Implementation for user clicks on different menu items.
        int id = item.getItemId();
        switch (id) {
            //User clicks on top-rated menu option
            case R.id.action_popular:
                if (mCurrentGridType == Constants.GRID_TYPE_POPULAR) {
                    return true;
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    setTitle(getString(R.string.action_popular));
                    mCurrentGridType = Constants.GRID_TYPE_POPULAR;
                    if (Constants.isNetworkConnected(this)) {
                        getPopularMovies();
                        return true;
                    } else {
                        mNoConnectionLinearLayout.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
            case R.id.action_top_rated:
                if (mCurrentGridType == Constants.GRID_TYPE_TOP_RATED) {
                    return true;
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    setTitle(getString(R.string.action_top_rated));
                    mCurrentGridType = Constants.GRID_TYPE_TOP_RATED;
                    if (Constants.isNetworkConnected(this)) {
                        getTopRatedMovies();
                        return true;
                    } else {
                        mNoConnectionLinearLayout.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
            case R.id.action_favourites:
                if (mCurrentGridType == Constants.GRID_TYPE_FAVOURITES) {
                    return true;
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    setTitle(getString(R.string.action_favourites));
                    mCurrentGridType = Constants.GRID_TYPE_FAVOURITES;
                    if (Constants.isNetworkConnected(this)) {
                        LoaderManager loaderManager = getSupportLoaderManager();
                        if (loaderManager.getLoader(MOVIE_LOADER_ID) == null) {
                            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
                        } else {
                            loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
                        }

                        return true;
                    } else {
                        mNoConnectionLinearLayout.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
