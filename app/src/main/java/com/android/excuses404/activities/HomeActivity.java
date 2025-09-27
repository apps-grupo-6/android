package com.android.excuses404.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.android.excuses404.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new com.android.excuses404.fragments.ClassesFragment())
                    .commit();
        }

    }
}
