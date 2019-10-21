package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.Movie;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.SortType;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.repository.MoviesRepository;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.ReturnCodes;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Result;

import java.util.ArrayList;
import java.util.List;

public class MoviesViewModel extends AndroidViewModel{

    MoviesRepository moviesRepository;

    public MoviesViewModel(@NonNull Application application) {
        super(application);
        moviesRepository = new MoviesRepository(application);
    }

    public LiveData<ArrayList<Result>> getLiveDataObject(){
        return moviesRepository.getLiveDataObject();
    }

    public LiveData<ReturnCodes> loadData(){
        return moviesRepository.loadData();
    }

    public void notifyPreferenceChanged(SortType loadType){
        moviesRepository.preferenceChanged(loadType);

    }

    //database stuff
    public LiveData<List<Movie>> getFavoriteMovies() {
        return moviesRepository.getFavoritesMovies();
    }

    public void setCurrentPreference(SortType loadType){
        moviesRepository.setCurrentPreference(loadType);
    }

    //expose the method to set data fetch status here as for database, activity can identify empty status through observers
    public void setStatusCode(ReturnCodes statusCode){
        moviesRepository.setStatusCode(statusCode);
    }
}
