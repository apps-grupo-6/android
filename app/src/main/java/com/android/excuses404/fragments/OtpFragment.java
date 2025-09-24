package com.android.excuses404.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.android.excuses404.activities.HomeActivity;
import com.android.excuses404.data.api.UserApiService;
import com.android.excuses404.data.api.model.ConfirmAccountRequest;
import com.android.excuses404.data.api.model.ConfirmAccountResponse;
import com.android.excuses404.data.api.model.OtpVerificationRequest;
import com.android.excuses404.data.api.model.OtpVerificationResponse;
import com.android.excuses404.data.api.model.ResendOtpRequest;
import com.android.excuses404.data.api.model.ResendOtpResponse;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.android.excuses404.utils.Constants.IS_USER_LOGGED_IN;
import static com.android.excuses404.utils.Constants.USER_DATA;

import com.android.excuses404.utils.ErrorDialog;

@AndroidEntryPoint
public class OtpFragment extends Fragment {

    private static final String ARG_USERNAME = "username";
    private static final String ARG_IS_PASSWORD_RESET = "is_password_reset";
    private String username;
    private boolean isPasswordReset = false;

    private EditText etOtpCode;
    private Button btnVerifyOtp;
    private TextView tvResendCode, tvGoBack;

    @Inject
    UserApiService userApiService;

    private CountDownTimer resendTimer;
    private boolean canResend = true;
    private static final long RESEND_COOLDOWN_MILLIS = 3 * 60 * 1000; // 3 minutos

    public static OtpFragment newInstance(String username) {
        OtpFragment fragment = new OtpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putBoolean(ARG_IS_PASSWORD_RESET, false);
        fragment.setArguments(args);
        return fragment;
    }

    public static OtpFragment newInstanceForPasswordReset(String username) {
        OtpFragment fragment = new OtpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putBoolean(ARG_IS_PASSWORD_RESET, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
            isPasswordReset = getArguments().getBoolean(ARG_IS_PASSWORD_RESET, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp_fragment, container, false);

        etOtpCode = view.findViewById(R.id.etOtpCode);
        btnVerifyOtp = view.findViewById(R.id.btnVerifyOtp);
        tvResendCode = view.findViewById(R.id.tvResendCode);
        tvGoBack = view.findViewById(R.id.tvGoBack);

        btnVerifyOtp.setOnClickListener(v -> {
            String otpCode = etOtpCode.getText().toString().trim();

            if (otpCode.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor ingresa el código de verificación", Toast.LENGTH_SHORT).show();
                return;
            }

            if (otpCode.length() != 6) {
                Toast.makeText(getActivity(), "El código debe tener 6 dígitos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPasswordReset) {
                verifyOtpForPasswordReset(otpCode);
            } else {
                confirmAccountForRegistration(otpCode);
            }
        });

        tvResendCode.setOnClickListener(v -> {
            if (canResend) {
                resendOtpCode();
            }
        });

        tvGoBack.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                if (isPasswordReset) {
                    ((AuthActivity) getActivity()).loadFragment(new ForgotPasswordFragment());
                } else {
                    ((AuthActivity) getActivity()).loadFragment(new RegisterFragment());
                }
            }
        });

        return view;
    }

    private void confirmAccountForRegistration(String otpCode) {
        ConfirmAccountRequest confirmRequest = new ConfirmAccountRequest(username, otpCode);
        Call<ConfirmAccountResponse> call = userApiService.confirmAccount(confirmRequest);

        Toast.makeText(getActivity(), "Verificando código...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<ConfirmAccountResponse>() {
            @Override
            public void onResponse(Call<ConfirmAccountResponse> call, Response<ConfirmAccountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ConfirmAccountResponse confirmResponse = response.body();
                    Log.d("OtpFragment", "ConfirmAccount response: " + confirmResponse.getCode() + " - "
                            + confirmResponse.getDescription());

                    // Marcar usuario como logueado y ir al Home
                    SharedPreferences prefs = getActivity().getSharedPreferences(USER_DATA, MODE_PRIVATE);
                    prefs.edit().putBoolean(IS_USER_LOGGED_IN, true).apply();

                    Toast.makeText(getActivity(), "¡Cuenta confirmada exitosamente! Bienvenido", Toast.LENGTH_LONG)
                            .show();

                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                } else {
                    ErrorDialog.showOtpError(getActivity(), () -> {
                        etOtpCode.setText("");
                        etOtpCode.requestFocus();
                    });
                    Log.e("OtpFragment", "Error en confirmación de cuenta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ConfirmAccountResponse> call, Throwable t) {
                ErrorDialog.showConnectionError(getActivity(), () -> {
                    confirmAccountForRegistration(otpCode);
                });
                Log.e("OtpFragment", "onFailure confirmación de cuenta", t);
            }
        });
    }

    private void verifyOtpForPasswordReset(String otpCode) {
        OtpVerificationRequest otpRequest = new OtpVerificationRequest(username, otpCode);
        Call<OtpVerificationResponse> call = userApiService.verifyOtp(otpRequest);

        Toast.makeText(getActivity(), "Verificando código...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<OtpVerificationResponse>() {
            @Override
            public void onResponse(Call<OtpVerificationResponse> call, Response<OtpVerificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OtpVerificationResponse otpResponse = response.body();
                    Log.d("OtpFragment",
                            "Verify response: " + otpResponse.getCode() + " - " + otpResponse.getDescription());
                    Log.d("OtpFragment", "Data object: " + (otpResponse.getData() != null ? "presente" : "null"));

                    if (otpResponse.getData() != null) {
                        Log.d("OtpFragment", "Data type: " + otpResponse.getData().getType());
                        Log.d("OtpFragment", "Data message: " + otpResponse.getData().getMessage());
                    }

                    Toast.makeText(getActivity(), "¡Código verificado! Ahora puedes cambiar tu contraseña",
                            Toast.LENGTH_LONG).show();

                    String resetToken = otpResponse.getReset_token();
                    Log.d("OtpFragment", "Reset token recibido: '" + resetToken + "'");

                    if (resetToken != null && !resetToken.isEmpty() && getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity())
                                .loadFragment(ChangePasswordFragment.newInstance(username, resetToken));
                        Log.d("OtpFragment", "Navegando a cambio de contraseña con reset token: " + resetToken);
                    } else {
                        Log.e("OtpFragment",
                                "Reset token es null o vacío, no se puede continuar con el cambio de contraseña");
                        ErrorDialog.showGenericError(getActivity(),
                                "Error al procesar la verificación. Intenta nuevamente.");
                    }

                } else {
                    ErrorDialog.showOtpError(getActivity(), () -> {
                        etOtpCode.setText("");
                        etOtpCode.requestFocus();
                    });
                    Log.e("OtpFragment", "Error en verificación para reset: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OtpVerificationResponse> call, Throwable t) {
                ErrorDialog.showConnectionError(getActivity(), () -> {
                    verifyOtpForPasswordReset(otpCode);
                });
                Log.e("OtpFragment", "onFailure verificación para reset", t);
            }
        });
    }

    private void resendOtpCode() {
        String type = isPasswordReset ? ResendOtpRequest.TYPE_RECOVERY : ResendOtpRequest.TYPE_REGISTRATION;
        ResendOtpRequest resendRequest = new ResendOtpRequest(username, type);
        Call<ResendOtpResponse> call = userApiService.resendOtp(resendRequest);

        Toast.makeText(getActivity(), "Reenviando código...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<ResendOtpResponse>() {
            @Override
            public void onResponse(Call<ResendOtpResponse> call, Response<ResendOtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResendOtpResponse resendResponse = response.body();
                    Log.d("OtpFragment",
                            "Resend response: " + resendResponse.getCode() + " - " + resendResponse.getDescription());

                    Toast.makeText(getActivity(), "Código reenviado exitosamente", Toast.LENGTH_LONG).show();
                    startResendCooldown();

                } else {
                    ErrorDialog.showGenericError(getActivity(), "No se pudo reenviar el código. Intenta nuevamente.");
                    Log.e("OtpFragment", "Error al reenviar código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResendOtpResponse> call, Throwable t) {
                ErrorDialog.showConnectionError(getActivity(), () -> {
                    resendOtpCode();
                });
                Log.e("OtpFragment", "onFailure reenviar código", t);
            }
        });
    }

    private void startResendCooldown() {
        canResend = false;

        if (resendTimer != null) {
            resendTimer.cancel();
        }

        resendTimer = new CountDownTimer(RESEND_COOLDOWN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;

                tvResendCode.setText(String.format("Reenviar código en %d:%02d", minutes, seconds));
                tvResendCode.setTextColor(getResources().getColor(android.R.color.darker_gray));
                tvResendCode.setEnabled(false);
            }

            @Override
            public void onFinish() {
                canResend = true;
                tvResendCode.setText("¿No recibiste el código? Reenviar");
                tvResendCode.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                tvResendCode.setEnabled(true);
            }
        };

        resendTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }
}