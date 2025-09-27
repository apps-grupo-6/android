package com.android.excuses404.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.excuses404.R;
import com.android.excuses404.core.repository.TokenRepository;
import com.android.excuses404.data.api.model.GetUserResponse;
import com.android.excuses404.data.api.model.UpdateUserRequest;
import com.android.excuses404.data.repository.UserServiceCallBack;
import com.android.excuses404.services.UserService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileActivity extends AppCompatActivity {

    @Inject
    TokenRepository tokenRepository;

    @Inject
    UserService userService;

    private ImageView btnBack;
    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private MaterialButton btnSaveProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupListeners();
        loadUserData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void loadUserData() {
        // Debug logs
        boolean hasToken = tokenRepository.hasToken();
        String token = tokenRepository.getToken();
        boolean isLoggedIn = tokenRepository.isLoggedIn();

        if (!hasToken) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userService.getUser(token, new UserServiceCallBack() {
            @Override
            public void onSuccess(Object response) {
                runOnUiThread(() -> {
                    if (response instanceof GetUserResponse) {
                        GetUserResponse getUserResponse = (GetUserResponse) response;
                        if (getUserResponse.getUser() != null) {
                            GetUserResponse.UserData userData = getUserResponse.getUser();

                            // Llenar los campos con los datos del servidor
                            setFieldText(etFirstName, userData.getFirst_name());
                            setFieldText(etLastName, userData.getLast_name());
                            setFieldText(etEmail, userData.getContact_email());
                            setFieldText(etPhone, userData.getTelephone());
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Error al cargar datos: " + error, Toast.LENGTH_LONG).show();
                    clearAllFields();
                });
            }
        });
    }

    private void saveProfile() {
        String firstName = getTextFromField(etFirstName);
        String lastName = getTextFromField(etLastName);
        String email = getTextFromField(etEmail);
        String phone = getTextFromField(etPhone);

        if (!validateField(etFirstName, firstName, "El nombre es requerido"))
            return;
        if (!validateField(etLastName, lastName, "El apellido es requerido"))
            return;
        if (!validateField(etEmail, email, "El email es requerido"))
            return;
        if (!validateEmail(etEmail, email))
            return;

        if (!tokenRepository.hasToken()) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSaveProfile.setEnabled(false);
        btnSaveProfile.setText("Guardando...");

        UpdateUserRequest updateRequest = new UpdateUserRequest(firstName, lastName, phone, email);
        String token = tokenRepository.getToken();

        userService.updateUser(token, updateRequest, new UserServiceCallBack() {
            @Override
            public void onSuccess(Object response) {
                runOnUiThread(() -> {
                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Guardar Cambios");
                    Toast.makeText(ProfileActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Guardar Cambios");
                    Toast.makeText(ProfileActivity.this, "Error al actualizar perfil: " + error, Toast.LENGTH_LONG)
                            .show();
                });
            }
        });
    }

    private boolean validateField(TextInputEditText field, String value, String errorMessage) {
        if (value.isEmpty()) {
            field.setError(errorMessage);
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateEmail(TextInputEditText field, String email) {
        if (!isValidEmail(email)) {
            field.setError("Formato de email inválido");
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String getTextFromField(TextInputEditText field) {
        return field.getText().toString().trim();
    }

    private void setFieldText(TextInputEditText field, String value) {
        field.setText(value != null ? value : "");
    }

    private void clearAllFields() {
        etFirstName.setText("");
        etLastName.setText("");
        etEmail.setText("");
        etPhone.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
