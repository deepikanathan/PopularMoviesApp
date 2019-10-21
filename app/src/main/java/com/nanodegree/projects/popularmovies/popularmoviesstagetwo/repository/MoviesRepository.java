package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.ReturnCodes;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.SortType;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.AppDatabase;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.Movie;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.MovieDao;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Movies;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.MoviesService;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Result;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.utils.RetrofitSingleton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesRepository {
    private static final String API_KEY = "f700b19d1af0889265b81cb218235275";

    private MutableLiveData<ArrayList<Result>> movieItemsFromResponse;
    private MoviesService moviesService;
    private int page=1;
    private int totalPages=1;
    private MutableLiveData<ReturnCodes> returnCodesMutableLiveData;
    private SortType sortType;
    private MovieDao movieDao;
    private AppDatabase appDatabase;
    private LiveData<List<Movie>> favoritesMovies;
    private Application application;

    public MoviesRepository(Application application){
        movieItemsFromResponse = new MutableLiveData<ArrayList<Result>>();
        movieItemsFromResponse.setValue(new ArrayList<Result>());
        returnCodesMutableLiveData = new MutableLiveData<>();
        this.page = 1;
        this.application = application;
    }

    private void getTitlesFromNetwork(SortType loadType, int page){
        moviesService = RetrofitSingleton.getService();
        Call<Movies> callBackend;
        if(loadType == SortType.TOP_RATED){
            callBackend = moviesService.getMoviesByTopRating(API_KEY,page);

        }else{
            callBackend = moviesService.getMoviesByPopularity(API_KEY,page);
        }
            callBackend.enqueue(new Callback<Movies>() {
                @Override
                public void onResponse(Call<Movies> call, Response<Movies> response) {
                    if(response.isSuccessful() && response.body().getResults().size()>0){
                        totalPages = response.body().getTotalPages();
                        ArrayList<Result> currentResult = movieItemsFromResponse.getValue();
                        currentResult.addAll(response.body().getResults());
                        movieItemsFromResponse.setValue(currentResult);
                        returnCodesMutableLiveData.setValue(ReturnCodes.SUCCESS);
                    }else if(response.isSuccessful() && response.body().getResults().size()==0){
                        returnCodesMutableLiveData.setValue(ReturnCodes.EMPTY);
                    }
                }

                @Override
                public void onFailure(Call<Movies> call, Throwable t) {
                    returnCodesMutableLiveData.setValue(ReturnCodes.NETWORK_FAILURE);
                }});
    }

    public LiveData<ArrayList<Result>> getLiveDataObject(){
        return movieItemsFromResponse;
    }

    public LiveData<ReturnCodes> loadData(){

        if (sortType == SortType.FAVORITES) {

            loadFavoritesFromDatabase();
        }else{
            if(page<=totalPages){
                getTitlesFromNetwork(sortType, page++);
            }else if(page > totalPages){
                returnCodesMutableLiveData.setValue(ReturnCodes.END_OF_DATA);
            }
        }

        return returnCodesMutableLiveData;
    }

    private void loadFavoritesFromDatabase() {
        if(appDatabase==null) {
            appDatabase = AppDatabase.getDatabase(application);
        }
        if (appDatabase!=null && movieDao == null) {
            movieDao = appDatabase.movieDao();
        }

        if(movieDao!=null){
            favoritesMovies = movieDao.getAll();
            DetermineCountAsyncTask d = new DetermineCountAsyncTask();
            d.execute();
        }
        if(appDatabase==null){
                returnCodesMutableLiveData.setValue(ReturnCodes.EMPTY);
        }

    }



    public void preferenceChanged(SortType loadType){

        if(loadType == null){
            return;
        }
        movieItemsFromResponse.getValue().clear();
        this.sortType = loadType;

        this.page = 1;
        this.totalPages = 1;
        loadData();
    }

    public LiveData<List<Movie>> getFavoritesMovies() {
        return favoritesMovies;
    }

    public void setCurrentPreference(SortType loadType){
        this.sortType = loadType;
    }


    private class DetermineCountAsyncTask extends AsyncTask<Void,Void,Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            return movieDao.getCount();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer.intValue()==0){
                returnCodesMutableLiveData.setValue(ReturnCodes.EMPTY);
            }else{
                returnCodesMutableLiveData.setValue(ReturnCodes.SUCCESS);
            }
        }
    }

    public void setStatusCode(ReturnCodes statusCode){
        this.returnCodesMutableLiveData.setValue(statusCode);
    }
}
