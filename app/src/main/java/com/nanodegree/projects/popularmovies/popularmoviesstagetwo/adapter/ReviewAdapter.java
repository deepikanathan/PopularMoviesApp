package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.R;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>
{
    private ArrayList<Review> reviewArrayList;
    private Context context;
    int resource;


    public ReviewAdapter(Context ctx, int res, @NonNull ArrayList<Review> reviewList)
    {
        context = ctx;
        resource = res;
        reviewArrayList = reviewList;
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int i)
    {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder viewHolder, final int position)
    {
        Review review = reviewArrayList.get(position);
        viewHolder.userIdView.setText(review.getAuthor());
        viewHolder.reviewView.setText(review.getContent());
    }

    @Override
    public int getItemCount()
    {
        int retVal = 0;
        if (reviewArrayList != null) {
            retVal = reviewArrayList.size();
        }
        return retVal;
    }


    public void setReview(ArrayList<Review> arraylist)
    {
        this.reviewArrayList = arraylist;
        if (this.reviewArrayList.size() > 0)
        {

        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public TextView userIdView;
        public TextView reviewView;
        public ReviewViewHolder(View view) {
            super(view);
            userIdView = view.findViewById(R.id.userId);
            reviewView = view.findViewById(R.id.reviewText);
        }
    }

    public void clearData(){
        if(reviewArrayList !=null)
         reviewArrayList.clear();
    }

}
