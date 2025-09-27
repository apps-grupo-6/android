package com.android.excuses404.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.excuses404.activities.AuthActivity;
import com.android.excuses404.activities.HomeActivity;
import com.android.excuses404.data.api.UserApiService;
import com.android.excuses404.data.api.model.UserLoginRequest;
import com.android.excuses404.data.api.model.UserLoginResponse;
import com.android.excuses404.R;
import com.google.gson.Gson;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.android.excuses404.utils.Constants.IS_USER_LOGGED_IN;
import static com.android.excuses404.utils.Constants.USER_DATA;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private static final String USER_ID = "";

    private EditText etUser, etPassword;
    private Button btnLogin;
    private Button btnDirectAccess; // Nuevo botón de acceso directo
    private TextView tvGoRegister;

    @Inject
    UserApiService userApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        etUser = view.findViewById(R.id.etUser);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnDirectAccess = view.findViewById(R.id.btnDirectAccess); // Inicializar el botón
        tvGoRegister = view.findViewById(R.id.tvGoRegister);

        // Configurar el botón de acceso directo
        btnDirectAccess.setOnClickListener(v -> {
            // Guardar un flag de sesión ficticia y un token JWT para desarrollo
            SharedPreferences prefs = getActivity().getSharedPreferences(USER_DATA, MODE_PRIVATE);
            prefs.edit()
                .putBoolean(IS_USER_LOGGED_IN, true)
                .putString("jwt_token", "token_ficticio_para_desarrollo_123456789")
                .apply();

            // Mostrar mensaje para el desarrollador
            Toast.makeText(getActivity(), "Accediendo sin autenticación (modo desarrollo)", Toast.LENGTH_SHORT).show();

            // Navegar directamente a HomeActivity
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
            // No hacemos finish() para poder volver al login si es necesario durante el desarrollo
        });

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor completa usuario y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            UserLoginRequest userRequest = new UserLoginRequest(user, pass);
            Call<UserLoginResponse> call = userApiService.login(userRequest);
            call.enqueue(new Callback<UserLoginResponse>() {
                @Override
                public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Toast.makeText(getActivity(), "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                        Log.e("LoginFragment", "Error en login: " + response.code());
                        return;
                    }

                    UserLoginResponse loginResponse = response.body();
                    String code = loginResponse.getCode();
                    Log.d("LoginFragment", "Backend response: "+ code + " - "+ loginResponse.getDescription());

                    if (code.equals("0200")) { // if everything is ok
                        SharedPreferences prefs = getActivity().getSharedPreferences(USER_DATA, MODE_PRIVATE);
                        prefs.edit().putBoolean(IS_USER_LOGGED_IN, true).apply();

                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                        getActivity().finish(); // keep it. Without it, user can go back to the login panel
                    } else {
                        Toast.makeText(getActivity(), "Error en la respuesta del servidor", Toast.LENGTH_LONG).show();
                        Log.e("LoginFragment", "Error en login con código: " + code);
                    }
                }

                @Override
                public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("LoginFragment", "onFailure login", t);
                }
            });
        });

        tvGoRegister.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).loadFragment(new RegisterFragment());
            }
        });

        return view;
    }
}
