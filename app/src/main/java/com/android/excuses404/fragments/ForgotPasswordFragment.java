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
import com.android.excuses404.utils.ErrorDialog;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {

    private EditText etUsername, etEmail;
    private Button btnContinue;
    private TextView tvBackToLogin;

    @Inject
    UserApiService userApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_password_fragment, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        btnContinue = view.findViewById(R.id.btnContinue);
        tvBackToLogin = view.findViewById(R.id.tvBackToLogin);

        btnContinue.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor ingresa tu usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor ingresa tu email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getActivity(), "Por favor ingresa un email válido", Toast.LENGTH_SHORT).show();
                return;
            }

            sendOtpForPasswordReset(username);
        });

        tvBackToLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).loadFragment(new LoginFragment());
            }
        });

        return view;
    }

    private void sendOtpForPasswordReset(String username) {
        ResendOtpRequest resendRequest = new ResendOtpRequest(username, ResendOtpRequest.TYPE_RECOVERY);
        Call<ResendOtpResponse> call = userApiService.resendOtp(resendRequest);

        Toast.makeText(getActivity(), "Enviando código de verificación...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<ResendOtpResponse>() {
            @Override
            public void onResponse(Call<ResendOtpResponse> call, Response<ResendOtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResendOtpResponse otpResponse = response.body();
                    Log.d("ForgotPasswordFragment",
                            "OTP sent response: " + otpResponse.getCode() + " - " + otpResponse.getDescription());

                    Toast.makeText(getActivity(), "Código enviado a tu email", Toast.LENGTH_LONG).show();

                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).loadFragment(OtpFragment.newInstanceForPasswordReset(username));
                    }

                } else {
                    ErrorDialog.showGenericError(getActivity(),
                            "No se pudo enviar el código. Verifica que el usuario sea correcto.");
                    Log.e("ForgotPasswordFragment", "Error al enviar OTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResendOtpResponse> call, Throwable t) {
                ErrorDialog.showConnectionError(getActivity(), () -> {
                    // Reintentar envío
                    sendOtpForPasswordReset(username);
                });
                Log.e("ForgotPasswordFragment", "onFailure enviar OTP", t);
            }
        });
    }
}
