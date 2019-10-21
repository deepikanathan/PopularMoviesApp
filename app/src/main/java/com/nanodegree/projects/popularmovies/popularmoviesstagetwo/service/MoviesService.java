package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesService {

    @GET("/3/movie/popular")
    Call<Movies> getMoviesByPopularity(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("/3/movie/top_rated")
    Call<Movies> getMoviesByTopRating(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("/3/movie/{id}/reviews")
    Call<ReviewsResponse> getReviews(@Path("id") int id, @Query("api_key") String apiKey, @Query("page") int page);

    @GET("/3/movie/{id}/videos")
    Call<TrailerResponse> getTrailers(@Path("id") int id, @Query("api_key") String apiKey);

}
