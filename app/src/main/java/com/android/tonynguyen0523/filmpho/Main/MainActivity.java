package com.android.tonynguyen0523.filmpho.Main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.tonynguyen0523.filmpho.Detail.DetailActivity;
import com.android.tonynguyen0523.filmpho.Detail.DetailFragment;
import com.android.tonynguyen0523.filmpho.Favorite.FavoriteActivity;
import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.SettingsActivity;
import com.android.tonynguyen0523.filmpho.Utility;
import com.android.tonynguyen0523.filmpho.sync.FilmphoSyncAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieFragment.CallBack {

    private final String MOVIEDETAILFRAGMENT_TAG = "MDFTAG";
    private String mSortBy;
    private boolean mTwoPane;

    @BindView(R.id.popular_button)Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get preferred sort.
        mSortBy = Utility.getPreferredSortBy(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Check for two pane
        if(findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), MOVIEDETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        if(mSortBy.contains("popular")){mButton.setText(R.string.most_pop_movie);}
        else {mButton.setText(R.string.top_movies);}

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,mButton);
                popupMenu.getMenuInflater()
                        .inflate(R.menu.menu_sort, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    MovieFragment mf = (MovieFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movie);

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id){
                            case R.id.action_popular:
                                mButton.setText(R.string.most_pop_movie);
                                if(null != mf){
                                    Utility.setSortBy(MainActivity.this,"popular");
                                    mf.onSortByChanged();
                                }
                                break;
                            case R.id.action_top_rated:
                                mButton.setText(R.string.top_movies);
                                if(null != mf){
                                    Utility.setSortBy(MainActivity.this,"top_rated");
                                    mf.onSortByChanged();
                                }
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        // Launch SyncAdapter
        FilmphoSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_favorites:
                startActivity(new Intent(this, FavoriteActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        String sortBy = Utility.getPreferredSortBy(this);
        Uri uri;

        if (sortBy != null && !sortBy.equals(mSortBy)){
            MovieFragment mf = (MovieFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if(null != mf){
                mf.onSortByChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(MOVIEDETAILFRAGMENT_TAG);
            if(null != df){
                uri = null;
                df.onSortByChanged(uri);
            }
            mSortBy = sortBy;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri, String resourceId, Boolean isNowPlaying){
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,contentUri);
            args.putString(DetailFragment.REPLACE_FRAGMENT, resourceId);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container,fragment, MOVIEDETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri)
                    .putExtra(getString(R.string.isNowPlaying),isNowPlaying)
                    .putExtra(getString(R.string.resource_id),resourceId);
            startActivity(intent);
        }
    }
}

