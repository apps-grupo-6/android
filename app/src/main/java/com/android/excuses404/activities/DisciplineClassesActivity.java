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
import com.android.excuses404.data.api.model.DisciplineData;
import com.android.excuses404.data.api.model.DisciplinesResponse;
import com.android.excuses404.data.repository.LocationsRepository;
import com.android.excuses404.data.repository.LocationsServiceCallBack;
import com.android.excuses404.models.Class;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DisciplineClassesActivity extends AppCompatActivity implements ClassesAdapter.OnClassClickListener {

    public static final String EXTRA_DISCIPLINE_NAME = "";
    private TextView tvTitle;
    private RecyclerView rvClasses;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private View btnBack;

    // Filtros UI
    private Spinner spSede;
    private TextView tvDatePickerFrom;
    private TextView tvDatePickerTo;
    private View btnClearFilters;

    private ClassesAdapter classesAdapter;
    private ArrayList<Class> classesForDiscipline = new ArrayList<>(); // cargadas de API
    private ArrayList<Class> filteredClasses = new ArrayList<>();

    // Estado de filtros
    private String selectedSede = null; // null o "Todas"
    private String selectedDateFrom = null; // formato yyyy-MM-dd
    private String selectedDateTo = null; // formato yyyy-MM-dd

    private final SimpleDateFormat inputDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final SimpleDateFormat onlyDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Inject
    public LocationsRepository locationsRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discipline_classes);

        initViews();
        setupRecycler();
        setupFilters();

        String discipline = getIntent().getStringExtra(EXTRA_DISCIPLINE_NAME);
        if (discipline == null) discipline = "Sin disciplina";
        tvTitle.setText(discipline);

        fetchData();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title_discipline);
        rvClasses = findViewById(R.id.rv_discipline_classes);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.pb_loading_discipline);
        btnBack = findViewById(R.id.btn_back_discipline);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        spSede = findViewById(R.id.sp_sede);
        tvDatePickerFrom = findViewById(R.id.tv_date_picker);
        tvDatePickerTo = findViewById(R.id.tv_date_picker2);
        btnClearFilters = findViewById(R.id.btn_clear_filters);
    }

    private void setupRecycler() {
        classesAdapter = new ClassesAdapter();
        classesAdapter.setOnClassClickListener(this);
        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        rvClasses.setAdapter(classesAdapter);
    }

    private void setupFilters() {
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
        tvDatePickerFrom.setOnClickListener(v -> {
            final Calendar cal = Calendar.getInstance();
            DatePickerDialog dlg = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth, 0, 0, 0);
                        selectedDateFrom = onlyDate.format(c.getTime());
                        tvDatePickerFrom.setText(selectedDateFrom);
                        applyFilters();
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dlg.show();
        });

        tvDatePickerTo.setOnClickListener(v -> {
            final Calendar cal = Calendar.getInstance();
            DatePickerDialog dlg = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth, 0, 0, 0);
                        selectedDateTo = onlyDate.format(c.getTime());
                        tvDatePickerTo.setText(selectedDateTo);
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
            selectedDateFrom = null;
            selectedDateTo = null;
            tvDatePickerFrom.setText("Desde");
            tvDatePickerTo.setText("Hasta");
            spSede.setSelection(0);
            applyFilters();
        });
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);

        locationsRepository.getAllLocations(new LocationsServiceCallBack() {
            @Override
            public void onSuccess(DisciplinesResponse response) {
                progressBar.setVisibility(View.GONE);
                classesForDiscipline = mapResponseToClasses(response);
                setupSedeOptions();
                applyFilters();

            }

            @Override
            public void onError(Throwable error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DisciplineClassesActivity.this, "Error cargando datos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSedeOptions() {
        List<String> sedeOptions = new ArrayList<>();
        sedeOptions.add("Todas");
        Set<String> uniques = new LinkedHashSet<>();
        for (Class c : classesForDiscipline) {
            String sede = c.getGymName();
            if (sede == null || sede.trim().isEmpty()) sede = "Sin sede";
            uniques.add(sede);
        }
        sedeOptions.addAll(uniques);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sedeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSede.setAdapter(adapter);
    }

    private ArrayList<Class> mapResponseToClasses(DisciplinesResponse response) {
        ArrayList<Class> list = new ArrayList<>();
        if (response == null || response.getData() == null) return list;

        String selectedDiscipline = getIntent().getStringExtra(EXTRA_DISCIPLINE_NAME);

        for (DisciplineData item : response.getData()) {
            if (selectedDiscipline != null && !selectedDiscipline.equals(item.getDisciplineName())) {
                continue;
            }
            Class c = new Class();
            c.setMaxParticipants(item.getClassMaxParticipants());
            c.setScheduledAt(item.getClassScheduledAt());
            c.setDisciplineName(item.getDisciplineName());
            c.setGymName(item.getGymName());
            c.setProfessorFirstName(item.getProfessorName()); // tu API trae "nombre apellido" en un solo campo
            list.add(c);
        }
        return list;
    }

    private void applyFilters() {
        filteredClasses.clear();
        for (Class c : classesForDiscipline) {
            if (selectedSede != null) {
                String sede = c.getGymName();
                if (sede == null || sede.trim().isEmpty())
                    sede = "Sin sede";

                if (!selectedSede.equals(sede)) continue; }

            if (selectedDateFrom != null || selectedDateTo != null) {
                String sched = c.getScheduledAt();

                try {
                    Date schedDate = inputDateTime.parse(sched);
                    Date schedDateOnly = onlyDate.parse(onlyDate.format(schedDate));

                    if (selectedDateFrom != null && selectedDateTo != null) {
                        Date from = onlyDate.parse(selectedDateFrom);
                        Date to = onlyDate.parse(selectedDateTo);

                        if (schedDateOnly.compareTo(from) < 0 || schedDateOnly.compareTo(to) > 0) {
                            continue;
                        }
                    }
                    else if (selectedDateFrom != null) {
                        Date from = onlyDate.parse(selectedDateFrom);
                        if (schedDateOnly.compareTo(from) < 0) {
                            continue;
                        }
                    }
                    else if (selectedDateTo != null) {
                        Date to = onlyDate.parse(selectedDateTo);
                        if (schedDateOnly.compareTo(to) > 0) {
                            continue;
                        }
                    }


                } catch (ParseException e) {
                    continue;
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
                        "\nProfesor: " + classItem.getProfessorFirstName() +
                        "\nGimnasio: " + classItem.getGymName() +
                        "\nFecha: " + classItem.getScheduledAt() +
                        "\nParticipantes máx: " + classItem.getMaxParticipants(),
                Toast.LENGTH_LONG).show();
    }
}
