package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.ServiceAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Service;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceFragment extends Fragment {
    RecyclerView recyclerServices;
    ServiceAdapter adapter;
    List<Service> serviceList;
    List<Service> serviceListFull;
    EditText searchView;
    ChipGroup chipGroup;
    ImageView clearSearch;
    ProgressBar searchProgress;
    LinearLayout emptyState, loadingState;
    
    private boolean isChipClicked = false;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        // Initialize views
        recyclerServices = view.findViewById(R.id.recyclerServices);
        searchView = view.findViewById(R.id.searchView);
        chipGroup = view.findViewById(R.id.chipGroup);
        clearSearch = view.findViewById(R.id.clearSearch);
        searchProgress = view.findViewById(R.id.searchProgress);
        emptyState = view.findViewById(R.id.emptyState);
        loadingState = view.findViewById(R.id.loadingState);

        // Setup RecyclerView
        recyclerServices.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Load data immediately
        serviceList = new ArrayList<>();
        loadDummyServices();
        serviceListFull = new ArrayList<>(serviceList);
        adapter = new ServiceAdapter(getContext(), serviceList);
        recyclerServices.setAdapter(adapter);

        setupChips();
        setupSearch();

        return view;
    }

    private void setupChips() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("All");
        
        for (Service item : serviceListFull) {
            String category = item.getCategory();
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }
        
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            Chip chip = new Chip(getContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setClickable(true);
            
            if (i == 0) {
                chip.setChecked(true);
            }
            
            chip.setOnClickListener(v -> {
                filterByCategory(category);
                isChipClicked = true;
                searchView.setText("");
            });
            chipGroup.addView(chip);
        }
    }

    private void setupSearch() {
        clearSearch.setOnClickListener(v -> {
            searchView.setText("");
            clearSearch.setVisibility(View.GONE);
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                
                if (isChipClicked) {
                    isChipClicked = false;
                    return;
                }
                
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    ((Chip) chipGroup.getChildAt(i)).setChecked(false);
                }
                
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                searchRunnable = () -> filterBySearch(s.toString());
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterByCategory(String category) {
        showSearchProgress();
        
        new Handler().postDelayed(() -> {
            serviceList.clear();
            if (category.equals("All")) {
                serviceList.addAll(serviceListFull);
            } else {
                for (Service item : serviceListFull) {
                    if (item.getCategory().equals(category)) {
                        serviceList.add(item);
                    }
                }
            }
            adapter.notifyDataSetChanged();
            updateEmptyState();
            hideSearchProgress();
        }, 200);
    }

    private void filterBySearch(String searchText) {
        showSearchProgress();
        
        new Handler().postDelayed(() -> {
            serviceList.clear();
            if (searchText.isEmpty()) {
                serviceList.addAll(serviceListFull);
            } else {
                String query = searchText.toLowerCase();
                for (Service item : serviceListFull) {
                    if (item.getName().toLowerCase().contains(query) ||
                        item.getCategory().toLowerCase().contains(query)) {
                        serviceList.add(item);
                    }
                }
            }
            adapter.notifyDataSetChanged();
            updateEmptyState();
            hideSearchProgress();
        }, 200);
    }

    private void showLoadingState() {
        loadingState.setVisibility(View.VISIBLE);
        recyclerServices.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        loadingState.setVisibility(View.GONE);
        recyclerServices.setVisibility(View.VISIBLE);
        updateEmptyState();
    }

    private void showSearchProgress() {
        searchProgress.setVisibility(View.VISIBLE);
        clearSearch.setVisibility(View.GONE);
    }

    private void hideSearchProgress() {
        searchProgress.setVisibility(View.GONE);
        if (searchView.getText().length() > 0) {
            clearSearch.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState() {
        if (serviceList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerServices.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerServices.setVisibility(View.VISIBLE);
        }
    }

    private void loadDummyServices() {
        Service.ServiceProvider cleanPro = new Service.ServiceProvider(
            "CleanPro Services", 
            "Professional cleaning service with 5+ years of experience.",
            "+880 1785 954300", 
            "contact@cleanpro.com", 
            4.8f, 
            120, 
            R.drawable.service_probider
        );

        Service.ServiceProvider homeCare = new Service.ServiceProvider(
            "HomeCare Specialists", 
            "Expert home cleaning with eco-friendly products.",
            "+880 1712 345678", 
            "info@homecare.com", 
            4.9f, 
            85, 
            R.drawable.ic_launcher_foreground
        );

        Service.ServiceProvider freshNShine = new Service.ServiceProvider(
            "Fresh & Shine", 
            "Bringing sparkle to your home with our premium cleaning services.",
            "+880 1812 345678", 
            "service@freshnshine.com", 
            4.7f, 
            156, 
            R.drawable.ic_launcher_foreground
        );

        serviceList.add(new Service("Premium Bathroom Cleaning", "Bathroom", "1900", 
            "Deep cleaning for bathroom with sanitization and mold removal.", 
            R.drawable.ic_bathroom, cleanPro));
            
        serviceList.add(new Service("Kitchen Regular Cleaning", "Kitchen", "699", 
            "Basic regular cleaning for kitchen including countertops and appliances.", 
            R.drawable.ic_kitchen, homeCare));
            
        serviceList.add(new Service("Floor Deep Cleaning", "Floor", "2800", 
            "Complete floor deep cleaning for all types of flooring.", 
            R.drawable.ic_floor, freshNShine));
            
        serviceList.add(new Service("Full Window Cleaning", "Window", "1000", 
            "Professional cleaning for standard windows, inside and out.", 
            R.drawable.ic_window, cleanPro));
            
        serviceList.add(new Service("Kitchen Deep Cleaning", "Kitchen", "1299", 
            "Thorough deep cleaning including cabinets, oven, and hard-to-reach areas.", 
            R.drawable.ic_kitchen, homeCare));
            
        serviceList.add(new Service("Bathroom Deep Clean", "Bathroom", "2200", 
            "Intensive bathroom cleaning with grout and tile treatment.", 
            R.drawable.ic_bathroom, freshNShine));
    }
}
