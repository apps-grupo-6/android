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
import com.android.excuses404.data.api.model.ResetPasswordRequest;
import com.android.excuses404.data.api.model.ResetPasswordResponse;
import com.android.excuses404.utils.ErrorDialog;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ChangePasswordFragment extends Fragment {

    private static final String ARG_USERNAME = "username";
    private static final String ARG_RESET_TOKEN = "reset_token";
    private String username;
    private String resetToken;

    private EditText etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private TextView tvBackToLogin;

    @Inject
    UserApiService userApiService;

    public static ChangePasswordFragment newInstance(String username, String resetToken) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_RESET_TOKEN, resetToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
            resetToken = getArguments().getString(ARG_RESET_TOKEN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_fragment, container, false);

        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        tvBackToLogin = view.findViewById(R.id.tvBackToLogin);

        btnChangePassword.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor ingresa la nueva contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(getActivity(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            if (confirmPassword.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor confirma la contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            changePassword(newPassword);
        });

        tvBackToLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).loadFragment(new LoginFragment());
            }
        });

        return view;
    }

    private void changePassword(String newPassword) {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest(resetToken, newPassword);
        Call<ResetPasswordResponse> call = userApiService.resetPassword(resetRequest);

        Toast.makeText(getActivity(), "Cambiando contraseña...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResetPasswordResponse resetResponse = response.body();
                    Log.d("ChangePasswordFragment", "Reset password response: " + resetResponse.getCode() + " - "
                            + resetResponse.getDescription());

                    Toast.makeText(getActivity(), "¡Contraseña cambiada exitosamente! Ya puedes iniciar sesión",
                            Toast.LENGTH_LONG).show();

                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).loadFragment(new LoginFragment());
                    }

                } else {
                    ErrorDialog.showGenericError(getActivity(),
                            "No se pudo cambiar la contraseña. Intenta nuevamente.");
                    Log.e("ChangePasswordFragment", "Error al cambiar contraseña: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                ErrorDialog.showConnectionError(getActivity(), () -> {
                    changePassword(newPassword);
                });
                Log.e("ChangePasswordFragment", "onFailure cambiar contraseña", t);
            }
        });
    }
}
