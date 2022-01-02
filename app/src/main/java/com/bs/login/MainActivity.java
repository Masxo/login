package com.bs.login;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bs.login.databinding.ActivityMainBinding;
import com.bs.login.ui.home.HomeViewModel;
import com.bs.login.ui.search.SearchViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements FragmentActivityCommunicator {

    //#region OBJECTS & VARIABLES
    private ActivityMainBinding binding;
    private CollectionReference DB = FirebaseFirestore.getInstance().collection("pessoas");
    private List<Map<String, Object>> DB_dataset = new ArrayList<>();
    private BottomNavigationView bottomNav;
    private HomeViewModel homeViewModel;
    private SearchViewModel searchViewModel;
    private NavHostFragment navHostFragment;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflating & setting contentview
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewModels
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // Bottom Navigation Fragment
        bottomNav = binding.navView;
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        // Setting Bottom Navigaion
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Searching data
        DB.orderBy("Time", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> list = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    // adds data to objects
                    DB_dataset.add(document.getData());
                    list.add(document.get("Nome").toString());
                }
                // adding to viewmodels relevant data
                homeViewModel.setDB_dataset(DB_dataset);
                homeViewModel.setCommunicator(this);
                searchViewModel.setDB(DB);
                searchViewModel.setData(list);
            }
        });
    }


    // method for data generation based on logins
    private void generateData() {
        DB.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DB.document(document.getId()).collection("Time").orderBy("Time", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                DB.document(document.getId()).update("Time", document2.get("Time"));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    for (Map<String, Object> users : DB_dataset) {
                                        if (users.containsValue(document.getId())) {
                                            DB_dataset.get(DB_dataset.indexOf(users)).replace("Time", document2.get("Time"));
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.w("Error", "procurando data no generateData");
                        }
                    });
                }
            } else {
                Log.w("Error", "procurando nome no generateData");
            }
        });
    }

    @Override
    public void updateFragment(Fragment fragment) {
        navHostFragment.getChildFragmentManager().beginTransaction().replace(fragment.getId(),fragment).commitNow();
    }

    @Override
    public void updateDatabase(Map<String,Object> data) {
        if(!DB.document((String) data.get("Nome")).get().isSuccessful())
            DB.document((String) data.get("Nome")).set(data);
        DB.document((String) data.get("Nome")).collection("Time").add(data);
        DB.document((String) data.get("Nome")).update("Time", data.get("Time"));
    }
}