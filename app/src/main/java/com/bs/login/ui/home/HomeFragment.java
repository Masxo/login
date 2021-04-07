package com.bs.login.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bs.login.FragmentActivityCommunicator;
import com.bs.login.Adapters.RecyclerAdapter;
import com.bs.login.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    //#region OBJECTS & VARIABLES
    private IntentIntegrator QR_scanner;
    private FragmentHomeBinding binding;
    private RecyclerAdapter mAdapter;
    private HomeViewModel homeViewModel;
    private List<Map<String, Object>> DB_dataset = new ArrayList<>(), 
            data_loading = new ArrayList<>();
    private Map<String, Object> user = new HashMap<>();
    private FragmentActivityCommunicator communicator;
    //#endregion

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // intent integrator for presenting qr code reading activity in fragment,
        // setting format & orientation lock
        QR_scanner = new IntentIntegrator(getActivity()).forSupportFragment(this);
        QR_scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        QR_scanner.setOrientationLocked(true);
        // null list to show loading
        data_loading.add(null);
        // creating adapter, viewmodel & inflating binding
        mAdapter = new RecyclerAdapter(data_loading);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater);
        // setting properties of reciclerView
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setHasFixedSize(true);
        // setting listener
        binding.buttonData.setOnClickListener(v -> ordenar(v));
        binding.buttonNome.setOnClickListener(v -> ordenar(v));
        binding.floatingActionButton.setOnClickListener(v -> QR_scanner.initiateScan());
        // adding listener to each mutable live data
        homeViewModel.setDB_dataset(null).observe(getViewLifecycleOwner(), maps -> {
            DB_dataset = maps;
            mAdapter.setData(DB_dataset);
        });
        homeViewModel.setCommunicator(null).observe(getViewLifecycleOwner(), fragmentActivityCommunicator -> {
            communicator = fragmentActivityCommunicator;
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // parse the result read from the qr code
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if it could not detect a qr code
        if (result != null) {
            String res = result.getContents();
            //if QR Code is null
            if (res == null) {
                Snackbar.make(binding.getRoot(), "ERRO", Snackbar.LENGTH_LONG)
                        .setAction("Tente novamente", v -> QR_scanner.initiateScan()).show();
            } else {
                // clear the single instance of hte user
                user.clear();
                // add the relevant data
                user.put("Time", new Timestamp(new Date()));
                user.put("Nome",res);
                // update the DB with the new data
                communicator.updateDatabase(user);
                // to update the data it needs to be at least nourgat version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // set to loading the recyclerView
                    mAdapter.setData(data_loading);
                    // find the element in the list, update it's data and the recyclerView
                    for (Map<String, Object> users : DB_dataset) {
                        if (users.containsValue(res)) {
                            DB_dataset.get(DB_dataset.indexOf(users)).replace("Data", user.get("Time"));
                            mAdapter.setData(DB_dataset);
                            break;
                        }
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void ordenar(View v) {
        // set to loading the recyclerView
        mAdapter.setData(data_loading);
        // obtain the data from the selected button
        String s = ((Button) v).getText().toString().substring(8);
        // to sort the data it needs to be at least nourgat version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // internal sort with basic comparator
            DB_dataset.sort((o1, o2) -> {
                if (s.equals("Data"))
                    return ((Timestamp) o2.get(s)).compareTo((Timestamp) o1.get(s));
                else
                    return ((String) o1.get(s)).compareTo((String) o2.get(s));
            });
        }
        // update the recyclerView
        mAdapter.setData(DB_dataset);
    }

}