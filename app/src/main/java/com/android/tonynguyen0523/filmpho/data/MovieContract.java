package com.android.tonynguyen0523.filmpho.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Tony Nguyen on 1/9/2017.
 */

public class MovieContract {

    /**
     * Content authority
     */
    public static final String CONTENT_AUTHORITY = "com.android.tonynguyen0523.filmpho";

    /**
     * Base content uri
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** Uri paths */
    public static final String PATH_SORTBY = "sortBy";
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_FAVORITE = "favorites";
    public static final String PATH_NOW_PLAYING = "now_playing";

    /** Now playing movie entries */
    public static final class NowPlayingMovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOW_PLAYING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOW_PLAYING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOW_PLAYING;

        public static final String TABLE_NAME = "now_playing";
        public static final String COLUMN_MOVIEID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASEDATE = "release_date";
        public static final String COLUMN_IMAGEURL = "poster";

        public static Uri buildNowPlayingUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static  Uri BuildNowPlayingMovieWithMovieId(String movieId){
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static  Uri BuildNowPlayingMovieWithSortAndMovieId(String sort, String movieId){
            return CONTENT_URI.buildUpon().appendPath(sort).appendPath(movieId).build();
        }
    }


    /** Favorite movie entries */
    public static final class FavoriteMovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIEID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASEDATE = "release_date";
        public static final String COLUMN_IMAGEURL = "poster";
        public static final String COLUMN_SORT_CATEGORY = "sort_category";

        public static Uri buildFavoriteMoviesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildFavoriteMovieWithSortAndMovieId(String sort,String movieId){
            return CONTENT_URI.buildUpon().appendPath(sort)
                    .appendPath(movieId).build();
        }
    }

    /** SortBy entries */
    public static final class MovieSortByEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SORTBY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SORTBY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SORTBY;


        public static final String TABLE_NAME = "sortBy";

        // This string will be sent to MovieDB as the sort query.
        public static final String COLUMN_MOVIE_SORT_BY = "movie_sortBy";

        public static Uri buildMovieSortByUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }

    /** Movie entries */
    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        // Column with the foreign key into the sortBy table.
        public static final String COLUMN_SORTBY_KEY = "sortBy_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movieId";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildMovieSortBy(String sortBy){
            return CONTENT_URI.buildUpon().appendPath(sortBy).build();
        }

        public static Uri buildMovieSortByWithMovieId(String sortBy, String movieId){
            return CONTENT_URI.buildUpon().appendPath(sortBy)
                    .appendPath(movieId).build();
        }

        public static String getMovieTableFromUri(Uri uri){
            return uri.getPathSegments().get(0);
        }

        public static String getMovieSortByFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

        public static String getNowPlayingMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
