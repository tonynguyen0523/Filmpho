package com.android.tonynguyen0523.filmpho.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Tony Nguyen on 1/9/2017.
 */

public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final String TEST_MOVIE_SORTBY = "/popular";
    private static final String TEST_MOVIE_ID = "12345";

    public void testBuildWeatherLocation() {
        Uri sortByUri = MovieContract.MovieEntry.buildMovieSortBy(TEST_MOVIE_SORTBY);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieSortBy in " +
                        "MovieContract.",
                sortByUri);
        assertEquals("Error: Movie sortBy not properly appended to the end of the Uri",
               TEST_MOVIE_SORTBY, sortByUri.getLastPathSegment());
        assertEquals("Error: Movie sortBy Uri doesn't match our expected result",
                sortByUri.toString(),
                "content://com.android.tonynguyen0523.filmpho/movie/%2Fpopular");
    }
}