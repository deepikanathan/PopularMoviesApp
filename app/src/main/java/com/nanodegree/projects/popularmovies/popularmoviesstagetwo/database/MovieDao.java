package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM favorite_movies")
    LiveData<List<Movie>>  getAll();

    @Query("SELECT COUNT(ID) FROM favorite_movies")
    int getCount();

    @Insert
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);
}
