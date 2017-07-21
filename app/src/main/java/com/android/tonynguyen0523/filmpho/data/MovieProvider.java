package com.android.tonynguyen0523.filmpho.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Tony Nguyen on 1/9/2017.
 */

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    /** Content Type constants */
    static final int MOVIE = 100;
    static final int MOVIE_WITH_SORTBY = 101;
    static final int MOVIE_WITH_SORTBY_AND_MOVIEID = 102;
    static final int SORTBY = 200;
    static final int FAVORITES = 300;
    static final int FAVORITES_WITH_SORTBY_AND_MOVIEID = 301;
    static final int NOW_PLAYING = 400;
    static final int NOW_PLAYING_WITH_MOVIEID = 401;

    private static final SQLiteQueryBuilder sMovieBySortByQueryBuilder;

    /**
     * Create INNER JOIN between
     * sortBy and movie tables
     */
    static {
        sMovieBySortByQueryBuilder = new SQLiteQueryBuilder();


        // INNER JOIN
        // movie INNER JOIN sortBy ON movie.sortBy_id = sortBy._id
        sMovieBySortByQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN "
                        + MovieContract.MovieSortByEntry.TABLE_NAME
                        + " ON "
                        + MovieContract.MovieEntry.TABLE_NAME
                        + " . "
                        + MovieContract.MovieEntry.COLUMN_SORTBY_KEY
                        + " = "
                        + MovieContract.MovieSortByEntry.TABLE_NAME
                        + " . "
                        + MovieContract.MovieSortByEntry._ID
        );
    }

    // sortBy.movie_sortBy = ?
    private static final String sSortBySelection =

            MovieContract.MovieSortByEntry.TABLE_NAME
                    + " . "
                    + MovieContract.MovieSortByEntry.COLUMN_MOVIE_SORT_BY
                    + " = ? ";

    // sortBy.movie_sortBy = ? AND movieId = ?
    private static final String sSortByAndMovieIdSelection =

            MovieContract.MovieSortByEntry.TABLE_NAME
                    + " . "
                    + MovieContract.MovieSortByEntry.COLUMN_MOVIE_SORT_BY
                    + " = ? AND "
                    + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                    + " = ? ";

    // favorite table
    // sort_category = ? AND movieId = ?
    private static final String sFavoriteSortAndMovieIdSelection =
            MovieContract.FavoriteMovieEntry.COLUMN_SORT_CATEGORY
                + " = ? AND "
                + MovieContract.FavoriteMovieEntry.COLUMN_MOVIEID
                + " = ? ";

    // now playing table
    // movieId = ?
    private static final String sNowPlayingMovieIdSeletion =
            MovieContract.NowPlayingMovieEntry.COLUMN_MOVIEID
                + " = ?";

    /**
     * Retrieve movies from database by the sort selected
     */
    private Cursor getMovieBySortBy(Uri uri, String[] projection, String sortOrder) {

        String sortBy = MovieContract.MovieEntry.getMovieSortByFromUri(uri);

        String[] selectionArgs = new String[]{sortBy};
        String selection = sSortBySelection;

        return sMovieBySortByQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    /**
     * Retrieve movie from database by the sort and movie selected
     */
    private Cursor getMovieBySortByAndMovieId(Uri uri, String[] projection, String sortOrder) {

        String sortBy = MovieContract.MovieEntry.getMovieSortByFromUri(uri);
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return sMovieBySortByQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sSortByAndMovieIdSelection,
                new String[]{sortBy, movieId},
                null,
                null,
                sortOrder);
    }

    /**
     * Retrieve movie from favorite database by the sort and movie selected
     */
    private Cursor getFavoriteMovieBySortAndMovieId(Uri uri, String[] projection, String sortOrder){

        String sort = MovieContract.MovieEntry.getMovieSortByFromUri(uri);
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.FavoriteMovieEntry.TABLE_NAME,
                projection,
                sFavoriteSortAndMovieIdSelection,
                new String[]{sort,movieId},
                null,
                null,
                sortOrder);
    }

    private Cursor getNowPlayingByMovieId(Uri uri, String[] projection, String sortOrder){

        String movieId = MovieContract.MovieEntry.getNowPlayingMovieIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.NowPlayingMovieEntry.TABLE_NAME,
                projection,
                sNowPlayingMovieIdSeletion,
                new String[]{movieId},
                null,
                null,
                sortOrder);
    }

    /** Construct UriMatcher with the content type constants */
    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_SORTBY);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*/*", MOVIE_WITH_SORTBY_AND_MOVIEID);
        matcher.addURI(authority, MovieContract.PATH_SORTBY, SORTBY);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE,FAVORITES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/*/*", FAVORITES_WITH_SORTBY_AND_MOVIEID);
        matcher.addURI(authority, MovieContract.PATH_NOW_PLAYING,NOW_PLAYING);
        matcher.addURI(authority, MovieContract.PATH_NOW_PLAYING + "/*", NOW_PLAYING_WITH_MOVIEID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /** Match the type constants with its type DIR/ITEM */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_WITH_SORTBY_AND_MOVIEID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_SORTBY:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case SORTBY:
                return MovieContract.MovieSortByEntry.CONTENT_TYPE;
            case FAVORITES:
                return MovieContract.FavoriteMovieEntry.CONTENT_TYPE;
            case FAVORITES_WITH_SORTBY_AND_MOVIEID:
                return MovieContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;
            case NOW_PLAYING:
                return MovieContract.NowPlayingMovieEntry.CONTENT_TYPE;
            case NOW_PLAYING_WITH_MOVIEID:
                return MovieContract.NowPlayingMovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case MOVIE_WITH_SORTBY_AND_MOVIEID: {
                retCursor = getMovieBySortByAndMovieId(uri, projection, sortOrder);
                break;
            }
            case MOVIE_WITH_SORTBY: {
                retCursor = getMovieBySortBy(uri, projection, sortOrder);
                break;
            }
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SORTBY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieSortByEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITES_WITH_SORTBY_AND_MOVIEID: {
                retCursor = getFavoriteMovieBySortAndMovieId(uri,projection,sortOrder);
                break;
            }
            case FAVORITES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case NOW_PLAYING: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.NowPlayingMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case NOW_PLAYING_WITH_MOVIEID:{
                retCursor = getNowPlayingByMovieId(uri,projection,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if( _id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SORTBY: {
                long _id = db.insert(MovieContract.MovieSortByEntry.TABLE_NAME,null,values);
                if(_id > 0)
                    returnUri = MovieContract.MovieSortByEntry.buildMovieSortByUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITES: {
                long _id = db.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME,null,values);
                if(_id > 0){
                    returnUri = MovieContract.FavoriteMovieEntry.buildFavoriteMoviesUri(_id);
                } else {
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
                }
                break;
            }
            case NOW_PLAYING: {
                long _id = db.insert(MovieContract.NowPlayingMovieEntry.TABLE_NAME,null,values);
                if(_id > 0){
                    returnUri = MovieContract.NowPlayingMovieEntry.buildNowPlayingUri(_id);
                } else {
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // This makes delete all rows return the number of rows deleted.
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SORTBY:
                rowsDeleted = db.delete(
                        MovieContract.MovieSortByEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITES:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteMovieEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case NOW_PLAYING:
                rowsDeleted = db.delete(
                        MovieContract.NowPlayingMovieEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SORTBY:
                rowsUpdated = db.update(
                        MovieContract.MovieSortByEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case NOW_PLAYING:
                rowsUpdated = db.update(
                        MovieContract.NowPlayingMovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case NOW_PLAYING:
                db.beginTransaction();
                int returnCount1 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.NowPlayingMovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount1++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount1;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
