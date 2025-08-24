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
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.ServiceAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Service;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceFragment extends Fragment {
    RecyclerView recyclerServices;
    ServiceAdapter adapter;
    List<Service> serviceList;
    SearchView searchView;
    Chip chipAll, chipBathroom, chipKitchen, chipFloor, chipWindow;
    View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_service, container, false);

        // Initialize views
        recyclerServices = rootView.findViewById(R.id.recyclerServices);
        searchView = rootView.findViewById(R.id.searchViewService);
        chipAll = rootView.findViewById(R.id.chipAll);
        chipBathroom = rootView.findViewById(R.id.chipBathroom);
        chipKitchen = rootView.findViewById(R.id.chipKitchen);
        chipFloor = rootView.findViewById(R.id.chipFloor);
        chipWindow = rootView.findViewById(R.id.chipWindow);

        // Setup RecyclerView
        recyclerServices.setLayoutManager(new LinearLayoutManager(getContext()));
        serviceList = new ArrayList<>();
        loadDummyServices();
        adapter = new ServiceAdapter(getContext(), serviceList);
        recyclerServices.setAdapter(adapter);

        // Setup SearchView
        setupSearchView();

        // Setup Chip listeners
        setupChipListeners();

        return rootView;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                clearAllChipsExceptSearch(); // Clear chips when searching
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                clearAllChipsExceptSearch(); // Clear chips when searching
                return false;
            }
        });
    }

    private void setupChipListeners() {
        View.OnClickListener chipClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chip clickedChip = (Chip) v;

                // Clear search query when any chip is clicked
                searchView.setQuery("", false);

                // Clear all chips first
                clearAllChips();

                // Select the clicked chip
                clickedChip.setChecked(true);

                // Handle filtering based on which chip was clicked
                if (clickedChip == chipAll) {
                    adapter.getFilter().filter(""); // Show all services
                } else {
                    String category = clickedChip.getText().toString();
                    adapter.getFilter().filter(category); // Filter by category
                }
            }
        };

        // Set listeners for all chips
        chipAll.setOnClickListener(chipClickListener);
        chipBathroom.setOnClickListener(chipClickListener);
        chipKitchen.setOnClickListener(chipClickListener);
        chipFloor.setOnClickListener(chipClickListener);
        chipWindow.setOnClickListener(chipClickListener);

        //Clear all when clicking category container area
        rootView.findViewById(R.id.categoryContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllChips();
                chipAll.setChecked(true); // Select "All" chip
                adapter.getFilter().filter(""); // Show all services
                searchView.setQuery("", false); // Clear search
            }
        });
    }

    private void clearAllChips() {
        chipAll.setChecked(false);
        chipBathroom.setChecked(false);
        chipKitchen.setChecked(false);
        chipFloor.setChecked(false);
        chipWindow.setChecked(false);
    }

    private void clearAllChipsExceptSearch() {
        // This method clears chips when user types in search
        // but doesn't interfere with search functionality
        chipAll.setChecked(false);
        chipBathroom.setChecked(false);
        chipKitchen.setChecked(false);
        chipFloor.setChecked(false);
        chipWindow.setChecked(false);
    }

    private void loadDummyServices() {
        // Use actual image URLs instead of drawable resources
        serviceList.add(new Service("Premium Bathroom Cleaning", "Bathroom", "1900", "Deep cleaning for bathroom. Deep cleaning for bathroom. Deep cleaning for bathroom. Deep cleaning for bathroom. Deep cleaning for bathroom. Deep cleaning for bathroom. Deep cleaning for bathroom. Deep cleaning for bathroom", "https://cdn.pixabay.com/photo/2014/12/17/16/54/clean-571679_1280.jpg"));
        serviceList.add(new Service("Kitchen Regular Cleaning", "Kitchen", "699", "Basic regular cleaning for kitchen", "https://cdn.pixabay.com/photo/2020/08/10/06/52/cleaning-5476953_960_720.jpg"));
        serviceList.add(new Service("Floor Deep Cleaning", "Floor", "2800", "Complete floor deep cleaning", "https://cdn.pixabay.com/photo/2014/02/17/13/34/cleaning-268126_960_720.jpg"));
        serviceList.add(new Service("Full Window Cleaning", "Window", "1000", "Cleaning for standard windows", "https://cdn.pixabay.com/photo/2019/06/10/12/15/building-cleaner-4264144_960_720.jpg"));
        serviceList.add(new Service("Kitchen Regular Cleaning", "Kitchen", "699", "Basic regular cleaning for kitchen", "https://cdn.pixabay.com/photo/2020/08/10/06/52/cleaning-5476953_960_720.jpg"));
        serviceList.add(new Service("Premium Bathroom Cleaning", "Bathroom", "1900", "Deep cleaning for bathroom", "https://cdn.pixabay.com/photo/2014/12/17/16/54/clean-571679_1280.jpg"));
        serviceList.add(new Service("Kitchen Premium Cleaning", "Kitchen", "699", "Basic regular cleaning for kitchen", "https://cdn.pixabay.com/photo/2020/08/10/06/52/cleaning-5476953_960_720.jpg"));
        serviceList.add(new Service("Floor Deep Cleaning", "Floor", "2800", "Complete floor deep cleaning", "https://cdn.pixabay.com/photo/2014/02/17/13/34/cleaning-268126_960_720.jpg"));
        serviceList.add(new Service("Full Window Premium Cleaning", "Window", "1000", "Cleaning for standard windows", "https://cdn.pixabay.com/photo/2019/06/10/12/15/building-cleaner-4264144_960_720.jpg"));
        serviceList.add(new Service("Kitchen Regular Cleaning", "Kitchen", "699", "Basic regular cleaning for kitchen", "https://cdn.pixabay.com/photo/2020/08/10/06/52/cleaning-5476953_960_720.jpg"));
        serviceList.add(new Service("Floor Stander Cleaning", "Floor", "2800", "Complete floor deep cleaning", "https://cdn.pixabay.com/photo/2014/02/17/13/34/cleaning-268126_960_720.jpg"));
        serviceList.add(new Service("Full Window Stander Cleaning", "Window", "500", "Cleaning for standard windows", "https://cdn.pixabay.com/photo/2019/06/10/12/15/building-cleaner-4264144_960_720.jpg"));
        serviceList.add(new Service("Thai Glass Cleaning", "Glass", "1500", "Cleaning for Thai glass panels", "https://cdn.pixabay.com/photo/2021/01/18/16/56/woman-5928694_1280.jpg"));
    }
}