package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

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
        // Create some service providers
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

        // Using local drawable resources for images with specific providers
        serviceList.add(new Service("Premium Bathroom Cleaning", "Bathroom", "1900", 
            "Deep cleaning for bathroom with sanitization and mold removal.", 
            R.drawable.ic_bathroom,
            cleanPro));
            
        serviceList.add(new Service("Kitchen Regular Cleaning", "Kitchen", "699", 
            "Basic regular cleaning for kitchen including countertops and appliances.", 
            R.drawable.ic_kitchen,
            homeCare));
            
        serviceList.add(new Service("Floor Deep Cleaning", "Floor", "2800", 
            "Complete floor deep cleaning for all types of flooring.", 
            R.drawable.ic_floor,
            freshNShine));
            
        serviceList.add(new Service("Full Window Cleaning", "Window", "1000", 
            "Professional cleaning for standard windows, inside and out.", 
            R.drawable.ic_window,
            cleanPro));
            
        serviceList.add(new Service("Kitchen Deep Cleaning", "Kitchen", "1299", 
            "Thorough deep cleaning including cabinets, oven, and hard-to-reach areas.", 
            R.drawable.ic_kitchen,
            homeCare));
            
        serviceList.add(new Service("Bathroom Deep Clean", "Bathroom", "2200", 
            "Intensive bathroom cleaning with grout and tile treatment.", 
            R.drawable.ic_bathroom,
            freshNShine));
            
        serviceList.add(new Service("Kitchen Premium Cleaning", "Kitchen", "1599", 
            "Premium kitchen cleaning with appliance deep clean and organization.", 
            R.drawable.ic_kitchen,
            cleanPro));
            
        serviceList.add(new Service("Hardwood Floor Care", "Floor", "3500", 
            "Specialized cleaning and polishing for hardwood floors.", 
            R.drawable.ic_floor,
            homeCare));
            
        serviceList.add(new Service("Carpet Deep Cleaning", "Floor", "2500", 
            "Steam cleaning and stain removal for carpets.", 
            R.drawable.ic_floor,
            freshNShine));
            
        serviceList.add(new Service("Window Track Cleaning", "Window", "1500", 
            "Detailed cleaning of window tracks and sills.", 
            R.drawable.ic_window,
            cleanPro));
            
        serviceList.add(new Service("Marble Floor Polishing", "Floor", "4500", 
            "Professional polishing and sealing for marble floors.", 
            R.drawable.ic_floor,
            homeCare));
            
        serviceList.add(new Service("Skylight Cleaning", "Window", "2000", 
            "Specialized cleaning for hard-to-reach skylights.", 
            R.drawable.ic_window));
            
        serviceList.add(new Service("Glass Partition Cleaning", "Glass", "1800", 
            "Streak-free cleaning for glass partitions and doors.", 
            R.drawable.ic_glass));
    }
}