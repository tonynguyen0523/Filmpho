package com.android.tonynguyen0523.filmpho;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Tony Nguyen on 1/10/2017.
 */

public class Utility {

    private static final String SORT_BY = "sort_by";
    /**
     * Get preferred sort by
     */
    public static String getPreferredSortBy(Context context) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        return prefs.getString(context.getString(R.string.pref_sort_key),
//                context.getString(R.string.popular_sort_value));
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.pref_sort_key), Context.MODE_PRIVATE);
        return sharedPreferences.getString(SORT_BY, "popular");

    }

    static void setSortBy(Context context, String sortBy){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.pref_sort_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sharedPreferences.edit();
        spe.putString(SORT_BY,sortBy);
        spe.apply();
    }

    /**
     * Check if there is internet
     */
    public static boolean hasInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    /**
     * Create valid image url to display poster thumbnail
     */
    public static String formatImageUrl(String imageUrl) {

        final String POSTERIMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";

        String posterUrl = POSTERIMAGE_BASE_URL + imageUrl;

        return posterUrl;

    }

    /**
     * Format date to display only year
     */
    public static String formatReleaseDateToYear(String releaseDate) {

        String year = releaseDate.substring(0, 4);

        return year;
    }

    /**
     * Create url by sort settings to retrieve and display movies.
     */
    public static String getUrl(String sortBy) {
        final String MOVIE_BASE_URL =
                "https://api.themoviedb.org/3/movie/";
        String FINAL_MOVIE_URL = MOVIE_BASE_URL + sortBy;
        final String APIKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(FINAL_MOVIE_URL).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, BuildConfig.OPEN_MOVIE_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String stringUrl = url.toString();

        return stringUrl;
    }

    /**
     * Create url by movie id to retrieve and display selected movie review.
     */
    static String getReviewUrlWithId(String movieId) {

        final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movieId + "/reviews?";
        final String APIKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, BuildConfig.OPEN_MOVIE_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String stringUrl = url.toString();

        Log.d("URL", stringUrl);

        return stringUrl;
    }

    /**
     * Create url by movie id to retrieve and display selected movie videos.
     */
    public static String getVideosUrlWithId(String movieId) {

        final String BASE_URL = "http://api.themoviedb.org/3/movie/" + movieId + "/videos?";
        final String APIKEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, BuildConfig.OPEN_MOVIE_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String stringUrl = url.toString();

        Log.d("URL", stringUrl);

        return stringUrl;
    }

    /**
     * Create youtube url for videos.
     */
    public static String getYouTubeUrl(String key) {

        final String BASE_URL = "https://www.youtube.com/watch?v=";
        String youtubeUrl = BASE_URL + key;

        return youtubeUrl;

    }

    /**
     * Get sort to readable text form
     */
    public static String sortToTextForm(String sortBy) {

        String sortText;

        if (sortBy.equals("popular")) {
            sortText = "Popular Movies";
        } else {
            sortText = "Top Rated Movies";
        }
        return sortText;
    }

}




