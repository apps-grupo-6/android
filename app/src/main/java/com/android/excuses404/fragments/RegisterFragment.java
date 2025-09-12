package com.android.excuses404.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.excuses404.R;
import com.android.excuses404.activities.AuthActivity;

public class RegisterFragment extends Fragment {

    private EditText etUser, etPassword, etEmail, etPhone;
    private Button btnRegister;
    private TextView tvGoLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        etUser = view.findViewById(R.id.etUser);
        etPassword = view.findViewById(R.id.etPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        btnRegister = view.findViewById(R.id.btnRegister);
        tvGoLogin = view.findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> {
            String user = etUser.getText().toString();
            String pass = etPassword.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();

            if (!user.isEmpty() && !pass.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
                Log.d("RegisterFragment", "yes");
            }
        });

        tvGoLogin.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).loadFragment(new LoginFragment());
            }
        });

        return view;
    }
}
