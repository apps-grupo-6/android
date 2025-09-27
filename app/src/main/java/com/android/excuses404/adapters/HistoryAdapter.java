package com.android.excuses404.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.excuses404.R;
import com.android.excuses404.data.api.model.HistoryClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryClass> historyClasses;
    private List<HistoryClass> filteredHistoryClasses;
    private String dateFrom;
    private String dateTo;

    public HistoryAdapter() {
        this.historyClasses = new ArrayList<>();
        this.filteredHistoryClasses = new ArrayList<>();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_class, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryClass historyClass = filteredHistoryClasses.get(position);
        holder.bind(historyClass);
    }

    @Override
    public int getItemCount() {
        return filteredHistoryClasses.size();
    }

    public void setHistoryClasses(List<HistoryClass> historyClasses) {
        this.historyClasses = historyClasses != null ? historyClasses : new ArrayList<>();
        applyFilters();
    }

    public void clearHistory() {
        this.historyClasses.clear();
        this.filteredHistoryClasses.clear();
        notifyDataSetChanged();
    }

    public void setDateFilter(String dateFrom, String dateTo) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        applyFilters();
    }

    public void clearFilters() {
        this.dateFrom = null;
        this.dateTo = null;
        applyFilters();
    }

    private void applyFilters() {
        filteredHistoryClasses.clear();

        android.util.Log.d("HistoryAdapter", "Aplicando filtros - Total clases: " + historyClasses.size());
        android.util.Log.d("HistoryAdapter", "Filtro desde: " + dateFrom + ", hasta: " + dateTo);

        for (HistoryClass historyClass : historyClasses) {
            boolean matches = matchesDateFilter(historyClass);
            android.util.Log.d("HistoryAdapter", "Clase " + historyClass.getGymName() +
                    " - Fecha: " + historyClass.getParticipantUpdatedAt() + " - Coincide: " + matches);

            if (matches) {
                filteredHistoryClasses.add(historyClass);
            }
        }

        android.util.Log.d("HistoryAdapter", "Clases filtradas: " + filteredHistoryClasses.size());
        notifyDataSetChanged();
    }

    private boolean matchesDateFilter(HistoryClass historyClass) {
        if (dateFrom == null && dateTo == null) {
            android.util.Log.d("HistoryAdapter", "Sin filtros aplicados, mostrando todas las clases");
            return true; // No filter applied
        }

        String classDate = historyClass.getParticipantUpdatedAt();
        android.util.Log.d("HistoryAdapter", "Evaluando fecha de clase: " + classDate);

        if (classDate == null || classDate.isEmpty()) {
            android.util.Log.d("HistoryAdapter", "Fecha de clase es null o vacía");
            return false;
        }

        try {
            Date classDateTime = null;

            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                classDateTime = serverFormat.parse(classDate);
                android.util.Log.d("HistoryAdapter", "Parseado con formato servidor: " + classDateTime);
            } catch (ParseException e) {
                android.util.Log.d("HistoryAdapter", "Formato servidor falló, probando ISO format");
            }

            if (classDateTime == null) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                try {
                    classDateTime = inputFormat.parse(classDate);
                    android.util.Log.d("HistoryAdapter", "Parseado con formato ISO: " + classDateTime);
                } catch (ParseException e) {
                    android.util.Log.d("HistoryAdapter", "Formato ISO falló, probando formato simple");
                }
            }

            if (classDateTime == null) {
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                classDateTime = simpleFormat.parse(classDate);
                android.util.Log.d("HistoryAdapter", "Parseado con formato simple: " + classDateTime);
            }

            if (classDateTime == null) {
                android.util.Log.d("HistoryAdapter", "No se pudo parsear la fecha de la clase");
                return false;
            }

            android.util.Log.d("HistoryAdapter", "Fecha parseada de clase: " + classDateTime);

            SimpleDateFormat filterFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            if (dateFrom != null) {
                Date fromDate = filterFormat.parse(dateFrom);
                android.util.Log.d("HistoryAdapter", "Fecha desde: " + fromDate);
                if (fromDate != null && classDateTime.before(fromDate)) {
                    android.util.Log.d("HistoryAdapter", "Clase es anterior a fecha desde");
                    return false;
                }
            }

            if (dateTo != null) {
                Date toDate = filterFormat.parse(dateTo);
                android.util.Log.d("HistoryAdapter", "Fecha hasta: " + toDate);
                if (toDate != null) {
                    Date endOfToDate = new Date(toDate.getTime() + 24 * 60 * 60 * 1000);
                    android.util.Log.d("HistoryAdapter", "Fecha hasta (fin del día): " + endOfToDate);
                    if (classDateTime.after(endOfToDate)) {
                        android.util.Log.d("HistoryAdapter", "Clase es posterior a fecha hasta");
                        return false;
                    }
                }
            }

            android.util.Log.d("HistoryAdapter", "Clase pasa el filtro de fechas");
            return true;

        } catch (ParseException e) {
            android.util.Log.e("HistoryAdapter", "Error parseando fecha: " + e.getMessage());
            return false;
        }
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGymName;
        private TextView tvGymCity;
        private TextView tvProfessorName;
        private TextView tvDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGymName = itemView.findViewById(R.id.tv_gym_name);
            tvGymCity = itemView.findViewById(R.id.tv_gym_city);
            tvProfessorName = itemView.findViewById(R.id.tv_professor_name);
            tvDate = itemView.findViewById(R.id.tv_date);
        }

        public void bind(HistoryClass historyClass) {
            // Gym name
            tvGymName.setText(historyClass.getGymName() != null ? historyClass.getGymName() : "Gimnasio no disponible");

            // Gym city
            tvGymCity.setText(historyClass.getGymCity() != null ? historyClass.getGymCity() : "Ciudad no disponible");

            // Professor name
            tvProfessorName.setText(historyClass.getProfessorFullName());

            // Date - format the date nicely
            String formattedDate = formatDate(historyClass.getParticipantUpdatedAt());
            tvDate.setText(formattedDate);
        }

        private String formatDate(String dateString) {
            if (dateString == null || dateString.isEmpty()) {
                return "Fecha no disponible";
            }

            try {
                Date date = null;
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                // Try server format first: "2025-09-27 07:40:37"
                try {
                    SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    date = serverFormat.parse(dateString);
                } catch (ParseException e) {
                    // Try ISO format
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                                Locale.getDefault());
                        date = inputFormat.parse(dateString);
                    } catch (ParseException ex) {
                        // Try simple format
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat simpleOutputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        date = simpleFormat.parse(dateString);
                        return date != null ? simpleOutputFormat.format(date) : dateString;
                    }
                }

                return date != null ? outputFormat.format(date) : dateString;
            } catch (ParseException e) {
                return dateString;
            }
        }
    }
}
