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
import com.android.excuses404.models.ClassSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private AttendanceViewModel vm;
    public HomeFragment(){ super(R.layout.home_fragment); }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(AttendanceViewModel.class);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.recycler);
        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe = view.findViewById(R.id.swipe);
        Button btnIr = view.findViewById(R.id.btnIrClases);

        SimpleAdapter adapter = new SimpleAdapter();
        rv.setAdapter(adapter);

        vm.sessions().observe(getViewLifecycleOwner(), adapter::submit);
        swipe.setOnRefreshListener(() -> { vm.refresh(); swipe.setRefreshing(false); });
        btnIr.setOnClickListener(v -> startActivity(new Intent(requireContext(), com.android.excuses404.activities.ClassesActivity.class)));

        vm.refresh();
    }

    static class SimpleAdapter extends RecyclerView.Adapter<VH> {
        private final java.util.ArrayList<ClassSession> data = new java.util.ArrayList<>();
        void submit(List<ClassSession> list){ data.clear(); if(list!=null) data.addAll(list); notifyDataSetChanged(); }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_session_simple, p, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos){ h.bind(data.get(pos)); }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime;
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        VH(@NonNull View v){ super(v); tvTitle=v.findViewById(R.id.tvTitle); tvTime=v.findViewById(R.id.tvTime); }
        void bind(ClassSession s){
            tvTitle.setText(s.getTitle());
            tvTime.setText(fmt.format(new Date(s.getStartsAt()))+" - "+fmt.format(new Date(s.getEndsAt())));
        }
    }
}
