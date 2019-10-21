package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.utils;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.MoviesService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    private static Retrofit retrofit=null;
    private static String BASE_URL = "http://api.themoviedb.org/";

    public static MoviesService getService(){

        if(retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(MoviesService.class);
    }
}

