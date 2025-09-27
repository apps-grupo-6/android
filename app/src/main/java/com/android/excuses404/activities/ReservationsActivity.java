package com.android.excuses404.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.excuses404.R;
import com.android.excuses404.adapters.ReservationsAdapter;
import com.android.excuses404.core.repository.TokenRepository;
import com.android.excuses404.data.api.ReservationsApiService;
import com.android.excuses404.data.api.model.ReservationClass;
import com.android.excuses404.data.api.model.ReservationsResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ReservationsActivity extends AppCompatActivity {

    private static final String TAG = "ReservationsActivity";

    @Inject
    ReservationsApiService reservationsApiService;

    @Inject
    TokenRepository tokenRepository;

    private RecyclerView recyclerView;
    private ReservationsAdapter reservationsAdapter;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private LinearLayout errorState;
    private TextView tvErrorMessage;
    private Button btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        initViews();
        setupRecyclerView();
        loadReservationsData();
    }

    private void initViews() {
        // Configurar botón de volver
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        // Configurar título
        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText("Mis Reservas");
        }

        // Inicializar vistas
        recyclerView = findViewById(R.id.rv_reservations);
        progressBar = findViewById(R.id.pb_loading);
        emptyState = findViewById(R.id.ll_empty_state);
        errorState = findViewById(R.id.ll_error_state);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        btnRetry = findViewById(R.id.btn_retry);

        // Configurar botón de reintentar
        if (btnRetry != null) {
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadReservationsData();
                }
            });
        }
    }

    private void setupRecyclerView() {
        reservationsAdapter = new ReservationsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reservationsAdapter);
    }

    private void loadReservationsData() {
        showLoading();

        if (!tokenRepository.hasToken()) {
            Log.e(TAG, "No hay token disponible");
            showError("No hay sesión activa");
            return;
        }

        String token = tokenRepository.getToken();
        String authHeader = "Bearer " + token;

        Log.d(TAG, "Cargando reservas próximas...");
        Log.d(TAG, "Token: " + (token != null ? "SÍ (longitud: " + token.length() + ")" : "NO"));
        Log.d(TAG, "Auth Header: " + authHeader);
        Log.d(TAG, "URL completa será: http://10.0.2.2:5000/api/classes/upcoming");

        Call<ReservationsResponse> call = reservationsApiService.getUpcomingReservations(authHeader);
        call.enqueue(new Callback<ReservationsResponse>() {
            @Override
            public void onResponse(Call<ReservationsResponse> call, Response<ReservationsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReservationsResponse reservationsResponse = response.body();
                    Log.d(TAG, "Respuesta del servidor: " + reservationsResponse.getCode() + " - "
                            + reservationsResponse.getDescription());
                    Log.d(TAG, "Data es null: " + (reservationsResponse.getData() == null));
                    if (reservationsResponse.getData() != null) {
                        Log.d(TAG, "Cantidad de elementos en data: " + reservationsResponse.getData().size());
                    }

                    if (reservationsResponse.isSuccess()) {
                        if (reservationsResponse.getData() != null && !reservationsResponse.getData().isEmpty()) {
                            Log.d(TAG,
                                    "Reservas cargadas exitosamente: " + reservationsResponse.getData().size() + " reservas");
                            showReservations(reservationsResponse.getData());
                        } else {
                            Log.d(TAG, "No hay reservas próximas");
                            showEmpty();
                        }
                    } else {
                        Log.e(TAG, "Error en la respuesta: " + reservationsResponse.getDescription());
                        showError(reservationsResponse.getDescription());
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta del servidor: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "No se pudo leer el error body", e);
                        }
                    }
                    showError("Error al cargar las reservas: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ReservationsResponse> call, Throwable t) {
                Log.e(TAG, "Error de red al cargar reservas", t);
                showError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
    }

    private void showReservations(List<ReservationClass> reservations) {
        reservationsAdapter.setReservationClasses(reservations);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
    }

    private void showEmpty() {
        emptyState.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
    }

    private void showError(String message) {
        if (tvErrorMessage != null) {
            tvErrorMessage.setText(message);
        }
        errorState.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
    }
}
