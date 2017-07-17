package com.android.tonynguyen0523.filmpho.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Tony Nguyen on 1/9/2017.
 */

public class TestUriMatcher extends AndroidTestCase {
    private static final String SORTBY_QUERY = "popular";
    private static final String TEST_MOVIEID = "12345";
    private static final long TEST_SORTBY_ID = 7L;

    // content://com.example.android.sunshine.app/Movie"
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_SORTBY_DIR = MovieContract.MovieEntry.buildMovieSortBy(SORTBY_QUERY);
    private static final Uri TEST_MOVIE_WITH_SORTBY_AND_MOVIEID_DIR = MovieContract.MovieEntry.buildMovieSortByWithMovieId(SORTBY_QUERY, TEST_MOVIEID);
    // content://com.example.android.sunshine.app/SORTBY"
    private static final Uri TEST_SORTBY_DIR = MovieContract.MovieSortByEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The Movie URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The Movie WITH SORTBY URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_SORTBY_DIR), MovieProvider.MOVIE_WITH_SORTBY);
        assertEquals("Error: The Movie WITH SORTBY AND DATE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_SORTBY_AND_MOVIEID_DIR), MovieProvider.MOVIE_WITH_SORTBY_AND_MOVIEID);
        assertEquals("Error: The SORTBY URI was matched incorrectly.",
                testMatcher.match(TEST_SORTBY_DIR), MovieProvider.SORTBY);
    }
}