package com.android.tonynguyen0523.filmpho.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.android.tonynguyen0523.filmpho.data.MovieContract.MovieEntry;
import com.android.tonynguyen0523.filmpho.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Tony Nguyen on 1/9/2017.
 */

public class TestUtilities extends AndroidTestCase {
    static final String TEST_SORTBY = "popular";
    static final String TEST_MOVIEID = "123456";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues(Long sortByRowId) {

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_SORTBY_KEY, sortByRowId);
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, TEST_MOVIEID);
        movieValues.put(MovieEntry.COLUMN_TITLE, "Harry Potter");
        movieValues.put(MovieEntry.COLUMN_IMAGE_URL, "www.harrypotter.com");
        movieValues.put(MovieEntry.COLUMN_PLOT, "You're a wizard Harry.");
        movieValues.put(MovieEntry.COLUMN_RATING, "10.00");
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "19970523");

        return movieValues;
    }

    static ContentValues createPopularMovieSortValues(){

        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieSortByEntry.COLUMN_MOVIE_SORT_BY,TEST_SORTBY);

        return testValues;
    }


    static long insertPopularMovieSortByValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createPopularMovieSortValues();

        long sortByRowId;
        sortByRowId = db.insert(MovieContract.MovieSortByEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Popular sortBy Movie Values", sortByRowId != -1);

        return sortByRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.
        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}