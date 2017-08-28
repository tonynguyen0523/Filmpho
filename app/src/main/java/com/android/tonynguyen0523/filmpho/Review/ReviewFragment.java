package com.android.tonynguyen0523.filmpho.Review;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.tonynguyen0523.filmpho.GridSpacingItemDecoration;
import com.android.tonynguyen0523.filmpho.MySingleton;
import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.Utility;
import com.android.tonynguyen0523.filmpho.data.MovieContract;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by tonynguyen on 1/24/17.
 */

public class ReviewFragment extends Fragment {

    static final String REVIEW_URI = "URI";

    static final String IS_NOW_PLAYING = "isNowPlaying";

    private ArrayList<String> reviewList;

    @Nullable
    @BindView(R.id.review_content)
    TextView mContentTextView;
    @Nullable
    @BindView(R.id.review_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_review_text)
    TextView mNoReviewText;
    @BindView(R.id.sad_face)
    ImageView mSadFace;
    @BindView(R.id.review_progress_bar)
    ProgressBar mProgressBar;
    private Unbinder unbinder;
    private Uri mUri;
    private Boolean isNowPlaying;

    public static ReviewFragment newInstance(Uri uri,Boolean isNowPlaying){
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putParcelable(REVIEW_URI,uri);
        args.putBoolean(IS_NOW_PLAYING,isNowPlaying);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(REVIEW_URI);
        isNowPlaying = getArguments().getBoolean(IS_NOW_PLAYING);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        mSadFace.setVisibility(View.GONE);
        mNoReviewText.setVisibility(View.GONE);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ReviewFragment.REVIEW_URI);
        }

        // Set RecyclerView LayoutManager.
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, GridSpacingItemDecoration.dpToPx(getContext(), 10), true));
        mRecyclerView.setNestedScrollingEnabled(false);

        // Initiate ArrayList.
        reviewList = new ArrayList<>();
        // Make sure ArrayList is clear.

        // Get movie id from uri to retrieve review data.
        final String movieId;
        if(isNowPlaying){
            movieId = MovieContract.MovieEntry.getNowPlayingMovieIdFromUri(mUri);
        } else {
            movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
        }

        if (movieId != null && Utility.hasInternet(getContext())) {
            getReviewData(movieId);
        } else {

            mProgressBar.setVisibility(View.GONE);

            Snackbar snackbar = Snackbar
                    .make(container, getString(R.string.check_internect), Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Get movie review using Volley JsonObjectRequest.
     */
    private void getReviewData(String movieId) {

        String url = Utility.getReviewUrlWithId(movieId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                displayResults(response);
                Log.v("ReviewResponse", response.toString());


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Set JsonObjectRequest to queue.
        MySingleton.getInstance(getContext().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Parse response from getReviewData() method and
     * add result to list.
     */
    private void displayResults(JSONObject response) {

        try {
            JSONArray reviewArray = response.getJSONArray("results");
            for (int i = 0; i < reviewArray.length(); i++) {

                JSONObject reviewResults = reviewArray.getJSONObject(i);

                String results = reviewResults.toString();

                // Add results to list.
                if (!results.isEmpty()) {
                    reviewList.add(results);
                }
            }

            // Set visibilities to views.
            if (!reviewList.isEmpty()) {
                mProgressBar.setVisibility(View.GONE);
                mSadFace.setVisibility(View.GONE);
                mNoReviewText.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mSadFace.setVisibility(View.VISIBLE);
                mNoReviewText.setVisibility(View.VISIBLE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Construct ReviewFragmentAdapter with ArrayList.
        ReviewRecyclerAdapter mReviewRecyclerAdapter = new ReviewRecyclerAdapter(getContext(), reviewList);
        mRecyclerView.setAdapter(mReviewRecyclerAdapter);

    }
}
