package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.R;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.utils.NetworkUtils;
import com.nanodegree.projects.popularmovies.popularmoviesstagetwo.service.Trailer;

import java.util.ArrayList;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder>
{
    ArrayList<Trailer> trailerArrayList;
    private Context context;
    private int resource;


    public TrailersAdapter(@NonNull Context ctx, int res, @NonNull ArrayList<Trailer> trailers)
    {
        context = ctx;
        resource = res;
        trailerArrayList = trailers;
    }


    @Override
    public TrailersAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int i)
    {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailersAdapter.TrailerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TrailersAdapter.TrailerViewHolder viewHolder, final int position)
    {
        Trailer trailer = trailerArrayList.get(position);
        viewHolder.name.setText(trailer.getName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent appIntent = new Intent(Intent.ACTION_VIEW, getAppUri(position) );
                Intent webIntent = new Intent(Intent.ACTION_VIEW, getWebUri(position));
                try
                {
                    context.startActivity(appIntent);
                }
                catch (ActivityNotFoundException ex)
                {
                    context.startActivity(webIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        int retVal = 0;
        if (trailerArrayList != null) {
            retVal = trailerArrayList.size();
        }
        return retVal;
    }

    public void setTrailer(ArrayList<Trailer> arraylist)
    {
        this.trailerArrayList = arraylist;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TrailerViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
        }
    }

    public void clearData(){
        if(trailerArrayList !=null)
            trailerArrayList.clear();
    }

    public Uri getWebUri(int position){
        Trailer trailer = trailerArrayList.get(position);
        return NetworkUtils.getWebUri(trailer);

    }

    public Uri getAppUri(int position){
        Trailer trailer = trailerArrayList.get(position);
        return NetworkUtils.getAppUri(trailer);
    }







//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
//    {
//        View listItem = convertView;
//
//        if(listItem==null)
//        {
//            listItem = LayoutInflater.from(mContext).inflate(mResource,parent,false);
//        }
//
//        TextView name = listItem.findViewById(R.id.name);
//        name.setText(trailerArrayList.get(position).getName());
//        return listItem;
//    }
//
//    @Override
//    public int getCount() {
//        return trailerArrayList.size();
//    }
//
//    public void setData(ArrayList<Trailer> data){
//        this.trailerArrayList = data;
//    }
//
//    public Uri getWebUri(int position){
//        //get the key first
//        Trailer trailer = trailerArrayList.get(position);
//
//        return Helper.getWebUri(trailer);
//
//    }

}
