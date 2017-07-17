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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class FilmphoSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = FilmphoSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60*180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public FilmphoSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        final String sortByQuery = Utility.getPreferredSortBy(getContext());
        String url = Utility.getUrl(sortByQuery);

        // Use the created url for JsonObjectRequest.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // Json parse response object.
                try {
                    getMovieDataFromJson(response,sortByQuery);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        MySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            // This will only happen if there was an error getting or parsing the forecast.
            return;
        }

    private void getMovieDataFromJson(JSONObject response, String sortBy)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULT = "results";
        final String MDB_TITLE = "original_title";
        final String MDB_POSTER = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RATING = "vote_average";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_MOVIE_ID = "id";

        try {

            long sortById = addSortBy(sortBy);

            JSONArray movieArray = response.getJSONArray(MDB_RESULT);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {

                String title;
                String posterUrl;
                String plot;
                String rating;
                String releaseDate;
                String movieId;

                JSONObject movieResults = movieArray.getJSONObject(i);

                title = movieResults.getString(MDB_TITLE);
                posterUrl = movieResults.getString(MDB_POSTER);
                plot = movieResults.getString(MDB_OVERVIEW);
                rating = movieResults.getString(MDB_RATING);
                releaseDate = movieResults.getString(MDB_RELEASE_DATE);
                movieId = movieResults.getString(MDB_MOVIE_ID);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_SORTBY_KEY, sortById);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, posterUrl);
                movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, plot);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

                cVVector.add(movieValues);
            }

            int inserted = 0;

            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,null,null);
                getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    // First, check if the sort by exist in the database.
    long addSortBy(String sortBy) {

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
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime){
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
    public static Account getSyncAccount(Context context) {
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
