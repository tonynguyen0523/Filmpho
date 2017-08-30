package com.android.tonynguyen0523.filmpho.Main;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.tonynguyen0523.filmpho.CursorRecyclerViewAdapter;
import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tonynguyen on 1/28/17.
 */

public class MovieFragmentAdapter extends CursorRecyclerViewAdapter<MovieFragmentAdapter.ViewHolder> {

    private Context mContext;
    private GridItemClickListener listener;

    MovieFragmentAdapter(Context context, Cursor cursor){
        super(context,cursor);
        mContext = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.movie_poster_grid_item) ImageView mPosterImage;
        @BindView(R.id.movie_position_number) TextView mPositionNumber;
        @BindView(R.id.movie_card_title) TextView mMovieTitle;
        @BindView(R.id.movie_progress_bar)ProgressBar mProgressBar;
        @BindView(R.id.movie_rating_bar)RatingBar mRatingBar;
        @BindView(R.id.movie_poster_empty_view)TextView mEmptyView;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            ButterKnife.bind(this,view);
        }

        @Override
        public void onClick(View view) {
            listener.onGridItemClicked(view,getAdapterPosition());
        }
    }

    void setOnItemClickListener(final GridItemClickListener listener){
        this.listener = listener;
    }

    interface GridItemClickListener{
        void onGridItemClicked(View view, int position);
    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie_recycler_item,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {

        String imageUrl = cursor.getString(MovieFragment.COL_MOVIE_IMAGEURL);
        String imageUrlFormatted = Utility.formatImageUrl(cursor.getString(MovieFragment.COL_MOVIE_IMAGEURL));
        int position = cursor.getPosition();
        String title = cursor.getString(MovieFragment.COL_MOVIE_TITLE);
        String rating = cursor.getString(MovieFragment.COL_MOVIE_RATING);

        viewHolder.mMovieTitle.setText(title);
        viewHolder.mPositionNumber.setText(Integer.toString(position + 1));
        viewHolder.mRatingBar.setRating(Float.parseFloat(rating));

        // Check if image url is null
        if (imageUrl == null) {
                    viewHolder.mProgressBar.setVisibility(View.GONE);
                    viewHolder.mEmptyView.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(mContext).load(imageUrlFormatted).into(viewHolder.mPosterImage, new Callback() {
                @Override
                public void onSuccess() {
                    viewHolder.mProgressBar.setVisibility(View.GONE);
                    viewHolder.mEmptyView.setVisibility(View.GONE);
                }
                @Override
                public void onError() {
                }
            });
        }
    }
}
