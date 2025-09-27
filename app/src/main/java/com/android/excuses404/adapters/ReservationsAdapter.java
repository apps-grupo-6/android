package com.android.excuses404.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.excuses404.R;
import com.android.excuses404.data.api.model.ReservationClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {

    private List<ReservationClass> reservationClasses;
    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault());
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public ReservationsAdapter() {
        this.reservationClasses = new ArrayList<>();
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation_class, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        ReservationClass reservationClass = reservationClasses.get(position);
        holder.bind(reservationClass);
    }

    @Override
    public int getItemCount() {
        return reservationClasses.size();
    }

    public void setReservationClasses(List<ReservationClass> reservationClasses) {
        this.reservationClasses = reservationClasses != null ? reservationClasses : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void clearReservations() {
        this.reservationClasses.clear();
        notifyDataSetChanged();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDisciplineName;
        private final TextView tvGymName;
        private final TextView tvGymCity;
        private final TextView tvProfessorName;
        private final TextView tvDate;
        private final TextView tvMaxParticipants;

        private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault());
        private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDisciplineName = itemView.findViewById(R.id.tv_discipline_name);
            tvGymName = itemView.findViewById(R.id.tv_gym_name);
            tvGymCity = itemView.findViewById(R.id.tv_gym_city);
            tvProfessorName = itemView.findViewById(R.id.tv_professor_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvMaxParticipants = itemView.findViewById(R.id.tv_max_participants);
        }

        public void bind(ReservationClass reservationClass) {
            // Set title as "Clase con {professor name}"
            String professorName = reservationClass.getProfessorFullName();
            tvDisciplineName.setText("Clase con " + professorName);

            // Set participant status with proper translation
            String participantStatus = reservationClass.getParticipantStatus();
            String statusText = "Estado: ";
            if ("active".equals(participantStatus)) {
                statusText += "Activa";
            } else if (participantStatus != null) {
                statusText += participantStatus;
            } else {
                statusText += "No disponible";
            }
            tvGymName.setText(statusText);

            // Set participant added date as gym city
            String addedDate = formatDate(reservationClass.getParticipantAddedAt());
            tvGymCity.setText("Reservado: " + addedDate);

            // Set professor name
            tvProfessorName.setText(professorName);

            // Set formatted date
            String formattedDate = formatDate(reservationClass.getClassScheduledAt());
            tvDate.setText(formattedDate);

            // Set confirmation status as max participants
            String confirmedAt = reservationClass.getParticipantConfirmedAt();
            tvMaxParticipants.setText(confirmedAt != null ? "Confirmado" : "Pendiente");
        }

        private String formatDate(String dateString) {
            if (dateString == null || dateString.isEmpty()) {
                return "Fecha no disponible";
            }

            try {
                // Try parsing with different formats
                Date date;
                try {
                    date = inputDateFormat.parse(dateString);
                } catch (ParseException e) {
                    // Try alternative format without milliseconds
                    SimpleDateFormat altFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    try {
                        date = altFormat.parse(dateString);
                    } catch (ParseException e2) {
                        // Try simple date format
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault());
                        date = simpleFormat.parse(dateString);
                    }
                }
                return outputDateFormat.format(date);
            } catch (ParseException e) {
                android.util.Log.e("ReservationsAdapter", "Error parsing date: " + dateString, e);
                return dateString; // Return original string if parsing fails
            }
        }
    }
}
