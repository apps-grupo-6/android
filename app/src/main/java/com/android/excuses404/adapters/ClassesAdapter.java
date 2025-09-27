package com.android.excuses404.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.excuses404.R;
import com.android.excuses404.models.Class;

import java.util.ArrayList;
import java.util.List;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassViewHolder> {
    private List<Class> classes;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(Class classItem);
    }

    public ClassesAdapter() {
        this.classes = new ArrayList<>();
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes != null ? classes : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnClassClickListener(OnClassClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Class classItem = classes.get(position);
        holder.bind(classItem, listener);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDiscipline;
        private final TextView tvProfessor;
        private final TextView tvLocation;
        private final TextView tvScheduledAt;
        private final TextView tvMaxParticipants;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiscipline = itemView.findViewById(R.id.tv_discipline);
            tvProfessor = itemView.findViewById(R.id.tv_professor);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvScheduledAt = itemView.findViewById(R.id.tv_scheduled_at);
            tvMaxParticipants = itemView.findViewById(R.id.tv_max_participants);
        }

        public void bind(Class classItem, OnClassClickListener listener) {
            try {
                if (classItem == null) {
                    Log.w("ClassesAdapter", "classItem is null");
                    return;
                }

                // Usar valores por defecto si algún campo es null
                String disciplineName = classItem.getDisciplineName() != null ? classItem.getDisciplineName() : "N/A";
                String professorName = classItem.getFullProfessorName() != null ? classItem.getFullProfessorName() : "N/A";
                String gymInfo = classItem.getFullGymInfo() != null ? classItem.getFullGymInfo() : "N/A";
                String scheduledAt = classItem.getScheduledAt() != null ? classItem.getScheduledAt() : "N/A";

                tvDiscipline.setText("Disciplina: " + disciplineName);
                tvProfessor.setText("Profesor: " + professorName);
                tvLocation.setText("Gimnasio: " + gymInfo);
                tvScheduledAt.setText("Horario: " + scheduledAt);
                tvMaxParticipants.setText("Max. Participantes: " + classItem.getMaxParticipants());

                if (listener != null) {
                    itemView.setOnClickListener(v -> listener.onClassClick(classItem));
                } else {
                    itemView.setOnClickListener(null);
                }
            } catch (Exception e) {
                Log.e("ClassesAdapter", "Error in bind method: " + e.getMessage(), e);
            }
        }
    }
}
