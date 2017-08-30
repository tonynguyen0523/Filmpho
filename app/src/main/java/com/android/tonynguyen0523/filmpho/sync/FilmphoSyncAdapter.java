package com.android.tonynguyen0523.filmpho.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.tonynguyen0523.filmpho.MySingleton;
import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.Utility;
import com.android.tonynguyen0523.filmpho.data.MovieContract;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FilmphoSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String LOG_TAG = FilmphoSyncAdapter.class.getSimpleName();

    private static final int SYNC_INTERVAL = 60*180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL;

    FilmphoSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        final String sortByQuery = Utility.getPreferredSortBy(getContext());
        String sortUrl = Utility.getUrl(sortByQuery);
        String nowPlayingUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=9ea41b1708f89fe7b448e6b08a4d5be0&language=en-US&page=1";

        // String volley
        StringRequest stringNowPlayingRequest = new StringRequest(Request.Method.GET, nowPlayingUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getNowPlayingMovieDataFromGSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });

        // String volley
        StringRequest stringRequest = new StringRequest(Request.Method.GET, sortUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getMovieDataFromGSON(response,sortByQuery);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

    });
        MySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(stringRequest);
        MySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(stringNowPlayingRequest);
    }

    private void getNowPlayingMovieDataFromGSON(String response)
            throws JSONException {

        Gson gson = new GsonBuilder().create();
        MovieDBResponse movieDBResponse = gson.fromJson(response,MovieDBResponse.class);
        List<Movies> nowPlayingMoviesList = new ArrayList<>();
        if(!nowPlayingMoviesList.isEmpty()){nowPlayingMoviesList.clear();}
        nowPlayingMoviesList = movieDBResponse.getList();


        Vector<ContentValues> cVVector = new Vector<>(nowPlayingMoviesList.size());

        Log.d(LOG_TAG,"Now playing " + Integer.toString(nowPlayingMoviesList.size()));

        for(int i = 0; i < nowPlayingMoviesList.size(); i++){

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.NowPlayingMovieEntry.COLUMN_MOVIEID, nowPlayingMoviesList.get(i).getMovieID());
            movieValues.put(MovieContract.NowPlayingMovieEntry.COLUMN_TITLE, nowPlayingMoviesList.get(i).getTitle());
            movieValues.put(MovieContract.NowPlayingMovieEntry.COLUMN_IMAGEURL, nowPlayingMoviesList.get(i).getPosterPath());
            movieValues.put(MovieContract.NowPlayingMovieEntry.COLUMN_PLOT, nowPlayingMoviesList.get(i).getOverview());
            movieValues.put(MovieContract.NowPlayingMovieEntry.COLUMN_RATING, nowPlayingMoviesList.get(i).getVoteAverage());
            movieValues.put(MovieContract.NowPlayingMovieEntry.COLUMN_RELEASEDATE, nowPlayingMoviesList.get(i).getReleaseDate());

            Log.d(LOG_TAG,nowPlayingMoviesList.get(i).getTitle());

            cVVector.add(movieValues);
        }

        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().delete(MovieContract.NowPlayingMovieEntry.CONTENT_URI,null,null);
            getContext().getContentResolver().bulkInsert(MovieContract.NowPlayingMovieEntry.CONTENT_URI, cvArray);
        }

    }

    private void getMovieDataFromGSON(String response, String sortBy)
            throws JSONException {

        long sortById = addSortBy(sortBy);

        Gson gson = new GsonBuilder().create();
        MovieDBResponse movieDBResponse = gson.fromJson(response,MovieDBResponse.class);
        List<Movies> moviesList = new ArrayList<>();
        if(!moviesList.isEmpty()){moviesList.clear();}
        moviesList = movieDBResponse.getList();


        Vector<ContentValues> cVVector = new Vector<>(moviesList.size());

        Log.d(LOG_TAG,Integer.toString(moviesList.size()));

        for(int i = 0; i < moviesList.size(); i++){

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_SORTBY_KEY, sortById);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, moviesList.get(i).getMovieID());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, moviesList.get(i).getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, moviesList.get(i).getPosterPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, moviesList.get(i).getOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, moviesList.get(i).getVoteAverage());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, moviesList.get(i).getReleaseDate());

            Log.d(LOG_TAG,moviesList.get(i).getTitle());

            cVVector.add(movieValues);

        }

        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,null,null);
            getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }

    }

    // First, check if the sort by exist in the database.
    private long addSortBy(String sortBy) {

        long sortId;

        Cursor sortByCursor = getContext().getContentResolver().query(
                MovieContract.MovieSortByEntry.CONTENT_URI,
                new String[]{MovieContract.MovieSortByEntry._ID},
                MovieContract.MovieSortByEntry.COLUMN_MOVIE_SORT_BY + " = ?",
                new String[]{sortBy},
                null
        );

        if (sortByCursor.moveToFirst()) {
            int sortByIdIndex = sortByCursor.getColumnIndex(MovieContract.MovieSortByEntry._ID);
            sortId = sortByCursor.getLong(sortByIdIndex);
        } else {

            ContentValues sortByValues = new ContentValues();

            sortByValues.put(MovieContract.MovieSortByEntry.COLUMN_MOVIE_SORT_BY, sortBy);

            Uri insertedUri = getContext().getContentResolver().insert(
                    MovieContract.MovieSortByEntry.CONTENT_URI,
                    sortByValues
            );

            sortId = ContentUris.parseId(insertedUri);
        }

        sortByCursor.close();

        return sortId;
    }


    /**
     * Helper method to schedule the sync adapter periodic execution.
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime){
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){

            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account,authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }



    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount,context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context){

        // Since we've created an account.
        FilmphoSyncAdapter.configurePeriodicSync(context,SYNC_INTERVAL,SYNC_FLEXTIME);

        // Without called setSyncAutomatically, periodic sync will not be enabled.
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        // Do a sync.
        syncImmediately(context);
    }
    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }
}
