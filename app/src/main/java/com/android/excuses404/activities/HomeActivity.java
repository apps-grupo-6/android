package com.android.excuses404.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.excuses404.R;
import com.android.excuses404.core.repository.TokenRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @Inject
    TokenRepository tokenRepository;

    private Button btnLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            tokenRepository.clearAll();

            redirectToAuth();
        });

        if (tokenRepository.hasToken()) {
            String token = tokenRepository.getToken();
            int userId = tokenRepository.getUserId();
        } else {
            Log.w(TAG, "No hay token almacenado");
        }
    }

    private void redirectToAuth() {
        Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}