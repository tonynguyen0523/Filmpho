package com.android.tonynguyen0523.filmpho;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tonynguyen on 2/1/17.
 */

public class FavoriteRecyclerListAdapter extends CursorRecyclerViewAdapter<FavoriteRecyclerListAdapter.ViewHolder> {

    private Context mContext;

    private ListItemClickListener listener;

    public FavoriteRecyclerListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.favorite_movie_title)
        TextView titleTV;
        @BindView(R.id.favorite_plot)
        TextView plotTV;
        @BindView(R.id.favorite_thumbnail)
        ImageView posterIV;
        @BindView(R.id.favorite_movie_date)TextView releaseDateTV;
        @BindView(R.id.favorite_movie_rating)TextView ratingTV;
        @BindView(R.id.favorite_relative_layout)View relativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            listener.onListItemClickListener(view, getAdapterPosition());
        }
    }

    void setOnItemClickListener(final ListItemClickListener listener) {
        this.listener = listener;
    }

    // Create interface for a lick listener
    interface ListItemClickListener {
        void onListItemClickListener(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_recycler_items, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {

        String imageUrl = Utility.formatImageUrl(cursor.getString(FavoriteFragment.COL_MOVIE_IMAGEURL));
        String title = cursor.getString(FavoriteFragment.COL_MOVIE_TITLE);
        String plot = cursor.getString(FavoriteFragment.COL_MOVIE_PLOT);
        String date = cursor.getString(FavoriteFragment.COL_MOVIE_RELEAST_DATE);
        String rating = cursor.getString(FavoriteFragment.COL_MOVIE_RATING);

        // Get the year only from the date string
        String year = Utility.formatReleaseDateToYear(date);

        viewHolder.titleTV.setText(title);
        viewHolder.plotTV.setText(plot);
        viewHolder.ratingTV.setText(rating);
        viewHolder.releaseDateTV.setText(year);

        Picasso.with(mContext).load(imageUrl).into(viewHolder.posterIV);
    }
}
