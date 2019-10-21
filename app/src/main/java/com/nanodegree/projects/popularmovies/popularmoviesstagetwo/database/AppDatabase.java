package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.R;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase
{

    public abstract MovieDao movieDao();
    private static AppDatabase INSTANCE;


    public static AppDatabase getDatabase(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (AppDatabase.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, context.getResources().getString(R.string.movies_database))
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
