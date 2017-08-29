package com.android.tonynguyen0523.filmpho.NowPlaying;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class MovieNowPlayingAdapter extends CursorRecyclerViewAdapter<MovieNowPlayingAdapter.ViewHolder> {

    private Context mContext;
    private GridItemClickListener listener;

    MovieNowPlayingAdapter(Context context, Cursor cursor){
        super(context,cursor);
        mContext = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.movie_poster_grid_item) ImageView mPosterImage;
        @BindView(R.id.poster_progress_bar)ProgressBar mProgressBar;
        @BindView(R.id.movie_title)TextView mTitle;
        @BindView(R.id.movie_rating)TextView mRating;

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
                .inflate(R.layout.fragment_movie_now_playing_recycler_item,parent,false);

        final ViewHolder vh = new ViewHolder(itemView);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {


        String imageUrl = Utility.formatImageUrl(cursor.getString(MovieNowPlayingFragment.COL_MOVIE_IMAGEURL));
        String title = cursor.getString(MovieNowPlayingFragment.COL_MOVIE_TITLE);
        String rating = cursor.getString(MovieNowPlayingFragment.COL_MOVIE_RATING);

        viewHolder.mTitle.setText(title);
        viewHolder.mRating.setText(mContext.getString(R.string.rating,rating));

        Picasso.with(mContext).load(imageUrl).into(viewHolder.mPosterImage, new Callback() {
            @Override
            public void onSuccess() {

                viewHolder.mProgressBar.setVisibility(View.GONE);
            }
            @Override
            public void onError() {

            }
        });
    }


}
