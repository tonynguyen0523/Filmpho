package com.android.tonynguyen0523.filmpho.Detail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.tonynguyen0523.filmpho.R;
import com.android.tonynguyen0523.filmpho.Review.ReviewFragment;

public class DetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            // Get intent and data that was passed through
            // and save it in DETAIL_URI to access data in fragment.
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            arguments.putString(DetailFragment.REPLACE_FRAGMENT, getIntent().getExtras().getString(getString(R.string.resource_id)));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_review, ReviewFragment.newInstance(getIntent().getData(),getIntent().
                            getBooleanExtra(getString(R.string.isNowPlaying),false)))
                    .commit();

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail, fragment)
                    .commit();


        }
    }
}