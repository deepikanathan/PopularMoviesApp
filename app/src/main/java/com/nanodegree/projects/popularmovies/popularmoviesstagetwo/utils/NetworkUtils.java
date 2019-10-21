package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.utils;

import android.content.Context;
import android.net.Uri;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.R;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.Movie;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.SortType;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Result;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Trailer;

import java.util.ArrayList;

public class NetworkUtils {

    private static final String URI_WEB = "http://www.youtube.com/watch?v=";
    private static final String URI_APP = "vnd.youtube:";

    public static SortType convertValueToLoadType(Context context, String value){
        SortType loadType=null;

        if(value.equalsIgnoreCase(context.getString(R.string.highest_rated_value))){
            loadType = SortType.TOP_RATED;
        }else if(value.equalsIgnoreCase(context.getString(R.string.most_popular_value))){
            loadType = SortType.MOST_POPULAR;

        }else if(value.equalsIgnoreCase(context.getString(R.string.favorites_value))){
            loadType = SortType.FAVORITES;
        }

        return loadType;
    }

    public static ArrayList<Movie> convertResultToMovie(ArrayList<Result> results){
        ArrayList<Movie> movies = new ArrayList<>();
        Movie m;

        for(Result r:results){
            m = new Movie();

            movies.add(convertResultToMovie(r));
        }

        return movies;
    }

    public static Movie convertResultToMovie(Result r){
        Movie m;

        m = new Movie();
        m.setId(r.getId());
        m.setImageURL(r.getAbsolutePosterPath());
        m.setPlot(r.getOverview());
        m.setRating(String.valueOf(r.getVoteAverage()));
        m.setReleaseDate(r.getReleaseDate());
        m.setTitle(r.getTitle());


        return m;
    }



    public static Uri getWebUri(Trailer trailer){
        String key = trailer.getKey();
        String site = trailer.getSite();
        if(!key.isEmpty() && !site.isEmpty() && site.equalsIgnoreCase("youtube")){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("www.youtube.com")
                    .appendPath("watch")
                    .appendQueryParameter("v",key);
            return builder.build();
        }else{
            return null;
        }

    }

    public static Uri getAppUri(Trailer trailer){
        String key = trailer.getKey();
        String site = trailer.getSite();
        if(!key.isEmpty() && !site.isEmpty() && site.equalsIgnoreCase("youtube")){
            return Uri.parse("vnd.youtube:" +key);
        }else{
            return null;
        }

    }
}
