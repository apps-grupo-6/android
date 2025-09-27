package com.android.excuses404.fragments;

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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.android.excuses404.R;
import com.android.excuses404.models.ClassSession;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ClassesFragment extends Fragment {

    private AttendanceViewModel vm;
    private SessionsAdapter adapter;

    public ClassesFragment() { super(R.layout.classes_fragment); } // <— antes home_fragment

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vm = new ViewModelProvider(this).get(AttendanceViewModel.class);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.recycler);
        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe = view.findViewById(R.id.swipe);

        adapter = new SessionsAdapter(new SessionsAdapter.OnAction() {
            @Override public void onReserve(ClassSession s) { vm.reserve(s); }
            @Override public void onConfirm(ClassSession s) { vm.confirm(s); }
            @Override public void onCancel(ClassSession s)  { vm.cancel(s); }
        });
        rv.setAdapter(adapter);

        swipe.setOnRefreshListener(() -> { vm.refresh(); swipe.setRefreshing(false); });
        vm.sessions().observe(getViewLifecycleOwner(), adapter::submitList);
        vm.refresh();
    }

    static class SessionDiff extends DiffUtil.ItemCallback<ClassSession> {
        @Override public boolean areItemsTheSame(@NonNull ClassSession o, @NonNull ClassSession n) { return o.getId().equals(n.getId()); }
        @Override public boolean areContentsTheSame(@NonNull ClassSession o, @NonNull ClassSession n) {
            return o.getTitle().equals(n.getTitle())
                    && o.getStartsAt() == n.getStartsAt()
                    && o.getEndsAt() == n.getEndsAt()
                    && o.getCoach().equals(n.getCoach())
                    && o.getCapacity() == n.getCapacity()
                    && o.isReserved() == n.isReserved()
                    && o.isConfirmed() == n.isConfirmed();
        }
    }

    static class SessionsAdapter extends ListAdapter<ClassSession, SessionsAdapter.VH> {
        interface OnAction {
            void onReserve(ClassSession s);
            void onConfirm(ClassSession s);
            void onCancel(ClassSession s);
        }
        private final OnAction onAction;
        SessionsAdapter(OnAction onAction){ super(new SessionDiff()); this.onAction = onAction; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
            return new VH(v, onAction);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int position) { h.bind(getItem(position)); }

        static class VH extends RecyclerView.ViewHolder {
            private final TextView tvTitle, tvCoach, tvTime;
            private final Button btnReserve, btnConfirm, btnCancel;
            private final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
            private final OnAction onAction;

            VH(@NonNull View itemView, OnAction onAction) {
                super(itemView);
                this.onAction = onAction;
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvCoach = itemView.findViewById(R.id.tvCoach);
                tvTime  = itemView.findViewById(R.id.tvTime);
                btnReserve = itemView.findViewById(R.id.btnReserve);
                btnConfirm = itemView.findViewById(R.id.btnConfirm);
                btnCancel  = itemView.findViewById(R.id.btnCancel);
            }

            void bind(ClassSession s){
                tvTitle.setText(s.getTitle());
                tvCoach.setText("Coach: " + s.getCoach());
                tvTime.setText(fmt.format(new Date(s.getStartsAt())) + " - " + fmt.format(new Date(s.getEndsAt())));

                btnReserve.setEnabled(!s.isReserved());
                btnConfirm.setEnabled(s.isReserved() && !s.isConfirmed());
                btnCancel.setEnabled(s.isReserved());

                btnReserve.setOnClickListener(v -> onAction.onReserve(s));
                btnConfirm.setOnClickListener(v -> onAction.onConfirm(s));
                btnCancel.setOnClickListener(v -> onAction.onCancel(s));
            }
        }
    }
}
