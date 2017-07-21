package com.android.tonynguyen0523.filmpho;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.tonynguyen0523.filmpho.data.MovieContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MovieNowPlayingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Loader Identifier
     */
    private static final int MOVIE_LOADER = 0;

    /**
     * Database projections
     */
    private static final String[] NOW_PLAYING_COLUMNS = {

            MovieContract.NowPlayingMovieEntry._ID,
            MovieContract.NowPlayingMovieEntry.COLUMN_IMAGEURL,
            MovieContract.NowPlayingMovieEntry.COLUMN_MOVIEID,
            MovieContract.NowPlayingMovieEntry.COLUMN_TITLE,
            MovieContract.NowPlayingMovieEntry.COLUMN_PLOT,
            MovieContract.NowPlayingMovieEntry.COLUMN_RATING,
            MovieContract.NowPlayingMovieEntry.COLUMN_RELEASEDATE
    };
    static final int COL_ROW_ID = 0;
    static final int COL_MOVIE_IMAGEURL = 1;
    static final int COL_MOVIE_ID = 2;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_PLOT = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_RELEAST_DATE = 6;

    /**
     * View Resources
     */
    @Nullable
    @BindView(R.id.now_playing_recycler_view)
    RecyclerView mRecyclerView;

    private MovieNowPlayingAdapter mRecyclerAdapter;
    private Unbinder unbinder;


    public MovieNowPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_now_playing, container,false);
        unbinder = ButterKnife.bind(this, rootView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        mRecyclerAdapter = new MovieNowPlayingAdapter(getContext(),null);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        mRecyclerAdapter.setOnItemClickListener(new MovieNowPlayingAdapter.GridItemClickListener() {
            @Override
            public void onGridItemClicked(View view, int position) {
                // Callback on click
                String movieId = mRecyclerAdapter.getPosterMovieId(position);
                ((MovieFragment.CallBack) getActivity()).onItemSelected(MovieContract.NowPlayingMovieEntry.BuildNowPlayingMovieWithMovieId(movieId),
                        getString(R.string.movie_detail_container));
            }
        });

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getContext(),
                MovieContract.NowPlayingMovieEntry.CONTENT_URI,
                NOW_PLAYING_COLUMNS,
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
}
