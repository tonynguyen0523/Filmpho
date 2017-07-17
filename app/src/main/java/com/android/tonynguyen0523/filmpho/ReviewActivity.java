package com.android.tonynguyen0523.filmpho;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        if (savedInstanceState == null) {


            ReviewFragment fragment = new ReviewFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_review_container, fragment)
                    .commit();
        }
    }
}
