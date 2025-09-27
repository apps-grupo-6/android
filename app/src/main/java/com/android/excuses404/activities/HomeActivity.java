package com.android.excuses404.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.excuses404.R;
import com.android.excuses404.core.repository.TokenRepository;
import com.android.excuses404.adapters.ClassesAdapter;
import com.android.excuses404.adapters.DisciplineAdapter;
import com.android.excuses404.data.api.model.DisciplineData;
import com.android.excuses404.data.api.model.DisciplinesResponse;
import com.android.excuses404.data.repository.LocationsRepository;
import com.android.excuses404.data.repository.LocationsServiceCallBack;
import com.android.excuses404.models.Class;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HomeActivity extends AppCompatActivity
        implements ClassesAdapter.OnClassClickListener, DisciplineAdapter.OnDisciplineClickListener {

    private static final String TAG = "HomeActivity";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";

    @Inject
    public LocationsRepository locationsRepository;
    private RecyclerView recyclerView;
    private ClassesAdapter classesAdapter;
    private DisciplineAdapter disciplineAdapter;
    private ProgressBar progressBar;
    private TextView tvErrorMessage;
    private TextView tvTitle;
    private Button btnBack;
    private Button btnBackDisciplines;
    private java.util.List<Class> allClasses = new java.util.ArrayList<>();
    private boolean showingDisciplines = true;
    private String currentDiscipline = null;

    @Inject
    TokenRepository tokenRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        setupRecyclerView();
        loadClassesCatalog();

        if (tokenRepository.hasToken()) {
            String token = tokenRepository.getToken();
            int userId = tokenRepository.getUserId();
        } else {
            Log.w(TAG, "No hay token almacenado");
        }

        setupProfileMenu();
    }

    private void setupProfileMenu() {
        ImageView profileIcon = findViewById(R.id.ivProfile);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileMenu(v);
            }
        });
    }

    private void showProfileMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_my_profile) {
                    handleMyProfile();
                    return true;
                } else if (itemId == R.id.menu_reservations) {
                    handleReservations();
                    return true;
                } else if (itemId == R.id.menu_history) {
                    handleHistory();
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    handleLogout();
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }

    private void handleMyProfile() {
        boolean hasToken = tokenRepository.hasToken();
        String token = tokenRepository.getToken();
        boolean isLoggedIn = tokenRepository.isLoggedIn();

        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void handleReservations() {
        Intent intent = new Intent(HomeActivity.this, ReservationsActivity.class);
        startActivity(intent);
    }

    private void handleHistory() {
        Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    private void handleLogout() {
        tokenRepository.clearAll();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        redirectToAuth();
    }

    private void redirectToAuth() {
        Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initViews() {
        try {
            recyclerView = findViewById(R.id.rv_clases);
            progressBar = findViewById(R.id.pb_loading);
            tvErrorMessage = findViewById(R.id.tv_error);
            tvTitle = findViewById(R.id.tv_title);
            btnBack = findViewById(R.id.btn_back);
            btnBackDisciplines = findViewById(R.id.btn_back_disciplines);

            if (btnBack != null) {
                btnBack.setOnClickListener(v -> returnToDisciplines());
            }

            if (btnBackDisciplines != null) {
                btnBackDisciplines.setOnClickListener(v -> returnToDisciplines());
            }

            // Verificar que todos los elementos fueron encontrados
            if (recyclerView == null || progressBar == null || tvErrorMessage == null || tvTitle == null
                    || btnBack == null || btnBackDisciplines == null) {
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
        if (btnBackDisciplines != null)
            btnBackDisciplines.setVisibility(View.GONE);
        if (tvTitle != null)
            tvTitle.setText("Nuestras Disciplinas");

        // Mostrar disciplinas en RecyclerView
        java.util.Set<String> set = new java.util.LinkedHashSet<>();
        for (Class c : allClasses) {
            String d = c.getDisciplineName();
            if (d == null || d.trim().isEmpty())
                d = "Sin disciplina";
            set.add(d);
        }
        disciplineAdapter.setDisciplines(new java.util.ArrayList<>(set));
        recyclerView.setAdapter(disciplineAdapter);
    }

    private void showDisciplinesView() {
        showingDisciplines = true;
        currentDiscipline = null;
        if (btnBack != null)
            btnBack.setVisibility(View.GONE);
        if (tvTitle != null)
            tvTitle.setText("Nuestras Disciplinas");

        // Mostrar disciplinas en RecyclerView
        java.util.Set<String> set = new java.util.LinkedHashSet<>();
        for (Class c : allClasses) {
            String d = c.getDisciplineName();
            if (d == null || d.trim().isEmpty())
                d = "Sin disciplina";
            set.add(d);
        }
        disciplineAdapter.setDisciplines(new java.util.ArrayList<>(set));
        recyclerView.setAdapter(disciplineAdapter);
    }

    private void showClassesForDiscipline(String disciplineName) {
        showingDisciplines = false;
        currentDiscipline = disciplineName;
        if (btnBack != null)
            btnBack.setVisibility(View.VISIBLE);
        if (tvTitle != null)
            tvTitle.setText("Clases de " + disciplineName);

        // Filtrar clases para la disciplina seleccionada
        java.util.ArrayList<Class> filtered = new java.util.ArrayList<>();
        for (Class c : allClasses) {
            String d = c.getDisciplineName();
            if (d == null || d.trim().isEmpty())
                d = "Sin disciplina";
            if (d.equals(disciplineName))
                filtered.add(c);
        }

        classesAdapter.setClasses(filtered);
        recyclerView.setAdapter(classesAdapter);
    }

    private void returnToDisciplines() {
        showDisciplinesView();
    }

    private void loadClassesCatalog() {
        showLoading();

        String token = getJwtToken();
        locationsRepository.getAllLocations(token, new LocationsServiceCallBack() {
            @Override
            public void onSuccess(DisciplinesResponse response) {
                allClasses = mapResponseToClassList(response);
                if (allClasses.isEmpty()) {
                    showEmpty("No se encontraron disciplinas");
                } else {
                    showDisciplines();
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable error) {
                showRetryableError("Error al obtener disciplinas: " + error.getMessage());
            }
        });
    }

    private List<Class> mapResponseToClassList(DisciplinesResponse resp) {
        List<Class> list = new ArrayList<>();
        if (resp == null || resp.getData() == null)
            return list;

        for (DisciplineData d : resp.getData()) {
            Class c = new Class();
            c.setDisciplineName(d.getDisciplineName());
            c.setScheduledAt(d.getClassScheduledAt());
            c.setMaxParticipants(d.getClassMaxParticipants());
            c.setGymName(d.getGymName());
            c.setProfessorFirstName(d.getProfessorName()); // backend ya concatena
            list.add(c);
        }
        return list;
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
        if (tokenRepository.hasToken()) {
            String token = tokenRepository.getToken();
            Log.d(TAG, "Token obtenido del TokenRepository: " + (token != null ? "SÍ" : "NO"));
            return token;
        }

        Log.d(TAG, "No hay token en TokenRepository, usando fallback");

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(KEY_JWT_TOKEN, null);
        Log.d(TAG, "Token en UserPrefs: " + (token != null ? "SÍ" : "NO"));

        // Si no lo encuentra, intenta con USER_DATA (usado en LoginFragment)
        if (token == null) {
            prefs = getSharedPreferences("userData", MODE_PRIVATE); // USER_DATA constant
            token = prefs.getString(KEY_JWT_TOKEN, null);
            Log.d(TAG, "Token en userData: " + (token != null ? "SÍ" : "NO"));
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
        if (classItem == null)
            return;
        Toast.makeText(this,
                "Clase: " + classItem.getDisciplineName() +
                        "\nProfesor: " + classItem.getProfessorFirstName() +
                        "\nGimnasio: " + classItem.getGymName() +
                        "\nFecha: " + classItem.getScheduledAt() +
                        "\nParticipantes máx: " + classItem.getMaxParticipants(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisciplineClick(String disciplineName) {
        // Mostrar clases para la disciplina seleccionada
        showClassesForDiscipline(disciplineName);
    }
}
