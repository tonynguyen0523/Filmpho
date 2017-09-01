package com.android.tonynguyen0523.filmpho.Detail;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.Review.ReviewFragment;
import com.android.tonynguyen0523.filmpho.Utility;
import com.android.tonynguyen0523.filmpho.data.MovieContract;
import com.android.tonynguyen0523.filmpho.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.detail_collapsing_toobar)
    CollapsingToolbarLayout mCToolbar;
    @BindView(R.id.detail_backdrop)
    ImageView mBackdropIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mCToolbar.setTitleEnabled(false);

        if (savedInstanceState == null) {
            // Initialize database to retrieve backdrop data.
            MovieDbHelper mDbHelper = new MovieDbHelper(this);
            SQLiteDatabase mDatabase = mDbHelper.getReadableDatabase();

            Uri movieUri = getIntent().getData();
            boolean isNowPlaying = getIntent().getBooleanExtra(getString(R.string.isNowPlaying), false);

            String movieID;
            String table;
            if (!isNowPlaying){
                movieID = MovieContract.MovieEntry.getMovieIdFromUri(movieUri);
                table = MovieContract.MovieEntry.TABLE_NAME;
            } else {
                movieID = MovieContract.NowPlayingMovieEntry.getNowPlayingMovieIdFromUri(movieUri);
                table = MovieContract.NowPlayingMovieEntry.TABLE_NAME;
            }

            // Retrieve backdrop data from database.
            String query = "SELECT * FROM " + table + " WHERE movie_id = " + movieID;
            Cursor cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                String backdrop = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP));
                String movieTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                getSupportActionBar().setTitle(movieTitle);
                Picasso.with(this).load(Utility.formatImageUrl(backdrop)).into(mBackdropIV);
                cursor.close();
            }

            // Get intent and data that was passed through
            // and save it in DETAIL_URI to access data in fragment.
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            arguments.putString(DetailFragment.REPLACE_FRAGMENT, getIntent().getExtras().getString(getString(R.string.resource_id)));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_review, ReviewFragment.newInstance(getIntent().getData(), getIntent().
                            getBooleanExtra(getString(R.string.isNowPlaying), false)))
                    .commit();

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }
}