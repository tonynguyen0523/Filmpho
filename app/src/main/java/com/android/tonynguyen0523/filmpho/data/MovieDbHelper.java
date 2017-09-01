package com.android.tonynguyen0523.filmpho.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.tonynguyen0523.filmpho.data.MovieContract.FavoriteMovieEntry;
import com.android.tonynguyen0523.filmpho.data.MovieContract.MovieSortByEntry;

import static com.android.tonynguyen0523.filmpho.data.MovieContract.*;
import static com.android.tonynguyen0523.filmpho.data.MovieContract.MovieEntry;

/**
 * Created by Tony Nguyen on 1/9/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 9;

    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_SORTBY_TABLE = "CREATE TABLE " + MovieSortByEntry.TABLE_NAME + " ("
                + MovieSortByEntry._ID + " INTEGER PRIMARY KEY,"
                + MovieSortByEntry.COLUMN_MOVIE_SORT_BY + " TEXT UNIQUE NOT NULL);";

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("
                + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MovieEntry.COLUMN_SORTBY_KEY + " TEXT NOT NULL,"
                + MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + MovieEntry.COLUMN_IMAGE_URL + " TEXT,"
                + MovieEntry.COLUMN_BACKDROP + " TEXT,"
                + MovieEntry.COLUMN_PLOT + " TEXT NOT NULL,"
                + MovieEntry.COLUMN_RATING + " TEXT NOT NULL,"
                + MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL,"
                + MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL,"

                // Set up the sortBy column as a foreign key to sortBy table.
                + " FOREIGN KEY (" + MovieEntry.COLUMN_SORTBY_KEY + ") REFERENCES "
                + MovieSortByEntry.TABLE_NAME + " ("
                + MovieSortByEntry._ID + "),"

                // To assure the application have just one movie entry
                // per sortBy, it's created a UNIQUE constraint with REPLACE strategy.
                + " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ", "
                + MovieEntry.COLUMN_SORTBY_KEY + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " ("
                + FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FavoriteMovieEntry.COLUMN_MOVIEID + " TEXT UNIQUE NOT NULL,"
                + FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + FavoriteMovieEntry.COLUMN_IMAGEURL + " TEXT,"
                + FavoriteMovieEntry.COLUMN_BACKDROP + " TEXT,"
                + FavoriteMovieEntry.COLUMN_PLOT + " TEXT NOT NULL,"
                + FavoriteMovieEntry.COLUMN_RATING + " TEXT NOT NULL,"
                + FavoriteMovieEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL,"
                + FavoriteMovieEntry.COLUMN_SORT_CATEGORY + " TEXT NOT NULL"+ ");";

        final String SQL_CREATE_NOW_PLAYING_TABLE = "CREATE TABLE " + NowPlayingMovieEntry.TABLE_NAME + " ("
                + NowPlayingMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NowPlayingMovieEntry.COLUMN_MOVIEID + " TEXT UNIQUE NOT NULL,"
                + NowPlayingMovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + NowPlayingMovieEntry.COLUMN_IMAGEURL + " TEXT,"
                + NowPlayingMovieEntry.COLUMN_BACKDROP + " TEXT,"
                + NowPlayingMovieEntry.COLUMN_PLOT + " TEXT NOT NULL,"
                + NowPlayingMovieEntry.COLUMN_RATING + " TEXT NOT NULL,"
                + NowPlayingMovieEntry.COLUMN_RELEASEDATE + " TEXT NOT NULL"+ ");";

        db.execSQL(SQL_CREATE_SORTBY_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
        db.execSQL(SQL_CREATE_NOW_PLAYING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + MovieSortByEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NowPlayingMovieEntry.TABLE_NAME);
        onCreate(db);

    }
}
