package com.android.excuses404.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.excuses404.R;
import com.android.excuses404.adapters.ClassesAdapter;
import com.android.excuses404.adapters.DisciplineAdapter;
import com.android.excuses404.data.api.ApiClient;
import com.android.excuses404.data.api.MockApiClient;
import com.android.excuses404.data.api.ClassesApiService;
import com.android.excuses404.data.api.model.ClassesResponse;
import com.android.excuses404.models.Class;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements ClassesAdapter.OnClassClickListener, DisciplineAdapter.OnDisciplineClickListener {

    private static final String TAG = "HomeActivity";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";

    private ClassesApiService classesApiService;
    private RecyclerView recyclerView;
    private ClassesAdapter classesAdapter;
    private DisciplineAdapter disciplineAdapter;
    private ProgressBar progressBar;
    private TextView tvErrorMessage;
    private TextView tvTitle;
    private View btnBack;

    private java.util.List<Class> allClasses = new java.util.ArrayList<>();
    private boolean showingDisciplines = true;
    private String currentDiscipline = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Usar MockApiClient para testing sin servidor
        classesApiService = MockApiClient.getApi();
        // classesApiService = ApiClient.getApi(); // producción

        initViews();
        setupRecyclerView();
        loadClassesCatalog();
    }

    private void initViews() {
        try {
            recyclerView = findViewById(R.id.rv_clases);
            progressBar = findViewById(R.id.pb_loading);
            tvErrorMessage = findViewById(R.id.tv_error);
            tvTitle = findViewById(R.id.tv_title);
            btnBack = findViewById(R.id.btn_back);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> returnToDisciplines());
            }
            // Verificar que todos los elementos fueron encontrados
            if (recyclerView == null || progressBar == null || tvErrorMessage == null || tvTitle == null || btnBack == null) {
                Log.e(TAG, "Error: No se pudieron encontrar todos los elementos del layout");
                Toast.makeText(this, "Error de interfaz: elementos faltantes", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en initViews: " + e.getMessage(), e);
            Toast.makeText(this, "Error al inicializar la interfaz", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupRecyclerView() {
        classesAdapter = new ClassesAdapter();
        classesAdapter.setOnClassClickListener(this);
        disciplineAdapter = new DisciplineAdapter();
        disciplineAdapter.setOnDisciplineClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(disciplineAdapter); // inicial: disciplinas
    }

    private void showDisciplines() {
        showingDisciplines = true;
        currentDiscipline = null;
        if (btnBack != null) btnBack.setVisibility(View.GONE);
        if (tvTitle != null) tvTitle.setText("Disciplinas");
        java.util.Set<String> set = new java.util.LinkedHashSet<>();
        for (Class c : allClasses) {
            String d = c.getDisciplineName();
            if (d == null || d.trim().isEmpty()) d = "Sin disciplina";
            set.add(d);
        }
        disciplineAdapter.setDisciplines(new java.util.ArrayList<>(set));
        recyclerView.setAdapter(disciplineAdapter);
    }

    private void returnToDisciplines() {
        showDisciplines();
    }

    private void loadClassesCatalog() {
        showLoading();

        String token = getJwtToken();
        if (token == null || token.isEmpty()) {
            showError("Token de autenticación no encontrado. Por favor, inicia sesión nuevamente.");
            return;
        }

        String authHeader = "Bearer " + token;

        try {
            Call<ClassesResponse> call = classesApiService.getAllClasses(authHeader);
            call.enqueue(new Callback<ClassesResponse>() {
                @Override
                public void onResponse(Call<ClassesResponse> call, Response<ClassesResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ClassesResponse classesResponse = response.body();
                        if (classesResponse.isSuccess()) {
                            if (classesResponse.getClasses() != null && !classesResponse.getClasses().isEmpty()) {
                                allClasses = classesResponse.getClasses();
                                showDisciplines();
                                recyclerView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Log.d(TAG, "Cargadas " + allClasses.size() + " clases");
                            } else {
                                showEmpty("No se encontraron clases disponibles");
                            }
                        } else {
                            showRetryableError("Error del servidor: " + classesResponse.getMessage());
                        }
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error al leer errorBody", e);
                        }
                        String errorMsg = "Error en la respuesta del servidor. Código: " + response.code() + ". Cuerpo: " + errorBody;
                        Log.e(TAG, errorMsg);
                        showRetryableError(errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<ClassesResponse> call, Throwable t) {
                    String message = "";
                    if (t.getMessage() != null && t.getMessage().contains("Unable to resolve host")) {
                        message = "No se pudo encontrar el servidor 192.168.0.15.\n\nVerifica que el servidor esté en ejecución y que el dispositivo esté conectado a la misma red.";
                    } else if (t.getMessage() != null && t.getMessage().contains("timeout")) {
                        message = "El servidor no respondió a tiempo.\n\nVerifica que el servidor no esté sobrecargado.";
                    } else if (t.getMessage() != null && t.getMessage().contains("Connection refused")) {
                        message = "Conexión rechazada por el servidor.\n\nVerifica que el servidor esté en ejecución en el puerto 5000.";
                    } else {
                        message = "Error de conexión: " + t.getMessage();
                    }

                    showRetryableError(message);
                    Log.e(TAG, "Network error", t);
                }
            });
        } catch (Exception e) {
            showRetryableError("Error al preparar la petición: " + e.getMessage());
            Log.e(TAG, "Error al preparar la petición", e);
        }
    }

    // Método para mostrar error que permite reintentar al tocar
    private void showRetryableError(String message) {
        tvErrorMessage.setText(message + "\n\n(Toca para reintentar)");
        tvErrorMessage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        // Agregar click listener para reintentar
        tvErrorMessage.setOnClickListener(v -> {
            Toast.makeText(this, "Reintentando conexión...", Toast.LENGTH_SHORT).show();
            loadClassesCatalog();
        });
    }

    private void showEmpty(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        tvErrorMessage.setOnClickListener(null);
    }

    private String getJwtToken() {
        // Primero intenta obtener el token de las preferencias de UserPrefs (original)
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(KEY_JWT_TOKEN, null);

        // Si no lo encuentra, intenta con USER_DATA (usado en LoginFragment)
        if (token == null) {
            prefs = getSharedPreferences("userData", MODE_PRIVATE);  // USER_DATA constant
            token = prefs.getString(KEY_JWT_TOKEN, null);
        }

        // Si aún es null, proporciona un token ficticio para desarrollo
        if (token == null) {
            // Guardar un token ficticio para futuros usos
            prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            token = "token_ficticio_para_desarrollo_123456789";
            prefs.edit().putString(KEY_JWT_TOKEN, token).apply();
            Log.d(TAG, "Usando token ficticio para desarrollo");
        }

        return token;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showCatalog(java.util.List<Class> classes) {
        classesAdapter.setClasses(classes);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClassClick(Class classItem) {
        if (classItem == null) return;
        Toast.makeText(this,
                "Clase: " + classItem.getDisciplineName() +
                        "\nProfesor: " + classItem.getFullProfessorName() +
                        "\nGimnasio: " + classItem.getFullGymInfo() +
                        "\nFecha: " + classItem.getScheduledAt() +
                        "\nParticipantes máx: " + classItem.getMaxParticipants(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisciplineClick(String disciplineName) {
        // Filtrar clases para la disciplina
        java.util.ArrayList<Class> filtered = new java.util.ArrayList<>();
        for (Class c : allClasses) {
            String d = c.getDisciplineName();
            if (d == null || d.trim().isEmpty()) d = "Sin disciplina";
            if (d.equals(disciplineName)) filtered.add(c);
        }
        Intent intent = new Intent(this, DisciplineClassesActivity.class);
        intent.putExtra(DisciplineClassesActivity.EXTRA_DISCIPLINE_NAME, disciplineName);
        intent.putParcelableArrayListExtra(DisciplineClassesActivity.EXTRA_CLASSES_LIST, filtered);
        startActivity(intent);
    }
}
