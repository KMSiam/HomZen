package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.ServiceAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Service;
import com.kmsiam.seu.isd.lab.project.homzen.R;
import com.kmsiam.seu.isd.lab.project.homzen.Utils.ChipUtils;

import java.util.ArrayList;
import java.util.List;

public class ServiceFragment extends Fragment {
    RecyclerView recyclerServices;
    ServiceAdapter adapter;
    List<Service> serviceList;
    SearchView searchView;
    private ChipGroup chipGroup;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_service, container, false);

        // Initialize views
        recyclerServices = rootView.findViewById(R.id.recyclerServices);
        searchView = rootView.findViewById(R.id.searchView);
        chipGroup = rootView.findViewById(R.id.chipGroup);

        // Setup RecyclerView
        recyclerServices.setLayoutManager(new LinearLayoutManager(getContext()));
        serviceList = new ArrayList<>();
        loadDummyServices();
        adapter = new ServiceAdapter(getContext(), serviceList);
        recyclerServices.setAdapter(adapter);

        // Setup SearchView and Chips
        setupSearchView();
        setupChips();

        return rootView;
    }

    private void setupSearchView() {
        // Clear search when a chip is selected
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // When search is cleared, select the first chip (All)
                    selectFirstChip();
                } else {
                    // When searching, clear all chips and filter by text
                    clearAllChips();
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }
    
    private void selectFirstChip() {
        if (chipGroup.getChildCount() > 0) {
            // Clear any text in search view
            searchView.setQuery("", false);
            searchView.clearFocus();
            
            // Select the first chip (All)
            Chip firstChip = (Chip) chipGroup.getChildAt(0);
            if (!firstChip.isChecked()) {
                firstChip.setChecked(true);
                // Trigger the filter for All items
                adapter.getFilter().filter("");
            }
        }
    }

    private void setupChips() {
        // Define your categories
        String[] categories = {"Bathroom", "Kitchen", "Floor", "Window", "Glass"};
        
        // Setup chips using the utility class
        ChipUtils.setupChips(requireContext(), chipGroup, categories, adapter.getFilter(), 
            new ChipUtils.OnChipSelectedListener() {
                @Override
                public void onChipSelected(String category) {
                    // Clear search when a chip is selected
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                }
            });
            
        // Set up search view to clear chip selection when searching
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // When search is cleared, select the first chip (All)
                    if (chipGroup.getChildCount() > 0) {
                        Chip firstChip = (Chip) chipGroup.getChildAt(0);
                        firstChip.setChecked(true);
                    }
                } else {
                    // When searching, clear all chips
                    for (int i = 0; i < chipGroup.getChildCount(); i++) {
                        Chip chip = (Chip) chipGroup.getChildAt(i);
                        chip.setChecked(false);
                    }
                }
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }



    private void clearAllChips() {
        // This method clears all chips
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setChecked(false);
        }
    }

    private void loadDummyServices() {
        // Using local drawable resources for images
        serviceList.add(new Service("Premium Bathroom Cleaning", "Bathroom", "1900", "Deep cleaning for bathroom.", R.drawable.ic_bathroom));
        serviceList.add(new Service("Kitchen Regular Cleaning", "Kitchen", "699", "Basic regular cleaning for kitchen", R.drawable.ic_kitchen));
        serviceList.add(new Service("Floor Deep Cleaning", "Floor", "2800", "Complete floor deep cleaning", R.drawable.ic_floor));
        serviceList.add(new Service("Full Window Cleaning", "Window", "1000", "Cleaning for standard windows", R.drawable.ic_window));
        serviceList.add(new Service("Kitchen Regular Cleaning", "Kitchen", "699", "Basic regular cleaning for kitchen", R.drawable.ic_kitchen));
        serviceList.add(new Service("Premium Bathroom Cleaning", "Bathroom", "1900", "Deep cleaning for bathroom", R.drawable.ic_bathroom));
        serviceList.add(new Service("Kitchen Premium Cleaning", "Kitchen", "699", "Basic regular cleaning for kitchen", R.drawable.ic_kitchen));
        serviceList.add(new Service("Floor Deep Cleaning", "Floor", "2800", "Complete floor deep cleaning", R.drawable.ic_floor));
        serviceList.add(new Service("Full Window Premium Cleaning", "Window", "1000", "Cleaning for standard windows", R.drawable.ic_window));
        serviceList.add(new Service("Kitchen Regular Cleaning", "Kitchen", "699", "Basic regular cleaning for kitchen", R.drawable.ic_kitchen));
        serviceList.add(new Service("Floor Stander Cleaning", "Floor", "2800", "Complete floor deep cleaning", R.drawable.ic_floor));
        serviceList.add(new Service("Full Window Stander Cleaning", "Window", "500", "Cleaning for standard windows", R.drawable.ic_window));
        serviceList.add(new Service("Thai Glass Cleaning", "Glass", "1500", "Cleaning for Thai glass panels", R.drawable.ic_glass));
    }
}