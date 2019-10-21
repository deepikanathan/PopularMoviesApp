package com.nanodegree.projects.popularmovies.popularmoviesstagetwo;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.adapter.MoviesAdapter;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.Movie;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.utils.NetworkUtils;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.model.MoviesViewModel;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Result;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private RecyclerView movieRecyclerView;
    private MoviesAdapter moviesAdapter;
    private GridLayoutManager gridLayoutManager;
    private MoviesViewModel viewModel;
    private TextView errorTextView;
    private ProgressBar pb_indicator;
    private ObserveDataFromNetwork observeDataFromNetwork;
    private ObserveDataFromDatabse observeDataFromDatabse;
    String sort_order;
    private SortType sortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        observeDataFromNetwork = new ObserveDataFromNetwork();
        observeDataFromDatabse = new ObserveDataFromDatabse();

        errorTextView = (TextView) findViewById(R.id.connection_error_textView);
        pb_indicator = (ProgressBar) findViewById(R.id.pb_indicator);

        // *************** set up the Movie RecyclerView
        movieRecyclerView = (RecyclerView) findViewById(R.id.movie_recycle_view);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            gridLayoutManager = new GridLayoutManager(this, 2);
            movieRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 50));
        }
        else
        {
            gridLayoutManager = new GridLayoutManager(this, 3);
            movieRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 50));
        }
        movieRecyclerView.setLayoutManager(gridLayoutManager);
        movieRecyclerView.setHasFixedSize(true);
        moviesAdapter = new MoviesAdapter(this);
        movieRecyclerView.setAdapter(moviesAdapter);

        viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        //  *****************************

        //  register SharedPreferences Change Listener
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String currentValue = sharedPreferences.getString(getString(R.string.sort_order_key), getString(R.string.most_popular_value));

        sortType = NetworkUtils.convertValueToLoadType(getApplicationContext(), currentValue);
        viewModel.setCurrentPreference(sortType);

        pb_indicator.setVisibility(View.VISIBLE);
        viewModel.loadData().observe(this, new Observer<ReturnCodes>() {
            @Override
            public void onChanged(@Nullable ReturnCodes returnCodes) {
                showReturnMessage(returnCodes);
            }
        });
        registerObserverForFavorites();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void showReturnMessage(ReturnCodes returnCodes){
        switch(returnCodes){
            case EMPTY:
                if(sortType == SortType.FAVORITES) {

                    errorTextView.setText(R.string.database_empty_message);
                }else{
                    errorTextView.setText(R.string.network_empty_message);
                }
                errorTextView.setVisibility(View.VISIBLE);
                pb_indicator.setVisibility(View.GONE);
                movieRecyclerView.setVisibility(View.GONE);
                break;
            case NETWORK_FAILURE:
                errorTextView.setText(R.string.network_error);
                errorTextView.setVisibility(View.VISIBLE);
                pb_indicator.setVisibility(View.GONE);
                movieRecyclerView.setVisibility(View.GONE);
                break;
            case SUCCESS:
                pb_indicator.setVisibility(View.GONE);
                errorTextView.setVisibility(View.GONE);
                movieRecyclerView.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
    {
        if(s.equals(getString(R.string.sort_order_key)))
        {
            moviesAdapter.clearData();
            if(viewModel.getLiveDataObject().hasObservers())
            {
                viewModel.getLiveDataObject().removeObservers(this);
            }
            if(viewModel.getFavoriteMovies()!=null && viewModel.getFavoriteMovies().hasObservers())
            {
                viewModel.getFavoriteMovies().removeObservers(this);
            }

            sort_order = sharedPreferences.getString(getString(R.string.sort_order_key),getString(R.string.most_popular_value));
            sortType = NetworkUtils.convertValueToLoadType(getApplicationContext(), sort_order);

            errorTextView.setVisibility(View.GONE);
            pb_indicator.setVisibility(View.VISIBLE);
            movieRecyclerView.setVisibility(View.GONE);

            viewModel.notifyPreferenceChanged(sortType);

            registerObserverForFavorites();
        }
    }

    private void registerObserverForFavorites(){

        if(sortType == SortType.FAVORITES){
            viewModel.getFavoriteMovies().observe(this, observeDataFromDatabse);
        }
        else{
            viewModel.getLiveDataObject().observe(this, observeDataFromNetwork);
        }
    }

    private class ObserveDataFromNetwork implements Observer<ArrayList<Result>>{
        @Override
        public void onChanged(@Nullable ArrayList<Result> results) {
            if(results!=null && results.size()>0){
                moviesAdapter.setMovies(NetworkUtils.convertResultToMovie(results));
                moviesAdapter.notifyDataSetChanged();
            }
        }
    }

    private class ObserveDataFromDatabse implements Observer<List<Movie>>{
        @Override
        public void onChanged(@Nullable List<Movie> movies) {
            if(movies!=null && movies.size()>0){
                moviesAdapter.setMovies((ArrayList<Movie>) movies);
                moviesAdapter.notifyDataSetChanged();
            }else if(movies.size()==0){
               moviesAdapter.clearData();
               moviesAdapter.notifyDataSetChanged();
               viewModel.setStatusCode(ReturnCodes.EMPTY);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

