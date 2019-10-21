package com.nanodegree.projects.popularmovies.popularmoviesstagetwo;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.adapter.ReviewAdapter;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.adapter.TrailersAdapter;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.database.Movie;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.databinding.MovieDetailsBinding;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.utils.NetworkUtils;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.model.DetailsParcelable;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.model.MovieDetailsViewModel;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Review;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Trailer;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private DetailsParcelable detailsParcelable;

    Button button;
    MovieDetailsViewModel movieDetailsViewModel;
    Movie movieEntity;

    List<Movie> latestFavoriteMovies;
    ImageView shareButton;
    String firstTrailer;


    private ReviewAdapter reviewsAdapter;
    private RecyclerView reviewRecyclerView;
    private LinearLayoutManager reviewLinearLayoutManager;
    ArrayList<Review> reviewsList;

    private TrailersAdapter trailersAdapter;
    private RecyclerView trailerRecyclerView;
    private LinearLayoutManager trailerLinearLayoutManager;
    ArrayList<Trailer> trailersList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MovieDetailsBinding detailsBinding =  DataBindingUtil.setContentView(this, R.layout.movie_details);
        final View view = detailsBinding.getRoot();
        button = view.findViewById(R.id.favoriteButton);
        shareButton = view.findViewById(R.id.shareButton);

        movieDetailsViewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);

        Intent intent = getIntent();
        DetailsParcelable detailsParcelable = intent.getParcelableExtra(getResources().getString(R.string.movie_intent_data));

        movieEntity = new Movie();
        movieEntity.setId(detailsParcelable.getId());
        movieEntity.setTitle(detailsParcelable.getTitle());
        movieEntity.setImageURL(detailsParcelable.getImageURL());
        movieEntity.setPlot(detailsParcelable.getPlot());
        movieEntity.setRating(detailsParcelable.getRating());
        movieEntity.setReleaseDate(detailsParcelable.getReleaseDate());

        detailsBinding.setMovie(movieEntity);

        movieDetailsViewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                updateFavoriteStatus(movies,movieEntity);
                latestFavoriteMovies = movies;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFavorite(latestFavoriteMovies,movieEntity))
                {
                    movieDetailsViewModel.deleteFavoriteMovie(movieEntity);
                    Toast.makeText(getApplicationContext(), "Movie is deleted from favorites", Toast.LENGTH_LONG).show();
                }else{
                    movieDetailsViewModel.insertFavoriteMovie(movieEntity);
                    Toast.makeText(getApplicationContext(), "Movie is added to favorites", Toast.LENGTH_LONG).show();
                }

            }
        });

        movieDetailsViewModel.getTrailers().observe(this, new Observer<ArrayList<Trailer>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Trailer> trailers) {
                if(trailers!=null && trailers.size()>0){
                    firstTrailer = NetworkUtils.getWebUri(trailers.get(0)).toString();
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(firstTrailer.isEmpty())
                    return;
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject)+movieEntity.getTitle());
                share.putExtra(Intent.EXTRA_TEXT, firstTrailer);

                startActivity(Intent.createChooser(share, getString(R.string.share_title)));
            }
        });


        trailerRecyclerView = (RecyclerView) findViewById(R.id.trailerRecyclerView);
        trailerLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        trailerRecyclerView.setLayoutManager(trailerLinearLayoutManager);
        trailerRecyclerView.setHasFixedSize(true);

        trailersList = new ArrayList<>();
        trailersAdapter = new TrailersAdapter(this, R.layout.trailer_item, trailersList);
        trailerRecyclerView.setAdapter(trailersAdapter);

        movieDetailsViewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);
        movieDetailsViewModel.loadTrailers(detailsParcelable.getId()).observe(this, new Observer<ReturnCodes>() {
            @Override
            public void onChanged(@Nullable ReturnCodes returnCodes) {
                //showStatusMessage(statusCodes);
            }
        });
        movieDetailsViewModel.getTrailers().observe(this, new Observer<ArrayList<Trailer>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Trailer> trailers) {
                    trailersList.addAll(trailers);
                    trailersAdapter.setTrailer(trailers);
                    trailersAdapter.notifyDataSetChanged();
            }
        });


        reviewRecyclerView = (RecyclerView) findViewById(R.id.reviewRecyclerView);
        reviewLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewRecyclerView.setLayoutManager(reviewLinearLayoutManager);
        reviewRecyclerView.setHasFixedSize(true);

        reviewsList = new ArrayList<>();
        reviewsAdapter = new ReviewAdapter(this, R.layout.review_item, reviewsList);
        reviewRecyclerView.setAdapter(reviewsAdapter);

        movieDetailsViewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);
        movieDetailsViewModel.loadReviews(detailsParcelable.getId()).observe(this, new Observer<ReturnCodes>() {
            @Override
            public void onChanged(@Nullable ReturnCodes returnCodes) {
                //showStatusMessage(statusCodes);
            }
        });
        movieDetailsViewModel.getReviews().observe(this, new Observer<ArrayList<Review>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Review> reviews) {
                if (reviews.size() > 0)
                {
                    view.findViewById(R.id.reviewLabel).setVisibility(View.VISIBLE);
                }
                reviewsList.addAll(reviews);
                reviewsAdapter.setReview(reviews);
                reviewsAdapter.notifyDataSetChanged();
            }
        });



    }

    private boolean isFavorite(List<Movie> movies, Movie m){
        boolean flag=false;

        if(movies.contains(m)){
            flag=true;
        }else{
            flag=false;
        }
        return flag;
    }

    private void updateFavoriteStatus(List<Movie> movies, Movie m){
        if(isFavorite(movies, m)){
            button.setText(getResources().getString(R.string.remove_from_favorites));
        }else{
            button.setText(getResources().getString(R.string.add_to_favorites));
        }

    }


}
