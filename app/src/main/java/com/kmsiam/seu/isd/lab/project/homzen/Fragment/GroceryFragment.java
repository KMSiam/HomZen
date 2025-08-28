package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.GroceryAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Grocery;
import com.kmsiam.seu.isd.lab.project.homzen.R;
import com.kmsiam.seu.isd.lab.project.homzen.Utils.ChipUtils;

import java.util.ArrayList;

public class GroceryFragment extends Fragment {
    View groceryView;
    RecyclerView groceryRecyclerView;
    ArrayList<Grocery> arrGrocery ;
    GroceryAdapter groceryAdapter;
    SearchView searchView;
    private ChipGroup chipGroup;

    public GroceryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groceryView = inflater.inflate(R.layout.fragment_grocery, container, false);

        // Initialize views
        groceryRecyclerView = groceryView.findViewById(R.id.groceryRecyclerView);
        searchView = groceryView.findViewById(R.id.searchView);
        chipGroup = groceryView.findViewById(R.id.chipGroup);

        // Setup RecyclerView
        groceryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        
        // Load data and setup adapter
        arrGrocery = new ArrayList<>();
        loadDummyGrocery();
        groceryAdapter = new GroceryAdapter(getContext(), arrGrocery);
        groceryRecyclerView.setAdapter(groceryAdapter);

        // Setup search and chips
        setupSearchView();
        setupChips();

        searchView.clearFocus();
        return groceryView;
    }

    ArrayList<Grocery> originalList = new ArrayList<>();
    boolean isSearching = false;

    private void setupChips() {
        // Define your categories
        String[] categories = {"Oil", "Fruits", "Vegetables", "Snacks", "Drinks"};
        
        // Setup chips using the utility class
        ChipUtils.setupChips(requireContext(), chipGroup, categories, groceryAdapter.getFilter(), 
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
                groceryAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void setupSearchView() {
        // Clear search when a chip is selected
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                groceryAdapter.getFilter().filter(query);
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
                    groceryAdapter.getFilter().filter(newText);
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
                groceryAdapter.getFilter().filter("");
            }
        }
    }

    private void filterByCategory(String category) {
        if (originalList.isEmpty()) return;
        
        if (category.equals("All")) {
            groceryAdapter.setFilteredList(new ArrayList<>(originalList));
            return;
        }
        
        ArrayList<Grocery> filteredList = new ArrayList<>();
        for (Grocery grocery : originalList) {
            if (grocery.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(grocery);
            }
        }
        
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No items in " + category + " category", Toast.LENGTH_SHORT).show();
        }
        groceryAdapter.setFilteredList(filteredList);
    }

    public void filterList(String newText) {
        groceryAdapter.getFilter().filter(newText);
    }
    


    private void clearAllChips() {
        // This method clears all chips
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setChecked(false);
        }
    }

    private void loadDummyGrocery(){
        // Oil category
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil", "Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil", "Par Unit", "Sunflower Oil 2 Ltr", "450"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil", "Par Unit", "Mustard Oil 1 Ltr", "250"));
        
        // Fruits category
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Fruits", "Per Kg", "Apple", "200"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Fruits", "Per Kg", "Banana", "80"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Fruits", "Per Kg", "Orange", "120"));
        
        // Vegetables category
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Vegetables", "Per Kg", "Potato", "30"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Vegetables", "Per Kg", "Tomato", "40"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Vegetables", "Per Kg", "Onion", "35"));
        
        // Snacks category
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Snacks", "Pack of 1", "Chips", "20"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Snacks", "Pack of 1", "Biscuits", "15"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Snacks", "Pack of 1", "Chocolate", "50"));
        
        // Drinks category
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Drinks", "1.5L Bottle", "Cola", "90"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Drinks", "1L Pack", "Orange Juice", "120"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Drinks", "500ml Can", "Energy Drink", "80"));
        
        // More items for testing
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil", "Par Unit", "Coconut Oil 1 Ltr", "300"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Fruits", "Per Kg", "Mango", "150"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Vegetables", "Per Kg", "Carrot", "60"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Snacks", "Pack of 1", "Nuts Mix", "100"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Drinks", "1L Bottle", "Mineral Water", "30"));
    }
}