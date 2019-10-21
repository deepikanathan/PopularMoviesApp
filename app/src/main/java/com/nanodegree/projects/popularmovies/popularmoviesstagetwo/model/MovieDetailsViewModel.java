package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.Movie;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.repository.DetailsRepository;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.ReturnCodes;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Review;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Trailer;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsViewModel extends AndroidViewModel {

    DetailsRepository detailsRepository;
    LiveData<List<Movie>> favoriteMovies;
    Application application;

    public MovieDetailsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        detailsRepository = new DetailsRepository(application);
    }

    public LiveData<List<Movie>> getFavoriteMovies() {

        if(detailsRepository ==null){
            detailsRepository = new DetailsRepository(application);

        }
        favoriteMovies = detailsRepository.getFavoritesMovies();
        return favoriteMovies;
    }

    public LiveData<ReturnCodes> loadReviews(int id){
         return detailsRepository.loadReviews(id);
    }

    public LiveData<ReturnCodes> loadTrailers(int id){
         return detailsRepository.setTRailers(id);
    }

    public LiveData<ArrayList<Review>> getReviews(){
        return detailsRepository.getReviews();
    }

    public LiveData<ArrayList<Trailer>> getTrailers(){ return detailsRepository.getTrailers(); }

    public void insertFavoriteMovie(Movie m){
        detailsRepository.insertNewFavoriteMovie(m);
    }

    public void deleteFavoriteMovie(Movie m)
    {
        detailsRepository.deleteFavoriteMovie(m);
    }


}
