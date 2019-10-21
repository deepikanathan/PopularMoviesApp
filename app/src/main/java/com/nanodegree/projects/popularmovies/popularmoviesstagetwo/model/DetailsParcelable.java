package com.nanodegree.projects.popularmovies.popularmoviesstagetwo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailsParcelable implements Parcelable {

    private int id;
    private String title;
    String releaseDate;
    String rating;
    String plot;
    String imageURL;

    public DetailsParcelable(int id, String title, String releaseDate, String rating, String plot, String imageURL){
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.plot = plot;
        this.imageURL = imageURL;
    }


    public DetailsParcelable(Parcel in) {
       id =  in.readInt();
       title = in.readString();
       releaseDate= in.readString();
       rating = in.readString();
       plot=in.readString();
       imageURL=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(rating);
        dest.writeString(plot);
        dest.writeString(imageURL);

    }

    public static final Parcelable.Creator<DetailsParcelable> CREATOR
            = new Parcelable.Creator<DetailsParcelable>(){

        @Override
        public DetailsParcelable createFromParcel(Parcel source) {
            return new DetailsParcelable(source);
        }

        @Override
        public DetailsParcelable[] newArray(int size) {
            return new DetailsParcelable[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public String getPlot() {
        return plot;
    }

    public String getImageURL() {
        return imageURL;
    }
}
