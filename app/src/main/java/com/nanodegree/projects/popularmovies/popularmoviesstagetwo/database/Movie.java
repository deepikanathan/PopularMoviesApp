package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

@Entity(tableName = "favorite_movies")

public class Movie {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "image_url")
    private String imageURL;

    @ColumnInfo(name = "plot")
    private String plot;

    @ColumnInfo(name = "rating")
    private String rating;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    public Movie() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object obj) {
        Movie m = (Movie) obj;
        if(id == m.getId()){
            return true;
        }else{
            return false;
        }
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .into(view);
    }
}

