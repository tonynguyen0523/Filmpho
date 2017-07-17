package com.android.tonynguyen0523.filmpho.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.android.tonynguyen0523.filmpho.data.MovieContract.MovieEntry;
import com.android.tonynguyen0523.filmpho.data.MovieContract.MovieSortByEntry;

/**
 * Created by Tony Nguyen on 1/9/2017.
 */

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
    Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.
    Students: Uncomment the tests in this class as you implement the functionality in your
    ContentProvider to make sure that you've implemented things reasonably correctly.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.MovieSortByEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.MovieSortByEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from sortBy table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieSortByEntry.TABLE_NAME, null, null);
        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // movieProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: movieProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: movieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {

        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        String testsortBy = "94074";
        type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieSortBy(testsortBy));
        assertEquals("Error: the MovieEntry CONTENT_URI with sortBy should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        String testMovieId = "12345";
        type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieSortByWithMovieId(testsortBy,testMovieId));
        assertEquals("Error: the MovieEntry CONTENT_URI with sortBy and date should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(MovieSortByEntry.CONTENT_URI);
        assertEquals("Error: the MovieSortByEntry CONTENT_URI should return MovieSortByEntry.CONTENT_TYPE",
                MovieSortByEntry.CONTENT_TYPE, type);
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic movie query functionality
        given in the ContentProvider is working correctly.
     */

    public void testBasicMovieQuery(){
        MovieDbHelper dbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createPopularMovieSortValues();
        long sortByRowId = TestUtilities.insertPopularMovieSortByValues(mContext);

        ContentValues movieValues = TestUtilities.createMovieValues(sortByRowId);

        long movieRowId = db.insert(MovieEntry.TABLE_NAME,null,movieValues);
        assertTrue("Unable to Insert MovieEntry into the Database", sortByRowId != -1);

        db.close();

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, movieValues);
    }

    public void testBasicSortByQueries() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createPopularMovieSortValues();
        long sortByRowId = TestUtilities.insertPopularMovieSortByValues(mContext);

        // Test the basic content provider query
        Cursor sortByCursor = mContext.getContentResolver().query(
                MovieSortByEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicSortByQueries, sortBy query", sortByCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: sortBy Query did not properly set NotificationUri",
                    sortByCursor.getNotificationUri(), MovieSortByEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update sortBy is functioning correctly.
     */
    public void testUpdateSortBy() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createPopularMovieSortValues();

        Uri sortByUri = mContext.getContentResolver().
                insert(MovieSortByEntry.CONTENT_URI, values);
        long sortByRowId = ContentUris.parseId(sortByUri);

        // Verify we got a row back.
        assertTrue(sortByRowId != -1);
        Log.d(LOG_TAG, "New row id: " + sortByRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieSortByEntry._ID, sortByRowId);
        updatedValues.put(MovieSortByEntry.COLUMN_MOVIE_SORT_BY, "top_rated");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor sortByCursor = mContext.getContentResolver().query(MovieSortByEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        sortByCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieSortByEntry.CONTENT_URI, updatedValues, MovieSortByEntry._ID + "= ?",
                new String[] { Long.toString(sortByRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        sortByCursor.unregisterContentObserver(tco);
        sortByCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieSortByEntry.CONTENT_URI,
                null,   // projection
                MovieSortByEntry._ID + " = " + sortByRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdatesortBy.  Error validating sortBy entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createPopularMovieSortValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieSortByEntry.CONTENT_URI, true, tco);
        Uri sortByUri = mContext.getContentResolver().insert(MovieSortByEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert sortBy
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long sortByRowId = ContentUris.parseId(sortByUri);

        // Verify we got a row back.
        assertTrue(sortByRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieSortByEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieSortByEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a sortBy, add some movie!
        ContentValues movieValues = TestUtilities.createMovieValues(sortByRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        Uri movieInsertUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, movieValues);
        assertTrue(movieInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert movie
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry insert.",
                movieCursor, movieValues);

        // Add the sortBy values in with the movie data so that we can make
        // sure that the join worked and we actually get all the values back
        movieValues.putAll(testValues);

        // Get the joined movie and sortBy data
        movieCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieSortBy(TestUtilities.TEST_SORTBY),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined movie and sortBy Data.",
                movieCursor, movieValues);

        // Get the joined movie and sortBy data with a start date
        movieCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieSortByWithMovieId(
                        TestUtilities.TEST_SORTBY, TestUtilities.TEST_MOVIEID),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined movie and sortBy Data with start date.",
                movieCursor, movieValues);

        // Get the joined movie data for a specific date
        movieCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieSortByWithMovieId(TestUtilities.TEST_SORTBY, TestUtilities.TEST_MOVIEID),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined movie and sortBy data for a specific movieId.",
                movieCursor, movieValues);
    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our sortBy delete.
        TestUtilities.TestContentObserver sortByObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieSortByEntry.CONTENT_URI, true, sortByObserver);

        // Register a content observer for our movie delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        sortByObserver.waitForNotificationOrFail();
        movieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(sortByObserver);
        mContext.getContentResolver().unregisterContentObserver(movieObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 1;
    static ContentValues[] createBulkInsertMovieValues(long sortByRowId) {
        String currentTestMovieID = TestUtilities.TEST_MOVIEID;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieEntry.COLUMN_SORTBY_KEY, sortByRowId);
            movieValues.put(MovieEntry.COLUMN_MOVIE_ID,currentTestMovieID);
            movieValues.put(MovieEntry.COLUMN_TITLE, "Harry Potter");
            movieValues.put(MovieEntry.COLUMN_IMAGE_URL, "www.harrypotter.com");
            movieValues.put(MovieEntry.COLUMN_PLOT, "You're a wizard Harry.");
            movieValues.put(MovieEntry.COLUMN_RATING, "10.00");
            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "19970523");

            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }


    public void testBulkInsert() {
        // first, let's create a sortBy value
        ContentValues testValues = TestUtilities.createPopularMovieSortValues();
        Uri sortByUri = mContext.getContentResolver().insert(MovieSortByEntry.CONTENT_URI, testValues);
        long sortByRowId = ContentUris.parseId(sortByUri);

        // Verify we got a row back.
        assertTrue(sortByRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieSortByEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating MovieSortByEntry.",
                cursor, testValues);

        // Now we can bulkInsert some movie.  In fact, we only implement BulkInsert for movie
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues(sortByRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}