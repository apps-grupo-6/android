package com.android.excuses404.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.excuses404.R;
import com.android.excuses404.core.biometric.BiometricAuthenticator;
import com.android.excuses404.core.repository.TokenRepository;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ActivityLifecycle";

    @Inject
    TokenRepository tokenRepository;

    private BiometricAuthenticator biometricAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "⭐ onCreate: La Activity está siendo creada");

        biometricAuthenticator = new BiometricAuthenticator(this, new BiometricAuthenticator.AuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                checkUserLoginStatus();
            }

            @Override
            public void onAuthenticationError(int errorCode, String errorMessage) {
                Toast.makeText(MainActivity.this,
                        "Error de autenticación biométrica: " + errorMessage, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        biometricAuthenticator.authenticate();
    }

    private void checkUserLoginStatus() {
        Log.d(TAG, "⭐ onCreate: validando que si el usuario esta logeado");

        boolean isLoggedIn = tokenRepository.isLoggedIn() && tokenRepository.hasToken();

        if (isLoggedIn) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            startActivity(new Intent(this, AuthActivity.class));
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "⭐ onStart: La Activity está a punto de hacerse visible");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "⭐ onResume: La Activity es visible y tiene el foco");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "⭐ onPause: La Activity está perdiendo el foco");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "⭐ onStop: La Activity ya no es visible");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "⭐ onRestart: La Activity está volviendo a empezar después de detenerse");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "⭐ onDestroy: La Activity está siendo destruida");
    }
    /*
     * private void loadPokemons() {
     * pokemonService.getAllPokemons(new PokemonServiceCallBack() {
     * 
     * @Override
     * public void onSuccess(List<Pokemon> pokemons) {
     * pokemonDisplayList.clear();
     * pokemonDisplayList.addAll(pokemons.stream()
     * .map(pokemon -> pokemon.getName() + " - " + pokemon.getType())
     * .collect(Collectors.toList()));
     * runOnUiThread(() -> adapter.notifyDataSetChanged());
     * }
     * 
     * @Override
     * public void onError(Throwable error) {
     * runOnUiThread(() -> Toast.makeText(MainActivity.this,
     * "Error al cargar los Pokemon: " + error.getMessage(),
     * Toast.LENGTH_LONG).show());
     * }
     * });
     * }
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "⭐ onSaveInstanceState: Guardando el estado de la Activity");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "⭐ onRestoreInstanceState: Restaurando el estado guardado de la Activity");
    }
    /*
     * @Override
     * public boolean onCreateOptionsMenu(Menu menu) {
     * getMenuInflater().inflate(R.menu.main_menu, menu);
     * return true;
     * }
     */
    /*
     * @Override
     * public boolean onOptionsItemSelected(MenuItem item) {
     * if (item.getItemId() == R.id.action_navigation) {
     * Intent intent = new Intent(this, PokemonMainActivity.class);
     * startActivity(intent);
     * return true;
     * }
     * return super.onOptionsItemSelected(item);
     * }
     */
}