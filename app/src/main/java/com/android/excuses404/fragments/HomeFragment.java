/*
package com.example.excuses404.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.excuses404.data.repository.PokemonServiceCallBack;
import com.example.excuses404.model.Pokemon;
import com.example.excuses404.services.PokemonService;
import com.example.excuses404.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    @Inject
    public PokemonService pokemonService;

    private ListView listView;
    private List<String> pokemonDisplayList;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        listView = view.findViewById(R.id.listView);
        pokemonDisplayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), 
                                   android.R.layout.simple_list_item_1,
                                   pokemonDisplayList);
        listView.setAdapter(adapter);
        loadPokemons();
        
        listView.setOnItemClickListener((parent, v, position, id) -> {
            String selectedPokemon = pokemonDisplayList.get(position);
            String pokemonName = selectedPokemon.split(" - ")[0];
            
            Bundle args = new Bundle();
            args.putString("pokemonId", pokemonName);
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_detailFragment, args);
        });
    }

    private void loadPokemons() {
        pokemonService.getAllPokemons(new PokemonServiceCallBack() {
            @Override
            public void onSuccess(List<Pokemon> pokemons) {
                pokemonDisplayList.clear();
                pokemonDisplayList.addAll(pokemons.stream()
                    .map(pokemon -> pokemon.getName() + " - " + pokemon.getType())
                    .collect(Collectors.toList()));
                requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            }

            @Override
            public void onError(Throwable error) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(),
                    "Error al cargar los Pokemon: " + error.getMessage(),
                    Toast.LENGTH_LONG).show());
            }
        });
    }
}
*/
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

public class HomeFragment extends Fragment {

    private AttendanceViewModel vm;
    private SessionsAdapter adapter;

    public HomeFragment() { super(R.layout.home_fragment); }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(AttendanceViewModel.class);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.recycler);
        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipe = view.findViewById(R.id.swipe);

        adapter = new SessionsAdapter(new SessionsAdapter.OnAction() {
            @Override public void onReserve(ClassSession s) { vm.reserve(s); }
            @Override public void onConfirm(ClassSession s) { vm.confirm(s); }
            @Override public void onCheckIn(ClassSession s) { vm.checkIn(s); }
        });
        rv.setAdapter(adapter);

        swipe.setOnRefreshListener(() -> {
            vm.refresh();
            swipe.setRefreshing(false);
        });

        vm.sessions().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            if (list == null || list.isEmpty()) {
                Snackbar.make(view, "No hay clases. Desliza para refrescar.", Snackbar.LENGTH_SHORT).show();
            }
        });

        // primera carga
        vm.refresh();
    }

    // ---------- Adapter ----------
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
            void onCheckIn(ClassSession s);
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
            private final Button btnReserve, btnConfirm, btnCheckIn;
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
                btnCheckIn = itemView.findViewById(R.id.btnCheckIn);
            }

            void bind(ClassSession s){
                tvTitle.setText(s.getTitle());
                tvCoach.setText("Coach: " + s.getCoach());
                tvTime.setText(fmt.format(new Date(s.getStartsAt())) + " - " + fmt.format(new Date(s.getEndsAt())));

                long now = System.currentTimeMillis();
                boolean inWindow = now >= s.getStartsAt() - 15*60_000L && now <= s.getEndsAt() + 15*60_000L;

                btnReserve.setEnabled(!s.isReserved());
                btnConfirm.setEnabled(s.isReserved() && !s.isConfirmed());
                btnCheckIn.setEnabled(s.isConfirmed() && inWindow);

                btnReserve.setOnClickListener(v -> onAction.onReserve(s));
                btnConfirm.setOnClickListener(v -> onAction.onConfirm(s));
                btnCheckIn.setOnClickListener(v -> onAction.onCheckIn(s));
            }
        }
    }
}
