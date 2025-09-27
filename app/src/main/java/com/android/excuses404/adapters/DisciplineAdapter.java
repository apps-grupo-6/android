package com.android.excuses404.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.excuses404.R;

import java.util.ArrayList;
import java.util.List;

public class DisciplineAdapter extends RecyclerView.Adapter<DisciplineAdapter.DisciplineViewHolder> {

    public interface OnDisciplineClickListener {
        void onDisciplineClick(String disciplineName);
    }

    private List<String> disciplines = new ArrayList<>();
    private OnDisciplineClickListener listener;

    public void setDisciplines(List<String> list) {
        disciplines = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnDisciplineClickListener(OnDisciplineClickListener l) {
        listener = l;
    }

    @NonNull
    @Override
    public DisciplineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discipline, parent, false);
        return new DisciplineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisciplineViewHolder holder, int position) {
        holder.bind(disciplines.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return disciplines.size();
    }

    static class DisciplineViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;

        public DisciplineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_discipline_name);
        }

        void bind(String name, OnDisciplineClickListener listener) {
            tvName.setText(name);
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDisciplineClick(name);
            });
        }
    }
}

