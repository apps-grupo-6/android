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
