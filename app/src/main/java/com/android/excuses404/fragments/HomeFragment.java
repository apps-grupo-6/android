package com.android.excuses404.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.android.excuses404.R;
import com.android.excuses404.activities.ClassesActivity;
import com.android.excuses404.models.ClassSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private AttendanceViewModel vm;

    public HomeFragment() {
        super(R.layout.home_fragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(AttendanceViewModel.class);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.recycler);
        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe = view.findViewById(R.id.swipe);
        Button btnIr = view.findViewById(R.id.btnIrClases);

        HomeAdapter adapter = new HomeAdapter(new HomeAdapter.OnAction() {
            @Override public void onReserve(ClassSession s) { vm.reserve(s); }
            @Override public void onConfirm(ClassSession s) { vm.confirm(s); }
            @Override public void onCancel(ClassSession s)  { vm.cancel(s); }
        });
        rv.setAdapter(adapter);

        vm.sessions().observe(getViewLifecycleOwner(), list -> {
            if (list == null) { adapter.submit(null); return; }
            int to = Math.min(3, list.size());
            adapter.submit(list.subList(0, to));
        });

        swipe.setOnRefreshListener(() -> { vm.refresh(); swipe.setRefreshing(false); });
        btnIr.setOnClickListener(v ->
                startActivity(new android.content.Intent(requireContext(), com.android.excuses404.activities.ClassesActivity.class))
        );

        vm.refresh();
    }

    // ----- Adapter -----
    static class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.VH> {
        interface OnAction {
            void onReserve(ClassSession s);

            void onConfirm(ClassSession s);

            void onCancel(ClassSession s);
        }

        private final OnAction onAction;
        private final java.util.ArrayList<ClassSession> data = new java.util.ArrayList<>();

        HomeAdapter(OnAction onAction) {
            this.onAction = onAction;
        }

        void submit(java.util.List<ClassSession> list) {
            data.clear();
            if (list != null) data.addAll(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_home_session, p, false);
            return new VH(v, onAction);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            h.bind(data.get(pos));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            private final TextView tvTitle, tvTime;
            private final Button btnPrimary, btnCancel;
            private final OnAction onAction;
            private final java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault());

            VH(@NonNull View itemView, OnAction onAction) {
                super(itemView);
                this.onAction = onAction;
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvTime = itemView.findViewById(R.id.tvTime);
                btnPrimary = itemView.findViewById(R.id.btnPrimary);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }

            void bind(ClassSession s) {
                tvTitle.setText(s.getTitle());
                tvTime.setText(fmt.format(new java.util.Date(s.getStartsAt())) + " - " + fmt.format(new java.util.Date(s.getEndsAt())));

                // Lógica de UI según estado
                if (!s.isReserved()) {
                    btnPrimary.setText("Reservar");
                    btnPrimary.setEnabled(true);
                    btnPrimary.setOnClickListener(v -> onAction.onReserve(s));
                    btnCancel.setVisibility(View.GONE);
                } else if (s.isReserved() && !s.isConfirmed()) {
                    btnPrimary.setText("Confirmar");
                    btnPrimary.setEnabled(true);
                    btnPrimary.setOnClickListener(v -> onAction.onConfirm(s));
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setEnabled(true);
                    btnCancel.setOnClickListener(v -> onAction.onCancel(s));
                } else { // confirmado
                    btnPrimary.setText("Confirmada");
                    btnPrimary.setEnabled(false);
                    btnPrimary.setOnClickListener(null);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setEnabled(true);
                    btnCancel.setOnClickListener(v -> onAction.onCancel(s));
                }
            }
        }
    }
}