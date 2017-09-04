package com.android.tonynguyen0523.filmpho.Detail;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_BACKDROP = "backdrop";

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.detail_collapsing_toobar)
    CollapsingToolbarLayout mCToolbar;
    @BindView(R.id.detail_backdrop)
    ImageView mBackdropIV;

    private String movieID;
    private String table;
    private String backdrop;
    private String movieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        
        mCToolbar.setTitleEnabled(false);

        if (savedInstanceState == null) {
            // Initialize database to retrieve backdrop data.
            MovieDbHelper mDbHelper = new MovieDbHelper(this);
            SQLiteDatabase mDatabase = mDbHelper.getReadableDatabase();

            Uri movieUri = getIntent().getData();
            boolean isNowPlaying = getIntent().getBooleanExtra(getString(R.string.isNowPlaying), false);

            if (isNowPlaying){
                movieID = MovieContract.NowPlayingMovieEntry.getNowPlayingMovieIdFromUri(movieUri);
                table = MovieContract.NowPlayingMovieEntry.TABLE_NAME;
            } else {
                movieID = MovieContract.MovieEntry.getMovieIdFromUri(movieUri);
                table = MovieContract.MovieEntry.TABLE_NAME;
            }

            // Retrieve backdrop data from database.
            String query = "SELECT * FROM " + table + " WHERE movie_id = " + movieID;
            Cursor cursor = mDatabase.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                backdrop = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP));
                movieTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
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
        } else {
            movieTitle = savedInstanceState.getString(MOVIE_TITLE);
            backdrop = savedInstanceState.getString(MOVIE_BACKDROP);
            getSupportActionBar().setTitle(movieTitle);
            Picasso.with(this).load(Utility.formatImageUrl(backdrop)).into(mBackdropIV);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_TITLE,movieTitle);
        outState.putString(MOVIE_BACKDROP,backdrop);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}