package com.android.excuses404.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.excuses404.R;
import com.android.excuses404.adapters.ClassesAdapter;
import com.android.excuses404.models.Class;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DisciplineClassesActivity extends AppCompatActivity implements ClassesAdapter.OnClassClickListener {

    public static final String EXTRA_DISCIPLINE_NAME = "extra_discipline_name";
    public static final String EXTRA_CLASSES_LIST = "extra_classes_list"; // ArrayList<Class>

    private TextView tvTitle;
    private RecyclerView rvClasses;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private View btnBack;

    // Filtros UI
    private Spinner spSede;
    private TextView tvDatePicker;
    private View btnClearFilters;

    private ClassesAdapter classesAdapter;
    private ArrayList<Class> classesForDiscipline; // original (de Intent)
    private ArrayList<Class> filteredClasses = new ArrayList<>();

    // Estado de filtros
    private String selectedSede = null; // null o "Todas"
    private String selectedDate = null; // formato yyyy-MM-dd

    private final SimpleDateFormat inputDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discipline_classes);

        initViews();
        loadFromIntent();
        setupRecycler();
        setupFilters();
        applyFilters();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title_discipline);
        rvClasses = findViewById(R.id.rv_discipline_classes);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.pb_loading_discipline);
        btnBack = findViewById(R.id.btn_back_discipline);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        spSede = findViewById(R.id.sp_sede);
        tvDatePicker = findViewById(R.id.tv_date_picker);
        btnClearFilters = findViewById(R.id.btn_clear_filters);
    }

    private void loadFromIntent() {
        String discipline = getIntent().getStringExtra(EXTRA_DISCIPLINE_NAME);
        if (discipline == null) discipline = "Sin disciplina";
        tvTitle.setText(discipline);
        classesForDiscipline = getIntent().getParcelableArrayListExtra(EXTRA_CLASSES_LIST);
        if (classesForDiscipline == null) classesForDiscipline = new ArrayList<>();
    }

    private void setupRecycler() {
        classesAdapter = new ClassesAdapter();
        classesAdapter.setOnClassClickListener(this);
        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        rvClasses.setAdapter(classesAdapter);
    }

    private void setupFilters() {
        // Opciones de sede (usar gymName + " - " + gymCity)
        List<String> sedeOptions = new ArrayList<>();
        sedeOptions.add("Todas");
        Set<String> uniques = new LinkedHashSet<>();
        for (Class c : classesForDiscipline) {
            String sede = c.getFullGymInfo();
            if (sede == null || sede.trim().isEmpty()) sede = "Sin sede";
            uniques.add(sede);
        }
        sedeOptions.addAll(uniques);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sedeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSede.setAdapter(adapter);
        spSede.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sel = (String) parent.getItemAtPosition(position);
                selectedSede = "Todas".equals(sel) ? null : sel;
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Date picker
        tvDatePicker.setOnClickListener(v -> {
            final Calendar cal = Calendar.getInstance();
            DatePickerDialog dlg = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth, 0, 0, 0);
                        selectedDate = onlyDate.format(c.getTime());
                        tvDatePicker.setText(selectedDate);
                        applyFilters();
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dlg.show();
        });

        // Limpiar filtros
        btnClearFilters.setOnClickListener(v -> {
            selectedSede = null;
            selectedDate = null;
            tvDatePicker.setText("Fecha");
            spSede.setSelection(0);
            applyFilters();
        });
    }

    private void applyFilters() {
        filteredClasses.clear();
        for (Class c : classesForDiscipline) {
            if (selectedSede != null) {
                String sede = c.getFullGymInfo();
                if (sede == null || sede.trim().isEmpty()) sede = "Sin sede";
                if (!selectedSede.equals(sede)) continue;
            }
            if (selectedDate != null) {
                String sched = c.getScheduledAt();
                if (sched == null || sched.length() < 10) continue;
                String datePart = sched.substring(0, 10); // yyyy-MM-dd
                // Si el formato no coincide, intentar parsear y re-formatear
                if (!datePart.equals(selectedDate)) {
                    try {
                        datePart = onlyDate.format(inputDateTime.parse(sched));
                    } catch (ParseException ignored) {}
                    if (!selectedDate.equals(datePart)) continue;
                }
            }
            filteredClasses.add(c);
        }
        render();
    }

    private void render() {
        progressBar.setVisibility(View.GONE);
        if (filteredClasses.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvClasses.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvClasses.setVisibility(View.VISIBLE);
            classesAdapter.setClasses(filteredClasses);
        }
    }

    @Override
    public void onClassClick(Class classItem) {
        Toast.makeText(this,
                "Clase: " + classItem.getDisciplineName() +
                        "\nProfesor: " + classItem.getFullProfessorName() +
                        "\nGimnasio: " + classItem.getFullGymInfo() +
                        "\nFecha: " + classItem.getScheduledAt() +
                        "\nParticipantes máx: " + classItem.getMaxParticipants(),
                Toast.LENGTH_LONG).show();
    }
}
