package com.android.tonynguyen0523.filmpho.sync;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tonyn on 7/17/2017.
 */

class Movies {

    @SerializedName("original_title")
    String title;

    @SerializedName("poster_path")
    String posterPath;

    @SerializedName("vote_average")
    String voteAverage;

    @SerializedName("release_date")
    String releaseDate;

    @SerializedName("id")
    String movieID;

    @SerializedName("backdrop_path")
    String backDrop;

    String overview;

    public String getTitle(){
        return title;
    }

    public String getPosterPath(){
        return posterPath;
    }

    public String getVoteAverage(){
        return voteAverage;
    }

    public String getReleaseDate(){
        return releaseDate;
    }

    public String getMovieID(){
        return movieID;
    }

    public String getOverview(){
        return overview;
    }

    public String getBackDrop() { return backDrop; }
}
