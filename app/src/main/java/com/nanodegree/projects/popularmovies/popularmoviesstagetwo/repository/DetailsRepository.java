package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.ReturnCodes;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.AppDatabase;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.Movie;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.MovieDao;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.MoviesService;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.utils.RetrofitSingleton;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Review;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.ReviewsResponse;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Trailer;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsRepository {
    private static final String API_KEY = "f700b19d1af0889265b81cb218235275";

    private MovieDao movieDao;
    private AppDatabase appDatabase;
    private LiveData<List<Movie>> favoritesMovies;
    private MutableLiveData<ReturnCodes> reviews_code;
    private MutableLiveData<ArrayList<Review>> reviewsFromAPIResponse;
    private MutableLiveData<ReturnCodes> trailers_code;
    private MutableLiveData<ArrayList<Trailer>> trailersFromAPIResponse;
    private MoviesService moviesService;
    private int totalPages;
    private int page;

    public DetailsRepository(Application application){
        appDatabase = AppDatabase.getDatabase(application);
        movieDao = appDatabase.movieDao();
        favoritesMovies = movieDao.getAll();
        reviewsFromAPIResponse = new MutableLiveData<ArrayList<Review>>();
        reviewsFromAPIResponse.setValue(new ArrayList<Review>());
        trailersFromAPIResponse = new MutableLiveData<>();
        trailersFromAPIResponse.setValue(new ArrayList<Trailer>());
        trailers_code = new MutableLiveData<>();
        reviews_code = new MutableLiveData<>();
        this.page = 1;
        this.totalPages=1;
    }

    public LiveData<List<Movie>> getFavoritesMovies() {
        return favoritesMovies;
    }

    public void insertNewFavoriteMovie(Movie m){
        new InsertAsyncTask(movieDao).execute(m);
    }

    public void deleteFavoriteMovie(Movie m){
        new DeleteAsyncTask(movieDao).execute(m);
    }

    private static class InsertAsyncTask extends AsyncTask<Movie,Void,Void> {
        private MovieDao movieDao;

        public InsertAsyncTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            movieDao.insert(movies[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Movie,Void,Void> {
        private MovieDao movieDao;

        public DeleteAsyncTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            movieDao.delete(movies[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }


    public LiveData<ArrayList<Trailer>> getTrailers(){
        return trailersFromAPIResponse;
    }
    public LiveData<ReturnCodes> setTRailers(int id){
        if(trailers_code !=null && trailers_code.getValue()== ReturnCodes.EMPTY) {
            return trailers_code;
        }
        getTrailersFromNetwork(id);
        return trailers_code;
    }
    private void getTrailersFromNetwork(int id){

        moviesService = RetrofitSingleton.getService();
        Call<TrailerResponse> callBackend;
        callBackend = moviesService.getTrailers(id,API_KEY);

        callBackend.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if(response.isSuccessful() && response.body().getResults().size()>0){
                    ArrayList<Trailer> currentResult = trailersFromAPIResponse.getValue();
                    currentResult.addAll(response.body().getResults());
                    trailersFromAPIResponse.setValue(currentResult);
                    trailers_code.setValue(ReturnCodes.SUCCESS);
                }else if(response.body().getResults().size()==0){
                    trailers_code.setValue(ReturnCodes.EMPTY);
                }
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {
                trailers_code.setValue(ReturnCodes.NETWORK_FAILURE);
            }});
    }

    public LiveData<ArrayList<Review>> getReviews(){
        return reviewsFromAPIResponse;
    }
    public LiveData<ReturnCodes> loadReviews(int id){
        if(reviews_code !=null && reviews_code.getValue()== ReturnCodes.EMPTY) {
            return reviews_code;
        }
        if(page<=totalPages){
            loadReviewsFromNetwork(id);
        }else if(page > totalPages){
            reviews_code.setValue(ReturnCodes.END_OF_DATA);
        }

        return reviews_code;
    }
    private void loadReviewsFromNetwork(int id){

        moviesService = RetrofitSingleton.getService();
        Call<ReviewsResponse> callBackend;
        callBackend = moviesService.getReviews(id,API_KEY,page);

        callBackend.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if(response.isSuccessful() && response.body().getResults().size()>0){
                    totalPages = response.body().getTotalPages();

                    ArrayList<Review> currentResult = reviewsFromAPIResponse.getValue();
                    currentResult.addAll(response.body().getResults());
                    reviewsFromAPIResponse.setValue(currentResult);
                    reviews_code.setValue(ReturnCodes.SUCCESS);
                    page++;

                }else if(response.body().getResults().size()==0 && page==1){
                    reviews_code.setValue(ReturnCodes.EMPTY);
                }else if(response.body().getResults().size()==0 && page>1){
                    reviews_code.setValue(ReturnCodes.END_OF_DATA);
                }

            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                reviews_code.setValue(ReturnCodes.NETWORK_FAILURE);
            }});
    }


}
