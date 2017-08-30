package com.android.tonynguyen0523.filmpho.Main;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.Utility;
import com.android.tonynguyen0523.filmpho.data.MovieContract;
import com.android.tonynguyen0523.filmpho.sync.FilmphoSyncAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Tony Nguyen on 1/3/2017.
 */

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Loader Identifier
     */
    private static final int MOVIE_LOADER = 0;

    /**
     * Database projections
     */
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + " . " + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_IMAGE_URL,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_PLOT,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    public static final int COL_ROW_ID = 0;
    public static final int COL_MOVIE_IMAGEURL = 1;
    public static final int COL_MOVIE_ID = 2;
    public static final int COL_MOVIE_TITLE = 3;
    public static final int COL_MOVIE_PLOT = 4;
    public static final int COL_MOVIE_RATING = 5;
    public static final int COL_MOVIE_RELEAST_DATE = 6;

    /**
     * View Resources
     */
    @Nullable
    @BindView(R.id.movie_recycler_grid_view)
    RecyclerView mRecyclerView;

    private MovieFragmentAdapter mRecyclerAdapter;
    private Unbinder unbinder;

    /** Save position variables */
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    /**
     * Create interface CallBack
     */
    public interface CallBack {
        void onItemSelected(Uri movieIdUri, String resource, Boolean isNowPlaying);
    }

    /**
     * Empty constructor
     */
    public MovieFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        // Bind BindViews to rootView.
        unbinder = ButterKnife.bind(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        mRecyclerAdapter = new MovieFragmentAdapter(getContext(),null);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        // Initiate RecyclerView click listener
        // to launch DetailActivity/DetailFragment.
        mRecyclerAdapter.setOnItemClickListener(new MovieFragmentAdapter.GridItemClickListener() {
            @Override
            public void onGridItemClicked(View view, int position) {

                // Callback on click
                String movieId = mRecyclerAdapter.getPosterMovieId(position);
                String sortBy = Utility.getPreferredSortBy(getActivity());
                ((CallBack) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieSortByWithMovieId(
                        sortBy, movieId), getString(R.string.movie_detail_container), false);

                // Update current position
                mPosition = position;
            }
        });

        // Return to correct position view.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check for internet connectivity.
        if (!Utility.hasInternet(getContext())) {
            Snackbar snackbar = Snackbar
                    .make(getView(), getString(R.string.check_internect), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onSortByChanged();
                        }
                    });
            snackbar.show();
        }
    }

    /** Update when user changes sort option */
    void onSortByChanged() {
        updateMovie();
        mPosition = 0;
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    /** Update method */
    private void updateMovie() {
        FilmphoSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortBy = Utility.getPreferredSortBy(getActivity());
        Uri movieForSortByUri = MovieContract.MovieEntry.buildMovieSortBy(sortBy);

        return new CursorLoader(getActivity(),
                movieForSortByUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        mRecyclerAdapter.swapCursor(cursor);

        if (mPosition != RecyclerView.NO_POSITION) {
            mRecyclerView.smoothScrollToPosition(mPosition);
        }

        // Check for internet connectivity.
        if (!Utility.hasInternet(getContext())) {
            Snackbar snackbar = Snackbar
                    .make(getView(), getString(R.string.check_internect), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onSortByChanged();
                        }
                    });
            snackbar.show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mRecyclerAdapter.swapCursor(null);
    }
}