package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.DetailsActivity;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.R;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.Movie;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.model.DetailsParcelable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>
{
    private ArrayList<Movie> movieArrayList;
    private Context context;

    public MoviesAdapter(Context ctx)
    {
        context = ctx;
    }

    @Override
    public MoviesAdapter.MoviesViewHolder onCreateViewHolder(ViewGroup parent, int i)
    {
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MoviesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MoviesViewHolder viewHolder, final int position)
    {
        Picasso.with(context)
                .load(movieArrayList.get(position).getImageURL())
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_error)
                .resize(185, 185)
                .into(viewHolder.imageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                intent.putExtra(view.getContext().getResources().getString(R.string.movie_intent_data), getMovieDetailsParcelableAt(position));
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        int retVal = 0;
        if (movieArrayList != null)
        {
            retVal = movieArrayList.size();
        }
        return retVal;
    }

    public void setMovies(ArrayList<Movie> arraylist)
    {
        this.movieArrayList = arraylist;
    }

    public void clearData()
    {
        if(movieArrayList !=null)
            movieArrayList.clear();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public MoviesViewHolder(ImageView itemView)
        {
            super(itemView);
            imageView = itemView;
        }
    }

    public DetailsParcelable getMovieDetailsParcelableAt(int position) {

        DetailsParcelable detailsParcelable = null;
        Movie m = movieArrayList.get(position);
        int id = m.getId();
        String title = m.getTitle();
        String imageURL = m.getImageURL();
        String plot = m.getPlot();
        String rating = String.valueOf(m.getRating());
        String releaseDate = m.getReleaseDate();

        detailsParcelable = new DetailsParcelable(id, title, releaseDate, rating, plot, imageURL);
        return detailsParcelable;
    }
}
