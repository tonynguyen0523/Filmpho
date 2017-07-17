package com.android.tonynguyen0523.filmpho;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
}