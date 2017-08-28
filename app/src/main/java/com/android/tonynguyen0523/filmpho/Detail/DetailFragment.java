package com.android.tonynguyen0523.filmpho.Detail;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.tonynguyen0523.filmpho.Favorite.FavoriteActivity;
import com.android.tonynguyen0523.filmpho.GridSpacingItemDecoration;
import com.android.tonynguyen0523.filmpho.MySingleton;
import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.Utility;
import com.android.tonynguyen0523.filmpho.data.MovieContract;
import com.android.tonynguyen0523.filmpho.data.MovieDbHelper;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.android.tonynguyen0523.filmpho.data.MovieContract.FavoriteMovieEntry;
import static com.android.tonynguyen0523.filmpho.data.MovieContract.MovieEntry;

/**
 * Created by Tony Nguyen on 1/11/2017.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Reference for uri
     */
    public static final String DETAIL_URI = "URI";

    public static final String REPLACE_FRAGMENT = "RESOURCE_ID";

    /**
     * Loader identifier
     */
    private static final int DETAIL_LOADER = 0;

    /**
     * Database projections when movie is clicked
     * in MainActivity.
     */
    private static final String[] MOVIE_COLUMNS = {

            MovieEntry.TABLE_NAME + " . " + MovieEntry._ID,
            MovieEntry.COLUMN_IMAGE_URL,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_PLOT,
            MovieEntry.COLUMN_RATING,
            MovieEntry.COLUMN_RELEASE_DATE
    };

    /**
     * Database projection when movie is clicked
     * in FavoriteActivity.
     */
    private static final String[] FAVORITE_COLUMNS = {

            FavoriteMovieEntry._ID,
            FavoriteMovieEntry.COLUMN_IMAGEURL,
            FavoriteMovieEntry.COLUMN_MOVIEID,
            FavoriteMovieEntry.COLUMN_TITLE,
            FavoriteMovieEntry.COLUMN_PLOT,
            FavoriteMovieEntry.COLUMN_RATING,
            FavoriteMovieEntry.COLUMN_RELEASEDATE,
            FavoriteMovieEntry.COLUMN_SORT_CATEGORY
    };

    private static final String[] NOW_PLAYING = {

            FavoriteMovieEntry._ID,
            FavoriteMovieEntry.COLUMN_IMAGEURL,
            FavoriteMovieEntry.COLUMN_MOVIEID,
            FavoriteMovieEntry.COLUMN_TITLE,
            FavoriteMovieEntry.COLUMN_PLOT,
            FavoriteMovieEntry.COLUMN_RATING,
            FavoriteMovieEntry.COLUMN_RELEASEDATE,
    };

    static final int COL_ROW_ID = 0;
    static final int COL_MOVIE_IMAGEURL = 1;
    static final int COL_MOVIE_ID = 2;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_PLOT = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_RELEASE_DATE = 6;
    static final int COL_MOVIE_SORT_CATEGORY = 7;

    /**
     * View resources
     */
    @BindView(R.id.detail_title_textview)
    TextView mTitleTextView;
    @BindView(R.id.detail_poster_imageview)
    ImageView mPosterImageView;
    @BindView(R.id.detail_plot_textview)
    TextView mPlotTextView;
    @BindView(R.id.detail_rating_textview)
    TextView mRatingTextView;
    @BindView(R.id.detail_release_date_textview)
    TextView mReleaseDateTextView;
    @BindView(R.id.favorite_toggle)
    ToggleButton mFavToggleButton;
    @BindView(R.id.detail_relative_layout)
    View mDetailRelativeLayout;
    @Nullable
    @BindView(R.id.video_recycler_view)
    RecyclerView mVideoRecyclerView;


    /**
     * ArrayList for video results
     */
    private ArrayList<String> videosList;

    private Unbinder unbinder;
    private Uri mUri;
    private String mReplaceFragment;
    private VideoRecyclerAdapter mVideoAdapter;

    private String title;
    private String movieId;
    private String imageUrl;
    private String plot;
    private String rating;
    private String releaseDate;
    private String table;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        // Bind the bindViews to View.
        unbinder = ButterKnife.bind(this, rootView);

        // Get uri passed in.
        Bundle arguments = getArguments();
        if (arguments != null && Utility.hasInternet(getContext()) ||
                arguments != null && arguments.getString(DetailFragment.REPLACE_FRAGMENT).contains("favorite")) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            mReplaceFragment = arguments.getString(DetailFragment.REPLACE_FRAGMENT);
        } else {
            // If uri is null set detail views INVISIBLE.
            mDetailRelativeLayout.setVisibility(View.INVISIBLE);
        }

        // Initialize ArrayList.
        videosList = new ArrayList<>();
        // Set the adapter to video's recycler view.
        mVideoAdapter = new VideoRecyclerAdapter(getContext(), videosList);
        mVideoRecyclerView.setAdapter(mVideoAdapter);
        mVideoRecyclerView.setNestedScrollingEnabled(false);

        // Set LayoutManager of the recycler view to LinearLayout.
        mVideoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mVideoRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, GridSpacingItemDecoration.dpToPx(getContext(), 7), true));

//        // Pass current uri to ReviewActivity when review button is clicked.
//        mReviewButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Send current uri to ReviewFragment
//                Bundle args = new Bundle();
//                args.putParcelable(ReviewFragment.REVIEW_URI, mUri);
//
//                ReviewFragment reviewFragment = new ReviewFragment();
//                reviewFragment.setArguments(args);
//
//                // Get current fragment resource id to replace
//                int resourceId = getContext().getResources()
//                        .getIdentifier(mReplaceFragment,"id", getContext().getPackageName());
//
//                final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(resourceId, reviewFragment);
//                ft.addToBackStack(null);
//                ft.commit();
//                Log.d("RESOURCE", Integer.toString(resourceId));
//            }
//
//        });

        // Insert or delete selected movie to/from favorite database.
        mFavToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFavToggleButton.isChecked()) {

                    // Get sort category that the movie belongs to.
                    String sortCategory = MovieEntry.getMovieSortByFromUri(mUri);

                    // Create Content Values.
                    ContentValues values = new ContentValues();
                    values.put(FavoriteMovieEntry.COLUMN_TITLE, title);
                    values.put(FavoriteMovieEntry.COLUMN_MOVIEID, movieId);
                    values.put(FavoriteMovieEntry.COLUMN_PLOT, plot);
                    values.put(FavoriteMovieEntry.COLUMN_RATING, rating);
                    values.put(FavoriteMovieEntry.COLUMN_RELEASEDATE, releaseDate);
                    values.put(FavoriteMovieEntry.COLUMN_IMAGEURL, imageUrl);
                    values.put(FavoriteMovieEntry.COLUMN_SORT_CATEGORY, sortCategory);

                    // Insert values into favorite database.
                    getContext().getContentResolver().insert(FavoriteMovieEntry.CONTENT_URI, values);

                    Snackbar snackbar = Snackbar
                            .make(container, title + " has been added to Favorites!", Snackbar.LENGTH_LONG)
                            .setAction("GO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), FavoriteActivity.class);
                                    startActivity(intent);
                                }
                            });
                    snackbar.show();
                } else {

                    // Delete from favorite database.
                    getContext().getContentResolver().delete(FavoriteMovieEntry.CONTENT_URI, FavoriteMovieEntry.COLUMN_MOVIEID + "=?",
                            new String[]{movieId});

                    if(mReplaceFragment.contains("favorite")){

                        // Relaunch FavoriteActivity and clear back stack.
                        Intent intent = new Intent(getContext(), FavoriteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();


                    } else {
                        Snackbar snackbar = Snackbar
                                .make(container, title + " has been deleted from Favorites.", Snackbar.LENGTH_LONG)
                                .setAction("GO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), FavoriteActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        snackbar.show();
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * On ActivityCreate initiate Loader
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Restart Loader when sort is changed
     */
    public void onSortByChanged(Uri uri) {
        mUri = uri;
        videosList.clear();
        mDetailRelativeLayout.setVisibility(View.GONE);
    }

    void restartDetail(){
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if (null != mUri) {

            String movieEntryTable = "movie";
            String favoritesTable = "favorites";
            String nowPlaying = "now_playing";
            table = MovieContract.MovieEntry.getMovieTableFromUri(mUri);

            // Check which table to access.
            if (table.contains(movieEntryTable)) {
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);
            } else if (table.contains(favoritesTable)) {
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        FAVORITE_COLUMNS,
                        null,
                        null,
                        null);
            } else if( table.contains(nowPlaying)){
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        NOW_PLAYING,
                        null,
                        null,
                        null);
            }

        }

            return null;
        }


        @Override
        public void onLoadFinished (Loader < Cursor > loader, Cursor data){

            if (data != null && data.moveToFirst()) {

                // Retrieve and set data from columns.
                title = data.getString(COL_MOVIE_TITLE);
                movieId = data.getString(COL_MOVIE_ID);
                imageUrl = data.getString(COL_MOVIE_IMAGEURL);
                plot = data.getString(COL_MOVIE_PLOT);
                rating = data.getString(COL_MOVIE_RATING);
                releaseDate = data.getString(COL_MOVIE_RELEASE_DATE);

                mTitleTextView.setText(title);
                mPlotTextView.setText(plot);
                mRatingTextView.setText(rating);

                // Display correct toggle image is movie exist in favorite database or not.
                if (movieIdExist(movieId)) {
                    mFavToggleButton.setChecked(true);
                } else {
                    mFavToggleButton.setChecked(false);
                }

                // Get the year only from the date string
                String year = Utility.formatReleaseDateToYear(releaseDate);
                mReleaseDateTextView.setText(year);

                // Create image url to display poster thumbnail.
                // Load picture from url with Picasso.
                String finalImageUrl = Utility.formatImageUrl(imageUrl);
                Picasso.with(this.getActivity()).load(finalImageUrl).into(mPosterImageView);
//                Picasso.with(this.getActivity()).load(finalImageUrl).into(mBigPoster);
            } else {
                mDetailRelativeLayout.setVisibility(View.GONE);
            }

            if(mUri != null) {
                // Create video url with current movie id.
                String videoUrl;
                if(!table.contains("now_playing")) videoUrl = Utility.getVideosUrlWithId(MovieEntry.getMovieIdFromUri(mUri));
                else videoUrl = Utility.getVideosUrlWithId(MovieEntry.getNowPlayingMovieIdFromUri(mUri));

                // Get movie videos.
                // JSON parse video url using Volley.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET, videoUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        displayVideoResults(response);

//                        if (videosList.isEmpty()) {
//                            mVideoHeader.setText(R.string.no_videos);
//                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                // Set json request to queue.
                MySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(jsonObjectRequest);

            }

            // Check for internet connectivity.
            if(!Utility.hasInternet(getContext())){
                Snackbar snackbar = Snackbar
                        .make(getView(), R.string.check_internect, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                restartDetail();
                            }
                        });
                snackbar.show();
            }

        }

        @Override
        public void onLoaderReset (Loader < Cursor > loader) {
        }

    // Method to parse JSONObject.
    private void displayVideoResults(JSONObject response) {

        // Clear list first
        videosList.clear();

        try {
            JSONArray videoArray = response.getJSONArray("results");
            for (int i = 0; i < videoArray.length(); i++) {

                JSONObject videoResults = videoArray.getJSONObject(i);

                String resultsStr = videoResults.toString();

                // Add results to ArrayList.
                if (!resultsStr.isEmpty()) {
                    videosList.add(resultsStr);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set the adapter to video's recycler view.
        mVideoAdapter = new VideoRecyclerAdapter(getContext(), videosList);
        mVideoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        mVideoRecyclerView.setAdapter(mVideoAdapter);
    }

    /**
     * Method to check if movie_id exist in database.
     */
    private boolean movieIdExist(String movieId) {

        MovieDbHelper mDbHelper = new MovieDbHelper(getContext());
        SQLiteDatabase mDatabase = mDbHelper.getReadableDatabase();
        long exist = DatabaseUtils.queryNumEntries(mDatabase, FavoriteMovieEntry.TABLE_NAME,
                FavoriteMovieEntry.COLUMN_MOVIEID +
                        " = " + movieId, null);

        mDatabase.close();

        if (exist > 0) {
            return true;
        } else {
            return false;
        }
    }
}
