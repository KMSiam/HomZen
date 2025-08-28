package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.GroceryAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Grocery;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.util.ArrayList;

public class GroceryFragment extends Fragment {
    View groceryView;
    RecyclerView groceryRecyclerView;
    ArrayList<Grocery> arrGrocery ;
    GroceryAdapter groceryAdapter;
    SearchView searchView;
    Chip chipAll, chipOil, chipFruits, chipVegetables, chipSnacks, chipDrinks;


    public GroceryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groceryView = inflater.inflate(R.layout.fragment_grocery, container, false);

        groceryRecyclerView = groceryView.findViewById(R.id.groceryRecyclerView);
        groceryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

        //search and chip view
        searchView = groceryView.findViewById(R.id.searchView);
        chipAll = groceryView.findViewById(R.id.chipAll);
        chipOil = groceryView.findViewById(R.id.chip2);
        chipOil.setText("Oil");
        chipOil.setVisibility(VISIBLE);
        chipFruits = groceryView.findViewById(R.id.chip3);
        chipFruits.setText("Fruits");
        chipFruits.setVisibility(VISIBLE);
        chipVegetables = groceryView.findViewById(R.id.chip4);
        chipVegetables.setText("Vegetables");
        chipVegetables.setVisibility(VISIBLE);
        chipSnacks = groceryView.findViewById(R.id.chip5);
        chipSnacks.setText("Snacks");
        chipSnacks.setVisibility(VISIBLE);
        chipDrinks = groceryView.findViewById(R.id.chip6);
        chipDrinks.setText("Drinks");
        chipDrinks.setVisibility(VISIBLE);

        arrGrocery = new ArrayList<>();
        loadDummyGrocery();
        groceryAdapter = new GroceryAdapter(getContext(), arrGrocery);
        groceryRecyclerView.setAdapter(groceryAdapter);

        setupSearchView();
        setupChipListeners();

        searchView.clearFocus();

        return groceryView;
    }

    ArrayList<Grocery> originalList = new ArrayList<>();
    boolean isSearching = false;

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
                    groceryAdapter.getFilter().filter(""); // Show all groceries
                } else {
                    String category = clickedChip.getText().toString();
                    groceryAdapter.getFilter().filter(category); // Filter by category
                }
            }
        };

        // Set listeners for all chips
        chipAll.setOnClickListener(chipClickListener);
        chipOil.setOnClickListener(chipClickListener);
        chipFruits.setOnClickListener(chipClickListener);
        chipVegetables.setOnClickListener(chipClickListener);
        chipSnacks.setOnClickListener(chipClickListener);
        chipDrinks.setOnClickListener(chipClickListener);

        // Clear all when clicking category container area
        groceryView.findViewById(R.id.categoryContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllChips();
                chipAll.setChecked(true); // Select "All" chip
                groceryAdapter.getFilter().filter(""); // Show all groceries
                searchView.setQuery("", false); // Clear search
            }
        });

        // Set click listeners for all chips
        chipAll.setOnClickListener(chipClickListener);
        chipOil.setOnClickListener(chipClickListener);
        chipFruits.setOnClickListener(chipClickListener);
        chipVegetables.setOnClickListener(chipClickListener);
        chipSnacks.setOnClickListener(chipClickListener);
        chipDrinks.setOnClickListener(chipClickListener);

        // Set All chip as checked by default
        chipAll.setChecked(true);
        groceryAdapter.getFilter().filter("");
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                groceryAdapter.getFilter().filter(query);
                clearAllChipsExceptSearch(); // Clear chips when searching
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                groceryAdapter.getFilter().filter(newText);
                clearAllChipsExceptSearch(); // Clear chips when searching
                return false;
            }
        });
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
        chipAll.setChecked(false);
        chipOil.setChecked(false);
        chipFruits.setChecked(false);
        chipVegetables.setChecked(false);
        chipSnacks.setChecked(false);
        chipDrinks.setChecked(false);
    }

    private void clearAllChipsExceptSearch() {
        // This method clears chips when user types in search
        // but doesn't interfere with search functionality
        chipAll.setChecked(false);
        chipOil.setChecked(false);
        chipFruits.setChecked(false);
        chipVegetables.setChecked(false);
        chipSnacks.setChecked(false);
        chipDrinks.setChecked(false);
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