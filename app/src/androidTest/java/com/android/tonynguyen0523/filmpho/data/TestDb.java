package com.android.tonynguyen0523.filmpho.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;


/**
 * Created by Tony Nguyen on 1/9/2017.
 */

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieSortByEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the sortBy entry
        // and movie entry tables
        assertTrue("Error: Your database was created without both the sortBy entry and movie entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieSortByEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> sortByColumnHastSet = new HashSet<String>();
        sortByColumnHastSet.add(MovieContract.MovieSortByEntry._ID);
        sortByColumnHastSet.add(MovieContract.MovieSortByEntry.COLUMN_MOVIE_SORT_BY);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            sortByColumnHastSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required sortBy
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required sortBy entry columns",
                sortByColumnHastSet.isEmpty());
        db.close();
    }

    public void testMovieSortByTable() {
        insertMovieSortBy();

    }

    public void testMovieTable() {

        long sortById = insertMovieSortBy();

        assertFalse("Error: sortBy not inserted correctly.", sortById == -1L);

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues(sortById);

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,movieValues);

        assertTrue(movieRowId != -1);

        Cursor movieCursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No records returned from sortBy Query", movieCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("textInsertReadDb movieEntry failed to validate", movieCursor,movieValues);

        assertFalse("Error: More than one record returned from movie query", movieCursor.moveToNext());

        movieCursor.close();
        dbHelper.close();
    }

    public long insertMovieSortBy() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues textValues = TestUtilities.createPopularMovieSortValues();

        long sortById;
        sortById = db.insert(MovieContract.MovieSortByEntry.TABLE_NAME,null,textValues);

        assertTrue(sortById != -1);

        Cursor cursor = db.query(
                MovieContract.MovieSortByEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue(" Error: No records returned from sortBy query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: sortBy Query Validation Failed",cursor, textValues);

        assertFalse("Error: More than one record returned from sortBy query", cursor.moveToNext());

        cursor.close();
        db.close();

        return sortById;
    }
}