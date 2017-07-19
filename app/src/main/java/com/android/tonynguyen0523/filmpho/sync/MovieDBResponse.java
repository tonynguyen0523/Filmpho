package com.android.tonynguyen0523.filmpho.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyn on 7/17/2017.
 */

public class MovieDBResponse {

    List<Movies> results;

    public MovieDBResponse(){
        results = new ArrayList<>();
    }

    public int getMovieListSize(){
         return results.size();
    }

    public List<Movies> getList(){
        return results;
    }
}
