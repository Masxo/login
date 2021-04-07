package com.bs.login.ui.search;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bs.login.Adapters.RecyclerAdapter;
import com.bs.login.R;
import com.bs.login.databinding.FragmentSearchBinding;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {

    //#region OBJECTS & VARIABLES
    private SearchViewModel searchViewModel;
    private CollectionReference DB;
    private FragmentSearchBinding binding;
    private RecyclerAdapter mAdapter;
    private List<Map<String, Object>> DB_dataset = new ArrayList<>(),
            data_loading = new ArrayList<>();
    private List<String> data_spinner = new ArrayList<>();
    //#endregion

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // null list to show loading
        data_loading.add(null);
        // creating adapter, viewmodel & inflating binding
        mAdapter = new RecyclerAdapter(data_loading);
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        binding = FragmentSearchBinding.inflate(inflater);
        // setting properties of reciclerView
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setHasFixedSize(true);
        // setting listener
        binding.button.setOnClickListener(v -> sort());
        binding.checkBoxNome.setOnClickListener(v -> binding.pesquisaNome.setVisibility(((MaterialCheckBox) v).isChecked() ? View.VISIBLE : View.INVISIBLE));
        binding.checkBoxData.setOnClickListener(v -> binding.pesquisaData.setVisibility(((MaterialCheckBox) v).isChecked() ? View.VISIBLE : View.INVISIBLE));
        // adding listener to each mutable live data
        searchViewModel.setDB(null).observe(getViewLifecycleOwner(), collectionReference -> DB = collectionReference);
        searchViewModel.setData(null).observe(getViewLifecycleOwner(),maps -> {
            // setting list and adapter for the spinner
            data_spinner = maps;
            binding.pesquisaNome.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_layout, data_spinner));
        });

        return binding.getRoot();
    }

    public void sort() {
        // null list to show loading
        mAdapter.setData(data_loading);

        // verifify if it's to sort by data or name
        if(binding.checkBoxData.isChecked()) {
            // it tries to parse the data set in the binding
            try {
                // firebase structured querie
                DB.whereLessThan("Data",new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(binding.pesquisaData.getText().toString().trim()+" 23:59:59")))
                        .whereGreaterThan("Data",new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(binding.pesquisaData.getText().toString().trim()+" 00:00:00"))
                        .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        // clearing the dataset
                        DB_dataset.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // sorting by both
                            if (binding.checkBoxNome.isChecked()) {
                                if (document.get("Nome").toString().equals(binding.pesquisaNome.getSelectedItem().toString())) {
                                    DB_dataset.add(document.getData());
                                }
                            } else {
                                DB_dataset.add(document.getData());
                            }
                        }
                        // to sort the data it needs to be at least nourgat version
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            DB_dataset.sort((o1, o2) -> ((String) o1.get("Nome")).compareTo((String) o2.get("Nome")));
                        }
                        // update the recyclerView
                        mAdapter.setData(DB_dataset);
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            // firebase basic querie
            DB.document(binding.pesquisaNome.getSelectedItem().toString()).collection("Time")
                    .orderBy("Time", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    // clearing the dataset
                    DB_dataset.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // creating a object for each data
                        Map<String,Object> user = new HashMap<>();
                        // adding a data and name to the object
                        user.put("Data",document.get("Time"));
                        user.put("Nome",binding.pesquisaNome.getSelectedItem().toString());
                        // adding the object to the list
                        DB_dataset.add(user);
                    }
                    // update the recyclerView
                    mAdapter.setData(DB_dataset);
                }
            });
        }
    }
}