package com.android.excuses404.fragments;

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

import com.android.excuses404.R;
import com.android.excuses404.activities.AuthActivity;
import com.android.excuses404.data.api.UserApiService;
import com.android.excuses404.data.api.model.ResendOtpRequest;
import com.android.excuses404.data.api.model.ResendOtpResponse;
import com.android.excuses404.data.api.model.UserRegisterRequest;
import com.android.excuses404.data.api.model.UserRegisterResponse;
import com.android.excuses404.utils.ErrorDialog;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private EditText etUser, etPassword, etFirstName, etLastName, etEmail, etPhone;
    private Button btnRegister;
    private TextView tvGoLogin;

    @Inject
    UserApiService userApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        etUser = view.findViewById(R.id.etUser);
        etPassword = view.findViewById(R.id.etPassword);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        btnRegister = view.findViewById(R.id.btnRegister);
        tvGoLogin = view.findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etUser.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            // Validar que todos los campos estén completos
            if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                    lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            UserRegisterRequest registerRequest = new UserRegisterRequest(
                    username, password, firstName, lastName, phone, email);

            Call<UserRegisterResponse> call = userApiService.register(registerRequest);
            call.enqueue(new Callback<UserRegisterResponse>() {
                @Override
                public void onResponse(Call<UserRegisterResponse> call, Response<UserRegisterResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserRegisterResponse registerResponse = response.body();
                        Log.d("RegisterFragment", "Backend response: " + registerResponse.getCode() + " - "
                                + registerResponse.getDescription());

                        Toast.makeText(getActivity(),
                                "Registro exitoso! Generando código de verificación...",
                                Toast.LENGTH_SHORT).show();

                        generateOtpForRegistration(username);

                    } else {
                        ErrorDialog.showRegistrationError(getActivity(),
                                "No se pudo completar el registro. Código de error: " + response.code());
                        Log.e("RegisterFragment", "Error en registro: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<UserRegisterResponse> call, Throwable t) {
                    ErrorDialog.showConnectionError(getActivity(), () -> {
                        btnRegister.performClick();
                    });
                    Log.e("RegisterFragment", "onFailure registro", t);
                }
            });
        });

        tvGoLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).loadFragment(new LoginFragment());
            }
        });

        return view;
    }

    private void generateOtpForRegistration(String username) {
        ResendOtpRequest otpRequest = new ResendOtpRequest(username, ResendOtpRequest.TYPE_REGISTRATION);
        Call<ResendOtpResponse> call = userApiService.resendOtp(otpRequest);

        call.enqueue(new Callback<ResendOtpResponse>() {
            @Override
            public void onResponse(Call<ResendOtpResponse> call, Response<ResendOtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResendOtpResponse otpResponse = response.body();
                    Log.d("RegisterFragment", "OTP generado exitosamente: " + otpResponse.getCode() + " - "
                            + otpResponse.getDescription());

                    Toast.makeText(getActivity(),
                            "Código de verificación enviado! Revisa tu email",
                            Toast.LENGTH_LONG).show();

                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).loadFragment(OtpFragment.newInstance(username));
                    }

                } else {
                    Log.e("RegisterFragment", "Error al generar OTP: " + response.code());
                    ErrorDialog.showGenericError(getActivity(),
                            "Registro completado, pero no se pudo enviar el código de verificación. " +
                                    "Intenta solicitar un nuevo código desde la pantalla de verificación.");

                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).loadFragment(OtpFragment.newInstance(username));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResendOtpResponse> call, Throwable t) {
                Log.e("RegisterFragment", "Error de conexión al generar OTP", t);
                ErrorDialog.showConnectionError(getActivity(), () -> {
                    generateOtpForRegistration(username);
                });
            }
        });
    }
}
