package com.android.tonynguyen0523.filmpho;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        if (savedInstanceState == null) {

            FavoriteFragment fragment = new FavoriteFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.favorite_movie_container, fragment)
                    .commit();
        }
    }
}
