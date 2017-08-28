package com.android.tonynguyen0523.filmpho.Detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tonynguyen on 1/25/17.
 */

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.ViewHolder> {

    private List<String> mVideoList = new ArrayList<>();

    private Context mContext;

    private ShareActionProvider mShareActionProvider;

    public class ViewHolder extends RecyclerView.ViewHolder{
//        @BindView(R.id.video_type) TextView mVideoType;
//        @BindView(R.id.video_title) TextView mVideoTitle;
        @BindView(R.id.video_menu) ImageView mVideoMenu;
        @BindView(R.id.video_thumbnail) ImageView mVideoThumbnail;

        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
    public VideoRecyclerAdapter(Context context, ArrayList<String> videoList){
        mContext = context;
        mVideoList = videoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_recycler_items,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Strings to parse from json object.
        final String MDB_VIDEO_KEY = "key";
        final String MDB_VIDEO_TYPE = "type";
        final String MDB_VIDEO_NAME = "name";

        try{
            String currentPositionStr = getItem(position).toString();
            JSONObject jsonObject = new JSONObject(currentPositionStr);

            final String key = jsonObject.getString(MDB_VIDEO_KEY);
            String type = jsonObject.getString(MDB_VIDEO_TYPE);
            String name = jsonObject.getString(MDB_VIDEO_NAME);

            // Set Strings to correct resource item.
//            holder.mVideoType.setText(type);
//            holder.mVideoTitle.setText(name);

            // Get url for video youtube thumbnail,
            // and set to image view.
            String youtubeThumbnailUrl = "http://img.youtube.com/vi/"+key+"/0.jpg";
            Picasso.with(mContext).load(youtubeThumbnailUrl).into(holder.mVideoThumbnail);

            // Set onClickListener when clicking video thumbnail.
            holder.mVideoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String youtubeUrl = Utility.getYouTubeUrl(key);

                    // Open youtube to watch video selected.
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl)));
                }
            });

            // Set onClick for video pop up menu
            holder.mVideoMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String url = Utility.getYouTubeUrl(key);
                    showPopupMenu(holder.mVideoMenu,url);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, String url) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuItem menuItem = popup.getMenu().findItem(R.id.action_share);
        mShareActionProvider = new ShareActionProvider(mContext);
        MenuItemCompat.setActionProvider(menuItem,mShareActionProvider);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_video, popup.getMenu());

        popup.show();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        mShareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public Object getItem(int position){ return  mVideoList.get(position);}
}
