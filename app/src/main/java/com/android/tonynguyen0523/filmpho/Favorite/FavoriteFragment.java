package com.android.tonynguyen0523.filmpho.Favorite;

import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.tonynguyen0523.filmpho.Detail.DetailFragment;
import com.android.tonynguyen0523.filmpho.GridSpacingItemDecoration;
import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.Review.ReviewFragment;
import com.android.tonynguyen0523.filmpho.data.MovieContract;
import com.android.tonynguyen0523.filmpho.data.MovieContract.FavoriteMovieEntry;
import com.android.tonynguyen0523.filmpho.data.MovieDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by tonynguyen on 1/25/17.
 */

public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Loader identifier */
    private static final int FAVORITE_LOADER = 0;

    /** Database projections */
    private static final String[] FAVORITE_MOVIE_COLUMNS = {
            FavoriteMovieEntry._ID,
            FavoriteMovieEntry.COLUMN_MOVIEID,
            FavoriteMovieEntry.COLUMN_TITLE,
            FavoriteMovieEntry.COLUMN_PLOT,
            FavoriteMovieEntry.COLUMN_IMAGEURL,
            FavoriteMovieEntry.COLUMN_RATING,
            FavoriteMovieEntry.COLUMN_RELEASEDATE,
            FavoriteMovieEntry.COLUMN_SORT_CATEGORY
    };

    public static final int COL_ROW_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_PLOT = 3;
    public static final int COL_MOVIE_IMAGEURL = 4;
    public static final int COL_MOVIE_RATING = 5;
    public static final int COL_MOVIE_RELEAST_DATE = 6;
    public static final int COL_MOVIE_SORT_CATEGORY = 7;

    /** View resources */
    @BindView(R.id.favorite_recycler_view)RecyclerView mRecyclerView;
    @BindView(R.id.empty_favorite_text)TextView mEmptyText;

    private FavoriteRecyclerListAdapter mRecyclerAdapter;
    private Unbinder unbinder;

    /** Empty constructor */
    public FavoriteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        // Bind BindViews to rootView.
        unbinder = ButterKnife.bind(this, rootView);

        // Check if favorite table has entries,
        // if not show empty view text.
        if(containsMovies()){
            mEmptyText.setVisibility(View.GONE);
        } else {
            mEmptyText.setVisibility(View.VISIBLE);
        }

        // Set grid span by orientation.
        int gridSpan;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridSpan = 2;
        } else {
            gridSpan = 1;
        }

        // Initialize and set adapter and LayoutManager
        mRecyclerAdapter = new FavoriteRecyclerListAdapter(getContext(),null);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),gridSpan));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(gridSpan, GridSpacingItemDecoration.dpToPx(getContext(),10),true));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        // Initialize RecyclerView click listener.
        mRecyclerAdapter.setOnItemClickListener(new FavoriteRecyclerListAdapter.ListItemClickListener() {
            @Override
            public void onListItemClickListener(View view, int position) {

                // Retrieve selected movies' movieId and sort category
                // and build Uri.
                String movieId = mRecyclerAdapter.getFavoriteMovieId(position);
                String sortCategory = mRecyclerAdapter.getFavoriteMovieSortCategory(position);
                Uri uri = MovieContract.FavoriteMovieEntry.buildFavoriteMovieWithSortAndMovieId(sortCategory,movieId);

                Bundle args = new Bundle();
                args.putParcelable(DetailFragment.DETAIL_URI, uri);
                args.putString(DetailFragment.REPLACE_FRAGMENT, getString(R.string.favorite_movie_containter));

                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(args);

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.favorite_movie_container, detailFragment);
                ft.replace(R.id.favorite_review_container, ReviewFragment.newInstance(uri,false));
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        // Initiate Loader
        getLoaderManager().initLoader(FAVORITE_LOADER,null,this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

        @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new CursorLoader(getContext(),
                    FavoriteMovieEntry.CONTENT_URI,
                    FAVORITE_MOVIE_COLUMNS,
                    null,
                    null,
                    null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecyclerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerAdapter.swapCursor(null);
    }

    /**
     * Method to check if movies exist in database.
     */
    private boolean containsMovies() {

        MovieDbHelper mDbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase mDatabase = mDbHelper.getReadableDatabase();
        long exist = DatabaseUtils.queryNumEntries(mDatabase, FavoriteMovieEntry.TABLE_NAME, null, null);

        mDatabase.close();

        if (exist > 0) {
            return true;
        } else {
            return false;
        }
    }

}

