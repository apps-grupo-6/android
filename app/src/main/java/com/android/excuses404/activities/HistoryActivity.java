package com.android.excuses404.activities;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.android.excuses404.R;
import com.android.excuses404.adapters.HistoryAdapter;
import com.android.excuses404.core.repository.TokenRepository;
import com.android.excuses404.data.api.HistoryApiService;
import com.android.excuses404.data.api.model.HistoryResponse;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";

    @Inject
    HistoryApiService historyApiService;

    @Inject
    TokenRepository tokenRepository;

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private LinearLayout errorState;
    private TextView tvErrorMessage;
    private Button btnRetry;

    // Filter components
    private TextView tvDateFrom;
    private TextView tvDateTo;
    private Button btnApplyFilters;
    private Button btnClearFilters;

    // Filter state
    private String selectedDateFrom;
    private String selectedDateTo;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initViews();
        setupRecyclerView();
        loadHistoryData();
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
            tvTitle.setText("Tus Clases Registradas");
        }

        // Inicializar vistas
        recyclerView = findViewById(R.id.recycler_view_history);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        errorState = findViewById(R.id.error_state);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        btnRetry = findViewById(R.id.btn_retry);

        tvDateFrom = findViewById(R.id.tv_date_from);
        tvDateTo = findViewById(R.id.tv_date_to);
        btnApplyFilters = findViewById(R.id.btn_apply_filters);
        btnClearFilters = findViewById(R.id.btn_clear_filters);

        btnRetry.setOnClickListener(v -> loadHistoryData());

        setupFilters();
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyAdapter);
    }

    private void setupFilters() {
        tvDateFrom.setOnClickListener(v -> showDatePicker(true));

        tvDateTo.setOnClickListener(v -> showDatePicker(false));

        btnApplyFilters.setOnClickListener(v -> applyFilters());

        btnClearFilters.setOnClickListener(v -> clearFilters());
    }

    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    String formattedDate = dateFormat.format(selectedDate.getTime());
                    String displayDate = displayDateFormat.format(selectedDate.getTime());

                    if (isFromDate) {
                        selectedDateFrom = formattedDate;
                        tvDateFrom.setText(displayDate);
                        Log.d(TAG, "Fecha DESDE seleccionada: " + formattedDate + " (mostrar: " + displayDate + ")");
                    } else {
                        selectedDateTo = formattedDate;
                        tvDateTo.setText(displayDate);
                        Log.d(TAG, "Fecha HASTA seleccionada: " + formattedDate + " (mostrar: " + displayDate + ")");
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void applyFilters() {
        Log.d(TAG, "Aplicando filtros - Desde: " + selectedDateFrom + ", Hasta: " + selectedDateTo);

        if (historyAdapter != null) {
            historyAdapter.setDateFilter(selectedDateFrom, selectedDateTo);

            String message = "Filtros aplicados";
            if (selectedDateFrom != null && selectedDateTo != null) {
                message += " (desde " + displayDateFormat.format(parseDate(selectedDateFrom)) +
                        " hasta " + displayDateFormat.format(parseDate(selectedDateTo)) + ")";
            } else if (selectedDateFrom != null) {
                message += " (desde " + displayDateFormat.format(parseDate(selectedDateFrom)) + ")";
            } else if (selectedDateTo != null) {
                message += " (hasta " + displayDateFormat.format(parseDate(selectedDateTo)) + ")";
            } else {
                message = "No hay filtros seleccionados";
            }

            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "historyAdapter es null");
        }
    }

    private void clearFilters() {
        selectedDateFrom = null;
        selectedDateTo = null;
        tvDateFrom.setText("Seleccionar fecha");
        tvDateTo.setText("Seleccionar fecha");

        if (historyAdapter != null) {
            historyAdapter.clearFilters();
            android.widget.Toast.makeText(this, "Filtros limpiados", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private java.util.Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            return new java.util.Date();
        }
    }

    private void loadHistoryData() {
        showLoading();

        if (!tokenRepository.hasToken()) {
            Log.e(TAG, "No hay token disponible");
            showError("No hay sesión activa");
            return;
        }

        String token = tokenRepository.getToken();
        String authHeader = "Bearer " + token;

        Log.d(TAG, "Cargando historial de clases...");
        Log.d(TAG, "Token: " + (token != null ? "SÍ (longitud: " + token.length() + ")" : "NO"));
        Log.d(TAG, "Auth Header: " + authHeader);
        Log.d(TAG, "URL completa será: http://10.0.2.2:5000/api/classes/history");

        Call<HistoryResponse> call = historyApiService.getClassesHistory(authHeader);
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    HistoryResponse historyResponse = response.body();
                    Log.d(TAG, "Respuesta del servidor: " + historyResponse.getCode() + " - "
                            + historyResponse.getDescription());
                    Log.d(TAG, "Data es null: " + (historyResponse.getData() == null));
                    if (historyResponse.getData() != null) {
                        Log.d(TAG, "Cantidad de elementos en data: " + historyResponse.getData().size());
                    }

                    if (historyResponse.isSuccess()) {
                        if (historyResponse.getData() != null && !historyResponse.getData().isEmpty()) {
                            Log.d(TAG,
                                    "Historial cargado exitosamente: " + historyResponse.getData().size() + " clases");
                            showHistory(historyResponse.getData());
                        } else {
                            Log.d(TAG, "No hay clases en el historial");
                            showEmpty();
                        }
                    } else {
                        Log.e(TAG, "Error en la respuesta: " + historyResponse.getDescription());
                        showError(historyResponse.getDescription());
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
                    showError("Error al cargar el historial: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
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

    private void showHistory(java.util.List<com.android.excuses404.data.api.model.HistoryClass> historyClasses) {
        historyAdapter.setHistoryClasses(historyClasses);
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
        tvErrorMessage.setText(message);
        errorState.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
    }
}
