package com.android.tonynguyen0523.filmpho.Review;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tonynguyen0523.filmpho.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tonynguyen on 1/24/17.
 */

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder>  {

    private List<String> mReviewList = new ArrayList<>();

    private Context mContext;

    private boolean contentClicked = false;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_author) TextView mAuthor;
        @BindView(R.id.review_content) TextView mContent;
        @BindView(R.id.text_box_arrow) ImageView mTextBoxArrow;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public ReviewRecyclerAdapter(Context c, ArrayList<String> reviewList) {

        mContext = c;
        mReviewList = reviewList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_recycler_items, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Strings needed to parse.
        final String MDB_REVIEW_AUTHOR = "author";
        final String MDB_REVIEW_CONTENT = "content";

        try {
            String currentReview = getItem(position).toString();
            JSONObject reviewJsonObject = new JSONObject(currentReview);

            // Get JsonObject to String.
            String author = reviewJsonObject.getString(MDB_REVIEW_AUTHOR);
            String content = reviewJsonObject.getString(MDB_REVIEW_CONTENT);

            // Set Strings to correct resources.
            holder.mAuthor.setText(author);
            holder.mContent.setText(content);

            holder.mContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(contentClicked){
                        holder.mContent.setMaxLines(10);
                        holder.mTextBoxArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                        contentClicked = false;
                    } else {
                        holder.mContent.setMaxLines(Integer.MAX_VALUE);
                        holder.mTextBoxArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                        contentClicked = true;
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public Object getItem(int position) {
        return mReviewList.get(position);
    }
}
