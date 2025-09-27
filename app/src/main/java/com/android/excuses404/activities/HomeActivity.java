package com.android.excuses404.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (tokenRepository.hasToken()) {
            String token = tokenRepository.getToken();
            int userId = tokenRepository.getUserId();
        } else {
            Log.w(TAG, "No hay token almacenado");
        }

        setupProfileMenu();
    }

    private void setupProfileMenu() {
        ImageView profileIcon = findViewById(R.id.ivProfile);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileMenu(v);
            }
        });
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_my_profile) {
                    handleMyProfile();
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    handleLogout();
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }

    private void handleMyProfile() {
        boolean hasToken = tokenRepository.hasToken();
        String token = tokenRepository.getToken();
        boolean isLoggedIn = tokenRepository.isLoggedIn();

        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void handleLogout() {
        tokenRepository.clearToken();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        redirectToAuth();
    }

    private void redirectToAuth() {
        Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
